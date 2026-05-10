package com.bridge.translator.camera.cache

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TranslationCacheDao {

    @Query("SELECT * FROM translation_cache WHERE cacheKey = :key AND expiresAt > :now LIMIT 1")
    suspend fun get(key: String, now: Long = System.currentTimeMillis()): TranslationCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun put(entry: TranslationCacheEntity)

    /** Delete expired entries — call periodically to keep the DB small. */
    @Query("DELETE FROM translation_cache WHERE expiresAt <= :now")
    suspend fun purgeExpired(now: Long = System.currentTimeMillis())

    @Query("DELETE FROM translation_cache")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM translation_cache")
    suspend fun count(): Int
}
