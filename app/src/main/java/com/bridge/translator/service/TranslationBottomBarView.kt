package com.bridge.translator.service

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView

class TranslationBottomBarView(
    context: Context,
    private val onSourceClick: () -> Unit,
    private val onTargetClick: () -> Unit,
    private val onTranslateClick: () -> Unit,
    private val onCloseClick: () -> Unit
) : LinearLayout(context) {

    private val sourceButton = pillButton()
    private val targetButton = pillButton()

    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        elevation = dp(14).toFloat()
        setPadding(dp(12), dp(10), dp(10), dp(10))
        background = GradientDrawable().apply {
            setColor(Color.WHITE)
            cornerRadius = dp(28).toFloat()
        }

        sourceButton.setOnClickListener { onSourceClick() }
        targetButton.setOnClickListener { onTargetClick() }

        addView(sourceButton, LayoutParams(0, dp(52), 1f))
        addView(TextView(context).apply {
            text = "→"
            textSize = 28f
            setTextColor(Color.parseColor("#1F2937"))
            gravity = Gravity.CENTER
        }, LayoutParams(dp(48), dp(52)))
        addView(targetButton, LayoutParams(0, dp(52), 1f))
        addView(circleButton("🌐", Color.parseColor("#0B63CE")).apply {
            setOnClickListener { onTranslateClick() }
        }, LayoutParams(dp(56), dp(56)).also { it.marginStart = dp(10) })
        addView(circleButton("×", Color.parseColor("#111827")).apply {
            textSize = 26f
            setOnClickListener { onCloseClick() }
        }, LayoutParams(dp(44), dp(44)).also { it.marginStart = dp(8) })
    }

    fun setLanguages(sourceLabel: String, targetLabel: String) {
        sourceButton.text = sourceLabel
        targetButton.text = targetLabel
    }

    private fun pillButton() = TextView(context).apply {
        gravity = Gravity.CENTER
        textSize = 16f
        typeface = Typeface.DEFAULT_BOLD
        setTextColor(Color.parseColor("#111827"))
        maxLines = 1
        setPadding(dp(12), 0, dp(12), 0)
        background = GradientDrawable().apply {
            setColor(Color.parseColor("#E8EEFD"))
            cornerRadius = dp(26).toFloat()
        }
    }

    private fun circleButton(label: String, color: Int) = TextView(context).apply {
        text = label
        textSize = 22f
        typeface = Typeface.DEFAULT_BOLD
        gravity = Gravity.CENTER
        setTextColor(Color.WHITE)
        background = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(color)
        }
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density + 0.5f).toInt()
}
