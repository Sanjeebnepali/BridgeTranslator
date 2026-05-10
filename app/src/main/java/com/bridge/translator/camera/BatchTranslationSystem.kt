package com.bridge.translator.camera

import android.content.Context
import android.util.LruCache
import com.bridge.translator.camera.cache.TranslationCacheDao
import com.bridge.translator.camera.cache.TranslationCacheEntity
import com.bridge.translator.camera.data.TextOrientationBlock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Module 5 – Batch Translation System.
 *
 * Features:
 *  - In-memory LRU cache (1 000 entries, O(1) lookup)
 *  - Disk cache via Room (30-day expiry, persists across sessions)
 *  - Group-by-language batching (max 50 items per batch)
 *  - Parallel coroutine translation (max 5 concurrent language groups)
 *  - Exponential-backoff retry (up to 3 retries)
 *  - Quality validation (length check, non-empty, not identical to source)
 *  - Offline fallback to common-phrase dictionary
 *
 * Target: <2 s total, ≥60 % cache-hit rate on repeated frames.
 */
class BatchTranslationSystem(private val cacheDao: TranslationCacheDao) {

    companion object {
        private const val MAX_IN_MEMORY   = 1_000
        private const val MAX_RETRIES     = 3
        private const val INITIAL_BACKOFF = 1_000L   // 1 s
        private const val MAX_BATCH_SIZE  = 50
        private const val MAX_PARALLEL    = 5
    }

    // ── In-memory LRU cache ────────────────────────────────────────────────────

    private val memoryCache = object : LruCache<String, String>(MAX_IN_MEMORY) {}

    // ── Public API ─────────────────────────────────────────────────────────────

    /**
     * Translate a list of text blocks concurrently.
     *
     * @param blocks     OCR blocks with language annotations.
     * @param targetLang BCP-47 target language code (e.g. "en").
     * @return           Map from block index → translated text.
     */
    suspend fun translateAll(
        blocks: List<TextOrientationBlock>,
        targetLang: String
    ): Map<Int, String> = withContext(Dispatchers.IO) {

        val results = mutableMapOf<Int, String>()
        val pending = mutableListOf<Pair<Int, TextOrientationBlock>>()  // index + block

        // 1. Check caches (memory first, then disk)
        blocks.forEachIndexed { idx, block ->
            val key = cacheKey(block.text, block.language, targetLang)
            val cached = memoryCache.get(key) ?: cacheDao.get(key)?.translatedText
            if (cached != null) {
                results[idx] = cached
                memoryCache.put(key, cached)  // warm memory cache from disk
            } else {
                pending.add(idx to block)
            }
        }

        if (pending.isEmpty()) return@withContext results

        // 2. Group by source language
        val grouped: Map<String, List<Pair<Int, TextOrientationBlock>>> =
            pending.groupBy { it.second.language.ifBlank { "und" } }

        // 3. Translate each language group in parallel (max MAX_PARALLEL concurrent)
        val batches = grouped.entries.chunked(MAX_PARALLEL)
        for (batch in batches) {
            coroutineScope {
                batch.map { (sourceLang, indexedBlocks) ->
                    async {
                        translateGroup(indexedBlocks, sourceLang, targetLang, results)
                    }
                }.forEach { it.await() }
            }
        }

        results
    }

    /**
     * Translate a single text.  Uses caching + retry.
     */
    suspend fun translateSingle(
        text: String,
        sourceLang: String,
        targetLang: String
    ): String = withContext(Dispatchers.IO) {
        if (text.isBlank() || sourceLang == targetLang) return@withContext text
        val key = cacheKey(text, sourceLang, targetLang)

        memoryCache.get(key)?.let { return@withContext it }
        cacheDao.get(key)?.translatedText?.also {
            memoryCache.put(key, it)
            return@withContext it
        }

        val translated = translateWithRetry(text, sourceLang, targetLang)
        if (translated.isNotBlank() && isValidTranslation(text, translated)) {
            storeInCache(key, text, sourceLang, targetLang, translated)
        }
        translated.ifBlank { text }
    }

    // ── Group translation ──────────────────────────────────────────────────────

    private suspend fun translateGroup(
        indexedBlocks: List<Pair<Int, TextOrientationBlock>>,
        sourceLang: String,
        targetLang: String,
        results: MutableMap<Int, String>
    ) {
        // Further chunk into batches of MAX_BATCH_SIZE
        indexedBlocks.chunked(MAX_BATCH_SIZE).forEach { chunk ->
            chunk.forEach { (idx, block) ->
                val translated = translateWithRetry(block.text, sourceLang, targetLang)
                val valid = if (isValidTranslation(block.text, translated)) translated else block.text
                results[idx] = valid

                val key = cacheKey(block.text, sourceLang, targetLang)
                storeInCache(key, block.text, sourceLang, targetLang, valid)
            }
        }
    }

    // ── Retry logic (exponential backoff) ─────────────────────────────────────

    private suspend fun translateWithRetry(
        text: String,
        sourceLang: String,
        targetLang: String
    ): String {
        var lastError = ""
        var backoff   = INITIAL_BACKOFF

        repeat(MAX_RETRIES) { attempt ->
            if (attempt > 0) delay(backoff).also { backoff *= 2 }
            val result = callTranslationEngine(text, sourceLang, targetLang)
            if (result.isNotBlank()) return result
        }

        // Offline fallback
        return OfflineFallback.get(text) ?: text
    }

    private suspend fun callTranslationEngine(
        text: String,
        sourceLang: String,
        targetLang: String
    ): String = suspendCoroutine { cont ->
        com.bridge.translator.engine.TranslationEngine.translate(
            text       = text,
            sourceCode = sourceLang.ifBlank { "ko" },
            targetCode = targetLang,
            wifiOnly   = false,
            onDownloading = {},
            onResult   = { cont.resume(it) },
            onError    = { cont.resume("") }
        )
    }

    // ── Validation ─────────────────────────────────────────────────────────────

    private fun isValidTranslation(source: String, translated: String): Boolean {
        if (translated.isBlank()) return false
        if (translated == source) return false  // failed translation returns original
        val ratio = translated.length.toFloat() / source.length.toFloat().coerceAtLeast(1f)
        return ratio in 0.2f..5.0f              // within ±80% / 500% length change
    }

    // ── Cache helpers ──────────────────────────────────────────────────────────

    private fun cacheKey(text: String, sourceLang: String, targetLang: String): String {
        // Simple key: truncate to 60 chars to avoid huge keys
        val raw = "${text.take(40)}|${sourceLang}|${targetLang}"
        return raw.hashCode().toString()
    }

    private suspend fun storeInCache(
        key: String, text: String, sourceLang: String, targetLang: String, translated: String
    ) {
        memoryCache.put(key, translated)
        cacheDao.put(
            TranslationCacheEntity(
                cacheKey       = key,
                sourceText     = text.take(500),
                translatedText = translated.take(500),
                sourceLang     = sourceLang,
                targetLang     = targetLang
            )
        )
    }

    /** Purge expired disk cache entries (call from Application or WorkManager). */
    suspend fun purgeExpiredCache() = cacheDao.purgeExpired()
}

// ── Offline fallback dictionary ───────────────────────────────────────────────

private object OfflineFallback {
    private val dictionary = mapOf(
        // Korean → English common product phrases
        "성분" to "Ingredients",
        "영양성분" to "Nutrition Facts",
        "칼로리" to "Calories",
        "단백질" to "Protein",
        "지방" to "Fat",
        "탄수화물" to "Carbohydrates",
        "나트륨" to "Sodium",
        "제조일자" to "Manufactured Date",
        "유통기한" to "Best Before",
        "원재료" to "Raw Materials",
        // Chinese → English
        "成分" to "Ingredients",
        "营养" to "Nutrition",
        "卡路里" to "Calories",
        "蛋白质" to "Protein",
        "脂肪" to "Fat",
        // Japanese → English
        "成分" to "Ingredients",
        "栄養" to "Nutrition",
        "カロリー" to "Calories"
    )

    fun get(text: String): String? = dictionary[text.trim()]
}
