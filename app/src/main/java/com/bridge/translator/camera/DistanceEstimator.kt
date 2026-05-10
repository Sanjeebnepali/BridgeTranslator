package com.bridge.translator.camera

import android.graphics.Bitmap
import com.bridge.translator.camera.data.DistanceReport
import com.bridge.translator.camera.data.DistanceZone
import com.bridge.translator.camera.data.QualityReport
import com.bridge.translator.camera.data.QualityZone
import com.bridge.translator.camera.data.TextOrientationBlock
import kotlin.math.sqrt

/**
 * Module 6 – Distance Estimation and Quality Assessment.
 *
 * Distance estimation:
 *   Formula: distance = (referenceHeightMm x focalLengthPx) / measuredHeightPx
 *   Reference: text at 50 cm → average character height ~20 px at 1080p.
 *   Confidence: MEDIUM (heuristic based) until object detection is integrated.
 *
 * Quality score (0-100):
 *   score = ocrConfidence*0.35 + sharpness*0.25 + distanceBucket*0.20
 *           + contrast*0.10 + stability*0.10
 *
 * Target: distance accuracy ±10 cm; quality–OCR correlation >0.85.
 */
object DistanceEstimator {

    // Reference calibration: at 50 cm, a 12pt character is ~20 px at 1080p
    private const val REFERENCE_HEIGHT_PX = 20f   // px at 1080p
    private const val REFERENCE_DIST_CM   = 50f   // cm
    private const val REFERENCE_HEIGHT_PT = 12f   // pt

    // ── Distance ──────────────────────────────────────────────────────────────

    /**
     * Estimate the distance from the camera to the text surface, using the
     * average character height of all detected text blocks.
     *
     * @param blocks       Detected text blocks (with bounding boxes).
     * @param bitmapHeight Height of the analysed bitmap in pixels.
     * @return             [DistanceReport] with estimated distance and user feedback.
     */
    fun estimateDistance(
        blocks: List<TextOrientationBlock>,
        bitmapHeight: Int
    ): DistanceReport {
        if (blocks.isEmpty()) return unknownDistance()

        // Average line height across all blocks
        val avgHeightPx = blocks.mapNotNull {
            val h = it.boundingBox.height()
            if (h > 0) h else null
        }.average().toFloat()

        if (avgHeightPx <= 0f) return unknownDistance()

        // Scale to 1080p reference
        val scaledHeight = avgHeightPx * (1080f / bitmapHeight.coerceAtLeast(1))

        // Distance formula: inversely proportional to measured height
        val estimatedCm = (REFERENCE_HEIGHT_PX * REFERENCE_DIST_CM) / scaledHeight

        val zone = classifyZone(estimatedCm)
        return DistanceReport(
            estimatedCm = estimatedCm,
            zone        = zone,
            confidence  = 0.65f,   // heuristic only
            message     = zoneMessage(zone, estimatedCm)
        )
    }

    private fun classifyZone(cm: Float): DistanceZone = when {
        cm < 20f         -> DistanceZone.TOO_CLOSE
        cm <= 100f       -> DistanceZone.OPTIMAL
        cm <= 150f       -> DistanceZone.ACCEPTABLE
        else             -> DistanceZone.TOO_FAR
    }

    private fun zoneMessage(zone: DistanceZone, cm: Float): String = when (zone) {
        DistanceZone.TOO_CLOSE   -> "Too close (${cm.toInt()} cm) — move back"
        DistanceZone.OPTIMAL     -> "Perfect distance (${cm.toInt()} cm)"
        DistanceZone.ACCEPTABLE  -> "Good distance (${cm.toInt()} cm) — slight quality drop"
        DistanceZone.TOO_FAR     -> "Too far (${cm.toInt()} cm) — move closer"
    }

    private fun unknownDistance() = DistanceReport(
        estimatedCm = -1f,
        zone        = DistanceZone.OPTIMAL,
        confidence  = 0f,
        message     = "Hold steady for distance measurement"
    )

    // ── Quality assessment ─────────────────────────────────────────────────────

    /**
     * Compute a composite quality score (0–100) for the current frame.
     *
     * @param bitmap         The camera frame bitmap (used for sharpness + contrast).
     * @param blocks         Detected text blocks.
     * @param distance       Distance report from [estimateDistance].
     * @param prevBitmap     Previous frame (for stability calculation); null on first frame.
     */
    fun assessQuality(
        bitmap: Bitmap,
        blocks: List<TextOrientationBlock>,
        distance: DistanceReport,
        prevBitmap: Bitmap? = null
    ): QualityReport {
        val ocrConf    = if (blocks.isEmpty()) 0f
                         else blocks.map { it.ocrConfidence }.average().toFloat()

        val sharpness  = sharpnessScore(bitmap)
        val distScore  = distanceScore(distance.zone)
        val contrast   = contrastScore(bitmap)
        val stability  = if (prevBitmap != null) stabilityScore(bitmap, prevBitmap) else 1f

        val composite = (ocrConf    * 0.35f +
                         sharpness  * 0.25f +
                         distScore  * 0.20f +
                         contrast   * 0.10f +
                         stability  * 0.10f) * 100f

        val zone = when {
            composite >= 80f -> QualityZone.EXCELLENT
            composite >= 60f -> QualityZone.GOOD
            else             -> QualityZone.POOR
        }

        return QualityReport(
            score          = composite,
            ocrConfidence  = ocrConf,
            sharpnessScore = sharpness,
            distanceScore  = distScore,
            contrastScore  = contrast,
            stabilityScore = stability,
            zone           = zone,
            feedback       = qualityFeedback(zone, composite)
        )
    }

    // ── Component scores ───────────────────────────────────────────────────────

    /**
     * Laplacian variance as a sharpness indicator.
     * >500 = sharp (score 1.0), 100–500 = moderate, <100 = blurry (score 0.0).
     */
    private fun sharpnessScore(bitmap: Bitmap): Float {
        val variance = SmallTextEnhancer.measureSharpness(bitmap)
        return when {
            variance >= 500f -> 1.0f
            variance >= 100f -> (variance - 100f) / 400f
            else             -> 0.0f
        }
    }

    private fun distanceScore(zone: DistanceZone): Float = when (zone) {
        DistanceZone.OPTIMAL     -> 1.0f
        DistanceZone.ACCEPTABLE  -> 0.9f
        DistanceZone.TOO_CLOSE   -> 0.5f
        DistanceZone.TOO_FAR     -> 0.6f
    }

    /**
     * Estimate RMS contrast of the image.
     * High contrast = text is readable; low contrast = washed out or dark.
     */
    private fun contrastScore(bitmap: Bitmap): Float {
        val small  = Bitmap.createScaledBitmap(bitmap, 64, 64, false)
        val pixels = IntArray(64 * 64)
        small.getPixels(pixels, 0, 64, 0, 0, 64, 64)
        small.recycle()

        val greys = pixels.map { p ->
            (0.299f * android.graphics.Color.red(p) +
             0.587f * android.graphics.Color.green(p) +
             0.114f * android.graphics.Color.blue(p))
        }
        val mean = greys.average().toFloat()
        val rms  = sqrt(greys.sumOf { ((it - mean) * (it - mean)).toDouble() } / greys.size)
            .toFloat()

        // RMS 0–50 = low contrast, 50–120 = good, >120 = too harsh
        return when {
            rms >= 50f  && rms <= 120f -> 1.0f
            rms < 50f                  -> rms / 50f
            else                       -> 1f - (rms - 120f) / 135f
        }.coerceIn(0f, 1f)
    }

    /**
     * Frame-to-frame stability using mean absolute pixel difference.
     * Low motion = high stability.
     */
    private fun stabilityScore(current: Bitmap, previous: Bitmap): Float {
        val w = 64; val h = 64
        val cur  = Bitmap.createScaledBitmap(current,  w, h, false)
        val prev = Bitmap.createScaledBitmap(previous, w, h, false)
        val curPx  = IntArray(w * h); cur.getPixels(curPx,  0, w, 0, 0, w, h)
        val prevPx = IntArray(w * h); prev.getPixels(prevPx, 0, w, 0, 0, w, h)
        cur.recycle(); prev.recycle()

        var totalDiff = 0f
        for (i in curPx.indices) {
            val dr = (android.graphics.Color.red(curPx[i])   - android.graphics.Color.red(prevPx[i])).toFloat()
            val dg = (android.graphics.Color.green(curPx[i]) - android.graphics.Color.green(prevPx[i])).toFloat()
            val db = (android.graphics.Color.blue(curPx[i])  - android.graphics.Color.blue(prevPx[i])).toFloat()
            totalDiff += (kotlin.math.abs(dr) + kotlin.math.abs(dg) + kotlin.math.abs(db)) / 3f
        }
        val avgDiff = totalDiff / curPx.size
        // avgDiff 0 = identical, >40 = high motion
        return (1f - avgDiff / 40f).coerceIn(0f, 1f)
    }

    private fun qualityFeedback(zone: QualityZone, score: Float): String = when (zone) {
        QualityZone.EXCELLENT -> "Excellent quality (${score.toInt()}%)"
        QualityZone.GOOD      -> "Good quality (${score.toInt()}%) — hold steady for better results"
        QualityZone.POOR      -> "Poor quality (${score.toInt()}%) — adjust lighting or distance"
    }
}
