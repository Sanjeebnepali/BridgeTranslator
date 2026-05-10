package com.bridge.translator.service;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000L\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\b\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0018\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u0007H\u0002J*\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u00142\b\b\u0002\u0010\u0015\u001a\u00020\u00102\b\b\u0002\u0010\u0016\u001a\u00020\u0010J\u0010\u0010\u0017\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rH\u0014J\u000e\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u0019J\u0014\u0010\u001b\u001a\u00020\u000b2\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006R\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001c"}, d2 = {"Lcom/bridge/translator/service/TranslationCanvasView;", "Landroid/view/View;", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "blocks", "", "Lcom/bridge/translator/service/TranslatedBlock;", "paint", "Landroid/graphics/Paint;", "drawBlock", "", "canvas", "Landroid/graphics/Canvas;", "block", "fitTextSize", "", "text", "", "rect", "Landroid/graphics/Rect;", "maxSize", "minSize", "onDraw", "readableTextColor", "", "bgColor", "setBlocks", "app_debug"})
public final class TranslationCanvasView extends android.view.View {
    @org.jetbrains.annotations.NotNull()
    private final android.graphics.Paint paint = null;
    @org.jetbrains.annotations.NotNull()
    private java.util.List<com.bridge.translator.service.TranslatedBlock> blocks;
    
    public TranslationCanvasView(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super(null);
    }
    
    public final void setBlocks(@org.jetbrains.annotations.NotNull()
    java.util.List<com.bridge.translator.service.TranslatedBlock> blocks) {
    }
    
    @java.lang.Override()
    protected void onDraw(@org.jetbrains.annotations.NotNull()
    android.graphics.Canvas canvas) {
    }
    
    private final void drawBlock(android.graphics.Canvas canvas, com.bridge.translator.service.TranslatedBlock block) {
    }
    
    public final float fitTextSize(@org.jetbrains.annotations.NotNull()
    java.lang.String text, @org.jetbrains.annotations.NotNull()
    android.graphics.Rect rect, float maxSize, float minSize) {
        return 0.0F;
    }
    
    public final int readableTextColor(int bgColor) {
        return 0;
    }
}