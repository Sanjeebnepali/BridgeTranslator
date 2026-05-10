package com.bridge.translator.service;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\b\u0010\n\u001a\u0004\u0018\u00010\u000bJ\b\u0010\f\u001a\u0004\u0018\u00010\u000bJ\u0016\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0012J\u0006\u0010\u0013\u001a\u00020\u000eJ\u0006\u0010\u0014\u001a\u00020\u000eJ\u0006\u0010\u0015\u001a\u00020\u000eR\u0011\u0010\u0005\u001a\u00020\u00068F\u00a2\u0006\u0006\u001a\u0004\b\u0005\u0010\u0007R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0016"}, d2 = {"Lcom/bridge/translator/service/ScreenCaptureHelper;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "isInitialized", "", "()Z", "manager", "Lcom/bridge/translator/service/ScreenCaptureManager;", "captureBitmap", "Landroid/graphics/Bitmap;", "captureFrame", "initialize", "", "resultCode", "", "data", "Landroid/content/Intent;", "invalidateCache", "onConfigurationChanged", "release", "app_debug"})
public final class ScreenCaptureHelper {
    @org.jetbrains.annotations.NotNull()
    private final com.bridge.translator.service.ScreenCaptureManager manager = null;
    
    public ScreenCaptureHelper(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    public final boolean isInitialized() {
        return false;
    }
    
    public final void initialize(int resultCode, @org.jetbrains.annotations.NotNull()
    android.content.Intent data) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final android.graphics.Bitmap captureBitmap() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final android.graphics.Bitmap captureFrame() {
        return null;
    }
    
    public final void invalidateCache() {
    }
    
    public final void onConfigurationChanged() {
    }
    
    public final void release() {
    }
}