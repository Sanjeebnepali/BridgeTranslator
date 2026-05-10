package com.example.bridgetranslator

import android.graphics.Point
import kotlin.math.atan2

data class OrientationResult(
    val orientation: TextOrientation,
    val angle: Float,
    val confidence: Float
)

class OrientationDetector {
    
    fun detectOrientation(cornerPoints: Array<Point>?, text: String): OrientationResult {
        if (cornerPoints == null || cornerPoints.size < 4) {
            return OrientationResult(TextOrientation.HORIZONTAL, 0f, 0.5f)
        }

        // Using Top-left (0) and Top-right (1)
        val tl = cornerPoints[0]
        val tr = cornerPoints[1]
        
        val dx = (tr.x - tl.x).toDouble()
        val dy = (tr.y - tl.y).toDouble()
        
        var angle = Math.toDegrees(atan2(dy, dx)).toFloat()
        if (angle < 0) {
            angle += 360f
        }

        val orientation = classifyAngle(angle)
        
        // Confidence calculation
        val lengthConfidence = minOf(text.length / 10f, 1.0f) * 0.4f
        val isIdealAngle = isIdealAngle(angle, orientation)
        val angleConfidence = if (isIdealAngle) 0.6f else 0.4f
        
        val confidence = lengthConfidence + angleConfidence

        return OrientationResult(orientation, angle, confidence)
    }

    private fun classifyAngle(angle: Float): TextOrientation {
        return when {
            // Horizontal (around 0/360 or 180)
            (angle in 0f..10f) || (angle in 350f..360f) -> TextOrientation.HORIZONTAL
            (angle in 170f..190f) -> TextOrientation.HORIZONTAL
            // Vertical (around 90 or 270)
            (angle in 80f..100f) -> TextOrientation.VERTICAL
            (angle in 260f..280f) -> TextOrientation.VERTICAL
            else -> TextOrientation.ROTATED
        }
    }
    
    private fun isIdealAngle(angle: Float, orientation: TextOrientation): Boolean {
        return when (orientation) {
            TextOrientation.HORIZONTAL -> (angle in 0f..5f) || (angle in 355f..360f) || (angle in 175f..185f)
            TextOrientation.VERTICAL -> (angle in 85f..95f) || (angle in 265f..275f)
            TextOrientation.ROTATED -> false
        }
    }
}
