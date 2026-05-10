package com.bridge.translator.processing;

/**
 * Stateless helper for cropping, rotating, masking, and re-scaling bitmaps
 * in preparation for OCR or for compositing translated regions back into a frame.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0006\n\u0002\u0010\u0007\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0016\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u00062\u0006\u0010\b\u001a\u00020\tJ \u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u000b2\u0006\u0010\r\u001a\u00020\u00042\u0006\u0010\u000e\u001a\u00020\u0004H\u0002J&\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00062\u0006\u0010\u0012\u001a\u00020\u00062\u0006\u0010\u0013\u001a\u00020\u000b2\u0006\u0010\b\u001a\u00020\tJ \u0010\u0014\u001a\u00020\u00062\u0006\u0010\u0015\u001a\u00020\u00062\u0006\u0010\u0013\u001a\u00020\u000b2\b\b\u0002\u0010\u0016\u001a\u00020\u0017J\u0018\u0010\u0018\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u00062\b\b\u0002\u0010\u0019\u001a\u00020\u0004J\u0018\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\t2\u0006\u0010\f\u001a\u00020\u000bH\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001d"}, d2 = {"Lcom/bridge/translator/processing/ImagePreprocessor;", "", "()V", "MAX_OCR_EDGE", "", "applyShapeMask", "Landroid/graphics/Bitmap;", "bitmap", "shapeType", "Lcom/bridge/translator/processing/ShapeType;", "clampRect", "Landroid/graphics/RectF;", "r", "w", "h", "compositeShapeBack", "", "target", "shapeBitmap", "bounds", "cropAndRotate", "source", "rotationDegrees", "", "scaleForOcr", "maxEdge", "shapeClipPath", "Landroid/graphics/Path;", "type", "app_debug"})
public final class ImagePreprocessor {
    private static final int MAX_OCR_EDGE = 720;
    @org.jetbrains.annotations.NotNull()
    public static final com.bridge.translator.processing.ImagePreprocessor INSTANCE = null;
    
    private ImagePreprocessor() {
        super();
    }
    
    /**
     * Crop [source] to [bounds] and apply optional [rotationDegrees] around the
     * crop centre.  Returns a new, mutable ARGB_8888 bitmap.
     *
     * @param source          Original bitmap.
     * @param bounds          Region in [source] coordinate space.
     * @param rotationDegrees Clockwise rotation to apply after cropping (0 = none).
     * @return                Cropped (and rotated) bitmap.
     */
    @org.jetbrains.annotations.NotNull()
    public final android.graphics.Bitmap cropAndRotate(@org.jetbrains.annotations.NotNull()
    android.graphics.Bitmap source, @org.jetbrains.annotations.NotNull()
    android.graphics.RectF bounds, float rotationDegrees) {
        return null;
    }
    
    /**
     * Down-scale [bitmap] so that its longest edge is at most [maxEdge] pixels.
     * Returns the original if it already fits.
     */
    @org.jetbrains.annotations.NotNull()
    public final android.graphics.Bitmap scaleForOcr(@org.jetbrains.annotations.NotNull()
    android.graphics.Bitmap bitmap, int maxEdge) {
        return null;
    }
    
    /**
     * Apply a shape-mask to [bitmap] so that pixels outside the shape are
     * transparent.  Useful for feeding a precise region to BitmapTextEraser.
     *
     * @param bitmap     Source bitmap (will not be modified).
     * @param shapeType  The type of mask to apply.
     * @return           New ARGB_8888 bitmap with transparent outside.
     */
    @org.jetbrains.annotations.NotNull()
    public final android.graphics.Bitmap applyShapeMask(@org.jetbrains.annotations.NotNull()
    android.graphics.Bitmap bitmap, @org.jetbrains.annotations.NotNull()
    com.bridge.translator.processing.ShapeType shapeType) {
        return null;
    }
    
    /**
     * Composite [shapeBitmap] (a translated+erased crop) back into [target] at [bounds].
     *
     * This mutates [target].
     *
     * @param target       Full-frame bitmap to composite into.
     * @param shapeBitmap  Processed crop that should be pasted back.
     * @param bounds       Where in [target] to paste.
     * @param shapeType    Clip path applied before pasting.
     */
    public final void compositeShapeBack(@org.jetbrains.annotations.NotNull()
    android.graphics.Bitmap target, @org.jetbrains.annotations.NotNull()
    android.graphics.Bitmap shapeBitmap, @org.jetbrains.annotations.NotNull()
    android.graphics.RectF bounds, @org.jetbrains.annotations.NotNull()
    com.bridge.translator.processing.ShapeType shapeType) {
    }
    
    private final android.graphics.RectF clampRect(android.graphics.RectF r, int w, int h) {
        return null;
    }
    
    private final android.graphics.Path shapeClipPath(com.bridge.translator.processing.ShapeType type, android.graphics.RectF r) {
        return null;
    }
}