package com.bridge.translator

import android.app.Application
import com.bridge.translator.tts.SpeechEngine

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // Initialise the singleton TTS engine once; all activities share the same instance.
        SpeechEngine.init(this)
    }

    override fun onTerminate() {
        SpeechEngine.shutdown()
        super.onTerminate()
    }
}
