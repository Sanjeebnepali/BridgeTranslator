package com.bridge.translator.processing;

/**
 * Orchestrates per-shape OCR → language identification → translation →
 * erase-and-replace → composite back into a frame.
 *
 * All heavy work runs on [Dispatchers.Default] / [Dispatchers.IO]; the caller
 * supplies a UI-thread callback to report progress.
 *
 * Usage:
 * ```kotlin
 * val pipeline = TranslationPipeline()
 * val result   = pipeline.process(
 *    frame        = frozenBitmap,
 *    shapes       = detectedShapes,
 *    targetLang   = "en",
 *    fallbackLang = "ko",
 *    onShapeStart = { idx -> overlayView.highlightShape(idx) }
 * )
 * ```
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000t\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001:\u00019B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0006\u0010\r\u001a\u00020\u000eJ\u0018\u0010\u000f\u001a\u0004\u0018\u00010\u00042\u0006\u0010\u0010\u001a\u00020\u0004H\u0082@\u00a2\u0006\u0002\u0010\u0011J\u0010\u0010\u0012\u001a\u00020\u00042\u0006\u0010\u0013\u001a\u00020\u0014H\u0002Jo\u0010\u0015\u001a\u0014\u0012\u0004\u0012\u00020\u0017\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00190\u00180\u00162\u0006\u0010\u001a\u001a\u00020\u00172\f\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u001c0\u00182\u0006\u0010\u001d\u001a\u00020\u00042\b\b\u0002\u0010\u001e\u001a\u00020\u00042%\b\u0002\u0010\u001f\u001a\u001f\u0012\u0013\u0012\u00110!\u00a2\u0006\f\b\"\u0012\b\b#\u0012\u0004\b\b($\u0012\u0004\u0012\u00020\u000e\u0018\u00010 H\u0086@\u00a2\u0006\u0002\u0010%J8\u0010&\u001a\u0004\u0018\u00010\u00192\u0006\u0010\u001a\u001a\u00020\u00172\u0006\u0010\'\u001a\u00020\u001c2\u0006\u0010$\u001a\u00020!2\u0006\u0010\u001d\u001a\u00020\u00042\u0006\u0010\u001e\u001a\u00020\u0004H\u0082@\u00a2\u0006\u0002\u0010(J\u0018\u0010)\u001a\u0004\u0018\u00010\u00142\u0006\u0010*\u001a\u00020\u0017H\u0082@\u00a2\u0006\u0002\u0010+J.\u0010,\u001a\b\u0012\u0004\u0012\u00020-0\u00182\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010.\u001a\u00020\u00042\u0006\u0010/\u001a\u0002002\u0006\u00101\u001a\u000200H\u0002J&\u00102\u001a\u00020\u00042\u0006\u0010\u0010\u001a\u00020\u00042\u0006\u00103\u001a\u00020\u00042\u0006\u0010\u001d\u001a\u00020\u0004H\u0082@\u00a2\u0006\u0002\u00104J \u00105\u001a\u0004\u0018\u0001H6\"\u0004\b\u0000\u00106*\b\u0012\u0004\u0012\u0002H607H\u0082@\u00a2\u0006\u0002\u00108R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082D\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006:"}, d2 = {"Lcom/bridge/translator/processing/TranslationPipeline;", "", "()V", "TAG", "", "chineseRecognizer", "Lcom/google/mlkit/vision/text/TextRecognizer;", "devanagariRecognizer", "japaneseRecognizer", "koreanRecognizer", "langIdentifier", "Lcom/google/mlkit/nl/languageid/LanguageIdentifier;", "latinRecognizer", "close", "", "identifyLanguage", "text", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "mergeText", "visionText", "Lcom/google/mlkit/vision/text/Text;", "process", "Lkotlin/Pair;", "Landroid/graphics/Bitmap;", "", "Lcom/bridge/translator/processing/TranslationPipeline$ShapeResult;", "frame", "shapes", "Lcom/bridge/translator/processing/DetectedShape;", "targetLang", "fallbackLang", "onShapeStart", "Lkotlin/Function1;", "", "Lkotlin/ParameterName;", "name", "index", "(Landroid/graphics/Bitmap;Ljava/util/List;Ljava/lang/String;Ljava/lang/String;Lkotlin/jvm/functions/Function1;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "processShape", "shape", "(Landroid/graphics/Bitmap;Lcom/bridge/translator/processing/DetectedShape;ILjava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "runOcr", "bitmap", "(Landroid/graphics/Bitmap;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "textBlocksFromVisionText", "Lcom/bridge/translator/processing/TextBlock;", "translatedText", "scaleX", "", "scaleY", "translateText", "sourceLang", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "await", "T", "Lcom/google/android/gms/tasks/Task;", "(Lcom/google/android/gms/tasks/Task;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "ShapeResult", "app_debug"})
public final class TranslationPipeline {
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String TAG = "TranslationPipeline";
    @org.jetbrains.annotations.NotNull()
    private final com.google.mlkit.vision.text.TextRecognizer latinRecognizer = null;
    @org.jetbrains.annotations.NotNull()
    private final com.google.mlkit.vision.text.TextRecognizer koreanRecognizer = null;
    @org.jetbrains.annotations.NotNull()
    private final com.google.mlkit.vision.text.TextRecognizer chineseRecognizer = null;
    @org.jetbrains.annotations.NotNull()
    private final com.google.mlkit.vision.text.TextRecognizer japaneseRecognizer = null;
    @org.jetbrains.annotations.NotNull()
    private final com.google.mlkit.vision.text.TextRecognizer devanagariRecognizer = null;
    @org.jetbrains.annotations.NotNull()
    private final com.google.mlkit.nl.languageid.LanguageIdentifier langIdentifier = null;
    
    public TranslationPipeline() {
        super();
    }
    
    /**
     * Process every shape concurrently and composite each result back into a
     * copy of [frame].
     *
     * @param frame        Frozen full-resolution camera frame.
     * @param shapes       Shapes to process (from ShapeDetector).
     * @param targetLang   BCP-47 target language code (e.g. "en").
     * @param fallbackLang Source language used when auto-detect fails.
     * @param onShapeStart Called on the UI thread when processing of shape [index] begins.
     * @return             Composed result bitmap (all shapes translated) and the list of per-shape results.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object process(@org.jetbrains.annotations.NotNull()
    android.graphics.Bitmap frame, @org.jetbrains.annotations.NotNull()
    java.util.List<com.bridge.translator.processing.DetectedShape> shapes, @org.jetbrains.annotations.NotNull()
    java.lang.String targetLang, @org.jetbrains.annotations.NotNull()
    java.lang.String fallbackLang, @org.jetbrains.annotations.Nullable()
    kotlin.jvm.functions.Function1<? super java.lang.Integer, kotlin.Unit> onShapeStart, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Pair<android.graphics.Bitmap, ? extends java.util.List<com.bridge.translator.processing.TranslationPipeline.ShapeResult>>> $completion) {
        return null;
    }
    
    private final java.lang.Object processShape(android.graphics.Bitmap frame, com.bridge.translator.processing.DetectedShape shape, int index, java.lang.String targetLang, java.lang.String fallbackLang, kotlin.coroutines.Continuation<? super com.bridge.translator.processing.TranslationPipeline.ShapeResult> $completion) {
        return null;
    }
    
    private final java.lang.Object runOcr(android.graphics.Bitmap bitmap, kotlin.coroutines.Continuation<? super com.google.mlkit.vision.text.Text> $completion) {
        return null;
    }
    
    private final java.lang.String mergeText(com.google.mlkit.vision.text.Text visionText) {
        return null;
    }
    
    private final java.util.List<com.bridge.translator.processing.TextBlock> textBlocksFromVisionText(com.google.mlkit.vision.text.Text visionText, java.lang.String translatedText, float scaleX, float scaleY) {
        return null;
    }
    
    private final java.lang.Object identifyLanguage(java.lang.String text, kotlin.coroutines.Continuation<? super java.lang.String> $completion) {
        return null;
    }
    
    private final java.lang.Object translateText(java.lang.String text, java.lang.String sourceLang, java.lang.String targetLang, kotlin.coroutines.Continuation<? super java.lang.String> $completion) {
        return null;
    }
    
    public final void close() {
    }
    
    private final <T extends java.lang.Object>java.lang.Object await(com.google.android.gms.tasks.Task<T> $this$await, kotlin.coroutines.Continuation<? super T> $completion) {
        return null;
    }
    
    /**
     * Result for a single shape after the full pipeline.
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0013\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0086\b\u0018\u00002\u00020\u0001B5\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\u0007\u0012\u0006\u0010\t\u001a\u00020\u0007\u0012\u0006\u0010\n\u001a\u00020\u000b\u00a2\u0006\u0002\u0010\fJ\t\u0010\u0017\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0018\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0019\u001a\u00020\u0007H\u00c6\u0003J\t\u0010\u001a\u001a\u00020\u0007H\u00c6\u0003J\t\u0010\u001b\u001a\u00020\u0007H\u00c6\u0003J\t\u0010\u001c\u001a\u00020\u000bH\u00c6\u0003JE\u0010\u001d\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\u00072\b\b\u0002\u0010\t\u001a\u00020\u00072\b\b\u0002\u0010\n\u001a\u00020\u000bH\u00c6\u0001J\u0013\u0010\u001e\u001a\u00020\u001f2\b\u0010 \u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010!\u001a\u00020\u0003H\u00d6\u0001J\t\u0010\"\u001a\u00020\u0007H\u00d6\u0001R\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0011\u0010\n\u001a\u00020\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0011\u0010\t\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u000eR\u0011\u0010\b\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u000e\u00a8\u0006#"}, d2 = {"Lcom/bridge/translator/processing/TranslationPipeline$ShapeResult;", "", "shapeIndex", "", "shape", "Lcom/bridge/translator/processing/DetectedShape;", "originalText", "", "translatedText", "sourceLang", "processedBitmap", "Landroid/graphics/Bitmap;", "(ILcom/bridge/translator/processing/DetectedShape;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Landroid/graphics/Bitmap;)V", "getOriginalText", "()Ljava/lang/String;", "getProcessedBitmap", "()Landroid/graphics/Bitmap;", "getShape", "()Lcom/bridge/translator/processing/DetectedShape;", "getShapeIndex", "()I", "getSourceLang", "getTranslatedText", "component1", "component2", "component3", "component4", "component5", "component6", "copy", "equals", "", "other", "hashCode", "toString", "app_debug"})
    public static final class ShapeResult {
        private final int shapeIndex = 0;
        @org.jetbrains.annotations.NotNull()
        private final com.bridge.translator.processing.DetectedShape shape = null;
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String originalText = null;
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String translatedText = null;
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String sourceLang = null;
        @org.jetbrains.annotations.NotNull()
        private final android.graphics.Bitmap processedBitmap = null;
        
        public ShapeResult(int shapeIndex, @org.jetbrains.annotations.NotNull()
        com.bridge.translator.processing.DetectedShape shape, @org.jetbrains.annotations.NotNull()
        java.lang.String originalText, @org.jetbrains.annotations.NotNull()
        java.lang.String translatedText, @org.jetbrains.annotations.NotNull()
        java.lang.String sourceLang, @org.jetbrains.annotations.NotNull()
        android.graphics.Bitmap processedBitmap) {
            super();
        }
        
        public final int getShapeIndex() {
            return 0;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.bridge.translator.processing.DetectedShape getShape() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getOriginalText() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getTranslatedText() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getSourceLang() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final android.graphics.Bitmap getProcessedBitmap() {
            return null;
        }
        
        public final int component1() {
            return 0;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.bridge.translator.processing.DetectedShape component2() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component3() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component4() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component5() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final android.graphics.Bitmap component6() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.bridge.translator.processing.TranslationPipeline.ShapeResult copy(int shapeIndex, @org.jetbrains.annotations.NotNull()
        com.bridge.translator.processing.DetectedShape shape, @org.jetbrains.annotations.NotNull()
        java.lang.String originalText, @org.jetbrains.annotations.NotNull()
        java.lang.String translatedText, @org.jetbrains.annotations.NotNull()
        java.lang.String sourceLang, @org.jetbrains.annotations.NotNull()
        android.graphics.Bitmap processedBitmap) {
            return null;
        }
        
        @java.lang.Override()
        public boolean equals(@org.jetbrains.annotations.Nullable()
        java.lang.Object other) {
            return false;
        }
        
        @java.lang.Override()
        public int hashCode() {
            return 0;
        }
        
        @java.lang.Override()
        @org.jetbrains.annotations.NotNull()
        public java.lang.String toString() {
            return null;
        }
    }
}