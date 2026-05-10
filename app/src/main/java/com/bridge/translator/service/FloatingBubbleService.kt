package com.bridge.translator.service

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.IBinder
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.bridge.translator.overlay.OverlayManager
import com.bridge.translator.processing.BitmapTextEraser
import com.bridge.translator.processing.ScreenAnalyser
import com.bridge.translator.translation.TranslationEngine
import com.example.bridgetranslator.HomeActivity
import com.example.bridgetranslator.R
import com.google.mlkit.nl.translate.TranslateLanguage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.abs

private const val TAG = "BridgeDebug"
private const val FLOW_TAG = "BridgeFlow"

/**
 * Manual snap-and-translate floating bubble service.
 *
 * The bubble is a state-machine button:
 *   IDLE      — grey circle with the translate icon. Tap to capture.
 *   CAPTURING — blue circle with a spinner. Tap-ignored.
 *   SHOWING   — red circle with ✕. Tap to dismiss the overlay.
 *
 * A user gesture on the bubble is the only way to start a translation cycle.
 * There is no auto-poll, no scroll/touch debounce, no motion gating.
 */
class FloatingBubbleService : Service() {

    enum class BubbleState { IDLE, CAPTURING, SHOWING }

    companion object {
        const val ACTION_MEDIA_PROJECTION_RESULT = "action.MEDIA_PROJECTION_RESULT"
        const val ACTION_STOP_SERVICE = "action.STOP_SERVICE"
        const val EXTRA_RESULT_CODE = "extra.RESULT_CODE"
        const val EXTRA_RESULT_DATA = "extra.RESULT_DATA"

        // Future hook: when set true, a higher layer can re-introduce auto
        // mode. Manual flow ignores it. Default false.
        const val PREF_AUTO_TRANSLATE_ENABLED = "auto_translate_enabled"

        /**
         * Banking / payment apps that MUST NOT receive a synthetic tap.
         * Held here (rather than in the accessibility service) so the tap-
         * through guard can read it without depending on the optional
         * accessibility service being connected.
         */
        val BANKING_PACKAGES: Set<String> = setOf(
            "net.one97.paytm",
            "com.google.android.apps.nbu.paisa.user",
            "com.phonepe.app",
            "com.sbi.lotusintouch",
            "com.csam.icici.bank.imobile",
            "com.kotak.mahindra.kotak.bank",
            "com.snapwork.hdfc",
            "com.idbi.mpassbook",
            "com.axis.mobile",
            "com.whatsapp"
        )

        private const val CHANNEL_ID = "bubble_service_channel"
        private const val NOTIFICATION_ID = 1001
        private const val BUBBLE_DP = 56
        private const val SNAP_MS = 200L
        private const val DRAG_THRESHOLD = 10f
        private const val STOP_ZONE_DP = 120
        private const val LONG_PRESS_MS = 500L
        private const val CANCEL_ZONE_SIZE_DP = 72
        private const val CANCEL_ZONE_MARGIN_DP = 40
        private const val CAPTURE_BUBBLE_HIDE_DELAY_MS = 80L

        @Volatile
        var instance: FloatingBubbleService? = null
            private set

        @Volatile
        var isRunning = false
            private set
    }

    enum class CaptureMode { SCREEN, CAMERA }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private lateinit var windowManager: WindowManager
    private lateinit var screenCaptureManager: ScreenCaptureHelper
    private lateinit var bitmapEraser: BitmapTextEraser
    private lateinit var overlayManager: OverlayManager
    private val translationEngine = TranslationEngine()

    private var cameraManager: CameraManager? = null
    private var captureMode: CaptureMode = CaptureMode.SCREEN
    private var screenAnalyser: ScreenAnalyser? = null
    private var cachedAnalyserLang: String? = null
    private var cycleJob: Job? = null
    private var capturePending = false
    private var targetLang = TranslateLanguage.ENGLISH

    @Volatile
    private var state: BubbleState = BubbleState.IDLE
    private var lastVisualState: BubbleState? = null

    /**
     * Set by [TranslatorAccessibilityService] on every TYPE_WINDOW_STATE_CHANGED
     * event. Read-only consumers (settings UI, etc.) can also peek at it.
     */
    @Volatile
    var currentForegroundPackage: String = ""

    private var bubbleView: View? = null
    private var bubbleIconView: ImageView? = null
    private var bubbleProgressBar: ProgressBar? = null
    private lateinit var bubbleParams: WindowManager.LayoutParams
    private var initialX = 0
    private var initialY = 0
    private var initialTouchX = 0f
    private var initialTouchY = 0f
    private var isDragging = false
    private var cancelZoneView: View? = null
    private var cancelZoneParams: WindowManager.LayoutParams? = null
    private var longPressJob: Job? = null
    private var showingCancelZone = false

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        Log.d(FLOW_TAG, "FloatingBubbleService.onCreate (manual mode)")
        instance = this
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        screenCaptureManager = ScreenCaptureHelper(this)
        bitmapEraser = BitmapTextEraser()
        cameraManager = CameraManager(this, previewView = null)
        cameraManager?.setupCamera()
        val screen = realDisplayMetrics()
        overlayManager = OverlayManager(
            context = this,
            windowManager = windowManager,
            screenW = screen.widthPixels,
            screenH = screen.heightPixels
        )
        overlayManager.onFirstTouch = { dismissOverlay() }
        Log.d(FLOW_TAG, "Service initialized. screen=${screen.widthPixels}x${screen.heightPixels}")
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(FLOW_TAG, "onStartCommand action=${intent?.action} bubbleAttached=${bubbleView != null}")
        when (intent?.action) {
            ACTION_MEDIA_PROJECTION_RESULT -> handleProjectionResult(intent)
            ACTION_STOP_SERVICE -> stopSelf()
            else -> if (bubbleView == null) attachBubble()
        }
        return START_STICKY
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Log.d(FLOW_TAG, "onConfigurationChanged: dropping any in-flight cycle")
        stopTranslation()
        screenCaptureManager.onConfigurationChanged()
        bubbleView?.let { view ->
            val size = dp(BUBBLE_DP)
            val screen = realDisplayMetrics()
            bubbleParams.x = bubbleParams.x.coerceIn(0, screen.widthPixels - size)
            bubbleParams.y = bubbleParams.y.coerceIn(0, screen.heightPixels - size)
            if (view.isAttachedToWindow) windowManager.updateViewLayout(view, bubbleParams)
            snapToEdge(view)
        }
    }

    override fun onDestroy() {
        Log.d(FLOW_TAG, "onDestroy")
        hideCancelZone()
        longPressJob?.cancel()
        overlayManager.remove()
        screenCaptureManager.release()
        translationEngine.release()
        cameraManager?.stopCamera()
        cameraManager?.shutdown()
        cycleJob?.cancel()
        bubbleView?.let { if (it.isAttachedToWindow) windowManager.removeView(it) }
        bubbleView = null
        bubbleIconView = null
        bubbleProgressBar = null
        isRunning = false
        instance = null
        scope.cancel()
        super.onDestroy()
    }

    // ---------------------------------------------------------------------
    // Bubble window: attach / detach / visual update
    // ---------------------------------------------------------------------

    @SuppressLint("ClickableViewAccessibility")
    private fun attachBubble(initialState: BubbleState = BubbleState.IDLE) {
        val size = dp(BUBBLE_DP)
        Log.d(FLOW_TAG, "attachBubble requested size=$size initialState=$initialState")

        val container = FrameLayout(this).apply {
            background = circleDrawable(Color.parseColor("#808080"))
            elevation = dp(6).toFloat()
        }
        val icon = ImageView(this).apply {
            setImageResource(R.drawable.ic_translate)
            scaleType = ImageView.ScaleType.CENTER_INSIDE
            imageTintList = ColorStateList.valueOf(Color.WHITE)
            setPadding(dp(14), dp(14), dp(14), dp(14))
        }
        container.addView(
            icon,
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        val spinner = ProgressBar(this).apply {
            isIndeterminate = true
            indeterminateTintList = ColorStateList.valueOf(Color.WHITE)
            visibility = View.GONE
        }
        val spinnerSize = dp(28)
        container.addView(
            spinner,
            FrameLayout.LayoutParams(
                spinnerSize,
                spinnerSize,
                Gravity.CENTER
            )
        )

        // Re-create bubbleParams only if it doesn't already exist (preserves
        // the prior position across detach/re-attach during a cycle).
        if (!::bubbleParams.isInitialized) {
            bubbleParams = WindowManager.LayoutParams(
                size,
                size,
                overlayWindowType(),
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT
            ).apply {
                gravity = Gravity.TOP or Gravity.START
                val screen = realDisplayMetrics()
                x = screen.widthPixels - size
                y = screen.heightPixels / 3
            }
        } else {
            // Make sure the size + flags are up to date even if we're reusing.
            bubbleParams.width = size
            bubbleParams.height = size
        }

        container.setOnTouchListener(::onBubbleTouch)
        try {
            windowManager.addView(container, bubbleParams)
            bubbleView = container
            bubbleIconView = icon
            bubbleProgressBar = spinner
            state = initialState
            updateBubbleVisual()
            isRunning = true
            Log.d(FLOW_TAG, "Bubble attached at x=${bubbleParams.x}, y=${bubbleParams.y}")
        } catch (e: Exception) {
            Log.e(TAG, "Unable to add bubble", e)
            Log.e(FLOW_TAG, "Bubble attach failed: ${e.message}", e)
            Toast.makeText(this, "Enable Display over other apps", Toast.LENGTH_LONG).show()
            stopSelf()
        }
    }

    private fun detachBubble() {
        bubbleView?.let { view ->
            if (view.isAttachedToWindow) {
                runCatching { windowManager.removeView(view) }
                    .onFailure { Log.w(FLOW_TAG, "detachBubble removeView failed: ${it.message}") }
            }
        }
        bubbleView = null
        bubbleIconView = null
        bubbleProgressBar = null
        // isRunning stays true — the service is still alive.
    }

    private fun updateBubbleVisual() {
        val icon = bubbleIconView ?: return
        val container = bubbleView ?: return
        val spinner = bubbleProgressBar
        when (state) {
            BubbleState.IDLE -> {
                icon.visibility = View.VISIBLE
                icon.setImageResource(R.drawable.ic_translate)
                icon.imageTintList = ColorStateList.valueOf(Color.WHITE)
                container.background = circleDrawable(Color.parseColor("#808080"))
                container.contentDescription = "Translate this screen"
                spinner?.visibility = View.GONE
            }
            BubbleState.CAPTURING -> {
                icon.visibility = View.GONE
                container.background = circleDrawable(Color.parseColor("#1F6FEB"))
                container.contentDescription = "Capturing screen…"
                spinner?.visibility = View.VISIBLE
            }
            BubbleState.SHOWING -> {
                icon.visibility = View.VISIBLE
                icon.setImageResource(R.drawable.ic_close)
                icon.imageTintList = ColorStateList.valueOf(Color.WHITE)
                container.background = circleDrawable(Color.parseColor("#E94560"))
                container.contentDescription = "Close translation"
                spinner?.visibility = View.GONE
                // Polish: one-time pulse to draw the eye to the bubble as the
                // dismiss affordance the moment the overlay first appears.
                // Fires only on edge transitions into SHOWING so a re-render
                // of the same state doesn't double-pulse.
                if (lastVisualState != BubbleState.SHOWING) playShowingPulse(container)
            }
        }
        lastVisualState = state
    }

    private fun playShowingPulse(view: View) {
        android.animation.ObjectAnimator.ofPropertyValuesHolder(
            view,
            android.animation.PropertyValuesHolder.ofFloat("scaleX", 1f, 1.18f, 1f),
            android.animation.PropertyValuesHolder.ofFloat("scaleY", 1f, 1.18f, 1f)
        ).apply {
            duration = 400
            start()
        }
    }

    // ---------------------------------------------------------------------
    // Bubble touch handling (drag, long-press cancel zone, tap)
    // ---------------------------------------------------------------------

    @SuppressLint("ClickableViewAccessibility")
    private fun onBubbleTouch(view: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                initialX = bubbleParams.x
                initialY = bubbleParams.y
                initialTouchX = event.rawX
                initialTouchY = event.rawY
                isDragging = false
                longPressJob = scope.launch {
                    delay(LONG_PRESS_MS)
                    showCancelZone()
                }
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = event.rawX - initialTouchX
                val dy = event.rawY - initialTouchY
                if (!isDragging && (abs(dx) > DRAG_THRESHOLD || abs(dy) > DRAG_THRESHOLD)) {
                    isDragging = true
                    if (!showingCancelZone) longPressJob?.cancel()
                }
                if (isDragging) {
                    val screen = realDisplayMetrics()
                    val size = dp(BUBBLE_DP)
                    bubbleParams.x = (initialX + dx.toInt()).coerceIn(0, screen.widthPixels - size)
                    bubbleParams.y = (initialY + dy.toInt()).coerceIn(0, screen.heightPixels - size)
                    windowManager.updateViewLayout(view, bubbleParams)
                    if (showingCancelZone) highlightCancelZone(isInStopZone(event.rawY))
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                longPressJob?.cancel()
                Log.d(FLOW_TAG, "bubble ACTION_UP dragging=$isDragging cancelZone=$showingCancelZone state=$state")
                val wasShowingCancelZone = showingCancelZone
                if (wasShowingCancelZone) hideCancelZone()
                if (wasShowingCancelZone && isDragging && isInStopZone(event.rawY)) {
                    stopTranslation()
                    stopSelf()
                    return true
                }
                if (isDragging) {
                    if (!wasShowingCancelZone && isInStopZone(event.rawY)) {
                        Log.d(FLOW_TAG, "Bubble dropped in stop zone -> stop translation")
                        stopTranslation()
                    }
                    snapToEdge(view)
                } else if (!wasShowingCancelZone) {
                    handleBubbleTap()
                }
            }
        }
        return true
    }

    // ---------------------------------------------------------------------
    // Manual-mode entry points
    // ---------------------------------------------------------------------

    private fun handleBubbleTap() {
        Log.d(FLOW_TAG, "handleBubbleTap state=$state mode=$captureMode screenReady=${screenCaptureManager.isInitialized} cameraReady=${cameraManager?.isInitialized}")
        when (state) {
            BubbleState.IDLE -> {
                // Check if we need screen capture permission (only for SCREEN mode)
                if (captureMode == CaptureMode.SCREEN && !screenCaptureManager.isInitialized) {
                    Log.d(FLOW_TAG, "Screen mode selected but screen capture not initialized, requesting permission")
                    launchPermissionScreen()
                    return
                }

                // Check if we need camera permission (only for CAMERA mode)
                if (captureMode == CaptureMode.CAMERA && !cameraManager?.isCameraPermissionGranted()!!) {
                    Log.w(FLOW_TAG, "Camera mode selected but camera permission not granted")
                    Toast.makeText(this, "Camera permission required for camera mode", Toast.LENGTH_SHORT).show()
                    return
                }

                startCaptureAndShowOverlay()
            }
            BubbleState.SHOWING -> dismissOverlay()
            BubbleState.CAPTURING -> Unit /* ignore taps mid-capture */
        }
    }

    private fun startCaptureAndShowOverlay() {
        Log.d(FLOW_TAG, "startCaptureAndShowOverlay")
        state = BubbleState.CAPTURING
        cycleJob?.cancel()
        cycleJob = scope.launch {
            var rawForCleanup: android.graphics.Bitmap? = null
            try {
                // 1. Detach bubble fully so it doesn't appear in the screenshot.
                val savedX = if (::bubbleParams.isInitialized) bubbleParams.x else 0
                val savedY = if (::bubbleParams.isInitialized) bubbleParams.y else 0
                detachBubble()
                delay(CAPTURE_BUBBLE_HIDE_DELAY_MS)

                // 2. Capture (screen or camera mode).
                val raw = withContext(Dispatchers.IO) {
                    when (captureMode) {
                        CaptureMode.SCREEN -> {
                            Log.d(FLOW_TAG, "Capturing screen")
                            screenCaptureManager.captureFrame()
                        }
                        CaptureMode.CAMERA -> {
                            Log.d(FLOW_TAG, "Capturing camera frame")
                            // Ensure camera permission is granted
                            if (cameraManager == null) {
                                Log.w(FLOW_TAG, "CameraManager is null")
                                return@withContext null
                            }

                            val hasPermission = cameraManager!!.isCameraPermissionGranted()
                            if (!hasPermission) {
                                Log.w(FLOW_TAG, "Camera permission not granted")
                                Toast.makeText(this@FloatingBubbleService, "Camera permission required", Toast.LENGTH_SHORT).show()
                                return@withContext null
                            }

                            // Wait for camera setup to complete
                            var attempts = 0
                            while (!cameraManager!!.isInitialized && attempts < 10) {
                                delay(100)
                                attempts++
                            }

                            if (!cameraManager!!.isInitialized) {
                                Log.w(FLOW_TAG, "Camera still not initialized after waiting")
                                return@withContext null
                            }

                            cameraManager!!.startCamera()
                            delay(200)  // Give camera time to bind
                            cameraManager!!.captureFrame()
                        }
                    }
                }
                rawForCleanup = raw
                if (captureMode == CaptureMode.CAMERA) {
                    cameraManager?.stopCamera()
                }

                // 3. Re-attach bubble in CAPTURING visual state at the same position.
                if (::bubbleParams.isInitialized) {
                    bubbleParams.x = savedX
                    bubbleParams.y = savedY
                }
                // Race guard: a TYPE_WINDOW_STATE_CHANGED event during the 80 ms
                // detach window could have made the accessibility service
                // re-attach the bubble already. In that case just refresh the
                // visual instead of double-attaching.
                if (bubbleView == null) {
                    attachBubble(initialState = BubbleState.CAPTURING)
                } else {
                    state = BubbleState.CAPTURING
                    updateBubbleVisual()
                }

                if (raw == null) {
                    Log.w(FLOW_TAG, "startCaptureAndShowOverlay: captureFrame returned null")
                    Toast.makeText(
                        this@FloatingBubbleService,
                        "Couldn't capture screen, please try again",
                        Toast.LENGTH_SHORT
                    ).show()
                    state = BubbleState.IDLE
                    updateBubbleVisual()
                    return@launch
                }
                Log.d(FLOW_TAG, "Captured ${raw.width}x${raw.height} -> OCR + translate")

                // 4. OCR + parallel translation.
                val activeLang = targetLanguage().also { targetLang = it }
                val analyser = ensureAnalyser(activeLang)
                val blocks = withContext(Dispatchers.IO) { analyser.analyse(raw) }
                Log.d(FLOW_TAG, "OCR analysed blocks=${blocks.size}")

                // No translatable text found — toast & return to IDLE so the
                // user sees a clear signal instead of an unchanged screenshot.
                if (blocks.isEmpty()) {
                    Log.d(FLOW_TAG, "No translatable blocks — skipping overlay")
                    Toast.makeText(
                        this@FloatingBubbleService,
                        "No translatable text on this screen",
                        Toast.LENGTH_SHORT
                    ).show()
                    state = BubbleState.IDLE
                    updateBubbleVisual()
                    return@launch
                }

                val processedBitmap = withContext(Dispatchers.IO) {
                    coroutineScope {
                        blocks.map { block ->
                            async {
                                block.translatedText = translationEngine.translate(
                                    text = block.originalText,
                                    sourceLang = block.detectedLang,
                                    targetLang = activeLang
                                ) ?: block.originalText
                            }
                        }.awaitAll()
                    }
                    val translatedCount = blocks.count {
                        it.translatedText.isNotBlank() && it.translatedText != it.originalText
                    }
                    Log.d(FLOW_TAG, "Translation done translatedBlocks=$translatedCount/${blocks.size}")
                    bitmapEraser.eraseAndReplace(raw, blocks)
                }
                raw.recycle()
                rawForCleanup = null

                // 5. Show overlay; switch bubble to SHOWING.
                overlayManager.show(processedBitmap)
                state = BubbleState.SHOWING
                updateBubbleVisual()
                Log.d(FLOW_TAG, "Cycle complete -> SHOWING")
            } catch (ce: kotlinx.coroutines.CancellationException) {
                Log.d(FLOW_TAG, "startCaptureAndShowOverlay cancelled")
                rawForCleanup?.recycle()
                throw ce  // cooperative cancellation
            } catch (t: Throwable) {
                Log.e(FLOW_TAG, "startCaptureAndShowOverlay failed: ${t.message}", t)
                rawForCleanup?.recycle()
                Toast.makeText(
                    this@FloatingBubbleService,
                    "Translation failed: ${t.message ?: "unexpected error"}",
                    Toast.LENGTH_SHORT
                ).show()
                // Make sure the bubble doesn't get stuck blue.
                if (bubbleView == null) {
                    attachBubble(initialState = BubbleState.IDLE)
                } else {
                    state = BubbleState.IDLE
                    updateBubbleVisual()
                }
            }
        }
    }

    private fun dismissOverlay() {
        Log.d(FLOW_TAG, "dismissOverlay state=$state")
        overlayManager.remove()
        state = BubbleState.IDLE
        updateBubbleVisual()
    }

    /**
     * Called by accessibility service when the user clicks or scrolls in the
     * underlying app so the overlay smoothly gets out of their way.
     */
    fun dismissOverlayIfShowing() {
        if (state == BubbleState.SHOWING) {
            Log.d(FLOW_TAG, "dismissOverlayIfShowing -> auto fading out overlay")
            overlayManager.fadeOutFast()
            // We can completely detach it slightly after the fast fade, or immediately since
            // it's just visually getting out of the way. Let's do a fast removal so it doesn't linger.
            scope.launch {
                delay(50L) // Wait for FADE_OUT_FAST_MS
                dismissOverlay()
            }
        }
    }

    private fun stopTranslation() {
        Log.d(FLOW_TAG, "stopTranslation")
        cycleJob?.cancel()
        // Polish #2: dropping the bubble on the cancel zone clears the
        // overlay too, regardless of state.
        overlayManager.remove()
        state = BubbleState.IDLE
        updateBubbleVisual()
    }

    /**
     * Polish #3: when the user changes target language in settings the next
     * tap should use the new language. Caller invokes this; we drop the
     * cached analyser and dismiss any visible overlay so the next cycle
     * starts fresh.
     */
    fun onTargetLanguageChanged() {
        Log.d(FLOW_TAG, "onTargetLanguageChanged -> reset to IDLE")
        screenAnalyser = null
        cachedAnalyserLang = null
        cycleJob?.cancel()
        dismissOverlay()
    }

    /**
     * Switch between screen and camera capture modes.
     */
    fun setCaptureMode(mode: CaptureMode) {
        Log.d(FLOW_TAG, "setCaptureMode -> $mode")
        captureMode = mode
    }

    // ---------------------------------------------------------------------
    // Accessibility-driven context tracking
    // ---------------------------------------------------------------------

    /**
     * Called from the accessibility service when the foreground package is in
     * SYSTEM_PACKAGES, launcher, or BANKING_PACKAGES. Detaches the bubble
     * window. Does **not** touch the overlay or any in-flight cycle.
     */
    fun hideBubbleForUnsupportedContext() {
        if (bubbleView == null) return
        Log.d(FLOW_TAG, "hideBubbleForUnsupportedContext")
        detachBubble()
    }

    /**
     * Called from the accessibility service when the foreground package is
     * supported. Re-attaches the bubble window in its current state if it
     * isn't already attached.
     */
    fun showBubbleForSupportedContext() {
        if (bubbleView != null) return
        Log.d(FLOW_TAG, "showBubbleForSupportedContext state=$state")
        attachBubble(initialState = state)
    }

    // ---------------------------------------------------------------------
    // Cancel zone (long-press → drag onto red ✕ → stop service)
    // ---------------------------------------------------------------------

    private fun showCancelZone() {
        if (showingCancelZone) return
        showingCancelZone = true
        val size = dp(CANCEL_ZONE_SIZE_DP)
        val screen = realDisplayMetrics()
        val cancelView = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            background = circleDrawable(Color.parseColor("#CC2222"))
            elevation = dp(8).toFloat()
        }
        cancelView.addView(
            TextView(this).apply {
                text = "✕"
                setTextColor(Color.WHITE)
                textSize = 26f
                gravity = Gravity.CENTER
                typeface = android.graphics.Typeface.DEFAULT_BOLD
            },
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        )
        val czParams = WindowManager.LayoutParams(
            size, size,
            overlayWindowType(),
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = (screen.widthPixels - size) / 2
            y = screen.heightPixels - size - dp(CANCEL_ZONE_MARGIN_DP)
        }
        try {
            windowManager.addView(cancelView, czParams)
            cancelZoneView = cancelView
            cancelZoneParams = czParams
            Log.d(FLOW_TAG, "Cancel zone shown at bottom-center")
        } catch (e: Exception) {
            Log.e(FLOW_TAG, "showCancelZone failed: ${e.message}", e)
            showingCancelZone = false
        }
    }

    private fun hideCancelZone() {
        showingCancelZone = false
        cancelZoneView?.let { view ->
            runCatching { windowManager.removeView(view) }
                .onFailure { Log.w(FLOW_TAG, "hideCancelZone failed: ${it.message}") }
        }
        cancelZoneView = null
        cancelZoneParams = null
    }

    private fun highlightCancelZone(active: Boolean) {
        val view = cancelZoneView ?: return
        view.background = circleDrawable(if (active) Color.RED else Color.parseColor("#CC2222"))
        val scale = if (active) 1.15f else 1.0f
        view.scaleX = scale
        view.scaleY = scale
    }

    // ---------------------------------------------------------------------
    // Permission flow + projection result
    // ---------------------------------------------------------------------

    private fun launchPermissionScreen() {
        Log.d(FLOW_TAG, "launchPermissionScreen")
        capturePending = true
        bubbleView?.visibility = View.INVISIBLE
        try {
            startActivity(
                Intent(this, com.example.bridgetranslator.ScreenCapturePermissionActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
            )
        } catch (e: Exception) {
            capturePending = false
            bubbleView?.visibility = View.VISIBLE
            Log.e(FLOW_TAG, "launchPermissionScreen failed: ${e.message}", e)
            Toast.makeText(this, "Cannot request screen capture: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun handleProjectionResult(intent: Intent) {
        Log.d(FLOW_TAG, "handleProjectionResult")
        capturePending = false
        bubbleView?.visibility = View.VISIBLE
        val resultCode = intent.getIntExtra(EXTRA_RESULT_CODE, Activity.RESULT_CANCELED)
        val data: Intent? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_RESULT_DATA, Intent::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_RESULT_DATA)
        }
        if (resultCode != Activity.RESULT_OK || data == null) {
            Log.w(FLOW_TAG, "Projection permission denied resultCode=$resultCode dataPresent=${data != null}")
            state = BubbleState.IDLE
            updateBubbleVisual()
            Toast.makeText(this, "Screen capture permission denied", Toast.LENGTH_SHORT).show()
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID,
                buildNotification(),
                android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION
            )
        }

        try {
            screenCaptureManager.initialize(resultCode, data)
            Log.d(FLOW_TAG, "Screen capture initialized")
        } catch (e: SecurityException) {
            state = BubbleState.IDLE
            updateBubbleVisual()
            Log.e(FLOW_TAG, "Screen capture initialize failed: ${e.message}", e)
            Toast.makeText(this, "Capture setup failed. Please grant permission again.", Toast.LENGTH_LONG).show()
            return
        }
        // Permission was just granted because the user tapped the bubble in
        // IDLE — kick off the cycle they asked for.
        if (state == BubbleState.IDLE) startCaptureAndShowOverlay()
    }

    // ---------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------

    private fun ensureAnalyser(lang: String): ScreenAnalyser {
        val cached = screenAnalyser
        if (cached != null && cachedAnalyserLang == lang) return cached
        val fresh = ScreenAnalyser(lang)
        screenAnalyser = fresh
        cachedAnalyserLang = lang
        return fresh
    }

    private fun targetLanguage(): String {
        return getSharedPreferences("bridge_prefs", MODE_PRIVATE)
            .getString("target_language", TranslateLanguage.ENGLISH)
            ?: TranslateLanguage.ENGLISH
    }

    private fun snapToEdge(view: View) {
        val screenW = realDisplayMetrics().widthPixels
        val size = dp(BUBBLE_DP)
        val targetX = if (bubbleParams.x + size / 2 < screenW / 2) 0 else screenW - size
        ValueAnimator.ofInt(bubbleParams.x, targetX).apply {
            duration = SNAP_MS
            interpolator = DecelerateInterpolator()
            addUpdateListener { anim ->
                if (bubbleView?.isAttachedToWindow == true) {
                    bubbleParams.x = anim.animatedValue as Int
                    windowManager.updateViewLayout(view, bubbleParams)
                }
            }
            start()
        }
    }

    private fun isInStopZone(rawY: Float): Boolean {
        val screenH = realDisplayMetrics().heightPixels
        return rawY >= screenH - dp(STOP_ZONE_DP)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Floating Bubble",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Bridge Translator floating bubble"
            setShowBadge(false)
        }
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    private fun buildNotification(): Notification {
        val openPi = PendingIntent.getActivity(
            this,
            0,
            Intent(this, HomeActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
        val stopPi = PendingIntent.getService(
            this,
            1,
            Intent(this, FloatingBubbleService::class.java).apply { action = ACTION_STOP_SERVICE },
            PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Bridge Translator")
            .setContentText("Tap bubble to translate any screen")
            .setSmallIcon(R.drawable.ic_bubble)
            .setContentIntent(openPi)
            .addAction(0, "Stop", stopPi)
            .setOngoing(true)
            .build()
    }

    private fun overlayWindowType(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }
    }

    private fun circleDrawable(color: Int) = GradientDrawable().apply {
        shape = GradientDrawable.OVAL
        setColor(color)
    }

    private fun realDisplayMetrics(): DisplayMetrics {
        val metrics = DisplayMetrics()
        @Suppress("DEPRECATION")
        windowManager.defaultDisplay.getRealMetrics(metrics)
        return metrics
    }

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density + 0.5f).toInt()
}
