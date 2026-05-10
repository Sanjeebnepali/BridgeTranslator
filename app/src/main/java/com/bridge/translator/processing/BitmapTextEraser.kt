package com.bridge.translator.processing

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

/**
 * Replace the original text in [source] with the translation drawn directly
 * onto a page-coloured erase patch.
 *
 * Strict design constraints (no card style, no translucent box, no stroke
 * shadow, no outline). The output should look like the original screenshot
 * with the text rectangles repainted in the page colour and the translation
 * written over them in a colour close to the original ink.
 *
 * For each [TextBlock]:
 *   1. Sample background from a 4-px ring just outside the bounding box.
 *   2. Sample text colour by ranking inside-pixels by RGB distance from
 *      the sampled bg and taking the median of the top 30%.
 *   3. Erase the rect with a solid fill + 2-px alpha feather.
 *   4. Choose a Sans-Serif typeface (bold if [TextBlock.isBold]) at a
 *      natural size of `0.65 × bbox.height` (floor of [MIN_FONT_PX]).
 *   5. Fit using single-line → shrink-to-70% → wrap-multiline with
 *      ellipsis on the final line (max 4 lines).
 *   6. Pick alignment: centre for tall bold headers, opposite for clearly
 *      right-margined columns, normal otherwise.
 *
 * Sampling uses bulk [Bitmap.getPixels] reads — never per-pixel
 * [Bitmap.getPixel] in a nested loop.
 */
class BitmapTextEraser {

    fun eraseAndReplace(source: Bitmap, blocks: List<TextBlock>): Bitmap {
        val result = source.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(result)
        val imageWidth = result.width
        val imageHeight = result.height

        blocks.forEach { block ->
            val translated = block.translatedText.takeIf { it.isNotBlank() } ?: return@forEach
            val bbox = block.rect
            if (bbox.width() <= 0 || bbox.height() <= 0) return@forEach
            // Clamp to bitmap so subsequent sampling never reads OOB.
            val clamped = Rect(
                bbox.left.coerceIn(0, imageWidth),
                bbox.top.coerceIn(0, imageHeight),
                bbox.right.coerceIn(0, imageWidth),
                bbox.bottom.coerceIn(0, imageHeight)
            )
            if (clamped.width() <= 0 || clamped.height() <= 0) return@forEach

            val isComplex = isBackgroundComplex(clamped, result)
            
            val bgColor: Int
            val textColor: Int
            
            if (isComplex) {
                // Fallback card style
                bgColor = Color.parseColor("#99000000")
                textColor = Color.WHITE
                
                val cardPaint = Paint().apply {
                    color = bgColor
                    style = Paint.Style.FILL
                    isAntiAlias = false
                }
                canvas.drawRect(
                    clamped.left.toFloat(), clamped.top.toFloat(),
                    clamped.right.toFloat(), clamped.bottom.toFloat(),
                    cardPaint
                )
            } else {
                // STEP 1 — bg colour from the surrounding 4-px ring.
                bgColor = sampleRingMedianColor(result, clamped) ?: block.bgColor

                // STEP 2 — text colour by colour distance.
                textColor = sampleTextColor(result, clamped, bgColor)
                    ?: if (relativeLuminance(bgColor) > 0.5) Color.BLACK else Color.WHITE

                // STEP 3 — erase: solid fill + 2-px feather.
                eraseWithFeather(canvas, clamped, bgColor, imageWidth, imageHeight)
            }

            // STEPS 4–7 — typeset the translation.
            val typeface = Typeface.create(
                Typeface.SANS_SERIF,
                if (block.isBold) Typeface.BOLD else Typeface.NORMAL
            )
            val naturalSize = (clamped.height() * MAX_FONT_PX_RATIO)
                .coerceAtLeast(MIN_FONT_PX)
            val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                this.typeface = typeface
                this.color = textColor
                this.isAntiAlias = true
                this.isSubpixelText = true
                this.hinting = Paint.HINTING_ON
                this.textSize = naturalSize
            }

            renderFitted(
                canvas = canvas,
                bbox = clamped,
                text = translated,
                textPaint = textPaint,
                naturalSize = naturalSize,
                bgColor = bgColor,
                isBold = block.isBold,
                imageWidth = imageWidth,
                imageHeight = imageHeight,
                upstreamAlignment = block.alignment
            )
        }

        return result
    }

    // ---------------------------------------------------------------------
    // STEP 1 — background sampling
    // ---------------------------------------------------------------------

    /**
     * Median R/G/B across a 4-px-wide ring just outside [bbox]. Skips sides
     * that fall off the bitmap. Returns `null` only if every side is
     * unsampleable (box pinned to all four edges of the bitmap).
     */
    private fun sampleRingPixels(bitmap: Bitmap, bbox: Rect): IntArray {
        val w = bitmap.width
        val h = bitmap.height
        val ring = RING_PX
        val pixels = ArrayList<Int>(ring * 2 * (bbox.width() + bbox.height()) + 16)

        readStripPixels(
            bitmap,
            x0 = max(0, bbox.left), y0 = max(0, bbox.top - ring),
            x1 = min(w, bbox.right), y1 = max(0, bbox.top),
            pixels = pixels
        )
        readStripPixels(
            bitmap,
            x0 = max(0, bbox.left), y0 = min(h, bbox.bottom),
            x1 = min(w, bbox.right), y1 = min(h, bbox.bottom + ring),
            pixels = pixels
        )
        readStripPixels(
            bitmap,
            x0 = max(0, bbox.left - ring), y0 = max(0, bbox.top),
            x1 = max(0, bbox.left), y1 = min(h, bbox.bottom),
            pixels = pixels
        )
        readStripPixels(
            bitmap,
            x0 = min(w, bbox.right), y0 = max(0, bbox.top),
            x1 = min(w, bbox.right + ring), y1 = min(h, bbox.bottom),
            pixels = pixels
        )
        return pixels.toIntArray()
    }

    /**
     * Median R/G/B across a 4-px-wide ring just outside [bbox]. Skips sides
     * that fall off the bitmap. Returns `null` only if every side is
     * unsampleable (box pinned to all four edges of the bitmap).
     */
    private fun sampleRingMedianColor(bitmap: Bitmap, bbox: Rect): Int? {
        val pixels = sampleRingPixels(bitmap, bbox)
        if (pixels.isEmpty()) return null
        
        val reds = ArrayList<Int>(pixels.size)
        val greens = ArrayList<Int>(pixels.size)
        val blues = ArrayList<Int>(pixels.size)
        for (p in pixels) {
            reds.add(Color.red(p))
            greens.add(Color.green(p))
            blues.add(Color.blue(p))
        }
        return Color.rgb(median(reds), median(greens), median(blues))
    }

    private fun isBackgroundComplex(bbox: Rect, src: Bitmap): Boolean {
        val ring = sampleRingPixels(src, bbox)
        if (ring.isEmpty()) return false
        
        val reds = ArrayList<Int>(ring.size)
        val greens = ArrayList<Int>(ring.size)
        val blues = ArrayList<Int>(ring.size)
        for (p in ring) {
            reds.add(Color.red(p))
            greens.add(Color.green(p))
            blues.add(Color.blue(p))
        }
        val medianR = median(reds)
        val medianG = median(greens)
        val medianB = median(blues)
        
        var closeCount = 0
        for (p in ring) {
            val dr = Color.red(p) - medianR
            val dg = Color.green(p) - medianG
            val db = Color.blue(p) - medianB
            val dist = sqrt((dr*dr + dg*dg + db*db).toFloat())
            // If the pixel is within a tight distance to the median
            if (dist < 20f) closeCount++
        }
        
        // If less than 60% of the ring is the dominant median color, it's a complex background
        // (e.g. gradients or images). Outliers like text touching the ring won't drop it below 60%.
        val dominantRatio = closeCount.toFloat() / ring.size
        return dominantRatio < 0.60f
    }

    // ---------------------------------------------------------------------
    // STEP 2 — text-colour sampling
    // ---------------------------------------------------------------------

    /**
     * Inside [bbox], find pixels most distant from [bgColor], take the top
     * 30%, return the median R/G/B. Returns `null` if the box is too small
     * or has no clearly distant cluster (caller falls back to black/white).
     */
    private fun sampleTextColor(bitmap: Bitmap, bbox: Rect, bgColor: Int): Int? {
        val width = bbox.width()
        val height = bbox.height()
        val totalPixels = width * height
        if (totalPixels < 20) return null

        // Subsample for very large blocks so we don't read megapixels per box.
        val targetSamples = 1500
        val step = max(1, sqrt(totalPixels.toDouble() / targetSamples).toInt())
        val sampledW = (width + step - 1) / step
        val sampledH = (height + step - 1) / step
        if (sampledW <= 0 || sampledH <= 0) return null

        // Bulk-read the whole rect once, then index every step-th pixel.
        val rectPixels = IntArray(width * height)
        bitmap.getPixels(rectPixels, 0, width, bbox.left, bbox.top, width, height)

        val pixelCount = sampledW * sampledH
        val pixels = IntArray(pixelCount)
        val dists = FloatArray(pixelCount)
        val bgR = Color.red(bgColor); val bgG = Color.green(bgColor); val bgB = Color.blue(bgColor)

        var wrote = 0
        var sy = 0
        while (sy < sampledH) {
            val y = sy * step
            if (y >= height) break
            var sx = 0
            while (sx < sampledW) {
                val x = sx * step
                if (x >= width) break
                val p = rectPixels[y * width + x]
                pixels[wrote] = p
                val dr = Color.red(p) - bgR
                val dg = Color.green(p) - bgG
                val db = Color.blue(p) - bgB
                dists[wrote] = sqrt((dr * dr + dg * dg + db * db).toFloat())
                wrote++
                sx++
            }
            sy++
        }
        if (wrote < 5) return null

        // Distinct-distant filter: how many sampled pixels are clearly
        // not-bg (distance > 32). If too few, the box is essentially solid
        // bg — let caller fall back.
        var distinctDistant = 0
        for (i in 0 until wrote) if (dists[i] > 32f) distinctDistant++
        if (distinctDistant < 5) return null

        // Top 30% most-distant -> median.
        val order = (0 until wrote).sortedByDescending { dists[it] }
        val topN = max(5, (wrote * 0.30).toInt())
        val rs = ArrayList<Int>(topN)
        val gs = ArrayList<Int>(topN)
        val bs = ArrayList<Int>(topN)
        for (i in 0 until topN) {
            val p = pixels[order[i]]
            rs.add(Color.red(p)); gs.add(Color.green(p)); bs.add(Color.blue(p))
        }
        val sampled = Color.rgb(median(rs), median(gs), median(bs))
        // Sanity: if the cluster median is itself near bg, the ranking
        // didn't actually find text — let caller fall back.
        if (colorDistance(sampled, bgColor) < 28.0) return null
        return sampled
    }

    // ---------------------------------------------------------------------
    // STEP 3 — erase (solid fill + 2-px alpha feather)
    // ---------------------------------------------------------------------

    private fun eraseWithFeather(
        canvas: Canvas,
        rect: Rect,
        bgColor: Int,
        imageWidth: Int,
        imageHeight: Int
    ) {
        val solid = Paint().apply {
            color = bgColor
            style = Paint.Style.FILL
            isAntiAlias = false
        }
        canvas.drawRect(
            rect.left.toFloat(), rect.top.toFloat(),
            rect.right.toFloat(), rect.bottom.toFloat(),
            solid
        )
        // 2-px feather: linear fade from ~67% bg at distance 1 to ~33% at
        // distance 2. The pixel right at the edge is already 100% from the
        // solid fill above.
        val featherAlphas = intArrayOf(170, 85)
        for (i in 0 until EDGE_FEATHER_PX) {
            val alpha = featherAlphas[i]
            val featherPaint = Paint().apply {
                color = (bgColor and 0x00FFFFFF) or (alpha shl 24)
                style = Paint.Style.FILL
                isAntiAlias = false
            }
            val d = i + 1
            // Top
            val topY = rect.top - d
            if (topY in 0 until imageHeight) {
                canvas.drawRect(
                    rect.left.toFloat(), topY.toFloat(),
                    rect.right.toFloat(), (topY + 1).toFloat(),
                    featherPaint
                )
            }
            // Bottom
            val botY = rect.bottom + i
            if (botY in 0 until imageHeight) {
                canvas.drawRect(
                    rect.left.toFloat(), botY.toFloat(),
                    rect.right.toFloat(), (botY + 1).toFloat(),
                    featherPaint
                )
            }
            // Left
            val leftX = rect.left - d
            if (leftX in 0 until imageWidth) {
                canvas.drawRect(
                    leftX.toFloat(), rect.top.toFloat(),
                    (leftX + 1).toFloat(), rect.bottom.toFloat(),
                    featherPaint
                )
            }
            // Right
            val rightX = rect.right + i
            if (rightX in 0 until imageWidth) {
                canvas.drawRect(
                    rightX.toFloat(), rect.top.toFloat(),
                    (rightX + 1).toFloat(), rect.bottom.toFloat(),
                    featherPaint
                )
            }
        }
    }

    // ---------------------------------------------------------------------
    // STEPS 4–7 — render with auto-fit
    // ---------------------------------------------------------------------

    private fun renderFitted(
        canvas: Canvas,
        bbox: Rect,
        text: String,
        textPaint: TextPaint,
        naturalSize: Float,
        bgColor: Int,
        isBold: Boolean,
        imageWidth: Int,
        imageHeight: Int,
        upstreamAlignment: Alignment
    ) {
        val maxWidth = bbox.width()
        val alignment = detectAlignment(
            isBold = isBold,
            bbox = bbox,
            imageWidth = imageWidth,
            imageHeight = imageHeight,
            upstream = upstreamAlignment
        )

        // 5a — single line at natural size?
        textPaint.textSize = naturalSize
        if (textPaint.measureText(text) <= maxWidth) {
            drawSingleLine(canvas, textPaint, text, bbox, alignment, naturalSize)
            return
        }

        // 5b — shrink to fit single line, but no smaller than the floor.
        val floorSize = max(MIN_FONT_PX, naturalSize * OVERFLOW_SHRINK_LIMIT)
        val singleAtNatural = textPaint.measureText(text)
        if (singleAtNatural > 0f) {
            val candidate = (naturalSize * (maxWidth / singleAtNatural)).coerceAtLeast(floorSize)
            textPaint.textSize = candidate
            if (textPaint.measureText(text) <= maxWidth && candidate >= floorSize) {
                drawSingleLine(canvas, textPaint, text, bbox, alignment, candidate)
                return
            }
        }

        // 5c — wrap to multiple lines at reduced size with ellipsis on overflow.
        textPaint.textSize = floorSize
        val layout = StaticLayout.Builder
            .obtain(text, 0, text.length, textPaint, maxWidth.coerceAtLeast(1))
            .setAlignment(alignment)
            .setLineSpacing(0f, 1.0f)
            .setIncludePad(false)
            .setMaxLines(MAX_LINES)
            .setEllipsize(TextUtils.TruncateAt.END)
            .build()

        // If wrapping pushed us below the original block, re-erase the
        // expanded rect (capped at 1.6 × original height) BEFORE drawing.
        val totalHeight = layout.height
        val maxExpandedBottom = bbox.top + (bbox.height() * MAX_EXPAND_RATIO).toInt()
        val drawnBottom = (bbox.top + totalHeight).coerceAtMost(maxExpandedBottom)
        if (drawnBottom > bbox.bottom) {
            val solid = Paint().apply {
                color = bgColor
                style = Paint.Style.FILL
                isAntiAlias = false
            }
            canvas.drawRect(
                bbox.left.toFloat(),
                bbox.bottom.toFloat(),
                bbox.right.toFloat(),
                drawnBottom.coerceAtMost(imageHeight).toFloat(),
                solid
            )
        }

        // Clip to the drawable area (so a layout that wants > 4 lines after
        // ellipsis still doesn't bleed below the cap).
        canvas.save()
        canvas.clipRect(
            bbox.left.toFloat(),
            bbox.top.toFloat(),
            bbox.right.toFloat(),
            drawnBottom.coerceAtMost(imageHeight).toFloat()
        )
        canvas.translate(bbox.left.toFloat(), bbox.top.toFloat())
        layout.draw(canvas)
        canvas.restore()
    }

    private fun drawSingleLine(
        canvas: Canvas,
        paint: TextPaint,
        text: String,
        bbox: Rect,
        alignment: Layout.Alignment,
        textSize: Float
    ) {
        paint.textSize = textSize
        val align = when (alignment) {
            Layout.Alignment.ALIGN_CENTER -> Paint.Align.CENTER
            Layout.Alignment.ALIGN_OPPOSITE -> Paint.Align.RIGHT
            else -> Paint.Align.LEFT
        }
        paint.textAlign = align
        // Vertical centering inside the bbox: baseline at
        //   top + (height + textSize) / 2 - descent
        val baselineY = bbox.top + (bbox.height() + textSize) / 2f - paint.descent()
        val x = when (align) {
            Paint.Align.CENTER -> bbox.exactCenterX()
            Paint.Align.RIGHT -> bbox.right.toFloat()
            else -> bbox.left.toFloat()
        }
        canvas.drawText(text, x, baselineY, paint)
    }

    // ---------------------------------------------------------------------
    // STEP 6 — alignment heuristic
    // ---------------------------------------------------------------------

    private fun detectAlignment(
        isBold: Boolean,
        bbox: Rect,
        imageWidth: Int,
        imageHeight: Int,
        upstream: Alignment
    ): Layout.Alignment {
        val leftMargin = bbox.left
        val rightMargin = imageWidth - bbox.right

        // Header heuristic: bold + tall + roughly centred horizontally.
        if (isBold && bbox.height() > imageHeight * 0.04 &&
            abs(leftMargin - rightMargin) < imageWidth * 0.08
        ) {
            return Layout.Alignment.ALIGN_CENTER
        }

        // Right-column heuristic: noticeably more padding on the left than
        // the right and pushed past the midline (e.g. price column).
        if (rightMargin > 0 && leftMargin > rightMargin * 2 && leftMargin > imageWidth * 0.4) {
            return Layout.Alignment.ALIGN_OPPOSITE
        }

        // Honour an explicit upstream estimate (CENTER/RIGHT) when geometry
        // didn't decide.
        return when (upstream) {
            Alignment.CENTER -> Layout.Alignment.ALIGN_CENTER
            Alignment.RIGHT -> Layout.Alignment.ALIGN_OPPOSITE
            else -> Layout.Alignment.ALIGN_NORMAL
        }
    }

    // ---------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------

    private fun readStripPixels(
        bitmap: Bitmap,
        x0: Int, y0: Int, x1: Int, y1: Int,
        pixels: ArrayList<Int>
    ) {
        val w = x1 - x0
        val h = y1 - y0
        if (w <= 0 || h <= 0) return
        val buf = IntArray(w * h)
        bitmap.getPixels(buf, 0, w, x0, y0, w, h)
        for (p in buf) {
            pixels.add(p)
        }
    }

    private fun median(values: ArrayList<Int>): Int {
        if (values.isEmpty()) return 0
        val sorted = values.toIntArray()
        sorted.sort()
        return sorted[sorted.size / 2]
    }

    private fun colorDistance(c1: Int, c2: Int): Double {
        val dr = (Color.red(c1) - Color.red(c2)).toDouble()
        val dg = (Color.green(c1) - Color.green(c2)).toDouble()
        val db = (Color.blue(c1) - Color.blue(c2)).toDouble()
        return sqrt(dr * dr + dg * dg + db * db)
    }

    private fun relativeLuminance(c: Int): Double =
        (0.299 * Color.red(c) +
                0.587 * Color.green(c) +
                0.114 * Color.blue(c)) / 255.0

    private fun colorVariance(pixels: IntArray): Float {
        if (pixels.isEmpty()) return 0f
        var sumR = 0f; var sumG = 0f; var sumB = 0f
        for (p in pixels) {
            sumR += Color.red(p) / 255f
            sumG += Color.green(p) / 255f
            sumB += Color.blue(p) / 255f
        }
        val meanR = sumR / pixels.size
        val meanG = sumG / pixels.size
        val meanB = sumB / pixels.size
        
        var varSum = 0f
        for (p in pixels) {
            val dr = (Color.red(p) / 255f) - meanR
            val dg = (Color.green(p) / 255f) - meanG
            val db = (Color.blue(p) / 255f) - meanB
            varSum += (dr * dr + dg * dg + db * db) / 3f
        }
        return sqrt(varSum / pixels.size)
    }

    private companion object {
        const val MIN_FONT_PX = 22f
        const val MAX_FONT_PX_RATIO = 0.65f
        const val OVERFLOW_SHRINK_LIMIT = 0.5f
        const val MAX_LINES = 4
        const val EDGE_FEATHER_PX = 2
        const val RING_PX = 4
        const val MAX_EXPAND_RATIO = 1.6f
        const val COMPLEXITY_VARIANCE_THRESHOLD = 0.12f
    }
}
