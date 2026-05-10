package com.bridge.translator.analysis

import android.graphics.Rect

enum class Alignment {
    LEFT,
    CENTER,
    RIGHT
}

data class AnalysedBlock(
    val text: String,
    val detectedLang: String,
    val boundingRect: Rect,
    val backgroundColor: Int,
    val textColor: Int,
    val estimatedFontSize: Float,
    val alignment: Alignment
)
