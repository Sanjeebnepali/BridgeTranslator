package com.bridge.translator.service

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Manages camera operations for BridgeTranslator.
 *
 * Handles:
 * - CameraX initialization and lifecycle
 * - Single frame capture from camera
 * - Device orientation handling
 * - Permission checks
 *
 * Usage:
 * ```
 * val cameraManager = CameraManager(context)
 * lifecycle.addObserver(cameraManager)
 * val bitmap = cameraManager.captureFrame()
 * ```
 */
class CameraManager(
    private val context: Context,
    private val previewView: PreviewView? = null
) : LifecycleObserver {

    companion object {
        private const val TAG = "CameraManager"
    }

    private var imageCapture: ImageCapture? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private val executor: ExecutorService = Executors.newSingleThreadExecutor()

    var isInitialized = false
        private set
    private var isCameraBindingStarted = false

    /**
     * Initialize camera on lifecycle CREATE
     */
    fun setupCamera() {
        if (isInitialized) return

        try {
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
            cameraProviderFuture.addListener({
                try {
                    cameraProvider = cameraProviderFuture.get()

                    // Build ImageCapture with optimized settings
                    imageCapture = ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .setTargetRotation(android.view.Surface.ROTATION_0)
                        .build()

                    isInitialized = true
                    Log.d(TAG, "Camera initialized successfully")
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to initialize camera provider: ${e.message}", e)
                    isInitialized = false
                }
            }, ContextCompat.getMainExecutor(context))
        } catch (e: Exception) {
            Log.e(TAG, "Failed to setup camera: ${e.message}", e)
            isInitialized = false
        }
    }

    /**
     * Start camera binding
     */
    fun startCamera() {
        if (!isInitialized || isCameraBindingStarted) {
            Log.w(TAG, "startCamera: isInitialized=$isInitialized, isCameraBindingStarted=$isCameraBindingStarted")
            return
        }

        bindCameraUseCases()
    }

    /**
     * Stop camera binding
     */
    fun stopCamera() {
        try {
            cameraProvider?.unbindAll()
            isCameraBindingStarted = false
            Log.d(TAG, "Camera stopped")
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping camera: ${e.message}", e)
        }
    }

    /**
     * Clean up resources (called from service onDestroy)
     */
    fun shutdown() {
        try {
            stopCamera()
            cameraProvider?.unbindAll()
            executor.shutdown()
            Log.d(TAG, "Camera resources cleaned up")
        } catch (e: Exception) {
            Log.e(TAG, "Error during shutdown: ${e.message}", e)
        }
    }

    /**
     * Bind camera use cases (Preview + ImageCapture)
     */
    private fun bindCameraUseCases() {
        if (!isInitialized || cameraProvider == null) {
            Log.w(TAG, "Camera not initialized or cameraProvider is null")
            return
        }

        cameraProvider?.let { provider ->
            try {
                provider.unbindAll()

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                // Build Preview for viewfinder (optional)
                val preview = Preview.Builder().build()
                previewView?.let { preview.setSurfaceProvider(it.surfaceProvider) }

                // Get LifecycleOwner - for Service context, we need a workaround
                val lifecycleOwner = context as? LifecycleOwner
                if (lifecycleOwner != null) {
                    provider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageCapture
                    )
                    isCameraBindingStarted = true
                    Log.d(TAG, "Camera use cases bound successfully")
                } else {
                    Log.w(TAG, "Context is not a LifecycleOwner, camera binding skipped")
                    isCameraBindingStarted = false
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to bind camera use cases: ${e.message}", e)
                isCameraBindingStarted = false
            }
        }
    }

    /**
     * Check if camera permission is granted
     */
    fun isCameraPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Check if camera is ready for capture
     */
    fun isReady(): Boolean {
        return isInitialized && imageCapture != null && isCameraBindingStarted
    }

    /**
     * Capture a single frame from camera and return as Bitmap
     *
     * This is a suspending function - call from coroutine scope
     *
     * @return Bitmap of captured frame, or null if capture fails
     */
    suspend fun captureFrame(): Bitmap? {
        Log.d(TAG, "captureFrame called: isReady=${isReady()}, isInitialized=$isInitialized, isCameraBindingStarted=$isCameraBindingStarted, imageCapture=${imageCapture != null}")

        if (!isReady()) {
            Log.w(TAG, "Camera not ready for capture: isInitialized=$isInitialized, imageCapture=${imageCapture != null}, isCameraBindingStarted=$isCameraBindingStarted")
            return null
        }

        return withContext(Dispatchers.Default) {
            var capturedBitmap: Bitmap? = null
            val waitLock = Object()
            var captureError: String? = null

            try {
                imageCapture?.takePicture(
                    executor,
                    object : ImageCapture.OnImageCapturedCallback() {
                        override fun onCaptureSuccess(image: androidx.camera.core.ImageProxy) {
                            try {
                                // Convert ImageProxy to Bitmap
                                capturedBitmap = image.toBitmap()

                                // Rotate bitmap based on device orientation
                                capturedBitmap = rotateIfNeeded(capturedBitmap!!, image.imageInfo.rotationDegrees)

                                Log.d(TAG, "Frame captured successfully (${capturedBitmap?.width}x${capturedBitmap?.height})")
                            } catch (e: Exception) {
                                Log.e(TAG, "Error converting image to bitmap: ${e.message}", e)
                                captureError = e.message
                            } finally {
                                image.close()
                                synchronized(waitLock) {
                                    waitLock.notifyAll()
                                }
                            }
                        }

                        override fun onError(exception: ImageCaptureException) {
                            Log.e(TAG, "Capture failed: ${exception.message}", exception)
                            captureError = exception.message
                            synchronized(waitLock) {
                                waitLock.notifyAll()
                            }
                        }
                    }
                )
            } catch (e: Exception) {
                Log.e(TAG, "Exception calling takePicture: ${e.message}", e)
                return@withContext null
            }

            // Wait for capture to complete (max 5 seconds)
            synchronized(waitLock) {
                waitLock.wait(5000)
            }

            if (captureError != null) {
                Log.e(TAG, "Capture error: $captureError")
            }

            capturedBitmap
        }
    }

    /**
     * Rotate bitmap based on rotation degrees
     */
    private fun rotateIfNeeded(bitmap: Bitmap, rotationDegrees: Int): Bitmap {
        if (rotationDegrees == 0) return bitmap

        return try {
            val matrix = android.graphics.Matrix().apply {
                postRotate(rotationDegrees.toFloat())
            }

            val rotated = Bitmap.createBitmap(
                bitmap,
                0, 0,
                bitmap.width, bitmap.height,
                matrix,
                true
            )

            // Recycle original if different from rotated
            if (rotated != bitmap) {
                bitmap.recycle()
            }

            rotated
        } catch (e: Exception) {
            Log.e(TAG, "Error rotating bitmap: ${e.message}", e)
            bitmap  // Return original on error
        }
    }
}

/**
 * Extension function to convert ImageProxy to Bitmap
 * Handles YUV format conversion
 */
fun androidx.camera.core.ImageProxy.toBitmap(): Bitmap {
    val bitmap = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888)

    // Handle different image formats
    when (this.format) {
        android.graphics.ImageFormat.YUV_420_888 -> {
            // Convert YUV to RGB
            val yPlane = this.planes[0]
            val uPlane = this.planes[1]
            val vPlane = this.planes[2]

            val yPixelStride = yPlane.pixelStride
            val uvPixelStride = uPlane.pixelStride

            val yBuffer = yPlane.buffer
            val uBuffer = uPlane.buffer
            val vBuffer = vPlane.buffer

            val ySize = yBuffer.remaining()
            val uSize = uBuffer.remaining()
            val vSize = vBuffer.remaining()

            val nv21 = ByteArray(ySize + uSize + vSize)

            yBuffer.get(nv21, 0, ySize)
            vBuffer.get(nv21, ySize, vSize)
            uBuffer.get(nv21, ySize + vSize, uSize)

            // Convert NV21 to RGB (simplified - full conversion in production)
            yuvToRgb(nv21, this.width, this.height, bitmap)
        }
        else -> {
            // For RGB formats, copy directly
            this.planes[0].buffer.rewind()
            bitmap.copyPixelsFromBuffer(this.planes[0].buffer)
        }
    }

    return bitmap
}

/**
 * Convert YUV NV21 format to RGB and write to Bitmap
 */
private fun yuvToRgb(yuv: ByteArray, width: Int, height: Int, bitmap: Bitmap) {
    val frameSize = width * height

    for (i in 0 until height) {
        for (j in 0 until width) {
            val y = yuv[i * width + j].toInt() and 0xff
            val uvPixelStride = 2
            val u = yuv[frameSize + (i shr 1) * width + (j and 1.inv()) * uvPixelStride].toInt() and 0xff
            val v = yuv[frameSize + (i shr 1) * width + (j and 1.inv()) * uvPixelStride + 1].toInt() and 0xff

            val r = (y + 1.402f * (v - 128)).toInt().coerceIn(0, 255)
            val g = (y - 0.344f * (u - 128) - 0.714f * (v - 128)).toInt().coerceIn(0, 255)
            val b = (y + 1.772f * (u - 128)).toInt().coerceIn(0, 255)

            bitmap.setPixel(j, i, android.graphics.Color.rgb(r, g, b))
        }
    }
}
