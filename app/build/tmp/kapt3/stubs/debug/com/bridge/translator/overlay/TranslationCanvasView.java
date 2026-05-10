package com.bridge.translator.overlay;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000L\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0006\u0010\f\u001a\u00020\rJ\u0018\u0010\u000e\u001a\u00020\r2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\tH\u0002J\u001e\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u0013J\u0010\u0010\u0019\u001a\u00020\r2\u0006\u0010\u000f\u001a\u00020\u0010H\u0014J\u0010\u0010\u001a\u001a\u00020\r2\b\u0010\u0005\u001a\u0004\u0018\u00010\u0006J\u0014\u0010\u001b\u001a\u00020\r2\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\bR\u0010\u0010\u0005\u001a\u0004\u0018\u00010\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001c"}, d2 = {"Lcom/bridge/translator/overlay/TranslationCanvasView;", "Landroid/view/View;", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "bitmap", "Landroid/graphics/Bitmap;", "blocks", "", "Lcom/bridge/translator/overlay/TranslatedBlock;", "paint", "Landroid/graphics/Paint;", "clearBlocks", "", "drawBlock", "canvas", "Landroid/graphics/Canvas;", "block", "fitTextSize", "", "text", "", "rect", "Landroid/graphics/Rect;", "startSize", "onDraw", "setBitmap", "setBlocks", "app_debug"})
public final class TranslationCanvasView extends android.view.View {
    @org.jetbrains.annotations.NotNull()
    private final android.graphics.Paint paint = null;
    @org.jetbrains.annotations.Nullable()
    private android.graphics.Bitmap bitmap;
    @org.jetbrains.annotations.NotNull()
    private java.util.List<com.bridge.translator.overlay.TranslatedBlock> blocks;
    
    public TranslationCanvasView(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super(null);
    }
    
    public final void setBitmap(@org.jetbrains.annotations.Nullable()
    android.graphics.Bitmap bitmap) {
    }
    
    public final void setBlocks(@org.jetbrains.annotations.NotNull()
    java.util.List<com.bridge.translator.overlay.TranslatedBlock> blocks) {
    }
    
    public final void clearBlocks() {
    }
    
    @java.lang.Override()
    protected void onDraw(@org.jetbrains.annotations.NotNull()
    android.graphics.Canvas canvas) {
    }
    
    private final void drawBlock(android.graphics.Canvas canvas, com.bridge.translator.overlay.TranslatedBlock block) {
    }
    
    public final float fitTextSize(@org.jetbrains.annotations.NotNull()
    java.lang.String text, @org.jetbrains.annotations.NotNull()
    android.graphics.Rect rect, float startSize) {
        return 0.0F;
    }
}