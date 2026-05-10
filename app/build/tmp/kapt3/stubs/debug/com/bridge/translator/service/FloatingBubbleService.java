package com.bridge.translator.service;

/**
 * Manual snap-and-translate floating bubble service.
 *
 * The bubble is a state-machine button:
 *  IDLE      — grey circle with the translate icon. Tap to capture.
 *  CAPTURING — blue circle with a spinner. Tap-ignored.
 *  SHOWING   — red circle with ✕. Tap to dismiss the overlay.
 *
 * A user gesture on the bubble is the only way to start a translation cycle.
 * There is no auto-poll, no scroll/touch debounce, no motion gating.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u00c8\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\r\u0018\u0000 p2\u00020\u0001:\u0003nopB\u0005\u00a2\u0006\u0002\u0010\u0002J\u0012\u00107\u001a\u0002082\b\b\u0002\u00109\u001a\u00020&H\u0003J\b\u0010:\u001a\u00020;H\u0002J\u0010\u0010<\u001a\u00020=2\u0006\u0010>\u001a\u00020\"H\u0002J\b\u0010?\u001a\u000208H\u0002J\b\u0010@\u001a\u000208H\u0002J\b\u0010A\u001a\u000208H\u0002J\u0006\u0010B\u001a\u000208J\u0010\u0010C\u001a\u00020\"2\u0006\u0010D\u001a\u00020\"H\u0002J\u0010\u0010E\u001a\u00020-2\u0006\u0010F\u001a\u00020\u000eH\u0002J\b\u0010G\u001a\u000208H\u0002J\u0010\u0010H\u001a\u0002082\u0006\u0010I\u001a\u00020JH\u0002J\u0006\u0010K\u001a\u000208J\b\u0010L\u001a\u000208H\u0002J\u0010\u0010M\u001a\u0002082\u0006\u0010N\u001a\u00020\u0016H\u0002J\u0010\u0010O\u001a\u00020\u00162\u0006\u0010P\u001a\u00020\u001fH\u0002J\b\u0010Q\u001a\u000208H\u0002J\u0014\u0010R\u001a\u0004\u0018\u00010S2\b\u0010I\u001a\u0004\u0018\u00010JH\u0016J\u0018\u0010T\u001a\u00020\u00162\u0006\u0010U\u001a\u00020\f2\u0006\u0010V\u001a\u00020WH\u0003J\u0010\u0010X\u001a\u0002082\u0006\u0010Y\u001a\u00020ZH\u0016J\b\u0010[\u001a\u000208H\u0016J\b\u0010\\\u001a\u000208H\u0016J\"\u0010]\u001a\u00020\"2\b\u0010I\u001a\u0004\u0018\u00010J2\u0006\u0010^\u001a\u00020\"2\u0006\u0010_\u001a\u00020\"H\u0016J\u0006\u0010`\u001a\u000208J\b\u0010a\u001a\u00020\"H\u0002J\u0010\u0010b\u001a\u0002082\u0006\u0010U\u001a\u00020\fH\u0002J\b\u0010c\u001a\u00020dH\u0002J\u000e\u0010e\u001a\u0002082\u0006\u0010f\u001a\u00020\u0014J\u0006\u0010g\u001a\u000208J\b\u0010h\u001a\u000208H\u0002J\u0010\u0010i\u001a\u0002082\u0006\u0010U\u001a\u00020\fH\u0002J\b\u0010j\u001a\u000208H\u0002J\b\u0010k\u001a\u000208H\u0002J\b\u0010l\u001a\u00020\u000eH\u0002J\b\u0010m\u001a\u000208H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0005\u001a\u0004\u0018\u00010\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082.\u00a2\u0006\u0002\n\u0000R\u0010\u0010\t\u001a\u0004\u0018\u00010\nX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u000b\u001a\u0004\u0018\u00010\fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\r\u001a\u0004\u0018\u00010\u000eX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u000f\u001a\u0004\u0018\u00010\u0010X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0011\u001a\u0004\u0018\u00010\bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0012\u001a\u0004\u0018\u00010\fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u0014X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0015\u001a\u00020\u0016X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001a\u0010\u0017\u001a\u00020\u000eX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0018\u0010\u0019\"\u0004\b\u001a\u0010\u001bR\u0010\u0010\u001c\u001a\u0004\u0018\u00010\u001dX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001e\u001a\u00020\u001fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010 \u001a\u00020\u001fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010!\u001a\u00020\"X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010#\u001a\u00020\"X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010$\u001a\u00020\u0016X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010%\u001a\u0004\u0018\u00010&X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\'\u001a\u0004\u0018\u00010\u001dX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010(\u001a\u00020)X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010*\u001a\u00020+X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010,\u001a\u0004\u0018\u00010-X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010.\u001a\u00020/X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u00100\u001a\u00020\u0016X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u00101\u001a\u00020&X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u00102\u001a\u00020\u000eX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u00103\u001a\u000204X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u00105\u001a\u000206X\u0082.\u00a2\u0006\u0002\n\u0000\u00a8\u0006q"}, d2 = {"Lcom/bridge/translator/service/FloatingBubbleService;", "Landroid/app/Service;", "()V", "bitmapEraser", "Lcom/bridge/translator/processing/BitmapTextEraser;", "bubbleIconView", "Landroid/widget/ImageView;", "bubbleParams", "Landroid/view/WindowManager$LayoutParams;", "bubbleProgressBar", "Landroid/widget/ProgressBar;", "bubbleView", "Landroid/view/View;", "cachedAnalyserLang", "", "cameraManager", "Lcom/bridge/translator/service/CameraManager;", "cancelZoneParams", "cancelZoneView", "captureMode", "Lcom/bridge/translator/service/FloatingBubbleService$CaptureMode;", "capturePending", "", "currentForegroundPackage", "getCurrentForegroundPackage", "()Ljava/lang/String;", "setCurrentForegroundPackage", "(Ljava/lang/String;)V", "cycleJob", "Lkotlinx/coroutines/Job;", "initialTouchX", "", "initialTouchY", "initialX", "", "initialY", "isDragging", "lastVisualState", "Lcom/bridge/translator/service/FloatingBubbleService$BubbleState;", "longPressJob", "overlayManager", "Lcom/bridge/translator/overlay/OverlayManager;", "scope", "Lkotlinx/coroutines/CoroutineScope;", "screenAnalyser", "Lcom/bridge/translator/processing/ScreenAnalyser;", "screenCaptureManager", "Lcom/bridge/translator/service/ScreenCaptureHelper;", "showingCancelZone", "state", "targetLang", "translationEngine", "Lcom/bridge/translator/translation/TranslationEngine;", "windowManager", "Landroid/view/WindowManager;", "attachBubble", "", "initialState", "buildNotification", "Landroid/app/Notification;", "circleDrawable", "Landroid/graphics/drawable/GradientDrawable;", "color", "createNotificationChannel", "detachBubble", "dismissOverlay", "dismissOverlayIfShowing", "dp", "value", "ensureAnalyser", "lang", "handleBubbleTap", "handleProjectionResult", "intent", "Landroid/content/Intent;", "hideBubbleForUnsupportedContext", "hideCancelZone", "highlightCancelZone", "active", "isInStopZone", "rawY", "launchPermissionScreen", "onBind", "Landroid/os/IBinder;", "onBubbleTouch", "view", "event", "Landroid/view/MotionEvent;", "onConfigurationChanged", "newConfig", "Landroid/content/res/Configuration;", "onCreate", "onDestroy", "onStartCommand", "flags", "startId", "onTargetLanguageChanged", "overlayWindowType", "playShowingPulse", "realDisplayMetrics", "Landroid/util/DisplayMetrics;", "setCaptureMode", "mode", "showBubbleForSupportedContext", "showCancelZone", "snapToEdge", "startCaptureAndShowOverlay", "stopTranslation", "targetLanguage", "updateBubbleVisual", "BubbleState", "CaptureMode", "Companion", "app_debug"})
public final class FloatingBubbleService extends android.app.Service {
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String ACTION_MEDIA_PROJECTION_RESULT = "action.MEDIA_PROJECTION_RESULT";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String ACTION_STOP_SERVICE = "action.STOP_SERVICE";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String EXTRA_RESULT_CODE = "extra.RESULT_CODE";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String EXTRA_RESULT_DATA = "extra.RESULT_DATA";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String PREF_AUTO_TRANSLATE_ENABLED = "auto_translate_enabled";
    
    /**
     * Banking / payment apps that MUST NOT receive a synthetic tap.
     * Held here (rather than in the accessibility service) so the tap-
     * through guard can read it without depending on the optional
     * accessibility service being connected.
     */
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Set<java.lang.String> BANKING_PACKAGES = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String CHANNEL_ID = "bubble_service_channel";
    private static final int NOTIFICATION_ID = 1001;
    private static final int BUBBLE_DP = 56;
    private static final long SNAP_MS = 200L;
    private static final float DRAG_THRESHOLD = 10.0F;
    private static final int STOP_ZONE_DP = 120;
    private static final long LONG_PRESS_MS = 500L;
    private static final int CANCEL_ZONE_SIZE_DP = 72;
    private static final int CANCEL_ZONE_MARGIN_DP = 40;
    private static final long CAPTURE_BUBBLE_HIDE_DELAY_MS = 80L;
    @kotlin.jvm.Volatile()
    @org.jetbrains.annotations.Nullable()
    private static volatile com.bridge.translator.service.FloatingBubbleService instance;
    @kotlin.jvm.Volatile()
    private static volatile boolean isRunning = false;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.CoroutineScope scope = null;
    private android.view.WindowManager windowManager;
    private com.bridge.translator.service.ScreenCaptureHelper screenCaptureManager;
    private com.bridge.translator.processing.BitmapTextEraser bitmapEraser;
    private com.bridge.translator.overlay.OverlayManager overlayManager;
    @org.jetbrains.annotations.NotNull()
    private final com.bridge.translator.translation.TranslationEngine translationEngine = null;
    @org.jetbrains.annotations.Nullable()
    private com.bridge.translator.service.CameraManager cameraManager;
    @org.jetbrains.annotations.NotNull()
    private com.bridge.translator.service.FloatingBubbleService.CaptureMode captureMode = com.bridge.translator.service.FloatingBubbleService.CaptureMode.SCREEN;
    @org.jetbrains.annotations.Nullable()
    private com.bridge.translator.processing.ScreenAnalyser screenAnalyser;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String cachedAnalyserLang;
    @org.jetbrains.annotations.Nullable()
    private kotlinx.coroutines.Job cycleJob;
    private boolean capturePending = false;
    @org.jetbrains.annotations.NotNull()
    private java.lang.String targetLang = "en";
    @kotlin.jvm.Volatile()
    @org.jetbrains.annotations.NotNull()
    private volatile com.bridge.translator.service.FloatingBubbleService.BubbleState state = com.bridge.translator.service.FloatingBubbleService.BubbleState.IDLE;
    @org.jetbrains.annotations.Nullable()
    private com.bridge.translator.service.FloatingBubbleService.BubbleState lastVisualState;
    
    /**
     * Set by [TranslatorAccessibilityService] on every TYPE_WINDOW_STATE_CHANGED
     * event. Read-only consumers (settings UI, etc.) can also peek at it.
     */
    @kotlin.jvm.Volatile()
    @org.jetbrains.annotations.NotNull()
    private volatile java.lang.String currentForegroundPackage = "";
    @org.jetbrains.annotations.Nullable()
    private android.view.View bubbleView;
    @org.jetbrains.annotations.Nullable()
    private android.widget.ImageView bubbleIconView;
    @org.jetbrains.annotations.Nullable()
    private android.widget.ProgressBar bubbleProgressBar;
    private android.view.WindowManager.LayoutParams bubbleParams;
    private int initialX = 0;
    private int initialY = 0;
    private float initialTouchX = 0.0F;
    private float initialTouchY = 0.0F;
    private boolean isDragging = false;
    @org.jetbrains.annotations.Nullable()
    private android.view.View cancelZoneView;
    @org.jetbrains.annotations.Nullable()
    private android.view.WindowManager.LayoutParams cancelZoneParams;
    @org.jetbrains.annotations.Nullable()
    private kotlinx.coroutines.Job longPressJob;
    private boolean showingCancelZone = false;
    @org.jetbrains.annotations.NotNull()
    public static final com.bridge.translator.service.FloatingBubbleService.Companion Companion = null;
    
    public FloatingBubbleService() {
        super();
    }
    
    /**
     * Set by [TranslatorAccessibilityService] on every TYPE_WINDOW_STATE_CHANGED
     * event. Read-only consumers (settings UI, etc.) can also peek at it.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getCurrentForegroundPackage() {
        return null;
    }
    
    /**
     * Set by [TranslatorAccessibilityService] on every TYPE_WINDOW_STATE_CHANGED
     * event. Read-only consumers (settings UI, etc.) can also peek at it.
     */
    public final void setCurrentForegroundPackage(@org.jetbrains.annotations.NotNull()
    java.lang.String p0) {
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public android.os.IBinder onBind(@org.jetbrains.annotations.Nullable()
    android.content.Intent intent) {
        return null;
    }
    
    @java.lang.Override()
    public void onCreate() {
    }
    
    @java.lang.Override()
    public int onStartCommand(@org.jetbrains.annotations.Nullable()
    android.content.Intent intent, int flags, int startId) {
        return 0;
    }
    
    @java.lang.Override()
    public void onConfigurationChanged(@org.jetbrains.annotations.NotNull()
    android.content.res.Configuration newConfig) {
    }
    
    @java.lang.Override()
    public void onDestroy() {
    }
    
    @android.annotation.SuppressLint(value = {"ClickableViewAccessibility"})
    private final void attachBubble(com.bridge.translator.service.FloatingBubbleService.BubbleState initialState) {
    }
    
    private final void detachBubble() {
    }
    
    private final void updateBubbleVisual() {
    }
    
    private final void playShowingPulse(android.view.View view) {
    }
    
    @android.annotation.SuppressLint(value = {"ClickableViewAccessibility"})
    private final boolean onBubbleTouch(android.view.View view, android.view.MotionEvent event) {
        return false;
    }
    
    private final void handleBubbleTap() {
    }
    
    private final void startCaptureAndShowOverlay() {
    }
    
    private final void dismissOverlay() {
    }
    
    /**
     * Called by accessibility service when the user clicks or scrolls in the
     * underlying app so the overlay smoothly gets out of their way.
     */
    public final void dismissOverlayIfShowing() {
    }
    
    private final void stopTranslation() {
    }
    
    /**
     * Polish #3: when the user changes target language in settings the next
     * tap should use the new language. Caller invokes this; we drop the
     * cached analyser and dismiss any visible overlay so the next cycle
     * starts fresh.
     */
    public final void onTargetLanguageChanged() {
    }
    
    /**
     * Switch between screen and camera capture modes.
     */
    public final void setCaptureMode(@org.jetbrains.annotations.NotNull()
    com.bridge.translator.service.FloatingBubbleService.CaptureMode mode) {
    }
    
    /**
     * Called from the accessibility service when the foreground package is in
     * SYSTEM_PACKAGES, launcher, or BANKING_PACKAGES. Detaches the bubble
     * window. Does **not** touch the overlay or any in-flight cycle.
     */
    public final void hideBubbleForUnsupportedContext() {
    }
    
    /**
     * Called from the accessibility service when the foreground package is
     * supported. Re-attaches the bubble window in its current state if it
     * isn't already attached.
     */
    public final void showBubbleForSupportedContext() {
    }
    
    private final void showCancelZone() {
    }
    
    private final void hideCancelZone() {
    }
    
    private final void highlightCancelZone(boolean active) {
    }
    
    private final void launchPermissionScreen() {
    }
    
    private final void handleProjectionResult(android.content.Intent intent) {
    }
    
    private final com.bridge.translator.processing.ScreenAnalyser ensureAnalyser(java.lang.String lang) {
        return null;
    }
    
    private final java.lang.String targetLanguage() {
        return null;
    }
    
    private final void snapToEdge(android.view.View view) {
    }
    
    private final boolean isInStopZone(float rawY) {
        return false;
    }
    
    private final void createNotificationChannel() {
    }
    
    private final android.app.Notification buildNotification() {
        return null;
    }
    
    private final int overlayWindowType() {
        return 0;
    }
    
    private final android.graphics.drawable.GradientDrawable circleDrawable(int color) {
        return null;
    }
    
    private final android.util.DisplayMetrics realDisplayMetrics() {
        return null;
    }
    
    private final int dp(int value) {
        return 0;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0005\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002j\u0002\b\u0003j\u0002\b\u0004j\u0002\b\u0005\u00a8\u0006\u0006"}, d2 = {"Lcom/bridge/translator/service/FloatingBubbleService$BubbleState;", "", "(Ljava/lang/String;I)V", "IDLE", "CAPTURING", "SHOWING", "app_debug"})
    public static enum BubbleState {
        /*public static final*/ IDLE /* = new IDLE() */,
        /*public static final*/ CAPTURING /* = new CAPTURING() */,
        /*public static final*/ SHOWING /* = new SHOWING() */;
        
        BubbleState() {
        }
        
        @org.jetbrains.annotations.NotNull()
        public static kotlin.enums.EnumEntries<com.bridge.translator.service.FloatingBubbleService.BubbleState> getEntries() {
            return null;
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0004\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002j\u0002\b\u0003j\u0002\b\u0004\u00a8\u0006\u0005"}, d2 = {"Lcom/bridge/translator/service/FloatingBubbleService$CaptureMode;", "", "(Ljava/lang/String;I)V", "SCREEN", "CAMERA", "app_debug"})
    public static enum CaptureMode {
        /*public static final*/ SCREEN /* = new SCREEN() */,
        /*public static final*/ CAMERA /* = new CAMERA() */;
        
        CaptureMode() {
        }
        
        @org.jetbrains.annotations.NotNull()
        public static kotlin.enums.EnumEntries<com.bridge.translator.service.FloatingBubbleService.CaptureMode> getEntries() {
            return null;
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\"\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00040\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u000e\u0010\n\u001a\u00020\u000bX\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\u000bX\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000bX\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000fX\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0012X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0014\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0015\u001a\u00020\u000fX\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0016\u001a\u00020\u000bX\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0017\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0018\u001a\u00020\u000fX\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0019\u001a\u00020\u000bX\u0082T\u00a2\u0006\u0002\n\u0000R\"\u0010\u001c\u001a\u0004\u0018\u00010\u001b2\b\u0010\u001a\u001a\u0004\u0018\u00010\u001b@BX\u0086\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u001eR\u001e\u0010 \u001a\u00020\u001f2\u0006\u0010\u001a\u001a\u00020\u001f@BX\u0086\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b \u0010!\u00a8\u0006\""}, d2 = {"Lcom/bridge/translator/service/FloatingBubbleService$Companion;", "", "()V", "ACTION_MEDIA_PROJECTION_RESULT", "", "ACTION_STOP_SERVICE", "BANKING_PACKAGES", "", "getBANKING_PACKAGES", "()Ljava/util/Set;", "BUBBLE_DP", "", "CANCEL_ZONE_MARGIN_DP", "CANCEL_ZONE_SIZE_DP", "CAPTURE_BUBBLE_HIDE_DELAY_MS", "", "CHANNEL_ID", "DRAG_THRESHOLD", "", "EXTRA_RESULT_CODE", "EXTRA_RESULT_DATA", "LONG_PRESS_MS", "NOTIFICATION_ID", "PREF_AUTO_TRANSLATE_ENABLED", "SNAP_MS", "STOP_ZONE_DP", "<set-?>", "Lcom/bridge/translator/service/FloatingBubbleService;", "instance", "getInstance", "()Lcom/bridge/translator/service/FloatingBubbleService;", "", "isRunning", "()Z", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        /**
         * Banking / payment apps that MUST NOT receive a synthetic tap.
         * Held here (rather than in the accessibility service) so the tap-
         * through guard can read it without depending on the optional
         * accessibility service being connected.
         */
        @org.jetbrains.annotations.NotNull()
        public final java.util.Set<java.lang.String> getBANKING_PACKAGES() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final com.bridge.translator.service.FloatingBubbleService getInstance() {
            return null;
        }
        
        public final boolean isRunning() {
            return false;
        }
    }
}