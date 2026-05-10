package com.bridge.translator.processing;

/**
 * Replace the original text in [source] with the translation drawn directly
 * onto a page-coloured erase patch.
 *
 * Strict design constraints (no card style, no translucent box, no stroke
 * shadow, no outline). The output should look like the original screenshot
 * with the text rectangles repainted in the page colour and the translation
 * written over them in a colour close to the original ink.
 *
 * For each [TextBlock]:
 *  1. Sample background from a 4-px ring just outside the bounding box.
 *  2. Sample text colour by ranking inside-pixels by RGB distance from
 *     the sampled bg and taking the median of the top 30%.
 *  3. Erase the rect with a solid fill + 2-px alpha feather.
 *  4. Choose a Sans-Serif typeface (bold if [TextBlock.isBold]) at a
 *     natural size of `0.65 × bbox.height` (floor of [MIN_FONT_PX]).
 *  5. Fit using single-line → shrink-to-70% → wrap-multiline with
 *     ellipsis on the final line (max 4 lines).
 *  6. Pick alignment: centre for tall bold headers, opposite for clearly
 *     right-margined columns, normal otherwise.
 *
 * Sampling uses bulk [Bitmap.getPixels] reads — never per-pixel
 * [Bitmap.getPixel] in a nested loop.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000z\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0000\n\u0002\u0010\u0015\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0013\u0018\u0000 @2\u00020\u0001:\u0001@B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0018\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u0006H\u0002J\u0010\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bH\u0002J0\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u00062\u0006\u0010\u0013\u001a\u00020\u00062\u0006\u0010\u0014\u001a\u00020\u0015H\u0002J8\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\u001d2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u001e\u001a\u00020\r2\u0006\u0010\u001f\u001a\u00020\tH\u0002J\u001c\u0010 \u001a\u00020!2\u0006\u0010\"\u001a\u00020!2\f\u0010#\u001a\b\u0012\u0004\u0012\u00020%0$J0\u0010&\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u00192\u0006\u0010\'\u001a\u00020\u00112\u0006\u0010(\u001a\u00020\u00062\u0006\u0010\u0012\u001a\u00020\u00062\u0006\u0010\u0013\u001a\u00020\u0006H\u0002J\u0018\u0010)\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010*\u001a\u00020!H\u0002J \u0010+\u001a\u00020\u00062\u0016\u0010,\u001a\u0012\u0012\u0004\u0012\u00020\u00060-j\b\u0012\u0004\u0012\u00020\u0006`.H\u0002JH\u0010/\u001a\u00020\u00172\u0006\u00100\u001a\u00020!2\u0006\u00101\u001a\u00020\u00062\u0006\u00102\u001a\u00020\u00062\u0006\u00103\u001a\u00020\u00062\u0006\u00104\u001a\u00020\u00062\u0016\u0010\n\u001a\u0012\u0012\u0004\u0012\u00020\u00060-j\b\u0012\u0004\u0012\u00020\u0006`.H\u0002J\u0010\u00105\u001a\u00020\u00042\u0006\u00106\u001a\u00020\u0006H\u0002JX\u00107\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u001c\u001a\u00020\u001d2\u0006\u00108\u001a\u00020\u001b2\u0006\u00109\u001a\u00020\t2\u0006\u0010(\u001a\u00020\u00062\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0012\u001a\u00020\u00062\u0006\u0010\u0013\u001a\u00020\u00062\u0006\u0010:\u001a\u00020\u0015H\u0002J\u001f\u0010;\u001a\u0004\u0018\u00010\u00062\u0006\u00100\u001a\u00020!2\u0006\u0010\u0010\u001a\u00020\u0011H\u0002\u00a2\u0006\u0002\u0010<J\u0018\u0010=\u001a\u00020\u000b2\u0006\u00100\u001a\u00020!2\u0006\u0010\u0010\u001a\u00020\u0011H\u0002J\'\u0010>\u001a\u0004\u0018\u00010\u00062\u0006\u00100\u001a\u00020!2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010(\u001a\u00020\u0006H\u0002\u00a2\u0006\u0002\u0010?\u00a8\u0006A"}, d2 = {"Lcom/bridge/translator/processing/BitmapTextEraser;", "", "()V", "colorDistance", "", "c1", "", "c2", "colorVariance", "", "pixels", "", "detectAlignment", "Landroid/text/Layout$Alignment;", "isBold", "", "bbox", "Landroid/graphics/Rect;", "imageWidth", "imageHeight", "upstream", "Lcom/bridge/translator/processing/Alignment;", "drawSingleLine", "", "canvas", "Landroid/graphics/Canvas;", "paint", "Landroid/text/TextPaint;", "text", "", "alignment", "textSize", "eraseAndReplace", "Landroid/graphics/Bitmap;", "source", "blocks", "", "Lcom/bridge/translator/processing/TextBlock;", "eraseWithFeather", "rect", "bgColor", "isBackgroundComplex", "src", "median", "values", "Ljava/util/ArrayList;", "Lkotlin/collections/ArrayList;", "readStripPixels", "bitmap", "x0", "y0", "x1", "y1", "relativeLuminance", "c", "renderFitted", "textPaint", "naturalSize", "upstreamAlignment", "sampleRingMedianColor", "(Landroid/graphics/Bitmap;Landroid/graphics/Rect;)Ljava/lang/Integer;", "sampleRingPixels", "sampleTextColor", "(Landroid/graphics/Bitmap;Landroid/graphics/Rect;I)Ljava/lang/Integer;", "Companion", "app_debug"})
public final class BitmapTextEraser {
    @java.lang.Deprecated()
    public static final float MIN_FONT_PX = 22.0F;
    @java.lang.Deprecated()
    public static final float MAX_FONT_PX_RATIO = 0.65F;
    @java.lang.Deprecated()
    public static final float OVERFLOW_SHRINK_LIMIT = 0.5F;
    @java.lang.Deprecated()
    public static final int MAX_LINES = 4;
    @java.lang.Deprecated()
    public static final int EDGE_FEATHER_PX = 2;
    @java.lang.Deprecated()
    public static final int RING_PX = 4;
    @java.lang.Deprecated()
    public static final float MAX_EXPAND_RATIO = 1.6F;
    @java.lang.Deprecated()
    public static final float COMPLEXITY_VARIANCE_THRESHOLD = 0.12F;
    @org.jetbrains.annotations.NotNull()
    private static final com.bridge.translator.processing.BitmapTextEraser.Companion Companion = null;
    
    public BitmapTextEraser() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final android.graphics.Bitmap eraseAndReplace(@org.jetbrains.annotations.NotNull()
    android.graphics.Bitmap source, @org.jetbrains.annotations.NotNull()
    java.util.List<com.bridge.translator.processing.TextBlock> blocks) {
        return null;
    }
    
    /**
     * Median R/G/B across a 4-px-wide ring just outside [bbox]. Skips sides
     * that fall off the bitmap. Returns `null` only if every side is
     * unsampleable (box pinned to all four edges of the bitmap).
     */
    private final int[] sampleRingPixels(android.graphics.Bitmap bitmap, android.graphics.Rect bbox) {
        return null;
    }
    
    /**
     * Median R/G/B across a 4-px-wide ring just outside [bbox]. Skips sides
     * that fall off the bitmap. Returns `null` only if every side is
     * unsampleable (box pinned to all four edges of the bitmap).
     */
    private final java.lang.Integer sampleRingMedianColor(android.graphics.Bitmap bitmap, android.graphics.Rect bbox) {
        return null;
    }
    
    private final boolean isBackgroundComplex(android.graphics.Rect bbox, android.graphics.Bitmap src) {
        return false;
    }
    
    /**
     * Inside [bbox], find pixels most distant from [bgColor], take the top
     * 30%, return the median R/G/B. Returns `null` if the box is too small
     * or has no clearly distant cluster (caller falls back to black/white).
     */
    private final java.lang.Integer sampleTextColor(android.graphics.Bitmap bitmap, android.graphics.Rect bbox, int bgColor) {
        return null;
    }
    
    private final void eraseWithFeather(android.graphics.Canvas canvas, android.graphics.Rect rect, int bgColor, int imageWidth, int imageHeight) {
    }
    
    private final void renderFitted(android.graphics.Canvas canvas, android.graphics.Rect bbox, java.lang.String text, android.text.TextPaint textPaint, float naturalSize, int bgColor, boolean isBold, int imageWidth, int imageHeight, com.bridge.translator.processing.Alignment upstreamAlignment) {
    }
    
    private final void drawSingleLine(android.graphics.Canvas canvas, android.text.TextPaint paint, java.lang.String text, android.graphics.Rect bbox, android.text.Layout.Alignment alignment, float textSize) {
    }
    
    private final android.text.Layout.Alignment detectAlignment(boolean isBold, android.graphics.Rect bbox, int imageWidth, int imageHeight, com.bridge.translator.processing.Alignment upstream) {
        return null;
    }
    
    private final void readStripPixels(android.graphics.Bitmap bitmap, int x0, int y0, int x1, int y1, java.util.ArrayList<java.lang.Integer> pixels) {
    }
    
    private final int median(java.util.ArrayList<java.lang.Integer> values) {
        return 0;
    }
    
    private final double colorDistance(int c1, int c2) {
        return 0.0;
    }
    
    private final double relativeLuminance(int c) {
        return 0.0;
    }
    
    private final float colorVariance(int[] pixels) {
        return 0.0F;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0000\n\u0002\u0010\b\n\u0002\b\u0007\b\u0082\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0006X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\u0006X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\r"}, d2 = {"Lcom/bridge/translator/processing/BitmapTextEraser$Companion;", "", "()V", "COMPLEXITY_VARIANCE_THRESHOLD", "", "EDGE_FEATHER_PX", "", "MAX_EXPAND_RATIO", "MAX_FONT_PX_RATIO", "MAX_LINES", "MIN_FONT_PX", "OVERFLOW_SHRINK_LIMIT", "RING_PX", "app_debug"})
    static final class Companion {
        
        private Companion() {
            super();
        }
    }
}