package com.example.bridgetranslator

import android.graphics.Point
import android.graphics.Rect

enum class TextOrientation {
    HORIZONTAL,
    VERTICAL,
    ROTATED
}

data class DetectedTextBlock(
    val text: String,
    val bounds: Rect,
    val confidence: Float,
    var language: String? = null,
    var cornerPoints: Array<Point>? = null,
    var angle: Float = 0f,
    var orientation: TextOrientation = TextOrientation.HORIZONTAL,
    var orientationConfidence: Float = 0f
)
