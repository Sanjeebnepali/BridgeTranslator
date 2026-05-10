package com.bridge.translator.camera

import android.graphics.PointF
import android.graphics.RectF
import com.bridge.translator.camera.data.TextOrientation
import com.bridge.translator.camera.data.TextOrientationBlock
import com.google.mlkit.vision.text.Text
import kotlin.math.atan2
import kotlin.math.hypot
import kotlin.math.sqrt

/**
 * Module 1 – Text Orientation Detection.
 *
 * Uses ML Kit corner points (4-point bounding box) and atan2 to calculate the
 * angle of each text block.  Classifies each block as HORIZONTAL, VERTICAL_UP,
 * VERTICAL_DOWN, ROTATED, or CURVED.
 *
 * Target: >95 % accuracy, <100 ms per frame.
 */
object OrientationDetector {

    // ── Public API ─────────────────────────────────────────────────────────────

    /**
     * Analyse [visionText] returned by ML Kit and produce an [TextOrientationBlock]
     * for every detected text block.
     *
     * @param visionText  ML Kit [Text] result.
     * @param bitmapWidth  Width  of the analysed bitmap (for font-size estimation).
     * @param bitmapHeight Height of the analysed bitmap.
     */
    fun detect(
        visionText: Text,
        bitmapWidth: Int,
        bitmapHeight: Int
    ): List<TextOrientationBlock> {
        return visionText.textBlocks.mapNotNull { block ->
            processBlock(block, bitmapWidth, bitmapHeight)
        }
    }

    // ── Per-block processing ───────────────────────────────────────────────────

    private fun processBlock(
        block: Text.TextBlock,
        bitmapWidth: Int,
        bitmapHeight: Int
    ): TextOrientationBlock? {
        val mergedText = block.lines.joinToString(" ") { it.text }.trim()
        if (mergedText.isBlank()) return null

        val cornerPoints = block.cornerPoints
        val angle = if (cornerPoints != null && cornerPoints.size == 4) {
            calculateAngle(cornerPoints)
        } else {
            block.boundingBox?.let { r ->
                0f // fallback: horizontal
            } ?: 0f
        }

        val orientation = classifyAngle(angle)
        val confidence  = calculateOrientationConfidence(block, angle)
        val bounds      = block.boundingBox?.let { r ->
            RectF(r.left.toFloat(), r.top.toFloat(), r.right.toFloat(), r.bottom.toFloat())
        } ?: RectF()

        val fontPt = estimateFontSizePt(block, bitmapHeight)

        return TextOrientationBlock(
            text                   = mergedText,
            boundingBox            = bounds,
            angle                  = angle,
            orientation            = orientation,
            orientationConfidence  = confidence,
            estimatedFontSizePt    = fontPt,
            ocrConfidence          = inferOcrConfidence(block)
        )
    }

    // ── Angle calculation ─────────────────────────────────────────────────────

    /**
     * Calculate the dominant angle from the top edge (top-left → top-right)
     * of the corner-point quad, in degrees clockwise from horizontal.
     *
     * ML Kit corner points order: [top-left, top-right, bottom-right, bottom-left].
     */
    private fun calculateAngle(points: Array<android.graphics.Point>): Float {
        val tl = points[0]; val tr = points[1]
        val dx = (tr.x - tl.x).toFloat()
        val dy = (tr.y - tl.y).toFloat()
        // atan2 returns angle in radians in (-π, π]; convert to degrees in [0, 360)
        var deg = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
        if (deg < 0f) deg += 360f
        return deg
    }

    // ── Orientation classification ─────────────────────────────────────────────

    /**
     * Classify angle into one of five orientations.
     *
     * Horizontal:    angle ∈ [-10°, 10°] ∪ [170°, 190°] ∪ [350°, 360°]
     * Vertical-up:   angle ∈ [80°, 100°]
     * Vertical-down: angle ∈ [260°, 280°]
     * Rotated:       anything else
     */
    fun classifyAngle(angle: Float): TextOrientation {
        val a = ((angle % 360f) + 360f) % 360f
        return when {
            a <= 10f || a >= 350f                          -> TextOrientation.HORIZONTAL
            a in 170f..190f                                -> TextOrientation.HORIZONTAL
            a in 80f..100f                                 -> TextOrientation.VERTICAL_UP
            a in 260f..280f                                -> TextOrientation.VERTICAL_DOWN
            else                                           -> TextOrientation.ROTATED
        }
    }

    // ── Confidence scoring ─────────────────────────────────────────────────────

    /**
     * Estimate orientation confidence based on:
     * 1. Text length  – longer text → higher confidence.
     * 2. Line straightness – how well the bottom points line up.
     * 3. Character size uniformity – variance of character heights.
     */
    private fun calculateOrientationConfidence(block: Text.TextBlock, angle: Float): Float {
        var score = 0f

        // 1. Text length contribution (up to 0.4)
        val len = block.lines.sumOf { it.text.length }
        score += (minOf(len, 20).toFloat() / 20f) * 0.4f

        // 2. Line straightness (up to 0.4): measure vertical deviation among line tops
        val lineTops = block.lines.mapNotNull { it.boundingBox?.top?.toFloat() }
        if (lineTops.size >= 2) {
            val variance = variance(lineTops)
            val avgBoxH  = block.boundingBox?.height()?.toFloat() ?: 1f
            val normVar  = variance / (avgBoxH * avgBoxH).coerceAtLeast(1f)
            score += (1f - normVar.coerceIn(0f, 1f)) * 0.4f
        } else {
            score += 0.4f
        }

        // 3. Angle sharpness (up to 0.2): how close to a cardinal direction
        val a = ((angle % 360f) + 360f) % 360f
        val toNearest90 = minOf(a % 90f, 90f - (a % 90f))
        score += (1f - toNearest90 / 45f).coerceIn(0f, 1f) * 0.2f

        return score.coerceIn(0f, 1f)
    }

    // ── Font size estimation ───────────────────────────────────────────────────

    /**
     * Rough font-size estimation in pt.
     *
     * Assumption: at 50 cm, a 12pt font occupies ~20 px on a 1080p sensor.
     * We use block average line height as a proxy; the result is an *estimate*
     * calibrated via distance (Module 6) when available.
     */
    fun estimateFontSizePt(block: Text.TextBlock, bitmapHeight: Int): Float {
        val avgLineHeight = block.lines.mapNotNull { it.boundingBox?.height()?.toFloat() }
            .average().toFloat()
        // Normalize: assume 1080p reference height
        val scale = 1080f / bitmapHeight.coerceAtLeast(1)
        val normalizedHeight = avgLineHeight * scale
        // Empirical: 20 px @ 1080p ≈ 12 pt at 50 cm
        return (normalizedHeight / 20f * 12f).coerceAtLeast(4f)
    }

    // ── OCR confidence inference ───────────────────────────────────────────────

    /**
     * ML Kit does not expose per-block confidence in a single field.
     * We infer it from text quality indicators.
     */
    private fun inferOcrConfidence(block: Text.TextBlock): Float {
        val text = block.lines.joinToString(" ") { it.text }
        val hasAlpha  = text.any { it.isLetter() }
        val noiseRatio = text.count { !it.isLetterOrDigit() && !it.isWhitespace() }.toFloat() /
                text.length.toFloat().coerceAtLeast(1f)
        return if (hasAlpha) (1f - noiseRatio * 0.5f).coerceIn(0.5f, 1f) else 0.4f
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    private fun variance(values: List<Float>): Float {
        if (values.isEmpty()) return 0f
        val mean = values.average().toFloat()
        return values.sumOf { ((it - mean) * (it - mean)).toDouble() }.toFloat() / values.size
    }
}
