package com.example.bridgetranslator

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "language_prefs")

class LanguageManager(private val context: Context) {

    companion object {
        private val SOURCE_LANG_KEY = stringPreferencesKey("source_language")
        private val TARGET_LANG_KEY = stringPreferencesKey("target_language")
        const val DEFAULT_SOURCE = "en"
        const val DEFAULT_TARGET = "en"
    }

    // Keep old key for backwards compat with LanguageBottomSheet
    val selectedLanguageCode: Flow<String> = context.dataStore.data
        .map { it[SOURCE_LANG_KEY] ?: DEFAULT_SOURCE }

    val sourceLangCode: Flow<String> = context.dataStore.data
        .map { it[SOURCE_LANG_KEY] ?: DEFAULT_SOURCE }

    val targetLangCode: Flow<String> = context.dataStore.data
        .map { it[TARGET_LANG_KEY] ?: DEFAULT_TARGET }

    suspend fun setSourceLanguage(code: String) {
        context.dataStore.edit { it[SOURCE_LANG_KEY] = code }
        context.getSharedPreferences("bridge_prefs", Context.MODE_PRIVATE)
            .edit()
            .putString("source_language", code)
            .apply()
    }

    suspend fun setTargetLanguage(code: String) {
        context.dataStore.edit { it[TARGET_LANG_KEY] = code }
        context.getSharedPreferences("bridge_prefs", Context.MODE_PRIVATE)
            .edit()
            .putString("target_language", code)
            .putBoolean("target_language_user_set", true)
            .apply()
    }

    // Legacy - sets source language
    suspend fun setLanguage(languageCode: String) = setSourceLanguage(languageCode)

    suspend fun swapLanguages() {
        context.dataStore.edit { prefs ->
            val src = prefs[SOURCE_LANG_KEY] ?: DEFAULT_SOURCE
            val tgt = prefs[TARGET_LANG_KEY] ?: DEFAULT_TARGET
            prefs[SOURCE_LANG_KEY] = tgt
            prefs[TARGET_LANG_KEY] = src
            context.getSharedPreferences("bridge_prefs", Context.MODE_PRIVATE)
                .edit()
                .putString("source_language", tgt)
                .putString("target_language", src)
                .putBoolean("target_language_user_set", true)
                .apply()
        }
    }
}
