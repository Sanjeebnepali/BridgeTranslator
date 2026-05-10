package com.example.bridgetranslator

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.util.Log

class ScreenCaptureManager(private val context: Context) {

    private var mediaProjection: MediaProjection? = null
    private var virtualDisplay: VirtualDisplay? = null
    private var imageReader: ImageReader? = null

    // resources.displayMetrics is safe from any context including Service.
    // currentWindowMetrics (API 30+) requires a UI context and throws from services.
    private fun screenWidth()  = context.resources.displayMetrics.widthPixels
    private fun screenHeight() = context.resources.displayMetrics.heightPixels
    private fun screenDpi()    = context.resources.displayMetrics.densityDpi

    /**
     * Call this immediately after the system MediaProjection consent is granted.
     * Throws SecurityException on Android 14+ if the calling foreground service
     * did not declare FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION - let the caller catch it.
     */
    fun initialize(resultCode: Int, data: Intent) {
        val mgr = context.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        mediaProjection = mgr.getMediaProjection(resultCode, data)
        rebuildVirtualDisplay()
    }

    private fun rebuildVirtualDisplay() {
        val w = screenWidth(); val h = screenHeight(); val dpi = screenDpi()

        imageReader?.close()
        imageReader = ImageReader.newInstance(w, h, PixelFormat.RGBA_8888, 2)

        virtualDisplay?.release()
        virtualDisplay = mediaProjection?.createVirtualDisplay(
            "BridgeCaptureDisplay",
            w, h, dpi,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            imageReader?.surface,
            null, null
        )
        Log.d("SCM", "VirtualDisplay rebuilt ${w}x${h} dpi=$dpi")
    }

    /** Called from IO thread. Polls up to 500 ms for the first rendered frame. */
    fun captureScreen(): Bitmap? {
        val w = screenWidth(); val h = screenHeight()
        repeat(10) {
            val image = imageReader?.acquireLatestImage()
            if (image != null) {
                return try {
                    val planes    = image.planes
                    val buffer    = planes[0].buffer
                    val pixStride = planes[0].pixelStride
                    val rowStride = planes[0].rowStride
                    val padding   = rowStride - pixStride * w

                    val raw = Bitmap.createBitmap(w + padding / pixStride, h, Bitmap.Config.ARGB_8888)
                    raw.copyPixelsFromBuffer(buffer)

                    if (padding > 0) {
                        val cropped = Bitmap.createBitmap(raw, 0, 0, w, h)
                        raw.recycle()
                        cropped
                    } else raw
                } finally {
                    image.close()
                }
            }
            Thread.sleep(50)
        }
        Log.w("SCM", "captureScreen: no frame after 500 ms")
        return null
    }

    fun onConfigurationChanged() {
        if (mediaProjection != null) rebuildVirtualDisplay()
    }

    fun release() {
        virtualDisplay?.release(); virtualDisplay = null
        imageReader?.close();      imageReader = null
        mediaProjection?.stop();   mediaProjection = null
    }
}
