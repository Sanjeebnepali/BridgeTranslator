package com.bridge.translator.overlay

import android.content.Context
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.WindowManager

class OverlayRenderer(private val context: Context) {

    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val canvasView = TranslationCanvasView(context)
    private var isOverlayAdded = false

    fun showOverlay(bitmap: Bitmap, blocks: List<TranslatedBlock>) {
        canvasView.setBitmap(bitmap)
        canvasView.setBlocks(blocks)
        if (!isOverlayAdded) {
            windowManager.addView(canvasView, layoutParams())
            isOverlayAdded = true
        } else {
            canvasView.invalidate()
        }
    }

    fun removeOverlay() {
        if (isOverlayAdded) {
            runCatching { windowManager.removeView(canvasView) }
            isOverlayAdded = false
        }
        canvasView.clearBlocks()
    }

    private fun layoutParams(): WindowManager.LayoutParams {
        val metrics = context.resources.displayMetrics
        return WindowManager.LayoutParams(
            metrics.widthPixels,
            metrics.heightPixels,
            overlayType(),
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
        }
    }

    private fun overlayType(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }
    }
}
