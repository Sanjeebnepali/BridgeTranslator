package com.bridge.translator.overlay

import android.graphics.Rect
import com.bridge.translator.analysis.Alignment

data class TranslatedBlock(
    val originalRect: Rect,
    val translatedText: String,
    val bgColor: Int,
    val textColor: Int,
    val fontSize: Float,
    val alignment: Alignment
)
