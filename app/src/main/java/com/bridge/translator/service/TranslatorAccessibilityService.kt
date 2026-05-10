package com.bridge.translator.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent

private const val FLOW_TAG = "BridgeFlow"

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
class TranslatorAccessibilityService : AccessibilityService() {

    private val launcherPackages: Set<String> by lazy { resolveLauncherPackages() }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val safeEvent = event ?: return

        // Auto-hide the overlay when user interacts (clicks or scrolls) with the underlying app.
        if (safeEvent.eventType == AccessibilityEvent.TYPE_VIEW_CLICKED ||
            safeEvent.eventType == AccessibilityEvent.TYPE_VIEW_SCROLLED) {
            FloatingBubbleService.instance?.dismissOverlayIfShowing()
            // We still continue to check window state changes below just in case,
            // though usually these events are mutually exclusive.
        }

        if (safeEvent.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return
        val packageName = safeEvent.packageName?.toString().orEmpty()
        if (packageName.isEmpty() || packageName == applicationContext.packageName) return

        val service = FloatingBubbleService.instance ?: return
        
        // Window state changed (e.g. page navigation or new app) -> hide overlay
        service.dismissOverlayIfShowing()
        
        service.currentForegroundPackage = packageName

        val isUnsupported = packageName in SYSTEM_PACKAGES ||
                packageName in launcherPackages ||
                packageName in FloatingBubbleService.BANKING_PACKAGES
        if (isUnsupported) {
            Log.d(FLOW_TAG, "Accessibility unsupported context package=$packageName -> hide bubble")
            service.hideBubbleForUnsupportedContext()
        } else {
            Log.d(FLOW_TAG, "Accessibility supported context package=$packageName -> show bubble")
            service.showBubbleForSupportedContext()
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        Log.d(FLOW_TAG, "Accessibility service connected (passive context tracker)")
    }

    override fun onDestroy() {
        if (instance === this) instance = null
        Log.d(FLOW_TAG, "Accessibility service destroyed")
        super.onDestroy()
    }

    override fun onUnbind(intent: Intent?): Boolean {
        if (instance === this) instance = null
        return super.onUnbind(intent)
    }

    override fun onInterrupt() {
        Log.w(FLOW_TAG, "Accessibility service interrupted")
    }

    private fun resolveLauncherPackages(): Set<String> {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
        }
        val packages = packageManager.queryIntentActivities(intent, 0)
            .mapNotNull { it.activityInfo?.packageName }
            .toSet()
        Log.d(FLOW_TAG, "Resolved launcher packages=$packages")
        return packages
    }

    companion object {
        @Volatile
        var instance: TranslatorAccessibilityService? = null
            private set

        private val SYSTEM_PACKAGES = setOf(
            "com.android.systemui"
        )
    }
}
