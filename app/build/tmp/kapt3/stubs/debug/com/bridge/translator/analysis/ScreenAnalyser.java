package com.bridge.translator.analysis;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000J\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u001c\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\n2\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000fJ\u000e\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0013J\u000e\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u0017J\u0012\u0010\u0018\u001a\u0004\u0018\u00010\u00192\u0006\u0010\u0012\u001a\u00020\u0013H\u0002J\u0010\u0010\u001a\u001a\u00020\u00172\u0006\u0010\u001b\u001a\u00020\u0017H\u0002J\u0016\u0010\u001c\u001a\u00020\u00172\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u001d\u001a\u00020\u0019J \u0010\u001e\u001a\u00020\u00172\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u001d\u001a\u00020\u00192\b\b\u0002\u0010\u001b\u001a\u00020\u0017R\u001b\u0010\u0003\u001a\u00020\u00048BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0007\u0010\b\u001a\u0004\b\u0005\u0010\u0006\u00a8\u0006\u001f"}, d2 = {"Lcom/bridge/translator/analysis/ScreenAnalyser;", "", "()V", "recognizer", "Lcom/google/mlkit/vision/text/TextRecognizer;", "getRecognizer", "()Lcom/google/mlkit/vision/text/TextRecognizer;", "recognizer$delegate", "Lkotlin/Lazy;", "analyse", "", "Lcom/bridge/translator/analysis/AnalysedBlock;", "bitmap", "Landroid/graphics/Bitmap;", "targetLanguage", "", "estimateAlignment", "Lcom/bridge/translator/analysis/Alignment;", "block", "Lcom/google/mlkit/vision/text/Text$TextBlock;", "luminance", "", "color", "", "mergedRect", "Landroid/graphics/Rect;", "readableTextColor", "bgColor", "sampleBackground", "rect", "sampleTextColor", "app_debug"})
public final class ScreenAnalyser {
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy recognizer$delegate = null;
    
    public ScreenAnalyser() {
        super();
    }
    
    private final com.google.mlkit.vision.text.TextRecognizer getRecognizer() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.bridge.translator.analysis.AnalysedBlock> analyse(@org.jetbrains.annotations.NotNull()
    android.graphics.Bitmap bitmap, @org.jetbrains.annotations.NotNull()
    java.lang.String targetLanguage) {
        return null;
    }
    
    private final android.graphics.Rect mergedRect(com.google.mlkit.vision.text.Text.TextBlock block) {
        return null;
    }
    
    public final int sampleBackground(@org.jetbrains.annotations.NotNull()
    android.graphics.Bitmap bitmap, @org.jetbrains.annotations.NotNull()
    android.graphics.Rect rect) {
        return 0;
    }
    
    public final int sampleTextColor(@org.jetbrains.annotations.NotNull()
    android.graphics.Bitmap bitmap, @org.jetbrains.annotations.NotNull()
    android.graphics.Rect rect, int bgColor) {
        return 0;
    }
    
    public final float luminance(int color) {
        return 0.0F;
    }
    
    private final int readableTextColor(int bgColor) {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.bridge.translator.analysis.Alignment estimateAlignment(@org.jetbrains.annotations.NotNull()
    com.google.mlkit.vision.text.Text.TextBlock block) {
        return null;
    }
}