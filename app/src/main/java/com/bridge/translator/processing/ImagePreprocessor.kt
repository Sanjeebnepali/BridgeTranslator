package com.bridge.translator.processing

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Stateless helper for cropping, rotating, masking, and re-scaling bitmaps
 * in preparation for OCR or for compositing translated regions back into a frame.
 */
object ImagePreprocessor {

    private const val MAX_OCR_EDGE = 720

    // ── Cropping ───────────────────────────────────────────────────────────────

    /**
     * Crop [source] to [bounds] and apply optional [rotationDegrees] around the
     * crop centre.  Returns a new, mutable ARGB_8888 bitmap.
     *
     * @param source          Original bitmap.
     * @param bounds          Region in [source] coordinate space.
     * @param rotationDegrees Clockwise rotation to apply after cropping (0 = none).
     * @return                Cropped (and rotated) bitmap.
     */
    fun cropAndRotate(source: Bitmap, bounds: RectF, rotationDegrees: Float = 0f): Bitmap {
        val clamped = clampRect(bounds, source.width, source.height)
        if (clamped.width() <= 0 || clamped.height() <= 0) {
            return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        }

        // Crop
        val cropped = Bitmap.createBitmap(
            source,
            clamped.left.roundToInt(),
            clamped.top.roundToInt(),
            clamped.width().roundToInt().coerceAtLeast(1),
            clamped.height().roundToInt().coerceAtLeast(1)
        )

        if (rotationDegrees == 0f) return cropped

        // Rotate around centre
        val matrix = Matrix().apply {
            postRotate(rotationDegrees, cropped.width / 2f, cropped.height / 2f)
        }
        val rotated = Bitmap.createBitmap(cropped, 0, 0, cropped.width, cropped.height, matrix, true)
        if (rotated !== cropped) cropped.recycle()
        return rotated
    }

    /**
     * Down-scale [bitmap] so that its longest edge is at most [maxEdge] pixels.
     * Returns the original if it already fits.
     */
    fun scaleForOcr(bitmap: Bitmap, maxEdge: Int = MAX_OCR_EDGE): Bitmap {
        val longest = max(bitmap.width, bitmap.height)
        if (longest <= maxEdge) return bitmap
        val scale = maxEdge.toFloat() / longest
        val nw = (bitmap.width  * scale).roundToInt().coerceAtLeast(1)
        val nh = (bitmap.height * scale).roundToInt().coerceAtLeast(1)
        return Bitmap.createScaledBitmap(bitmap, nw, nh, true)
    }

    // ── Shape masking (clip to shape before OCR / rendering) ──────────────────

    /**
     * Apply a shape-mask to [bitmap] so that pixels outside the shape are
     * transparent.  Useful for feeding a precise region to BitmapTextEraser.
     *
     * @param bitmap     Source bitmap (will not be modified).
     * @param shapeType  The type of mask to apply.
     * @return           New ARGB_8888 bitmap with transparent outside.
     */
    fun applyShapeMask(bitmap: Bitmap, shapeType: ShapeType): Bitmap {
        val w = bitmap.width.toFloat()
        val h = bitmap.height.toFloat()
        val bounds = RectF(0f, 0f, w, h)

        val masked = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(masked)

        // Draw shape clip path
        val path = shapeClipPath(shapeType, bounds)
        canvas.clipPath(path)
        canvas.drawBitmap(bitmap, 0f, 0f, null)
        return masked
    }

    /**
     * Composite [shapeBitmap] (a translated+erased crop) back into [target] at [bounds].
     *
     * This mutates [target].
     *
     * @param target       Full-frame bitmap to composite into.
     * @param shapeBitmap  Processed crop that should be pasted back.
     * @param bounds       Where in [target] to paste.
     * @param shapeType    Clip path applied before pasting.
     */
    fun compositeShapeBack(
        target: Bitmap,
        shapeBitmap: Bitmap,
        bounds: RectF,
        shapeType: ShapeType
    ) {
        val clamped = clampRect(bounds, target.width, target.height)
        if (clamped.width() <= 0 || clamped.height() <= 0) return

        // Scale shapeBitmap to match the clamped size
        val dstW = clamped.width().roundToInt().coerceAtLeast(1)
        val dstH = clamped.height().roundToInt().coerceAtLeast(1)
        val scaled = if (shapeBitmap.width != dstW || shapeBitmap.height != dstH)
            Bitmap.createScaledBitmap(shapeBitmap, dstW, dstH, true)
        else shapeBitmap

        val canvas = Canvas(target)
        canvas.save()
        canvas.translate(clamped.left, clamped.top)

        val path = shapeClipPath(shapeType, RectF(0f, 0f, dstW.toFloat(), dstH.toFloat()))
        canvas.clipPath(path)
        canvas.drawBitmap(scaled, 0f, 0f, null)
        canvas.restore()

        if (scaled !== shapeBitmap) scaled.recycle()
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    private fun clampRect(r: RectF, w: Int, h: Int): RectF = RectF(
        r.left.coerceIn(0f, w.toFloat()),
        r.top.coerceIn(0f, h.toFloat()),
        r.right.coerceIn(0f, w.toFloat()),
        r.bottom.coerceIn(0f, h.toFloat())
    )

    private fun shapeClipPath(type: ShapeType, r: RectF): Path = when (type) {
        ShapeType.RECTANGLE -> Path().apply { addRoundRect(r, 12f, 12f, Path.Direction.CW) }
        ShapeType.CIRCLE    -> Path().apply {
            val cx = r.centerX(); val cy = r.centerY()
            val radius = min(r.width(), r.height()) / 2f
            addCircle(cx, cy, radius, Path.Direction.CW)
        }
        ShapeType.TRIANGLE  -> Path().apply {
            moveTo(r.centerX(), r.top)
            lineTo(r.right, r.bottom)
            lineTo(r.left,  r.bottom)
            close()
        }
        ShapeType.CYLINDER  -> {
            val capH = (r.height() * 0.2f).coerceAtLeast(8f)
            val bodyRect = RectF(r.left, r.top + capH / 2f, r.right, r.bottom - capH / 2f)
            Path().apply {
                addRect(bodyRect, Path.Direction.CW)
                addOval(RectF(r.left, r.top, r.right, r.top + capH), Path.Direction.CW)
                addOval(RectF(r.left, r.bottom - capH, r.right, r.bottom), Path.Direction.CW)
            }
        }
    }
}
