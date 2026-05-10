package com.bridge.translator.service

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.view.MotionEvent
import android.view.View

class SelectionOverlayView(context: Context) : View(context) {

    interface Listener {
        fun onSelected(rect: Rect)
        fun onCancelled()
    }

    var listener: Listener? = null

    private var startX = 0f
    private var startY = 0f
    private val selRect = RectF()

    // 50% black scrim over everything outside the selection
    private val scrimPaint = Paint().apply {
        color = Color.argb(128, 0, 0, 0)
        style = Paint.Style.FILL
    }

    // White border around the selected rectangle while drawing
    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 3f
    }

    // White corner handles
    private val handlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.FILL
    }

    private val hintPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 44f
        textAlign = Paint.Align.CENTER
        setShadowLayer(8f, 0f, 2f, Color.BLACK)
    }

    private val subHintPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(210, 220, 220, 220)
        textSize = 30f
        textAlign = Paint.Align.CENTER
        setShadowLayer(6f, 0f, 1f, Color.BLACK)
    }

    override fun onDraw(canvas: Canvas) {
        if (selRect.isEmpty) {
            // Full-screen scrim with instructions
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), scrimPaint)
            val cx = width / 2f
            val cy = height / 2f
            canvas.drawText("Draw a box to translate", cx, cy - 32f, hintPaint)
            canvas.drawText("Minimum size: 40 x 40 dp", cx, cy + 18f, subHintPaint)
            canvas.drawText("Tap the bubble to cancel", cx, cy + 58f, subHintPaint)
            return
        }

        // Scrim on the four regions surrounding the selection
        canvas.drawRect(0f, 0f, width.toFloat(), selRect.top, scrimPaint)
        canvas.drawRect(0f, selRect.bottom, width.toFloat(), height.toFloat(), scrimPaint)
        canvas.drawRect(0f, selRect.top, selRect.left, selRect.bottom, scrimPaint)
        canvas.drawRect(selRect.right, selRect.top, width.toFloat(), selRect.bottom, scrimPaint)

        // White border
        canvas.drawRect(selRect, borderPaint)

        // Corner handles
        val r = 14f
        for ((x, y) in listOf(
            selRect.left  to selRect.top,
            selRect.right to selRect.top,
            selRect.left  to selRect.bottom,
            selRect.right to selRect.bottom
        )) canvas.drawCircle(x, y, r, handlePaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = event.x
                startY = event.y
                selRect.set(startX, startY, startX, startY)
                invalidate()
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                selRect.set(
                    minOf(startX, event.x), minOf(startY, event.y),
                    maxOf(startX, event.x), maxOf(startY, event.y)
                )
                invalidate()
                return true
            }
            MotionEvent.ACTION_UP -> {
                if (selRect.width() > MIN_SIZE && selRect.height() > MIN_SIZE) {
                    invalidate()
                    listener?.onSelected(selRect.toIntRect())
                } else {
                    // Too small - reset and let user try again
                    selRect.setEmpty()
                    invalidate()
                }
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun RectF.toIntRect() = Rect(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())

    private companion object {
        const val MIN_SIZE = 40f
    }
}
