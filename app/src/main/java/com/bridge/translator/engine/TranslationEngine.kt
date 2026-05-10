package com.bridge.translator.engine

import android.graphics.Bitmap
import android.graphics.Rect
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions

object TranslationEngine {

    data class LangOption(val label: String, val code: String)

    /** One detected text region with its screen-coordinate bounding box. */
    data class TextBlock(
        val text: String,
        val bounds: Rect
    )

    val LANGUAGES: List<LangOption> = listOf(
        LangOption("English",    TranslateLanguage.ENGLISH),
        LangOption("Spanish",    TranslateLanguage.SPANISH),
        LangOption("French",     TranslateLanguage.FRENCH),
        LangOption("German",     TranslateLanguage.GERMAN),
        LangOption("Japanese",   TranslateLanguage.JAPANESE),
        LangOption("Chinese",    TranslateLanguage.CHINESE),
        LangOption("Korean",     TranslateLanguage.KOREAN),
        LangOption("Arabic",     TranslateLanguage.ARABIC),
        LangOption("Hindi",      TranslateLanguage.HINDI),
        LangOption("Portuguese", TranslateLanguage.PORTUGUESE),
        LangOption("Russian",    TranslateLanguage.RUSSIAN),
        LangOption("Italian",    TranslateLanguage.ITALIAN),
        LangOption("Turkish",    TranslateLanguage.TURKISH),
    )

    private val recognizer by lazy {
        TextRecognition.getClient(KoreanTextRecognizerOptions.Builder().build())
    }
    private val translatorLock = Any()
    private val translators = mutableMapOf<String, Translator>()
    private val readyPairs = mutableSetOf<String>()

    /**
     * Runs OCR on [bitmap] and returns one [TextBlock] per ML Kit text block.
     * Lines inside a block are merged so Korean phrases translate as a phrase
     * instead of separate word fragments.
     */
    fun recognizeBlocks(bitmap: Bitmap, onResult: (List<TextBlock>) -> Unit) {
        recognizer.process(InputImage.fromBitmap(bitmap, 0))
            .addOnSuccessListener { visionText ->
                val blocks = mutableListOf<TextBlock>()
                visionText.textBlocks.forEach { block ->
                    val text = block.lines.joinToString(" ") { it.text.trim() }.trim()
                    if (text.length <= 1) return@forEach

                    val firstBounds = block.boundingBox ?: block.lines.firstOrNull()?.boundingBox
                    var mergedBounds = firstBounds?.let { Rect(it) } ?: return@forEach
                    block.lines.forEach { line ->
                        line.boundingBox?.let { bounds ->
                            mergedBounds = Rect(
                                minOf(mergedBounds.left, bounds.left),
                                minOf(mergedBounds.top, bounds.top),
                                maxOf(mergedBounds.right, bounds.right),
                                maxOf(mergedBounds.bottom, bounds.bottom)
                            )
                        }
                    }
                    blocks.add(TextBlock(text, mergedBounds))
                }
                onResult(blocks)
            }
            .addOnFailureListener { onResult(emptyList()) }
    }

    /**
     * Identifies the source language automatically, then translates [text] to [targetCode].
     * Falls back to English when identification fails or returns "und".
     * Callbacks fire on the main thread.
     */
    fun detectAndTranslate(
        text: String,
        targetCode: String,
        onResult: (sourceCode: String, translated: String) -> Unit,
        onError: (String) -> Unit
    ) {
        val identifier = LanguageIdentification.getClient()
        identifier.identifyLanguage(text)
            .addOnSuccessListener { langCode ->
                identifier.close()
                val source = if (langCode == "und" || langCode.isBlank()) TranslateLanguage.ENGLISH else langCode
                if (source == targetCode) { onResult(source, text); return@addOnSuccessListener }
                translate(text, source, targetCode,
                    onResult = { translated -> onResult(source, translated) },
                    onError  = onError)
            }
            .addOnFailureListener {
                identifier.close()
                translate(text, TranslateLanguage.ENGLISH, targetCode,
                    onResult = { translated -> onResult(TranslateLanguage.ENGLISH, translated) },
                    onError  = onError)
            }
    }

    /** Downloads the ML Kit translation model if needed, then translates. Callbacks on main thread. */
    fun translate(
        text: String,
        sourceCode: String,
        targetCode: String,
        wifiOnly: Boolean = false,
        onDownloading: () -> Unit = {},
        onResult: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val pairKey = "${sourceCode.lowercase()}:${targetCode.lowercase()}"
        val translator = try {
            synchronized(translatorLock) {
                translators.getOrPut(pairKey) {
                    Translation.getClient(
                        TranslatorOptions.Builder()
                            .setSourceLanguage(sourceCode)
                            .setTargetLanguage(targetCode)
                            .build()
                    )
                }
            }
        } catch (e: IllegalArgumentException) {
            onError("Language pair is not supported")
            return
        }
        val conditions = if (wifiOnly) {
            DownloadConditions.Builder().requireWifi().build()
        } else {
            DownloadConditions.Builder().build()
        }
        if (synchronized(translatorLock) { pairKey !in readyPairs }) onDownloading()
        translator.downloadModelIfNeeded(conditions)
            .addOnSuccessListener {
                synchronized(translatorLock) { readyPairs.add(pairKey) }
                translator.translate(text)
                    .addOnSuccessListener { translated -> onResult(translated) }
                    .addOnFailureListener { e -> onError("Translation failed: ${e.message}") }
            }
            .addOnFailureListener {
                val message = if (wifiOnly) {
                    "Connect to Wi-Fi to download this model"
                } else {
                    "Connect to the internet to download this model"
                }
                onError(message)
            }
    }

    fun close() {
        synchronized(translatorLock) {
            translators.values.forEach { it.close() }
            translators.clear()
            readyPairs.clear()
        }
    }
}
