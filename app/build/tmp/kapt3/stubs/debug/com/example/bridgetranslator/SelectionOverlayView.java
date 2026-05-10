package com.example.bridgetranslator;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000Z\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001:\u0001$B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0006\u0010\u001a\u001a\u00020\u001bJ\u0010\u0010\u001c\u001a\u00020\u001d2\u0006\u0010\u001e\u001a\u00020\u001fH\u0014J\u0010\u0010 \u001a\u00020\r2\u0006\u0010!\u001a\u00020\"H\u0016J\f\u0010#\u001a\u00020\u001b*\u00020\u0015H\u0002R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001c\u0010\u000e\u001a\u0004\u0018\u00010\u000fX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0010\u0010\u0011\"\u0004\b\u0012\u0010\u0013R\u000e\u0010\u0014\u001a\u00020\u0015X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0016\u001a\u00020\u0017X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0018\u001a\u00020\u0017X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0019\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006%"}, d2 = {"Lcom/example/bridgetranslator/SelectionOverlayView;", "Landroid/view/View;", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "borderPaint", "Landroid/graphics/Paint;", "dimPaint", "gestureDetector", "Landroid/view/GestureDetector;", "handlePaint", "instructionPaint", "isDrawing", "", "listener", "Lcom/example/bridgetranslator/SelectionOverlayView$SelectionListener;", "getListener", "()Lcom/example/bridgetranslator/SelectionOverlayView$SelectionListener;", "setListener", "(Lcom/example/bridgetranslator/SelectionOverlayView$SelectionListener;)V", "selRect", "Landroid/graphics/RectF;", "startX", "", "startY", "subInstructionPaint", "getSelectedRect", "Landroid/graphics/Rect;", "onDraw", "", "canvas", "Landroid/graphics/Canvas;", "onTouchEvent", "event", "Landroid/view/MotionEvent;", "toIntRect", "SelectionListener", "app_debug"})
public final class SelectionOverlayView extends android.view.View {
    @org.jetbrains.annotations.Nullable()
    private com.example.bridgetranslator.SelectionOverlayView.SelectionListener listener;
    private float startX = 0.0F;
    private float startY = 0.0F;
    @org.jetbrains.annotations.NotNull()
    private android.graphics.RectF selRect;
    private boolean isDrawing = false;
    @org.jetbrains.annotations.NotNull()
    private final android.graphics.Paint dimPaint = null;
    @org.jetbrains.annotations.NotNull()
    private final android.graphics.Paint borderPaint = null;
    @org.jetbrains.annotations.NotNull()
    private final android.graphics.Paint handlePaint = null;
    @org.jetbrains.annotations.NotNull()
    private final android.graphics.Paint instructionPaint = null;
    @org.jetbrains.annotations.NotNull()
    private final android.graphics.Paint subInstructionPaint = null;
    @org.jetbrains.annotations.NotNull()
    private final android.view.GestureDetector gestureDetector = null;
    
    public SelectionOverlayView(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super(null);
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.example.bridgetranslator.SelectionOverlayView.SelectionListener getListener() {
        return null;
    }
    
    public final void setListener(@org.jetbrains.annotations.Nullable()
    com.example.bridgetranslator.SelectionOverlayView.SelectionListener p0) {
    }
    
    @java.lang.Override()
    protected void onDraw(@org.jetbrains.annotations.NotNull()
    android.graphics.Canvas canvas) {
    }
    
    @java.lang.Override()
    public boolean onTouchEvent(@org.jetbrains.annotations.NotNull()
    android.view.MotionEvent event) {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final android.graphics.Rect getSelectedRect() {
        return null;
    }
    
    private final android.graphics.Rect toIntRect(android.graphics.RectF $this$toIntRect) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\bf\u0018\u00002\u00020\u0001J\b\u0010\u0002\u001a\u00020\u0003H&J\u0010\u0010\u0004\u001a\u00020\u00032\u0006\u0010\u0005\u001a\u00020\u0006H&\u00a8\u0006\u0007"}, d2 = {"Lcom/example/bridgetranslator/SelectionOverlayView$SelectionListener;", "", "onCancelled", "", "onSelectionComplete", "rect", "Landroid/graphics/Rect;", "app_debug"})
    public static abstract interface SelectionListener {
        
        public abstract void onSelectionComplete(@org.jetbrains.annotations.NotNull()
        android.graphics.Rect rect);
        
        public abstract void onCancelled();
    }
}