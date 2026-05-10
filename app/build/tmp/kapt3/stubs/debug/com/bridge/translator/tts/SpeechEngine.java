package com.bridge.translator.tts;

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
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000`\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0013\u001a\u00020\u000e2\u0006\u0010\u0014\u001a\u00020\u0010H\u0002J\u0018\u0010\u0015\u001a\u00020\u000e2\u0006\u0010\u0016\u001a\u00020\u00172\b\b\u0002\u0010\u0014\u001a\u00020\u0010J\u0010\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u0004H\u0002J\u001a\u0010\u001b\u001a\u0004\u0018\u00010\u001c2\u0006\u0010\u001d\u001a\u00020\u00122\u0006\u0010\u0014\u001a\u00020\u0010H\u0002J\u000e\u0010\u001e\u001a\u00020\u000e2\u0006\u0010\u0014\u001a\u00020\u0010J)\u0010\u001f\u001a\u00020\u000e2!\u0010 \u001a\u001d\u0012\u0013\u0012\u00110\u0006\u00a2\u0006\f\b\u000b\u0012\b\b\f\u0012\u0004\b\b(\r\u0012\u0004\u0012\u00020\u000e0\nJ\u0006\u0010!\u001a\u00020\u000eJ\u0018\u0010\"\u001a\u00020\u000e2\u0006\u0010#\u001a\u00020\u00042\b\b\u0002\u0010$\u001a\u00020\u0004J\u0014\u0010%\u001a\u00020\u000e2\f\u0010&\u001a\b\u0012\u0004\u0012\u00020\u00040\'J \u0010(\u001a\b\u0012\u0004\u0012\u00020\u00040\'2\u0006\u0010#\u001a\u00020\u00042\b\b\u0002\u0010)\u001a\u00020*H\u0002J\u0006\u0010+\u001a\u00020\u000eR\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0011\u0010\u0007\u001a\u00020\u00068F\u00a2\u0006\u0006\u001a\u0004\b\u0007\u0010\bR+\u0010\t\u001a\u001f\u0012\u0013\u0012\u00110\u0006\u00a2\u0006\f\b\u000b\u0012\b\b\f\u0012\u0004\b\b(\r\u0012\u0004\u0012\u00020\u000e\u0018\u00010\nX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0010X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0011\u001a\u0004\u0018\u00010\u0012X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006,"}, d2 = {"Lcom/bridge/translator/tts/SpeechEngine;", "", "()V", "TAG", "", "isReady", "", "isSpeaking", "()Z", "onReadingStateChanged", "Lkotlin/Function1;", "Lkotlin/ParameterName;", "name", "isReading", "", "targetLocale", "Ljava/util/Locale;", "tts", "Landroid/speech/tts/TextToSpeech;", "configureLocale", "locale", "init", "context", "Landroid/content/Context;", "pauseAfterChunk", "", "chunk", "selectBestLocalVoice", "Landroid/speech/tts/Voice;", "engine", "setLocale", "setOnReadingStateChanged", "callback", "shutdown", "speak", "text", "utteranceId", "speakTranslatedText", "translations", "", "splitSpeechChunks", "maxLen", "", "stop", "app_debug"})
public final class SpeechEngine {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String TAG = "SpeechEngine";
    @kotlin.jvm.Volatile()
    @org.jetbrains.annotations.Nullable()
    private static volatile android.speech.tts.TextToSpeech tts;
    @kotlin.jvm.Volatile()
    private static volatile boolean isReady = false;
    @kotlin.jvm.Volatile()
    @org.jetbrains.annotations.NotNull()
    private static volatile java.util.Locale targetLocale;
    @org.jetbrains.annotations.Nullable()
    private static kotlin.jvm.functions.Function1<? super java.lang.Boolean, kotlin.Unit> onReadingStateChanged;
    @org.jetbrains.annotations.NotNull()
    public static final com.bridge.translator.tts.SpeechEngine INSTANCE = null;
    
    private SpeechEngine() {
        super();
    }
    
    /**
     * Initialise the TTS engine.  Safe to call multiple times; subsequent calls
     * are no-ops once the engine is ready.
     *
     * @param context       Application context.
     * @param locale        Voice locale to use; defaults to [Locale.ENGLISH].
     */
    public final void init(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.util.Locale locale) {
    }
    
    /**
     * Update the target locale, e.g. when the user changes the target language.
     */
    public final void setLocale(@org.jetbrains.annotations.NotNull()
    java.util.Locale locale) {
    }
    
    /**
     * Register a callback that fires when speech starts / stops.
     */
    public final void setOnReadingStateChanged(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.Boolean, kotlin.Unit> callback) {
    }
    
    /**
     * Speak a single [text] string.  Interrupts any in-progress speech.
     *
     * @param text          Text to read aloud.
     * @param utteranceId   Optional identifier (useful for tracking completion).
     */
    public final void speak(@org.jetbrains.annotations.NotNull()
    java.lang.String text, @org.jetbrains.annotations.NotNull()
    java.lang.String utteranceId) {
    }
    
    /**
     * Speak each translated block in logical reading order
     * (caller sorts by top-to-bottom, left-to-right before passing).
     *
     * @param translations  List of translated text strings.
     */
    public final void speakTranslatedText(@org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> translations) {
    }
    
    /**
     * Stop any currently playing speech immediately.
     */
    public final void stop() {
    }
    
    /**
     * Release TTS resources.  Call from [Application.onTerminate] or cleanup.
     */
    public final void shutdown() {
    }
    
    public final boolean isSpeaking() {
        return false;
    }
    
    private final void configureLocale(java.util.Locale locale) {
    }
    
    private final android.speech.tts.Voice selectBestLocalVoice(android.speech.tts.TextToSpeech engine, java.util.Locale locale) {
        return null;
    }
    
    private final java.util.List<java.lang.String> splitSpeechChunks(java.lang.String text, int maxLen) {
        return null;
    }
    
    private final long pauseAfterChunk(java.lang.String chunk) {
        return 0L;
    }
}