package com.bridge.translator.translation

import android.util.Log
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap

class TranslationEngine {

    // ConcurrentHashMap so parallel coroutine calls are safe.
    private val clients = ConcurrentHashMap<String, Translator>()
    // Track which models are already downloaded to skip repeated downloadModelIfNeeded() IPC.
    private val downloadedModels: MutableSet<String> = ConcurrentHashMap.newKeySet()
    private val cache = TranslationCache()

    suspend fun translate(
        text: String,
        sourceLang: String,
        targetLang: String
    ): String? = withContext(Dispatchers.IO) {
        if (text.isBlank()) return@withContext text

        // Normalize.
        val src = sourceLang.lowercase().take(2)
        val tgt = targetLang.lowercase().take(2)

        // CRITICAL: never round-trip through a different language. If source
        // already matches target, return the original text as-is.
        if (src == tgt) return@withContext text

        // Undetermined source — fall back to Korean (the OCR's primary lang).
        val effectiveSource = if (src.isBlank() || src == "un" || src == "und")
            TranslateLanguage.KOREAN else src

        // If even the fallback equals target, still skip.
        if (effectiveSource == tgt) return@withContext text

        val key = "$effectiveSource:$tgt:$text"
        cache.get(key)?.let {
            Log.d(FLOW_TAG, "translate cache hit $effectiveSource->$tgt text='${text.take(30)}'")
            return@withContext it
        }

        val source = TranslateLanguage.fromLanguageTag(effectiveSource) ?: run {
            Log.w(FLOW_TAG, "translate unsupported source language=$effectiveSource")
            return@withContext null
        }
        val target = TranslateLanguage.fromLanguageTag(tgt) ?: run {
            Log.w(FLOW_TAG, "translate unsupported target language=$tgt")
            return@withContext null
        }

        val clientKey = "$effectiveSource->$tgt"
        // computeIfAbsent is atomic on ConcurrentHashMap — no duplicate client creation.
        val translator = clients.computeIfAbsent(clientKey) {
            Translation.getClient(
                TranslatorOptions.Builder()
                    .setSourceLanguage(source)
                    .setTargetLanguage(target)
                    .build()
            )
        }

        try {
            // Only download once per session; skip the IPC call on subsequent translations.
            if (downloadedModels.add(clientKey)) {
                Log.d(FLOW_TAG, "Downloading model $clientKey")
                Tasks.await(translator.downloadModelIfNeeded())
            }
            // Sanity log: every translate call records the exact direction so
            // a user inspecting logcat can confirm source ≠ target.
            Log.d(FLOW_TAG, "translate src=$effectiveSource tgt=$tgt textLen=${text.length}")
            val result = Tasks.await(translator.translate(text))
            cache.put(key, result)
            Log.d(FLOW_TAG, "translate result='${result.take(30)}'")
            result
        } catch (e: Exception) {
            // Remove from downloaded set so next call retries the download.
            downloadedModels.remove(clientKey)
            Log.e(FLOW_TAG, "translate failed $effectiveSource->$tgt: ${e.message}", e)
            null
        }
    }

    fun release() {
        clients.values.forEach { it.close() }
        clients.clear()
        downloadedModels.clear()
        cache.clear()
    }

    private companion object {
        const val FLOW_TAG = "BridgeFlow"
    }
}
