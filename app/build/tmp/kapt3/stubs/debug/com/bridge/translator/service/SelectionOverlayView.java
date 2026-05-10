package com.bridge.translator.service;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000P\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u0000  2\u00020\u0001:\u0002 !B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0010\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u0019H\u0014J\u0010\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\u001dH\u0016J\f\u0010\u001e\u001a\u00020\u001f*\u00020\u0011H\u0002R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001c\u0010\t\u001a\u0004\u0018\u00010\nX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u000b\u0010\f\"\u0004\b\r\u0010\u000eR\u000e\u0010\u000f\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0011X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0013X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0014\u001a\u00020\u0013X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0015\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\""}, d2 = {"Lcom/bridge/translator/service/SelectionOverlayView;", "Landroid/view/View;", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "borderPaint", "Landroid/graphics/Paint;", "handlePaint", "hintPaint", "listener", "Lcom/bridge/translator/service/SelectionOverlayView$Listener;", "getListener", "()Lcom/bridge/translator/service/SelectionOverlayView$Listener;", "setListener", "(Lcom/bridge/translator/service/SelectionOverlayView$Listener;)V", "scrimPaint", "selRect", "Landroid/graphics/RectF;", "startX", "", "startY", "subHintPaint", "onDraw", "", "canvas", "Landroid/graphics/Canvas;", "onTouchEvent", "", "event", "Landroid/view/MotionEvent;", "toIntRect", "Landroid/graphics/Rect;", "Companion", "Listener", "app_debug"})
public final class SelectionOverlayView extends android.view.View {
    @org.jetbrains.annotations.Nullable()
    private com.bridge.translator.service.SelectionOverlayView.Listener listener;
    private float startX = 0.0F;
    private float startY = 0.0F;
    @org.jetbrains.annotations.NotNull()
    private final android.graphics.RectF selRect = null;
    @org.jetbrains.annotations.NotNull()
    private final android.graphics.Paint scrimPaint = null;
    @org.jetbrains.annotations.NotNull()
    private final android.graphics.Paint borderPaint = null;
    @org.jetbrains.annotations.NotNull()
    private final android.graphics.Paint handlePaint = null;
    @org.jetbrains.annotations.NotNull()
    private final android.graphics.Paint hintPaint = null;
    @org.jetbrains.annotations.NotNull()
    private final android.graphics.Paint subHintPaint = null;
    @java.lang.Deprecated()
    public static final float MIN_SIZE = 40.0F;
    @org.jetbrains.annotations.NotNull()
    private static final com.bridge.translator.service.SelectionOverlayView.Companion Companion = null;
    
    public SelectionOverlayView(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super(null);
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.bridge.translator.service.SelectionOverlayView.Listener getListener() {
        return null;
    }
    
    public final void setListener(@org.jetbrains.annotations.Nullable()
    com.bridge.translator.service.SelectionOverlayView.Listener p0) {
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
    
    private final android.graphics.Rect toIntRect(android.graphics.RectF $this$toIntRect) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0000\b\u0082\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0005"}, d2 = {"Lcom/bridge/translator/service/SelectionOverlayView$Companion;", "", "()V", "MIN_SIZE", "", "app_debug"})
    static final class Companion {
        
        private Companion() {
            super();
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\bf\u0018\u00002\u00020\u0001J\b\u0010\u0002\u001a\u00020\u0003H&J\u0010\u0010\u0004\u001a\u00020\u00032\u0006\u0010\u0005\u001a\u00020\u0006H&\u00a8\u0006\u0007"}, d2 = {"Lcom/bridge/translator/service/SelectionOverlayView$Listener;", "", "onCancelled", "", "onSelected", "rect", "Landroid/graphics/Rect;", "app_debug"})
    public static abstract interface Listener {
        
        public abstract void onSelected(@org.jetbrains.annotations.NotNull()
        android.graphics.Rect rect);
        
        public abstract void onCancelled();
    }
}