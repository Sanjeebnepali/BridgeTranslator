package com.bridge.translator.service

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager

class ScreenCaptureManager(private val context: Context) {

    private var mediaProjection: MediaProjection? = null
    private var virtualDisplay: VirtualDisplay? = null
    private var imageReader: ImageReader? = null
    private var projectionCallback: MediaProjection.Callback? = null
    private var captureWidth = 0
    private var captureHeight = 0
    private var cachedFrame: Bitmap? = null

    val isInitialized: Boolean get() = mediaProjection != null

    private fun realDisplayMetrics(): DisplayMetrics {
        val metrics = DisplayMetrics()
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        @Suppress("DEPRECATION")
        wm.defaultDisplay.getRealMetrics(metrics)
        return metrics
    }

    /**
     * Call immediately after the system MediaProjection consent is granted.
     * Throws SecurityException on Android 14+ if the foreground service did not declare
     * FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION - the caller must catch and handle it.
     */
    fun initialize(resultCode: Int, data: Intent) {
        Log.d(FLOW_TAG, "ScreenCaptureManager.initialize resultCode=$resultCode")
        release()
        val mgr = context.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        val projection = mgr.getMediaProjection(resultCode, data)
        // Android 14 requires a callback registered before createVirtualDisplay()
        val callback = object : MediaProjection.Callback() {
            override fun onStop() {
                Log.d(TAG, "MediaProjection.onStop: releasing")
                release()
            }
        }
        projection.registerCallback(callback, Handler(Looper.getMainLooper()))
        projectionCallback = callback
        mediaProjection = projection
        buildVirtualDisplay()
    }

    private fun buildVirtualDisplay() {
        val metrics = realDisplayMetrics()
        val w = metrics.widthPixels
        val h = metrics.heightPixels
        val dpi = metrics.densityDpi
        captureWidth = w
        captureHeight = h
        imageReader?.close()
        imageReader = ImageReader.newInstance(w, h, PixelFormat.RGBA_8888, 2)
        virtualDisplay?.release()
        virtualDisplay = try {
            Log.d(FLOW_TAG, "createVirtualDisplay requested ${w}x${h} dpi=$dpi")
            mediaProjection?.createVirtualDisplay(
                "BridgeCaptureDisplay", w, h, dpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                imageReader?.surface, null, null
            )
        } catch (e: SecurityException) {
            Log.e(TAG, "createVirtualDisplay failed: ${e.message}")
            release()
            throw e
        }
        Log.d(TAG, "VirtualDisplay built ${w}x${h} dpi=$dpi")
        Log.d(FLOW_TAG, "VirtualDisplay built ${w}x${h} dpi=$dpi")
    }

    /**
     * Polls up to 500 ms for the first rendered frame and returns it as a Bitmap.
     * Must be called from a background thread (contains Thread.sleep).
     * Returns null if no frame arrives in time.
     */
    fun captureScreen(): Bitmap? {
        val w = captureWidth.takeIf { it > 0 } ?: realDisplayMetrics().widthPixels
        val h = captureHeight.takeIf { it > 0 } ?: realDisplayMetrics().heightPixels
        Log.d(FLOW_TAG, "captureScreen polling size=${w}x${h}")
        repeat(10) {
            val image = imageReader?.acquireLatestImage()
            if (image != null) {
                Log.d(FLOW_TAG, "captureScreen image acquired attempt=${it + 1}")
                return try {
                    val plane = image.planes[0]
                    val buffer = plane.buffer
                    val pixStride = plane.pixelStride
                    val rowStride = plane.rowStride
                    val rowPadding = rowStride - pixStride * w

                    val raw = Bitmap.createBitmap(w + rowPadding / pixStride, h, Bitmap.Config.ARGB_8888)
                    raw.copyPixelsFromBuffer(buffer)

                    if (rowPadding > 0) {
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
        Log.w(TAG, "captureScreen: no frame after 500 ms")
        Log.w(FLOW_TAG, "captureScreen returned null after polling")
        return null
    }

    fun captureFrame(): Bitmap? {
        val fresh = captureScreen()
        if (fresh != null) {
            cachedFrame?.recycle()
            cachedFrame = fresh.copy(fresh.config ?: Bitmap.Config.ARGB_8888, false)
            return fresh
        }
        Log.w(FLOW_TAG, "captureFrame: buffer empty, returning cached frame")
        val cached = cachedFrame ?: return null
        return cached.copy(cached.config ?: Bitmap.Config.ARGB_8888, false)
    }

    fun invalidateCache() {
        cachedFrame?.recycle()
        cachedFrame = null
    }

    /**
     * A MediaProjection consent token is single-use on newer Android versions. Do not rebuild the
     * VirtualDisplay after rotation/config changes; release this session and ask for consent again.
     */
    fun onConfigurationChanged() {
        if (mediaProjection != null) {
            Log.d(TAG, "configuration changed: invalidating capture session")
            release()
        }
    }

    fun release() {
        Log.d(FLOW_TAG, "ScreenCaptureManager.release projectionPresent=${mediaProjection != null}")
        val projection = mediaProjection
        val callback = projectionCallback
        mediaProjection = null
        projectionCallback = null

        virtualDisplay?.release(); virtualDisplay = null
        imageReader?.close();      imageReader = null
        captureWidth = 0
        captureHeight = 0
        cachedFrame?.recycle();    cachedFrame = null

        if (projection != null) {
            if (callback != null) {
                runCatching { projection.unregisterCallback(callback) }
            }
            runCatching { projection.stop() }
                .onFailure { Log.w(TAG, "projection stop failed: ${it.message}") }
        }
        Log.d(TAG, "released")
    }

    private companion object {
        const val TAG = "ScreenCaptureManager"
        const val FLOW_TAG = "BridgeFlow"
    }
}
