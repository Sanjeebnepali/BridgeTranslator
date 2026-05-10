package com.bridge.translator.processing;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000f\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0000\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\u0018\u0000 #2\u00020\u0001:\u0001#B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u001c\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000f0\u000e2\u0006\u0010\u0010\u001a\u00020\u0011H\u0086@\u00a2\u0006\u0002\u0010\u0012J\u0010\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u0016H\u0002J\u0010\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\u001aH\u0002J \u0010\u001b\u001a\u0004\u0018\u00010\u000f2\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0010\u001a\u00020\u0011H\u0082@\u00a2\u0006\u0002\u0010\u001cJ\u0018\u0010\u001d\u001a\u00020\u001a2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u001e\u001a\u00020\u001fH\u0002J\u0018\u0010 \u001a\u00020\u001a2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u001e\u001a\u00020\u001fH\u0002J\f\u0010!\u001a\u00020\"*\u00020\u0003H\u0002R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006$"}, d2 = {"Lcom/bridge/translator/processing/ScreenAnalyser;", "", "targetLang", "", "(Ljava/lang/String;)V", "langDetector", "Lcom/google/mlkit/nl/languageid/LanguageIdentifier;", "languageFallback", "Lcom/bridge/translator/processing/LanguageFallback;", "orientationDetector", "Lcom/bridge/translator/processing/TextOrientationDetector;", "recognizer", "Lcom/google/mlkit/vision/text/TextRecognizer;", "analyse", "", "Lcom/bridge/translator/processing/TextBlock;", "bitmap", "Landroid/graphics/Bitmap;", "(Landroid/graphics/Bitmap;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "estimateAlignment", "Lcom/bridge/translator/processing/Alignment;", "block", "Lcom/google/mlkit/vision/text/Text$TextBlock;", "luminance", "", "color", "", "processBlock", "(Lcom/google/mlkit/vision/text/Text$TextBlock;Landroid/graphics/Bitmap;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "sampleOutside", "rect", "Landroid/graphics/Rect;", "sampleTextColor", "containsHangul", "", "Companion", "app_debug"})
public final class ScreenAnalyser {
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String targetLang = null;
    @org.jetbrains.annotations.NotNull()
    private final com.google.mlkit.vision.text.TextRecognizer recognizer = null;
    @org.jetbrains.annotations.NotNull()
    private final com.google.mlkit.nl.languageid.LanguageIdentifier langDetector = null;
    @org.jetbrains.annotations.NotNull()
    private final com.bridge.translator.processing.TextOrientationDetector orientationDetector = null;
    @org.jetbrains.annotations.NotNull()
    private final com.bridge.translator.processing.LanguageFallback languageFallback = null;
    @org.jetbrains.annotations.NotNull()
    @java.lang.Deprecated()
    public static final java.lang.String FLOW_TAG = "BridgeFlow";
    @org.jetbrains.annotations.NotNull()
    private static final com.bridge.translator.processing.ScreenAnalyser.Companion Companion = null;
    
    public ScreenAnalyser(@org.jetbrains.annotations.NotNull()
    java.lang.String targetLang) {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object analyse(@org.jetbrains.annotations.NotNull()
    android.graphics.Bitmap bitmap, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.bridge.translator.processing.TextBlock>> $completion) {
        return null;
    }
    
    private final java.lang.Object processBlock(com.google.mlkit.vision.text.Text.TextBlock block, android.graphics.Bitmap bitmap, kotlin.coroutines.Continuation<? super com.bridge.translator.processing.TextBlock> $completion) {
        return null;
    }
    
    private final boolean containsHangul(java.lang.String $this$containsHangul) {
        return false;
    }
    
    private final int sampleTextColor(android.graphics.Bitmap bitmap, android.graphics.Rect rect) {
        return 0;
    }
    
    private final int sampleOutside(android.graphics.Bitmap bitmap, android.graphics.Rect rect) {
        return 0;
    }
    
    private final com.bridge.translator.processing.Alignment estimateAlignment(com.google.mlkit.vision.text.Text.TextBlock block) {
        return null;
    }
    
    private final float luminance(int color) {
        return 0.0F;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\b\u0082\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0005"}, d2 = {"Lcom/bridge/translator/processing/ScreenAnalyser$Companion;", "", "()V", "FLOW_TAG", "", "app_debug"})
    static final class Companion {
        
        private Companion() {
            super();
        }
    }
}