package com.bridge.translator.ui.main;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000T\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0011\n\u0000\n\u0002\u0010\u0015\n\u0002\b\n\u0018\u0000 &2\u00020\u0001:\u0001&B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u000e\u001a\u00020\u000fH\u0002J\b\u0010\u0010\u001a\u00020\u0011H\u0002J\b\u0010\u0012\u001a\u00020\u0013H\u0002J\u0012\u0010\u0014\u001a\u00020\u00112\b\u0010\u0015\u001a\u0004\u0018\u00010\u0016H\u0014J-\u0010\u0017\u001a\u00020\u00112\u0006\u0010\u0018\u001a\u00020\u00192\u000e\u0010\u001a\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u000f0\u001b2\u0006\u0010\u001c\u001a\u00020\u001dH\u0016\u00a2\u0006\u0002\u0010\u001eJ\b\u0010\u001f\u001a\u00020\u0011H\u0014J\b\u0010 \u001a\u00020\u0011H\u0002J\b\u0010!\u001a\u00020\u0011H\u0002J\b\u0010\"\u001a\u00020\u0011H\u0002J\b\u0010#\u001a\u00020\u0011H\u0002J\b\u0010$\u001a\u00020\u0011H\u0002J\b\u0010%\u001a\u00020\u0011H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001b\u0010\b\u001a\u00020\t8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\f\u0010\r\u001a\u0004\b\n\u0010\u000b\u00a8\u0006\'"}, d2 = {"Lcom/bridge/translator/ui/main/MainActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "()V", "binding", "Lcom/example/bridgetranslator/databinding/ActivityMainBinding;", "overlayPermissionLauncher", "Landroidx/activity/result/ActivityResultLauncher;", "Landroid/content/Intent;", "viewModel", "Lcom/bridge/translator/ui/main/MainViewModel;", "getViewModel", "()Lcom/bridge/translator/ui/main/MainViewModel;", "viewModel$delegate", "Lkotlin/Lazy;", "accessibilityTargetLabel", "", "checkCameraPermission", "", "isAccessibilityServiceEnabled", "", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "onRequestPermissionsResult", "requestCode", "", "permissions", "", "grantResults", "", "(I[Ljava/lang/String;[I)V", "onResume", "requestOverlayPermission", "setupCaptureModSelector", "setupClickListeners", "setupObservers", "updateAccessibilityUi", "updatePermissionUi", "Companion", "app_debug"})
public final class MainActivity extends androidx.appcompat.app.AppCompatActivity {
    private static final int CAMERA_PERMISSION_CODE = 100;
    private com.example.bridgetranslator.databinding.ActivityMainBinding binding;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy viewModel$delegate = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.activity.result.ActivityResultLauncher<android.content.Intent> overlayPermissionLauncher = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.bridge.translator.ui.main.MainActivity.Companion Companion = null;
    
    public MainActivity() {
        super();
    }
    
    private final com.bridge.translator.ui.main.MainViewModel getViewModel() {
        return null;
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    @java.lang.Override()
    protected void onResume() {
    }
    
    @java.lang.Override()
    public void onRequestPermissionsResult(int requestCode, @org.jetbrains.annotations.NotNull()
    java.lang.String[] permissions, @org.jetbrains.annotations.NotNull()
    int[] grantResults) {
    }
    
    private final void setupObservers() {
    }
    
    private final void setupClickListeners() {
    }
    
    private final void requestOverlayPermission() {
    }
    
    private final void checkCameraPermission() {
    }
    
    private final void setupCaptureModSelector() {
    }
    
    private final void updatePermissionUi() {
    }
    
    private final void updateAccessibilityUi() {
    }
    
    private final java.lang.String accessibilityTargetLabel() {
        return null;
    }
    
    private final boolean isAccessibilityServiceEnabled() {
        return false;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0005"}, d2 = {"Lcom/bridge/translator/ui/main/MainActivity$Companion;", "", "()V", "CAMERA_PERMISSION_CODE", "", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}