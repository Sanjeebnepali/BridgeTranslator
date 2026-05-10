package com.example.bridgetranslator

import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.languageid.LanguageIdentifier
import kotlinx.coroutines.tasks.await

class LanguageDetector(
    private val languageIdentifier: LanguageIdentifier = LanguageIdentification.getClient()
) {

    suspend fun detectLanguage(text: String): String {
        return try {
            val result = languageIdentifier.identifyLanguage(text).await()
            if (result != "und") {
                result
            } else {
                fallbackDetection(text)
            }
        } catch (e: Exception) {
            fallbackDetection(text)
        }
    }

    internal fun fallbackDetection(text: String): String {
        // Step 1: Character Set Analysis
        val charSetScores = analyzeCharacterSets(text)
        
        // Find the language with the highest character set score
        val bestMatch = charSetScores.maxByOrNull { it.value }
        
        if (bestMatch != null && bestMatch.value > 0.4f) {
            return bestMatch.key
        }
        
        return "und"
    }

    internal fun analyzeCharacterSets(text: String): Map<String, Float> {
        val scores = mutableMapOf<String, Float>()
        if (text.isEmpty()) return scores

        val length = text.length.toFloat()
        
        var koreanCount = 0
        var cjkCount = 0
        var hiraganaKatakanaCount = 0
        var arabicCount = 0
        var cyrillicCount = 0
        var thaiCount = 0
        var hebrewCount = 0

        for (char in text) {
            val codePoint = char.code
            when {
                // Korean Hangul
                codePoint in 0xAC00..0xD7A3 -> koreanCount++
                // CJK (Chinese)
                codePoint in 0x4E00..0x9FFF -> cjkCount++
                // Japanese (Hiragana + Katakana)
                codePoint in 0x3040..0x309F || codePoint in 0x30A0..0x30FF -> hiraganaKatakanaCount++
                // Arabic
                codePoint in 0x0600..0x06FF -> arabicCount++
                // Cyrillic (Russian)
                codePoint in 0x0400..0x04FF -> cyrillicCount++
                // Thai
                codePoint in 0x0E00..0x0E7F -> thaiCount++
                // Hebrew
                codePoint in 0x0590..0x05FF -> hebrewCount++
            }
        }

        if (koreanCount > 0) scores["ko"] = koreanCount / length
        if (cjkCount > 0) {
            // Note: CJK characters are shared. For fallback, assuming 'zh' unless hiragana/katakana is also present.
            // Simplified logic:
            if (hiraganaKatakanaCount > 0) {
                 scores["ja"] = (cjkCount + hiraganaKatakanaCount) / length
            } else {
                 scores["zh"] = cjkCount / length
            }
        }
        if (hiraganaKatakanaCount > 0 && cjkCount == 0) scores["ja"] = hiraganaKatakanaCount / length
        if (arabicCount > 0) scores["ar"] = arabicCount / length
        if (cyrillicCount > 0) scores["ru"] = cyrillicCount / length
        if (thaiCount > 0) scores["th"] = thaiCount / length
        if (hebrewCount > 0) scores["iw"] = hebrewCount / length // ML Kit uses 'iw' for Hebrew occasionally, or 'he'

        return scores
    }
    
    fun close() {
        languageIdentifier.close()
    }
}
