package com.bridge.translator.camera

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import com.bridge.translator.camera.data.EnhancedOcrResult
import com.bridge.translator.camera.data.TextOrientationBlock
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Module 3 – Small Text Enhancement (<8pt).
 *
 * Pipeline for low-resolution text regions:
 *   Stage 1: Estimate font size (from OrientationDetector output).
 *   Stage 2: Upscale bitmap 2–3x (bicubic via createScaledBitmap with filter=true).
 *   Stage 3: Histogram equalization (contrast enhancement on V channel).
 *   Stage 4: Sharpening convolution (3×3 unsharp mask).
 *   Stage 5: Multi-run ML Kit OCR on enhanced image.
 *   Stage 6: Confidence boosting when runs agree.
 *
 * Target: 80 % accuracy (4–6 pt), 85 % (6–8 pt), 95 % (8–10 pt).
 */
object SmallTextEnhancer {

    private const val SMALL_TEXT_THRESHOLD_PT = 8f   // below this → enhance
    private const val CONFIDENCE_THRESHOLD    = 0.75f

    private val latinRecognizer  = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    private val koreanRecognizer = TextRecognition.getClient(KoreanTextRecognizerOptions.Builder().build())

    // ── Public API ─────────────────────────────────────────────────────────────

    /**
     * Process a list of OCR blocks.  For each block whose estimated font size
     * is < [SMALL_TEXT_THRESHOLD_PT], crop its region, apply enhancement, and
     * re-run OCR.  Non-small blocks pass through unchanged.
     *
     * @param sourceBitmap The full-resolution camera frame.
     * @param blocks       Orientation-annotated text blocks from [OrientationDetector].
     * @return             Per-block [EnhancedOcrResult] list.
     */
    suspend fun enhanceBlocks(
        sourceBitmap: Bitmap,
        blocks: List<TextOrientationBlock>
    ): List<EnhancedOcrResult> = withContext(Dispatchers.Default) {
        blocks.map { block ->
            if (block.estimatedFontSizePt < SMALL_TEXT_THRESHOLD_PT) {
                enhanceBlock(sourceBitmap, block)
            } else {
                // Large text: pass through with a synthetic high-confidence result
                EnhancedOcrResult(
                    text            = block.text,
                    confidence      = block.ocrConfidence,
                    upscaleFactor   = 1f,
                    wasEnhanced     = false,
                    mlkitText       = block.text,
                    mlkitConfidence = block.ocrConfidence
                )
            }
        }
    }

    // ── Per-block enhancement ──────────────────────────────────────────────────

    private suspend fun enhanceBlock(
        source: Bitmap,
        block: TextOrientationBlock
    ): EnhancedOcrResult = withContext(Dispatchers.Default) {

        // 1. Choose upscale factor
        val scale = upscaleFactor(block.estimatedFontSizePt)

        // 2. Crop region with margin
        val crop = cropWithMargin(source, block.boundingBox, margin = 12)

        // 3. Upscale
        val upscaled = upscaleBitmap(crop, scale)
        crop.recycle()

        // 4. Contrast enhancement (histogram approximation)
        val contrasted = enhanceContrast(upscaled)
        upscaled.recycle()

        // 5. Sharpen
        val sharpened = sharpen(contrasted)
        contrasted.recycle()

        // 6. Multi-run OCR
        val run1 = runOcr(sharpened, latin = true)
        val run2 = runOcr(sharpened, latin = false)  // Korean/CJK recognizer

        sharpened.recycle()

        // 7. Pick best result and boost confidence
        val best = if ((run1?.let { mergeText(it) }?.length ?: 0) >=
                       (run2?.let { mergeText(it) }?.length ?: 0)) run1 else run2

        val bestText = best?.let { mergeText(it) } ?: block.text
        val run1Text = run1?.let { mergeText(it) } ?: ""
        val run2Text = run2?.let { mergeText(it) } ?: ""

        val rawConf  = inferConfidence(best)
        // Boost if both agree
        val boosted  = if (run1Text.isNotBlank() && run2Text.isNotBlank() &&
                           textSimilarity(run1Text, run2Text) > 0.8f) {
            (rawConf + 0.15f).coerceAtMost(0.95f)
        } else rawConf

        EnhancedOcrResult(
            text            = bestText.ifBlank { block.text },
            confidence      = boosted,
            upscaleFactor   = scale,
            wasEnhanced     = true,
            mlkitText       = run1Text,
            mlkitConfidence = rawConf
        )
    }

    // ── Stage helpers ──────────────────────────────────────────────────────────

    private fun upscaleFactor(fontPt: Float): Float = when {
        fontPt < 4f  -> 3.0f
        fontPt < 6f  -> 2.5f
        else          -> 2.0f
    }

    private fun cropWithMargin(source: Bitmap, bounds: RectF, margin: Int): Bitmap {
        val l = (bounds.left   - margin).toInt().coerceAtLeast(0)
        val t = (bounds.top    - margin).toInt().coerceAtLeast(0)
        val r = (bounds.right  + margin).toInt().coerceAtMost(source.width)
        val b = (bounds.bottom + margin).toInt().coerceAtMost(source.height)
        val w = (r - l).coerceAtLeast(1)
        val h = (b - t).coerceAtLeast(1)
        return Bitmap.createBitmap(source, l, t, w, h)
    }

    private fun upscaleBitmap(src: Bitmap, scale: Float): Bitmap {
        val nw = (src.width  * scale).roundToInt().coerceAtLeast(1)
        val nh = (src.height * scale).roundToInt().coerceAtLeast(1)
        return Bitmap.createScaledBitmap(src, nw, nh, true /* bilinear filter */)
    }

    /**
     * Approximate histogram equalization:
     * Increase contrast by stretching the luminance range.
     * Uses a ColorMatrix to boost contrast without altering hue.
     */
    private fun enhanceContrast(src: Bitmap): Bitmap {
        val result = src.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(result)
        val paint  = Paint()

        // Boost contrast (contrast = 1.4) and brightness slightly
        val contrast   = 1.4f
        val brightness = -20f
        val cm = ColorMatrix(floatArrayOf(
            contrast, 0f,       0f,       0f, brightness,
            0f,       contrast, 0f,       0f, brightness,
            0f,       0f,       contrast, 0f, brightness,
            0f,       0f,       0f,       1f, 0f
        ))
        paint.colorFilter = ColorMatrixColorFilter(cm)
        canvas.drawBitmap(src, 0f, 0f, paint)
        return result
    }

    /**
     * Approximate unsharp mask using a simple 3×3 sharpening kernel applied
     * via per-pixel convolution on a downscaled check region.
     *
     * Kernel: [[ 0, -1, 0], [-1, 5, -1], [ 0, -1, 0]]
     */
    private fun sharpen(src: Bitmap): Bitmap {
        val w = src.width; val h = src.height
        if (w < 3 || h < 3) return src.copy(Bitmap.Config.ARGB_8888, false)

        val pixels = IntArray(w * h)
        src.getPixels(pixels, 0, w, 0, 0, w, h)
        val output = IntArray(w * h)

        for (y in 1 until h - 1) {
            for (x in 1 until w - 1) {
                val i = y * w + x
                val r = (5 * red(pixels[i])
                        - red(pixels[i - 1]) - red(pixels[i + 1])
                        - red(pixels[i - w]) - red(pixels[i + w])).coerceIn(0, 255)
                val g = (5 * grn(pixels[i])
                        - grn(pixels[i - 1]) - grn(pixels[i + 1])
                        - grn(pixels[i - w]) - grn(pixels[i + w])).coerceIn(0, 255)
                val b = (5 * blu(pixels[i])
                        - blu(pixels[i - 1]) - blu(pixels[i + 1])
                        - blu(pixels[i - w]) - blu(pixels[i + w])).coerceIn(0, 255)
                output[i] = (0xFF shl 24) or (r shl 16) or (g shl 8) or b
            }
        }
        // Copy border pixels unchanged
        for (x in 0 until w) { output[x] = pixels[x]; output[(h-1)*w+x] = pixels[(h-1)*w+x] }
        for (y in 0 until h) { output[y*w] = pixels[y*w]; output[y*w+w-1] = pixels[y*w+w-1] }

        val result = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        result.setPixels(output, 0, w, 0, 0, w, h)
        return result
    }

    // ── OCR helpers ───────────────────────────────────────────────────────────

    private suspend fun runOcr(bitmap: Bitmap, latin: Boolean): Text? =
        suspendCoroutine { cont ->
            val recognizer = if (latin) latinRecognizer else koreanRecognizer
            recognizer.process(InputImage.fromBitmap(bitmap, 0))
                .addOnSuccessListener { cont.resume(it) }
                .addOnFailureListener { cont.resume(null) }
        }

    private fun mergeText(visionText: Text): String =
        visionText.textBlocks.joinToString(" ") { block ->
            block.lines.joinToString(" ") { it.text }
        }.trim()

    private fun inferConfidence(result: Text?): Float {
        if (result == null) return 0.3f
        val text = mergeText(result)
        if (text.isBlank()) return 0.3f
        val noiseRatio = text.count { !it.isLetterOrDigit() && !it.isWhitespace() }.toFloat() /
                text.length.toFloat().coerceAtLeast(1f)
        return (0.75f - noiseRatio * 0.4f).coerceIn(0.3f, 0.95f)
    }

    // ── Text similarity ────────────────────────────────────────────────────────

    private fun textSimilarity(a: String, b: String): Float {
        if (a == b) return 1f
        val shorter = if (a.length < b.length) a else b
        val longer  = if (a.length < b.length) b else a
        if (longer.isEmpty()) return 1f
        val commonChars = shorter.count { it in longer }
        return commonChars.toFloat() / longer.length.toFloat()
    }

    // ── Pixel helpers ──────────────────────────────────────────────────────────

    private fun red(pixel: Int) = (pixel shr 16) and 0xFF
    private fun grn(pixel: Int) = (pixel shr 8)  and 0xFF
    private fun blu(pixel: Int) =  pixel          and 0xFF

    // ── Sharpness measurement (Laplacian variance) ─────────────────────────────

    /**
     * Measure image sharpness using Laplacian variance.
     * >500 = sharp, 100–500 = moderate, <100 = blurry.
     */
    fun measureSharpness(bitmap: Bitmap): Float {
        val small = Bitmap.createScaledBitmap(bitmap, 200, 200, true)
        val w = small.width; val h = small.height
        val pixels = IntArray(w * h)
        small.getPixels(pixels, 0, w, 0, 0, w, h)
        small.recycle()

        var sum = 0f; var sumSq = 0f; var count = 0
        for (y in 1 until h - 1) {
            for (x in 1 until w - 1) {
                val i    = y * w + x
                val gray = (0.299f * red(pixels[i]) + 0.587f * grn(pixels[i]) + 0.114f * blu(pixels[i]))
                val tGray = (0.299f * red(pixels[i-w]) + 0.587f * grn(pixels[i-w]) + 0.114f * blu(pixels[i-w]))
                val bGray = (0.299f * red(pixels[i+w]) + 0.587f * grn(pixels[i+w]) + 0.114f * blu(pixels[i+w]))
                val lGray = (0.299f * red(pixels[i-1]) + 0.587f * grn(pixels[i-1]) + 0.114f * blu(pixels[i-1]))
                val rGray = (0.299f * red(pixels[i+1]) + 0.587f * grn(pixels[i+1]) + 0.114f * blu(pixels[i+1]))
                val lap = tGray + bGray + lGray + rGray - 4 * gray
                sum   += lap; sumSq += lap * lap; count++
            }
        }
        if (count == 0) return 0f
        val mean    = sum / count
        val variance = sumSq / count - mean * mean
        return variance.coerceAtLeast(0f)
    }

    fun close() {
        latinRecognizer.close()
        koreanRecognizer.close()
    }
}
