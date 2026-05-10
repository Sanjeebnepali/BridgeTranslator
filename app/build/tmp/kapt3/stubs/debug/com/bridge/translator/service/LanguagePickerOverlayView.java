package com.bridge.translator.service;

/**
 * Full-screen overlay for choosing the floating bubble translation target.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000J\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u0000 \u001b2\u00020\u0001:\u0001\u001bB+\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0012\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00070\u0005\u0012\b\b\u0002\u0010\b\u001a\u00020\u0006\u00a2\u0006\u0002\u0010\tJ\b\u0010\u0010\u001a\u00020\u0007H\u0002J\b\u0010\u0011\u001a\u00020\u0007H\u0002J\u0010\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u0013H\u0002J\u0010\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u0018H\u0017J\u0010\u0010\u0019\u001a\u00020\u00072\u0006\u0010\u001a\u001a\u00020\u000eH\u0002R\u001a\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00070\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\n\u001a\u0004\u0018\u00010\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R \u0010\u000b\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u000e\u0012\u0004\u0012\u00020\u000f0\r0\fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001c"}, d2 = {"Lcom/bridge/translator/service/LanguagePickerOverlayView;", "Landroid/widget/FrameLayout;", "context", "Landroid/content/Context;", "onPicked", "Lkotlin/Function1;", "", "", "title", "(Landroid/content/Context;Lkotlin/jvm/functions/Function1;Ljava/lang/String;)V", "pendingTarget", "pickerButtons", "", "Lkotlin/Pair;", "Lcom/bridge/translator/engine/TranslationEngine$LangOption;", "Landroid/widget/TextView;", "buildUi", "confirmLang", "dp", "", "v", "onTouchEvent", "", "event", "Landroid/view/MotionEvent;", "selectLang", "lang", "Companion", "app_debug"})
public final class LanguagePickerOverlayView extends android.widget.FrameLayout {
    @org.jetbrains.annotations.NotNull()
    private final kotlin.jvm.functions.Function1<java.lang.String, kotlin.Unit> onPicked = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String title = null;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String pendingTarget;
    @org.jetbrains.annotations.NotNull()
    private java.util.List<? extends kotlin.Pair<com.bridge.translator.engine.TranslationEngine.LangOption, ? extends android.widget.TextView>> pickerButtons;
    private static final int MATCH = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
    private static final int WRAP = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
    @org.jetbrains.annotations.NotNull()
    public static final com.bridge.translator.service.LanguagePickerOverlayView.Companion Companion = null;
    
    public LanguagePickerOverlayView(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onPicked, @org.jetbrains.annotations.NotNull()
    java.lang.String title) {
        super(null);
    }
    
    @java.lang.Override()
    @android.annotation.SuppressLint(value = {"ClickableViewAccessibility"})
    public boolean onTouchEvent(@org.jetbrains.annotations.NotNull()
    android.view.MotionEvent event) {
        return false;
    }
    
    private final void buildUi() {
    }
    
    private final void selectLang(com.bridge.translator.engine.TranslationEngine.LangOption lang) {
    }
    
    private final void confirmLang() {
    }
    
    private final int dp(int v) {
        return 0;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082D\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0082D\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0006"}, d2 = {"Lcom/bridge/translator/service/LanguagePickerOverlayView$Companion;", "", "()V", "MATCH", "", "WRAP", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}