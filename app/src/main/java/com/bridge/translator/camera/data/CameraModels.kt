package com.bridge.translator.camera.data

import android.graphics.PointF
import android.graphics.RectF

// ── Orientation ───────────────────────────────────────────────────────────────

enum class TextOrientation {
    HORIZONTAL,       // angle approx 0° or 180°
    VERTICAL_UP,      // angle approx 90°
    VERTICAL_DOWN,    // angle approx 270°
    ROTATED,          // any other angle
    CURVED            // varies per character (cylinders, bottles)
}

/**
 * A single detected text region annotated with orientation information.
 */
data class TextOrientationBlock(
    val text: String,
    val boundingBox: RectF,
    val angle: Float,                     // degrees, clockwise from horizontal
    val orientation: TextOrientation,
    val orientationConfidence: Float,     // 0–1
    val estimatedFontSizePt: Float,       // estimated pt size at current distance
    val language: String = "und",
    val ocrConfidence: Float = 1f
)

// ── Language detection ────────────────────────────────────────────────────────

data class LanguageDetectionResult(
    val language: String,                 // BCP-47 code, e.g. "ko", "zh", "und"
    val confidence: Float,                // 0–1
    val method: DetectionMethod
)

enum class DetectionMethod {
    MLKIT_PRIMARY,
    CHARSET_ANALYSIS,
    DICTIONARY_LOOKUP,
    HYBRID,
    UNKNOWN
}

// ── Small text enhancement ────────────────────────────────────────────────────

data class EnhancedOcrResult(
    val text: String,
    val confidence: Float,
    val upscaleFactor: Float,            // 1.0 = no upscale; 2.0, 2.5, 3.0
    val wasEnhanced: Boolean,
    val mlkitText: String,
    val mlkitConfidence: Float
)

// ── Banner rendering ──────────────────────────────────────────────────────────

enum class BannerPlacement {
    BELOW,
    ABOVE,
    SIDE_LEFT,
    SIDE_RIGHT,
    POPUP_CENTER
}

data class TranslationBanner(
    val sourceBlock: TextOrientationBlock,
    val translatedText: String,
    val targetRect: RectF,               // where to draw the banner
    val placement: BannerPlacement,
    val textSize: Float,
    val isRtl: Boolean,
    val bgAlpha: Int,                    // 0–255
    val textColor: Int,
    val bgColor: Int
)

// ── Distance & quality ────────────────────────────────────────────────────────

enum class DistanceZone {
    TOO_CLOSE,    // < 20 cm
    OPTIMAL,      // 20–100 cm
    ACCEPTABLE,   // 100–150 cm
    TOO_FAR       // > 150 cm
}

data class DistanceReport(
    val estimatedCm: Float,
    val zone: DistanceZone,
    val confidence: Float,               // 0–1 (HIGH = object-based, MEDIUM = heuristic)
    val message: String                  // user-facing feedback
)

data class QualityReport(
    val score: Float,                    // 0–100
    val ocrConfidence: Float,            // 0–1
    val sharpnessScore: Float,           // 0–1 (Laplacian variance)
    val distanceScore: Float,            // 0–1
    val contrastScore: Float,            // 0–1
    val stabilityScore: Float,           // 0–1
    val zone: QualityZone,
    val feedback: String
)

enum class QualityZone {
    EXCELLENT,   // 80–100
    GOOD,        // 60–80
    POOR         // < 60
}

// ── Orchestrator result ───────────────────────────────────────────────────────

data class OrchestrationResult(
    val banners: List<TranslationBanner>,
    val quality: QualityReport,
    val distance: DistanceReport,
    val processingMs: Long,
    val allBlocks: List<TextOrientationBlock>
)
