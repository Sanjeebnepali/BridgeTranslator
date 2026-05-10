package com.example.bridgetranslator;

/**
 * Live-scan camera activity with geometric shape detection, per-shape OCR + translation,
 * and Text-to-Speech.
 *
 * Flow:
 * 1. CameraX preview starts; ShapeDetector runs per frame → ShapeOverlayView updates.
 * 2. User taps Capture FAB → preview freezes, progress dialog shows.
 * 3. TranslationPipeline processes all shapes concurrently.
 * 4. Result composited back onto frozen preview; overlay removed.
 * 5. Speaker button becomes active; reads all translations in order.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u008c\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\b\r\u0018\u0000 C2\u00020\u0001:\u0001CB\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010)\u001a\u00020*2\u0006\u0010+\u001a\u00020,H\u0003J\b\u0010-\u001a\u00020*H\u0002J\b\u0010.\u001a\u00020*H\u0002J\b\u0010/\u001a\u00020\u0011H\u0002J\b\u00100\u001a\u00020*H\u0002J\u0012\u00101\u001a\u0004\u0018\u00010\f2\u0006\u0010+\u001a\u00020,H\u0003J\b\u00102\u001a\u00020\u0011H\u0002J\b\u00103\u001a\u00020\u0011H\u0002J\b\u00104\u001a\u00020*H\u0002J\u0012\u00105\u001a\u00020*2\b\u00106\u001a\u0004\u0018\u000107H\u0014J\b\u00108\u001a\u00020*H\u0014J\b\u00109\u001a\u00020*H\u0014J\b\u0010:\u001a\u00020*H\u0014J\b\u0010;\u001a\u00020*H\u0002J\b\u0010<\u001a\u00020*H\u0002J\b\u0010=\u001a\u00020*H\u0002J\b\u0010>\u001a\u00020*H\u0002J\u0010\u0010?\u001a\u00020*2\u0006\u0010@\u001a\u00020\u0011H\u0002J\b\u0010A\u001a\u00020*H\u0002J\b\u0010B\u001a\u00020*H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000R\u0014\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u000b\u001a\u0004\u0018\u00010\fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000f0\u000eX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0011X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0011X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u0014X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0015\u001a\u00020\u0016X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0017\u001a\u00020\u0018X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\n0\u000eX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001a\u001a\u00020\u001bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001c\u001a\u00020\u001dX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001e\u001a\u00020\u001fX\u0082.\u00a2\u0006\u0002\n\u0000R\u0010\u0010 \u001a\u0004\u0018\u00010!X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\"\u001a\u00020#X\u0082.\u00a2\u0006\u0002\n\u0000R\u0010\u0010$\u001a\u0004\u0018\u00010%X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010&\u001a\u00020\'X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010(\u001a\u00020\'X\u0082.\u00a2\u0006\u0002\n\u0000\u00a8\u0006D"}, d2 = {"Lcom/example/bridgetranslator/CameraFeatureActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "()V", "btnCapture", "Landroid/view/View;", "btnClose", "btnShare", "btnSpeaker", "cameraPermissionLauncher", "Landroidx/activity/result/ActivityResultLauncher;", "", "currentResultBitmap", "Landroid/graphics/Bitmap;", "currentShapes", "", "Lcom/bridge/translator/processing/DetectedShape;", "isFrozen", "", "isProcessing", "ivResult", "Landroid/widget/ImageView;", "languageManager", "Lcom/example/bridgetranslator/LanguageManager;", "lastAnalysisMs", "", "pendingTranslations", "pipeline", "Lcom/bridge/translator/processing/TranslationPipeline;", "previewView", "Landroidx/camera/view/PreviewView;", "progressBar", "Landroid/widget/ProgressBar;", "progressDialog", "Landroid/app/AlertDialog;", "shapeOverlayView", "Lcom/bridge/translator/ui/ShapeOverlayView;", "translationJob", "Lkotlinx/coroutines/Job;", "tvShapeCount", "Landroid/widget/TextView;", "tvStatus", "analyzeFrame", "", "proxy", "Landroidx/camera/core/ImageProxy;", "applyWindowInsets", "bindViews", "hasCameraPermission", "hideProgressDialog", "imageProxyToBitmap", "isFastModeEnabled", "isSpeakerAutoEnabled", "onCaptureClicked", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "onDestroy", "onPause", "onResume", "onShareClicked", "onSpeakerClicked", "resetToLiveScan", "restoreSettingsState", "setSpeakerActive", "active", "showProgressDialog", "startCamera", "Companion", "app_debug"})
public final class CameraFeatureActivity extends androidx.appcompat.app.AppCompatActivity {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String TAG = "CameraFeatureActivity";
    private static final long FRAME_ANALYSIS_INTERVAL_MS = 200L;
    private static final int REQUEST_CAMERA_PERMISSION = 1001;
    private androidx.camera.view.PreviewView previewView;
    private com.bridge.translator.ui.ShapeOverlayView shapeOverlayView;
    private android.widget.ImageView ivResult;
    private android.view.View btnCapture;
    private android.view.View btnSpeaker;
    private android.view.View btnShare;
    private android.view.View btnClose;
    private android.widget.TextView tvStatus;
    private android.widget.TextView tvShapeCount;
    private android.widget.ProgressBar progressBar;
    private boolean isFrozen = false;
    private boolean isProcessing = false;
    private long lastAnalysisMs = 0L;
    @org.jetbrains.annotations.NotNull()
    private java.util.List<com.bridge.translator.processing.DetectedShape> currentShapes;
    @org.jetbrains.annotations.Nullable()
    private kotlinx.coroutines.Job translationJob;
    @org.jetbrains.annotations.Nullable()
    private android.graphics.Bitmap currentResultBitmap;
    @org.jetbrains.annotations.NotNull()
    private java.util.List<java.lang.String> pendingTranslations;
    @org.jetbrains.annotations.NotNull()
    private final com.bridge.translator.processing.TranslationPipeline pipeline = null;
    private com.example.bridgetranslator.LanguageManager languageManager;
    @org.jetbrains.annotations.NotNull()
    private final androidx.activity.result.ActivityResultLauncher<java.lang.String> cameraPermissionLauncher = null;
    @org.jetbrains.annotations.Nullable()
    private android.app.AlertDialog progressDialog;
    @org.jetbrains.annotations.NotNull()
    public static final com.example.bridgetranslator.CameraFeatureActivity.Companion Companion = null;
    
    public CameraFeatureActivity() {
        super();
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    @java.lang.Override()
    protected void onResume() {
    }
    
    @java.lang.Override()
    protected void onPause() {
    }
    
    @java.lang.Override()
    protected void onDestroy() {
    }
    
    private final void startCamera() {
    }
    
    @androidx.camera.core.ExperimentalGetImage()
    private final void analyzeFrame(androidx.camera.core.ImageProxy proxy) {
    }
    
    private final void onCaptureClicked() {
    }
    
    private final void showProgressDialog() {
    }
    
    private final void hideProgressDialog() {
    }
    
    private final void onSpeakerClicked() {
    }
    
    private final void setSpeakerActive(boolean active) {
    }
    
    private final void onShareClicked() {
    }
    
    private final void resetToLiveScan() {
    }
    
    private final boolean isFastModeEnabled() {
        return false;
    }
    
    private final boolean isSpeakerAutoEnabled() {
        return false;
    }
    
    @androidx.camera.core.ExperimentalGetImage()
    private final android.graphics.Bitmap imageProxyToBitmap(androidx.camera.core.ImageProxy proxy) {
        return null;
    }
    
    private final void bindViews() {
    }
    
    private final void restoreSettingsState() {
    }
    
    private final void applyWindowInsets() {
    }
    
    private final boolean hasCameraPermission() {
        return false;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\t"}, d2 = {"Lcom/example/bridgetranslator/CameraFeatureActivity$Companion;", "", "()V", "FRAME_ANALYSIS_INTERVAL_MS", "", "REQUEST_CAMERA_PERMISSION", "", "TAG", "", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}