package com.example.bridgetranslator;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\b\u0010\u000b\u001a\u0004\u0018\u00010\fJ\u0016\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0012J\u0006\u0010\u0013\u001a\u00020\u000eJ\b\u0010\u0014\u001a\u00020\u000eH\u0002J\u0006\u0010\u0015\u001a\u00020\u000eJ\b\u0010\u0016\u001a\u00020\u0010H\u0002J\b\u0010\u0017\u001a\u00020\u0010H\u0002J\b\u0010\u0018\u001a\u00020\u0010H\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0005\u001a\u0004\u0018\u00010\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0007\u001a\u0004\u0018\u00010\bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\t\u001a\u0004\u0018\u00010\nX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0019"}, d2 = {"Lcom/example/bridgetranslator/ScreenCaptureManager;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "imageReader", "Landroid/media/ImageReader;", "mediaProjection", "Landroid/media/projection/MediaProjection;", "virtualDisplay", "Landroid/hardware/display/VirtualDisplay;", "captureScreen", "Landroid/graphics/Bitmap;", "initialize", "", "resultCode", "", "data", "Landroid/content/Intent;", "onConfigurationChanged", "rebuildVirtualDisplay", "release", "screenDpi", "screenHeight", "screenWidth", "app_debug"})
public final class ScreenCaptureManager {
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    @org.jetbrains.annotations.Nullable()
    private android.media.projection.MediaProjection mediaProjection;
    @org.jetbrains.annotations.Nullable()
    private android.hardware.display.VirtualDisplay virtualDisplay;
    @org.jetbrains.annotations.Nullable()
    private android.media.ImageReader imageReader;
    
    public ScreenCaptureManager(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    private final int screenWidth() {
        return 0;
    }
    
    private final int screenHeight() {
        return 0;
    }
    
    private final int screenDpi() {
        return 0;
    }
    
    /**
     * Call this immediately after the system MediaProjection consent is granted.
     * Throws SecurityException on Android 14+ if the calling foreground service
     * did not declare FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION - let the caller catch it.
     */
    public final void initialize(int resultCode, @org.jetbrains.annotations.NotNull()
    android.content.Intent data) {
    }
    
    private final void rebuildVirtualDisplay() {
    }
    
    /**
     * Called from IO thread. Polls up to 500 ms for the first rendered frame.
     */
    @org.jetbrains.annotations.Nullable()
    public final android.graphics.Bitmap captureScreen() {
        return null;
    }
    
    public final void onConfigurationChanged() {
    }
    
    public final void release() {
    }
}