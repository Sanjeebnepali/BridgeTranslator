package com.example.bridgetranslator

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View

class DocumentFrameView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val frame = RectF()
    private val framePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = dp(3f)
        color = Color.argb(210, 255, 255, 255)
    }
    private val cornerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = dp(5f)
        strokeCap = Paint.Cap.ROUND
        color = Color.argb(235, 34, 211, 238)
    }
    private val shadePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.argb(64, 0, 0, 0)
    }
    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        textSize = dp(14f)
        typeface = android.graphics.Typeface.DEFAULT_BOLD
    }
    private val scanPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeWidth = dp(4f)
    }

    private var label = "Place text inside frame"
    private var scanProgress = 0f
    private val scanAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
        duration = 1800L
        repeatCount = ValueAnimator.INFINITE
        repeatMode = ValueAnimator.RESTART
        addUpdateListener {
            scanProgress = it.animatedValue as Float
            invalidate()
        }
    }

    init {
        startScanAnimation()
    }

    fun setScanning() {
        label = "Place text inside frame"
        framePaint.color = Color.argb(180, 255, 255, 255)
        cornerPaint.color = Color.argb(235, 34, 211, 238)
        startScanAnimation()
        invalidate()
    }

    fun setDetecting() {
        label = "Text found - scanning"
        framePaint.color = Color.argb(210, 255, 255, 255)
        cornerPaint.color = Color.argb(245, 255, 204, 64)
        startScanAnimation()
        invalidate()
    }

    fun setLocked(locked: Boolean) {
        label = if (locked) "Image locked" else "Place text inside frame"
        framePaint.color = if (locked) Color.argb(230, 255, 255, 255) else Color.argb(180, 255, 255, 255)
        cornerPaint.color = if (locked) Color.argb(245, 76, 217, 100) else Color.argb(235, 34, 211, 238)
        if (locked) stopScanAnimation() else startScanAnimation()
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val horizontalMargin = width * 0.07f
        val top = height * 0.18f
        val bottom = height * 0.78f
        frame.set(horizontalMargin, top, width - horizontalMargin, bottom)

        canvas.drawRect(0f, 0f, width.toFloat(), frame.top, shadePaint)
        canvas.drawRect(0f, frame.bottom, width.toFloat(), height.toFloat(), shadePaint)
        canvas.drawRect(0f, frame.top, frame.left, frame.bottom, shadePaint)
        canvas.drawRect(frame.right, frame.top, width.toFloat(), frame.bottom, shadePaint)

        canvas.drawRoundRect(frame, dp(18f), dp(18f), framePaint)
        drawScanLine(canvas)
        drawCorners(canvas)
        canvas.drawText(label, width / 2f, frame.bottom + dp(34f), labelPaint)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startScanAnimation()
    }

    override fun onDetachedFromWindow() {
        stopScanAnimation()
        super.onDetachedFromWindow()
    }

    private fun drawScanLine(canvas: Canvas) {
        if (!scanAnimator.isStarted) return
        val y = frame.top + frame.height() * scanProgress
        scanPaint.shader = LinearGradient(
            frame.left,
            y,
            frame.right,
            y,
            intArrayOf(
                Color.TRANSPARENT,
                Color.argb(230, 42, 220, 255),
                Color.WHITE,
                Color.argb(230, 42, 220, 255),
                Color.TRANSPARENT
            ),
            floatArrayOf(0f, 0.28f, 0.5f, 0.72f, 1f),
            Shader.TileMode.CLAMP
        )
        canvas.drawLine(frame.left + dp(18f), y, frame.right - dp(18f), y, scanPaint)
        scanPaint.shader = null
    }

    private fun drawCorners(canvas: Canvas) {
        val len = dp(42f)
        val l = frame.left
        val t = frame.top
        val r = frame.right
        val b = frame.bottom

        canvas.drawLine(l, t + len, l, t, cornerPaint)
        canvas.drawLine(l, t, l + len, t, cornerPaint)
        canvas.drawLine(r - len, t, r, t, cornerPaint)
        canvas.drawLine(r, t, r, t + len, cornerPaint)
        canvas.drawLine(l, b - len, l, b, cornerPaint)
        canvas.drawLine(l, b, l + len, b, cornerPaint)
        canvas.drawLine(r - len, b, r, b, cornerPaint)
        canvas.drawLine(r, b, r, b - len, cornerPaint)
    }

    private fun startScanAnimation() {
        if (!scanAnimator.isStarted) scanAnimator.start()
    }

    private fun stopScanAnimation() {
        if (scanAnimator.isStarted) scanAnimator.cancel()
    }

    private fun dp(value: Float): Float = value * resources.displayMetrics.density
}
