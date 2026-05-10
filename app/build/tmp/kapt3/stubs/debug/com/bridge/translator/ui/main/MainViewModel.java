package com.bridge.translator.ui.main;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000L\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\b\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0006\u0010\u001a\u001a\u00020\u001bJ\b\u0010\u001c\u001a\u00020\rH\u0002J\u000e\u0010\u001d\u001a\u00020\u001b2\u0006\u0010\u001e\u001a\u00020\u0007J\b\u0010\u001f\u001a\u00020\u001bH\u0002J\b\u0010 \u001a\u00020\u001bH\u0002J\u0006\u0010!\u001a\u00020\u001bJ\u0006\u0010\"\u001a\u00020\u001bR\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001c\u0010\b\u001a\u0010\u0012\f\u0012\n \u000b*\u0004\u0018\u00010\n0\n0\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001c\u0010\f\u001a\u0010\u0012\f\u0012\n \u000b*\u0004\u0018\u00010\r0\r0\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00070\u000f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0016\u0010\u0012\u001a\n \u000b*\u0004\u0018\u00010\u00130\u0013X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\n0\u0015\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u0017\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\r0\u0015\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u0017\u00a8\u0006#"}, d2 = {"Lcom/bridge/translator/ui/main/MainViewModel;", "Landroidx/lifecycle/AndroidViewModel;", "application", "Landroid/app/Application;", "(Landroid/app/Application;)V", "_captureMode", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/bridge/translator/service/FloatingBubbleService$CaptureMode;", "_serviceRunning", "Landroidx/lifecycle/MutableLiveData;", "", "kotlin.jvm.PlatformType", "_targetLanguageLabel", "", "captureMode", "Lkotlinx/coroutines/flow/StateFlow;", "getCaptureMode", "()Lkotlinx/coroutines/flow/StateFlow;", "prefs", "Landroid/content/SharedPreferences;", "serviceRunning", "Landroidx/lifecycle/LiveData;", "getServiceRunning", "()Landroidx/lifecycle/LiveData;", "targetLanguageLabel", "getTargetLanguageLabel", "clearTargetLanguage", "", "currentTargetLabel", "setCaptureMode", "mode", "startBubbleService", "stopBubbleService", "syncState", "toggleService", "app_debug"})
public final class MainViewModel extends androidx.lifecycle.AndroidViewModel {
    private final android.content.SharedPreferences prefs = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.MutableLiveData<java.lang.Boolean> _serviceRunning = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.LiveData<java.lang.Boolean> serviceRunning = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.MutableLiveData<java.lang.String> _targetLanguageLabel = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.LiveData<java.lang.String> targetLanguageLabel = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.bridge.translator.service.FloatingBubbleService.CaptureMode> _captureMode = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.bridge.translator.service.FloatingBubbleService.CaptureMode> captureMode = null;
    
    public MainViewModel(@org.jetbrains.annotations.NotNull()
    android.app.Application application) {
        super(null);
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<java.lang.Boolean> getServiceRunning() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<java.lang.String> getTargetLanguageLabel() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.bridge.translator.service.FloatingBubbleService.CaptureMode> getCaptureMode() {
        return null;
    }
    
    public final void syncState() {
    }
    
    public final void toggleService() {
    }
    
    public final void clearTargetLanguage() {
    }
    
    public final void setCaptureMode(@org.jetbrains.annotations.NotNull()
    com.bridge.translator.service.FloatingBubbleService.CaptureMode mode) {
    }
    
    private final java.lang.String currentTargetLabel() {
        return null;
    }
    
    private final void startBubbleService() {
    }
    
    private final void stopBubbleService() {
    }
}