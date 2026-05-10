package com.bridge.translator.service

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap

class ScreenCaptureHelper(context: Context) {
    private val manager = ScreenCaptureManager(context)

    val isInitialized: Boolean get() = manager.isInitialized

    fun initialize(resultCode: Int, data: Intent) = manager.initialize(resultCode, data)

    fun captureBitmap(): Bitmap? = manager.captureScreen()

    fun captureFrame(): Bitmap? = manager.captureFrame()

    fun invalidateCache() = manager.invalidateCache()

    fun onConfigurationChanged() = manager.onConfigurationChanged()

    fun release() = manager.release()
}
