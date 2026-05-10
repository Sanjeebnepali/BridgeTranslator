package com.bridge.translator.analysis

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Rect
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions

class ScreenAnalyser {

    private val recognizer by lazy {
        TextRecognition.getClient(KoreanTextRecognizerOptions.Builder().build())
    }

    fun analyse(bitmap: Bitmap, targetLanguage: String): List<AnalysedBlock> {
        val image = InputImage.fromBitmap(bitmap, 0)
        val visionText = Tasks.await(recognizer.process(image))
        val languageIdentifier = LanguageIdentification.getClient()

        return try {
            visionText.textBlocks.mapNotNull { block ->
                val rect = mergedRect(block) ?: return@mapNotNull null
                val mergedText = block.lines.joinToString(" ") { it.text.trim() }.trim()
                if (mergedText.length < 2) return@mapNotNull null

                val lang = Tasks.await(languageIdentifier.identifyLanguage(mergedText))
                    .takeIf { it != "und" && it.isNotBlank() }
                    ?: return@mapNotNull null
                if (lang == targetLanguage) return@mapNotNull null

                val bgColor = sampleBackground(bitmap, rect)
                val textColor = sampleTextColor(bitmap, rect, bgColor)

                AnalysedBlock(
                    text = mergedText,
                    detectedLang = lang,
                    boundingRect = rect,
                    backgroundColor = bgColor,
                    textColor = textColor,
                    estimatedFontSize = rect.height() * 0.65f,
                    alignment = estimateAlignment(block)
                )
            }
        } finally {
            languageIdentifier.close()
        }
    }

    private fun mergedRect(block: Text.TextBlock): Rect? {
        val initial = block.boundingBox?.let { Rect(it) }
            ?: block.lines.firstOrNull()?.boundingBox?.let { Rect(it) }
            ?: return null
        return block.lines.fold(initial) { acc, line ->
            val b = line.boundingBox ?: return@fold acc
            Rect(
                minOf(acc.left, b.left),
                minOf(acc.top, b.top),
                maxOf(acc.right, b.right),
                maxOf(acc.bottom, b.bottom)
            )
        }
    }

    fun sampleBackground(bitmap: Bitmap, rect: Rect): Int {
        val samples = mutableListOf<Int>()
        listOf(rect.left + 2, rect.right - 2).forEach { x ->
            listOf(rect.top + 2, rect.centerY(), rect.bottom - 2).forEach { y ->
                if (x in 0 until bitmap.width && y in 0 until bitmap.height) {
                    samples.add(bitmap.getPixel(x, y))
                }
            }
        }
        return samples.groupBy { it }.maxByOrNull { it.value.size }?.key ?: Color.WHITE
    }

    fun sampleTextColor(bitmap: Bitmap, rect: Rect, bgColor: Int = sampleBackground(bitmap, rect)): Int {
        val cx = rect.centerX()
        val cy = rect.centerY()
        val samples = mutableListOf<Int>()
        for (dx in -4..4) {
            for (dy in -4..4) {
                val px = (cx + dx).coerceIn(0, bitmap.width - 1)
                val py = (cy + dy).coerceIn(0, bitmap.height - 1)
                val pixel = bitmap.getPixel(px, py)
                if (pixel != bgColor) samples.add(pixel)
            }
        }
        return samples.groupBy { it }.maxByOrNull { it.value.size }?.key ?: readableTextColor(bgColor)
    }

    fun luminance(color: Int): Float {
        return (0.299f * Color.red(color) +
                0.587f * Color.green(color) +
                0.114f * Color.blue(color)) / 255f
    }

    private fun readableTextColor(bgColor: Int): Int {
        return if (luminance(bgColor) > 0.5f) Color.BLACK else Color.WHITE
    }

    fun estimateAlignment(block: Text.TextBlock): Alignment {
        val bounds = block.boundingBox ?: return Alignment.LEFT
        val blockLeft = bounds.left
        val blockWidth = bounds.width()
        if (blockWidth <= 0) return Alignment.LEFT
        val lineCenters = block.lines.mapNotNull { line ->
            val b = line.boundingBox ?: return@mapNotNull null
            b.left + b.width() / 2
        }
        if (lineCenters.isEmpty()) return Alignment.LEFT
        val avgCenter = lineCenters.average()
        val blockCenter = blockLeft + blockWidth / 2.0
        return when {
            kotlin.math.abs(avgCenter - blockCenter) < blockWidth * 0.1 -> Alignment.CENTER
            avgCenter > blockCenter -> Alignment.RIGHT
            else -> Alignment.LEFT
        }
    }
}
