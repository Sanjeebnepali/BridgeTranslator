package com.bridge.translator.keyboard

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.inputmethodservice.InputMethodService
import android.media.AudioManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.text.InputType
import android.view.Gravity
import android.view.HapticFeedbackConstants
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.bridgetranslator.TranslationEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

class BridgeKeyboardService : InputMethodService() {

    private enum class BridgeMode { EASY, MEDIUM, PRIVATE }
    private enum class TypingLayout { ENGLISH, NEPALI }
    private enum class KeyboardTheme { LIGHT, DARK }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val translationEngine = TranslationEngine()
    private val prefs by lazy { getSharedPreferences("bridge_prefs", Context.MODE_PRIVATE) }
    private val repeatHandler = Handler(Looper.getMainLooper())

    private var mode = BridgeMode.EASY
    private var typingLayout = TypingLayout.ENGLISH
    private var keyboardTheme = KeyboardTheme.LIGHT
    private var isCaps = true
    private var isSymbols = false
    private var isSettingsOpen = false
    private var isListening = false
    private var activeEditorInfo: EditorInfo? = null
    private var speechRecognizer: SpeechRecognizer? = null

    private var keyboardRoot: LinearLayout? = null
    private var toolbarRow: LinearLayout? = null
    private var rowsContainer: LinearLayout? = null
    private var statusView: TextView? = null
    private var targetView: TextView? = null

    private val deleteRepeat = object : Runnable {
        override fun run() {
            deleteOne()
            repeatHandler.postDelayed(this, 55L)
        }
    }

    override fun onCreateInputView(): View {
        mode = enumPref(KEY_MODE, BridgeMode.EASY)
        typingLayout = enumPref(KEY_LAYOUT, TypingLayout.ENGLISH)
        keyboardTheme = enumPref(KEY_THEME, KeyboardTheme.LIGHT)

        return LinearLayout(this).apply {
            keyboardRoot = this
            orientation = LinearLayout.VERTICAL
            setPadding(dp(8), dp(6), dp(8), dp(8))
            setBackgroundColor(bgColor())
            addView(buildTopArea())
            rowsContainer = LinearLayout(this@BridgeKeyboardService).apply {
                orientation = LinearLayout.VERTICAL
            }
            addView(rowsContainer)
            rebuildAll()
        }
    }

    override fun onStartInput(attribute: EditorInfo?, restarting: Boolean) {
        super.onStartInput(attribute, restarting)
        activeEditorInfo = attribute
        updateStatus()
    }

    override fun onFinishInput() {
        activeEditorInfo = null
        stopDeleteRepeat()
        super.onFinishInput()
    }

    override fun onDestroy() {
        stopDeleteRepeat()
        speechRecognizer?.destroy()
        translationEngine.close()
        scope.cancel()
        super.onDestroy()
    }

    private fun buildTopArea(): View {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL

            toolbarRow = LinearLayout(this@BridgeKeyboardService).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
            }
            toolbarRow?.addView(toolbarButton("\u2699") { toggleSettingsPanel() })
            toolbarRow?.addView(toolbarButton("\u263A") { commitText("\uD83D\uDE0A") })
            toolbarRow?.addView(toolbarButton("\u25D0") { toggleTheme() })
            toolbarRow?.addView(toolbarButton("Mic") { startVoiceInput() })

            targetView = TextView(this@BridgeKeyboardService).apply {
                gravity = Gravity.CENTER
                textSize = 12f
                typeface = Typeface.DEFAULT_BOLD
                setPadding(dp(6), 0, dp(6), 0)
                background = rounded(toolbarButtonColor(), dp(18))
                setOnClickListener {
                    tapFeedback(this)
                    isSettingsOpen = true
                    rebuildKeys()
                    updateStatus("Choose languages in settings.")
                }
            }
            toolbarRow?.addView(targetView, LinearLayout.LayoutParams(0, dp(42), 1f))

            statusView = TextView(this@BridgeKeyboardService).apply {
                textSize = 12f
                setPadding(dp(8), dp(3), dp(8), dp(6))
                maxLines = 2
            }

            addView(toolbarRow)
            addView(statusView)
        }
    }

    private fun rebuildAll() {
        keyboardRoot?.setBackgroundColor(bgColor())
        toolbarRow?.let { row ->
            for (i in 0 until row.childCount) {
                (row.getChildAt(i) as? TextView)?.let { styleToolbarButton(it) }
            }
        }
        statusView?.setTextColor(mutedTextColor())
        targetView?.setTextColor(accentColor())
        rebuildKeys()
        updateStatus()
    }

    private fun rebuildKeys() {
        val rows = rowsContainer ?: return
        rows.removeAllViews()

        if (isSettingsOpen) {
            buildSettingsPage(rows)
            return
        }

        if (isSymbols) {
            addRow(rows, listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0"))
            addRow(rows, listOf("@", "#", "$", "%", "&", "*", "-", "+", "(", ")"))
            addRow(rows, listOf(ABC, "/", ":", ";", "'", "\"", "?", "!", BACKSPACE))
        } else {
            when (typingLayout) {
                TypingLayout.ENGLISH -> {
                    addRow(rows, "qwertyuiop".map { it.toString() })
                    addRow(rows, "asdfghjkl".map { it.toString() }, startPaddingWeight = 0.35f, endPaddingWeight = 0.35f)
                    addRow(rows, listOf(SHIFT) + "zxcvbnm".map { it.toString() } + listOf(BACKSPACE))
                }
                TypingLayout.NEPALI -> {
                    addRow(rows, listOf("\u0915", "\u0916", "\u0917", "\u0918", "\u0919", "\u091A", "\u091B", "\u091C", "\u091D", "\u091E"))
                    addRow(rows, listOf("\u091F", "\u0920", "\u0921", "\u0922", "\u0923", "\u0924", "\u0925", "\u0926", "\u0927", "\u0928"))
                    addRow(rows, listOf(SHIFT, "\u092A", "\u092B", "\u092C", "\u092D", "\u092E", "\u092F", "\u0930", "\u0932", BACKSPACE))
                    addRow(rows, listOf("\u093E", "\u093F", "\u0940", "\u0941", "\u0942", "\u0947", "\u0948", "\u094B", "\u094C", "\u094D"))
                }
            }
        }

        addBottomRow(rows)
    }

    private fun addRow(
        root: LinearLayout,
        labels: List<String>,
        startPaddingWeight: Float = 0f,
        endPaddingWeight: Float = 0f
    ) {
        root.addView(LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            if (startPaddingWeight > 0f) addView(spacer(startPaddingWeight))
            labels.forEach { label ->
                addView(key(displayLabel(label), keyWeight(label), isSpecialKey(label)) { handleKey(label) })
            }
            if (endPaddingWeight > 0f) addView(spacer(endPaddingWeight))
        })
    }

    private fun addBottomRow(root: LinearLayout) {
        root.addView(LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            addView(key(if (isSymbols) ABC else "123", 1.05f, true) {
                isSymbols = !isSymbols
                rebuildKeys()
            })
            addView(key(",", 0.75f, true) { commitText(",") })
            addView(key("${typingLayoutLabel()} space", 3.6f, false) { commitText(" ") })
            addView(key(".", 0.75f, true) { commitText(".") })
            addView(key(ENTER, 1.15f, true) { translateThenSend() })
        })
    }

    private fun buildSettingsPage(root: LinearLayout) {
        root.addView(LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(10), dp(10), dp(10), dp(10))
            background = rounded(panelColor(), dp(22))

            addView(settingsHeader())
            addView(label("Bridge mode"))
            addView(settingRow {
                addView(settingChip("Easy", mode == BridgeMode.EASY) { setMode(BridgeMode.EASY) })
                addView(settingChip("Medium", mode == BridgeMode.MEDIUM) { setMode(BridgeMode.MEDIUM) })
                addView(settingChip("Private", mode == BridgeMode.PRIVATE) { setMode(BridgeMode.PRIVATE) })
            })

            addView(label("Typing keyboard"))
            addView(settingRow {
                addView(settingChip("English", typingLayout == TypingLayout.ENGLISH) { setTypingLayout(TypingLayout.ENGLISH) })
                addView(settingChip("Nepali", typingLayout == TypingLayout.NEPALI) { setTypingLayout(TypingLayout.NEPALI) })
            })

            addView(label("Translate to"))
            addView(settingRow {
                targetOptions.take(3).forEach { option ->
                    addView(targetChip(option))
                }
            })
            addView(settingRow {
                targetOptions.drop(3).take(3).forEach { option ->
                    addView(targetChip(option))
                }
            })
            addView(settingRow {
                targetOptions.drop(6).forEach { option ->
                    addView(targetChip(option))
                }
            })

            addView(label("Theme"))
            addView(settingRow {
                addView(settingChip("Light", keyboardTheme == KeyboardTheme.LIGHT) { setTheme(KeyboardTheme.LIGHT) })
                addView(settingChip("Dark", keyboardTheme == KeyboardTheme.DARK) { setTheme(KeyboardTheme.DARK) })
            })

            addView(label("More keyboard layouts can be added here as we test them. ML Kit translation supports fewer languages than a full 190-language keyboard list."))
        })
    }

    private fun settingsHeader(): TextView =
        TextView(this).apply {
            text = "Keyboard Settings"
            textSize = 18f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(keyTextColor())
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(8), 0, dp(8), dp(8))
            setOnClickListener {
                isSettingsOpen = false
                rebuildKeys()
                updateStatus()
            }
        }

    private fun settingRow(block: LinearLayout.() -> Unit): LinearLayout =
        LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            block()
        }

    private fun settingChip(label: String, active: Boolean, onClick: () -> Unit): TextView =
        TextView(this).apply {
            text = label
            gravity = Gravity.CENTER
            textSize = 13f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(if (active) Color.WHITE else keyTextColor())
            background = rounded(if (active) primaryColor() else keyColor(), dp(18))
            setOnClickListener {
                tapFeedback(this)
                onClick()
            }
            layoutParams = LinearLayout.LayoutParams(0, dp(40), 1f).apply {
                setMargins(dp(4), dp(4), dp(4), dp(4))
            }
        }

    private fun targetChip(option: LanguageOption): TextView {
        val current = prefs.getString("target_language", "ko") ?: "ko"
        return settingChip(option.name, current == option.code) {
            prefs.edit()
                .putString("target_language", option.code)
                .putBoolean("target_language_user_set", true)
                .apply()
            rebuildKeys()
            updateStatus()
        }
    }

    private fun key(label: String, weight: Float = 1f, special: Boolean = false, onClick: () -> Unit): View {
        return TextView(this).apply {
            text = label
            textSize = when {
                label.contains("space") -> 14f
                label.length == 1 -> 25f
                else -> 15f
            }
            gravity = Gravity.CENTER
            typeface = Typeface.DEFAULT
            setTextColor(keyTextColor())
            background = rounded(if (special) specialKeyColor() else keyColor(), dp(11))
            setOnTouchListener { view, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        tapFeedback(view)
                        if (label == BACKSPACE) {
                            startDeleteRepeat()
                        } else {
                            onClick()
                        }
                        true
                    }
                    MotionEvent.ACTION_UP,
                    MotionEvent.ACTION_CANCEL -> {
                        if (label == BACKSPACE) stopDeleteRepeat()
                        true
                    }
                    else -> true
                }
            }
        }.also {
            it.layoutParams = LinearLayout.LayoutParams(0, dp(52), weight).apply {
                setMargins(dp(4), dp(4), dp(4), dp(4))
            }
        }
    }

    private fun toolbarButton(label: String, onClick: () -> Unit): TextView =
        TextView(this).apply {
            text = label
            gravity = Gravity.CENTER
            textSize = if (label.length <= 2) 22f else 14f
            typeface = Typeface.DEFAULT_BOLD
            setOnClickListener {
                tapFeedback(this)
                onClick()
            }
            layoutParams = LinearLayout.LayoutParams(dp(54), dp(42)).apply {
                setMargins(dp(2), dp(2), dp(2), dp(2))
            }
        }

    private fun styleToolbarButton(view: TextView) {
        view.setTextColor(toolbarTextColor())
        view.background = rounded(toolbarButtonColor(), dp(18))
    }

    private fun chip(label: String, onClick: () -> Unit): TextView =
        TextView(this).apply {
            text = label
            gravity = Gravity.CENTER
            textSize = 13f
            typeface = Typeface.DEFAULT_BOLD
            setOnClickListener {
                tapFeedback(this)
                onClick()
            }
            layoutParams = LinearLayout.LayoutParams(0, dp(38), 1f).apply {
                setMargins(dp(4), dp(4), dp(4), dp(4))
            }
        }

    private fun label(value: String): TextView =
        TextView(this).apply {
            text = value
            textSize = 12f
            setTextColor(mutedTextColor())
            setPadding(dp(8), dp(4), dp(8), dp(2))
        }

    private fun spacer(weight: Float): View =
        View(this).apply {
            layoutParams = LinearLayout.LayoutParams(0, dp(52), weight)
        }

    private fun handleKey(rawLabel: String) {
        when (rawLabel) {
            SHIFT -> {
                isCaps = !isCaps
                rebuildKeys()
            }
            BACKSPACE -> deleteOne()
            ABC -> {
                isSymbols = false
                rebuildKeys()
            }
            else -> {
                val value = if (typingLayout == TypingLayout.ENGLISH && rawLabel.length == 1 && rawLabel[0].isLetter()) {
                    if (isCaps) rawLabel.uppercase() else rawLabel.lowercase()
                } else {
                    rawLabel
                }
                commitText(value)
            }
        }
    }

    private fun translateThenSend() {
        val editor = activeEditorInfo
        if (mode == BridgeMode.PRIVATE || isSensitiveEditor(editor)) {
            performSendAction()
            return
        }

        val inputConnection = currentInputConnection ?: return
        val beforeCursor = inputConnection.getTextBeforeCursor(1200, 0)?.toString().orEmpty()
        val sourceText = currentMessageSegment(beforeCursor).trim()
        if (sourceText.isBlank()) {
            performSendAction()
            return
        }

        val sourceLang = sourceLanguageForInput()
        val targetLang = prefs.getString("target_language", "ko") ?: "ko"
        if (sourceLang == targetLang) {
            performSendAction()
            return
        }

        val prepared = prepareForMode(sourceText)
        updateStatus("Translating and sending...")
        scope.launch {
            val translated = translate(prepared.textForTranslation, sourceLang, targetLang)
            val finalText = prepared.restore(translated)
            withContext(Dispatchers.Main) {
                replaceInputText(inputConnection, sourceText, finalText)
                updateStatus("Sent in ${displayLanguage(targetLang)}")
                performSendAction()
            }
        }
    }

    private suspend fun translate(text: String, source: String, target: String): String =
        suspendCancellableCoroutine { cont ->
            translationEngine.translate(
                text = text,
                srcCode = source,
                tgtCode = target,
                onDownloading = { updateStatus("Downloading language model...") },
                onSuccess = { if (cont.isActive) cont.resume(it) },
                onError = { if (cont.isActive) cont.resume(text) }
            )
        }

    private fun replaceInputText(inputConnection: InputConnection, sourceText: String, translated: String) {
        inputConnection.beginBatchEdit()
        try {
            inputConnection.deleteSurroundingText(sourceText.length, 0)
            inputConnection.commitText(translated, 1)
        } finally {
            inputConnection.endBatchEdit()
        }
    }

    private fun performSendAction() {
        val inputConnection = currentInputConnection ?: return
        val action = activeEditorInfo?.imeOptions?.and(EditorInfo.IME_MASK_ACTION) ?: EditorInfo.IME_ACTION_UNSPECIFIED
        when (action) {
            EditorInfo.IME_ACTION_SEND,
            EditorInfo.IME_ACTION_DONE,
            EditorInfo.IME_ACTION_GO,
            EditorInfo.IME_ACTION_SEARCH -> inputConnection.performEditorAction(action)
            else -> {
                inputConnection.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER))
                inputConnection.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER))
            }
        }
    }

    private fun prepareForMode(text: String): PreparedInput {
        val cleaned = when (mode) {
            BridgeMode.EASY -> text.trim()
            BridgeMode.MEDIUM -> addRuleBasedPolish(text)
            BridgeMode.PRIVATE -> text
        }
        return protectEmojiAndSymbols(cleaned)
    }

    private fun addRuleBasedPolish(raw: String): String {
        var text = raw.trim()
            .replace(Regex("\\s+"), " ")
            .replace(Regex("\\s+([,.!?])"), "$1")
            .replace(Regex("([,.!?])([^\\s,.!?])"), "$1 $2")
            .replace(Regex("\\bi\\b"), "I")

        if (text.isNotBlank()) {
            text = text.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        }

        if (!containsEmoji(text)) {
            val lower = text.lowercase()
            text += when {
                "thank" in lower || "thanks" in lower || "sorry" in lower -> " \uD83D\uDE4F"
                "love" in lower -> " \u2764\uFE0F"
                "happy" in lower || "great" in lower -> " \uD83D\uDE0A"
                lower == "ok" || lower.endsWith(" ok") -> " \uD83D\uDC4D"
                else -> ""
            }
        }
        return text
    }

    private fun protectEmojiAndSymbols(text: String): PreparedInput {
        val tokens = mutableListOf<String>()
        val out = StringBuilder()
        var i = 0
        while (i < text.length) {
            val codePoint = text.codePointAt(i)
            val chars = String(Character.toChars(codePoint))
            if (shouldProtect(codePoint)) {
                val token = " BRIDGETOKEN${tokens.size} "
                tokens.add(chars)
                out.append(token)
            } else {
                out.append(chars)
            }
            i += Character.charCount(codePoint)
        }
        return PreparedInput(out.toString().trim(), tokens)
    }

    private fun shouldProtect(codePoint: Int): Boolean {
        if (codePoint == 0x20) return false
        if (Character.isLetterOrDigit(codePoint)) return false
        val type = Character.getType(codePoint)
        return type == Character.OTHER_SYMBOL.toInt() ||
                type == Character.MATH_SYMBOL.toInt() ||
                type == Character.CURRENCY_SYMBOL.toInt() ||
                type == Character.MODIFIER_SYMBOL.toInt() ||
                codePoint in 0x1F000..0x1FAFF ||
                codePoint in 0x2600..0x27BF
    }

    private fun containsEmoji(text: String): Boolean {
        var i = 0
        while (i < text.length) {
            val codePoint = text.codePointAt(i)
            if (codePoint in 0x1F000..0x1FAFF || codePoint in 0x2600..0x27BF) return true
            i += Character.charCount(codePoint)
        }
        return false
    }

    private fun currentMessageSegment(beforeCursor: String): String =
        beforeCursor.substringAfterLast('\n').takeLast(500)

    private fun sourceLanguageForInput(): String =
        when (typingLayout) {
            TypingLayout.NEPALI -> "hi"
            TypingLayout.ENGLISH -> prefs.getString("source_language", "en") ?: "en"
        }

    private fun isSensitiveEditor(info: EditorInfo?): Boolean {
        val inputType = info?.inputType ?: return false
        val klass = inputType and InputType.TYPE_MASK_CLASS
        val variation = inputType and InputType.TYPE_MASK_VARIATION
        return variation == InputType.TYPE_TEXT_VARIATION_PASSWORD ||
                variation == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD ||
                variation == InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD ||
                variation == InputType.TYPE_NUMBER_VARIATION_PASSWORD ||
                klass == InputType.TYPE_CLASS_PHONE
    }

    private fun setMode(newMode: BridgeMode) {
        mode = newMode
        prefs.edit().putString(KEY_MODE, mode.name).apply()
        if (isSettingsOpen) rebuildKeys()
        updateStatus()
    }

    private fun setTypingLayout(newLayout: TypingLayout) {
        typingLayout = newLayout
        isCaps = typingLayout == TypingLayout.ENGLISH
        isSymbols = false
        prefs.edit().putString(KEY_LAYOUT, typingLayout.name).apply()
        rebuildKeys()
        updateStatus()
    }

    private fun toggleSettingsPanel() {
        isSettingsOpen = !isSettingsOpen
        rebuildKeys()
        updateStatus()
    }

    private fun toggleTheme() {
        setTheme(if (keyboardTheme == KeyboardTheme.LIGHT) KeyboardTheme.DARK else KeyboardTheme.LIGHT)
    }

    private fun setTheme(newTheme: KeyboardTheme) {
        keyboardTheme = newTheme
        prefs.edit().putString(KEY_THEME, keyboardTheme.name).apply()
        rebuildAll()
    }

    private fun startVoiceInput() {
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            updateStatus("Voice input is not available on this device.")
            return
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            updateStatus("Microphone permission is needed for voice input.")
            return
        }
        if (isListening) {
            speechRecognizer?.stopListening()
            return
        }

        val locale = when (typingLayout) {
            TypingLayout.NEPALI -> "ne-NP"
            TypingLayout.ENGLISH -> "en-US"
        }
        val recognizer = speechRecognizer ?: SpeechRecognizer.createSpeechRecognizer(this).also {
            speechRecognizer = it
        }
        recognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                isListening = true
                updateStatus("Listening...")
            }

            override fun onResults(results: Bundle?) {
                isListening = false
                val heard = results
                    ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?.firstOrNull()
                    .orEmpty()
                if (heard.isNotBlank()) commitText(heard)
                updateStatus()
            }

            override fun onError(error: Int) {
                isListening = false
                updateStatus("Could not hear clearly. Try again.")
            }

            override fun onBeginningOfSpeech() = Unit
            override fun onRmsChanged(rmsdB: Float) = Unit
            override fun onBufferReceived(buffer: ByteArray?) = Unit
            override fun onEndOfSpeech() {
                isListening = false
            }
            override fun onPartialResults(partialResults: Bundle?) = Unit
            override fun onEvent(eventType: Int, params: Bundle?) = Unit
        })

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, locale)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false)
        }
        recognizer.startListening(intent)
    }

    private fun updateStatus(message: String? = null) {
        targetView?.text = if (isSettingsOpen) {
            "Close settings"
        } else {
            "${typingLayoutLabel()} -> ${displayLanguage(prefs.getString("target_language", "ko") ?: "ko")}"
        }
        val default = when {
            isSettingsOpen -> "Tap Keyboard Settings title to return to typing."
            mode == BridgeMode.PRIVATE -> "Private: typing only, auto-translate off."
            isSensitiveEditor(activeEditorInfo) -> "Secure field detected: translation off."
            mode == BridgeMode.EASY -> "Enter translates and sends. Emojis stay."
            else -> "Medium: cleanup + emoji hint, then translate/send."
        }
        statusView?.text = message ?: default
    }

    private fun commitText(value: String) {
        currentInputConnection?.commitText(value, 1)
    }

    private fun deleteOne() {
        currentInputConnection?.deleteSurroundingText(1, 0)
    }

    private fun startDeleteRepeat() {
        stopDeleteRepeat()
        deleteOne()
        repeatHandler.postDelayed(deleteRepeat, 210L)
    }

    private fun stopDeleteRepeat() {
        repeatHandler.removeCallbacks(deleteRepeat)
    }

    private fun tapFeedback(view: View) {
        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        (getSystemService(Context.AUDIO_SERVICE) as? AudioManager)
            ?.playSoundEffect(AudioManager.FX_KEY_CLICK, 0.28f)
    }

    private fun displayLabel(rawLabel: String): String =
        if (typingLayout == TypingLayout.ENGLISH && rawLabel.length == 1 && rawLabel[0].isLetter() && isCaps) {
            rawLabel.uppercase()
        } else {
            rawLabel
        }

    private fun keyWeight(label: String): Float =
        when (label) {
            SHIFT -> 1.2f
            BACKSPACE -> 1.35f
            ABC -> 1.15f
            else -> 1f
        }

    private fun isSpecialKey(label: String): Boolean =
        label == SHIFT || label == BACKSPACE || label == ABC

    private fun typingLayoutLabel(): String =
        when (typingLayout) {
            TypingLayout.ENGLISH -> "English"
            TypingLayout.NEPALI -> "Nepali"
        }

    private fun displayLanguage(code: String): String =
        when (code) {
            "en" -> "English"
            "ko" -> "Korean"
            "ja" -> "Japanese"
            "zh" -> "Chinese"
            "es" -> "Spanish"
            "fr" -> "French"
            "hi" -> "Hindi"
            "ar" -> "Arabic"
            "de" -> "German"
            else -> code.uppercase()
        }

    private inline fun <reified T : Enum<T>> enumPref(key: String, default: T): T =
        runCatching { enumValueOf<T>(prefs.getString(key, default.name) ?: default.name) }.getOrDefault(default)

    private fun bgColor(): Int =
        if (keyboardTheme == KeyboardTheme.LIGHT) Color.rgb(244, 244, 250) else Color.rgb(22, 22, 39)

    private fun panelColor(): Int =
        if (keyboardTheme == KeyboardTheme.LIGHT) Color.rgb(235, 235, 244) else Color.rgb(29, 29, 48)

    private fun keyColor(): Int =
        if (keyboardTheme == KeyboardTheme.LIGHT) Color.WHITE else Color.rgb(35, 35, 59)

    private fun specialKeyColor(): Int =
        if (keyboardTheme == KeyboardTheme.LIGHT) Color.rgb(221, 228, 252) else Color.rgb(48, 48, 74)

    private fun toolbarButtonColor(): Int =
        if (keyboardTheme == KeyboardTheme.LIGHT) Color.TRANSPARENT else Color.rgb(35, 35, 59)

    private fun primaryColor(): Int = Color.rgb(98, 0, 238)

    private fun accentColor(): Int =
        if (keyboardTheme == KeyboardTheme.LIGHT) Color.rgb(55, 76, 130) else Color.rgb(122, 200, 255)

    private fun keyTextColor(): Int =
        if (keyboardTheme == KeyboardTheme.LIGHT) Color.rgb(27, 30, 36) else Color.WHITE

    private fun toolbarTextColor(): Int = keyTextColor()

    private fun mutedTextColor(): Int =
        if (keyboardTheme == KeyboardTheme.LIGHT) Color.rgb(90, 94, 106) else Color.rgb(169, 169, 188)

    private fun rounded(color: Int, radius: Int): GradientDrawable =
        GradientDrawable().apply {
            setColor(color)
            cornerRadius = radius.toFloat()
        }

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()

    private data class PreparedInput(
        val textForTranslation: String,
        val protectedTokens: List<String>
    ) {
        fun restore(translated: String): String {
            var restored = translated
            protectedTokens.forEachIndexed { index, token ->
                restored = restored.replace(Regex("\\s*BRIDGETOKEN$index\\s*", RegexOption.IGNORE_CASE), token)
            }
            return restored.replace(Regex("\\s+([,.!?])"), "$1").trim()
        }
    }

    private data class LanguageOption(
        val code: String,
        val name: String
    )

    private companion object {
        const val KEY_MODE = "bridge_keyboard_mode"
        const val KEY_LAYOUT = "bridge_keyboard_layout"
        const val KEY_THEME = "bridge_keyboard_theme"
        const val SHIFT = "\u21E7"
        const val BACKSPACE = "\u232B"
        const val ENTER = "\u21B5"
        const val ABC = "ABC"
        val targetOptions = listOf(
            LanguageOption("en", "English"),
            LanguageOption("ko", "Korean"),
            LanguageOption("ja", "Japanese"),
            LanguageOption("zh", "Chinese"),
            LanguageOption("es", "Spanish"),
            LanguageOption("fr", "French"),
            LanguageOption("hi", "Hindi"),
            LanguageOption("ar", "Arabic"),
            LanguageOption("de", "German")
        )
    }
}
