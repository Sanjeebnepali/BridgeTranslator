package com.example.bridgetranslator

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bridge.translator.tts.SpeechEngine
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class TranslateActivity : AppCompatActivity() {

    private lateinit var languageManager: LanguageManager
    private lateinit var engine: TranslationEngine
    private lateinit var tvSourceLang: TextView
    private lateinit var tvTargetLang: TextView
    private lateinit var tvResult: TextView
    private lateinit var cardResult: MaterialCardView
    private lateinit var btnTranslate: View
    private lateinit var btnSave: View
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_translate)
        applySystemInsets(topViewId = R.id.topBar, bottomNavId = R.id.bottomNav)

        languageManager = LanguageManager(this)
        engine = TranslationEngine()

        val etInput = findViewById<EditText>(R.id.etInput)
        tvResult = findViewById(R.id.tvResult)
        cardResult = findViewById(R.id.cardResult)
        btnTranslate = findViewById(R.id.btnTranslate)
        val btnClear = findViewById<View>(R.id.btnClear)
        btnSave = findViewById(R.id.btnSave)
        val btnCopy = findViewById<View>(R.id.btnCopy)
        val btnSwap = findViewById<View>(R.id.btnSwapLang)
        tvSourceLang = findViewById(R.id.tvSourceLang)
        tvTargetLang = findViewById(R.id.tvTargetLang)
        progressBar = findViewById(R.id.progressTranslate)

        etInput.setOnEditorActionListener { view, actionId, event ->
            val pressedEnter = event?.keyCode == KeyEvent.KEYCODE_ENTER &&
                    event.action == KeyEvent.ACTION_UP
            if (actionId == EditorInfo.IME_ACTION_DONE || pressedEnter) {
                hideKeyboard(view)
                view.clearFocus()
                true
            } else {
                false
            }
        }

        // Reactively update language labels from DataStore
        lifecycleScope.launch {
            combine(languageManager.sourceLangCode, languageManager.targetLangCode) { s, t ->
                Pair(s, t)
            }.collect { (src, tgt) ->
                val srcLang = Language.getLanguageByCode(src)
                val tgtLang = Language.getLanguageByCode(tgt)
                tvSourceLang.text = "${srcLang?.flagEmoji ?: ""} ${srcLang?.name ?: src}"
                tvTargetLang.text = "${tgtLang?.flagEmoji ?: ""} ${tgtLang?.name ?: tgt}"
            }
        }

        // Source language picker
        tvSourceLang.setOnClickListener {
            LanguageBottomSheet.newInstance(isSource = true).also { sheet ->
                sheet.setOnLanguageSelectedListener { cardResult.visibility = View.GONE }
                sheet.show(supportFragmentManager, "srcLang")
            }
        }

        // Target language picker
        tvTargetLang.setOnClickListener {
            LanguageBottomSheet.newInstance(isSource = false).also { sheet ->
                sheet.setOnLanguageSelectedListener { cardResult.visibility = View.GONE }
                sheet.show(supportFragmentManager, "tgtLang")
            }
        }

        // Swap languages
        btnSwap.setOnClickListener {
            lifecycleScope.launch {
                val currentResult = tvResult.text.toString()
                languageManager.swapLanguages()
                if (cardResult.visibility == View.VISIBLE && currentResult.isNotEmpty()) {
                    etInput.setText(currentResult)
                    cardResult.visibility = View.GONE
                }
            }
        }

        // Translate button - real ML Kit on-device translation
        btnTranslate.setOnClickListener {
            val input = etInput.text.toString().trim()
            if (input.isEmpty()) {
                Toast.makeText(this, "Please enter text to translate", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            lifecycleScope.launch {
                val srcCode = languageManager.sourceLangCode.first()
                val tgtCode = languageManager.targetLangCode.first()
                val wifiOnly = getSharedPreferences("bridge_prefs", Context.MODE_PRIVATE)
                    .getBoolean("wifi_only_download", false)

                setTranslating(true)
                engine.translate(
                    text = input,
                    srcCode = srcCode,
                    tgtCode = tgtCode,
                    wifiOnly = wifiOnly,
                    onDownloading = {
                        runOnUiThread {
                            tvResult.text = "Downloading language model..."
                            cardResult.visibility = View.VISIBLE
                        }
                    },
                    onSuccess = { translated ->
                        runOnUiThread {
                            tvResult.text = translated
                            cardResult.visibility = View.VISIBLE
                            btnSave.isEnabled = true
                            setTranslating(false)
                        }
                    },
                    onError = { msg ->
                        runOnUiThread {
                            cardResult.visibility = View.GONE
                            Toast.makeText(this@TranslateActivity, msg, Toast.LENGTH_LONG).show()
                            setTranslating(false)
                        }
                    }
                )
            }
        }

        // Re-enable save whenever the user types new text
        etInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { btnSave.isEnabled = true }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Clear
        btnClear.setOnClickListener {
            etInput.text.clear()
            cardResult.visibility = View.GONE
        }

        // Save to history
        btnSave.setOnClickListener {
            val source = etInput.text.toString()
            val result = tvResult.text.toString()
            if (source.isNotEmpty() && result.isNotEmpty()) {
                lifecycleScope.launch {
                    val srcCode = languageManager.sourceLangCode.first()
                    val tgtCode = languageManager.targetLangCode.first()
                    saveToHistory(source, result, srcCode, tgtCode)
                    Toast.makeText(
                        this@TranslateActivity,
                        "Saved to History!",
                        Toast.LENGTH_SHORT
                    ).show()
                    btnSave.isEnabled = false
                }
            }
        }

        // Copy to clipboard
        btnCopy.setOnClickListener {
            val result = tvResult.text.toString()
            if (result.isNotEmpty()) {
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(ClipData.newPlainText("translation", result))
                Toast.makeText(this, "Copied to clipboard", Toast.LENGTH_SHORT).show()
            }
        }

        // Speaker – read translation aloud
        val btnSpeak = findViewById<View>(R.id.btnSpeak)
        btnSpeak.setOnClickListener {
            val result = tvResult.text.toString()
            if (result.isNotEmpty()) {
                if (SpeechEngine.isSpeaking) {
                    SpeechEngine.stop()
                } else {
                    SpeechEngine.speakTranslatedText(listOf(result))
                }
            }
        }

        // Bottom Navigation
        findViewById<View>(R.id.navHome).setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            overridePendingTransition(0, 0)
        }
        findViewById<View>(R.id.navTranslate).setOnClickListener { /* already here */ }
        findViewById<View>(R.id.navHistory).setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
            overridePendingTransition(0, 0)
        }
        findViewById<View>(R.id.navSettings).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
            overridePendingTransition(0, 0)
        }
    }

    private fun setTranslating(loading: Boolean) {
        progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        btnTranslate.isEnabled = !loading
    }

    private fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private suspend fun saveToHistory(source: String, result: String, srcCode: String, tgtCode: String) {
        AppDatabase.get(this).historyDao().insert(
            HistoryEntity(
                sourceText = source,
                resultText = result,
                sourceLangCode = srcCode,
                targetLangCode = tgtCode
            )
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        SpeechEngine.stop()
        engine.close()
    }
}
