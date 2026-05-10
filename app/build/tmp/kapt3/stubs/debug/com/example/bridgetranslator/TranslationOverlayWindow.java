package com.example.bridgetranslator;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\b\u0010\t\u001a\u00020\nH\u0002J\u0006\u0010\u000b\u001a\u00020\nJ\u0010\u0010\f\u001a\u00020\n2\u0006\u0010\r\u001a\u00020\u000eH\u0002J\u0014\u0010\u000f\u001a\u00020\n2\f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00120\u0011R\u0010\u0010\u0005\u001a\u0004\u0018\u00010\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0013"}, d2 = {"Lcom/example/bridgetranslator/TranslationOverlayWindow;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "canvas", "Lcom/example/bridgetranslator/TranslationCanvas;", "wm", "Landroid/view/WindowManager;", "attachOverlay", "", "clearOverlays", "safeRemove", "v", "Landroid/view/View;", "showTranslations", "translations", "", "Lcom/example/bridgetranslator/TranslatedBlock;", "app_debug"})
public final class TranslationOverlayWindow {
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull()
    private final android.view.WindowManager wm = null;
    @org.jetbrains.annotations.Nullable()
    private com.example.bridgetranslator.TranslationCanvas canvas;
    
    public TranslationOverlayWindow(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    public final void showTranslations(@org.jetbrains.annotations.NotNull()
    java.util.List<com.example.bridgetranslator.TranslatedBlock> translations) {
    }
    
    public final void clearOverlays() {
    }
    
    private final void attachOverlay() {
    }
    
    private final void safeRemove(android.view.View v) {
    }
}