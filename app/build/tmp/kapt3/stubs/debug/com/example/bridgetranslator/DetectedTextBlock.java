package com.example.bridgetranslator;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b$\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001BY\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\u0003\u0012\u0010\b\u0002\u0010\t\u001a\n\u0012\u0004\u0012\u00020\u000b\u0018\u00010\n\u0012\b\b\u0002\u0010\f\u001a\u00020\u0007\u0012\b\b\u0002\u0010\r\u001a\u00020\u000e\u0012\b\b\u0002\u0010\u000f\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\u0010J\t\u0010(\u001a\u00020\u0003H\u00c6\u0003J\t\u0010)\u001a\u00020\u0005H\u00c6\u0003J\t\u0010*\u001a\u00020\u0007H\u00c6\u0003J\u000b\u0010+\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u0016\u0010,\u001a\n\u0012\u0004\u0012\u00020\u000b\u0018\u00010\nH\u00c6\u0003\u00a2\u0006\u0002\u0010\u0019J\t\u0010-\u001a\u00020\u0007H\u00c6\u0003J\t\u0010.\u001a\u00020\u000eH\u00c6\u0003J\t\u0010/\u001a\u00020\u0007H\u00c6\u0003Jh\u00100\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\u00032\u0010\b\u0002\u0010\t\u001a\n\u0012\u0004\u0012\u00020\u000b\u0018\u00010\n2\b\b\u0002\u0010\f\u001a\u00020\u00072\b\b\u0002\u0010\r\u001a\u00020\u000e2\b\b\u0002\u0010\u000f\u001a\u00020\u0007H\u00c6\u0001\u00a2\u0006\u0002\u00101J\u0013\u00102\u001a\u0002032\b\u00104\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u00105\u001a\u000206H\u00d6\u0001J\t\u00107\u001a\u00020\u0003H\u00d6\u0001R\u001a\u0010\f\u001a\u00020\u0007X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0011\u0010\u0012\"\u0004\b\u0013\u0010\u0014R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0012R$\u0010\t\u001a\n\u0012\u0004\u0012\u00020\u000b\u0018\u00010\nX\u0086\u000e\u00a2\u0006\u0010\n\u0002\u0010\u001c\u001a\u0004\b\u0018\u0010\u0019\"\u0004\b\u001a\u0010\u001bR\u001c\u0010\b\u001a\u0004\u0018\u00010\u0003X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u001d\u0010\u001e\"\u0004\b\u001f\u0010 R\u001a\u0010\r\u001a\u00020\u000eX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b!\u0010\"\"\u0004\b#\u0010$R\u001a\u0010\u000f\u001a\u00020\u0007X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b%\u0010\u0012\"\u0004\b&\u0010\u0014R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\'\u0010\u001e\u00a8\u00068"}, d2 = {"Lcom/example/bridgetranslator/DetectedTextBlock;", "", "text", "", "bounds", "Landroid/graphics/Rect;", "confidence", "", "language", "cornerPoints", "", "Landroid/graphics/Point;", "angle", "orientation", "Lcom/example/bridgetranslator/TextOrientation;", "orientationConfidence", "(Ljava/lang/String;Landroid/graphics/Rect;FLjava/lang/String;[Landroid/graphics/Point;FLcom/example/bridgetranslator/TextOrientation;F)V", "getAngle", "()F", "setAngle", "(F)V", "getBounds", "()Landroid/graphics/Rect;", "getConfidence", "getCornerPoints", "()[Landroid/graphics/Point;", "setCornerPoints", "([Landroid/graphics/Point;)V", "[Landroid/graphics/Point;", "getLanguage", "()Ljava/lang/String;", "setLanguage", "(Ljava/lang/String;)V", "getOrientation", "()Lcom/example/bridgetranslator/TextOrientation;", "setOrientation", "(Lcom/example/bridgetranslator/TextOrientation;)V", "getOrientationConfidence", "setOrientationConfidence", "getText", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "copy", "(Ljava/lang/String;Landroid/graphics/Rect;FLjava/lang/String;[Landroid/graphics/Point;FLcom/example/bridgetranslator/TextOrientation;F)Lcom/example/bridgetranslator/DetectedTextBlock;", "equals", "", "other", "hashCode", "", "toString", "app_debug"})
public final class DetectedTextBlock {
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String text = null;
    @org.jetbrains.annotations.NotNull()
    private final android.graphics.Rect bounds = null;
    private final float confidence = 0.0F;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String language;
    @org.jetbrains.annotations.Nullable()
    private android.graphics.Point[] cornerPoints;
    private float angle;
    @org.jetbrains.annotations.NotNull()
    private com.example.bridgetranslator.TextOrientation orientation;
    private float orientationConfidence;
    
    public DetectedTextBlock(@org.jetbrains.annotations.NotNull()
    java.lang.String text, @org.jetbrains.annotations.NotNull()
    android.graphics.Rect bounds, float confidence, @org.jetbrains.annotations.Nullable()
    java.lang.String language, @org.jetbrains.annotations.Nullable()
    android.graphics.Point[] cornerPoints, float angle, @org.jetbrains.annotations.NotNull()
    com.example.bridgetranslator.TextOrientation orientation, float orientationConfidence) {
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
    
    public final float getConfidence() {
        return 0.0F;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getLanguage() {
        return null;
    }
    
    public final void setLanguage(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final android.graphics.Point[] getCornerPoints() {
        return null;
    }
    
    public final void setCornerPoints(@org.jetbrains.annotations.Nullable()
    android.graphics.Point[] p0) {
    }
    
    public final float getAngle() {
        return 0.0F;
    }
    
    public final void setAngle(float p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.bridgetranslator.TextOrientation getOrientation() {
        return null;
    }
    
    public final void setOrientation(@org.jetbrains.annotations.NotNull()
    com.example.bridgetranslator.TextOrientation p0) {
    }
    
    public final float getOrientationConfidence() {
        return 0.0F;
    }
    
    public final void setOrientationConfidence(float p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component1() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final android.graphics.Rect component2() {
        return null;
    }
    
    public final float component3() {
        return 0.0F;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component4() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final android.graphics.Point[] component5() {
        return null;
    }
    
    public final float component6() {
        return 0.0F;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.bridgetranslator.TextOrientation component7() {
        return null;
    }
    
    public final float component8() {
        return 0.0F;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.bridgetranslator.DetectedTextBlock copy(@org.jetbrains.annotations.NotNull()
    java.lang.String text, @org.jetbrains.annotations.NotNull()
    android.graphics.Rect bounds, float confidence, @org.jetbrains.annotations.Nullable()
    java.lang.String language, @org.jetbrains.annotations.Nullable()
    android.graphics.Point[] cornerPoints, float angle, @org.jetbrains.annotations.NotNull()
    com.example.bridgetranslator.TextOrientation orientation, float orientationConfidence) {
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