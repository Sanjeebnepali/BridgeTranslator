package com.bridge.translator.translation;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010#\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0007\u0018\u0000 \u00122\u00020\u0001:\u0001\u0012B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0006\u0010\u000b\u001a\u00020\fJ(\u0010\r\u001a\u0004\u0018\u00010\u00072\u0006\u0010\u000e\u001a\u00020\u00072\u0006\u0010\u000f\u001a\u00020\u00072\u0006\u0010\u0010\u001a\u00020\u0007H\u0086@\u00a2\u0006\u0002\u0010\u0011R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010\u0005\u001a\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\b0\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00070\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0013"}, d2 = {"Lcom/bridge/translator/translation/TranslationEngine;", "", "()V", "cache", "Lcom/bridge/translator/translation/TranslationCache;", "clients", "Ljava/util/concurrent/ConcurrentHashMap;", "", "Lcom/google/mlkit/nl/translate/Translator;", "downloadedModels", "", "release", "", "translate", "text", "sourceLang", "targetLang", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "Companion", "app_debug"})
public final class TranslationEngine {
    @org.jetbrains.annotations.NotNull()
    private final java.util.concurrent.ConcurrentHashMap<java.lang.String, com.google.mlkit.nl.translate.Translator> clients = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.Set<java.lang.String> downloadedModels = null;
    @org.jetbrains.annotations.NotNull()
    private final com.bridge.translator.translation.TranslationCache cache = null;
    @org.jetbrains.annotations.NotNull()
    @java.lang.Deprecated()
    public static final java.lang.String FLOW_TAG = "BridgeFlow";
    @org.jetbrains.annotations.NotNull()
    private static final com.bridge.translator.translation.TranslationEngine.Companion Companion = null;
    
    public TranslationEngine() {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object translate(@org.jetbrains.annotations.NotNull()
    java.lang.String text, @org.jetbrains.annotations.NotNull()
    java.lang.String sourceLang, @org.jetbrains.annotations.NotNull()
    java.lang.String targetLang, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.String> $completion) {
        return null;
    }
    
    public final void release() {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\b\u0082\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0005"}, d2 = {"Lcom/bridge/translator/translation/TranslationEngine$Companion;", "", "()V", "FLOW_TAG", "", "app_debug"})
    static final class Companion {
        
        private Companion() {
            super();
        }
    }
}