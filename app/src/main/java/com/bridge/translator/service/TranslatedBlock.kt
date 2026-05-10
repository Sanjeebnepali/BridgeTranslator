package com.bridge.translator.service

import android.graphics.Rect

data class TranslatedBlock(
    val originalRect: Rect,
    val translatedText: String,
    val bgColor: Int,
    val sourceLang: String,
    val targetLang: String
)
