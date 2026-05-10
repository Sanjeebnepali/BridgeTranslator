package com.bridge.translator.service;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000\"\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u0012\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u001a(\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00052\u0006\u0010\u0007\u001a\u00020\bH\u0002\u001a\n\u0010\t\u001a\u00020\b*\u00020\n\u00a8\u0006\u000b"}, d2 = {"yuvToRgb", "", "yuv", "", "width", "", "height", "bitmap", "Landroid/graphics/Bitmap;", "toBitmap", "Landroidx/camera/core/ImageProxy;", "app_debug"})
public final class CameraManagerKt {
    
    /**
     * Extension function to convert ImageProxy to Bitmap
     * Handles YUV format conversion
     */
    @org.jetbrains.annotations.NotNull()
    public static final android.graphics.Bitmap toBitmap(@org.jetbrains.annotations.NotNull()
    androidx.camera.core.ImageProxy $this$toBitmap) {
        return null;
    }
    
    /**
     * Convert YUV NV21 format to RGB and write to Bitmap
     */
    private static final void yuvToRgb(byte[] yuv, int width, int height, android.graphics.Bitmap bitmap) {
    }
}