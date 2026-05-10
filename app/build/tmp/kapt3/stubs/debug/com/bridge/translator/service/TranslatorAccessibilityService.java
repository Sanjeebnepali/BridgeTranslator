package com.bridge.translator.service;

/**
 * Passive context tracker for the manual snap-and-translate flow.
 *
 * Subscribed to TYPE_WINDOW_STATE_CHANGED only. On every event it updates
 * [FloatingBubbleService.currentForegroundPackage] and toggles the bubble
 * window: detached for system UI, launchers, and BANKING_PACKAGES; attached
 * everywhere else.
 *
 * **No synthetic gestures.** The previous tap-through path that used
 * [dispatchGesture] has been removed — the overlay is fully non-touchable
 * now, so the underlying app receives every touch directly and there is
 * nothing to inject.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\"\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u0000 \u00162\u00020\u0001:\u0001\u0016B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0012\u0010\n\u001a\u00020\u000b2\b\u0010\f\u001a\u0004\u0018\u00010\rH\u0016J\b\u0010\u000e\u001a\u00020\u000bH\u0016J\b\u0010\u000f\u001a\u00020\u000bH\u0016J\b\u0010\u0010\u001a\u00020\u000bH\u0014J\u0012\u0010\u0011\u001a\u00020\u00122\b\u0010\u0013\u001a\u0004\u0018\u00010\u0014H\u0016J\u000e\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004H\u0002R!\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u00048BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\b\u0010\t\u001a\u0004\b\u0006\u0010\u0007\u00a8\u0006\u0017"}, d2 = {"Lcom/bridge/translator/service/TranslatorAccessibilityService;", "Landroid/accessibilityservice/AccessibilityService;", "()V", "launcherPackages", "", "", "getLauncherPackages", "()Ljava/util/Set;", "launcherPackages$delegate", "Lkotlin/Lazy;", "onAccessibilityEvent", "", "event", "Landroid/view/accessibility/AccessibilityEvent;", "onDestroy", "onInterrupt", "onServiceConnected", "onUnbind", "", "intent", "Landroid/content/Intent;", "resolveLauncherPackages", "Companion", "app_debug"})
public final class TranslatorAccessibilityService extends android.accessibilityservice.AccessibilityService {
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy launcherPackages$delegate = null;
    @kotlin.jvm.Volatile()
    @org.jetbrains.annotations.Nullable()
    private static volatile com.bridge.translator.service.TranslatorAccessibilityService instance;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Set<java.lang.String> SYSTEM_PACKAGES = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.bridge.translator.service.TranslatorAccessibilityService.Companion Companion = null;
    
    public TranslatorAccessibilityService() {
        super();
    }
    
    private final java.util.Set<java.lang.String> getLauncherPackages() {
        return null;
    }
    
    @java.lang.Override()
    public void onAccessibilityEvent(@org.jetbrains.annotations.Nullable()
    android.view.accessibility.AccessibilityEvent event) {
    }
    
    @java.lang.Override()
    protected void onServiceConnected() {
    }
    
    @java.lang.Override()
    public void onDestroy() {
    }
    
    @java.lang.Override()
    public boolean onUnbind(@org.jetbrains.annotations.Nullable()
    android.content.Intent intent) {
        return false;
    }
    
    @java.lang.Override()
    public void onInterrupt() {
    }
    
    private final java.util.Set<java.lang.String> resolveLauncherPackages() {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\"\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u0014\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\"\u0010\b\u001a\u0004\u0018\u00010\u00072\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007@BX\u0086\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\n\u00a8\u0006\u000b"}, d2 = {"Lcom/bridge/translator/service/TranslatorAccessibilityService$Companion;", "", "()V", "SYSTEM_PACKAGES", "", "", "<set-?>", "Lcom/bridge/translator/service/TranslatorAccessibilityService;", "instance", "getInstance", "()Lcom/bridge/translator/service/TranslatorAccessibilityService;", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.Nullable()
        public final com.bridge.translator.service.TranslatorAccessibilityService getInstance() {
            return null;
        }
    }
}