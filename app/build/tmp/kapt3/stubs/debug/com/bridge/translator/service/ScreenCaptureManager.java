package com.bridge.translator.service;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000X\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u0000 \"2\u00020\u0001:\u0001\"B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\b\u0010\u0015\u001a\u00020\u0016H\u0002J\b\u0010\u0017\u001a\u0004\u0018\u00010\u0006J\b\u0010\u0018\u001a\u0004\u0018\u00010\u0006J\u0016\u0010\u0019\u001a\u00020\u00162\u0006\u0010\u001a\u001a\u00020\b2\u0006\u0010\u001b\u001a\u00020\u001cJ\u0006\u0010\u001d\u001a\u00020\u0016J\u0006\u0010\u001e\u001a\u00020\u0016J\b\u0010\u001f\u001a\u00020 H\u0002J\u0006\u0010!\u001a\u00020\u0016R\u0010\u0010\u0005\u001a\u0004\u0018\u00010\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\n\u001a\u0004\u0018\u00010\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0011\u0010\f\u001a\u00020\r8F\u00a2\u0006\u0006\u001a\u0004\b\f\u0010\u000eR\u0010\u0010\u000f\u001a\u0004\u0018\u00010\u0010X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0011\u001a\u0004\u0018\u00010\u0012X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0013\u001a\u0004\u0018\u00010\u0014X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006#"}, d2 = {"Lcom/bridge/translator/service/ScreenCaptureManager;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "cachedFrame", "Landroid/graphics/Bitmap;", "captureHeight", "", "captureWidth", "imageReader", "Landroid/media/ImageReader;", "isInitialized", "", "()Z", "mediaProjection", "Landroid/media/projection/MediaProjection;", "projectionCallback", "Landroid/media/projection/MediaProjection$Callback;", "virtualDisplay", "Landroid/hardware/display/VirtualDisplay;", "buildVirtualDisplay", "", "captureFrame", "captureScreen", "initialize", "resultCode", "data", "Landroid/content/Intent;", "invalidateCache", "onConfigurationChanged", "realDisplayMetrics", "Landroid/util/DisplayMetrics;", "release", "Companion", "app_debug"})
public final class ScreenCaptureManager {
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    @org.jetbrains.annotations.Nullable()
    private android.media.projection.MediaProjection mediaProjection;
    @org.jetbrains.annotations.Nullable()
    private android.hardware.display.VirtualDisplay virtualDisplay;
    @org.jetbrains.annotations.Nullable()
    private android.media.ImageReader imageReader;
    @org.jetbrains.annotations.Nullable()
    private android.media.projection.MediaProjection.Callback projectionCallback;
    private int captureWidth = 0;
    private int captureHeight = 0;
    @org.jetbrains.annotations.Nullable()
    private android.graphics.Bitmap cachedFrame;
    @org.jetbrains.annotations.NotNull()
    @java.lang.Deprecated()
    public static final java.lang.String TAG = "ScreenCaptureManager";
    @org.jetbrains.annotations.NotNull()
    @java.lang.Deprecated()
    public static final java.lang.String FLOW_TAG = "BridgeFlow";
    @org.jetbrains.annotations.NotNull()
    private static final com.bridge.translator.service.ScreenCaptureManager.Companion Companion = null;
    
    public ScreenCaptureManager(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    public final boolean isInitialized() {
        return false;
    }
    
    private final android.util.DisplayMetrics realDisplayMetrics() {
        return null;
    }
    
    /**
     * Call immediately after the system MediaProjection consent is granted.
     * Throws SecurityException on Android 14+ if the foreground service did not declare
     * FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION - the caller must catch and handle it.
     */
    public final void initialize(int resultCode, @org.jetbrains.annotations.NotNull()
    android.content.Intent data) {
    }
    
    private final void buildVirtualDisplay() {
    }
    
    /**
     * Polls up to 500 ms for the first rendered frame and returns it as a Bitmap.
     * Must be called from a background thread (contains Thread.sleep).
     * Returns null if no frame arrives in time.
     */
    @org.jetbrains.annotations.Nullable()
    public final android.graphics.Bitmap captureScreen() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final android.graphics.Bitmap captureFrame() {
        return null;
    }
    
    public final void invalidateCache() {
    }
    
    /**
     * A MediaProjection consent token is single-use on newer Android versions. Do not rebuild the
     * VirtualDisplay after rotation/config changes; release this session and ask for consent again.
     */
    public final void onConfigurationChanged() {
    }
    
    public final void release() {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0082\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0006"}, d2 = {"Lcom/bridge/translator/service/ScreenCaptureManager$Companion;", "", "()V", "FLOW_TAG", "", "TAG", "app_debug"})
    static final class Companion {
        
        private Companion() {
            super();
        }
    }
}