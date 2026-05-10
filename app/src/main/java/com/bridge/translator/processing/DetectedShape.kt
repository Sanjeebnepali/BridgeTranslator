package com.bridge.translator.processing

import android.graphics.RectF
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/** Geometric shape types that the ShapeDetector can classify. */
enum class ShapeType {
    RECTANGLE,
    CIRCLE,
    TRIANGLE,
    CYLINDER
}

/**
 * Represents a single geometric region detected in a camera frame.
 *
 * @param bounds          Bounding rectangle in the *original* (un-scaled) bitmap coordinate space.
 * @param shapeType       Classified shape (RECTANGLE / CIRCLE / TRIANGLE / CYLINDER).
 * @param rotationDegrees Clockwise rotation of the shape relative to upright, in degrees.
 * @param confidence      Detection confidence in [0, 1].
 */
@Parcelize
data class DetectedShape(
    val bounds: RectF,
    val shapeType: ShapeType,
    val rotationDegrees: Float = 0f,
    val confidence: Float = 1f
) : Parcelable
