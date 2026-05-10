package com.example.bridgetranslator

import android.content.Context
import android.graphics.*
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View

class SelectionOverlayView(context: Context) : View(context) {

    interface SelectionListener {
        fun onSelectionComplete(rect: Rect)
        fun onCancelled()
    }

    var listener: SelectionListener? = null

    private var startX = 0f
    private var startY = 0f
    private var selRect = RectF()
    private var isDrawing = false

    private val dimPaint = Paint().apply {
        color = Color.argb(120, 0, 0, 0)
        style = Paint.Style.FILL
    }
    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#E94560")
        style = Paint.Style.STROKE
        strokeWidth = 3f
    }
    private val handlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#E94560")
        style = Paint.Style.FILL
    }
    private val instructionPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 38f
        textAlign = Paint.Align.CENTER
        setShadowLayer(6f, 0f, 2f, Color.BLACK)
    }
    private val subInstructionPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(200, 200, 200, 200)
        textSize = 28f
        textAlign = Paint.Align.CENTER
        setShadowLayer(4f, 0f, 1f, Color.BLACK)
    }

    private val gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
        override fun onDoubleTap(e: MotionEvent): Boolean {
            selRect.set(0f, 0f, width.toFloat(), height.toFloat())
            isDrawing = false
            invalidate()
            listener?.onSelectionComplete(selRect.toIntRect())
            return true
        }
    })

    override fun onDraw(canvas: Canvas) {
        if (selRect.isEmpty && !isDrawing) {
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), dimPaint)
            val cy = height / 2f
            canvas.drawText("Draw a box around text to translate", width / 2f, cy - 30f, instructionPaint)
            canvas.drawText("or double-tap to capture the full screen", width / 2f, cy + 16f, subInstructionPaint)
            canvas.drawText("Tap the bubble again to cancel", width / 2f, cy + 56f, subInstructionPaint)
            return
        }

        // Dim the 4 regions outside the selection, leaving the rect transparent.
        canvas.drawRect(0f, 0f, width.toFloat(), selRect.top, dimPaint)
        canvas.drawRect(0f, selRect.bottom, width.toFloat(), height.toFloat(), dimPaint)
        canvas.drawRect(0f, selRect.top, selRect.left, selRect.bottom, dimPaint)
        canvas.drawRect(selRect.right, selRect.top, width.toFloat(), selRect.bottom, dimPaint)

        canvas.drawRect(selRect, borderPaint)

        // Corner handles
        listOf(
            selRect.left to selRect.top,
            selRect.right to selRect.top,
            selRect.left to selRect.bottom,
            selRect.right to selRect.bottom
        ).forEach { (x, y) -> canvas.drawCircle(x, y, 18f, handlePaint) }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(event)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = event.x
                startY = event.y
                selRect.set(startX, startY, startX, startY)
                isDrawing = true
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
                isDrawing = false
                if (selRect.width() > 50 && selRect.height() > 50) {
                    invalidate()
                    listener?.onSelectionComplete(selRect.toIntRect())
                } else {
                    selRect.setEmpty()
                    invalidate()
                }
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    fun getSelectedRect(): Rect = selRect.toIntRect()

    private fun RectF.toIntRect() = Rect(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
}
