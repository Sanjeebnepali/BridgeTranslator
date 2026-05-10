package com.example.bridgetranslator

import android.graphics.Point
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class OrientationDetectorTest {

    private val detector = OrientationDetector()

    @Test
    fun `test horizontal orientation`() {
        val points = arrayOf(
            Point(0, 0),
            Point(100, 0),
            Point(100, 20),
            Point(0, 20)
        )
        val result = detector.detectOrientation(points, "Some text here")
        assertEquals(TextOrientation.HORIZONTAL, result.orientation)
        assertEquals(0f, result.angle, 0.1f)
    }

    @Test
    fun `test vertical orientation 90 degrees`() {
        val points = arrayOf(
            Point(0, 0),
            Point(0, 100),
            Point(-20, 100),
            Point(-20, 0)
        )
        val result = detector.detectOrientation(points, "Vertical")
        assertEquals(TextOrientation.VERTICAL, result.orientation)
        assertEquals(90f, result.angle, 0.1f)
    }

    @Test
    fun `test vertical orientation 270 degrees`() {
        val points = arrayOf(
            Point(0, 100),
            Point(0, 0),
            Point(20, 0),
            Point(20, 100)
        )
        val result = detector.detectOrientation(points, "Vertical")
        assertEquals(TextOrientation.VERTICAL, result.orientation)
        assertEquals(270f, result.angle, 0.1f)
    }

    @Test
    fun `test rotated orientation 45 degrees`() {
        val points = arrayOf(
            Point(0, 0),
            Point(100, 100),
            Point(80, 120),
            Point(-20, 20)
        )
        val result = detector.detectOrientation(points, "Rotated")
        assertEquals(TextOrientation.ROTATED, result.orientation)
        assertEquals(45f, result.angle, 0.1f)
    }
}
