package com.bridge.translator.service

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.view.View

class TranslationCanvasView(context: Context) : View(context) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        isSubpixelText = true
        typeface = Typeface.DEFAULT
    }

    private var blocks: List<TranslatedBlock> = emptyList()

    init {
        setWillNotDraw(false)
        setBackgroundColor(Color.TRANSPARENT)
    }

    fun setBlocks(blocks: List<TranslatedBlock>) {
        this.blocks = blocks
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        blocks.forEach { block ->
            drawBlock(canvas, block)
        }
    }

    private fun drawBlock(canvas: Canvas, block: TranslatedBlock) {
        val rect = block.originalRect

        paint.color = block.bgColor
        canvas.drawRect(rect, paint)

        paint.color = readableTextColor(block.bgColor)
        paint.textSize = fitTextSize(block.translatedText, rect)
        paint.typeface = Typeface.DEFAULT

        val fm = paint.fontMetrics
        val textHeight = fm.descent - fm.ascent
        val y = rect.top + (rect.height() - textHeight) / 2f - fm.ascent
        canvas.drawText(block.translatedText, rect.left + 4f, y, paint)
    }

    fun fitTextSize(
        text: String,
        rect: Rect,
        maxSize: Float = 14f,
        minSize: Float = 7f
    ): Float {
        val measurePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        var size = maxSize
        while (size > minSize) {
            measurePaint.textSize = size
            val measured = measurePaint.measureText(text)
            if (measured <= (rect.width() - 8).toFloat()) break
            size -= 0.5f
        }
        return size
    }

    fun readableTextColor(bgColor: Int): Int {
        val luminance = (0.299 * Color.red(bgColor) +
                0.587 * Color.green(bgColor) +
                0.114 * Color.blue(bgColor)) / 255
        return if (luminance > 0.5) Color.BLACK else Color.WHITE
    }
}
