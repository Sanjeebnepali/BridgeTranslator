package com.bridge.translator.overlay

import android.content.Context
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.ImageView

private const val FLOW_TAG = "BridgeFlow"

/**
 * Pure visual overlay. The window is **completely non-touchable** — every
 * touch falls straight through to the underlying app, exactly as if the
 * overlay weren't there. Dismissal happens via the bubble (which is in its
 * SHOWING state — red ✕ icon — while an overlay is up); see
 * [com.bridge.translator.service.FloatingBubbleService] for the wiring.
 *
 * This is the same UX pattern used by Google Lens, Apple Live Text, and
 * Microsoft Translator camera modes — a separate close affordance instead of
 * tap-anywhere-to-dismiss, because intercepting taps would steal the user's
 * gesture from the underlying app and cancel scrolls/clicks mid-stream.
 */
class OverlayManager(
    private val context: Context,
    private val windowManager: WindowManager,
    private val screenW: Int,
    private val screenH: Int
) {

    private var imageView: ImageView? = null
    private var currentBitmap: Bitmap? = null
    private var isShowing = false
    private var isHiddenForCapture = false

    val isVisible: Boolean
        get() = isShowing && (imageView?.alpha ?: 0f) > 0.01f

    var onFirstTouch: (() -> Unit)? = null

    /** Whether the overlay window is currently attached to the WindowManager. */
    fun isAttached(): Boolean = isShowing

    /**
     * Permanent flags. The overlay must NEVER receive input — touches must
     * fall straight through to the underlying app. Removing FLAG_NOT_TOUCHABLE
     * here is the bug that previously broke single-tap clicks and scrolls.
     */
    private val staticFlags =
        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                WindowManager.LayoutParams.FLAG_FULLSCREEN

    private val params = WindowManager.LayoutParams(
        screenW,
        screenH,
        overlayType(),
        staticFlags,
        PixelFormat.TRANSLUCENT
    ).apply {
        gravity = Gravity.TOP or Gravity.START
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
    }

    fun show(processedBitmap: Bitmap) {
        Log.d(FLOW_TAG, "OverlayManager.show bitmap=${processedBitmap.width}x${processedBitmap.height} isShowing=$isShowing")
        val oldBitmap = currentBitmap
        currentBitmap = processedBitmap

        if (imageView == null) {
            imageView = ImageView(context).apply {
                scaleType = ImageView.ScaleType.FIT_XY
                alpha = 0f
                // Hardware layer keeps the alpha animation a single GPU op
                // instead of a per-frame software redraw of a fullscreen bitmap.
                setLayerType(View.LAYER_TYPE_HARDWARE, null)
                setOnTouchListener { _, event ->
                    if (event.action == android.view.MotionEvent.ACTION_DOWN) {
                        onFirstTouch?.invoke()
                        true
                    } else false
                }
            }
        }
        val view = imageView ?: return

        view.setImageBitmap(processedBitmap)
        if (oldBitmap !== processedBitmap) oldBitmap?.recycle()
        isHiddenForCapture = false

        if (!isShowing) {
            try {
                windowManager.addView(view, params)
                isShowing = true
                Log.d(FLOW_TAG, "OverlayManager.addView OK")
            } catch (e: Exception) {
                Log.e(FLOW_TAG, "OverlayManager.addView failed: ${e.message}", e)
                return
            }
        }

        // Smooth fade-in. If the view was already visible (re-translate of
        // the same screen) the bitmap swap is instant under a stable alpha,
        // which the user perceives as a clean update rather than a flash.
        view.animate().cancel()
        view.animate()
            .alpha(1f)
            .setDuration(FADE_IN_MS)
            .start()
    }

    /**
     * Instant alpha=0; preserved for a future auto-mode capture flow. Not
     * used by the manual snap-and-translate path.
     */
    fun hideForCapture() {
        Log.d(FLOW_TAG, "OverlayManager.hideForCapture isShowing=$isShowing hidden=$isHiddenForCapture")
        if (!isShowing || isHiddenForCapture) return
        val view = imageView ?: return
        view.animate().cancel()
        view.alpha = 0f
        isHiddenForCapture = true
    }

    fun restoreAfterCapture() {
        if (isShowing && isHiddenForCapture) {
            Log.d(FLOW_TAG, "OverlayManager.restoreAfterCapture")
            val view = imageView ?: return
            view.animate().cancel()
            view.animate()
                .alpha(1f)
                .setDuration(FADE_IN_MS)
                .start()
            isHiddenForCapture = false
        }
    }

    /** Smooth fade-out. Used by the bubble service on dismiss. */
    fun fadeOut() {
        if (!isShowing) return
        val view = imageView ?: return
        view.animate().cancel()
        view.animate()
            .alpha(0f)
            .setDuration(FADE_OUT_MS)
            .start()
        isHiddenForCapture = true
    }

    /** Snap-out; preserved for a future auto-mode. Not used manually. */
    fun fadeOutFast() {
        if (!isShowing) return
        val view = imageView ?: return
        view.animate().cancel()
        view.animate()
            .alpha(0f)
            .setDuration(FADE_OUT_FAST_MS)
            .start()
        isHiddenForCapture = true
    }

    fun remove() {
        Log.d(FLOW_TAG, "OverlayManager.remove isShowing=$isShowing")
        imageView?.animate()?.cancel()
        if (isShowing && imageView != null) {
            runCatching { windowManager.removeView(imageView) }
                .onFailure { Log.w(FLOW_TAG, "OverlayManager.remove failed: ${it.message}") }
            isShowing = false
        }
        isHiddenForCapture = false
        imageView?.setImageDrawable(null)
        imageView?.setOnTouchListener(null)
        imageView?.alpha = 0f
        currentBitmap?.recycle()
        currentBitmap = null
    }

    private fun overlayType(): Int =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

    private companion object {
        const val FADE_IN_MS = 140L
        const val FADE_OUT_MS = 120L
        const val FADE_OUT_FAST_MS = 50L
    }
}
