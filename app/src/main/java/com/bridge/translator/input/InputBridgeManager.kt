package com.bridge.translator.input

import android.accessibilityservice.AccessibilityService
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.PixelFormat
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.bridgetranslator.R
import com.example.bridgetranslator.TranslationEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

class InputBridgeManager(
    private val service: AccessibilityService,
    private val scope: CoroutineScope
) {
    private val prefs = service.getSharedPreferences("bridge_prefs", Context.MODE_PRIVATE)
    private val windowManager = service.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val translationEngine = TranslationEngine()

    private var globeView: TextView? = null
    private var miniTranslatorView: View? = null
    private var fallbackView: View? = null
    private var focusedNode: AccessibilityNodeInfo? = null
    private var lastFocusedKey: FocusKey? = null
    private var translateJob: Job? = null

    fun isEnabled(): Boolean = prefs.getBoolean(KEY_INPUT_BRIDGE_ENABLED, false)

    fun isAllowedPackage(packageName: String?): Boolean {
        val pkg = packageName?.lowercase().orEmpty()
        if (pkg.isBlank() || pkg == service.packageName) return false
        return DEFAULT_WHITELIST.any { pkg == it || pkg.startsWith("$it.") }
    }

    fun onEditableFocused(node: AccessibilityNodeInfo?) {
        if (!isEnabled()) {
            hideAll()
            return
        }
        val safeNode = node ?: return
        if (!isSupportedEditable(safeNode)) {
            hideGlobe()
            hideFallback()
            clearFocus()
            return
        }
        hideFallback()
        focusedNode?.recycle()
        focusedNode = AccessibilityNodeInfo.obtain(safeNode)
        lastFocusedKey = safeNode.focusKey()
        showGlobeNear(safeNode)
    }

    fun onPossibleFocusLoss(newFocusedNode: AccessibilityNodeInfo?) {
        val previous = focusedNode ?: return
        if (newFocusedNode != null && newFocusedNode.focusKey() == lastFocusedKey) {
            if (isSupportedEditable(newFocusedNode)) {
                focusedNode?.recycle()
                focusedNode = AccessibilityNodeInfo.obtain(newFocusedNode)
                showGlobeNear(newFocusedNode)
            }
            return
        }
        translateFocusedText(previous, manual = false)
        hideGlobe()
        clearFocus()
    }

    fun onTextChanged(node: AccessibilityNodeInfo?) {
        val safeNode = node ?: return
        if (safeNode.focusKey() == lastFocusedKey && isSupportedEditable(safeNode)) {
            focusedNode?.recycle()
            focusedNode = AccessibilityNodeInfo.obtain(safeNode)
            showGlobeNear(safeNode)
        }
    }

    fun hideAll() {
        hideGlobe()
        hideMiniTranslator()
        hideFallback()
        clearFocus()
    }

    fun close() {
        hideAll()
        translationEngine.close()
    }

    private fun showGlobeNear(node: AccessibilityNodeInfo) {
        val bounds = Rect()
        node.getBoundsInScreen(bounds)
        if (bounds.isEmpty) return

        val view = globeView ?: TextView(service).apply {
            text = "\uD83C\uDF10"
            textSize = 22f
            gravity = Gravity.CENTER
            setTextColor(ContextCompat.getColor(service, android.R.color.white))
            background = ContextCompat.getDrawable(service, R.drawable.bubble_active_gradient)
            elevation = 10f
            setOnClickListener { onGlobeClicked() }
            globeView = this
        }

        val size = dp(48)
        val params = WindowManager.LayoutParams(
            size,
            size,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = (bounds.right - size).coerceAtLeast(0)
            y = (bounds.top - size - dp(6)).coerceAtLeast(0)
        }

        if (view.parent == null) {
            windowManager.addView(view, params)
        } else {
            windowManager.updateViewLayout(view, params)
        }
    }

    private fun onGlobeClicked() {
        val node = focusedNode ?: return
        val text = node.text?.toString()?.trim().orEmpty()
        if (text.isBlank()) {
            showMiniTranslator()
        } else {
            translateFocusedText(node, manual = true)
        }
    }

    private fun translateFocusedText(node: AccessibilityNodeInfo, manual: Boolean) {
        val original = node.text?.toString()?.trim().orEmpty()
        if (original.isBlank() || shouldProtectText(original)) {
            if (manual && original.isBlank()) showMiniTranslator()
            return
        }
        val targetNode = AccessibilityNodeInfo.obtain(node)
        translateJob?.cancel()
        translateJob = scope.launch {
            try {
                val translated = translate(original)
                withContext(Dispatchers.Main) {
                    val replaced = replaceNodeText(targetNode, translated)
                    if (replaced) {
                        Toast.makeText(service, "Sent in ${targetDisplayName()} ${targetFlag()}", Toast.LENGTH_SHORT).show()
                    } else {
                        showFallbackTranslation(translated, sensitive = shouldProtectText(original))
                    }
                }
            } finally {
                targetNode.recycle()
            }
        }
    }

    private suspend fun translate(text: String): String {
        val source = prefs.getString(KEY_SOURCE_LANG, null) ?: "en"
        val target = prefs.getString(KEY_TARGET_LANG, null) ?: "ko"
        if (source == target) return text
        return suspendCancellableCoroutine { cont ->
            translationEngine.translate(
                text = text,
                srcCode = source,
                tgtCode = target,
                onDownloading = {},
                onSuccess = { if (cont.isActive) cont.resume(it) },
                onError = { if (cont.isActive) cont.resume(text) }
            )
        }
    }

    private fun replaceNodeText(node: AccessibilityNodeInfo, text: String): Boolean {
        val args = Bundle().apply {
            putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text)
        }
        return runCatching {
            node.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, args)
        }.getOrDefault(false)
    }

    private fun showMiniTranslator() {
        hideMiniTranslator()
        val container = LinearLayout(service).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(14), dp(12), dp(14), dp(12))
            background = ContextCompat.getDrawable(service, R.drawable.bg_bubble)
            elevation = 14f
        }
        val input = EditText(service).apply {
            hint = "Type here"
            minWidth = dp(260)
            maxLines = 4
        }
        val row = LinearLayout(service).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.END
        }
        val cancel = Button(service).apply {
            text = "Cancel"
            setOnClickListener { hideMiniTranslator() }
        }
        val paste = Button(service).apply {
            text = "Translate"
            setOnClickListener {
                val original = input.text?.toString()?.trim().orEmpty()
                if (original.isBlank()) return@setOnClickListener
                scope.launch {
                    val translated = translate(original)
                    withContext(Dispatchers.Main) {
                        val node = focusedNode
                        val replaced = node != null && replaceNodeText(node, translated)
                        hideMiniTranslator()
                        if (replaced) {
                            Toast.makeText(service, "Sent in ${targetDisplayName()} ${targetFlag()}", Toast.LENGTH_SHORT).show()
                        } else {
                            showFallbackTranslation(translated, sensitive = shouldProtectText(original))
                        }
                    }
                }
            }
        }
        row.addView(cancel)
        row.addView(paste)
        container.addView(input)
        container.addView(row)

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
            y = dp(120)
        }
        windowManager.addView(container, params)
        miniTranslatorView = container
        input.requestFocus()
    }

    private fun showFallbackTranslation(text: String, sensitive: Boolean) {
        hideFallback()
        val displayText = if (sensitive) PRIVACY_MASK else text
        val container = LinearLayout(service).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(14), dp(16), dp(14))
            background = ContextCompat.getDrawable(service, R.drawable.bg_bubble)
            elevation = 14f
        }
        val title = TextView(service).apply {
            this.text = "App blocked paste"
            textSize = 14f
            setTextColor(ContextCompat.getColor(service, android.R.color.white))
        }
        val body = TextView(service).apply {
            this.text = displayText
            textSize = 18f
            setTextColor(ContextCompat.getColor(service, android.R.color.white))
            setPadding(0, dp(10), 0, dp(10))
            maxLines = 5
        }
        val row = LinearLayout(service).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.END
        }
        val close = Button(service).apply {
            this.text = "Close"
            setOnClickListener { hideFallback() }
        }
        val copy = Button(service).apply {
            this.text = "Copy"
            setOnClickListener {
                val clipboard = service.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(ClipData.newPlainText("Bridge translation", displayText))
                Toast.makeText(service, "Translation copied", Toast.LENGTH_SHORT).show()
                if (sensitive) {
                    scope.launch {
                        delay(30_000L)
                        withContext(Dispatchers.Main) {
                            clipboard.setPrimaryClip(ClipData.newPlainText("", ""))
                        }
                    }
                }
            }
        }
        row.addView(close)
        row.addView(copy)
        container.addView(title)
        container.addView(body)
        container.addView(row)

        val params = WindowManager.LayoutParams(
            service.resources.displayMetrics.widthPixels - dp(32),
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = dp(16)
            y = dp(140)
        }
        windowManager.addView(container, params)
        fallbackView = container
    }

    private fun hideGlobe() {
        globeView?.let { view ->
            if (view.parent != null) runCatching { windowManager.removeView(view) }
        }
        globeView = null
    }

    private fun hideMiniTranslator() {
        miniTranslatorView?.let { view ->
            if (view.parent != null) runCatching { windowManager.removeView(view) }
        }
        miniTranslatorView = null
    }

    private fun hideFallback() {
        fallbackView?.let { view ->
            if (view.parent != null) runCatching { windowManager.removeView(view) }
        }
        fallbackView = null
    }

    private fun clearFocus() {
        focusedNode?.recycle()
        focusedNode = null
        lastFocusedKey = null
    }

    private fun isSupportedEditable(node: AccessibilityNodeInfo): Boolean {
        if (node.isPassword) return false
        val className = node.className?.toString().orEmpty()
        if (!node.isEditable && !className.contains("EditText", ignoreCase = true)) return false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && isSensitiveInputType(node.inputType)) return false
        return true
    }

    private fun isSensitiveInputType(inputType: Int): Boolean {
        val variation = inputType and InputType.TYPE_MASK_VARIATION
        val klass = inputType and InputType.TYPE_MASK_CLASS
        return variation == InputType.TYPE_TEXT_VARIATION_PASSWORD ||
                variation == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD ||
                variation == InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD ||
                variation == InputType.TYPE_NUMBER_VARIATION_PASSWORD ||
                klass == InputType.TYPE_CLASS_PHONE
    }

    private fun shouldProtectText(text: String): Boolean =
        Regex("\\b(otp|password|passcode|pin|cvv|token|bank|card)\\b", RegexOption.IGNORE_CASE).containsMatchIn(text)

    private fun targetDisplayName(): String =
        when (prefs.getString(KEY_TARGET_LANG, null) ?: "ko") {
            "ko" -> "Korean"
            "en" -> "English"
            "ja" -> "Japanese"
            "zh" -> "Chinese"
            "es" -> "Spanish"
            "fr" -> "French"
            "hi" -> "Hindi"
            "ar" -> "Arabic"
            else -> "target language"
        }

    private fun targetFlag(): String =
        when (prefs.getString(KEY_TARGET_LANG, null) ?: "ko") {
            "ko" -> "\uD83C\uDDF0\uD83C\uDDF7"
            "en" -> "\uD83C\uDDFA\uD83C\uDDF8"
            "ja" -> "\uD83C\uDDEF\uD83C\uDDF5"
            "zh" -> "\uD83C\uDDE8\uD83C\uDDF3"
            "es" -> "\uD83C\uDDEA\uD83C\uDDF8"
            "fr" -> "\uD83C\uDDEB\uD83C\uDDF7"
            "hi" -> "\uD83C\uDDEE\uD83C\uDDF3"
            "ar" -> "\uD83C\uDDF8\uD83C\uDDE6"
            else -> "\uD83C\uDF10"
        }

    private fun dp(value: Int): Int =
        (value * service.resources.displayMetrics.density).toInt()

    private fun AccessibilityNodeInfo.focusKey(): FocusKey {
        val bounds = Rect()
        getBoundsInScreen(bounds)
        val stableViewId = viewIdResourceName.orEmpty()
        val useBoundsFallback = stableViewId.isBlank()
        return FocusKey(
            packageName = packageName?.toString().orEmpty(),
            viewId = stableViewId,
            className = className?.toString().orEmpty(),
            left = if (useBoundsFallback) bounds.left else 0,
            top = if (useBoundsFallback) bounds.top else 0,
            right = if (useBoundsFallback) bounds.right else 0,
            bottom = if (useBoundsFallback) bounds.bottom else 0
        )
    }

    private data class FocusKey(
        val packageName: String,
        val viewId: String,
        val className: String,
        val left: Int,
        val top: Int,
        val right: Int,
        val bottom: Int
    )

    companion object {
        const val KEY_INPUT_BRIDGE_ENABLED = "input_bridge_enabled"
        const val KEY_SOURCE_LANG = "source_language"
        const val KEY_TARGET_LANG = "target_language"
        const val PRIVACY_MASK = "@#$ @#$"

        private val DEFAULT_WHITELIST = setOf(
            "com.whatsapp",
            "org.telegram.messenger",
            "com.google.android.gm",
            "com.facebook.orca",
            "com.instagram.android",
            "com.android.chrome",
            "com.google.android.apps.messaging"
        )
    }
}
