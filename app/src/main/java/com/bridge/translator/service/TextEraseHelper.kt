package com.bridge.translator.service

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import kotlin.math.max
import kotlin.math.min

data class OverlayTextItem(
    val sourceText: String,
    val translatedText: String,
    val eraseRect: Rect,
    val backgroundColor: Int,
    val textColor: Int,
    val textSizePx: Float
)

object TextEraseHelper {

    fun createTranslatedBlock(
        bitmap: Bitmap,
        translatedText: String,
        rawBounds: Rect,
        sourceLang: String,
        targetLang: String
    ): TranslatedBlock {
        val bounds = exactBounds(rawBounds, bitmap.width, bitmap.height)
        val bg = sampleDominantColor(bitmap, bounds)
        return TranslatedBlock(
            originalRect = bounds,
            translatedText = translatedText,
            bgColor = bg,
            sourceLang = sourceLang,
            targetLang = targetLang
        )
    }

    fun createItem(
        bitmap: Bitmap,
        sourceText: String,
        translatedText: String,
        rawBounds: Rect
    ): OverlayTextItem {
        val eraseRect = expandBounds(rawBounds, bitmap.width, bitmap.height)
        val bg = sampleDominantBackground(bitmap, eraseRect)
        return OverlayTextItem(
            sourceText = sourceText,
            translatedText = translatedText,
            eraseRect = eraseRect,
            backgroundColor = bg,
            textColor = readableTextColor(bg),
            textSizePx = textSizeFor(eraseRect)
        )
    }

    fun screenshotHash(bitmap: Bitmap): Long {
        val samples = IntArray(64)
        var total = 0
        var index = 0
        for (gy in 0 until 8) {
            val y = ((gy + 0.5f) * bitmap.height / 8f).toInt().coerceIn(0, bitmap.height - 1)
            for (gx in 0 until 8) {
                val x = ((gx + 0.5f) * bitmap.width / 8f).toInt().coerceIn(0, bitmap.width - 1)
                val c = bitmap.getPixel(x, y)
                val lum = luminance(c)
                samples[index++] = lum
                total += lum
            }
        }
        val avg = total / samples.size
        var hash = 0L
        for (i in samples.indices) {
            if (samples[i] >= avg) hash = hash or (1L shl i)
        }
        return hash
    }

    fun computeHash(bitmap: Bitmap): String {
        val scaled = Bitmap.createScaledBitmap(bitmap, 16, 16, false)
        val sb = StringBuilder(512)
        for (y in 0 until 16) {
            for (x in 0 until 16) {
                val p = scaled.getPixel(x, y)
                val gray = (Color.red(p) * 0.299 +
                        Color.green(p) * 0.587 +
                        Color.blue(p) * 0.114).toInt()
                sb.append(gray.toString(16).padStart(2, '0'))
            }
        }
        scaled.recycle()
        return sb.toString()
    }

    fun isChanged(previous: Long?, current: Long): Boolean {
        if (previous == null) return true
        return java.lang.Long.bitCount(previous xor current) >= 3
    }

    fun drawWrappedText(
        canvas: android.graphics.Canvas,
        text: String,
        rect: Rect,
        paint: Paint
    ) {
        val maxWidth = max(1f, rect.width() - 6f)
        var lines: List<String> = listOf(text)
        var lineHeight = 1f
        var maxLines = 1

        while (true) {
            lines = wrapText(text, paint, maxWidth)
            val metrics = paint.fontMetrics
            lineHeight = metrics.descent - metrics.ascent
            maxLines = max(1, (rect.height() / lineHeight).toInt())
            if (lines.size <= maxLines || paint.textSize <= 6f) break
            paint.textSize -= 1f
        }

        val metrics = paint.fontMetrics
        val visible = lines.take(maxLines)
        var y = rect.top + (rect.height() - visible.size * lineHeight) / 2f - metrics.ascent

        for ((i, line) in visible.withIndex()) {
            val out = if (i == visible.lastIndex && lines.size > visible.size) {
                ellipsize(line, paint, maxWidth)
            } else {
                ellipsize(line, paint, maxWidth)
            }
            canvas.drawText(out, rect.left + 3f, y, paint)
            y += lineHeight
        }
    }

    private fun wrapText(text: String, paint: Paint, maxWidth: Float): List<String> {
        val words = text.split(Regex("\\s+")).filter { it.isNotBlank() }
        if (words.isEmpty()) return listOf(text)
        val lines = mutableListOf<String>()
        var current = ""
        for (word in words) {
            val candidate = if (current.isEmpty()) word else "$current $word"
            if (paint.measureText(candidate) <= maxWidth) {
                current = candidate
            } else {
                if (current.isNotEmpty()) lines.add(current)
                current = word
            }
        }
        if (current.isNotEmpty()) lines.add(current)
        return lines
    }

    private fun expandBounds(bounds: Rect, maxW: Int, maxH: Int): Rect {
        val padX = max(6, (bounds.width() * 0.16f).toInt())
        val padY = max(5, (bounds.height() * 0.60f).toInt())
        return Rect(
            max(0, bounds.left - padX),
            max(0, bounds.top - padY),
            min(maxW, bounds.right + padX),
            min(maxH, bounds.bottom + padY)
        )
    }

    private fun exactBounds(bounds: Rect, maxW: Int, maxH: Int): Rect {
        return Rect(
            bounds.left.coerceIn(0, maxW),
            bounds.top.coerceIn(0, maxH),
            bounds.right.coerceIn(0, maxW),
            bounds.bottom.coerceIn(0, maxH)
        )
    }

    fun sampleDominantColor(bitmap: Bitmap, rect: Rect): Int {
        val samples = mutableListOf<Int>()
        val xs = listOf(rect.left + 4, rect.centerX(), rect.right - 4)
        val ys = listOf(rect.top + 4, rect.centerY(), rect.bottom - 4)
        for (x in xs) {
            for (y in ys) {
                if (x in 0 until bitmap.width && y in 0 until bitmap.height) {
                    samples.add(bitmap.getPixel(x, y))
                }
            }
        }
        return samples.groupBy { it }.maxByOrNull { it.value.size }?.key
            ?: Color.WHITE
    }

    private fun sampleDominantBackground(bitmap: Bitmap, rect: Rect): Int {
        val buckets = HashMap<Int, Int>()
        val stepX = max(1, rect.width() / 12)
        val stepY = max(1, rect.height() / 8)
        for (y in rect.top until rect.bottom step stepY) {
            for (x in rect.left until rect.right step stepX) {
                val c = bitmap.getPixel(x, y)
                if (isLikelyBackground(c)) {
                    val key = quantize(c)
                    buckets[key] = (buckets[key] ?: 0) + 1
                }
            }
        }
        val best = buckets.maxByOrNull { it.value }?.key
        return best?.let { unquantize(it) } ?: Color.WHITE
    }

    private fun sampleBackgroundAroundText(bitmap: Bitmap, rect: Rect): Int {
        val ring = Rect(
            max(0, rect.left - 6),
            max(0, rect.top - 6),
            min(bitmap.width, rect.right + 6),
            min(bitmap.height, rect.bottom + 6)
        )
        val buckets = HashMap<Int, Int>()
        val stepX = max(1, ring.width() / 14)
        val stepY = max(1, ring.height() / 10)
        for (y in ring.top until ring.bottom step stepY) {
            for (x in ring.left until ring.right step stepX) {
                if (rect.contains(x, y)) continue
                val key = quantize(bitmap.getPixel(x, y))
                buckets[key] = (buckets[key] ?: 0) + 1
            }
        }
        val best = buckets.maxByOrNull { it.value }?.key
        return best?.let { unquantize(it) } ?: sampleDominantBackground(bitmap, rect)
    }

    private fun readableTextColor(background: Int): Int {
        return if (luminance(background) >= 150) Color.rgb(22, 22, 22) else Color.WHITE
    }

    private fun textSizeFor(rect: Rect): Float {
        return rect.height().coerceIn(12, 46) * 0.56f
    }

    private fun isLikelyBackground(color: Int): Boolean {
        val lum = luminance(color)
        val saturation = maxOf(Color.red(color), Color.green(color), Color.blue(color)) -
                minOf(Color.red(color), Color.green(color), Color.blue(color))
        return lum >= 145 || saturation < 40
    }

    private fun luminance(color: Int): Int {
        return ((Color.red(color) * 30 + Color.green(color) * 59 + Color.blue(color) * 11) / 100)
    }

    private fun quantize(color: Int): Int {
        val r = Color.red(color) / 24
        val g = Color.green(color) / 24
        val b = Color.blue(color) / 24
        return (r shl 16) or (g shl 8) or b
    }

    private fun unquantize(key: Int): Int {
        val r = ((key shr 16) and 0xff) * 24 + 12
        val g = ((key shr 8) and 0xff) * 24 + 12
        val b = (key and 0xff) * 24 + 12
        return Color.rgb(r.coerceIn(0, 255), g.coerceIn(0, 255), b.coerceIn(0, 255))
    }

    private fun ellipsize(text: String, paint: Paint, maxWidth: Float): String {
        if (paint.measureText(text) <= maxWidth) return text
        var out = text
        while (out.length > 1 && paint.measureText("$out...") > maxWidth) {
            out = out.dropLast(1)
        }
        return "$out..."
    }
}
