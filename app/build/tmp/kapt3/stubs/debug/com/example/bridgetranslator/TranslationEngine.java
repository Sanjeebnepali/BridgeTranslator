package com.example.bridgetranslator;

/**
 * Compatibility wrapper for the older camera/manual UI.
 * The actual ML Kit implementation now lives in com.bridge.translator.engine.TranslationEngine.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\"\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0006\u0010\u0006\u001a\u00020\u0007J\u000e\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u0005J`\u0010\u000b\u001a\u00020\u00072\u0006\u0010\f\u001a\u00020\u00052\u0006\u0010\r\u001a\u00020\u00052\u0006\u0010\u000e\u001a\u00020\u00052\b\b\u0002\u0010\u000f\u001a\u00020\t2\u000e\b\u0002\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00070\u00112\u0012\u0010\u0012\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00070\u00132\u0012\u0010\u0014\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00070\u0013R\u0014\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0015"}, d2 = {"Lcom/example/bridgetranslator/TranslationEngine;", "", "()V", "supportedCodes", "", "", "close", "", "isSupported", "", "code", "translate", "text", "srcCode", "tgtCode", "wifiOnly", "onDownloading", "Lkotlin/Function0;", "onSuccess", "Lkotlin/Function1;", "onError", "app_debug"})
public final class TranslationEngine {
    @org.jetbrains.annotations.NotNull()
    private final java.util.Set<java.lang.String> supportedCodes = null;
    
    public TranslationEngine() {
        super();
    }
    
    public final boolean isSupported(@org.jetbrains.annotations.NotNull()
    java.lang.String code) {
        return false;
    }
    
    public final void translate(@org.jetbrains.annotations.NotNull()
    java.lang.String text, @org.jetbrains.annotations.NotNull()
    java.lang.String srcCode, @org.jetbrains.annotations.NotNull()
    java.lang.String tgtCode, boolean wifiOnly, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onDownloading, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onSuccess, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onError) {
    }
    
    public final void close() {
    }
}