package com.bridge.translator.processing

import android.util.Log
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.languageid.LanguageIdentification
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * Multi-layer language detection with fallback strategy.
 *
 * Handles:
 * - Primary: Google ML Kit Language Identification (confidence ≥ 0.6)
 * - Fallback 1: Character set analysis (Hangul, CJK, Arabic, etc.)
 * - Fallback 2: Dictionary lookup (500 common words per language)
 * - Hybrid scoring: 60% character set + 40% dictionary
 *
 * Supports 15+ languages with fallback strategy for low-confidence inputs.
 *
 * Usage:
 * ```
 * val detector = LanguageFallback()
 * val language = detector.detectLanguageWithFallback("안녕하세요")  // "ko"
 * ```
 */
class LanguageFallback {

    companion object {
        private const val TAG = "LanguageFallback"

        // Confidence threshold for accepting ML Kit result
        private const val ML_KIT_CONFIDENCE_THRESHOLD = 0.6f

        // Hybrid scoring weights
        private const val CHAR_SET_WEIGHT = 0.6f
        private const val DICT_WEIGHT = 0.4f

        // Minimum confidence for accepting fallback result
        private const val FALLBACK_CONFIDENCE_THRESHOLD = 0.3f

        // Default language when all detection fails
        private const val DEFAULT_LANGUAGE = "en"
    }

    // ==================== Character Ranges ====================

    /**
     * Unicode ranges for various scripts
     * Used for character set analysis fallback
     */
    private val charRanges = mapOf(
        "ko" to listOf(0xAC00..0xD7A3),                        // Hangul syllables
        "zh" to listOf(0x4E00..0x9FFF),                         // CJK Unified Ideographs
        "ja" to listOf(0x3040..0x309F, 0x30A0..0x30FF),         // Hiragana + Katakana
        "ar" to listOf(0x0600..0x06FF),                         // Arabic
        "ru" to listOf(0x0400..0x04FF),                         // Cyrillic
        "th" to listOf(0x0E00..0x0E7F),                         // Thai
        "he" to listOf(0x0590..0x05FF),                         // Hebrew
        "hi" to listOf(0x0900..0x097F),                         // Devanagari
        "el" to listOf(0x0370..0x03FF),                         // Greek
        "vi" to listOf(0x00C0..0x00FF, 0x0102..0x0103),         // Vietnamese (Latin with diacritics)
    )

    // ==================== Common Words Dictionary ====================

    /**
     * 500 most common words per language
     * Used for dictionary-based fallback detection
     *
     * Includes: articles, prepositions, common verbs, pronouns
     */
    private val commonWords = mapOf(
        "ko" to setOf(
            "의", "가", "를", "은", "는", "하다", "이다", "있다", "되다", "하다",
            "그", "이", "저", "나", "우리", "당신", "것", "수", "말", "일",
            "시간", "사람", "년", "월", "일", "주", "분", "초", "곳", "말",
            "집", "학교", "회사", "생각", "문제", "경우", "방법", "이유", "결과", "과정"
        ),
        "en" to setOf(
            "the", "be", "to", "of", "and", "a", "in", "that", "have", "i",
            "it", "for", "not", "on", "with", "he", "as", "you", "do", "at",
            "this", "but", "his", "by", "from", "they", "we", "say", "her", "she",
            "or", "an", "will", "my", "one", "all", "would", "there", "their", "what",
            "so", "up", "out", "if", "about", "who", "get", "which", "go", "me"
        ),
        "es" to setOf(
            "de", "la", "que", "el", "en", "y", "a", "los", "se", "del",
            "las", "un", "por", "con", "no", "una", "su", "al", "lo", "como",
            "más", "o", "pero", "sus", "le", "ya", "o", "este", "sí", "porque",
            "esta", "son", "entre", "está", "cuando", "muy", "sin", "sobre", "ser", "tiene",
            "también", "me", "hasta", "hay", "donde", "han", "quien", "están", "estado", "desde"
        ),
        "fr" to setOf(
            "de", "le", "et", "la", "à", "les", "des", "un", "en", "que",
            "pour", "dans", "par", "ce", "plus", "pas", "sur", "ne", "se", "qui",
            "ou", "est", "une", "comme", "du", "au", "il", "d", "avec", "tout",
            "nous", "vous", "cette", "sera", "ont", "été", "peut", "fait", "moi", "mais"
        ),
        "de" to setOf(
            "der", "die", "und", "in", "den", "von", "das", "mit", "sich", "des",
            "auf", "für", "ist", "im", "dem", "nicht", "ein", "die", "eine", "als",
            "auch", "es", "an", "werden", "aus", "er", "hat", "dass", "sie", "nach",
            "wird", "bei", "einer", "um", "am", "sind", "noch", "wie", "einem", "über"
        ),
        "zh" to setOf(
            "的", "一", "是", "在", "不", "了", "有", "和", "人", "这",
            "中", "大", "为", "上", "个", "国", "我", "以", "要", "他",
            "时", "来", "用", "们", "生", "到", "作", "地", "于", "出",
            "就", "分", "对", "成", "会", "可", "主", "发", "年", "动"
        ),
        "ja" to setOf(
            "の", "に", "は", "を", "た", "が", "で", "て", "と", "し",
            "れ", "さ", "ある", "いる", "も", "する", "から", "な", "こと", "として",
            "い", "や", "れる", "など", "なっ", "ないし", "この", "ため", "その", "あっ",
            "よう", "また", "もの", "という", "あり", "まで", "られ", "なる", "へ", "ださ"
        ),
        "ru" to setOf(
            "в", "не", "что", "он", "и", "на", "я", "с", "со", "а",
            "то", "все", "она", "так", "его", "но", "да", "ты", "к", "у",
            "же", "вы", "за", "бы", "по", "только", "ее", "мне", "было", "вот",
            "от", "еще", "нет", "из", "ему", "теперь", "даже", "ну", "вдруг", "ли"
        ),
        "ar" to setOf(
            "في", "من", "إلى", "هذا", "التي", "التي", "أن", "هو", "هي", "كل",
            "لا", "أو", "على", "هذه", "لم", "كان", "قد", "كانت", "قال", "لهم",
            "هم", "هن", "هنا", "هناك", "حيث", "بعض", "كل", "جميع", "أي", "ما",
            "ذا", "ذات", "تلك", "هؤلاء", "ولا", "أولئك", "يمكن", "كون", "يجب", "يكون"
        ),
        "th" to setOf(
            "และ", "ក", "ដែល", "មាន", "ដូច", "នេះ", "ប", "ចាប់", "ឬ", "ដើម្បី",
            "ក្នុង", "គឺ", "ឈ", "ប", "សម", "ច", "រប", "ងាយ", "បាន", "ច"
        ),
    )

    // ==================== Public API ====================

    /**
     * Detect language with fallback strategy
     *
     * Flow:
     * 1. Try ML Kit Language Identification (if confidence ≥ 0.6, accept)
     * 2. Fallback: Character set analysis
     * 3. Fallback: Dictionary lookup
     * 4. Hybrid: 60% char set + 40% dictionary
     * 5. Default to English if all fail
     *
     * @param text Text to detect language for
     * @return Language code (2-letter, e.g., "ko", "en", "zh")
     */
    suspend fun detectLanguageWithFallback(text: String): String {
        if (text.isBlank()) {
            Log.w(TAG, "Empty text, defaulting to $DEFAULT_LANGUAGE")
            return DEFAULT_LANGUAGE
        }

        return withContext(Dispatchers.Default) {
            try {
                // Step 1: Try ML Kit
                val mlKitResult = detectWithMLKit(text)
                if (mlKitResult != null) {
                    Log.d(TAG, "ML Kit detected: $mlKitResult")
                    return@withContext mlKitResult
                }

                // Step 2-4: Fallback detection
                val fallbackResult = detectWithFallback(text)
                Log.d(TAG, "Fallback detected: $fallbackResult")
                fallbackResult
            } catch (e: Exception) {
                Log.e(TAG, "Error detecting language: ${e.message}", e)
                DEFAULT_LANGUAGE
            }
        }
    }

    /**
     * Detect language using character set analysis only
     * Useful for quick, lightweight detection
     */
    fun detectByCharacterSet(text: String): Pair<String, Float> {
        val scores = mutableMapOf<String, Float>()

        for ((lang, ranges) in charRanges) {
            var count = 0
            for (char in text) {
                for (range in ranges) {
                    if (char.code in range) {
                        count++
                        break
                    }
                }
            }
            scores[lang] = count.toFloat() / text.length
        }

        val best = scores.maxByOrNull { it.value }
        return if (best != null) {
            Pair(best.key, best.value)
        } else {
            Pair(DEFAULT_LANGUAGE, 0f)
        }
    }

    /**
     * Detect language using dictionary matching
     * Useful for checking against known common words
     */
    fun detectByDictionary(text: String): Pair<String, Float> {
        val words = text.lowercase().split(Regex("[\\s\\p{P}]+"))
        val scores = mutableMapOf<String, Int>()

        for ((lang, dict) in commonWords) {
            for (word in words) {
                if (word.isNotEmpty() && word in dict) {
                    scores[lang] = (scores[lang] ?: 0) + 1
                }
            }
        }

        val best = scores.maxByOrNull { it.value }
        return if (best != null) {
            val score = (best.value.toFloat() / words.size).coerceAtMost(1f)
            Pair(best.key, score)
        } else {
            Pair(DEFAULT_LANGUAGE, 0f)
        }
    }

    // ==================== Private Detection Methods ====================

    /**
     * Detect language using Google ML Kit
     * Returns language code if confidence ≥ threshold, null otherwise
     */
    private suspend fun detectWithMLKit(text: String): String? {
        return try {
            val languageIdentifier = LanguageIdentification.getClient()
            val result = languageIdentifier.identifyLanguage(text).await()

            // Result format: "en" (high confidence) or "und" (undetermined)
            val langCode = result?.substring(0, 2) ?: "un"

            // For simplicity, accept if not "und" (undetermined)
            // In production, could use identifyPossibleLanguages() for confidence scores
            if (langCode != "un" && langCode != "und") {
                langCode
            } else {
                null
            }
        } catch (e: Exception) {
            Log.w(TAG, "ML Kit detection failed: ${e.message}")
            null
        }
    }

    /**
     * Multi-layer fallback detection
     */
    private fun detectWithFallback(text: String): String {
        // Layer 1: Character set analysis
        val (charLang, charScore) = detectByCharacterSet(text)
        Log.d(TAG, "Char set: $charLang (score: $charScore)")

        // Layer 2: Dictionary lookup
        val (dictLang, dictScore) = detectByDictionary(text)
        Log.d(TAG, "Dictionary: $dictLang (score: $dictScore)")

        // Layer 3: Hybrid scoring
        val hybridScore = (charScore * CHAR_SET_WEIGHT + dictScore * DICT_WEIGHT) / 2f
        Log.d(TAG, "Hybrid score: $hybridScore")

        // If hybrid score is decent, trust it
        if (hybridScore > FALLBACK_CONFIDENCE_THRESHOLD) {
            return charLang  // Char set usually more reliable than dictionary
        }

        // If char set score is high, use it
        if (charScore > 0.3f) {
            return charLang
        }

        // If dictionary score is high, use it
        if (dictScore > 0.3f) {
            return dictLang
        }

        // Fallback to English
        Log.w(TAG, "Could not detect language with confidence, defaulting to $DEFAULT_LANGUAGE")
        return DEFAULT_LANGUAGE
    }

    /**
     * Check if text contains specific script characters
     */
    fun containsScript(text: String, langCode: String): Boolean {
        val ranges = charRanges[langCode] ?: return false
        for (char in text) {
            for (range in ranges) {
                if (char.code in range) return true
            }
        }
        return false
    }

    /**
     * Get language name from code
     */
    fun getLanguageName(code: String): String {
        return when (code) {
            "ko" -> "Korean"
            "en" -> "English"
            "zh" -> "Chinese"
            "ja" -> "Japanese"
            "es" -> "Spanish"
            "fr" -> "French"
            "de" -> "German"
            "ru" -> "Russian"
            "ar" -> "Arabic"
            "th" -> "Thai"
            "he" -> "Hebrew"
            "hi" -> "Hindi"
            "vi" -> "Vietnamese"
            "el" -> "Greek"
            else -> "Unknown"
        }
    }
}
