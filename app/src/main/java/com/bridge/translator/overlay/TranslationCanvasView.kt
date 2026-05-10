package com.bridge.translator.overlay

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import com.bridge.translator.analysis.Alignment

class TranslationCanvasView(context: Context) : View(context) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        isSubpixelText = true
    }

    private var bitmap: Bitmap? = null
    private var blocks: List<TranslatedBlock> = emptyList()

    init {
        setWillNotDraw(false)
        setBackgroundColor(Color.TRANSPARENT)
    }

    fun setBitmap(bitmap: Bitmap?) {
        if (this.bitmap !== bitmap) {
            this.bitmap?.recycle()
        }
        this.bitmap = bitmap
    }

    fun setBlocks(blocks: List<TranslatedBlock>) {
        this.blocks = blocks
        invalidate()
    }

    fun clearBlocks() {
        blocks = emptyList()
        bitmap?.recycle()
        bitmap = null
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        blocks.forEach { block -> drawBlock(canvas, block) }
    }

    private fun drawBlock(canvas: Canvas, block: TranslatedBlock) {
        paint.color = block.bgColor
        canvas.drawRect(block.originalRect, paint)

        paint.color = block.textColor
        paint.textSize = fitTextSize(block.translatedText, block.originalRect, block.fontSize)
        paint.textAlign = when (block.alignment) {
            Alignment.CENTER -> Paint.Align.CENTER
            Alignment.RIGHT -> Paint.Align.RIGHT
            Alignment.LEFT -> Paint.Align.LEFT
        }

        val x = when (block.alignment) {
            Alignment.CENTER -> block.originalRect.centerX().toFloat()
            Alignment.RIGHT -> block.originalRect.right.toFloat() - 4f
            Alignment.LEFT -> block.originalRect.left.toFloat() + 4f
        }
        val fm = paint.fontMetrics
        val textH = fm.descent - fm.ascent
        val y = block.originalRect.top + (block.originalRect.height() - textH) / 2f - fm.ascent
        canvas.drawText(block.translatedText, x, y, paint)
    }

    fun fitTextSize(text: String, rect: Rect, startSize: Float): Float {
        val p = Paint(Paint.ANTI_ALIAS_FLAG)
        var size = startSize.coerceAtMost(22f).coerceAtLeast(7f)
        while (size > 7f) {
            p.textSize = size
            if (p.measureText(text) <= rect.width() - 8f) break
            size -= 0.5f
        }
        return size
    }
}
