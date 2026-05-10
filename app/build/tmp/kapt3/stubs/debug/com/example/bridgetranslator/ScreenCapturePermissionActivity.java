package com.example.bridgetranslator;

/**
 * Transparent trampoline for the system MediaProjection consent dialog.
 *
 * Keep this activity visually empty. Drawing our own fullscreen "Tap Allow" UI here can obscure
 * or visually compete with Android's capture consent screen on some OEM builds.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u0000 \r2\u00020\u0001:\u0001\rB\u0005\u00a2\u0006\u0002\u0010\u0002J\"\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u00062\b\u0010\b\u001a\u0004\u0018\u00010\tH\u0015J\u0012\u0010\n\u001a\u00020\u00042\b\u0010\u000b\u001a\u0004\u0018\u00010\fH\u0014\u00a8\u0006\u000e"}, d2 = {"Lcom/example/bridgetranslator/ScreenCapturePermissionActivity;", "Landroid/app/Activity;", "()V", "onActivityResult", "", "requestCode", "", "resultCode", "data", "Landroid/content/Intent;", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "Companion", "app_debug"})
public final class ScreenCapturePermissionActivity extends android.app.Activity {
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String EXTRA_RESULT_CODE = "extra_result_code";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String EXTRA_RESULT_DATA = "extra_result_data";
    private static final int REQ_SCREEN_CAPTURE = 1001;
    @org.jetbrains.annotations.NotNull()
    public static final com.example.bridgetranslator.ScreenCapturePermissionActivity.Companion Companion = null;
    
    public ScreenCapturePermissionActivity() {
        super();
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    @java.lang.Override()
    @java.lang.Deprecated()
    protected void onActivityResult(int requestCode, int resultCode, @org.jetbrains.annotations.Nullable()
    android.content.Intent data) {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\b"}, d2 = {"Lcom/example/bridgetranslator/ScreenCapturePermissionActivity$Companion;", "", "()V", "EXTRA_RESULT_CODE", "", "EXTRA_RESULT_DATA", "REQ_SCREEN_CAPTURE", "", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}