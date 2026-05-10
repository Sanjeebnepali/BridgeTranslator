package com.bridge.translator.tts

import android.content.Context
import android.os.Build
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import android.util.Log
import java.util.Locale

/**
 * Application-scoped singleton wrapper around [TextToSpeech].
 *
 * Initialise once in [Application.onCreate]:
 * ```kotlin
 * SpeechEngine.init(this)
 * ```
 *
 * Then from any component:
 * ```kotlin
 * SpeechEngine.speak("Hello world")
 * SpeechEngine.speakTranslatedText(listOf("Block 1", "Block 2"))
 * SpeechEngine.stop()
 * ```
 */
object SpeechEngine {

    private const val TAG = "SpeechEngine"

    // ── State ──────────────────────────────────────────────────────────────────

    @Volatile private var tts: TextToSpeech? = null
    @Volatile private var isReady = false
    @Volatile private var targetLocale: Locale = Locale.ENGLISH

    private var onReadingStateChanged: ((isReading: Boolean) -> Unit)? = null

    // ── Lifecycle ──────────────────────────────────────────────────────────────

    /**
     * Initialise the TTS engine.  Safe to call multiple times; subsequent calls
     * are no-ops once the engine is ready.
     *
     * @param context       Application context.
     * @param locale        Voice locale to use; defaults to [Locale.ENGLISH].
     */
    fun init(context: Context, locale: Locale = Locale.ENGLISH) {
        if (isReady && tts != null) return
        targetLocale = locale
        tts = TextToSpeech(context.applicationContext) { status ->
            isReady = status == TextToSpeech.SUCCESS
            if (isReady) {
                configureLocale(locale)
                tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        onReadingStateChanged?.invoke(true)
                    }
                    override fun onDone(utteranceId: String?) {
                        onReadingStateChanged?.invoke(false)
                    }
                    override fun onError(utteranceId: String?) {
                        onReadingStateChanged?.invoke(false)
                    }
                    override fun onError(utteranceId: String?, errorCode: Int) {
                        onReadingStateChanged?.invoke(false)
                    }
                    override fun onStop(utteranceId: String?, interrupted: Boolean) {
                        onReadingStateChanged?.invoke(false)
                    }
                })
                Log.i(TAG, "TTS engine ready (locale: $locale)")
            } else {
                Log.w(TAG, "TTS initialisation failed (status=$status)")
            }
        }
    }

    /** Update the target locale, e.g. when the user changes the target language. */
    fun setLocale(locale: Locale) {
        targetLocale = locale
        if (isReady) configureLocale(locale)
    }

    /** Register a callback that fires when speech starts / stops. */
    fun setOnReadingStateChanged(callback: (isReading: Boolean) -> Unit) {
        onReadingStateChanged = callback
    }

    // ── Speak ──────────────────────────────────────────────────────────────────

    /**
     * Speak a single [text] string.  Interrupts any in-progress speech.
     *
     * @param text          Text to read aloud.
     * @param utteranceId   Optional identifier (useful for tracking completion).
     */
    fun speak(text: String, utteranceId: String = "se_${System.currentTimeMillis()}") {
        if (!isReady || tts == null || text.isBlank()) return
        val chunks = splitSpeechChunks(text)
        chunks.forEachIndexed { i, chunk ->
            val uid   = "${utteranceId}_$i"
            val mode  = if (i == 0) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD
            tts?.speak(chunk, mode, null, uid)
            if (i < chunks.lastIndex) {
                val pauseMs = pauseAfterChunk(chunk)
                if (pauseMs > 0) {
                    tts?.playSilentUtterance(pauseMs, TextToSpeech.QUEUE_ADD, "${uid}_pause")
                }
            }
        }
    }

    /**
     * Speak each translated block in logical reading order
     * (caller sorts by top-to-bottom, left-to-right before passing).
     *
     * @param translations  List of translated text strings.
     */
    fun speakTranslatedText(translations: List<String>) {
        val combined = translations
            .filter { it.isNotBlank() }
            .joinToString(". ")
            .trim()
        if (combined.isNotBlank()) speak(combined, "se_translated")
    }

    /** Stop any currently playing speech immediately. */
    fun stop() {
        tts?.stop()
        onReadingStateChanged?.invoke(false)
    }

    /** Release TTS resources.  Call from [Application.onTerminate] or cleanup. */
    fun shutdown() {
        stop()
        tts?.shutdown()
        tts    = null
        isReady = false
    }

    val isSpeaking: Boolean get() = tts?.isSpeaking == true

    // ── Private helpers ────────────────────────────────────────────────────────

    private fun configureLocale(locale: Locale) {
        val engine = tts ?: return
        val availability = engine.isLanguageAvailable(locale)
        if (availability == TextToSpeech.LANG_MISSING_DATA ||
            availability == TextToSpeech.LANG_NOT_SUPPORTED) {
            // Fallback to English
            engine.language = Locale.ENGLISH
            Log.w(TAG, "Language $locale not supported, falling back to English")
            return
        }
        val bestVoice = selectBestLocalVoice(engine, locale)
        if (bestVoice != null) {
            engine.voice = bestVoice
        } else {
            engine.language = locale
        }
    }

    private fun selectBestLocalVoice(engine: TextToSpeech, locale: Locale): Voice? {
        val voices = engine.voices ?: return null
        return voices
            .filter { v ->
                v.locale.language == locale.language &&
                        !v.isNetworkConnectionRequired &&
                        !v.features.contains(TextToSpeech.Engine.KEY_FEATURE_NOT_INSTALLED)
            }
            .minByOrNull { it.latency }
    }

    private fun splitSpeechChunks(text: String, maxLen: Int = 220): List<String> {
        if (text.length <= maxLen) return listOf(text)
        val chunks = mutableListOf<String>()
        val delimiters = Regex("[.!?]+")
        val sentences = delimiters.split(text).map { it.trim() }.filter { it.isNotBlank() }
        val current   = StringBuilder()
        for (sentence in sentences) {
            if (current.length + sentence.length + 2 > maxLen && current.isNotEmpty()) {
                chunks += current.toString()
                current.clear()
            }
            if (current.isNotEmpty()) current.append(". ")
            current.append(sentence)
        }
        if (current.isNotEmpty()) chunks += current.toString()
        return chunks.ifEmpty { listOf(text.take(maxLen)) }
    }

    private fun pauseAfterChunk(chunk: String): Long = when {
        chunk.endsWith(".")  -> 400L
        chunk.endsWith("?")  -> 300L
        chunk.endsWith("!")  -> 300L
        chunk.endsWith(",")  -> 150L
        else                 -> 0L
    }
}
