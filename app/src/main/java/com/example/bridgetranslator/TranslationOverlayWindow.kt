package com.example.bridgetranslator

import android.content.Context
import android.graphics.*
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.WindowManager

class TranslationOverlayWindow(private val context: Context) {

    private val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private var canvas: TranslationCanvas? = null

    fun showTranslations(translations: List<TranslatedBlock>) {
        if (canvas == null) attachOverlay()
        canvas?.update(translations)
    }

    fun clearOverlays() {
        canvas?.let { safeRemove(it) }
        canvas = null
    }

    private fun attachOverlay() {
        val m = context.resources.displayMetrics
        val view = TranslationCanvas(context)
        val lp = WindowManager.LayoutParams(
            m.widthPixels, m.heightPixels,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                @Suppress("DEPRECATION") WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            android.graphics.PixelFormat.TRANSLUCENT
        ).also { it.gravity = Gravity.TOP or Gravity.START }
        wm.addView(view, lp)
        canvas = view
    }

    private fun safeRemove(v: View) = try { wm.removeView(v) } catch (_: Exception) {}
}

private class TranslationCanvas(context: Context) : View(context) {

    private var translations: List<TranslatedBlock> = emptyList()

    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(200, 26, 26, 46) // #1A1A2E at 78 % opacity
    }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textAlign = Paint.Align.LEFT
    }
    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#E94560")
        style = Paint.Style.STROKE
        strokeWidth = 1.5f
    }

    fun update(blocks: List<TranslatedBlock>) {
        translations = blocks
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        translations.forEach { block ->
            if (block.translatedText.isBlank()) return@forEach
            val b = block.original.bounds
            val blockW = (b.right - b.left).toFloat().coerceAtLeast(80f)
            val blockH = (b.bottom - b.top).toFloat().coerceAtLeast(14f)

            textPaint.textSize = fittedSize(block.translatedText, blockW, blockH)
            val ascent = -textPaint.ascent()
            val descent = textPaint.descent()
            val padH = 8f; val padV = 5f
            val pillW = minOf(textPaint.measureText(block.translatedText) + padH * 2, blockW + padH * 2)
            val pillH = ascent + descent + padV * 2

            val pill = RectF(
                b.left.toFloat(), b.top.toFloat(),
                b.left + pillW, b.top + pillH
            )
            canvas.drawRoundRect(pill, 10f, 10f, bgPaint)
            canvas.drawRoundRect(pill, 10f, 10f, strokePaint)
            canvas.drawText(
                block.translatedText,
                pill.left + padH,
                pill.top + padV + ascent,
                textPaint
            )
        }
    }

    private fun fittedSize(text: String, maxW: Float, maxH: Float): Float {
        var size = maxH.coerceIn(10f, 42f)
        textPaint.textSize = size
        while (textPaint.measureText(text) > maxW && size > 8f) {
            size -= 1f
            textPaint.textSize = size
        }
        return size
    }
}
