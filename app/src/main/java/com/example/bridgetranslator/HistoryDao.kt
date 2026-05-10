package com.example.bridgetranslator

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {

    @Query("SELECT * FROM history ORDER BY timestamp DESC")
    fun getAllFlow(): Flow<List<HistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: HistoryEntity)

    @Delete
    suspend fun delete(entry: HistoryEntity)

    @Query("UPDATE history SET isStarred = :starred WHERE id = :id")
    suspend fun setStarred(id: Long, starred: Boolean)

    @Query("DELETE FROM history")
    suspend fun clearAll()
}
