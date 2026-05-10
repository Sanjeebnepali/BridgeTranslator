package com.bridge.translator.service;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000h\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\t\n\u0002\b\u0010\n\u0002\u0010 \n\u0000\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006J&\u0010\u0007\u001a\u00020\b2\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\t\u001a\u00020\u00042\u0006\u0010\n\u001a\u00020\u00042\u0006\u0010\u000b\u001a\u00020\fJ.\u0010\r\u001a\u00020\u000e2\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\n\u001a\u00020\u00042\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\u000f\u001a\u00020\u00042\u0006\u0010\u0010\u001a\u00020\u0004J&\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u00042\u0006\u0010\u0016\u001a\u00020\f2\u0006\u0010\u0017\u001a\u00020\u0018J \u0010\u0019\u001a\u00020\u00042\u0006\u0010\u0015\u001a\u00020\u00042\u0006\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u001a\u001a\u00020\u001bH\u0002J \u0010\u001c\u001a\u00020\f2\u0006\u0010\u001d\u001a\u00020\f2\u0006\u0010\u001e\u001a\u00020\u001f2\u0006\u0010 \u001a\u00020\u001fH\u0002J \u0010!\u001a\u00020\f2\u0006\u0010\u001d\u001a\u00020\f2\u0006\u0010\u001e\u001a\u00020\u001f2\u0006\u0010 \u001a\u00020\u001fH\u0002J\u001d\u0010\"\u001a\u00020#2\b\u0010$\u001a\u0004\u0018\u00010%2\u0006\u0010&\u001a\u00020%\u00a2\u0006\u0002\u0010\'J\u0010\u0010(\u001a\u00020#2\u0006\u0010)\u001a\u00020\u001fH\u0002J\u0010\u0010*\u001a\u00020\u001f2\u0006\u0010)\u001a\u00020\u001fH\u0002J\u0010\u0010+\u001a\u00020\u001f2\u0006\u0010)\u001a\u00020\u001fH\u0002J\u0010\u0010,\u001a\u00020\u001f2\u0006\u0010-\u001a\u00020\u001fH\u0002J\u0018\u0010.\u001a\u00020\u001f2\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0016\u001a\u00020\fH\u0002J\u0018\u0010/\u001a\u00020\u001f2\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0016\u001a\u00020\fH\u0002J\u0016\u00100\u001a\u00020\u001f2\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0016\u001a\u00020\fJ\u000e\u00101\u001a\u00020%2\u0006\u0010\u0005\u001a\u00020\u0006J\u0010\u00102\u001a\u00020\u001b2\u0006\u0010\u0016\u001a\u00020\fH\u0002J\u0010\u00103\u001a\u00020\u001f2\u0006\u00104\u001a\u00020\u001fH\u0002J&\u00105\u001a\b\u0012\u0004\u0012\u00020\u0004062\u0006\u0010\u0015\u001a\u00020\u00042\u0006\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u001a\u001a\u00020\u001bH\u0002\u00a8\u00067"}, d2 = {"Lcom/bridge/translator/service/TextEraseHelper;", "", "()V", "computeHash", "", "bitmap", "Landroid/graphics/Bitmap;", "createItem", "Lcom/bridge/translator/service/OverlayTextItem;", "sourceText", "translatedText", "rawBounds", "Landroid/graphics/Rect;", "createTranslatedBlock", "Lcom/bridge/translator/service/TranslatedBlock;", "sourceLang", "targetLang", "drawWrappedText", "", "canvas", "Landroid/graphics/Canvas;", "text", "rect", "paint", "Landroid/graphics/Paint;", "ellipsize", "maxWidth", "", "exactBounds", "bounds", "maxW", "", "maxH", "expandBounds", "isChanged", "", "previous", "", "current", "(Ljava/lang/Long;J)Z", "isLikelyBackground", "color", "luminance", "quantize", "readableTextColor", "background", "sampleBackgroundAroundText", "sampleDominantBackground", "sampleDominantColor", "screenshotHash", "textSizeFor", "unquantize", "key", "wrapText", "", "app_debug"})
public final class TextEraseHelper {
    @org.jetbrains.annotations.NotNull()
    public static final com.bridge.translator.service.TextEraseHelper INSTANCE = null;
    
    private TextEraseHelper() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.bridge.translator.service.TranslatedBlock createTranslatedBlock(@org.jetbrains.annotations.NotNull()
    android.graphics.Bitmap bitmap, @org.jetbrains.annotations.NotNull()
    java.lang.String translatedText, @org.jetbrains.annotations.NotNull()
    android.graphics.Rect rawBounds, @org.jetbrains.annotations.NotNull()
    java.lang.String sourceLang, @org.jetbrains.annotations.NotNull()
    java.lang.String targetLang) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.bridge.translator.service.OverlayTextItem createItem(@org.jetbrains.annotations.NotNull()
    android.graphics.Bitmap bitmap, @org.jetbrains.annotations.NotNull()
    java.lang.String sourceText, @org.jetbrains.annotations.NotNull()
    java.lang.String translatedText, @org.jetbrains.annotations.NotNull()
    android.graphics.Rect rawBounds) {
        return null;
    }
    
    public final long screenshotHash(@org.jetbrains.annotations.NotNull()
    android.graphics.Bitmap bitmap) {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String computeHash(@org.jetbrains.annotations.NotNull()
    android.graphics.Bitmap bitmap) {
        return null;
    }
    
    public final boolean isChanged(@org.jetbrains.annotations.Nullable()
    java.lang.Long previous, long current) {
        return false;
    }
    
    public final void drawWrappedText(@org.jetbrains.annotations.NotNull()
    android.graphics.Canvas canvas, @org.jetbrains.annotations.NotNull()
    java.lang.String text, @org.jetbrains.annotations.NotNull()
    android.graphics.Rect rect, @org.jetbrains.annotations.NotNull()
    android.graphics.Paint paint) {
    }
    
    private final java.util.List<java.lang.String> wrapText(java.lang.String text, android.graphics.Paint paint, float maxWidth) {
        return null;
    }
    
    private final android.graphics.Rect expandBounds(android.graphics.Rect bounds, int maxW, int maxH) {
        return null;
    }
    
    private final android.graphics.Rect exactBounds(android.graphics.Rect bounds, int maxW, int maxH) {
        return null;
    }
    
    public final int sampleDominantColor(@org.jetbrains.annotations.NotNull()
    android.graphics.Bitmap bitmap, @org.jetbrains.annotations.NotNull()
    android.graphics.Rect rect) {
        return 0;
    }
    
    private final int sampleDominantBackground(android.graphics.Bitmap bitmap, android.graphics.Rect rect) {
        return 0;
    }
    
    private final int sampleBackgroundAroundText(android.graphics.Bitmap bitmap, android.graphics.Rect rect) {
        return 0;
    }
    
    private final int readableTextColor(int background) {
        return 0;
    }
    
    private final float textSizeFor(android.graphics.Rect rect) {
        return 0.0F;
    }
    
    private final boolean isLikelyBackground(int color) {
        return false;
    }
    
    private final int luminance(int color) {
        return 0;
    }
    
    private final int quantize(int color) {
        return 0;
    }
    
    private final int unquantize(int key) {
        return 0;
    }
    
    private final java.lang.String ellipsize(java.lang.String text, android.graphics.Paint paint, float maxWidth) {
        return null;
    }
}