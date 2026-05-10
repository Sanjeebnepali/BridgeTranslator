package com.bridge.translator.service;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0002\u0018\u0000 \u00152\u00020\u0001:\u0001\u0015B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0007\u001a\u00020\bH\u0002J\b\u0010\t\u001a\u00020\bH\u0002J\n\u0010\n\u001a\u0004\u0018\u00010\u000bH\u0002J\u0012\u0010\f\u001a\u00020\b2\b\u0010\r\u001a\u0004\u0018\u00010\u000eH\u0016J\b\u0010\u000f\u001a\u00020\bH\u0016J\b\u0010\u0010\u001a\u00020\bH\u0016J\b\u0010\u0011\u001a\u00020\bH\u0014J\b\u0010\u0012\u001a\u00020\bH\u0002J\f\u0010\u0013\u001a\u00020\u0014*\u00020\u000bH\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0016"}, d2 = {"Lcom/bridge/translator/service/BridgeAccessibilityService;", "Landroid/accessibilityservice/AccessibilityService;", "()V", "inputBridgeManager", "Lcom/bridge/translator/input/InputBridgeManager;", "scope", "Lkotlinx/coroutines/CoroutineScope;", "cancelNotification", "", "createNotificationChannel", "findFocusedInput", "Landroid/view/accessibility/AccessibilityNodeInfo;", "onAccessibilityEvent", "event", "Landroid/view/accessibility/AccessibilityEvent;", "onDestroy", "onInterrupt", "onServiceConnected", "updateNotification", "isEditableLike", "", "Companion", "app_debug"})
public final class BridgeAccessibilityService extends android.accessibilityservice.AccessibilityService {
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.CoroutineScope scope = null;
    private com.bridge.translator.input.InputBridgeManager inputBridgeManager;
    @org.jetbrains.annotations.NotNull()
    @java.lang.Deprecated()
    public static final java.lang.String INPUT_BRIDGE_CHANNEL = "input_bridge";
    @java.lang.Deprecated()
    public static final int INPUT_BRIDGE_NOTIFICATION_ID = 5127;
    @org.jetbrains.annotations.NotNull()
    private static final com.bridge.translator.service.BridgeAccessibilityService.Companion Companion = null;
    
    public BridgeAccessibilityService() {
        super();
    }
    
    @java.lang.Override()
    protected void onServiceConnected() {
    }
    
    @java.lang.Override()
    public void onAccessibilityEvent(@org.jetbrains.annotations.Nullable()
    android.view.accessibility.AccessibilityEvent event) {
    }
    
    @java.lang.Override()
    public void onInterrupt() {
    }
    
    @java.lang.Override()
    public void onDestroy() {
    }
    
    private final android.view.accessibility.AccessibilityNodeInfo findFocusedInput() {
        return null;
    }
    
    private final void updateNotification() {
    }
    
    private final void cancelNotification() {
    }
    
    private final void createNotificationChannel() {
    }
    
    private final boolean isEditableLike(android.view.accessibility.AccessibilityNodeInfo $this$isEditableLike) {
        return false;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0000\b\u0082\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0007"}, d2 = {"Lcom/bridge/translator/service/BridgeAccessibilityService$Companion;", "", "()V", "INPUT_BRIDGE_CHANNEL", "", "INPUT_BRIDGE_NOTIFICATION_ID", "", "app_debug"})
    static final class Companion {
        
        private Companion() {
            super();
        }
    }
}