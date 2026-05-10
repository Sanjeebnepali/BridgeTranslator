package com.bridge.translator.ui

import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class LanguageSelectorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Translate to"

        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(20), dp(20), dp(20))
        }

        LANGUAGES.forEach { option ->
            container.addView(TextView(this).apply {
                text = option.label
                textSize = 18f
                gravity = Gravity.CENTER_VERTICAL
                setPadding(dp(12), dp(16), dp(12), dp(16))
                setOnClickListener {
                    getSharedPreferences("bridge_prefs", MODE_PRIVATE)
                        .edit()
                        .putString("target_language", option.code)
                        .putBoolean("target_language_user_set", true)
                        .apply()
                    finish()
                }
            }, LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ))
        }

        setContentView(container)
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density + 0.5f).toInt()

    private data class LanguageOption(val label: String, val code: String)

    private companion object {
        val LANGUAGES = listOf(
            LanguageOption("English", "en"),
            LanguageOption("Korean", "ko"),
            LanguageOption("Japanese", "ja"),
            LanguageOption("Chinese", "zh"),
            LanguageOption("Spanish", "es"),
            LanguageOption("Arabic", "ar"),
            LanguageOption("Hindi", "hi"),
            LanguageOption("French", "fr")
        )
    }
}
