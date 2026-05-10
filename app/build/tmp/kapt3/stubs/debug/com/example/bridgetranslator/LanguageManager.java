package com.example.bridgetranslator;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0010\u0002\n\u0002\b\t\u0018\u0000 \u00172\u00020\u0001:\u0001\u0017B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0016\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0007H\u0086@\u00a2\u0006\u0002\u0010\u0011J\u0016\u0010\u0012\u001a\u00020\u000f2\u0006\u0010\u0013\u001a\u00020\u0007H\u0086@\u00a2\u0006\u0002\u0010\u0011J\u0016\u0010\u0014\u001a\u00020\u000f2\u0006\u0010\u0013\u001a\u00020\u0007H\u0086@\u00a2\u0006\u0002\u0010\u0011J\u000e\u0010\u0015\u001a\u00020\u000fH\u0086@\u00a2\u0006\u0002\u0010\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0017\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\tR\u0017\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\t\u00a8\u0006\u0018"}, d2 = {"Lcom/example/bridgetranslator/LanguageManager;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "selectedLanguageCode", "Lkotlinx/coroutines/flow/Flow;", "", "getSelectedLanguageCode", "()Lkotlinx/coroutines/flow/Flow;", "sourceLangCode", "getSourceLangCode", "targetLangCode", "getTargetLangCode", "setLanguage", "", "languageCode", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "setSourceLanguage", "code", "setTargetLanguage", "swapLanguages", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "Companion", "app_debug"})
public final class LanguageManager {
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> SOURCE_LANG_KEY = null;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> TARGET_LANG_KEY = null;
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String DEFAULT_SOURCE = "en";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String DEFAULT_TARGET = "en";
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.Flow<java.lang.String> selectedLanguageCode = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.Flow<java.lang.String> sourceLangCode = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.Flow<java.lang.String> targetLangCode = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.example.bridgetranslator.LanguageManager.Companion Companion = null;
    
    public LanguageManager(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.lang.String> getSelectedLanguageCode() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.lang.String> getSourceLangCode() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.lang.String> getTargetLangCode() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object setSourceLanguage(@org.jetbrains.annotations.NotNull()
    java.lang.String code, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object setTargetLanguage(@org.jetbrains.annotations.NotNull()
    java.lang.String code, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object setLanguage(@org.jetbrains.annotations.NotNull()
    java.lang.String languageCode, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object swapLanguages(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00040\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00040\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\t"}, d2 = {"Lcom/example/bridgetranslator/LanguageManager$Companion;", "", "()V", "DEFAULT_SOURCE", "", "DEFAULT_TARGET", "SOURCE_LANG_KEY", "Landroidx/datastore/preferences/core/Preferences$Key;", "TARGET_LANG_KEY", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}