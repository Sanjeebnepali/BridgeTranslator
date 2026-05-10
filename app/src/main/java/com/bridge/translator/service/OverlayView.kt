package com.bridge.translator.service

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.view.View

class OverlayView(context: Context) : View(context) {

    private val erasePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        isSubpixelText = true
    }

    private var items: List<OverlayTextItem> = emptyList()

    fun setItems(next: List<OverlayTextItem>) {
        items = next
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (item in items) {
            erasePaint.color = item.backgroundColor
            val r = item.eraseRect
            canvas.drawRect(RectF(r), erasePaint)

            textPaint.color = item.textColor
            textPaint.textSize = item.textSizePx
            TextEraseHelper.drawWrappedText(canvas, item.translatedText, r, textPaint)
        }
    }
}
