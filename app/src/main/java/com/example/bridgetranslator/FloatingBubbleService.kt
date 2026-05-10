package com.example.bridgetranslator

import android.annotation.SuppressLint
import android.app.*
import android.content.Intent
import android.content.pm.ServiceInfo
import android.content.res.Configuration
import android.graphics.*
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.google.android.material.card.MaterialCardView
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.math.abs

class FloatingBubbleService : Service() {

    // --- State ---

    private enum class State { INACTIVE, WAITING_PERMISSION, SELECTING, TRANSLATING, ACTIVE }

    companion object {
        private const val TAG = "FloatingBubbleService"
        private const val CHANNEL_ID = "bridge_bubble_service"
        private const val NOTIFICATION_ID = 101
        private const val LONG_PRESS_MS = 500L
        const val ACTION_STOP = "com.example.bridgetranslator.ACTION_STOP"
        const val ACTION_MEDIA_PROJECTION_RESULT = "com.example.bridgetranslator.ACTION_MEDIA_PROJECTION_RESULT"
    }

    // --- Fields ---

    private lateinit var wm: WindowManager
    private var bubbleView: View? = null
    private var selectionOverlay: SelectionOverlayView? = null
    private var translationOverlay: TranslationOverlayWindow? = null
    private var screenCapture: ScreenCaptureManager? = null

    private var state = State.INACTIVE
    private val mainHandler = Handler(Looper.getMainLooper())
    private var longPressRunnable: Runnable? = null

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val engine = TranslationEngine()

    // --- Service lifecycle ---

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        wm = getSystemService(WINDOW_SERVICE) as WindowManager
        createChannel()
        startForeground(NOTIFICATION_ID, buildNotification("Bridge is ready"))

        // Do NOT gate on Settings.canDrawOverlays() - it returns false on MIUI, OneUI,
        // and ColorOS even when the permission is actually granted. Just try to add
        // the view; the WindowManager will throw if permission is truly missing.
        try {
            attachBubble()
        } catch (e: Exception) {
            Log.e(TAG, "attachBubble failed - overlay permission likely missing", e)
            Toast.makeText(
                this,
                "Bridge needs 'Display over other apps'.\n" +
                "Settings -> Apps -> Bridge Translator -> Display over other apps -> Allow",
                Toast.LENGTH_LONG
            ).show()
            stopSelf()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP -> stopSelf()
            ACTION_MEDIA_PROJECTION_RESULT -> handleProjectionResult(intent)
        }
        return START_STICKY
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        screenCapture?.onConfigurationChanged()
    }

    override fun onDestroy() {
        scope.cancel()
        engine.close()
        screenCapture?.release()
        clearOverlays()
        safeRemove(bubbleView)
        bubbleView = null
        super.onDestroy()
    }

    // --- Bubble setup ---

    @SuppressLint("ClickableViewAccessibility")
    private fun attachBubble() {
        val ctx = ContextThemeWrapper(this, R.style.Theme_BridgeTranslator)
        bubbleView = LayoutInflater.from(ctx).inflate(R.layout.layout_floating_bubble, null)

        val size = dp(56)
        // Place the bubble on the right edge, roughly mid-screen vertically,
        // so it is visible regardless of device size.
        val screenH = resources.displayMetrics.heightPixels
        val screenW = resources.displayMetrics.widthPixels
        val lp = WindowManager.LayoutParams(
            size, size,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = screenW - size - dp(16)  // right edge with 16dp margin
            y = screenH / 3               // upper-third of screen
        }

        bubbleView!!.setOnTouchListener(BubbleTouchListener(lp))
        wm.addView(bubbleView, lp)
        renderBubble()
        Toast.makeText(this, "Bridge bubble added - look for the circle on the right side of your screen", Toast.LENGTH_LONG).show()
    }

    private inner class BubbleTouchListener(
        private val lp: WindowManager.LayoutParams
    ) : View.OnTouchListener {
        private var ox = 0; private var oy = 0
        private var tx = 0f; private var ty = 0f
        private var didLongPress = false

        @SuppressLint("ClickableViewAccessibility")
        override fun onTouch(v: View?, e: MotionEvent?): Boolean {
            when (e?.action) {
                MotionEvent.ACTION_DOWN -> {
                    ox = lp.x; oy = lp.y; tx = e.rawX; ty = e.rawY; didLongPress = false
                    longPressRunnable = Runnable { didLongPress = true; openMainActivity() }
                    mainHandler.postDelayed(longPressRunnable!!, LONG_PRESS_MS)
                    return true
                }
                MotionEvent.ACTION_MOVE -> {
                    if (abs(e.rawX - tx) > 8 || abs(e.rawY - ty) > 8) {
                        mainHandler.removeCallbacks(longPressRunnable!!)
                        lp.x = ox + (e.rawX - tx).toInt()
                        lp.y = oy + (e.rawY - ty).toInt()
                        bubbleView?.let { wm.updateViewLayout(it, lp) }
                    }
                    return true
                }
                MotionEvent.ACTION_UP -> {
                    mainHandler.removeCallbacks(longPressRunnable!!)
                    if (!didLongPress && abs(e.rawX - tx) < 8 && abs(e.rawY - ty) < 8) {
                        onBubbleTap()
                    }
                    return true
                }
            }
            return false
        }
    }

    // --- State machine ---

    private fun onBubbleTap() {
        when (state) {
            State.INACTIVE -> beginActivation()
            State.ACTIVE, State.SELECTING, State.TRANSLATING -> deactivate()
            State.WAITING_PERMISSION -> { /* wait quietly */ }
        }
    }

    @SuppressLint("NewApi")
    private fun beginActivation() {
        if (screenCapture != null) {
            transition(State.SELECTING)
            return
        }
        // On API 29+, the foreground service must declare mediaProjection type
        // before the system shows the consent dialog (required from API 34).
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID,
                buildNotification("Requesting screen capture permission..."),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION
            )
        }
        transition(State.WAITING_PERMISSION)
        startActivity(
            Intent(this, ScreenCapturePermissionActivity::class.java)
                .addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
                )
        )
    }

    private fun deactivate() {
        transition(State.INACTIVE)
        clearOverlays()
    }

    private fun transition(next: State) {
        state = next
        renderBubble()
        val msg = when (next) {
            State.INACTIVE -> "Bridge is ready"
            State.WAITING_PERMISSION -> "Waiting for screen capture permission..."
            State.SELECTING, State.TRANSLATING, State.ACTIVE -> "Bridge is translating your screen"
        }
        postNotification(msg)
        if (next == State.SELECTING) showSelectionOverlay()
    }

    // --- MediaProjection result ---

    private fun handleProjectionResult(intent: Intent) {
        val code = intent.getIntExtra(
            ScreenCapturePermissionActivity.EXTRA_RESULT_CODE, Activity.RESULT_CANCELED
        )
        @Suppress("DEPRECATION")
        val data: Intent? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            intent.getParcelableExtra(
                ScreenCapturePermissionActivity.EXTRA_RESULT_DATA, Intent::class.java
            )
        else
            intent.getParcelableExtra(ScreenCapturePermissionActivity.EXTRA_RESULT_DATA)

        Log.d(TAG, "handleProjectionResult code=$code dataNull=${data == null}")

        if (code != Activity.RESULT_OK || data == null) {
            Toast.makeText(this, "Screen capture cancelled - tap bubble to try again", Toast.LENGTH_LONG).show()
            transition(State.INACTIVE)
            return
        }

        // Wrap in try-catch: getMediaProjection() throws SecurityException on Android 14+
        // if the foreground service type wasn't declared correctly, and throws
        // IllegalStateException on some OEM ROMs. Either way we must not let it
        // crash the service process (which would remove the bubble from the screen).
        try {
            val mgr = ScreenCaptureManager(this)
            mgr.initialize(code, data)
            screenCapture = mgr
            Toast.makeText(this, "Draw a box around text to translate", Toast.LENGTH_LONG).show()
            transition(State.SELECTING)
        } catch (e: SecurityException) {
            Log.e(TAG, "getMediaProjection denied - missing FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION?", e)
            Toast.makeText(
                this,
                "Screen capture blocked by Android security - restart the app and try again",
                Toast.LENGTH_LONG
            ).show()
            transition(State.INACTIVE)
        } catch (e: Exception) {
            Log.e(TAG, "Screen capture init failed", e)
            Toast.makeText(this, "Screen capture failed: ${e.message}", Toast.LENGTH_LONG).show()
            transition(State.INACTIVE)
        }
    }

    // --- Selection overlay ---

    private fun showSelectionOverlay() {
        dismissSelectionOverlay()
        val m = resources.displayMetrics
        val ctx = ContextThemeWrapper(this, R.style.Theme_BridgeTranslator)
        selectionOverlay = SelectionOverlayView(ctx).apply {
            listener = object : SelectionOverlayView.SelectionListener {
                override fun onSelectionComplete(rect: Rect) {
                    dismissSelectionOverlay()
                    captureAndTranslate(rect)
                }
                override fun onCancelled() = deactivate()
            }
        }
        val lp = WindowManager.LayoutParams(
            m.widthPixels, m.heightPixels,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply { gravity = Gravity.TOP or Gravity.START }
        try {
            wm.addView(selectionOverlay, lp)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to show selection overlay", e)
            selectionOverlay = null
            deactivate()
        }
    }

    private fun dismissSelectionOverlay() {
        safeRemove(selectionOverlay)
        selectionOverlay = null
    }

    // --- Capture -> OCR -> Translate pipeline ---

    private fun captureAndTranslate(region: Rect) {
        transition(State.TRANSLATING)
        scope.launch {
            val full: Bitmap? = withContext(Dispatchers.IO) { screenCapture?.captureScreen() }
            if (full == null) {
                Toast.makeText(this@FloatingBubbleService, "Capture failed - tap to retry", Toast.LENGTH_SHORT).show()
                deactivate()
                return@launch
            }

            val safe = Rect(
                region.left.coerceIn(0, full.width),
                region.top.coerceIn(0, full.height),
                region.right.coerceIn(0, full.width),
                region.bottom.coerceIn(0, full.height)
            )
            val crop = withContext(Dispatchers.IO) {
                Bitmap.createBitmap(full, safe.left, safe.top, safe.width(), safe.height())
                    .also { full.recycle() }
            }

            val blocks = runOcr(crop, safe)
            withContext(Dispatchers.IO) { crop.recycle() }

            if (blocks.isEmpty()) {
                Toast.makeText(this@FloatingBubbleService, "No text found in selection", Toast.LENGTH_SHORT).show()
                deactivate()
                return@launch
            }

            val langMgr = LanguageManager(this@FloatingBubbleService)
            val src = langMgr.sourceLangCode.first()
            val tgt = langMgr.targetLangCode.first()
            val translated = translateAll(blocks, src, tgt)

            if (translationOverlay == null) {
                translationOverlay = TranslationOverlayWindow(this@FloatingBubbleService)
            }
            translationOverlay!!.showTranslations(translated)
            transition(State.ACTIVE)
        }
    }

    private suspend fun runOcr(bitmap: Bitmap, regionOffset: Rect): List<DetectedTextBlock> =
        suspendCoroutine { cont ->
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            recognizer.process(InputImage.fromBitmap(bitmap, 0))
                .addOnSuccessListener { result ->
                    recognizer.close()
                    val blocks = result.textBlocks.mapNotNull { block ->
                        val b = block.boundingBox ?: return@mapNotNull null
                        DetectedTextBlock(
                            text = block.text,
                            bounds = Rect(
                                b.left + regionOffset.left,
                                b.top + regionOffset.top,
                                b.right + regionOffset.left,
                                b.bottom + regionOffset.top
                            ),
                            confidence = block.lines.firstOrNull()?.confidence ?: 1f
                        )
                    }
                    cont.resume(blocks)
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "OCR failed", e)
                    recognizer.close()
                    cont.resume(emptyList())
                }
        }

    private suspend fun translateAll(
        blocks: List<DetectedTextBlock>,
        src: String,
        tgt: String
    ): List<TranslatedBlock> = coroutineScope {
        blocks.map { block ->
            async(Dispatchers.IO) {
                val translated = suspendCoroutine<String> { cont ->
                    engine.translate(
                        text = block.text,
                        srcCode = src,
                        tgtCode = tgt,
                        onSuccess = { cont.resume(it) },
                        onError = { cont.resume(block.text) } // fall back to original on error
                    )
                }
                TranslatedBlock(original = block, translatedText = translated)
            }
        }.awaitAll()
    }

    // --- Overlay cleanup ---

    private fun clearOverlays() {
        dismissSelectionOverlay()
        translationOverlay?.clearOverlays()
        translationOverlay = null
    }

    // --- Bubble visuals ---

    private fun renderBubble() {
        val isActive = state != State.INACTIVE
        val icon = bubbleView?.findViewById<ImageView>(R.id.iv_bubble)

        if (isActive) {
            icon?.setColorFilter(getColor(R.color.white))
            bubbleView?.startAnimation(AnimationUtils.loadAnimation(this, R.anim.pulse_animation))
        } else {
            bubbleView?.clearAnimation()
            icon?.setColorFilter(getColor(R.color.text_primary))
        }
    }

    // --- Notification ---

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel(
                CHANNEL_ID, "Bridge Floating Service", NotificationManager.IMPORTANCE_LOW
            ).also { getSystemService(NotificationManager::class.java).createNotificationChannel(it) }
        }
    }

    private fun buildNotification(text: String): Notification {
        val openPi = PendingIntent.getActivity(
            this, 0, Intent(this, HomeActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val stopPi = PendingIntent.getService(
            this, 1,
            Intent(this, FloatingBubbleService::class.java).apply { action = ACTION_STOP },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Bridge Translator")
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_translate)
            .setContentIntent(openPi)
            .addAction(R.drawable.ic_close, "Stop", stopPi)
            .setOngoing(true)
            .build()
    }

    private fun postNotification(text: String) =
        getSystemService(NotificationManager::class.java).notify(NOTIFICATION_ID, buildNotification(text))

    // --- Utilities ---

    private fun openMainActivity() {
        startActivity(Intent(this, HomeActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    private fun safeRemove(v: View?) = v?.let { try { wm.removeView(it) } catch (_: Exception) {} }

    private fun dp(value: Int) = (value * resources.displayMetrics.density).toInt()
}
