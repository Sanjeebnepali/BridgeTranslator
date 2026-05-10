package com.bridge.translator.overlay;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000H\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\b\u0010\u000b\u001a\u00020\fH\u0002J\b\u0010\r\u001a\u00020\u000eH\u0002J\u0006\u0010\u000f\u001a\u00020\u0010J\u001c\u0010\u0011\u001a\u00020\u00102\u0006\u0010\u0012\u001a\u00020\u00132\f\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00160\u0015R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0017"}, d2 = {"Lcom/bridge/translator/overlay/OverlayRenderer;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "canvasView", "Lcom/bridge/translator/overlay/TranslationCanvasView;", "isOverlayAdded", "", "windowManager", "Landroid/view/WindowManager;", "layoutParams", "Landroid/view/WindowManager$LayoutParams;", "overlayType", "", "removeOverlay", "", "showOverlay", "bitmap", "Landroid/graphics/Bitmap;", "blocks", "", "Lcom/bridge/translator/overlay/TranslatedBlock;", "app_debug"})
public final class OverlayRenderer {
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull()
    private final android.view.WindowManager windowManager = null;
    @org.jetbrains.annotations.NotNull()
    private final com.bridge.translator.overlay.TranslationCanvasView canvasView = null;
    private boolean isOverlayAdded = false;
    
    public OverlayRenderer(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    public final void showOverlay(@org.jetbrains.annotations.NotNull()
    android.graphics.Bitmap bitmap, @org.jetbrains.annotations.NotNull()
    java.util.List<com.bridge.translator.overlay.TranslatedBlock> blocks) {
    }
    
    public final void removeOverlay() {
    }
    
    private final android.view.WindowManager.LayoutParams layoutParams() {
        return null;
    }
    
    private final int overlayType() {
        return 0;
    }
}