package com.bridge.translator.processing

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Rect
import android.util.Log
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ScreenAnalyser(private val targetLang: String) {

    private val recognizer = TextRecognition.getClient(
        KoreanTextRecognizerOptions.Builder().build()
    )
    private val langDetector = LanguageIdentification.getClient()
    private val orientationDetector = TextOrientationDetector()
    private val languageFallback = LanguageFallback()

    suspend fun analyse(bitmap: Bitmap): List<TextBlock> =
        withContext(Dispatchers.IO) {
            Log.d(FLOW_TAG, "ScreenAnalyser.analyse bitmap=${bitmap.width}x${bitmap.height} target=$targetLang")
            val image = InputImage.fromBitmap(bitmap, 0)
            val result = Tasks.await(recognizer.process(image))
            Log.d(FLOW_TAG, "ScreenAnalyser OCR textBlocks=${result.textBlocks.size}")

            // Detect language for all blocks concurrently — each call is independent.
            val analysed = coroutineScope {
                result.textBlocks.map { block ->
                    async {
                        processBlock(block, bitmap)
                    }
                }.awaitAll().filterNotNull()
            }
            Log.d(FLOW_TAG, "ScreenAnalyser analysed=${analysed.size}")
            analysed
        }

    private suspend fun processBlock(block: Text.TextBlock, bitmap: Bitmap): TextBlock? {
        val rect = block.boundingBox ?: return null
        var text = block.lines.joinToString(" ") { it.text.trim() }.trim()
        if (text.length < 2) return null

        // NEW: Check text orientation and rotate if vertical
        val (rotatedBitmap, rotationAngle) = orientationDetector.rotateIfNeeded(bitmap, block)
        if (rotationAngle != 0f) {
            try {
                val image = InputImage.fromBitmap(rotatedBitmap, 0)
                val rerecognized = Tasks.await(recognizer.process(image))
                if (rerecognized.textBlocks.isNotEmpty()) {
                    text = rerecognized.textBlocks[0].lines.joinToString(" ") { it.text.trim() }.trim()
                    Log.d(FLOW_TAG, "Re-recognized vertical text after $rotationAngle° rotation")
                }
            } catch (e: Exception) {
                Log.w(FLOW_TAG, "Re-recognition failed, using original: ${e.message}")
            }
        }

        // NEW: Use fallback language detection (handles ML Kit + character set + dictionary)
        val lang = languageFallback.detectLanguageWithFallback(text)
        Log.d(FLOW_TAG, "Detected block lang=$lang text='${text.take(30)}'")
        if (lang == targetLang) return null

        val textColor = sampleTextColor(bitmap, rect)
        val bgColor = sampleOutside(bitmap, rect)
        val avgLineH = block.lines.mapNotNull { it.boundingBox?.height() }.average()
        val isBold = avgLineH > 24
        val alignment = estimateAlignment(block)

        return TextBlock(
            originalText = text,
            rect = rect,
            textColor = textColor,
            bgColor = bgColor,
            estimatedFontSize = rect.height() * 0.65f,
            alignment = alignment,
            isBold = isBold,
            detectedLang = lang
        )
    }

    private companion object {
        const val FLOW_TAG = "BridgeFlow"
    }

    private fun String.containsHangul(): Boolean =
        any { it in '가'..'힯' || it in 'ᄀ'..'ᇿ' || it in '㄰'..'㆏' }

    private fun sampleTextColor(bitmap: Bitmap, rect: Rect): Int {
        val cx = rect.centerX().coerceIn(0, bitmap.width - 1)
        val cy = rect.centerY().coerceIn(0, bitmap.height - 1)
        val bgColor = sampleOutside(bitmap, rect)
        val samples = mutableListOf<Int>()
        for (dx in -6..6 step 2) {
            for (dy in -6..6 step 2) {
                val px = (cx + dx).coerceIn(0, bitmap.width - 1)
                val py = (cy + dy).coerceIn(0, bitmap.height - 1)
                val pixel = bitmap.getPixel(px, py)
                if (pixel != bgColor) samples.add(pixel)
            }
        }
        if (samples.isEmpty()) {
            val lum = luminance(bgColor)
            return if (lum > 0.5f) Color.BLACK else Color.WHITE
        }
        return samples.groupBy { it }.maxByOrNull { it.value.size }?.key ?: Color.BLACK
    }

    private fun sampleOutside(bitmap: Bitmap, rect: Rect): Int {
        val samples = mutableListOf<Int>()
        val margin = 4
        val step = maxOf(1, minOf(rect.width(), rect.height()) / 8)

        val topY = (rect.top - margin).coerceAtLeast(0)
        var x = rect.left
        while (x <= rect.right) {
            samples.add(bitmap.getPixel(x.coerceIn(0, bitmap.width - 1), topY))
            x += step
        }

        val bottomY = (rect.bottom + margin).coerceAtMost(bitmap.height - 1)
        x = rect.left
        while (x <= rect.right) {
            samples.add(bitmap.getPixel(x.coerceIn(0, bitmap.width - 1), bottomY))
            x += step
        }

        val leftX = (rect.left - margin).coerceAtLeast(0)
        var y = rect.top
        while (y <= rect.bottom) {
            samples.add(bitmap.getPixel(leftX, y.coerceIn(0, bitmap.height - 1)))
            y += step
        }

        val rightX = (rect.right + margin).coerceAtMost(bitmap.width - 1)
        y = rect.top
        while (y <= rect.bottom) {
            samples.add(bitmap.getPixel(rightX, y.coerceIn(0, bitmap.height - 1)))
            y += step
        }

        val opaque = samples.filter { Color.alpha(it) > 128 }
        val dominant = opaque.groupBy { it }.maxByOrNull { it.value.size }?.key
        if (dominant != null) return dominant or 0xFF000000.toInt()
        return Color.WHITE
    }

    private fun estimateAlignment(block: Text.TextBlock): Alignment {
        val bLeft = block.boundingBox?.left ?: return Alignment.LEFT
        val bRight = block.boundingBox?.right ?: return Alignment.LEFT
        val bCenter = (bLeft + bRight) / 2.0
        val bWidth = bRight - bLeft

        val lineCenters = block.lines.mapNotNull {
            val b = it.boundingBox ?: return@mapNotNull null
            (b.left + b.right) / 2.0
        }
        if (lineCenters.isEmpty()) return Alignment.LEFT

        val avgCenter = lineCenters.average()
        return when {
            kotlin.math.abs(avgCenter - bCenter) < bWidth * 0.1 -> Alignment.CENTER
            avgCenter > bCenter -> Alignment.RIGHT
            else -> Alignment.LEFT
        }
    }

    private fun luminance(color: Int): Float =
        (0.299f * Color.red(color) +
                0.587f * Color.green(color) +
                0.114f * Color.blue(color)) / 255f
}
