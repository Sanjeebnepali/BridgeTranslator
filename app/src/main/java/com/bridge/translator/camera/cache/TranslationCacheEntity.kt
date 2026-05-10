package com.bridge.translator.camera.cache

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/** Room entity that stores a single translated text entry on disk. */
@Entity(
    tableName = "translation_cache",
    indices = [Index(value = ["cacheKey"], unique = true)]
)
data class TranslationCacheEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    /** Format: "sourceText|sourceLang|targetLang" (SHA-256 truncated to 64 chars) */
    val cacheKey: String,
    val sourceText: String,
    val translatedText: String,
    val sourceLang: String,
    val targetLang: String,
    val cachedAt: Long = System.currentTimeMillis(),
    /** Expires after 30 days (30 * 24 * 60 * 60 * 1000 ms) */
    val expiresAt: Long = System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000
)
