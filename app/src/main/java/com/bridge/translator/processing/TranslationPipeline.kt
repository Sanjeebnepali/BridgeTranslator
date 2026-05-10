package com.bridge.translator.processing

import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Rect
import android.util.Log
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions
import com.google.mlkit.vision.text.devanagari.DevanagariTextRecognizerOptions
import com.google.mlkit.vision.text.japanese.JapaneseTextRecognizerOptions
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Orchestrates per-shape OCR → language identification → translation →
 * erase-and-replace → composite back into a frame.
 *
 * All heavy work runs on [Dispatchers.Default] / [Dispatchers.IO]; the caller
 * supplies a UI-thread callback to report progress.
 *
 * Usage:
 * ```kotlin
 * val pipeline = TranslationPipeline()
 * val result   = pipeline.process(
 *     frame        = frozenBitmap,
 *     shapes       = detectedShapes,
 *     targetLang   = "en",
 *     fallbackLang = "ko",
 *     onShapeStart = { idx -> overlayView.highlightShape(idx) }
 * )
 * ```
 */
class TranslationPipeline {

    private val TAG = "TranslationPipeline"

    // ML Kit recognisers (one per script; shared across calls)
    private val latinRecognizer     = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    private val koreanRecognizer    = TextRecognition.getClient(KoreanTextRecognizerOptions.Builder().build())
    private val chineseRecognizer   = TextRecognition.getClient(ChineseTextRecognizerOptions.Builder().build())
    private val japaneseRecognizer  = TextRecognition.getClient(JapaneseTextRecognizerOptions.Builder().build())
    private val devanagariRecognizer = TextRecognition.getClient(DevanagariTextRecognizerOptions.Builder().build())
    private val langIdentifier      = LanguageIdentification.getClient()

    // ── Result ─────────────────────────────────────────────────────────────────

    /** Result for a single shape after the full pipeline. */
    data class ShapeResult(
        val shapeIndex:     Int,
        val shape:          DetectedShape,
        val originalText:   String,
        val translatedText: String,
        val sourceLang:     String,
        val processedBitmap: Bitmap   // the frame with this shape's text replaced
    )

    // ── Main entry point ───────────────────────────────────────────────────────

    /**
     * Process every shape concurrently and composite each result back into a
     * copy of [frame].
     *
     * @param frame        Frozen full-resolution camera frame.
     * @param shapes       Shapes to process (from ShapeDetector).
     * @param targetLang   BCP-47 target language code (e.g. "en").
     * @param fallbackLang Source language used when auto-detect fails.
     * @param onShapeStart Called on the UI thread when processing of shape [index] begins.
     * @return             Composed result bitmap (all shapes translated) and the list of per-shape results.
     */
    suspend fun process(
        frame:        Bitmap,
        shapes:       List<DetectedShape>,
        targetLang:   String,
        fallbackLang: String = "ko",
        onShapeStart: ((index: Int) -> Unit)? = null
    ): Pair<Bitmap, List<ShapeResult>> = withContext(Dispatchers.Default) {

        if (shapes.isEmpty()) return@withContext frame to emptyList()

        // Work on a mutable copy so we can composite progressively
        val result = frame.copy(Bitmap.Config.ARGB_8888, true)

        val shapeResults = coroutineScope {
            shapes.mapIndexed { idx, shape ->
                async {
                    withContext(Dispatchers.Main) { onShapeStart?.invoke(idx) }
                    processShape(result, shape, idx, targetLang, fallbackLang)
                }
            }.map { it.await() }.filterNotNull()
        }

        result to shapeResults
    }

    // ── Per-shape pipeline ─────────────────────────────────────────────────────

    private suspend fun processShape(
        frame:        Bitmap,  // mutable; we composite into this
        shape:        DetectedShape,
        index:        Int,
        targetLang:   String,
        fallbackLang: String
    ): ShapeResult? = withContext(Dispatchers.Default) {

        val t0 = System.currentTimeMillis()

        // 1. Crop and rotate to shape bounds
        val cropped = ImagePreprocessor.cropAndRotate(frame, shape.bounds, shape.rotationDegrees)

        // 2. Scale to max 720 px for OCR speed
        val ocrBitmap = ImagePreprocessor.scaleForOcr(cropped)
        val ocrScaleX = cropped.width.toFloat()  / ocrBitmap.width
        val ocrScaleY = cropped.height.toFloat() / ocrBitmap.height

        // 3. OCR – run all recognisers concurrently
        val visionText = runOcr(ocrBitmap) ?: run {
            if (ocrBitmap !== cropped) ocrBitmap.recycle()
            cropped.recycle()
            return@withContext null
        }
        val originalText = mergeText(visionText).trim()
        if (originalText.isBlank()) {
            if (ocrBitmap !== cropped) ocrBitmap.recycle()
            cropped.recycle()
            return@withContext null
        }

        // 4. Language identification
        val srcLang = identifyLanguage(originalText) ?: fallbackLang

        // 5. Translate
        val translated = translateText(originalText, srcLang, targetLang).ifBlank { originalText }

        // 6. Build TextBlock list for BitmapTextEraser
        val eraser = BitmapTextEraser()
        val blocks = textBlocksFromVisionText(visionText, translated, ocrScaleX, ocrScaleY)

        // 7. Erase & replace – operates on the cropped bitmap
        val processedCrop = if (blocks.isNotEmpty()) {
            eraser.eraseAndReplace(
                if (ocrBitmap !== cropped) {
                    // Scale ocrBitmap's block coords back to cropped coords handled via blocks
                    cropped
                } else cropped,
                blocks
            )
        } else cropped

        // 8. Composite back into frame (mutates [frame])
        ImagePreprocessor.compositeShapeBack(frame, processedCrop, shape.bounds, shape.shapeType)

        if (processedCrop !== cropped) processedCrop.recycle()
        if (ocrBitmap !== cropped) ocrBitmap.recycle()
        cropped.recycle()

        val elapsed = System.currentTimeMillis() - t0
        Log.d(TAG, "Shape[$index] ${shape.shapeType} processed in ${elapsed}ms: \"$originalText\" → \"$translated\"")

        ShapeResult(
            shapeIndex     = index,
            shape          = shape,
            originalText   = originalText,
            translatedText = translated,
            sourceLang     = srcLang,
            processedBitmap = frame
        )
    }

    // ── OCR helpers ───────────────────────────────────────────────────────────

    private suspend fun runOcr(bitmap: Bitmap): Text? = withContext(Dispatchers.IO) {
        try {
            val inputImage = InputImage.fromBitmap(bitmap, 0)
            val latinTask     = latinRecognizer.process(inputImage)
            val koreanTask    = koreanRecognizer.process(inputImage)
            val chineseTask   = chineseRecognizer.process(inputImage)
            val japaneseTask  = japaneseRecognizer.process(inputImage)
            val devanagariTask = devanagariRecognizer.process(inputImage)

            Tasks.whenAllComplete(latinTask, koreanTask, chineseTask, japaneseTask, devanagariTask).await()

            // Pick the result with the most text
            listOf(latinTask, koreanTask, chineseTask, japaneseTask, devanagariTask)
                .filter { it.isSuccessful && it.result != null }
                .maxByOrNull { mergeText(it.result!!).length }
                ?.result
        } catch (e: Exception) {
            Log.w(TAG, "OCR failed: ${e.message}")
            null
        }
    }

    private fun mergeText(visionText: Text): String =
        visionText.textBlocks.joinToString("\n") { block ->
            block.lines.joinToString(" ") { it.text }
        }

    private fun textBlocksFromVisionText(
        visionText: Text,
        translatedText: String,
        scaleX: Float,
        scaleY: Float
    ): List<TextBlock> {
        val blocks = visionText.textBlocks
        if (blocks.isEmpty()) return emptyList()

        // Distribute the translated text across blocks proportionally by char count
        val originalBlocks = blocks.map { block ->
            block.lines.joinToString(" ") { it.text }
        }
        val totalLen = originalBlocks.sumOf { it.length }.coerceAtLeast(1)
        val translatedWords = translatedText.split(" ").toMutableList()
        val totalWords = translatedWords.size.coerceAtLeast(1)

        return blocks.mapIndexed { idx, block ->
            val proportion = originalBlocks[idx].length.toFloat() / totalLen
            val wordCount  = (proportion * totalWords).toInt().coerceAtLeast(1)
            val start      = (idx * wordCount).coerceAtMost(translatedWords.size)
            val end        = (start + wordCount).coerceAtMost(translatedWords.size)
            val blockTranslation = if (start < translatedWords.size)
                translatedWords.subList(start, end).joinToString(" ")
            else
                translatedText

            val rawRect = block.boundingBox ?: android.graphics.Rect(0, 0, 100, 20)
            val scaledRect = Rect(
                (rawRect.left   * scaleX).toInt(),
                (rawRect.top    * scaleY).toInt(),
                (rawRect.right  * scaleX).toInt(),
                (rawRect.bottom * scaleY).toInt()
            )

            TextBlock(
                originalText     = originalBlocks[idx],
                translatedText   = blockTranslation,
                rect             = scaledRect,
                textColor        = android.graphics.Color.BLACK,
                bgColor          = android.graphics.Color.WHITE,
                estimatedFontSize = (scaledRect.height() * 0.65f).coerceAtLeast(10f),
                alignment        = Alignment.LEFT,
                isBold           = false,
                detectedLang     = "und"
            )
        }
    }

    // ── Language ID ───────────────────────────────────────────────────────────

    private suspend fun identifyLanguage(text: String): String? = suspendCoroutine { cont ->
        langIdentifier.identifyLanguage(text)
            .addOnSuccessListener { lang ->
                cont.resume(if (lang == "und" || lang.isBlank()) null else lang)
            }
            .addOnFailureListener { cont.resume(null) }
    }

    // ── Translation ───────────────────────────────────────────────────────────

    private suspend fun translateText(
        text:       String,
        sourceLang: String,
        targetLang: String
    ): String = suspendCoroutine { cont ->
        com.bridge.translator.engine.TranslationEngine.translate(
            text       = text,
            sourceCode = sourceLang,
            targetCode = targetLang,
            wifiOnly   = false,
            onDownloading = {},
            onResult   = { cont.resume(it) },
            onError    = { cont.resume("") }
        )
    }

    // ── Resource management ───────────────────────────────────────────────────

    fun close() {
        latinRecognizer.close()
        koreanRecognizer.close()
        chineseRecognizer.close()
        japaneseRecognizer.close()
        devanagariRecognizer.close()
        langIdentifier.close()
    }

    // ── Extension ─────────────────────────────────────────────────────────────

    private suspend fun <T> com.google.android.gms.tasks.Task<T>.await(): T? =
        suspendCoroutine { cont ->
            addOnCompleteListener { task ->
                cont.resume(if (task.isSuccessful) task.result else null)
            }
        }
}
