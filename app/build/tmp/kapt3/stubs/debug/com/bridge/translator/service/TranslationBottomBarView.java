package com.bridge.translator.service;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0007\u0018\u00002\u00020\u0001BE\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u0012\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u0012\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u0012\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u00a2\u0006\u0002\u0010\nJ\u0018\u0010\u000e\u001a\u00020\f2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0012H\u0002J\u0010\u0010\u0013\u001a\u00020\u00122\u0006\u0010\u0014\u001a\u00020\u0012H\u0002J\b\u0010\u0015\u001a\u00020\fH\u0002J\u0016\u0010\u0016\u001a\u00020\u00062\u0006\u0010\u0017\u001a\u00020\u00102\u0006\u0010\u0018\u001a\u00020\u0010R\u0014\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0019"}, d2 = {"Lcom/bridge/translator/service/TranslationBottomBarView;", "Landroid/widget/LinearLayout;", "context", "Landroid/content/Context;", "onSourceClick", "Lkotlin/Function0;", "", "onTargetClick", "onTranslateClick", "onCloseClick", "(Landroid/content/Context;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;)V", "sourceButton", "Landroid/widget/TextView;", "targetButton", "circleButton", "label", "", "color", "", "dp", "value", "pillButton", "setLanguages", "sourceLabel", "targetLabel", "app_debug"})
public final class TranslationBottomBarView extends android.widget.LinearLayout {
    @org.jetbrains.annotations.NotNull()
    private final kotlin.jvm.functions.Function0<kotlin.Unit> onSourceClick = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.jvm.functions.Function0<kotlin.Unit> onTargetClick = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.jvm.functions.Function0<kotlin.Unit> onTranslateClick = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.jvm.functions.Function0<kotlin.Unit> onCloseClick = null;
    @org.jetbrains.annotations.NotNull()
    private final android.widget.TextView sourceButton = null;
    @org.jetbrains.annotations.NotNull()
    private final android.widget.TextView targetButton = null;
    
    public TranslationBottomBarView(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onSourceClick, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onTargetClick, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onTranslateClick, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onCloseClick) {
        super(null);
    }
    
    public final void setLanguages(@org.jetbrains.annotations.NotNull()
    java.lang.String sourceLabel, @org.jetbrains.annotations.NotNull()
    java.lang.String targetLabel) {
    }
    
    private final android.widget.TextView pillButton() {
        return null;
    }
    
    private final android.widget.TextView circleButton(java.lang.String label, int color) {
        return null;
    }
    
    private final int dp(int value) {
        return 0;
    }
}