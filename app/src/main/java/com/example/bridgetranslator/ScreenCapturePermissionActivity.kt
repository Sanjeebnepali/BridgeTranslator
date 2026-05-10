package com.example.bridgetranslator

import android.app.Activity
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.FrameLayout

private const val TAG = "BridgeDebug"

/**
 * Transparent trampoline for the system MediaProjection consent dialog.
 *
 * Keep this activity visually empty. Drawing our own fullscreen "Tap Allow" UI here can obscure
 * or visually compete with Android's capture consent screen on some OEM builds.
 */
class ScreenCapturePermissionActivity : Activity() {

    companion object {
        const val EXTRA_RESULT_CODE = "extra_result_code"
        const val EXTRA_RESULT_DATA = "extra_result_data"
        private const val REQ_SCREEN_CAPTURE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "ScreenCapturePermissionActivity: onCreate")
        setContentView(FrameLayout(this))

        Handler(Looper.getMainLooper()).post {
            val mgr = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
            Log.d(TAG, "ScreenCapturePermissionActivity: launching system capture dialog")
            @Suppress("DEPRECATION")
            startActivityForResult(mgr.createScreenCaptureIntent(), REQ_SCREEN_CAPTURE)
            Log.d(TAG, "ScreenCapturePermissionActivity: startActivityForResult returned")
        }
    }

    @Deprecated("Required for API < 33 compatibility")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "ScreenCapturePermissionActivity: onActivityResult requestCode=$requestCode resultCode=$resultCode data=${if (data != null) "present" else "null"}")
        if (requestCode == REQ_SCREEN_CAPTURE) {
            val serviceClass = com.bridge.translator.service.FloatingBubbleService::class.java
            val serviceIntent = Intent(this, serviceClass).apply {
                action = com.bridge.translator.service.FloatingBubbleService.ACTION_MEDIA_PROJECTION_RESULT
                putExtra(com.bridge.translator.service.FloatingBubbleService.EXTRA_RESULT_CODE, resultCode)
                putExtra(com.bridge.translator.service.FloatingBubbleService.EXTRA_RESULT_DATA, data)
            }
            Log.d(TAG, "ScreenCapturePermissionActivity: forwarding result to FloatingBubbleService")
            try {
                startForegroundService(serviceIntent)
                Log.d(TAG, "ScreenCapturePermissionActivity: startForegroundService OK")
            } catch (e: Exception) {
                Log.w(TAG, "ScreenCapturePermissionActivity: startForegroundService failed (${e.message}), trying startService")
                startService(serviceIntent)
            }
        }
        Log.d(TAG, "ScreenCapturePermissionActivity: calling finish()")
        finish()
    }
}
