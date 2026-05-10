package com.bridge.translator.camera.data;

/**
 * A single detected text region annotated with orientation information.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u001b\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001BI\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u0012\u0006\u0010\n\u001a\u00020\u0007\u0012\u0006\u0010\u000b\u001a\u00020\u0007\u0012\b\b\u0002\u0010\f\u001a\u00020\u0003\u0012\b\b\u0002\u0010\r\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\u000eJ\t\u0010\u001b\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001c\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u001d\u001a\u00020\u0007H\u00c6\u0003J\t\u0010\u001e\u001a\u00020\tH\u00c6\u0003J\t\u0010\u001f\u001a\u00020\u0007H\u00c6\u0003J\t\u0010 \u001a\u00020\u0007H\u00c6\u0003J\t\u0010!\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\"\u001a\u00020\u0007H\u00c6\u0003JY\u0010#\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\u00072\b\b\u0002\u0010\u000b\u001a\u00020\u00072\b\b\u0002\u0010\f\u001a\u00020\u00032\b\b\u0002\u0010\r\u001a\u00020\u0007H\u00c6\u0001J\u0013\u0010$\u001a\u00020%2\b\u0010&\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\'\u001a\u00020(H\u00d6\u0001J\t\u0010)\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0011\u0010\u000b\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0010R\u0011\u0010\f\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u0011\u0010\r\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0010R\u0011\u0010\b\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018R\u0011\u0010\n\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u0010R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u0015\u00a8\u0006*"}, d2 = {"Lcom/bridge/translator/camera/data/TextOrientationBlock;", "", "text", "", "boundingBox", "Landroid/graphics/RectF;", "angle", "", "orientation", "Lcom/bridge/translator/camera/data/TextOrientation;", "orientationConfidence", "estimatedFontSizePt", "language", "ocrConfidence", "(Ljava/lang/String;Landroid/graphics/RectF;FLcom/bridge/translator/camera/data/TextOrientation;FFLjava/lang/String;F)V", "getAngle", "()F", "getBoundingBox", "()Landroid/graphics/RectF;", "getEstimatedFontSizePt", "getLanguage", "()Ljava/lang/String;", "getOcrConfidence", "getOrientation", "()Lcom/bridge/translator/camera/data/TextOrientation;", "getOrientationConfidence", "getText", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "copy", "equals", "", "other", "hashCode", "", "toString", "app_debug"})
public final class TextOrientationBlock {
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String text = null;
    @org.jetbrains.annotations.NotNull()
    private final android.graphics.RectF boundingBox = null;
    private final float angle = 0.0F;
    @org.jetbrains.annotations.NotNull()
    private final com.bridge.translator.camera.data.TextOrientation orientation = null;
    private final float orientationConfidence = 0.0F;
    private final float estimatedFontSizePt = 0.0F;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String language = null;
    private final float ocrConfidence = 0.0F;
    
    public TextOrientationBlock(@org.jetbrains.annotations.NotNull()
    java.lang.String text, @org.jetbrains.annotations.NotNull()
    android.graphics.RectF boundingBox, float angle, @org.jetbrains.annotations.NotNull()
    com.bridge.translator.camera.data.TextOrientation orientation, float orientationConfidence, float estimatedFontSizePt, @org.jetbrains.annotations.NotNull()
    java.lang.String language, float ocrConfidence) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getText() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final android.graphics.RectF getBoundingBox() {
        return null;
    }
    
    public final float getAngle() {
        return 0.0F;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.bridge.translator.camera.data.TextOrientation getOrientation() {
        return null;
    }
    
    public final float getOrientationConfidence() {
        return 0.0F;
    }
    
    public final float getEstimatedFontSizePt() {
        return 0.0F;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getLanguage() {
        return null;
    }
    
    public final float getOcrConfidence() {
        return 0.0F;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component1() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final android.graphics.RectF component2() {
        return null;
    }
    
    public final float component3() {
        return 0.0F;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.bridge.translator.camera.data.TextOrientation component4() {
        return null;
    }
    
    public final float component5() {
        return 0.0F;
    }
    
    public final float component6() {
        return 0.0F;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component7() {
        return null;
    }
    
    public final float component8() {
        return 0.0F;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.bridge.translator.camera.data.TextOrientationBlock copy(@org.jetbrains.annotations.NotNull()
    java.lang.String text, @org.jetbrains.annotations.NotNull()
    android.graphics.RectF boundingBox, float angle, @org.jetbrains.annotations.NotNull()
    com.bridge.translator.camera.data.TextOrientation orientation, float orientationConfidence, float estimatedFontSizePt, @org.jetbrains.annotations.NotNull()
    java.lang.String language, float ocrConfidence) {
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