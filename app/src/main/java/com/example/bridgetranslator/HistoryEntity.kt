package com.example.bridgetranslator

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sourceText: String,
    val resultText: String,
    val sourceLangCode: String,
    val targetLangCode: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isStarred: Boolean = false
)
