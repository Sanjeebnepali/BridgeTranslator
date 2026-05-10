package com.bridge.translator.service

import android.accessibilityservice.AccessibilityService
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.core.app.NotificationCompat
import com.bridge.translator.input.InputBridgeManager
import com.example.bridgetranslator.R
import com.example.bridgetranslator.SettingsActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

class BridgeAccessibilityService : AccessibilityService() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private lateinit var inputBridgeManager: InputBridgeManager

    override fun onServiceConnected() {
        super.onServiceConnected()
        inputBridgeManager = InputBridgeManager(this, scope)
        updateNotification()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val safeEvent = event ?: return
        if (!::inputBridgeManager.isInitialized) return

        val packageName = safeEvent.packageName?.toString()
        if (!inputBridgeManager.isEnabled()) {
            inputBridgeManager.hideAll()
            cancelNotification()
            return
        }
        updateNotification()

        if (!inputBridgeManager.isAllowedPackage(packageName)) {
            inputBridgeManager.hideAll()
            return
        }

        when (safeEvent.eventType) {
            AccessibilityEvent.TYPE_VIEW_FOCUSED -> {
                val source = safeEvent.source
                try {
                    if (source != null && source.isEditableLike()) {
                        inputBridgeManager.onEditableFocused(source)
                    } else {
                        val focused = findFocusedInput()
                        try {
                            inputBridgeManager.onPossibleFocusLoss(focused)
                        } finally {
                            focused?.recycle()
                        }
                    }
                } finally {
                    source?.recycle()
                }
            }

            AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED -> {
                val source = safeEvent.source
                try {
                    inputBridgeManager.onTextChanged(source)
                } finally {
                    source?.recycle()
                }
            }

            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED,
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> {
                val focused = findFocusedInput()
                try {
                    inputBridgeManager.onPossibleFocusLoss(focused)
                } finally {
                    focused?.recycle()
                }
            }
        }
    }

    override fun onInterrupt() {
        if (::inputBridgeManager.isInitialized) inputBridgeManager.hideAll()
    }

    override fun onDestroy() {
        if (::inputBridgeManager.isInitialized) inputBridgeManager.close()
        cancelNotification()
        scope.cancel()
        super.onDestroy()
    }

    private fun findFocusedInput(): AccessibilityNodeInfo? =
        runCatching {
            rootInActiveWindow?.findFocus(AccessibilityNodeInfo.FOCUS_INPUT)
        }.getOrNull()

    private fun updateNotification() {
        if (!::inputBridgeManager.isInitialized || !inputBridgeManager.isEnabled()) {
            cancelNotification()
            return
        }
        createNotificationChannel()
        val intent = Intent(this, SettingsActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            42,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notification = NotificationCompat.Builder(this, INPUT_BRIDGE_CHANNEL)
            .setSmallIcon(R.drawable.ic_translate)
            .setContentTitle("Input Bridge is on")
            .setContentText("Bridge can translate typed text in supported apps.")
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setSilent(true)
            .build()

        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(INPUT_BRIDGE_NOTIFICATION_ID, notification)
    }

    private fun cancelNotification() {
        getSystemService(NotificationManager::class.java)
            .cancel(INPUT_BRIDGE_NOTIFICATION_ID)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = getSystemService(NotificationManager::class.java)
        if (manager.getNotificationChannel(INPUT_BRIDGE_CHANNEL) != null) return
        val channel = NotificationChannel(
            INPUT_BRIDGE_CHANNEL,
            "Input Bridge",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Shows when Input Bridge is monitoring supported text fields."
            setShowBadge(false)
        }
        manager.createNotificationChannel(channel)
    }

    private fun AccessibilityNodeInfo.isEditableLike(): Boolean =
        isEditable || className?.toString()?.contains("EditText", ignoreCase = true) == true

    private companion object {
        const val INPUT_BRIDGE_CHANNEL = "input_bridge"
        const val INPUT_BRIDGE_NOTIFICATION_ID = 5127
    }
}
