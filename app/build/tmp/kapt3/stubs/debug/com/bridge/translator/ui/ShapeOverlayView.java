package com.bridge.translator.ui;

/**
 * Transparent overlay that draws semi-transparent masks for each [DetectedShape].
 *
 * Features:
 * - Draws a shape-appropriate border/mask for RECTANGLE, CIRCLE, TRIANGLE, CYLINDER.
 * - Highlights the shape currently being processed with a distinct border colour.
 * - Supports pinch-to-zoom (scales the overlay coordinate space to match).
 * - Call [setShapes] on the UI thread whenever the detector produces new results.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000p\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\u0018\u00002\u00020\u0001B\u001b\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\u0002\u0010\u0006J\u0006\u0010\u0019\u001a\u00020\u001aJ\u0006\u0010\u001b\u001a\u00020\u001aJ\u0010\u0010\u001c\u001a\u00020\u001d2\u0006\u0010\u001e\u001a\u00020\u001fH\u0002J8\u0010 \u001a\u00020\u001a2\u0006\u0010!\u001a\u00020\"2\u0006\u0010#\u001a\u00020$2\u0006\u0010%\u001a\u00020\u001f2\u0006\u0010&\u001a\u00020\b2\u0006\u0010\'\u001a\u00020\b2\u0006\u0010(\u001a\u00020\u0014H\u0002J\u000e\u0010)\u001a\u00020\u001a2\u0006\u0010*\u001a\u00020\rJ\u0010\u0010+\u001a\u00020\u001a2\u0006\u0010!\u001a\u00020\"H\u0014J\u0010\u0010,\u001a\u00020-2\u0006\u0010.\u001a\u00020/H\u0016J$\u00100\u001a\u00020\u001a2\f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00120\u00112\u0006\u00101\u001a\u00020\r2\u0006\u00102\u001a\u00020\rJ\u0010\u00103\u001a\u00020\u001d2\u0006\u0010\u001e\u001a\u00020\u001fH\u0002R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00120\u0011X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u0014X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0015\u001a\u00020\u0014X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0016\u001a\u00020\u0014X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0017\u001a\u00020\u0014X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0018\u001a\u00020\u0014X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u00064"}, d2 = {"Lcom/bridge/translator/ui/ShapeOverlayView;", "Landroid/view/View;", "context", "Landroid/content/Context;", "attrs", "Landroid/util/AttributeSet;", "(Landroid/content/Context;Landroid/util/AttributeSet;)V", "borderPaint", "Landroid/graphics/Paint;", "fillPaint", "highlightBorderPaint", "highlightFillPaint", "processingIndex", "", "scaleDetector", "Landroid/view/ScaleGestureDetector;", "shapes", "", "Lcom/bridge/translator/processing/DetectedShape;", "sourceHeight", "", "sourceWidth", "zoomOffsetX", "zoomOffsetY", "zoomScale", "clearHighlight", "", "clearShapes", "cylinderPath", "Landroid/graphics/Path;", "r", "Landroid/graphics/RectF;", "drawShape", "canvas", "Landroid/graphics/Canvas;", "type", "Lcom/bridge/translator/processing/ShapeType;", "rect", "fill", "border", "rotation", "highlightShape", "index", "onDraw", "onTouchEvent", "", "event", "Landroid/view/MotionEvent;", "setShapes", "sourceW", "sourceH", "trianglePath", "app_debug"})
public final class ShapeOverlayView extends android.view.View {
    @org.jetbrains.annotations.NotNull()
    private java.util.List<com.bridge.translator.processing.DetectedShape> shapes;
    private int processingIndex = -1;
    private float sourceWidth = 1.0F;
    private float sourceHeight = 1.0F;
    private float zoomScale = 1.0F;
    private float zoomOffsetX = 0.0F;
    private float zoomOffsetY = 0.0F;
    @org.jetbrains.annotations.NotNull()
    private final android.graphics.Paint fillPaint = null;
    @org.jetbrains.annotations.NotNull()
    private final android.graphics.Paint borderPaint = null;
    @org.jetbrains.annotations.NotNull()
    private final android.graphics.Paint highlightBorderPaint = null;
    @org.jetbrains.annotations.NotNull()
    private final android.graphics.Paint highlightFillPaint = null;
    @org.jetbrains.annotations.NotNull()
    private final android.view.ScaleGestureDetector scaleDetector = null;
    
    @kotlin.jvm.JvmOverloads()
    public ShapeOverlayView(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.Nullable()
    android.util.AttributeSet attrs) {
        super(null);
    }
    
    /**
     * Supply the current list of detected shapes.
     *
     * @param shapes       Shapes in *source-bitmap* coordinate space.
     * @param sourceW      Width  of the source bitmap (used for coordinate mapping).
     * @param sourceH      Height of the source bitmap.
     */
    public final void setShapes(@org.jetbrains.annotations.NotNull()
    java.util.List<com.bridge.translator.processing.DetectedShape> shapes, int sourceW, int sourceH) {
    }
    
    public final void clearShapes() {
    }
    
    /**
     * Highlight one shape to indicate it is currently being translated.
     */
    public final void highlightShape(int index) {
    }
    
    public final void clearHighlight() {
    }
    
    @java.lang.Override()
    protected void onDraw(@org.jetbrains.annotations.NotNull()
    android.graphics.Canvas canvas) {
    }
    
    private final void drawShape(android.graphics.Canvas canvas, com.bridge.translator.processing.ShapeType type, android.graphics.RectF rect, android.graphics.Paint fill, android.graphics.Paint border, float rotation) {
    }
    
    /**
     * Isosceles triangle pointing upward.
     */
    private final android.graphics.Path trianglePath(android.graphics.RectF r) {
        return null;
    }
    
    /**
     * Cylinder outline: rectangle body with elliptical caps.
     * The ellipse height is 20 % of the rectangle height.
     */
    private final android.graphics.Path cylinderPath(android.graphics.RectF r) {
        return null;
    }
    
    @java.lang.Override()
    public boolean onTouchEvent(@org.jetbrains.annotations.NotNull()
    android.view.MotionEvent event) {
        return false;
    }
    
    @kotlin.jvm.JvmOverloads()
    public ShapeOverlayView(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super(null);
    }
}