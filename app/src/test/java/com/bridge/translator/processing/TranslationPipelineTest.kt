package com.bridge.translator.processing

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.RectF
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class TranslationPipelineTest {

    private lateinit var pipeline: TranslationPipeline

    @Before
    fun setUp() {
        pipeline = TranslationPipeline()
    }

    @After
    fun tearDown() {
        pipeline.close()
    }

    // ── process() with empty shapes ───────────────────────────────────────────

    @Test
    fun `process with empty shapes list returns original frame and empty results`() = runTest {
        val frame   = createSolidBitmap(400, 400, Color.WHITE)
        val (result, shapes) = pipeline.process(
            frame        = frame,
            shapes       = emptyList(),
            targetLang   = "en",
            fallbackLang = "ko"
        )
        assertNotNull("Result bitmap should not be null", result)
        assertTrue("Shape results should be empty", shapes.isEmpty())
        // Original frame returned unchanged
        assertEquals("Width unchanged",  400, result.width)
        assertEquals("Height unchanged", 400, result.height)
        frame.recycle()
    }

    @Test
    fun `process does not throw for single blank-bitmap shape`() = runTest {
        val frame = createSolidBitmap(400, 400, Color.WHITE)
        val shapes = listOf(
            DetectedShape(
                bounds          = RectF(50f, 50f, 350f, 350f),
                shapeType       = ShapeType.RECTANGLE,
                rotationDegrees = 0f,
                confidence      = 0.9f
            )
        )
        val (result, _) = pipeline.process(
            frame        = frame,
            shapes       = shapes,
            targetLang   = "en",
            fallbackLang = "ko"
        )
        assertNotNull("Result bitmap should not be null", result)
        frame.recycle()
    }

    @Test
    fun `process with out-of-bounds shape bounds does not crash`() = runTest {
        val frame = createSolidBitmap(200, 200, Color.LTGRAY)
        val shapes = listOf(
            DetectedShape(
                bounds          = RectF(-10f, -10f, 500f, 500f), // way outside
                shapeType       = ShapeType.RECTANGLE,
                rotationDegrees = 0f,
                confidence      = 0.5f
            )
        )
        val (result, _) = pipeline.process(
            frame        = frame,
            shapes       = shapes,
            targetLang   = "en",
            fallbackLang = "ko"
        )
        assertNotNull(result)
        frame.recycle()
    }

    @Test
    fun `process concurrent shapes does not produce null result`() = runTest {
        val frame = createSolidBitmap(600, 400, Color.WHITE)
        val shapes = (0 until 4).map { i ->
            DetectedShape(
                bounds    = RectF(i * 100f + 10f, 50f, i * 100f + 90f, 350f),
                shapeType = ShapeType.RECTANGLE,
                rotationDegrees = 0f,
                confidence      = 0.8f
            )
        }
        val (result, _) = pipeline.process(
            frame        = frame,
            shapes       = shapes,
            targetLang   = "en",
            fallbackLang = "ko"
        )
        assertNotNull("Result bitmap must not be null for concurrent shapes", result)
        frame.recycle()
    }

    // ── ImagePreprocessor integration ─────────────────────────────────────────

    @Test
    fun `cropAndRotate returns correct dimensions for zero rotation`() {
        val src    = createSolidBitmap(400, 400, Color.RED)
        val bounds = RectF(50f, 50f, 250f, 200f)
        val crop   = ImagePreprocessor.cropAndRotate(src, bounds, 0f)

        assertEquals("Crop width",  200, crop.width)
        assertEquals("Crop height", 150, crop.height)
        src.recycle()
        crop.recycle()
    }

    @Test
    fun `cropAndRotate with fully-out-of-bounds rect returns 1x1 placeholder`() {
        val src    = createSolidBitmap(100, 100, Color.BLUE)
        val bounds = RectF(200f, 200f, 400f, 400f)   // completely outside
        val crop   = ImagePreprocessor.cropAndRotate(src, bounds, 0f)
        // Should not crash, returns degenerate bitmap
        assertTrue("Width >= 1",  crop.width  >= 1)
        assertTrue("Height >= 1", crop.height >= 1)
        src.recycle()
        crop.recycle()
    }

    @Test
    fun `scaleForOcr does not upscale small bitmaps`() {
        val small = createSolidBitmap(100, 80, Color.GREEN)
        val result = ImagePreprocessor.scaleForOcr(small, maxEdge = 720)
        assertEquals("Width should stay 100",  100, result.width)
        assertEquals("Height should stay 80",   80, result.height)
        small.recycle()
    }

    @Test
    fun `scaleForOcr downscales large bitmaps`() {
        val large  = createSolidBitmap(2000, 1500, Color.CYAN)
        val result = ImagePreprocessor.scaleForOcr(large, maxEdge = 720)
        assertTrue("Longest edge <= 720",
            maxOf(result.width, result.height) <= 720)
        large.recycle()
        if (result !== large) result.recycle()
    }

    @Test
    fun `applyShapeMask rectangle returns same-size bitmap`() {
        val src    = createSolidBitmap(200, 150, Color.YELLOW)
        val masked = ImagePreprocessor.applyShapeMask(src, ShapeType.RECTANGLE)
        assertEquals(200, masked.width)
        assertEquals(150, masked.height)
        src.recycle()
        masked.recycle()
    }

    @Test
    fun `applyShapeMask circle does not throw`() {
        val src    = createSolidBitmap(200, 200, Color.MAGENTA)
        val masked = ImagePreprocessor.applyShapeMask(src, ShapeType.CIRCLE)
        assertNotNull(masked)
        src.recycle()
        masked.recycle()
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    private fun createSolidBitmap(w: Int, h: Int, color: Int): Bitmap =
        Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888).also { it.eraseColor(color) }
}
