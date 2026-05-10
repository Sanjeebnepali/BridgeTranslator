package com.example.bridgetranslator;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000R\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0000\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u0004H\u0002J\u0012\u0010\u0015\u001a\u00020\u00132\b\u0010\u0016\u001a\u0004\u0018\u00010\u0017H\u0014J\b\u0010\u0018\u001a\u00020\u0013H\u0014J.\u0010\u0019\u001a\u00020\u00132\u0006\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\u001b2\u0006\u0010\u001d\u001a\u00020\u001b2\u0006\u0010\u001e\u001a\u00020\u001bH\u0082@\u00a2\u0006\u0002\u0010\u001fJ\u0010\u0010 \u001a\u00020\u00132\u0006\u0010!\u001a\u00020\"H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000fX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u000fX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u000fX\u0082.\u00a2\u0006\u0002\n\u0000\u00a8\u0006#"}, d2 = {"Lcom/example/bridgetranslator/TranslateActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "()V", "btnSave", "Landroid/view/View;", "btnTranslate", "cardResult", "Lcom/google/android/material/card/MaterialCardView;", "engine", "Lcom/example/bridgetranslator/TranslationEngine;", "languageManager", "Lcom/example/bridgetranslator/LanguageManager;", "progressBar", "Landroid/widget/ProgressBar;", "tvResult", "Landroid/widget/TextView;", "tvSourceLang", "tvTargetLang", "hideKeyboard", "", "view", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "onDestroy", "saveToHistory", "source", "", "result", "srcCode", "tgtCode", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "setTranslating", "loading", "", "app_debug"})
public final class TranslateActivity extends androidx.appcompat.app.AppCompatActivity {
    private com.example.bridgetranslator.LanguageManager languageManager;
    private com.example.bridgetranslator.TranslationEngine engine;
    private android.widget.TextView tvSourceLang;
    private android.widget.TextView tvTargetLang;
    private android.widget.TextView tvResult;
    private com.google.android.material.card.MaterialCardView cardResult;
    private android.view.View btnTranslate;
    private android.view.View btnSave;
    private android.widget.ProgressBar progressBar;
    
    public TranslateActivity() {
        super();
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    private final void setTranslating(boolean loading) {
    }
    
    private final void hideKeyboard(android.view.View view) {
    }
    
    private final java.lang.Object saveToHistory(java.lang.String source, java.lang.String result, java.lang.String srcCode, java.lang.String tgtCode, kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    protected void onDestroy() {
    }
}