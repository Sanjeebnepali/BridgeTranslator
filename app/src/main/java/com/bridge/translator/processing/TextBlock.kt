package com.bridge.translator.processing

import android.graphics.Paint
import android.graphics.Rect

data class TextBlock(
    val originalText: String,
    var translatedText: String = "",
    val rect: Rect,
    val textColor: Int,
    val bgColor: Int,
    val estimatedFontSize: Float,
    val alignment: Alignment,
    val isBold: Boolean,
    val detectedLang: String
)

enum class Alignment {
    LEFT,
    CENTER,
    RIGHT;

    fun toPaintAlign(): Paint.Align = when (this) {
        CENTER -> Paint.Align.CENTER
        RIGHT -> Paint.Align.RIGHT
        LEFT -> Paint.Align.LEFT
    }
}
