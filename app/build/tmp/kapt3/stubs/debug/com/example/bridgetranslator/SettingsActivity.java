package com.example.bridgetranslator;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\t\u0018\u0000 $2\u00020\u0001:\u0001$B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rH\u0002J\b\u0010\u000e\u001a\u00020\u000bH\u0002J\b\u0010\u000f\u001a\u00020\u000bH\u0002J\b\u0010\u0010\u001a\u00020\u000bH\u0002J\u0012\u0010\u0011\u001a\u00020\u00122\b\u0010\u0013\u001a\u0004\u0018\u00010\u0014H\u0014J\b\u0010\u0015\u001a\u00020\u0012H\u0014J\b\u0010\u0016\u001a\u00020\u0012H\u0002J\b\u0010\u0017\u001a\u00020\u0012H\u0002J\b\u0010\u0018\u001a\u00020\u0012H\u0002J\b\u0010\u0019\u001a\u00020\u0012H\u0002J\u0010\u0010\u001a\u001a\u00020\u00122\u0006\u0010\u001b\u001a\u00020\u001cH\u0002J\u0018\u0010\u001d\u001a\u00020\u00122\u0006\u0010\u001e\u001a\u00020\u000b2\u0006\u0010\u001b\u001a\u00020\u001cH\u0002J\u0018\u0010\u001f\u001a\u00020\u00122\u0006\u0010 \u001a\u00020\u000b2\u0006\u0010\u001b\u001a\u00020\u001cH\u0002J\u0018\u0010!\u001a\u00020\u00122\u0006\u0010 \u001a\u00020\u000b2\u0006\u0010\u001b\u001a\u00020\u001cH\u0002J\u0010\u0010\"\u001a\u00020\u00122\u0006\u0010\u001b\u001a\u00020\u001cH\u0002J\u0018\u0010#\u001a\u00020\u00122\u0006\u0010\u001e\u001a\u00020\u000b2\u0006\u0010\u001b\u001a\u00020\u001cH\u0002R#\u0010\u0003\u001a\n \u0005*\u0004\u0018\u00010\u00040\u00048BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\b\u0010\t\u001a\u0004\b\u0006\u0010\u0007\u00a8\u0006%"}, d2 = {"Lcom/example/bridgetranslator/SettingsActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "()V", "prefs", "Landroid/content/SharedPreferences;", "kotlin.jvm.PlatformType", "getPrefs", "()Landroid/content/SharedPreferences;", "prefs$delegate", "Lkotlin/Lazy;", "isAccessibilityServiceEnabled", "", "expected", "", "isBridgeAccessibilityEnabled", "isBridgeKeyboardEnabled", "isInputBridgeAccessibilityEnabled", "onCreate", "", "savedInstanceState", "Landroid/os/Bundle;", "onResume", "openKeyboardSettings", "requestMicPermissionForKeyboard", "startBridgeService", "stopBridgeService", "updateAccessibilityStatus", "tv", "Landroid/widget/TextView;", "updateFastModeStatus", "enabled", "updateInputBridgeStatus", "isEnabled", "updateOfflineStatus", "updateOverlayStatus", "updateSpeakerStatus", "Companion", "app_debug"})
public final class SettingsActivity extends androidx.appcompat.app.AppCompatActivity {
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy prefs$delegate = null;
    private static final int REQUEST_RECORD_AUDIO = 4207;
    @org.jetbrains.annotations.NotNull()
    public static final com.example.bridgetranslator.SettingsActivity.Companion Companion = null;
    
    public SettingsActivity() {
        super();
    }
    
    private final android.content.SharedPreferences getPrefs() {
        return null;
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    private final void updateOverlayStatus(android.widget.TextView tv) {
    }
    
    private final void updateAccessibilityStatus(android.widget.TextView tv) {
    }
    
    private final boolean isBridgeAccessibilityEnabled() {
        return false;
    }
    
    private final boolean isInputBridgeAccessibilityEnabled() {
        return false;
    }
    
    private final boolean isBridgeKeyboardEnabled() {
        return false;
    }
    
    private final boolean isAccessibilityServiceEnabled(java.lang.String expected) {
        return false;
    }
    
    private final void updateInputBridgeStatus(boolean isEnabled, android.widget.TextView tv) {
    }
    
    private final void openKeyboardSettings() {
    }
    
    private final void requestMicPermissionForKeyboard() {
    }
    
    private final void updateFastModeStatus(boolean enabled, android.widget.TextView tv) {
    }
    
    private final void updateSpeakerStatus(boolean enabled, android.widget.TextView tv) {
    }
    
    private final void updateOfflineStatus(boolean isEnabled, android.widget.TextView tv) {
    }
    
    private final void startBridgeService() {
    }
    
    private final void stopBridgeService() {
    }
    
    @java.lang.Override()
    protected void onResume() {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0005"}, d2 = {"Lcom/example/bridgetranslator/SettingsActivity$Companion;", "", "()V", "REQUEST_RECORD_AUDIO", "", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}