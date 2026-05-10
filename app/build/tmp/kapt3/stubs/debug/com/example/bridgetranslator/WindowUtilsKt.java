package com.example.bridgetranslator;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000\u0014\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\u001a\u001e\u0010\u0000\u001a\u00020\u0001*\u00020\u00022\b\b\u0002\u0010\u0003\u001a\u00020\u00042\b\b\u0002\u0010\u0005\u001a\u00020\u0004\u00a8\u0006\u0006"}, d2 = {"applySystemInsets", "", "Landroid/app/Activity;", "topViewId", "", "bottomNavId", "app_debug"})
public final class WindowUtilsKt {
    
    /**
     * Call once in each Activity.onCreate() after setContentView().
     *
     * @param topViewId  The view that should receive status-bar top padding
     *                  (e.g. the header / first child). Pass 0 to skip.
     * @param bottomNavId The bottom navigation view that gets nav-bar bottom padding.
     *                  Defaults to R.id.bottomNav.
     */
    public static final void applySystemInsets(@org.jetbrains.annotations.NotNull()
    android.app.Activity $this$applySystemInsets, int topViewId, int bottomNavId) {
    }
}