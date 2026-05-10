package com.bridge.translator.service

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import com.bridge.translator.engine.TranslationEngine

/**
 * Full-screen overlay for choosing the floating bubble translation target.
 */
class LanguagePickerOverlayView(
    context: Context,
    private val onPicked: (String) -> Unit,
    private val title: String = "Translate to"
) : FrameLayout(context) {

    private var pendingTarget: String? = null
    private var pickerButtons: List<Pair<TranslationEngine.LangOption, TextView>> = emptyList()

    init {
        setBackgroundColor(Color.argb(170, 0, 0, 0))
        buildUi()
    }

    // Consume all touches - user must interact with the picker panel
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent) = true

    private fun buildUi() {
        val content = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.argb(252, 18, 18, 38))
            setPadding(dp(20), dp(18), dp(20), dp(28))
        }

        content.addView(TextView(context).apply {
            text = title
            setTextColor(Color.WHITE)
            textSize = 15f
            typeface = Typeface.DEFAULT_BOLD
            layoutParams = LinearLayout.LayoutParams(MATCH, WRAP).also { it.bottomMargin = dp(14) }
        })

        val buttons = mutableListOf<Pair<TranslationEngine.LangOption, TextView>>()
        TranslationEngine.LANGUAGES.chunked(3).forEach { rowLangs ->
            val row = LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                weightSum = 3f
            }
            rowLangs.forEach { lang ->
                val btn = TextView(context).apply {
                    text = lang.label
                    textSize = 13f
                    gravity = Gravity.CENTER
                    setTextColor(Color.argb(200, 200, 200, 200))
                    background = GradientDrawable().apply {
                        setColor(Color.argb(255, 50, 50, 80))
                        cornerRadius = dp(6).toFloat()
                    }
                    setPadding(dp(6), dp(10), dp(6), dp(10))
                    setOnClickListener { selectLang(lang) }
                }
                row.addView(btn, LinearLayout.LayoutParams(0, WRAP, 1f).also { it.marginEnd = dp(6) })
                buttons.add(lang to btn)
            }
            repeat(3 - rowLangs.size) {
                row.addView(View(context), LinearLayout.LayoutParams(0, 1, 1f))
            }
            content.addView(row, LinearLayout.LayoutParams(MATCH, WRAP).also { it.bottomMargin = dp(6) })
        }
        pickerButtons = buttons

        content.addView(View(context).also { it.layoutParams = LinearLayout.LayoutParams(MATCH, dp(14)) })

        content.addView(TextView(context).apply {
            text = "Set Language & Translate"
            setTextColor(Color.WHITE)
            textSize = 15f
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            setBackgroundColor(Color.parseColor("#E94560"))
            setPadding(0, dp(14), 0, dp(14))
            setOnClickListener { confirmLang() }
        }, LinearLayout.LayoutParams(MATCH, WRAP))

        val scroll = ScrollView(context).apply {
            isVerticalScrollBarEnabled = false
            addView(content, ViewGroup.LayoutParams(MATCH, WRAP))
        }
        addView(scroll, LayoutParams(MATCH, WRAP, Gravity.BOTTOM))
    }

    private fun selectLang(lang: TranslationEngine.LangOption) {
        pickerButtons.forEach { (l, btn) ->
            val sel = l == lang
            (btn.background as? GradientDrawable)?.setColor(
                if (sel) Color.parseColor("#E94560") else Color.argb(255, 50, 50, 80)
            )
            btn.setTextColor(if (sel) Color.WHITE else Color.argb(200, 200, 200, 200))
        }
        pendingTarget = lang.code
    }

    private fun confirmLang() {
        val target = pendingTarget
            ?: return Toast.makeText(context, "Select a language first", Toast.LENGTH_SHORT).show()
        onPicked(target)
    }

    private fun dp(v: Int) = (v * resources.displayMetrics.density + 0.5f).toInt()

    companion object {
        private val MATCH = ViewGroup.LayoutParams.MATCH_PARENT
        private val WRAP  = ViewGroup.LayoutParams.WRAP_CONTENT
    }
}
