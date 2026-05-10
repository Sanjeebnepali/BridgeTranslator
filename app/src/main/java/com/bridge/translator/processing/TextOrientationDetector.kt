package com.bridge.translator.processing

import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import com.google.mlkit.vision.text.Text
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.abs

/**
 * Detects text orientation (horizontal, vertical, rotated) from ML Kit OCR blocks.
 *
 * Handles:
 * - Text angle calculation from bounding box corners
 * - Orientation classification (HORIZONTAL/VERTICAL/ROTATED)
 * - Confidence scoring based on text characteristics
 * - Bitmap rotation for vertical text
 *
 * Usage:
 * ```
 * val detector = TextOrientationDetector()
 * val orientation = detector.detectOrientation(textBlock)
 * val (rotatedBitmap, angle) = detector.rotateIfNeeded(bitmap, textBlock)
 * ```
 */
class TextOrientationDetector {

    companion object {
        private const val TAG = "TextOrientationDetector"

        // Angle thresholds (in degrees)
        private const val HORIZONTAL_THRESHOLD = 10f
        private const val VERTICAL_LOWER = 80f
        private const val VERTICAL_UPPER = 100f
        private const val VERTICAL_LOWER_OPPOSITE = 260f
        private const val VERTICAL_UPPER_OPPOSITE = 280f
    }

    /**
     * Result of orientation detection
     */
    data class OrientationResult(
        val text: Text.TextBlock,
        val angle: Float,                    // -180 to 180 degrees
        val orientation: Orientation,
        val confidence: Float,               // 0.0 to 1.0
        val shouldRotate: Boolean,
        val rotatedBitmap: Bitmap?,
        val rotationAngle: Float             // Actual rotation applied (0, 90, -90)
    )

    /**
     * Text orientation classification
     */
    enum class Orientation {
        HORIZONTAL,  // 0° ± threshold
        VERTICAL,    // 90° or 270° ± threshold
        ROTATED      // Any other angle
    }

    /**
     * Detect text orientation from ML Kit text block
     *
     * @param textBlock ML Kit recognized text block with bounding box
     * @return Orientation classification
     */
    fun detectOrientation(textBlock: Text.TextBlock): Orientation {
        val angle = calculateAngle(textBlock)
        return classifyAngle(angle)
    }

    /**
     * Calculate confidence score for orientation detection
     *
     * Factors:
     * - Text length (longer = higher confidence)
     * - Line straightness (measure deviation from ideal line)
     * - Character consistency (uniform sizing)
     *
     * @param textBlock ML Kit text block
     * @return Confidence score (0.0 to 1.0)
     */
    fun calculateConfidence(textBlock: Text.TextBlock): Float {
        val text = textBlock.text
        val corners = textBlock.cornerPoints ?: return 0.5f

        if (corners.size < 2 || text.isEmpty()) return 0.3f

        // Factor 1: Text length (longer text = higher confidence)
        // Normalize to 0-1: 0 chars = 0.0, 10+ chars = 1.0
        val lengthConfidence = (text.length.toFloat() / 10f).coerceIn(0f, 1f)

        // Factor 2: Line straightness
        // Calculate deviation from ideal line between corners
        val straightnessConfidence = calculateLinesStraightness(corners)

        // Factor 3: Character consistency
        // Compare character sizes (should be uniform)
        val consistencyConfidence = calculateCharacterConsistency(textBlock)

        // Weighted average
        return (
            lengthConfidence * 0.4f +
            straightnessConfidence * 0.35f +
            consistencyConfidence * 0.25f
        )
    }

    /**
     * Rotate bitmap if text is significantly vertical
     *
     * Only rotates for clearly vertical text (80-100° or 260-280°)
     * This helps with OCR accuracy on vertical text
     *
     * @param bitmap Image containing the text
     * @param textBlock ML Kit text block
     * @return Pair of (bitmap, rotationAngle applied)
     */
    fun rotateIfNeeded(
        bitmap: Bitmap,
        textBlock: Text.TextBlock
    ): Pair<Bitmap, Float> {
        val orientation = detectOrientation(textBlock)

        // Only rotate if clearly vertical
        if (orientation != Orientation.VERTICAL) {
            return Pair(bitmap, 0f)
        }

        val angle = calculateAngle(textBlock)

        // Determine rotation direction and amount
        val rotationAngle = when {
            // Vertical: 80-100°, rotate -90° (counter-clockwise)
            angle in VERTICAL_LOWER..VERTICAL_UPPER -> -90f

            // Vertical opposite: 260-280°, rotate 90° (clockwise)
            angle in VERTICAL_LOWER_OPPOSITE..VERTICAL_UPPER_OPPOSITE -> 90f

            else -> 0f
        }

        if (rotationAngle == 0f) {
            return Pair(bitmap, 0f)
        }

        // Perform rotation
        return try {
            val rotated = rotateBitmap(bitmap, rotationAngle)
            Log.d(TAG, "Rotated bitmap by $rotationAngle degrees")
            Pair(rotated, rotationAngle)
        } catch (e: Exception) {
            Log.e(TAG, "Error rotating bitmap: ${e.message}", e)
            Pair(bitmap, 0f)  // Return original on error
        }
    }

    /**
     * Full orientation detection with all metadata
     */
    fun detectFull(
        bitmap: Bitmap,
        textBlock: Text.TextBlock
    ): OrientationResult {
        val orientation = detectOrientation(textBlock)
        val angle = calculateAngle(textBlock)
        val confidence = calculateConfidence(textBlock)
        val (rotatedBitmap, rotationApplied) = rotateIfNeeded(bitmap, textBlock)
        val shouldRotate = rotationApplied != 0f

        return OrientationResult(
            text = textBlock,
            angle = angle,
            orientation = orientation,
            confidence = confidence,
            shouldRotate = shouldRotate,
            rotatedBitmap = rotatedBitmap,
            rotationAngle = rotationApplied
        )
    }

    // ==================== Private Helper Functions ====================

    /**
     * Calculate text angle from bounding box corners using atan2
     *
     * @param textBlock ML Kit text block with cornerPoints
     * @return Angle in degrees (-180 to 180)
     */
    private fun calculateAngle(textBlock: Text.TextBlock): Float {
        val corners = textBlock.cornerPoints ?: return 0f
        if (corners.size < 2) return 0f

        // Use first two corners to calculate angle
        val p1 = corners[0]
        val p2 = corners[1]

        val dx = (p2.x - p1.x).toFloat()
        val dy = (p2.y - p1.y).toFloat()

        // Calculate angle using atan2 (gives -π to π)
        val angleRad = atan2(dy, dx)
        var angleDeg = Math.toDegrees(angleRad.toDouble()).toFloat()

        // Normalize to -180 to 180 range
        while (angleDeg > 180f) angleDeg -= 360f
        while (angleDeg < -180f) angleDeg += 360f

        return angleDeg
    }

    /**
     * Classify angle into orientation bucket
     */
    private fun classifyAngle(angle: Float): Orientation {
        // Normalize to 0-360 for easier classification
        val normalized = if (angle < 0) angle + 360f else angle

        return when {
            // Horizontal: ~0° or ~180°
            (normalized >= 360f - HORIZONTAL_THRESHOLD || normalized <= HORIZONTAL_THRESHOLD) ||
            (normalized >= 180f - HORIZONTAL_THRESHOLD && normalized <= 180f + HORIZONTAL_THRESHOLD) ->
                Orientation.HORIZONTAL

            // Vertical: ~90° or ~270°
            (normalized >= VERTICAL_LOWER && normalized <= VERTICAL_UPPER) ||
            (normalized >= VERTICAL_LOWER_OPPOSITE && normalized <= VERTICAL_UPPER_OPPOSITE) ->
                Orientation.VERTICAL

            // Everything else
            else -> Orientation.ROTATED
        }
    }

    /**
     * Calculate how straight the line is (for straightness confidence)
     *
     * Measures deviation from ideal line connecting first and last corners
     */
    private fun calculateLinesStraightness(corners: Array<android.graphics.Point>): Float {
        if (corners.size < 3) return 1.0f

        try {
            val firstPoint = corners[0]
            val lastPoint = corners[corners.size - 1]

            // Calculate ideal line distance
            val dx = lastPoint.x - firstPoint.x
            val dy = lastPoint.y - firstPoint.y
            val lineLength = kotlin.math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()

            if (lineLength == 0f) return 1.0f

            // Measure max deviation from line
            var maxDeviation = 0f
            for (i in 1 until corners.size - 1) {
                val point = corners[i]
                // Distance from point to line
                val deviation = abs(
                    (dy * point.x - dx * point.y + lastPoint.x * firstPoint.y - lastPoint.y * firstPoint.x) /
                    lineLength
                )
                maxDeviation = maxOf(maxDeviation, deviation)
            }

            // Normalize: 0 deviation = 1.0 confidence, >10px deviation = 0.0
            val straightness = (1f - (maxDeviation / 10f)).coerceIn(0f, 1f)
            return straightness
        } catch (e: Exception) {
            Log.w(TAG, "Error calculating straightness: ${e.message}")
            return 0.5f
        }
    }

    /**
     * Calculate character consistency confidence
     *
     * Checks if characters are uniformly sized
     */
    private fun calculateCharacterConsistency(textBlock: Text.TextBlock): Float {
        try {
            val lines = textBlock.lines
            if (lines.isEmpty()) return 0.5f

            val heights = mutableListOf<Float>()

            // Collect character/line heights
            for (line in lines) {
                val bbox = line.boundingBox ?: continue
                heights.add(bbox.height().toFloat())
            }

            if (heights.isEmpty()) return 0.5f

            // Calculate standard deviation
            val mean = heights.average().toFloat()
            val variance = heights.map { (it - mean) * (it - mean) }.average()
            val stdDev = kotlin.math.sqrt(variance).toFloat()

            // Consistency: lower stdDev = higher consistency
            // If stdDev is 0, perfect consistency (1.0)
            // If stdDev is > mean, poor consistency (0.0)
            val consistency = (1f - (stdDev / mean)).coerceIn(0f, 1f)
            return consistency
        } catch (e: Exception) {
            Log.w(TAG, "Error calculating consistency: ${e.message}")
            return 0.5f
        }
    }

    /**
     * Rotate bitmap by specified angle
     */
    private fun rotateBitmap(bitmap: Bitmap, angle: Float): Bitmap {
        return try {
            val matrix = Matrix().apply {
                postRotate(
                    angle,
                    bitmap.width / 2f,
                    bitmap.height / 2f
                )
            }

            val rotated = Bitmap.createBitmap(
                bitmap,
                0, 0,
                bitmap.width, bitmap.height,
                matrix,
                true
            )

            // Recycle original if different from rotated
            if (rotated != bitmap) {
                bitmap.recycle()
            }

            rotated
        } catch (e: Exception) {
            Log.e(TAG, "Error rotating bitmap: ${e.message}", e)
            bitmap  // Return original on error
        }
    }
}
