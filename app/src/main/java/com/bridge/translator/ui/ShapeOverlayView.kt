package com.bridge.translator.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.util.AttributeSet
import android.view.ScaleGestureDetector
import android.view.View
import com.bridge.translator.processing.DetectedShape
import com.bridge.translator.processing.ShapeType

/**
 * Transparent overlay that draws semi-transparent masks for each [DetectedShape].
 *
 * Features:
 * - Draws a shape-appropriate border/mask for RECTANGLE, CIRCLE, TRIANGLE, CYLINDER.
 * - Highlights the shape currently being processed with a distinct border colour.
 * - Supports pinch-to-zoom (scales the overlay coordinate space to match).
 * - Call [setShapes] on the UI thread whenever the detector produces new results.
 */
class ShapeOverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    // ── State ──────────────────────────────────────────────────────────────────

    private var shapes: List<DetectedShape> = emptyList()
    private var processingIndex: Int = -1   // index in [shapes] currently being translated
    private var sourceWidth  = 1f           // width  of the bitmap this overlay is referencing
    private var sourceHeight = 1f           // height of the bitmap this overlay is referencing

    // Zoom support
    private var zoomScale    = 1f
    private var zoomOffsetX  = 0f
    private var zoomOffsetY  = 0f

    // ── Paints ─────────────────────────────────────────────────────────────────

    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#33448AFF")   // semi-transparent blue
    }
    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 3f
        color = Color.parseColor("#FF448AFF")   // solid blue border
    }
    private val highlightBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 5f
        color = Color.parseColor("#FF00E5FF")   // cyan for active processing
    }
    private val highlightFillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#4400E5FF")   // semi-transparent cyan
    }

    // ── Pinch-to-zoom ──────────────────────────────────────────────────────────

    private val scaleDetector = ScaleGestureDetector(context,
        object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                zoomScale = (zoomScale * detector.scaleFactor).coerceIn(0.5f, 5f)
                invalidate()
                return true
            }
        }
    )

    // ── Public API ─────────────────────────────────────────────────────────────

    /**
     * Supply the current list of detected shapes.
     *
     * @param shapes       Shapes in *source-bitmap* coordinate space.
     * @param sourceW      Width  of the source bitmap (used for coordinate mapping).
     * @param sourceH      Height of the source bitmap.
     */
    fun setShapes(shapes: List<DetectedShape>, sourceW: Int, sourceH: Int) {
        this.shapes       = shapes
        this.sourceWidth  = sourceW.toFloat().coerceAtLeast(1f)
        this.sourceHeight = sourceH.toFloat().coerceAtLeast(1f)
        invalidate()
    }

    fun clearShapes() {
        shapes = emptyList()
        processingIndex = -1
        invalidate()
    }

    /** Highlight one shape to indicate it is currently being translated. */
    fun highlightShape(index: Int) {
        processingIndex = index
        invalidate()
    }

    fun clearHighlight() {
        processingIndex = -1
        invalidate()
    }

    // ── Drawing ────────────────────────────────────────────────────────────────

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (shapes.isEmpty()) return

        // Build mapping from source-bitmap space → view space
        val viewW = width.toFloat()
        val viewH = height.toFloat()
        val scaleX = viewW / sourceWidth  * zoomScale
        val scaleY = viewH / sourceHeight * zoomScale

        canvas.save()
        canvas.translate(zoomOffsetX, zoomOffsetY)

        shapes.forEachIndexed { idx, shape ->
            val isProcessing = idx == processingIndex
            val fill   = if (isProcessing) highlightFillPaint   else fillPaint
            val border = if (isProcessing) highlightBorderPaint else borderPaint

            val b = shape.bounds
            val rect = RectF(
                b.left   * scaleX,
                b.top    * scaleY,
                b.right  * scaleX,
                b.bottom * scaleY
            )

            drawShape(canvas, shape.shapeType, rect, fill, border, shape.rotationDegrees)
        }

        canvas.restore()
    }

    private fun drawShape(
        canvas: Canvas,
        type: ShapeType,
        rect: RectF,
        fill: Paint,
        border: Paint,
        rotation: Float
    ) {
        canvas.save()
        canvas.rotate(rotation, rect.centerX(), rect.centerY())

        when (type) {
            ShapeType.RECTANGLE -> {
                canvas.drawRoundRect(rect, 12f, 12f, fill)
                canvas.drawRoundRect(rect, 12f, 12f, border)
            }
            ShapeType.CIRCLE -> {
                val cx = rect.centerX()
                val cy = rect.centerY()
                val r  = minOf(rect.width(), rect.height()) / 2f
                canvas.drawCircle(cx, cy, r, fill)
                canvas.drawCircle(cx, cy, r, border)
            }
            ShapeType.TRIANGLE -> {
                val path = trianglePath(rect)
                canvas.drawPath(path, fill)
                canvas.drawPath(path, border)
            }
            ShapeType.CYLINDER -> {
                val path = cylinderPath(rect)
                canvas.drawPath(path, fill)
                canvas.drawPath(path, border)
            }
        }

        canvas.restore()
    }

    /** Isosceles triangle pointing upward. */
    private fun trianglePath(r: RectF): Path = Path().apply {
        moveTo(r.centerX(), r.top)
        lineTo(r.right, r.bottom)
        lineTo(r.left,  r.bottom)
        close()
    }

    /**
     * Cylinder outline: rectangle body with elliptical caps.
     * The ellipse height is 20 % of the rectangle height.
     */
    private fun cylinderPath(r: RectF): Path {
        val capH = (r.height() * 0.2f).coerceAtLeast(8f)
        val topEllipse    = RectF(r.left, r.top,          r.right, r.top    + capH)
        val bottomEllipse = RectF(r.left, r.bottom - capH, r.right, r.bottom)
        val bodyRect      = RectF(r.left, r.top + capH / 2, r.right, r.bottom - capH / 2)

        return Path().apply {
            // Body rectangle
            addRect(bodyRect, Path.Direction.CW)
            // Top ellipse
            addOval(topEllipse, Path.Direction.CW)
            // Bottom ellipse
            addOval(bottomEllipse, Path.Direction.CW)
        }
    }

    // ── Touch ──────────────────────────────────────────────────────────────────

    override fun onTouchEvent(event: android.view.MotionEvent): Boolean {
        scaleDetector.onTouchEvent(event)
        return true
    }
}
