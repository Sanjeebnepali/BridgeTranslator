package com.bridge.translator.engine;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000j\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010#\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010%\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001:\u0002+,B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0006\u0010\u0015\u001a\u00020\u0016Jb\u0010\u0017\u001a\u00020\u00162\u0006\u0010\u0018\u001a\u00020\n2\u0006\u0010\u0019\u001a\u00020\n26\u0010\u001a\u001a2\u0012\u0013\u0012\u00110\n\u00a2\u0006\f\b\u001c\u0012\b\b\u001d\u0012\u0004\b\b(\u001e\u0012\u0013\u0012\u00110\n\u00a2\u0006\f\b\u001c\u0012\b\b\u001d\u0012\u0004\b\b(\u001f\u0012\u0004\u0012\u00020\u00160\u001b2\u0012\u0010 \u001a\u000e\u0012\u0004\u0012\u00020\n\u0012\u0004\u0012\u00020\u00160!J(\u0010\"\u001a\u00020\u00162\u0006\u0010#\u001a\u00020$2\u0018\u0010\u001a\u001a\u0014\u0012\n\u0012\b\u0012\u0004\u0012\u00020%0\u0004\u0012\u0004\u0012\u00020\u00160!J`\u0010&\u001a\u00020\u00162\u0006\u0010\u0018\u001a\u00020\n2\u0006\u0010\u001e\u001a\u00020\n2\u0006\u0010\u0019\u001a\u00020\n2\b\b\u0002\u0010\'\u001a\u00020(2\u000e\b\u0002\u0010)\u001a\b\u0012\u0004\u0012\u00020\u00160*2\u0012\u0010\u001a\u001a\u000e\u0012\u0004\u0012\u00020\n\u0012\u0004\u0012\u00020\u00160!2\u0012\u0010 \u001a\u000e\u0012\u0004\u0012\u00020\n\u0012\u0004\u0012\u00020\u00160!R\u0017\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u0014\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001b\u0010\u000b\u001a\u00020\f8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u000f\u0010\u0010\u001a\u0004\b\r\u0010\u000eR\u000e\u0010\u0011\u001a\u00020\u0001X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010\u0012\u001a\u000e\u0012\u0004\u0012\u00020\n\u0012\u0004\u0012\u00020\u00140\u0013X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006-"}, d2 = {"Lcom/bridge/translator/engine/TranslationEngine;", "", "()V", "LANGUAGES", "", "Lcom/bridge/translator/engine/TranslationEngine$LangOption;", "getLANGUAGES", "()Ljava/util/List;", "readyPairs", "", "", "recognizer", "Lcom/google/mlkit/vision/text/TextRecognizer;", "getRecognizer", "()Lcom/google/mlkit/vision/text/TextRecognizer;", "recognizer$delegate", "Lkotlin/Lazy;", "translatorLock", "translators", "", "Lcom/google/mlkit/nl/translate/Translator;", "close", "", "detectAndTranslate", "text", "targetCode", "onResult", "Lkotlin/Function2;", "Lkotlin/ParameterName;", "name", "sourceCode", "translated", "onError", "Lkotlin/Function1;", "recognizeBlocks", "bitmap", "Landroid/graphics/Bitmap;", "Lcom/bridge/translator/engine/TranslationEngine$TextBlock;", "translate", "wifiOnly", "", "onDownloading", "Lkotlin/Function0;", "LangOption", "TextBlock", "app_debug"})
public final class TranslationEngine {
    @org.jetbrains.annotations.NotNull()
    private static final java.util.List<com.bridge.translator.engine.TranslationEngine.LangOption> LANGUAGES = null;
    @org.jetbrains.annotations.NotNull()
    private static final kotlin.Lazy recognizer$delegate = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.Object translatorLock = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Map<java.lang.String, com.google.mlkit.nl.translate.Translator> translators = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Set<java.lang.String> readyPairs = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.bridge.translator.engine.TranslationEngine INSTANCE = null;
    
    private TranslationEngine() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.bridge.translator.engine.TranslationEngine.LangOption> getLANGUAGES() {
        return null;
    }
    
    private final com.google.mlkit.vision.text.TextRecognizer getRecognizer() {
        return null;
    }
    
    /**
     * Runs OCR on [bitmap] and returns one [TextBlock] per ML Kit text block.
     * Lines inside a block are merged so Korean phrases translate as a phrase
     * instead of separate word fragments.
     */
    public final void recognizeBlocks(@org.jetbrains.annotations.NotNull()
    android.graphics.Bitmap bitmap, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.util.List<com.bridge.translator.engine.TranslationEngine.TextBlock>, kotlin.Unit> onResult) {
    }
    
    /**
     * Identifies the source language automatically, then translates [text] to [targetCode].
     * Falls back to English when identification fails or returns "und".
     * Callbacks fire on the main thread.
     */
    public final void detectAndTranslate(@org.jetbrains.annotations.NotNull()
    java.lang.String text, @org.jetbrains.annotations.NotNull()
    java.lang.String targetCode, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function2<? super java.lang.String, ? super java.lang.String, kotlin.Unit> onResult, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onError) {
    }
    
    /**
     * Downloads the ML Kit translation model if needed, then translates. Callbacks on main thread.
     */
    public final void translate(@org.jetbrains.annotations.NotNull()
    java.lang.String text, @org.jetbrains.annotations.NotNull()
    java.lang.String sourceCode, @org.jetbrains.annotations.NotNull()
    java.lang.String targetCode, boolean wifiOnly, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onDownloading, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onResult, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onError) {
    }
    
    public final void close() {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\t\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0005J\t\u0010\t\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\n\u001a\u00020\u0003H\u00c6\u0003J\u001d\u0010\u000b\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\f\u001a\u00020\r2\b\u0010\u000e\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u000f\u001a\u00020\u0010H\u00d6\u0001J\t\u0010\u0011\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\u0007\u00a8\u0006\u0012"}, d2 = {"Lcom/bridge/translator/engine/TranslationEngine$LangOption;", "", "label", "", "code", "(Ljava/lang/String;Ljava/lang/String;)V", "getCode", "()Ljava/lang/String;", "getLabel", "component1", "component2", "copy", "equals", "", "other", "hashCode", "", "toString", "app_debug"})
    public static final class LangOption {
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String label = null;
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String code = null;
        
        public LangOption(@org.jetbrains.annotations.NotNull()
        java.lang.String label, @org.jetbrains.annotations.NotNull()
        java.lang.String code) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getLabel() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getCode() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component1() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component2() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.bridge.translator.engine.TranslationEngine.LangOption copy(@org.jetbrains.annotations.NotNull()
        java.lang.String label, @org.jetbrains.annotations.NotNull()
        java.lang.String code) {
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
    
    /**
     * One detected text region with its screen-coordinate bounding box.
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\t\u0010\u000b\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\f\u001a\u00020\u0005H\u00c6\u0003J\u001d\u0010\r\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005H\u00c6\u0001J\u0013\u0010\u000e\u001a\u00020\u000f2\b\u0010\u0010\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0011\u001a\u00020\u0012H\u00d6\u0001J\t\u0010\u0013\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\n\u00a8\u0006\u0014"}, d2 = {"Lcom/bridge/translator/engine/TranslationEngine$TextBlock;", "", "text", "", "bounds", "Landroid/graphics/Rect;", "(Ljava/lang/String;Landroid/graphics/Rect;)V", "getBounds", "()Landroid/graphics/Rect;", "getText", "()Ljava/lang/String;", "component1", "component2", "copy", "equals", "", "other", "hashCode", "", "toString", "app_debug"})
    public static final class TextBlock {
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String text = null;
        @org.jetbrains.annotations.NotNull()
        private final android.graphics.Rect bounds = null;
        
        public TextBlock(@org.jetbrains.annotations.NotNull()
        java.lang.String text, @org.jetbrains.annotations.NotNull()
        android.graphics.Rect bounds) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getText() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final android.graphics.Rect getBounds() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component1() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final android.graphics.Rect component2() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.bridge.translator.engine.TranslationEngine.TextBlock copy(@org.jetbrains.annotations.NotNull()
        java.lang.String text, @org.jetbrains.annotations.NotNull()
        android.graphics.Rect bounds) {
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