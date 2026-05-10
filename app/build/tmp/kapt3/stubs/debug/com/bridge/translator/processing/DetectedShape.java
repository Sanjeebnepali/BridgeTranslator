package com.bridge.translator.processing;

/**
 * Represents a single geometric region detected in a camera frame.
 *
 * @param bounds          Bounding rectangle in the *original* (un-scaled) bitmap coordinate space.
 * @param shapeType       Classified shape (RECTANGLE / CIRCLE / TRIANGLE / CYLINDER).
 * @param rotationDegrees Clockwise rotation of the shape relative to upright, in degrees.
 * @param confidence      Detection confidence in [0, 1].
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000F\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u000f\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B)\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u0012\b\b\u0002\u0010\b\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\tJ\t\u0010\u0011\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0012\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0013\u001a\u00020\u0007H\u00c6\u0003J\t\u0010\u0014\u001a\u00020\u0007H\u00c6\u0003J1\u0010\u0015\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\u0007H\u00c6\u0001J\t\u0010\u0016\u001a\u00020\u0017H\u00d6\u0001J\u0013\u0010\u0018\u001a\u00020\u00192\b\u0010\u001a\u001a\u0004\u0018\u00010\u001bH\u00d6\u0003J\t\u0010\u001c\u001a\u00020\u0017H\u00d6\u0001J\t\u0010\u001d\u001a\u00020\u001eH\u00d6\u0001J\u0019\u0010\u001f\u001a\u00020 2\u0006\u0010!\u001a\u00020\"2\u0006\u0010#\u001a\u00020\u0017H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\b\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\rR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010\u00a8\u0006$"}, d2 = {"Lcom/bridge/translator/processing/DetectedShape;", "Landroid/os/Parcelable;", "bounds", "Landroid/graphics/RectF;", "shapeType", "Lcom/bridge/translator/processing/ShapeType;", "rotationDegrees", "", "confidence", "(Landroid/graphics/RectF;Lcom/bridge/translator/processing/ShapeType;FF)V", "getBounds", "()Landroid/graphics/RectF;", "getConfidence", "()F", "getRotationDegrees", "getShapeType", "()Lcom/bridge/translator/processing/ShapeType;", "component1", "component2", "component3", "component4", "copy", "describeContents", "", "equals", "", "other", "", "hashCode", "toString", "", "writeToParcel", "", "parcel", "Landroid/os/Parcel;", "flags", "app_debug"})
@kotlinx.parcelize.Parcelize()
public final class DetectedShape implements android.os.Parcelable {
    @org.jetbrains.annotations.NotNull()
    private final android.graphics.RectF bounds = null;
    @org.jetbrains.annotations.NotNull()
    private final com.bridge.translator.processing.ShapeType shapeType = null;
    private final float rotationDegrees = 0.0F;
    private final float confidence = 0.0F;
    
    public DetectedShape(@org.jetbrains.annotations.NotNull()
    android.graphics.RectF bounds, @org.jetbrains.annotations.NotNull()
    com.bridge.translator.processing.ShapeType shapeType, float rotationDegrees, float confidence) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final android.graphics.RectF getBounds() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.bridge.translator.processing.ShapeType getShapeType() {
        return null;
    }
    
    public final float getRotationDegrees() {
        return 0.0F;
    }
    
    public final float getConfidence() {
        return 0.0F;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final android.graphics.RectF component1() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.bridge.translator.processing.ShapeType component2() {
        return null;
    }
    
    public final float component3() {
        return 0.0F;
    }
    
    public final float component4() {
        return 0.0F;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.bridge.translator.processing.DetectedShape copy(@org.jetbrains.annotations.NotNull()
    android.graphics.RectF bounds, @org.jetbrains.annotations.NotNull()
    com.bridge.translator.processing.ShapeType shapeType, float rotationDegrees, float confidence) {
        return null;
    }
    
    @java.lang.Override()
    public int describeContents() {
        return 0;
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
    
    @java.lang.Override()
    public void writeToParcel(@org.jetbrains.annotations.NotNull()
    android.os.Parcel parcel, int flags) {
    }
}