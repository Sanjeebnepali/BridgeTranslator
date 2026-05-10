package com.example.bridgetranslator;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0006\u0010\u0007\u001a\u00020\bJ\u001e\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\nH\u0086@\u00a2\u0006\u0002\u0010\u000eJ\u0010\u0010\u000f\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fH\u0002J\u0016\u0010\u0010\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fH\u0082@\u00a2\u0006\u0002\u0010\u0011J\u0016\u0010\u0012\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fH\u0082@\u00a2\u0006\u0002\u0010\u0011J\u0010\u0010\u0013\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fH\u0002J\u0010\u0010\u0014\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fH\u0002J\u0018\u0010\u0015\u001a\u00020\n2\u0006\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\nH\u0002J\u0016\u0010\u0019\u001a\u00020\n2\u0006\u0010\u001a\u001a\u00020\u001bH\u0082@\u00a2\u0006\u0002\u0010\u001cR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001d"}, d2 = {"Lcom/example/bridgetranslator/FileTextExtractor;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "recognizer", "Lcom/google/mlkit/vision/text/TextRecognizer;", "close", "", "extract", "", "uri", "Landroid/net/Uri;", "mimeType", "(Landroid/net/Uri;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "extractFromDocx", "extractFromImage", "(Landroid/net/Uri;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "extractFromPdf", "extractFromPptx", "extractFromText", "extractXmlText", "stream", "Ljava/io/InputStream;", "targetElement", "recognizeText", "image", "Lcom/google/mlkit/vision/common/InputImage;", "(Lcom/google/mlkit/vision/common/InputImage;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public final class FileTextExtractor {
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull()
    private final com.google.mlkit.vision.text.TextRecognizer recognizer = null;
    
    public FileTextExtractor(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object extract(@org.jetbrains.annotations.NotNull()
    android.net.Uri uri, @org.jetbrains.annotations.NotNull()
    java.lang.String mimeType, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.String> $completion) {
        return null;
    }
    
    private final java.lang.Object extractFromImage(android.net.Uri uri, kotlin.coroutines.Continuation<? super java.lang.String> $completion) {
        return null;
    }
    
    private final java.lang.Object extractFromPdf(android.net.Uri uri, kotlin.coroutines.Continuation<? super java.lang.String> $completion) {
        return null;
    }
    
    private final java.lang.String extractFromText(android.net.Uri uri) {
        return null;
    }
    
    private final java.lang.String extractFromDocx(android.net.Uri uri) {
        return null;
    }
    
    private final java.lang.String extractFromPptx(android.net.Uri uri) {
        return null;
    }
    
    private final java.lang.String extractXmlText(java.io.InputStream stream, java.lang.String targetElement) {
        return null;
    }
    
    private final java.lang.Object recognizeText(com.google.mlkit.vision.common.InputImage image, kotlin.coroutines.Continuation<? super java.lang.String> $completion) {
        return null;
    }
    
    public final void close() {
    }
}