package com.bridge.translator.processing

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.RectF
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

/**
 * On-device geometric shape detector.
 *
 * Primary path   : TensorFlow Lite model loaded from `assets/shape_detection.tflite`
 *                  (GPU delegate used when available).
 * Fallback path  : Pure-Android bitmap analysis using grayscale conversion,
 *                  Sobel edge detection, connected-component labelling, and
 *                  geometric classification heuristics.
 *
 * The fallback is transparent — callers see the same `List<DetectedShape>` regardless
 * of which path is active.
 *
 * Usage:
 * ```kotlin
 * ShapeDetector.init(context)           // call once from Application.onCreate
 * val shapes = ShapeDetector.detect(bitmap)
 * ```
 */
object ShapeDetector {

    private const val TAG = "ShapeDetector"
    private const val MODEL_ASSET = "shape_detection.tflite"
    private const val MAX_INPUT_EDGE = 320
    private const val MIN_REGION_AREA_RATIO = 0.005f   // 0.5 % of frame area
    private const val MAX_REGIONS = 8
    private const val EDGE_THRESHOLD = 40              // 0-255 Sobel magnitude cutoff
    private const val CIRCULARITY_THRESHOLD = 0.72f    // π·r² / boundingBox area
    private const val TRIANGLE_FILL_THRESHOLD = 0.55f  // fill below this → triangle
    private const val CYLINDER_ASPECT_MIN = 1.6f       // height / width ≥ this → cylinder candidate

    // Optional TFLite interpreter (null when model is absent / GPU init failed)
    @Volatile private var tfliteInterpreter: Any? = null  // typed as Any to avoid hard dep at class-load
    @Volatile private var useFallback = true

    // ── Initialisation ──────────────────────────────────────────────────────────

    /**
     * Warm up the detector.  Must be called from a background thread (or
     * inside a coroutine on Dispatchers.IO).
     *
     * @param context       Application context.
     * @param useGpuDelegate Whether to try the GPU delegate for TFLite inference.
     */
    fun init(context: Context, useGpuDelegate: Boolean = true) {
        tryLoadTfliteModel(context, useGpuDelegate)
    }

    private fun tryLoadTfliteModel(context: Context, useGpuDelegate: Boolean) {
        try {
            // Reflect on TFLite classes so we don't crash at class-load if the
            // dependency is absent (it will just stay null and we fall back).
            val interpreterClass = Class.forName("org.tensorflow.lite.Interpreter")
            val optionsClass = Class.forName("org.tensorflow.lite.Interpreter\$Options")
            val options = optionsClass.newInstance()

            if (useGpuDelegate) {
                try {
                    val gpuDelegateClass = Class.forName("org.tensorflow.lite.gpu.GpuDelegate")
                    val gpuDelegate = gpuDelegateClass.newInstance()
                    val addDelegateMethod = optionsClass.getMethod("addDelegate",
                        Class.forName("org.tensorflow.lite.Delegate"))
                    addDelegateMethod.invoke(options, gpuDelegate)
                    Log.d(TAG, "GPU delegate attached")
                } catch (e: Exception) {
                    Log.d(TAG, "GPU delegate unavailable, using CPU: ${e.message}")
                }
            }

            val assetFileDescriptor = context.assets.openFd(MODEL_ASSET)
            val fileInputStream = assetFileDescriptor.createInputStream()
            val modelBytes = fileInputStream.readBytes()
            fileInputStream.close()

            if (modelBytes.size < 8) {
                Log.w(TAG, "shape_detection.tflite is a placeholder – using fallback detector")
                useFallback = true
                return
            }

            val byteBuffer = java.nio.ByteBuffer.allocateDirect(modelBytes.size)
            byteBuffer.put(modelBytes)
            byteBuffer.rewind()

            val ctor = interpreterClass.getConstructor(java.nio.ByteBuffer::class.java, optionsClass)
            tfliteInterpreter = ctor.newInstance(byteBuffer, options)
            useFallback = false
            Log.i(TAG, "TFLite shape model loaded (${modelBytes.size / 1024} KB)")
        } catch (e: Exception) {
            Log.w(TAG, "TFLite model not loaded, using fallback: ${e.message}")
            useFallback = true
        }
    }

    // ── Public API ───────────────────────────────────────────────────────────────

    /**
     * Detect geometric shapes in [bitmap].
     *
     * @param bitmap    Source bitmap (any size; will be down-scaled internally).
     * @return          List of [DetectedShape] sorted by area (largest first).
     */
    suspend fun detect(bitmap: Bitmap): List<DetectedShape> = withContext(Dispatchers.Default) {
        if (!useFallback && tfliteInterpreter != null) {
            detectWithTflite(bitmap)
        } else {
            detectWithFallback(bitmap)
        }
    }

    // ── TFLite inference (skeleton – swap model and post-processing as needed) ──

    @Suppress("UNUSED_PARAMETER")
    private fun detectWithTflite(bitmap: Bitmap): List<DetectedShape> {
        // Real post-processing depends on the model's output tensor layout.
        // Fall through to fallback until a real model is provided.
        Log.d(TAG, "TFLite inference – falling back (no real model yet)")
        return detectWithFallback(bitmap)
    }

    // ── Fallback: pure-Android contour-based detection ───────────────────────────

    /**
     * Pure-Android shape detection:
     *
     * 1. Down-scale to ≤ 320 px on longest edge.
     * 2. Convert to grayscale.
     * 3. Apply 3×3 Sobel edge filter.
     * 4. Threshold → binary edge map.
     * 5. Row/column scanning to find dense horizontal & vertical bands → bounding rects.
     * 6. Classify each candidate region by fill-ratio and aspect-ratio heuristics.
     */
    private fun detectWithFallback(source: Bitmap): List<DetectedShape> {
        val scaled = scaleBitmap(source, MAX_INPUT_EDGE)
        val scaleX = source.width.toFloat() / scaled.width
        val scaleY = source.height.toFloat() / scaled.height

        val w = scaled.width
        val h = scaled.height
        val pixels = IntArray(w * h)
        scaled.getPixels(pixels, 0, w, 0, 0, w, h)
        if (scaled !== source) scaled.recycle()

        val grey = toGrayscale(pixels, w, h)
        val edges = sobelEdges(grey, w, h)
        val binary = threshold(edges, EDGE_THRESHOLD)
        val regions = findRegions(binary, w, h)

        val minArea = w * h * MIN_REGION_AREA_RATIO
        val results = mutableListOf<DetectedShape>()

        for (region in regions) {
            if (region.area < minArea) continue
            val shapedBounds = RectF(
                region.left * scaleX,
                region.top * scaleY,
                region.right * scaleX,
                region.bottom * scaleY
            )
            val shapeType = classifyRegion(region)
            results += DetectedShape(
                bounds = shapedBounds,
                shapeType = shapeType,
                rotationDegrees = 0f,
                confidence = region.confidence
            )
        }

        return results
            .sortedByDescending { it.bounds.width() * it.bounds.height() }
            .take(MAX_REGIONS)
    }

    // ── Image processing helpers ──────────────────────────────────────────────────

    private fun scaleBitmap(src: Bitmap, maxEdge: Int): Bitmap {
        val longest = max(src.width, src.height)
        if (longest <= maxEdge) return src
        val scale = maxEdge.toFloat() / longest
        val nw = (src.width * scale).toInt().coerceAtLeast(1)
        val nh = (src.height * scale).toInt().coerceAtLeast(1)
        return Bitmap.createScaledBitmap(src, nw, nh, true)
    }

    private fun toGrayscale(pixels: IntArray, w: Int, h: Int): FloatArray {
        val grey = FloatArray(w * h)
        for (i in pixels.indices) {
            val p = pixels[i]
            grey[i] = (0.299f * Color.red(p) + 0.587f * Color.green(p) + 0.114f * Color.blue(p))
        }
        return grey
    }

    /** Approximate Sobel magnitude — no 2D convolution allocation. */
    private fun sobelEdges(grey: FloatArray, w: Int, h: Int): FloatArray {
        val out = FloatArray(w * h)
        for (y in 1 until h - 1) {
            for (x in 1 until w - 1) {
                val i = y * w + x
                val gx = (-grey[i - w - 1] - 2 * grey[i - 1] - grey[i + w - 1]
                        + grey[i - w + 1] + 2 * grey[i + 1] + grey[i + w + 1])
                val gy = (-grey[i - w - 1] - 2 * grey[i - w] - grey[i - w + 1]
                        + grey[i + w - 1] + 2 * grey[i + w] + grey[i + w + 1])
                out[i] = sqrt(gx * gx + gy * gy)
            }
        }
        return out
    }

    private fun threshold(edges: FloatArray, thresh: Int): BooleanArray {
        val t = thresh.toFloat()
        return BooleanArray(edges.size) { edges[it] >= t }
    }

    // ── Connected-component / region finder ───────────────────────────────────────

    private data class Region(
        val left: Int, val top: Int, val right: Int, val bottom: Int,
        val edgePixelCount: Int
    ) {
        val width get() = right - left
        val height get() = bottom - top
        val area get() = width * height
        val fillRatio get() = edgePixelCount.toFloat() / area.coerceAtLeast(1)
        val aspectRatio get() = height.toFloat() / width.coerceAtLeast(1)
        val confidence get() = (fillRatio * 2f).coerceIn(0f, 1f)
    }

    /**
     * Simplified region finding via column/row density scanning.
     *
     * Splits the binary edge map into a grid of cells; cells with edge density
     * above a threshold are marked "active".  Contiguous active cells are merged
     * into bounding rectangles.
     */
    private fun findRegions(binary: BooleanArray, w: Int, h: Int): List<Region> {
        val cellSize = 8
        val cols = (w + cellSize - 1) / cellSize
        val rows = (h + cellSize - 1) / cellSize
        val cellActive = BooleanArray(cols * rows)
        val cellEdgePx = IntArray(cols * rows)

        for (cy in 0 until rows) {
            for (cx in 0 until cols) {
                var count = 0
                val x0 = cx * cellSize
                val y0 = cy * cellSize
                val x1 = min(x0 + cellSize, w)
                val y1 = min(y0 + cellSize, h)
                for (py in y0 until y1) {
                    for (px in x0 until x1) {
                        if (binary[py * w + px]) count++
                    }
                }
                val total = (x1 - x0) * (y1 - y0)
                val density = count.toFloat() / total
                cellActive[cy * cols + cx] = density > 0.1f
                cellEdgePx[cy * cols + cx] = count
            }
        }

        // BFS on the cell grid
        val visited = BooleanArray(cols * rows)
        val regions = mutableListOf<Region>()

        for (startIdx in cellActive.indices) {
            if (!cellActive[startIdx] || visited[startIdx]) continue
            val queue = ArrayDeque<Int>()
            queue.add(startIdx)
            visited[startIdx] = true
            var minCx = Int.MAX_VALUE; var maxCx = Int.MIN_VALUE
            var minCy = Int.MAX_VALUE; var maxCy = Int.MIN_VALUE
            var totalEdgePx = 0

            while (queue.isNotEmpty()) {
                val idx = queue.removeFirst()
                val cx = idx % cols
                val cy = idx / cols
                minCx = min(minCx, cx); maxCx = max(maxCx, cx)
                minCy = min(minCy, cy); maxCy = max(maxCy, cy)
                totalEdgePx += cellEdgePx[idx]

                for ((dx, dy) in listOf(0 to 1, 0 to -1, 1 to 0, -1 to 0)) {
                    val nx = cx + dx; val ny = cy + dy
                    if (nx < 0 || nx >= cols || ny < 0 || ny >= rows) continue
                    val nIdx = ny * cols + nx
                    if (!visited[nIdx] && cellActive[nIdx]) {
                        visited[nIdx] = true
                        queue.add(nIdx)
                    }
                }
            }
            regions += Region(
                left  = minCx * cellSize,
                top   = minCy * cellSize,
                right = min((maxCx + 1) * cellSize, w),
                bottom = min((maxCy + 1) * cellSize, h),
                edgePixelCount = totalEdgePx
            )
        }
        return regions
    }

    // ── Shape classification ───────────────────────────────────────────────────────

    private fun classifyRegion(r: Region): ShapeType {
        val aspect = r.aspectRatio           // height / width
        val fill   = r.fillRatio             // edge pixels / bounding area

        // Circularity approximation: a perfect circle inscribed in its bounding
        // box has aspect ≈ 1 and a high ratio of edge pixels along the perimeter.
        val isSquarish = aspect in 0.7f..1.4f
        val perimeter  = 2 * (r.width + r.height).toFloat()
        val circularityScore = r.edgePixelCount / perimeter.coerceAtLeast(1f)
        // For a filled circle the perimeter-edge density is ~π ≈ 3.14
        val looksCircular = isSquarish && circularityScore > 2.0f

        return when {
            looksCircular && fill < TRIANGLE_FILL_THRESHOLD + 0.15f -> ShapeType.CIRCLE
            aspect >= CYLINDER_ASPECT_MIN && isAspectCylinder(r)    -> ShapeType.CYLINDER
            fill < TRIANGLE_FILL_THRESHOLD && !isSquarish           -> ShapeType.TRIANGLE
            else                                                      -> ShapeType.RECTANGLE
        }
    }

    /** Cylinders tend to have parallel vertical edges and a curved top / bottom. */
    private fun isAspectCylinder(r: Region): Boolean =
        r.aspectRatio >= CYLINDER_ASPECT_MIN && abs(r.aspectRatio - 2f) < 1.5f

    /** Exposed for fast-mode: reduce input resolution further for speed. */
    fun detectFastMode(bitmap: Bitmap): List<DetectedShape> {
        val halfW = (bitmap.width * 0.5f).toInt().coerceAtLeast(1)
        val halfH = (bitmap.height * 0.5f).toInt().coerceAtLeast(1)
        val half = Bitmap.createScaledBitmap(bitmap, halfW, halfH, false)
        val shapes = mutableListOf<DetectedShape>()
        val scaleX = bitmap.width.toFloat() / halfW
        val scaleY = bitmap.height.toFloat() / halfH
        val scaled = scaleBitmap(half, MAX_INPUT_EDGE)
        val sx2 = halfW.toFloat() / scaled.width
        val sy2 = halfH.toFloat() / scaled.height

        val w = scaled.width; val h = scaled.height
        val pixels = IntArray(w * h)
        scaled.getPixels(pixels, 0, w, 0, 0, w, h)
        if (scaled !== half) scaled.recycle()
        half.recycle()

        val grey = toGrayscale(pixels, w, h)
        val edges = sobelEdges(grey, w, h)
        val binary = threshold(edges, EDGE_THRESHOLD)
        val regions = findRegions(binary, w, h)

        val minArea = w * h * MIN_REGION_AREA_RATIO
        for (region in regions) {
            if (region.area < minArea) continue
            shapes += DetectedShape(
                bounds = RectF(
                    region.left * sx2 * scaleX,
                    region.top  * sy2 * scaleY,
                    region.right  * sx2 * scaleX,
                    region.bottom * sy2 * scaleY
                ),
                shapeType = classifyRegion(region),
                rotationDegrees = 0f,
                confidence = region.confidence
            )
        }
        return shapes
            .sortedByDescending { it.bounds.width() * it.bounds.height() }
            .take(MAX_REGIONS)
    }

    /** Release any TFLite resources. */
    fun close() {
        try {
            tfliteInterpreter?.let {
                it.javaClass.getMethod("close").invoke(it)
            }
        } catch (_: Exception) {}
        tfliteInterpreter = null
        useFallback = true
    }
}
