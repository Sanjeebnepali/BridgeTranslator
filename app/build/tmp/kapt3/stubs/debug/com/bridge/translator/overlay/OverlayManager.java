package com.bridge.translator.overlay;

/**
 * Pure visual overlay. The window is **completely non-touchable** — every
 * touch falls straight through to the underlying app, exactly as if the
 * overlay weren't there. Dismissal happens via the bubble (which is in its
 * SHOWING state — red ✕ icon — while an overlay is up); see
 * [com.bridge.translator.service.FloatingBubbleService] for the wiring.
 *
 * This is the same UX pattern used by Google Lens, Apple Live Text, and
 * Microsoft Translator camera modes — a separate close affordance instead of
 * tap-anywhere-to-dismiss, because intercepting taps would steal the user's
 * gesture from the underlying app and cancel scrolls/clicks mid-stream.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000F\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\f\u0018\u0000 &2\u00020\u0001:\u0001&B%\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\tJ\u0006\u0010\u001d\u001a\u00020\u0015J\u0006\u0010\u001e\u001a\u00020\u0015J\u0006\u0010\u001f\u001a\u00020\u0015J\u0006\u0010 \u001a\u00020\u000fJ\b\u0010!\u001a\u00020\u0007H\u0002J\u0006\u0010\"\u001a\u00020\u0015J\u0006\u0010#\u001a\u00020\u0015J\u000e\u0010$\u001a\u00020\u00152\u0006\u0010%\u001a\u00020\u000bR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\n\u001a\u0004\u0018\u00010\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\f\u001a\u0004\u0018\u00010\rX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u000fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0011\u0010\u0011\u001a\u00020\u000f8F\u00a2\u0006\u0006\u001a\u0004\b\u0011\u0010\u0012R\"\u0010\u0013\u001a\n\u0012\u0004\u0012\u00020\u0015\u0018\u00010\u0014X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0016\u0010\u0017\"\u0004\b\u0018\u0010\u0019R\u000e\u0010\u001a\u001a\u00020\u001bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001c\u001a\u00020\u0007X\u0082D\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\'"}, d2 = {"Lcom/bridge/translator/overlay/OverlayManager;", "", "context", "Landroid/content/Context;", "windowManager", "Landroid/view/WindowManager;", "screenW", "", "screenH", "(Landroid/content/Context;Landroid/view/WindowManager;II)V", "currentBitmap", "Landroid/graphics/Bitmap;", "imageView", "Landroid/widget/ImageView;", "isHiddenForCapture", "", "isShowing", "isVisible", "()Z", "onFirstTouch", "Lkotlin/Function0;", "", "getOnFirstTouch", "()Lkotlin/jvm/functions/Function0;", "setOnFirstTouch", "(Lkotlin/jvm/functions/Function0;)V", "params", "Landroid/view/WindowManager$LayoutParams;", "staticFlags", "fadeOut", "fadeOutFast", "hideForCapture", "isAttached", "overlayType", "remove", "restoreAfterCapture", "show", "processedBitmap", "Companion", "app_debug"})
public final class OverlayManager {
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull()
    private final android.view.WindowManager windowManager = null;
    private final int screenW = 0;
    private final int screenH = 0;
    @org.jetbrains.annotations.Nullable()
    private android.widget.ImageView imageView;
    @org.jetbrains.annotations.Nullable()
    private android.graphics.Bitmap currentBitmap;
    private boolean isShowing = false;
    private boolean isHiddenForCapture = false;
    @org.jetbrains.annotations.Nullable()
    private kotlin.jvm.functions.Function0<kotlin.Unit> onFirstTouch;
    
    /**
     * Permanent flags. The overlay must NEVER receive input — touches must
     * fall straight through to the underlying app. Removing FLAG_NOT_TOUCHABLE
     * here is the bug that previously broke single-tap clicks and scrolls.
     */
    private final int staticFlags = 1816;
    @org.jetbrains.annotations.NotNull()
    private final android.view.WindowManager.LayoutParams params = null;
    @java.lang.Deprecated()
    public static final long FADE_IN_MS = 140L;
    @java.lang.Deprecated()
    public static final long FADE_OUT_MS = 120L;
    @java.lang.Deprecated()
    public static final long FADE_OUT_FAST_MS = 50L;
    @org.jetbrains.annotations.NotNull()
    private static final com.bridge.translator.overlay.OverlayManager.Companion Companion = null;
    
    public OverlayManager(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    android.view.WindowManager windowManager, int screenW, int screenH) {
        super();
    }
    
    public final boolean isVisible() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final kotlin.jvm.functions.Function0<kotlin.Unit> getOnFirstTouch() {
        return null;
    }
    
    public final void setOnFirstTouch(@org.jetbrains.annotations.Nullable()
    kotlin.jvm.functions.Function0<kotlin.Unit> p0) {
    }
    
    /**
     * Whether the overlay window is currently attached to the WindowManager.
     */
    public final boolean isAttached() {
        return false;
    }
    
    public final void show(@org.jetbrains.annotations.NotNull()
    android.graphics.Bitmap processedBitmap) {
    }
    
    /**
     * Instant alpha=0; preserved for a future auto-mode capture flow. Not
     * used by the manual snap-and-translate path.
     */
    public final void hideForCapture() {
    }
    
    public final void restoreAfterCapture() {
    }
    
    /**
     * Smooth fade-out. Used by the bubble service on dismiss.
     */
    public final void fadeOut() {
    }
    
    /**
     * Snap-out; preserved for a future auto-mode. Not used manually.
     */
    public final void fadeOutFast() {
    }
    
    public final void remove() {
    }
    
    private final int overlayType() {
        return 0;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0003\b\u0082\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0007"}, d2 = {"Lcom/bridge/translator/overlay/OverlayManager$Companion;", "", "()V", "FADE_IN_MS", "", "FADE_OUT_FAST_MS", "FADE_OUT_MS", "app_debug"})
    static final class Companion {
        
        private Companion() {
            super();
        }
    }
}