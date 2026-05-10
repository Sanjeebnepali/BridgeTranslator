package com.example.bridgetranslator

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import com.bridge.translator.processing.DetectedShape
import com.bridge.translator.processing.ShapeDetector
import com.bridge.translator.processing.TranslationPipeline
import com.bridge.translator.tts.SpeechEngine
import com.bridge.translator.ui.ShapeOverlayView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.Locale

/**
 * Live-scan camera activity with geometric shape detection, per-shape OCR + translation,
 * and Text-to-Speech.
 *
 * Flow:
 * 1. CameraX preview starts; ShapeDetector runs per frame → ShapeOverlayView updates.
 * 2. User taps Capture FAB → preview freezes, progress dialog shows.
 * 3. TranslationPipeline processes all shapes concurrently.
 * 4. Result composited back onto frozen preview; overlay removed.
 * 5. Speaker button becomes active; reads all translations in order.
 */
class CameraFeatureActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "CameraFeatureActivity"
        private const val FRAME_ANALYSIS_INTERVAL_MS = 200L    // ~5 fps shape detection
        private const val REQUEST_CAMERA_PERMISSION  = 1001
    }

    // ── Views ──────────────────────────────────────────────────────────────────

    private lateinit var previewView:      PreviewView
    private lateinit var shapeOverlayView: ShapeOverlayView
    private lateinit var ivResult:         ImageView
    private lateinit var btnCapture:       View
    private lateinit var btnSpeaker:       View
    private lateinit var btnShare:         View
    private lateinit var btnClose:         View
    private lateinit var tvStatus:         TextView
    private lateinit var tvShapeCount:     TextView
    private lateinit var progressBar:      ProgressBar

    // ── State ──────────────────────────────────────────────────────────────────

    private var isFrozen              = false
    private var isProcessing          = false
    private var lastAnalysisMs        = 0L
    private var currentShapes         = listOf<DetectedShape>()
    private var translationJob: Job?  = null
    private var currentResultBitmap: Bitmap? = null
    private var pendingTranslations   = listOf<String>()

    // ── ML Kit pipeline ────────────────────────────────────────────────────────

    private val pipeline = TranslationPipeline()
    private lateinit var languageManager: LanguageManager

    // ── Permission launcher ────────────────────────────────────────────────────

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) startCamera()
        else {
            Toast.makeText(this, "Camera permission required for Live Scan", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    // ── Lifecycle ──────────────────────────────────────────────────────────────

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_feature)

        languageManager = LanguageManager(this)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        applyWindowInsets()
        bindViews()
        restoreSettingsState()

        lifecycleScope.launch {
            ShapeDetector.init(
                this@CameraFeatureActivity,
                useGpuDelegate = isFastModeEnabled()
            )
        }

        btnCapture.setOnClickListener { onCaptureClicked() }
        btnClose.setOnClickListener   { finish() }
        btnSpeaker.setOnClickListener { onSpeakerClicked() }
        btnShare.setOnClickListener   { onShareClicked() }

        if (hasCameraPermission()) startCamera()
        else cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
    }

    override fun onResume() {
        super.onResume()
        SpeechEngine.setOnReadingStateChanged { isReading ->
            runOnUiThread { setSpeakerActive(isReading) }
        }
    }

    override fun onPause() {
        super.onPause()
        SpeechEngine.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        translationJob?.cancel()
        pipeline.close()
        currentResultBitmap?.recycle()
    }

    // ── Camera ─────────────────────────────────────────────────────────────────

    private fun startCamera() {
        val future = ProcessCameraProvider.getInstance(this)
        future.addListener({
            val provider = future.get()
            val preview  = Preview.Builder().build().apply {
                setSurfaceProvider(previewView.surfaceProvider)
            }
            val analysis = ImageAnalysis.Builder()
                .setTargetResolution(Size(1080, 1920))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .apply {
                    setAnalyzer(ContextCompat.getMainExecutor(this@CameraFeatureActivity)) { proxy ->
                        analyzeFrame(proxy)
                    }
                }
            try {
                provider.unbindAll()
                provider.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA, preview, analysis)
                tvStatus.text = "Point camera at text on a shape"
            } catch (e: Exception) {
                Log.e(TAG, "Camera bind failed: ${e.message}")
                Toast.makeText(this, "Camera error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    @ExperimentalGetImage
    private fun analyzeFrame(proxy: ImageProxy) {
        val now = System.currentTimeMillis()
        if (isFrozen || isProcessing || now - lastAnalysisMs < FRAME_ANALYSIS_INTERVAL_MS) {
            proxy.close()
            return
        }
        lastAnalysisMs = now

        val bitmap = imageProxyToBitmap(proxy) ?: run { proxy.close(); return }
        proxy.close()

        lifecycleScope.launch(Dispatchers.Default) {
            val shapes = if (isFastModeEnabled())
                ShapeDetector.detectFastMode(bitmap)
            else
                ShapeDetector.detect(bitmap)

            bitmap.recycle()

            withContext(Dispatchers.Main) {
                if (!isFrozen && !isProcessing) {
                    currentShapes = shapes
                    shapeOverlayView.setShapes(
                        shapes,
                        previewView.width.coerceAtLeast(1),
                        previewView.height.coerceAtLeast(1)
                    )
                    tvShapeCount.text = if (shapes.isEmpty()) ""
                    else "${shapes.size} shape${if (shapes.size > 1) "s" else ""} detected"
                }
            }
        }
    }

    // ── Capture ────────────────────────────────────────────────────────────────

    private fun onCaptureClicked() {
        if (isProcessing) return

        if (isFrozen) {
            // Second tap: reset back to live scan
            resetToLiveScan()
            return
        }

        // Freeze preview
        isFrozen    = true
        isProcessing = true
        btnCapture.isEnabled = false
        shapeOverlayView.visibility = View.INVISIBLE

        val frozenBitmap = previewView.bitmap
        if (frozenBitmap == null) {
            Toast.makeText(this, "Could not capture frame", Toast.LENGTH_SHORT).show()
            resetToLiveScan()
            return
        }

        showProgressDialog()
        tvStatus.text = "Translating ${currentShapes.size} shape${if (currentShapes.size != 1) "s" else ""}…"

        translationJob = lifecycleScope.launch {
            lifecycleScope.launch(Dispatchers.Default) {
                val targetLang = languageManager.targetLangCode.first()

                val (resultBitmap, shapeResults) = pipeline.process(
                    frame        = frozenBitmap,
                    shapes       = currentShapes,
                    targetLang   = targetLang,
                    fallbackLang = "ko",
                    onShapeStart = { idx ->
                        runOnUiThread { shapeOverlayView.highlightShape(idx) }
                    }
                )

                val translations = shapeResults
                    .sortedWith(compareBy({ it.shape.bounds.top }, { it.shape.bounds.left }))
                    .map { it.translatedText }

                withContext(Dispatchers.Main) {
                    hideProgressDialog()
                    isProcessing = false
                    btnCapture.isEnabled = true

                    currentResultBitmap = resultBitmap
                    pendingTranslations = translations

                    ivResult.setImageBitmap(resultBitmap)
                    ivResult.visibility = View.VISIBLE
                    shapeOverlayView.clearShapes()
                    shapeOverlayView.visibility = View.VISIBLE

                    if (translations.isNotEmpty()) {
                        btnSpeaker.isEnabled = true
                        btnSpeaker.alpha = 1f
                        tvStatus.text = "Tap speaker to hear · tap image to rescan"
                        if (isSpeakerAutoEnabled()) {
                            SpeechEngine.speakTranslatedText(translations)
                        }
                    } else {
                        tvStatus.text = "No translatable text found · tap to rescan"
                    }
                }
            }
        }
    }

    private var progressDialog: AlertDialog? = null

    private fun showProgressDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_progress, null)
        progressDialog = AlertDialog.Builder(this)
            .setView(view)
            .setCancelable(false)
            .create()
        progressDialog?.show()
    }

    private fun hideProgressDialog() {
        progressDialog?.dismiss()
        progressDialog = null
    }

    // ── Speaker ────────────────────────────────────────────────────────────────

    private fun onSpeakerClicked() {
        if (SpeechEngine.isSpeaking) {
            SpeechEngine.stop()
        } else {
            if (pendingTranslations.isNotEmpty()) {
                SpeechEngine.speakTranslatedText(pendingTranslations)
            }
        }
    }

    private fun setSpeakerActive(active: Boolean) {
        btnSpeaker.isSelected = active
    }

    // ── Share / Save ──────────────────────────────────────────────────────────

    private fun onShareClicked() {
        val bitmap = currentResultBitmap ?: return
        try {
            val cacheDir = File(cacheDir, "shared_images")
            cacheDir.mkdirs()
            val file = File(cacheDir, "bridge_scan_${System.currentTimeMillis()}.png")
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            val uri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", file)
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(Intent.createChooser(shareIntent, "Share Translation"))
        } catch (e: Exception) {
            Toast.makeText(this, "Could not share: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // ── Reset ──────────────────────────────────────────────────────────────────

    private fun resetToLiveScan() {
        SpeechEngine.stop()
        isFrozen = false
        isProcessing = false
        ivResult.visibility = View.GONE
        ivResult.setImageBitmap(null)
        shapeOverlayView.clearShapes()
        btnSpeaker.isEnabled = false
        btnSpeaker.alpha = 0.4f
        pendingTranslations = emptyList()
        tvStatus.text = "Point camera at text on a shape"
        tvShapeCount.text = ""
        currentResultBitmap?.recycle()
        currentResultBitmap = null
    }

    // ── Settings helpers ───────────────────────────────────────────────────────

    private fun isFastModeEnabled(): Boolean =
        getSharedPreferences("bridge_prefs", Context.MODE_PRIVATE)
            .getBoolean("fast_mode_enabled", false)

    private fun isSpeakerAutoEnabled(): Boolean =
        getSharedPreferences("bridge_prefs", Context.MODE_PRIVATE)
            .getBoolean("speaker_enabled", false)

    // ── Bitmap helpers ─────────────────────────────────────────────────────────

    @ExperimentalGetImage
    private fun imageProxyToBitmap(proxy: ImageProxy): Bitmap? {
        val image = proxy.image ?: return null
        return try {
            val yBuffer = image.planes[0].buffer
            val uBuffer = image.planes[1].buffer
            val vBuffer = image.planes[2].buffer
            val ySize   = yBuffer.remaining()
            val uSize   = uBuffer.remaining()
            val vSize   = vBuffer.remaining()
            val nv21 = ByteArray(ySize + uSize + vSize)
            yBuffer.get(nv21, 0, ySize)
            vBuffer.get(nv21, ySize, vSize)
            uBuffer.get(nv21, ySize + vSize, uSize)
            val yuvImage = android.graphics.YuvImage(
                nv21, android.graphics.ImageFormat.NV21, proxy.width, proxy.height, null
            )
            val out = ByteArrayOutputStream()
            yuvImage.compressToJpeg(android.graphics.Rect(0, 0, proxy.width, proxy.height), 85, out)
            val bytes = out.toByteArray()
            android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } catch (e: Exception) {
            Log.e(TAG, "imageProxyToBitmap failed: ${e.message}")
            null
        }
    }

    // ── UI helpers ─────────────────────────────────────────────────────────────

    private fun bindViews() {
        previewView      = findViewById(R.id.cameraFeaturePreview)
        shapeOverlayView = findViewById(R.id.shapeOverlayView)
        ivResult         = findViewById(R.id.ivCameraFeatureResult)
        btnCapture       = findViewById(R.id.btnCameraFeatureCapture)
        btnSpeaker       = findViewById(R.id.btnCameraFeatureSpeaker)
        btnShare         = findViewById(R.id.btnCameraFeatureShare)
        btnClose         = findViewById(R.id.btnCameraFeatureClose)
        tvStatus         = findViewById(R.id.tvCameraFeatureStatus)
        tvShapeCount     = findViewById(R.id.tvCameraFeatureShapeCount)
        progressBar      = findViewById(R.id.cameraFeatureProgressBar)
    }

    private fun restoreSettingsState() {
        btnSpeaker.isEnabled = false
        btnSpeaker.alpha     = 0.4f
    }

    private fun applyWindowInsets() {
        val topBar    = findViewById<View>(R.id.cameraFeatureTopBar)
        val bottomBar = findViewById<View>(R.id.cameraFeatureBottomBar)
        val origTop    = topBar.paddingTop
        val origBottom = bottomBar.paddingBottom
        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { _, insets ->
            val status = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            val nav    = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            topBar.updatePadding(top    = origTop    + status.top)
            bottomBar.updatePadding(bottom = origBottom + nav.bottom)
            insets
        }
        ViewCompat.requestApplyInsets(window.decorView)
    }

    private fun hasCameraPermission() =
        ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
}
