package com.example.bridgetranslator

import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.YuvImage
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import android.util.Size
import android.view.View
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.nl.languageid.LanguageIdentification
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions
import com.google.mlkit.vision.text.devanagari.DevanagariTextRecognizerOptions
import com.google.mlkit.vision.text.japanese.JapaneseTextRecognizerOptions
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class CameraActivity : AppCompatActivity() {

    // Views
    private lateinit var previewView: PreviewView
    private lateinit var tvDetectedLang: TextView
    private lateinit var tvTargetLangLabel: TextView
    private lateinit var tvScanStatus: TextView
    private lateinit var tvOriginalLabel: TextView
    private lateinit var tvOriginalText: TextView
    private lateinit var tvTranslatedLabel: TextView
    private lateinit var tvTranslatedText: TextView
    private lateinit var cardTranslationResult: View
    private lateinit var ivFrozenTranslation: ImageView
    private lateinit var documentVisualScroll: ScrollView
    private lateinit var ivDocumentTranslation: ImageView
    private lateinit var documentFrameView: DocumentFrameView

    // Camera & ML Kit
    private val latinTextRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    private val koreanTextRecognizer = TextRecognition.getClient(
        KoreanTextRecognizerOptions.Builder().build()
    )
    private val chineseTextRecognizer = TextRecognition.getClient(
        ChineseTextRecognizerOptions.Builder().build()
    )
    private val japaneseTextRecognizer = TextRecognition.getClient(
        JapaneseTextRecognizerOptions.Builder().build()
    )
    private val devanagariTextRecognizer = TextRecognition.getClient(
        DevanagariTextRecognizerOptions.Builder().build()
    )
    private val languageIdentifier = LanguageIdentification.getClient()
    private val engine = TranslationEngine()
    private lateinit var languageManager: LanguageManager
    private lateinit var fileExtractor: FileTextExtractor
    private var textToSpeech: TextToSpeech? = null
    private var isTtsReady = false
    private var isReadingTranslation = false
    private var pendingSpeechParts = 0

    // State
    private var lastOcrText = ""
    private var pendingStableText = ""
    private var stableTextHits = 0
    private var lastTranslatedKey = ""
    private var lastResultShownAt = 0L
    private var lastAnalyzedTimeMs = 0L
    private var isAnalyzingFrame = false
    private var isFrozenSession = false
    private var isVisualDocumentSession = false
    private var isCapturingFrame = false
    private var stableLockedAtMs = 0L
    private var currentFrozenBitmap: Bitmap? = null
    private var detectedSrcCode = "und"
    private var targetCode = "en"
    private var translationJob: Job? = null

    // Activity result launchers
    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri -> uri?.let { processUri(it, resolveImageMimeType(it)) } }

    private val fileLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            val mime = contentResolver.getType(it) ?: "application/octet-stream"
            processUri(it, mime)
        }
    }

    private val cameraPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) startCamera()
        else {
            Toast.makeText(this, "Camera permission is required", Toast.LENGTH_LONG).show()
            showPermissionPlaceholder()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        languageManager = LanguageManager(this)
        fileExtractor = FileTextExtractor(this)

        // Edge-to-edge: camera fills full screen, bars are overlaid
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val topBarCamera = findViewById<View>(R.id.topBarCamera)
        val bottomBar = findViewById<View>(R.id.bottomActionBar)
        val origTopPad = topBarCamera.paddingTop
        val origBottomPad = bottomBar.paddingBottom
        val origBottomHeight = bottomBar.layoutParams.height
        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { _, insets ->
            val statusBars = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            val navBars = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            topBarCamera.updatePadding(top = origTopPad + statusBars.top)
            bottomBar.updatePadding(bottom = origBottomPad + navBars.bottom)
            if (origBottomHeight > 0) {
                bottomBar.layoutParams = bottomBar.layoutParams.apply {
                    height = origBottomHeight + navBars.bottom
                }
            }
            insets
        }
        ViewCompat.requestApplyInsets(window.decorView)

        bindViews()
        initTextToSpeech()

        // Load target language and update label
        lifecycleScope.launch {
            targetCode = languageManager.targetLangCode.first()
            val lang = Language.getLanguageByCode(targetCode)
            tvTargetLangLabel.text = lang?.code?.uppercase() ?: targetCode.uppercase()
            tvTranslatedLabel.text = "TRANSLATED (${targetCode.uppercase()})"
            configureTextToSpeechLanguage()
            prewarmCameraTranslationModel()
        }

        // Gallery button
        findViewById<View>(R.id.btnGallery).setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        // File picker - images + PDF + TXT + DOCX + PPTX
        findViewById<View>(R.id.btnFilePicker).setOnClickListener {
            fileLauncher.launch(arrayOf(
                "image/*",
                "application/pdf",
                "text/plain",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "application/vnd.openxmlformats-officedocument.presentationml.presentation",
                "application/msword",
                "application/vnd.ms-powerpoint"
            ))
        }

        // Close
        findViewById<View>(R.id.btnClose).setOnClickListener { finish() }

        // History navigation
        findViewById<View>(R.id.ivHistoryNav).setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }

        // Save translation to history
        findViewById<View>(R.id.btnSaveTranslation).setOnClickListener {
            val src = tvOriginalText.text.toString()
            val res = tvTranslatedText.text.toString()
            if (src.isNotEmpty() && res.isNotEmpty()
                && src != "Point camera at text..."
                && res != "Translation appears here...") {
                lifecycleScope.launch {
                    AppDatabase.get(this@CameraActivity).historyDao().insert(
                        HistoryEntity(
                            sourceText = src,
                            resultText = res,
                            sourceLangCode = detectedSrcCode.ifBlank { "?" },
                            targetLangCode = targetCode
                        )
                    )
                    Toast.makeText(this@CameraActivity, "Saved to history!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Copy translated text
        findViewById<View>(R.id.btnCopyTranslation).setOnClickListener {
            val text = tvTranslatedText.text.toString()
            if (text.isNotEmpty() && text != "Translation appears here...") {
                val cb = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                cb.setPrimaryClip(ClipData.newPlainText("translation", text))
                Toast.makeText(this, "Copied!", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<View>(R.id.topBarCamera).bringToFront()
        findViewById<View>(R.id.btnSpeakTranslation).bringToFront()
        val speakClickListener = View.OnClickListener {
            animateSpeakerTap()
            speakTranslatedText()
        }
        findViewById<View>(R.id.btnSpeakTranslation).setOnClickListener(speakClickListener)
        findViewById<View>(R.id.ivSpeakIcon).setOnClickListener(speakClickListener)
        findViewById<View>(R.id.btnDownloadVisual).setOnClickListener {
            saveCurrentVisualToDownloads()
        }
        findViewById<View>(R.id.btnShareVisual).setOnClickListener {
            shareCurrentVisual()
        }

        findViewById<View>(R.id.ivFrozenTranslation).setOnClickListener {
            if (!isVisualDocumentSession) resetLiveScanSession()
        }

        // Right-side of pill: tap to change target language only
        findViewById<View>(R.id.btnTargetLangPill).setOnClickListener {
            LanguageBottomSheet.newInstance(isSource = false).also { sheet ->
                sheet.setOnLanguageSelectedListener { lang ->
                    targetCode = lang.code
                    tvTargetLangLabel.text = lang.code.uppercase()
                    tvTranslatedLabel.text = "TRANSLATED (${lang.code.uppercase()})"
                    configureTextToSpeechLanguage()
                    prewarmCameraTranslationModel()
                    if (isFrozenSession) {
                        resetLiveScanSession()
                    } else if (cardTranslationResult.visibility == View.VISIBLE && lastOcrText.isNotEmpty()) {
                        onTextDetected(lastOcrText)
                    }
                }
                sheet.show(supportFragmentManager, "camTargetLang")
            }
        }

        // Start live camera
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED) {
            startCamera()
        } else {
            cameraPermission.launch(android.Manifest.permission.CAMERA)
        }
    }

    private fun bindViews() {
        previewView = findViewById(R.id.previewView)
        tvDetectedLang = findViewById(R.id.tvDetectedLang)
        tvTargetLangLabel = findViewById(R.id.tvTargetLangLabel)
        tvScanStatus = findViewById(R.id.tvScanStatus)
        tvOriginalLabel = findViewById(R.id.tvOriginalLabel)
        tvOriginalText = findViewById(R.id.tvOriginalText)
        tvTranslatedLabel = findViewById(R.id.tvTranslatedLabel)
        tvTranslatedText = findViewById(R.id.tvTranslatedText)
        cardTranslationResult = findViewById(R.id.cardTranslationResult)
        ivFrozenTranslation = findViewById(R.id.ivFrozenTranslation)
        documentVisualScroll = findViewById(R.id.documentVisualScroll)
        ivDocumentTranslation = findViewById(R.id.ivDocumentTranslation)
        documentFrameView = findViewById(R.id.documentFrameView)
    }

    // - CameraX -

    private fun startCamera() {
        val future = ProcessCameraProvider.getInstance(this)
        future.addListener({
            val provider = future.get()
            val preview = Preview.Builder().build().apply {
                setSurfaceProvider(previewView.surfaceProvider)
            }
            val analysis = ImageAnalysis.Builder()
                .setTargetResolution(Size(1080, 1920))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .apply {
                    setAnalyzer(ContextCompat.getMainExecutor(this@CameraActivity)) { proxy ->
                        analyzeFrame(proxy)
                    }
                }
            try {
                provider.unbindAll()
                provider.bindToLifecycle(
                    this, CameraSelector.DEFAULT_BACK_CAMERA, preview, analysis
                )
            } catch (e: Exception) {
                Toast.makeText(this, "Camera error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    @androidx.annotation.OptIn(ExperimentalGetImage::class)
    private fun analyzeFrame(imageProxy: ImageProxy) {
        val now = System.currentTimeMillis()
        if (isFrozenSession || isCapturingFrame || isAnalyzingFrame || now - lastAnalyzedTimeMs < FRAME_ANALYSIS_INTERVAL_MS) {
            imageProxy.close()
            return
        }
        lastAnalyzedTimeMs = now

        isAnalyzingFrame = true
        val rawBitmap = imageProxyToBitmap(imageProxy)
        if (rawBitmap == null) {
            isAnalyzingFrame = false
            imageProxy.close()
            return
        }
        val frameBitmap = cropToDocumentFrame(rawBitmap)
        if (frameBitmap !== rawBitmap) rawBitmap.recycle()

        val inputImage = InputImage.fromBitmap(frameBitmap, 0)
        val latinTask = latinTextRecognizer.process(inputImage)
        val koreanTask = koreanTextRecognizer.process(inputImage)
        val chineseTask = chineseTextRecognizer.process(inputImage)
        val japaneseTask = japaneseTextRecognizer.process(inputImage)
        val devanagariTask = devanagariTextRecognizer.process(inputImage)

        Tasks.whenAllComplete(latinTask, koreanTask, chineseTask, japaneseTask, devanagariTask)
            .addOnSuccessListener {
                val bestResult = chooseBestOcrResult(
                    latinTask.resultOrNull(),
                    koreanTask.resultOrNull(),
                    chineseTask.resultOrNull(),
                    japaneseTask.resultOrNull(),
                    devanagariTask.resultOrNull()
                )
                val bestText = bestResult?.let { mergedTextFromVisionText(it) }.orEmpty()
                if (handleOcrCandidate(bestText)) {
                    isCapturingFrame = true
                    runOnUiThread {
                        documentFrameView.setLocked(true)
                        tvScanStatus.text = "Translating image..."
                    }
                    lifecycleScope.launch {
                        processFrozenBitmap(frameBitmap, lastOcrText, bestResult)
                    }
                } else {
                    frameBitmap.recycle()
                }
            }
            .addOnFailureListener {
                if (!isCapturingFrame) frameBitmap.recycle()
                runOnUiThread { tvScanStatus.text = "Scanning..." }
            }
            .addOnCompleteListener { imageProxy.close() }
            .addOnCompleteListener { isAnalyzingFrame = false }
    }

    // - Text pipeline: OCR -> Language ID -> Translation -

    private fun handleOcrCandidate(text: String): Boolean {
        val normalized = normalizeOcrText(text)
        if (normalized.length < MIN_FRAME_TEXT_FOR_CAPTURE) {
            runOnUiThread {
                documentFrameView.setScanning()
                tvScanStatus.text = "Scanning..."
            }
            return false
        }

        val similarity = similarityRatio(normalized, pendingStableText)
        if (similarity >= STABLE_TEXT_SIMILARITY || normalized.length >= LONG_TEXT_FAST_LOCK_LENGTH) {
            stableTextHits += 1
        } else {
            pendingStableText = normalized
            stableTextHits = 1
            stableLockedAtMs = 0L
        }

        runOnUiThread {
            documentFrameView.setDetecting()
            tvScanStatus.text = if (stableTextHits >= STABLE_TEXT_HITS_REQUIRED) {
                "Hold steady..."
            } else {
                "Hold steady..."
            }
        }

        if (stableTextHits < STABLE_TEXT_HITS_REQUIRED) return false
        if (stableLockedAtMs == 0L) stableLockedAtMs = System.currentTimeMillis()

        val remainingMs = AUTO_FREEZE_DELAY_MS - (System.currentTimeMillis() - stableLockedAtMs)
        if (remainingMs > 0L) {
            runOnUiThread {
                documentFrameView.setDetecting()
                tvScanStatus.text = "Scanning page..."
            }
            return false
        }

        val translationKey = "$targetCode:$normalized"
        if (translationKey == lastTranslatedKey) return false

        lastTranslatedKey = translationKey
        lastOcrText = text.trim()
        return true
    }

    private fun onTextDetected(text: String) {
        runOnUiThread {
            tvScanStatus.text = "Detecting language..."
            tvOriginalText.text = text
            cardTranslationResult.visibility = View.VISIBLE
        }

        translationJob?.cancel()
        translationJob = lifecycleScope.launch {
            delay(250)

            val sampleText = normalizeOcrText(text).take(400)
            val scriptLang = detectLanguageFromScript(sampleText)
            if (scriptLang != null) {
                detectedSrcCode = scriptLang
                updateDetectedLanguage(scriptLang)
                doTranslate(text, scriptLang)
                return@launch
            }

            languageIdentifier.identifyPossibleLanguages(sampleText)
                .addOnSuccessListener { identifiedLanguages ->
                    val best = identifiedLanguages
                        .map { it.languageTag.normalizeLangTag() to it.confidence }
                        .filter { (tag, confidence) ->
                            confidence >= LANGUAGE_CONFIDENCE_THRESHOLD &&
                                    tag != "und" &&
                                    engine.isSupported(tag)
                        }
                        .maxByOrNull { it.second }

                    val srcCode = when {
                        best != null -> best.first
                        detectedSrcCode != "und" -> detectedSrcCode
                        else -> {
                            runOnUiThread {
                                tvScanStatus.text = "Move closer for language detection"
                                tvTranslatedText.text = "Text found, but language is not clear yet."
                            }
                            return@addOnSuccessListener
                        }
                    }
                    detectedSrcCode = srcCode

                    updateDetectedLanguage(srcCode)
                    doTranslate(text, srcCode)
                }
                .addOnFailureListener {
                    runOnUiThread {
                        tvScanStatus.text = "Language detection failed"
                        tvTranslatedText.text = "Try holding the camera steady and closer to the text."
                    }
                }
        }
    }

    private fun doTranslate(text: String, srcCode: String) {
        if (srcCode == targetCode) {
            runOnUiThread {
                tvScanStatus.text = "Already in ${targetCode.uppercase(Locale.ROOT)}"
                tvTranslatedText.text = text
                lastResultShownAt = System.currentTimeMillis()
            }
            return
        }
        engine.translate(
            text = text,
            srcCode = srcCode,
            tgtCode = targetCode,
            onDownloading = {
                runOnUiThread { tvTranslatedText.text = "Downloading model..." }
            },
            onSuccess = { translated ->
                runOnUiThread {
                    tvScanStatus.text = "Translation ready"
                    tvTranslatedText.text = translated
                    lastResultShownAt = System.currentTimeMillis()
                }
            },
            onError = { err ->
                runOnUiThread {
                    tvScanStatus.text = "Translation issue"
                    tvTranslatedText.text = "Warning: $err"
                }
            }
        )
    }

    private suspend fun processFrozenBitmap(bitmap: Bitmap, seedText: String, recognizedText: Text? = null) {
        try {
            runOnUiThread { tvScanStatus.text = "Reading text..." }
            val blocks = withContext(Dispatchers.IO) {
                (recognizedText?.let { cameraBlocksFromText(it, mergeLinesInBlock = true) }
                    ?: recognizeCameraBlocks(bitmap, mergeLinesInBlock = true))
                    .mapNotNull { cleanCameraTextBlock(it) }
            }
            if (blocks.isEmpty()) {
                isCapturingFrame = false
                bitmap.recycle()
                runOnUiThread {
                    documentFrameView.setLocked(false)
                    tvScanStatus.text = "No clear text found"
                }
                return
            }

            val pageSourceLang = withContext(Dispatchers.IO) {
                resolveSourceLanguage(seedText)
            }
            val protectSystemScreen = isSystemOrPrivateScreen(seedText)
            val translatedBlocks = coroutineScope {
                val limiter = Semaphore(TRANSLATION_PARALLELISM)
                blocks.map { block ->
                    async(Dispatchers.IO) {
                        limiter.withPermit {
                            if (shouldMaskText(block.text) || protectSystemScreen && shouldProtectOnSystemScreen(block.text)) {
                                return@withPermit block.copy(translatedText = PRIVACY_MASK, sourceLang = block.sourceLang)
                            }
                            val sourceLang = block.sourceLang
                                .takeIf { it != "und" }
                                ?: pageSourceLang
                                ?: resolveSourceLanguage(block.text)
                                ?: return@withPermit null
                            val translated = if (sourceLang == targetCode || isMostlyTargetLanguage(block.text, targetCode)) {
                                block.text
                            } else {
                                translateSuspend(block.text, sourceLang, targetCode)
                            }
                            block.copy(translatedText = translated, sourceLang = sourceLang)
                        }
                    }
                }.awaitAll().filterNotNull()
            }

            if (translatedBlocks.isEmpty()) {
                isCapturingFrame = false
                bitmap.recycle()
                runOnUiThread {
                    documentFrameView.setLocked(false)
                    tvScanStatus.text = "Language not clear"
                }
                return
            }

            val processed = withContext(Dispatchers.Default) {
                renderTranslatedBitmap(bitmap, translatedBlocks)
            }
            bitmap.recycle()

            val mergedOriginal = translatedBlocks.joinToString("\n") { it.text }.ifBlank { seedText }
            val mergedTranslated = translatedBlocks.joinToString("\n") { it.translatedText }
            detectedSrcCode = translatedBlocks.firstOrNull()?.sourceLang ?: detectedSrcCode

            runOnUiThread {
                currentFrozenBitmap?.recycle()
                currentFrozenBitmap = processed
                documentVisualScroll.visibility = View.GONE
                ivDocumentTranslation.setImageDrawable(null)
                ivFrozenTranslation.setImageBitmap(processed)
                ivFrozenTranslation.visibility = View.VISIBLE
                documentFrameView.visibility = View.GONE
                cardTranslationResult.visibility = View.GONE
                tvOriginalText.text = mergedOriginal
                tvTranslatedText.text = mergedTranslated
                updateDetectedLanguage(detectedSrcCode)
                tvScanStatus.text = "Tap screen to scan again"
                isFrozenSession = true
                isVisualDocumentSession = false
                isCapturingFrame = false
                lastResultShownAt = System.currentTimeMillis()
            }
        } catch (e: Exception) {
            isCapturingFrame = false
            bitmap.recycle()
            runOnUiThread {
                documentFrameView.setLocked(false)
                tvScanStatus.text = "Try again"
                Toast.makeText(this, "Could not translate this frame", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun recognizeCameraBlocks(
        bitmap: Bitmap,
        mergeAllRecognizers: Boolean = false,
        mergeLinesInBlock: Boolean = false
    ): List<CameraTextBlock> {
        val image = InputImage.fromBitmap(bitmap, 0)
        val results = listOfNotNull(
            runCatching { Tasks.await(latinTextRecognizer.process(image)) }.getOrNull(),
            runCatching { Tasks.await(koreanTextRecognizer.process(image)) }.getOrNull(),
            runCatching { Tasks.await(chineseTextRecognizer.process(image)) }.getOrNull(),
            runCatching { Tasks.await(japaneseTextRecognizer.process(image)) }.getOrNull(),
            runCatching { Tasks.await(devanagariTextRecognizer.process(image)) }.getOrNull()
        )
        if (mergeAllRecognizers) {
            return mergeOcrBlocks(results.flatMap { cameraBlocksFromText(it, mergeLinesInBlock) })
        }
        val best = results.maxWithOrNull(
            compareBy<Text> { scriptCoverageScore(it.text) }
                .thenBy { normalizeOcrText(it.text).length }
        ) ?: return emptyList()

        return cameraBlocksFromText(best, mergeLinesInBlock)
    }

    private fun cameraBlocksFromText(visionText: Text, mergeLinesInBlock: Boolean = false): List<CameraTextBlock> {
        if (mergeLinesInBlock) {
            return visionText.textBlocks.mapNotNull { block ->
                val lines = block.lines.filter { it.boundingBox != null && cleanOcrText(it.text).isNotBlank() }
                if (lines.isEmpty()) return@mapNotNull null
                val text = cleanOcrText(lines.joinToString(" ") { it.text })
                if (text.length < MIN_TEXT_FOR_TRANSLATION || !hasTranslatableCharacter(text)) return@mapNotNull null
                val rect = unionLineBounds(lines) ?: return@mapNotNull null
                CameraTextBlock(
                    text = text,
                    translatedText = "",
                    rect = rect,
                    sourceLang = detectLanguageFromScript(text) ?: "und",
                    alignment = estimateBlockAlignment(rect, lines)
                )
            }
        }
        return visionText.textBlocks.flatMap { block -> block.lines }.mapNotNull { line ->
            val rect = line.boundingBox ?: return@mapNotNull null
            val text = cleanOcrText(line.text)
            if (text.length < MIN_TEXT_FOR_TRANSLATION || !hasTranslatableCharacter(text)) return@mapNotNull null
            CameraTextBlock(
                text = text,
                translatedText = "",
                rect = Rect(rect),
                sourceLang = detectLanguageFromScript(text) ?: "und"
            )
        }
    }

    private fun unionLineBounds(lines: List<Text.Line>): Rect? {
        val bounds = lines.mapNotNull { it.boundingBox }
        if (bounds.isEmpty()) return null
        return Rect(
            bounds.minOf { it.left },
            bounds.minOf { it.top },
            bounds.maxOf { it.right },
            bounds.maxOf { it.bottom }
        )
    }

    private fun estimateBlockAlignment(rect: Rect, lines: List<Text.Line>): Paint.Align {
        val centers = lines.mapNotNull { line ->
            line.boundingBox?.let { (it.left + it.right) / 2f }
        }
        if (centers.isEmpty()) return Paint.Align.LEFT
        val avgCenter = centers.average().toFloat()
        val rectCenter = rect.centerX().toFloat()
        return when {
            kotlin.math.abs(avgCenter - rectCenter) < rect.width() * 0.12f -> Paint.Align.CENTER
            avgCenter > rectCenter -> Paint.Align.RIGHT
            else -> Paint.Align.LEFT
        }
    }

    private fun mergeOcrBlocks(blocks: List<CameraTextBlock>): List<CameraTextBlock> {
        val merged = mutableListOf<CameraTextBlock>()
        blocks.sortedWith(compareBy<CameraTextBlock> { it.rect.top }.thenBy { it.rect.left }).forEach { candidate ->
            val duplicateIndex = merged.indexOfFirst { existing ->
                rectOverlapRatio(existing.rect, candidate.rect) > 0.62f ||
                        normalizeOcrText(existing.text).equals(normalizeOcrText(candidate.text), ignoreCase = true)
            }
            if (duplicateIndex == -1) {
                merged.add(candidate)
            } else {
                val existing = merged[duplicateIndex]
                if (scriptCoverageScore(candidate.text) > scriptCoverageScore(existing.text) ||
                    candidate.text.length > existing.text.length) {
                    merged[duplicateIndex] = candidate
                }
            }
        }
        return merged
    }

    private fun rectOverlapRatio(a: Rect, b: Rect): Float {
        val left = maxOf(a.left, b.left)
        val top = maxOf(a.top, b.top)
        val right = minOf(a.right, b.right)
        val bottom = minOf(a.bottom, b.bottom)
        if (right <= left || bottom <= top) return 0f
        val intersection = (right - left) * (bottom - top)
        val smallerArea = minOf(a.width() * a.height(), b.width() * b.height()).coerceAtLeast(1)
        return intersection / smallerArea.toFloat()
    }

    private suspend fun resolveSourceLanguage(text: String): String? {
        detectLanguageFromScript(text)?.let { return it }
        val sample = normalizeOcrText(text).take(400)
        val possible = Tasks.await(languageIdentifier.identifyPossibleLanguages(sample))
        val best = possible
            .map { it.languageTag.normalizeLangTag() to it.confidence }
            .filter { (tag, confidence) ->
                confidence >= LANGUAGE_CONFIDENCE_THRESHOLD && tag != "und" && engine.isSupported(tag)
            }
            .maxByOrNull { it.second }
        return best?.first ?: detectedSrcCode.takeIf { it != "und" }
    }

    private suspend fun translateSuspend(text: String, sourceLang: String, targetLang: String): String =
        suspendCoroutine { continuation ->
            engine.translate(
                text = text,
                srcCode = sourceLang,
                tgtCode = targetLang,
                onDownloading = {
                    runOnUiThread { tvScanStatus.text = "Downloading model..." }
                },
                onSuccess = { translated ->
                    continuation.resume(translated)
                },
                onError = {
                    continuation.resume(text)
                }
            )
        }

    private fun prewarmCameraTranslationModel() {
        val source = DEFAULT_CAMERA_SOURCE_LANG
        val target = targetCode
        if (source == target || !engine.isSupported(source) || !engine.isSupported(target)) return
        engine.translate(
            text = MODEL_PREWARM_TEXT,
            srcCode = source,
            tgtCode = target,
            onDownloading = {},
            onSuccess = {},
            onError = {}
        )
    }

    private fun initTextToSpeech() {
        textToSpeech = TextToSpeech(this) { status ->
            isTtsReady = status == TextToSpeech.SUCCESS
            if (isTtsReady) {
                textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        if (utteranceId?.startsWith(TTS_UTTERANCE_PREFIX) == true) {
                            runOnUiThread { setSpeakerActive(true) }
                        }
                    }

                    override fun onDone(utteranceId: String?) {
                        onTtsPartFinished(utteranceId)
                    }

                    @Deprecated("Deprecated in Java")
                    override fun onError(utteranceId: String?) {
                        onTtsPartFinished(utteranceId)
                    }

                    override fun onError(utteranceId: String?, errorCode: Int) {
                        onTtsPartFinished(utteranceId)
                    }

                    override fun onStop(utteranceId: String?, interrupted: Boolean) {
                        if (utteranceId?.startsWith(TTS_UTTERANCE_PREFIX) == true) {
                            runOnUiThread {
                                pendingSpeechParts = 0
                                setSpeakerActive(false)
                            }
                        }
                    }
                })
                configureTextToSpeechLanguage()
            }
        }
    }

    private fun configureTextToSpeechLanguage(): Boolean {
        val tts = textToSpeech ?: return false
        if (!isTtsReady) return false
        val locale = localeForLanguageCode(targetCode)
        val availability = tts.isLanguageAvailable(locale)
        if (availability == TextToSpeech.LANG_MISSING_DATA || availability == TextToSpeech.LANG_NOT_SUPPORTED) {
            return false
        }

        tts.language = locale
        tts.setSpeechRate(0.86f)
        tts.setPitch(1.0f)
        selectBestLocalVoice(tts, locale)?.let { tts.voice = it }
        return true
    }

    private fun speakTranslatedText() {
        val text = tvTranslatedText.text?.toString()?.trim().orEmpty()
        if (text.isBlank() || text == "Translation appears here..." || text.startsWith("Warning:")) {
            Toast.makeText(this, "No translated text to read yet", Toast.LENGTH_SHORT).show()
            return
        }
        val tts = textToSpeech
        if (tts == null || !isTtsReady) {
            Toast.makeText(this, "Speaker is still getting ready", Toast.LENGTH_SHORT).show()
            return
        }
        if (!configureTextToSpeechLanguage()) {
            Toast.makeText(this, "Install ${targetCode.uppercase()} voice data in Android Text-to-Speech settings", Toast.LENGTH_LONG).show()
            runCatching {
                startActivity(Intent(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA))
            }
            return
        }

        if (isReadingTranslation || tts.isSpeaking) {
            tts.stop()
            pendingSpeechParts = 0
            setSpeakerActive(false)
            return
        }

        tts.stop()
        speakWithNaturalPauses(tts, prepareTextForSpeech(text.take(MAX_TTS_CHARS)))
    }

    private fun speakWithNaturalPauses(tts: TextToSpeech, text: String) {
        val chunks = splitSpeechChunks(text)
        if (chunks.isEmpty()) return
        var acceptedParts = 0
        chunks.forEachIndexed { index, chunk ->
            val queueMode = if (index == 0) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD
            val utteranceId = "$TTS_UTTERANCE_PREFIX${System.currentTimeMillis()}_$index"
            val result = tts.speak(chunk, queueMode, null, utteranceId)
            if (result == TextToSpeech.SUCCESS) {
                acceptedParts += 1
            }
            val pauseMs = pauseAfterChunk(chunk)
            if (result == TextToSpeech.SUCCESS && pauseMs > 0 && index < chunks.lastIndex) {
                tts.playSilentUtterance(pauseMs, TextToSpeech.QUEUE_ADD, "camera_pause_${System.currentTimeMillis()}_$index")
            }
        }
        pendingSpeechParts = acceptedParts
        if (acceptedParts > 0) {
            setSpeakerActive(true)
        } else {
            setSpeakerActive(false)
            Toast.makeText(this, "Could not start speaker. Check Android Text-to-Speech engine.", Toast.LENGTH_LONG).show()
        }
    }

    private fun onTtsPartFinished(utteranceId: String?) {
        if (utteranceId?.startsWith(TTS_UTTERANCE_PREFIX) != true) return
        runOnUiThread {
            pendingSpeechParts = (pendingSpeechParts - 1).coerceAtLeast(0)
            if (pendingSpeechParts == 0) {
                setSpeakerActive(false)
            }
        }
    }

    private fun setSpeakerActive(active: Boolean) {
        isReadingTranslation = active
        val button = findViewById<View>(R.id.btnSpeakTranslation)
        val icon = findViewById<ImageView>(R.id.ivSpeakIcon)
        val color = if (active) {
            ContextCompat.getColor(this, R.color.accent_cyan)
        } else {
            Color.WHITE
        }
        icon.setColorFilter(color)
        button.animate()
            .scaleX(if (active) 1.16f else 1f)
            .scaleY(if (active) 1.16f else 1f)
            .alpha(if (active) 1f else 0.92f)
            .setDuration(160L)
            .start()
    }

    private fun animateSpeakerTap() {
        val button = findViewById<View>(R.id.btnSpeakTranslation)
        val icon = findViewById<ImageView>(R.id.ivSpeakIcon)
        if (!isReadingTranslation) {
            icon.setColorFilter(ContextCompat.getColor(this, R.color.accent_cyan))
            icon.postDelayed({
                if (!isReadingTranslation) icon.setColorFilter(Color.WHITE)
            }, 450L)
        }
        button.animate()
            .scaleX(0.9f)
            .scaleY(0.9f)
            .setDuration(70L)
            .withEndAction {
                button.animate()
                    .scaleX(if (isReadingTranslation) 1.16f else 1f)
                    .scaleY(if (isReadingTranslation) 1.16f else 1f)
                    .setDuration(120L)
                    .start()
            }
            .start()
    }

    private fun prepareTextForSpeech(text: String): String =
        text.replace(Regex("\\s+"), " ")
            .replace("...", ". ")
            .replace("…", ". ")
            .replace(Regex("\\s+([,.!?;:])"), "$1")
            .trim()

    private fun splitSpeechChunks(text: String): List<String> {
        val parts = Regex("[^.!?;:]+[.!?;:]*")
            .findAll(text)
            .map { it.value.trim() }
            .filter { it.isNotBlank() }
            .toList()
        if (parts.isEmpty()) return text.takeIf { it.isNotBlank() }?.let { listOf(it) }.orEmpty()

        val chunks = mutableListOf<String>()
        parts.forEach { part ->
            if (part.length <= MAX_TTS_CHUNK_CHARS) {
                chunks.add(part)
            } else {
                part.chunked(MAX_TTS_CHUNK_CHARS).mapTo(chunks) { it.trim() }
            }
        }
        return chunks.filter { it.isNotBlank() }
    }

    private fun pauseAfterChunk(chunk: String): Long =
        when {
            chunk.endsWith("?") -> 520L
            chunk.endsWith("!") -> 480L
            chunk.endsWith(".") -> 420L
            chunk.endsWith(";") || chunk.endsWith(":") -> 320L
            chunk.endsWith(",") -> 220L
            else -> 160L
        }

    private fun selectBestLocalVoice(tts: TextToSpeech, locale: Locale): Voice? =
        tts.voices
            ?.asSequence()
            ?.filter { it.locale.language == locale.language }
            ?.filter { !it.isNetworkConnectionRequired }
            ?.sortedWith(
                compareByDescending<Voice> { it.quality }
                    .thenBy { it.latency }
                    .thenBy { it.name }
            )
            ?.firstOrNull()

    private fun localeForLanguageCode(code: String): Locale =
        when (code.lowercase(Locale.ROOT)) {
            "zh" -> Locale.CHINESE
            "ja" -> Locale.JAPANESE
            "ko" -> Locale.KOREAN
            "en" -> Locale.ENGLISH
            "fr" -> Locale.FRENCH
            "de" -> Locale.GERMAN
            "it" -> Locale.ITALIAN
            "es" -> Locale("es")
            "hi" -> Locale("hi")
            "ar" -> Locale("ar")
            "pt" -> Locale("pt")
            "ru" -> Locale("ru")
            else -> Locale.forLanguageTag(code)
        }

    private fun renderTranslatedBitmap(source: Bitmap, blocks: List<CameraTextBlock>): Bitmap {
        val result = source.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(result)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            isSubpixelText = true
            typeface = android.graphics.Typeface.DEFAULT_BOLD
        }

        blocks.forEach { block ->
            if (!isDrawableBlock(source, block)) return@forEach
            val bgColor = sampleBackground(source, block.rect)
            val pad = if (block.translatedText == PRIVACY_MASK) {
                maxOf(5f, block.rect.height() * 0.18f)
            } else {
                maxOf(2f, block.rect.height() * 0.08f)
            }
            paint.style = Paint.Style.FILL
            paint.color = bgColor
            canvas.drawRect(
                block.rect.left - pad,
                block.rect.top - pad,
                block.rect.right + pad,
                block.rect.bottom + pad,
                paint
            )

            val textColor = readableTextColor(bgColor)
            val fitted = fitCameraText(block.translatedText, block.rect)
            paint.textSize = fitted.textSize
            paint.textAlign = block.alignment
            val x = when (block.alignment) {
                Paint.Align.RIGHT -> block.rect.right - 4f
                Paint.Align.CENTER -> block.rect.centerX().toFloat()
                Paint.Align.LEFT -> block.rect.left + 4f
            }
            val fm = paint.fontMetrics
            val lineHeight = fm.descent - fm.ascent
            val totalHeight = lineHeight * fitted.lines.size
            var y = block.rect.top + (block.rect.height() - totalHeight) / 2f - fm.ascent

            fitted.lines.forEach { line ->
                paint.textSize = fitted.textSize
                paint.textAlign = block.alignment
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = (fitted.textSize * 0.12f).coerceIn(1.5f, 4f)
                paint.color = if (textColor == Color.WHITE) Color.BLACK else Color.WHITE
                canvas.drawText(line, x, y, paint)
                paint.style = Paint.Style.FILL
                paint.strokeWidth = 0f
                paint.color = textColor
                canvas.drawText(line, x, y, paint)
                y += lineHeight
            }
        }
        return result
    }

    private fun renderVisualTranslatedBitmap(source: Bitmap, blocks: List<CameraTextBlock>): Bitmap {
        val result = source.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(result)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            isSubpixelText = true
            typeface = android.graphics.Typeface.DEFAULT
        }

        blocks.sortedBy { it.rect.top }.forEach { block ->
            if (!isDrawableBlock(source, block)) return@forEach
            val drawRect = visualDrawRect(source, block)
            val bgColor = sampleBackground(source, block.rect)
            paint.style = Paint.Style.FILL
            paint.color = bgColor
            canvas.drawRect(drawRect, paint)

            paint.color = readableTextColor(bgColor)
            paint.textAlign = block.alignment
            val fitted = fitVisualText(block.translatedText, drawRect, block.rect.height() * 0.86f)
            paint.textSize = fitted.textSize
            val x = when (block.alignment) {
                Paint.Align.RIGHT -> drawRect.right - VISUAL_TEXT_PADDING
                Paint.Align.CENTER -> drawRect.centerX()
                Paint.Align.LEFT -> drawRect.left + VISUAL_TEXT_PADDING
            }
            val fm = paint.fontMetrics
            val lineHeight = fm.descent - fm.ascent
            val totalHeight = lineHeight * fitted.lines.size
            var y = drawRect.top + (drawRect.height() - totalHeight) / 2f - fm.ascent
            canvas.save()
            canvas.clipRect(drawRect)
            fitted.lines.forEach { line ->
                canvas.drawText(line, x, y, paint)
                y += lineHeight
            }
            canvas.restore()
        }
        return result
    }

    private fun visualDrawRect(source: Bitmap, block: CameraTextBlock): android.graphics.RectF {
        val rowPadX = maxOf(4f, block.rect.height() * 0.22f)
        val rowPadY = maxOf(2f, block.rect.height() * 0.14f)
        val pageW = source.width.toFloat()
        val leftBoundary = pageW * 0.08f
        val middleLeft = pageW * 0.40f
        val middleRight = pageW * 0.56f
        val rightBoundary = pageW * 0.94f
        val center = block.rect.centerX() / pageW
        val naturalWidth = block.rect.width() + rowPadX * 2f
        val maxColumnWidth = pageW * 0.36f

        val left = when {
            block.alignment == Paint.Align.RIGHT -> maxOf(middleRight, block.rect.right - maxColumnWidth)
            block.alignment == Paint.Align.CENTER -> (block.rect.left - rowPadX).coerceAtLeast(leftBoundary)
            center < 0.42f -> (block.rect.left - rowPadX).coerceAtLeast(leftBoundary)
            else -> maxOf(middleLeft, block.rect.left - rowPadX)
        }
        val right = when {
            block.alignment == Paint.Align.RIGHT -> minOf(rightBoundary, block.rect.right + rowPadX)
            block.alignment == Paint.Align.CENTER -> (block.rect.right + rowPadX).coerceAtMost(rightBoundary)
            center < 0.42f -> minOf(middleLeft, left + minOf(maxColumnWidth, naturalWidth + pageW * 0.08f))
            else -> minOf(rightBoundary, left + minOf(maxColumnWidth, naturalWidth + pageW * 0.08f))
        }

        return android.graphics.RectF(
            left.coerceIn(0f, pageW - 1f),
            (block.rect.top - rowPadY).coerceAtLeast(0f),
            right.coerceIn(left + 1f, pageW),
            (block.rect.bottom + rowPadY).coerceAtMost(source.height.toFloat())
        )
    }

    private fun fitVisualText(text: String, rect: android.graphics.RectF, startSize: Float): FittedCameraText {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val maxWidth = (rect.width() - VISUAL_TEXT_PADDING * 2f).coerceAtLeast(1f)
        val maxHeight = rect.height().coerceAtLeast(1f)
        var size = startSize.coerceIn(8f, 28f)
        while (size > 6f) {
            paint.textSize = size
            val lines = wrapCameraText(text, paint, maxWidth)
            val fm = paint.fontMetrics
            val totalHeight = (fm.descent - fm.ascent) * lines.size
            if (lines.size <= 2 && totalHeight <= maxHeight + 4f) {
                return FittedCameraText(lines, size)
            }
            size -= 0.5f
        }
        paint.textSize = 6f
        return FittedCameraText(wrapCameraText(text, paint, maxWidth).take(2), 6f)
    }

    private fun fitCameraText(text: String, rect: Rect): FittedCameraText {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val maxWidth = (rect.width() - 8f).coerceAtLeast(1f)
        val maxHeight = rect.height().toFloat().coerceAtLeast(1f)
        var size = (rect.height() * 0.72f).coerceIn(7f, 30f)
        while (size > 6f) {
            paint.textSize = size
            val lines = wrapCameraText(text, paint, maxWidth)
            val fm = paint.fontMetrics
            val totalHeight = (fm.descent - fm.ascent) * lines.size
            if (lines.size <= 3 && totalHeight <= maxHeight + 6f) {
                return FittedCameraText(lines, size)
            }
            size -= 0.5f
        }
        paint.textSize = 6f
        return FittedCameraText(wrapCameraText(text, paint, maxWidth).take(3), 6f)
    }

    private fun wrapCameraText(text: String, paint: Paint, maxWidth: Float): List<String> {
        if (paint.measureText(text) <= maxWidth) return listOf(text)
        val words = text.split(Regex("\\s+")).filter { it.isNotBlank() }
        if (words.size <= 1) return listOf(text)
        val lines = mutableListOf<String>()
        var current = ""
        words.forEach { word ->
            val candidate = if (current.isBlank()) word else "$current $word"
            if (paint.measureText(candidate) <= maxWidth || current.isBlank()) {
                current = candidate
            } else {
                lines.add(current)
                current = word
            }
        }
        if (current.isNotBlank()) lines.add(current)
        return lines
    }

    private fun sampleBackground(bitmap: Bitmap, rect: Rect): Int {
        val samples = mutableListOf<Int>()
        val step = maxOf(1, rect.width() / 8)
        val y1 = (rect.top - 4).coerceIn(0, bitmap.height - 1)
        val y2 = (rect.bottom + 4).coerceIn(0, bitmap.height - 1)
        var x = rect.left
        while (x <= rect.right) {
            val sx = x.coerceIn(0, bitmap.width - 1)
            samples.add(bitmap.getPixel(sx, y1))
            samples.add(bitmap.getPixel(sx, y2))
            x += step
        }
        return samples.groupBy { it }.maxByOrNull { it.value.size }?.key ?: Color.WHITE
    }

    private fun readableTextColor(bgColor: Int): Int {
        val luminance = (0.299f * Color.red(bgColor) +
                0.587f * Color.green(bgColor) +
                0.114f * Color.blue(bgColor)) / 255f
        return if (luminance > 0.52f) Color.BLACK else Color.WHITE
    }

    private fun resetLiveScanSession() {
        ivFrozenTranslation.visibility = View.GONE
        ivFrozenTranslation.setImageDrawable(null)
        documentVisualScroll.visibility = View.GONE
        ivDocumentTranslation.setImageDrawable(null)
        documentFrameView.visibility = View.VISIBLE
        documentFrameView.setScanning()
        currentFrozenBitmap?.recycle()
        currentFrozenBitmap = null
        isFrozenSession = false
        isVisualDocumentSession = false
        isCapturingFrame = false
        pendingStableText = ""
        stableTextHits = 0
        stableLockedAtMs = 0L
        lastTranslatedKey = ""
        tvScanStatus.text = "Scanning..."
    }

    private fun imageProxyToBitmap(image: ImageProxy): Bitmap? {
        val decoded = if (image.format == ImageFormat.JPEG) {
            val buffer = image.planes.firstOrNull()?.buffer ?: return null
            val bytes = ByteArray(buffer.remaining())
            buffer.get(bytes)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } else {
            val nv21 = yuv420ToNv21(image)
            val yuvImage = YuvImage(nv21, ImageFormat.NV21, image.width, image.height, null)
            val out = ByteArrayOutputStream()
            yuvImage.compressToJpeg(Rect(0, 0, image.width, image.height), 92, out)
            val bytes = out.toByteArray()
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } ?: return null

        val rotation = image.imageInfo.rotationDegrees
        if (rotation == 0) return decoded
        val matrix = Matrix().apply { postRotate(rotation.toFloat()) }
        val rotated = Bitmap.createBitmap(decoded, 0, 0, decoded.width, decoded.height, matrix, true)
        if (rotated !== decoded) decoded.recycle()
        return rotated
    }

    private fun cropToDocumentFrame(bitmap: Bitmap): Bitmap {
        if (bitmap.width < 40 || bitmap.height < 40) return bitmap
        val viewWidth = previewView.width.takeIf { it > 0 } ?: bitmap.width
        val viewHeight = previewView.height.takeIf { it > 0 } ?: bitmap.height

        val scale = maxOf(
            viewWidth / bitmap.width.toFloat(),
            viewHeight / bitmap.height.toFloat()
        )
        val displayedWidth = bitmap.width * scale
        val displayedHeight = bitmap.height * scale
        val offsetX = (viewWidth - displayedWidth) / 2f
        val offsetY = (viewHeight - displayedHeight) / 2f

        val frameLeft = viewWidth * DOCUMENT_FRAME_LEFT_RATIO
        val frameTop = viewHeight * DOCUMENT_FRAME_TOP_RATIO
        val frameRight = viewWidth * DOCUMENT_FRAME_RIGHT_RATIO
        val frameBottom = viewHeight * DOCUMENT_FRAME_BOTTOM_RATIO

        val left = ((frameLeft - offsetX) / scale).toInt().coerceIn(0, bitmap.width - 2)
        val top = ((frameTop - offsetY) / scale).toInt().coerceIn(0, bitmap.height - 2)
        val right = ((frameRight - offsetX) / scale).toInt().coerceIn(left + 1, bitmap.width)
        val bottom = ((frameBottom - offsetY) / scale).toInt().coerceIn(top + 1, bitmap.height)
        return Bitmap.createBitmap(bitmap, left, top, right - left, bottom - top)
    }

    private fun yuv420ToNv21(image: ImageProxy): ByteArray {
        val yPlane = image.planes[0]
        val uPlane = image.planes[1]
        val vPlane = image.planes[2]
        val width = image.width
        val height = image.height
        val nv21 = ByteArray(width * height * 3 / 2)

        var outputOffset = 0
        val yBuffer = yPlane.buffer
        for (row in 0 until height) {
            val rowStart = row * yPlane.rowStride
            yBuffer.position(rowStart)
            yBuffer.get(nv21, outputOffset, width)
            outputOffset += width
        }

        val chromaHeight = height / 2
        val chromaWidth = width / 2
        val uBuffer = uPlane.buffer
        val vBuffer = vPlane.buffer
        for (row in 0 until chromaHeight) {
            for (col in 0 until chromaWidth) {
                val vuIndex = row * vPlane.rowStride + col * vPlane.pixelStride
                val uuIndex = row * uPlane.rowStride + col * uPlane.pixelStride
                nv21[outputOffset++] = vBuffer.get(vuIndex)
                nv21[outputOffset++] = uBuffer.get(uuIndex)
            }
        }
        return nv21
    }

    // - File / Gallery processing -

    private fun processUri(uri: Uri, mimeType: String) {
        when {
            mimeType.startsWith("image/") -> {
                processVisualUri(uri, mimeType)
                return
            }
            mimeType == "application/pdf" -> {
                processVisualUri(uri, mimeType)
                return
            }
        }

        if (isFrozenSession) resetLiveScanSession()
        tvScanStatus.text = "Processing file..."
        documentFrameView.visibility = View.GONE
        cardTranslationResult.visibility = View.VISIBLE
        tvOriginalText.text = "Reading file..."
        tvTranslatedText.text = "Please wait..."

        lifecycleScope.launch {
            try {
                val extracted = fileExtractor.extract(uri, mimeType)
                if (extracted.isBlank()) {
                    Toast.makeText(this@CameraActivity, "No text found in file", Toast.LENGTH_SHORT).show()
                    return@launch
                }
                lastOcrText = extracted
                onTextDetected(extracted)
            } catch (e: UnsupportedOperationException) {
                Toast.makeText(this@CameraActivity, e.message, Toast.LENGTH_LONG).show()
                cardTranslationResult.visibility = View.GONE
                documentFrameView.visibility = View.VISIBLE
            } catch (e: Exception) {
                Toast.makeText(
                    this@CameraActivity,
                    "Error reading file: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
                cardTranslationResult.visibility = View.GONE
                documentFrameView.visibility = View.VISIBLE
            }
        }
    }

    private fun processVisualUri(uri: Uri, mimeType: String) {
        if (isFrozenSession) resetLiveScanSession()
        tvScanStatus.text = if (mimeType == "application/pdf") "Rendering PDF page..." else "Reading image..."
        documentFrameView.visibility = View.GONE
        showVisualProcessingState(if (mimeType == "application/pdf") "Rendering selected PDF page..." else "Preparing selected image...")
        ivFrozenTranslation.visibility = View.GONE
        documentVisualScroll.visibility = View.GONE
        lifecycleScope.launch {
            var sourceBitmap: Bitmap? = null
            try {
                sourceBitmap = withContext(Dispatchers.IO) {
                    if (mimeType == "application/pdf") {
                        renderPdfPagesAsLongBitmap(uri)
                    } else {
                        decodeBitmapFromUri(uri)
                    }
                }
                showVisualProcessingState("Translating selected ${if (mimeType == "application/pdf") "PDF page" else "image"}...")
                processVisualBitmap(sourceBitmap, if (mimeType == "application/pdf") "PDF page" else "image")
            } catch (e: Exception) {
                sourceBitmap?.recycle()
                Toast.makeText(
                    this@CameraActivity,
                    "Could not translate visual file: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
                tvScanStatus.text = "Try another file"
                documentFrameView.visibility = View.VISIBLE
            }
        }
    }

    private suspend fun processVisualBitmap(bitmap: Bitmap, sourceLabel: String) {
        try {
            showVisualProcessingState("Reading $sourceLabel...")
            tvScanStatus.text = "Reading $sourceLabel..."
            val blocks = withContext(Dispatchers.IO) {
                val useLayoutLines = isStructuredDocumentSource(sourceLabel)
                recognizeCameraBlocks(
                    bitmap,
                    mergeAllRecognizers = true,
                    mergeLinesInBlock = !useLayoutLines
                )
                    .mapNotNull { cleanCameraTextBlock(it) }
                    .filter { shouldTranslateVisualText(it.text) }
                    .map { it.copy(alignment = estimateVisualAlignment(bitmap, it.rect)) }
            }
            if (blocks.isEmpty()) {
                bitmap.recycle()
                tvScanStatus.text = "No clear text found"
                return
            }

            tvScanStatus.text = "Translating $sourceLabel..."
            val seedText = blocks.joinToString("\n") { it.text }
            val pageSourceLang = withContext(Dispatchers.IO) { resolveSourceLanguage(seedText) }
            val protectSystemScreen = isSystemOrPrivateScreen(seedText)
            val translatedBlocks = coroutineScope {
                val limiter = Semaphore(TRANSLATION_PARALLELISM)
                blocks.map { block ->
                    async(Dispatchers.IO) {
                        limiter.withPermit {
                            if (shouldMaskText(block.text) || protectSystemScreen && shouldProtectOnSystemScreen(block.text)) {
                                return@withPermit block.copy(translatedText = PRIVACY_MASK, sourceLang = block.sourceLang)
                            }
                            val sourceLang = block.sourceLang
                                .takeIf { it != "und" }
                                ?: pageSourceLang
                                ?: resolveSourceLanguage(block.text)
                                ?: return@withPermit null
                            val translated = if (sourceLang == targetCode || isMostlyTargetLanguage(block.text, targetCode)) {
                                block.text
                            } else {
                                translateSuspend(block.text, sourceLang, targetCode)
                            }
                            block.copy(translatedText = translated, sourceLang = sourceLang)
                        }
                    }
                }.awaitAll().filterNotNull()
            }
            if (translatedBlocks.isEmpty()) {
                bitmap.recycle()
                tvScanStatus.text = "Language not clear"
                return
            }

            val processed = withContext(Dispatchers.Default) {
                renderVisualTranslatedBitmap(bitmap, translatedBlocks)
            }
            bitmap.recycle()

            currentFrozenBitmap?.recycle()
            currentFrozenBitmap = processed
            ivDocumentTranslation.setImageBitmap(processed)
            documentVisualScroll.visibility = View.VISIBLE
            documentVisualScroll.scrollTo(0, 0)
            documentVisualScroll.bringToFront()
            ivFrozenTranslation.visibility = View.GONE
            findViewById<View>(R.id.topBarCamera).bringToFront()
            findViewById<View>(R.id.bottomActionBar).bringToFront()
            documentFrameView.visibility = View.GONE
            cardTranslationResult.visibility = View.GONE
            isFrozenSession = true
            isVisualDocumentSession = true
            val mergedOriginal = translatedBlocks.joinToString("\n") { it.text }
            val mergedTranslated = translatedBlocks.joinToString("\n") { it.translatedText }
            tvOriginalText.text = mergedOriginal
            tvTranslatedText.text = mergedTranslated
            detectedSrcCode = translatedBlocks.firstOrNull()?.sourceLang ?: detectedSrcCode
            updateDetectedLanguage(detectedSrcCode)
            tvScanStatus.text = "Translated page ready"
        } catch (e: Exception) {
            bitmap.recycle()
            tvScanStatus.text = "Try again"
            Toast.makeText(this, "Could not translate this visual", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showVisualProcessingState(message: String) {
        tvScanStatus.text = message
        cardTranslationResult.visibility = View.VISIBLE
        tvOriginalLabel.text = "DOCUMENT"
        tvTranslatedLabel.text = "TRANSLATING"
        tvOriginalText.text = "Selected document received."
        tvTranslatedText.text = message
    }

    private fun isStructuredDocumentSource(sourceLabel: String): Boolean =
        sourceLabel.contains("pdf", ignoreCase = true)

    private fun shouldTranslateVisualText(text: String): Boolean {
        val normalized = cleanOcrText(text)
        if (normalized.isBlank()) return false
        if (isAppControlText(normalized) || isNoisyOcrText(normalized)) return false
        if (shouldMaskText(normalized)) return true
        if (!normalized.any { it.isLetter() || it in '\uAC00'..'\uD7AF' || it in '\u3040'..'\u30FF' || it in '\u4E00'..'\u9FFF' }) {
            return false
        }
        val letters = normalized.count { it.isLetter() || it in '\uAC00'..'\uD7AF' || it in '\u3040'..'\u30FF' || it in '\u4E00'..'\u9FFF' }
        val digits = normalized.count { it.isDigit() }
        return letters >= 2 || digits == 0
    }

    private fun estimateVisualAlignment(bitmap: Bitmap, rect: Rect): Paint.Align {
        val center = rect.centerX() / bitmap.width.toFloat()
        return when {
            center > 0.58f -> Paint.Align.RIGHT
            center in 0.42f..0.58f -> Paint.Align.CENTER
            else -> Paint.Align.LEFT
        }
    }

    private fun cleanCameraTextBlock(block: CameraTextBlock): CameraTextBlock? {
        val cleaned = cleanOcrText(block.text)
        if (cleaned.isBlank()) return null
        if (isAppControlText(cleaned) || isNoisyOcrText(cleaned)) return null
        return block.copy(text = cleaned)
    }

    private fun cleanOcrText(text: String): String =
        text.replace(Regex("[\\u0000-\\u001F]+"), " ")
            .replace(Regex("\\s+"), " ")
            .replace(Regex("([A-Za-z])\\1{5,}"), "$1$1")
            .trim(' ', '-', '_', '|', '/', '\\')

    private fun isNoisyOcrText(text: String): Boolean {
        val normalized = text.trim()
        if (normalized.length <= 1) return true
        if (Regex("^[\\W_]+$").matches(normalized)) return true
        if (Regex(".*([A-Za-z])\\1{5,}.*").matches(normalized)) return true
        val letters = normalized.count { it.isLetter() || it in '\uAC00'..'\uD7AF' || it in '\u3040'..'\u30FF' || it in '\u4E00'..'\u9FFF' }
        val symbols = normalized.count { !it.isLetterOrDigit() && !it.isWhitespace() }
        if (normalized.length >= 6 && letters == 0) return true
        if (symbols > letters + normalized.count { it.isDigit() } && normalized.length > 5) return true
        return false
    }

    private fun isAppControlText(text: String): Boolean {
        val lower = text.lowercase(Locale.ROOT)
        return CAMERA_UI_SKIP_WORDS.any { lower.contains(it) }
    }

    private fun shouldMaskText(text: String): Boolean {
        val normalized = text.trim()
        if (Regex("[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}", RegexOption.IGNORE_CASE).containsMatchIn(normalized)) return true
        if (Regex("\\b\\d[\\d\\s-]{5,}\\d\\b").containsMatchIn(normalized)) return true
        if (Regex("\\b(otp|password|passcode|pin|token|verification code|security code|account number|bank code|card number|cvv)\\b", RegexOption.IGNORE_CASE).containsMatchIn(normalized)) return true
        return false
    }

    private fun isSystemOrPrivateScreen(text: String): Boolean {
        val lower = text.lowercase(Locale.ROOT)
        val hits = SYSTEM_PRIVATE_HINTS.count { lower.contains(it) }
        return hits >= 2
    }

    private fun shouldProtectOnSystemScreen(text: String): Boolean {
        if (shouldMaskText(text)) return true
        val lower = text.lowercase(Locale.ROOT)
        return SYSTEM_PRIVATE_HINTS.any { lower.contains(it) } ||
                Regex("\\b\\d+\\s*(mb|gb|kb|%)\\b", RegexOption.IGNORE_CASE).containsMatchIn(text)
    }

    private fun isMostlyTargetLanguage(text: String, target: String): Boolean {
        val letters = text.count { it.isLetter() }.coerceAtLeast(1)
        return when (target.lowercase(Locale.ROOT)) {
            "en" -> text.count { it in 'A'..'Z' || it in 'a'..'z' } / letters.toFloat() > 0.78f
            "ko" -> text.count { it in '\uAC00'..'\uD7AF' || it in '\u1100'..'\u11FF' } / letters.toFloat() > 0.55f
            "ja" -> text.count { it in '\u3040'..'\u30FF' } / letters.toFloat() > 0.45f
            "zh" -> text.count { it in '\u4E00'..'\u9FFF' } / letters.toFloat() > 0.55f
            else -> false
        }
    }

    private fun isDrawableBlock(source: Bitmap, block: CameraTextBlock): Boolean {
        if (block.rect.width() <= 2 || block.rect.height() <= 2) return false
        val area = block.rect.width() * block.rect.height()
        val screenArea = source.width * source.height
        if (area > screenArea * 0.18f) return false
        if (block.rect.width() > source.width * 0.92f && block.rect.height() > source.height * 0.05f) return false
        return true
    }

    private fun decodeBitmapFromUri(uri: Uri): Bitmap {
        val original = contentResolver.openInputStream(uri)?.use { input ->
            BitmapFactory.decodeStream(input)
        } ?: throw IllegalStateException("Cannot decode image")
        return scaleBitmapForVisualProcessing(original)
    }

    private fun renderFirstPdfPage(uri: Uri): Bitmap {
        val fd = contentResolver.openFileDescriptor(uri, "r")
            ?: throw IllegalStateException("Cannot open PDF")
        val renderer = PdfRenderer(fd)
        try {
            if (renderer.pageCount == 0) throw IllegalStateException("PDF has no pages")
            val page = renderer.openPage(0)
            try {
                val scale = minOf(3f, maxOf(1f, VISUAL_MAX_BITMAP_EDGE / maxOf(page.width, page.height).toFloat()))
                val width = (page.width * scale).toInt().coerceAtLeast(1)
                val height = (page.height * scale).toInt().coerceAtLeast(1)
                val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                Canvas(bitmap).drawColor(Color.WHITE)
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                return bitmap
            } finally {
                page.close()
            }
        } finally {
            renderer.close()
            fd.close()
        }
    }

    private fun renderPdfPagesAsLongBitmap(uri: Uri): Bitmap {
        val fd = contentResolver.openFileDescriptor(uri, "r")
            ?: throw IllegalStateException("Cannot open PDF")
        val renderer = PdfRenderer(fd)
        val renderedPages = mutableListOf<Bitmap>()
        try {
            if (renderer.pageCount == 0) throw IllegalStateException("PDF has no pages")
            val pageCount = minOf(renderer.pageCount, MAX_VISUAL_PDF_PAGES)
            for (i in 0 until pageCount) {
                renderer.openPage(i).usePage { page ->
                    val scale = minOf(2.2f, maxOf(1f, VISUAL_MAX_BITMAP_EDGE / maxOf(page.width, page.height).toFloat()))
                    val width = (page.width * scale).toInt().coerceAtLeast(1)
                    val height = (page.height * scale).toInt().coerceAtLeast(1)
                    val pageBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                    Canvas(pageBitmap).drawColor(Color.WHITE)
                    page.render(pageBitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                    renderedPages.add(pageBitmap)
                }
            }
            return combinePdfPages(renderedPages)
        } finally {
            renderer.close()
            fd.close()
        }
    }

    private inline fun PdfRenderer.Page.usePage(block: (PdfRenderer.Page) -> Unit) {
        try {
            block(this)
        } finally {
            close()
        }
    }

    private fun combinePdfPages(pages: List<Bitmap>): Bitmap {
        if (pages.isEmpty()) throw IllegalStateException("No PDF pages rendered")
        if (pages.size == 1) return pages.first()
        val width = pages.maxOf { it.width }
        val gap = (width * 0.035f).toInt().coerceAtLeast(24)
        val height = pages.sumOf { it.height } + gap * (pages.size - 1)
        val combined = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(combined)
        canvas.drawColor(Color.WHITE)
        var y = 0
        pages.forEachIndexed { index, page ->
            val left = (width - page.width) / 2f
            canvas.drawBitmap(page, left, y.toFloat(), null)
            y += page.height
            if (index < pages.lastIndex) y += gap
            page.recycle()
        }
        return combined
    }

    private fun scaleBitmapForVisualProcessing(bitmap: Bitmap): Bitmap {
        val maxEdge = maxOf(bitmap.width, bitmap.height)
        if (maxEdge <= VISUAL_MAX_BITMAP_EDGE) return bitmap
        val scale = VISUAL_MAX_BITMAP_EDGE / maxEdge.toFloat()
        val scaled = Bitmap.createScaledBitmap(
            bitmap,
            (bitmap.width * scale).toInt().coerceAtLeast(1),
            (bitmap.height * scale).toInt().coerceAtLeast(1),
            true
        )
        if (scaled !== bitmap) bitmap.recycle()
        return scaled
    }

    private fun saveCurrentVisualToDownloads() {
        val bitmap = currentFrozenBitmap
        if (bitmap == null) {
            Toast.makeText(this, "No translated page to download yet", Toast.LENGTH_SHORT).show()
            return
        }
        lifecycleScope.launch {
            val savedUri = withContext(Dispatchers.IO) { saveBitmapToPictures(bitmap) }
            Toast.makeText(
                this@CameraActivity,
                if (savedUri != null) "Translated page saved" else "Could not save translated page",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun shareCurrentVisual() {
        val bitmap = currentFrozenBitmap
        if (bitmap == null) {
            Toast.makeText(this, "No translated page to share yet", Toast.LENGTH_SHORT).show()
            return
        }
        lifecycleScope.launch {
            val uri = withContext(Dispatchers.IO) { saveBitmapToShareCache(bitmap) }
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(Intent.createChooser(shareIntent, "Share translated page"))
        }
    }

    private fun saveBitmapToShareCache(bitmap: Bitmap): Uri {
        val dir = File(cacheDir, "translated").apply { mkdirs() }
        val file = File(dir, translatedVisualFileName())
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        return FileProvider.getUriForFile(this, "$packageName.fileprovider", file)
    }

    private fun saveBitmapToPictures(bitmap: Bitmap): Uri? {
        val name = translatedVisualFileName()
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, name)
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/BridgeTranslator")
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
            val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                ?: return null
            contentResolver.openOutputStream(uri)?.use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            } ?: return null
            values.clear()
            values.put(MediaStore.Images.Media.IS_PENDING, 0)
            contentResolver.update(uri, values, null, null)
            uri
        } else {
            val dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                ?.resolve("BridgeTranslator")
                ?.apply { mkdirs() }
                ?: return null
            val file = File(dir, name)
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            FileProvider.getUriForFile(this, "$packageName.fileprovider", file)
        }
    }

    private fun translatedVisualFileName(): String =
        "bridge_translated_${System.currentTimeMillis()}.png"

    private fun resolveImageMimeType(uri: Uri): String =
        contentResolver.getType(uri) ?: "image/jpeg"

    private fun showPermissionPlaceholder() {
        tvScanStatus.text = "Camera permission denied"
    }

    // - Cleanup -

    override fun onDestroy() {
        super.onDestroy()
        currentFrozenBitmap?.recycle()
        currentFrozenBitmap = null
        latinTextRecognizer.close()
        koreanTextRecognizer.close()
        chineseTextRecognizer.close()
        japaneseTextRecognizer.close()
        devanagariTextRecognizer.close()
        languageIdentifier.close()
        engine.close()
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        textToSpeech = null
        isReadingTranslation = false
        pendingSpeechParts = 0
        fileExtractor.close()
    }

    private fun chooseBestOcrText(vararg results: Text?): String {
        val candidates = results.filterNotNull().map { mergedTextFromVisionText(it) }.filter { it.isNotBlank() }

        return candidates.maxWithOrNull(
            compareBy<String> { scriptCoverageScore(it) }
                .thenBy { normalizeOcrText(it).length }
        ).orEmpty()
    }

    private fun chooseBestOcrResult(vararg results: Text?): Text? =
        results.filterNotNull().maxWithOrNull(
            compareBy<Text> { scriptCoverageScore(it.text) }
                .thenBy { normalizeOcrText(it.text).length }
        )

    private fun mergedTextFromVisionText(visionText: Text): String =
        visionText.textBlocks
            .mapNotNull { block ->
                val merged = block.lines.joinToString(" ") { it.text.trim() }.trim()
                if (merged.length < 2) null else merged
            }
            .joinToString("\n")
            .trim()

    private fun updateDetectedLanguage(srcCode: String) {
        val displayCode = Language.getLanguageByCode(srcCode)?.code?.uppercase(Locale.ROOT)
            ?: srcCode.uppercase(Locale.ROOT)
        runOnUiThread {
            tvDetectedLang.text = displayCode
            tvOriginalLabel.text = "ORIGINAL ($displayCode)"
        }
    }

    private fun normalizeOcrText(text: String): String =
        text.replace(Regex("[\\t\\r]+"), " ")
            .replace(Regex(" +"), " ")
            .replace(Regex("\\n{3,}"), "\n\n")
            .trim()

    private fun String.normalizeLangTag(): String =
        lowercase(Locale.ROOT).substringBefore("-").ifBlank { "und" }

    private fun detectLanguageFromScript(text: String): String? {
        val totalLetters = text.count { it.isLetter() }.coerceAtLeast(1)
        fun ratio(range: CharRange): Float = text.count { it in range } / totalLetters.toFloat()

        return when {
            ratio('\uAC00'..'\uD7AF') > 0.20f || ratio('\u1100'..'\u11FF') > 0.20f -> "ko"
            ratio('\u3040'..'\u30FF') > 0.15f -> "ja"
            ratio('\u4E00'..'\u9FFF') > 0.25f -> "zh"
            ratio('\u0600'..'\u06FF') > 0.25f -> "ar"
            ratio('\u0900'..'\u097F') > 0.25f -> "hi"
            else -> null
        }
    }

    private fun scriptCoverageScore(text: String): Int =
        text.count {
            it.isLetterOrDigit() ||
                    it in '\uAC00'..'\uD7AF' ||
                    it in '\u3040'..'\u30FF' ||
                    it in '\u4E00'..'\u9FFF' ||
                    it in '\u0600'..'\u06FF' ||
                    it in '\u0900'..'\u097F'
        }

    private fun hasTranslatableCharacter(text: String): Boolean =
        text.any {
            it.isLetterOrDigit() ||
                    it in '\uAC00'..'\uD7AF' ||
                    it in '\u3040'..'\u30FF' ||
                    it in '\u4E00'..'\u9FFF' ||
                    it in '\u0600'..'\u06FF' ||
                    it in '\u0900'..'\u097F'
        }

    private fun similarityRatio(a: String, b: String): Float {
        if (a.isBlank() || b.isBlank()) return 0f
        if (a == b) return 1f
        val distance = levenshteinDistance(a.take(300), b.take(300))
        val maxLen = maxOf(a.length, b.length).coerceAtLeast(1)
        return 1f - (distance / maxLen.toFloat())
    }

    private fun levenshteinDistance(a: String, b: String): Int {
        val previous = IntArray(b.length + 1) { it }
        val current = IntArray(b.length + 1)
        for (i in 1..a.length) {
            current[0] = i
            for (j in 1..b.length) {
                val cost = if (a[i - 1] == b[j - 1]) 0 else 1
                current[j] = minOf(
                    current[j - 1] + 1,
                    previous[j] + 1,
                    previous[j - 1] + cost
                )
            }
            for (j in previous.indices) previous[j] = current[j]
        }
        return previous[b.length]
    }

    private fun <T> com.google.android.gms.tasks.Task<T>.resultOrNull(): T? =
        if (isSuccessful) result else null

    private data class CameraTextBlock(
        val text: String,
        val translatedText: String,
        val rect: Rect,
        val sourceLang: String,
        val alignment: Paint.Align = Paint.Align.LEFT
    )

    private data class FittedCameraText(
        val lines: List<String>,
        val textSize: Float
    )

    private companion object {
        const val FRAME_ANALYSIS_INTERVAL_MS = 700L
        const val MIN_FRAME_TEXT_FOR_CAPTURE = 12
        const val MIN_TEXT_FOR_TRANSLATION = 1
        const val STABLE_TEXT_SIMILARITY = 0.78f
        const val STABLE_TEXT_HITS_REQUIRED = 1
        const val AUTO_FREEZE_DELAY_MS = 1800L
        const val LONG_TEXT_FAST_LOCK_LENGTH = 45
        const val LANGUAGE_CONFIDENCE_THRESHOLD = 0.45f
        const val TRANSLATION_PARALLELISM = 3
        const val DEFAULT_CAMERA_SOURCE_LANG = "ko"
        const val MODEL_PREWARM_TEXT = "test"
        const val MAX_TTS_CHARS = 3500
        const val MAX_TTS_CHUNK_CHARS = 220
        const val TTS_UTTERANCE_PREFIX = "camera_translation_"
        const val VISUAL_MAX_BITMAP_EDGE = 2400
        const val MAX_VISUAL_PDF_PAGES = 10
        const val VISUAL_TEXT_PADDING = 8f
        const val PRIVACY_MASK = "@#$ @#$"
        const val DOCUMENT_FRAME_LEFT_RATIO = 0.07f
        const val DOCUMENT_FRAME_TOP_RATIO = 0.18f
        const val DOCUMENT_FRAME_RIGHT_RATIO = 0.93f
        const val DOCUMENT_FRAME_BOTTOM_RATIO = 0.78f
        val CAMERA_UI_SKIP_WORDS = setOf(
            "bridge camera",
            "gallery",
            "file",
            "close",
            "share",
            "download",
            "tap screen to scan again",
            "error repo",
            "korean",
            "english"
        )
        val SYSTEM_PRIVATE_HINTS = setOf(
            "android system",
            "usb debugging",
            "mobile data",
            "wi-fi",
            "wifi",
            "clear all",
            "notification",
            "charging this device",
            "password",
            "otp",
            "bank",
            "account",
            "private chats"
        )
    }
}
