package com.bridge.translator.ui.main

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bridge.translator.engine.TranslationEngine
import com.bridge.translator.service.FloatingBubbleService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("bridge_prefs", Context.MODE_PRIVATE)

    private val _serviceRunning = MutableLiveData(FloatingBubbleService.isRunning)
    val serviceRunning: LiveData<Boolean> = _serviceRunning

    private val _targetLanguageLabel = MutableLiveData(currentTargetLabel())
    val targetLanguageLabel: LiveData<String> = _targetLanguageLabel

    private val _captureMode = MutableStateFlow(
        FloatingBubbleService.CaptureMode.valueOf(
            prefs.getString("capture_mode", "SCREEN") ?: "SCREEN"
        )
    )
    val captureMode: StateFlow<FloatingBubbleService.CaptureMode> = _captureMode.asStateFlow()

    fun syncState() {
        _serviceRunning.value = FloatingBubbleService.isRunning
        _targetLanguageLabel.value = currentTargetLabel()
    }

    fun toggleService() {
        if (FloatingBubbleService.isRunning) stopBubbleService() else startBubbleService()
    }

    fun clearTargetLanguage() {
        prefs.edit().remove("target_language").apply()
        _targetLanguageLabel.value = currentTargetLabel()
    }

    fun setCaptureMode(mode: FloatingBubbleService.CaptureMode) {
        _captureMode.value = mode
        prefs.edit().putString("capture_mode", mode.name).apply()
        FloatingBubbleService.instance?.setCaptureMode(mode)
    }

    private fun currentTargetLabel(): String {
        val code = prefs.getString("target_language", null) ?: return "Not set (will ask on first use)"
        return TranslationEngine.LANGUAGES.find { it.code == code }?.label ?: code
    }

    private fun startBubbleService() {
        getApplication<Application>().startForegroundService(
            Intent(getApplication(), FloatingBubbleService::class.java)
        )
        _serviceRunning.value = true
    }

    private fun stopBubbleService() {
        getApplication<Application>().stopService(
            Intent(getApplication(), FloatingBubbleService::class.java)
        )
        _serviceRunning.value = false
    }
}
