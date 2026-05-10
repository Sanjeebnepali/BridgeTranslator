package com.bridge.translator.processing;

/**
 * Detects text orientation (horizontal, vertical, rotated) from ML Kit OCR blocks.
 *
 * Handles:
 * - Text angle calculation from bounding box corners
 * - Orientation classification (HORIZONTAL/VERTICAL/ROTATED)
 * - Confidence scoring based on text characteristics
 * - Bitmap rotation for vertical text
 *
 * Usage:
 * ```
 * val detector = TextOrientationDetector()
 * val orientation = detector.detectOrientation(textBlock)
 * val (rotatedBitmap, angle) = detector.rotateIfNeeded(bitmap, textBlock)
 * ```
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u0000 \u00192\u00020\u0001:\u0003\u0019\u001a\u001bB\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0002J\u0010\u0010\u0007\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0002J\u000e\u0010\b\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006J\u001b\u0010\t\u001a\u00020\u00042\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\f0\u000bH\u0002\u00a2\u0006\u0002\u0010\rJ\u0010\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0004H\u0002J\u0016\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0005\u001a\u00020\u0006J\u000e\u0010\u0015\u001a\u00020\u000f2\u0006\u0010\u0005\u001a\u00020\u0006J\u0018\u0010\u0016\u001a\u00020\u00142\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0010\u001a\u00020\u0004H\u0002J\"\u0010\u0017\u001a\u000e\u0012\u0004\u0012\u00020\u0014\u0012\u0004\u0012\u00020\u00040\u00182\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0005\u001a\u00020\u0006\u00a8\u0006\u001c"}, d2 = {"Lcom/bridge/translator/processing/TextOrientationDetector;", "", "()V", "calculateAngle", "", "textBlock", "Lcom/google/mlkit/vision/text/Text$TextBlock;", "calculateCharacterConsistency", "calculateConfidence", "calculateLinesStraightness", "corners", "", "Landroid/graphics/Point;", "([Landroid/graphics/Point;)F", "classifyAngle", "Lcom/bridge/translator/processing/TextOrientationDetector$Orientation;", "angle", "detectFull", "Lcom/bridge/translator/processing/TextOrientationDetector$OrientationResult;", "bitmap", "Landroid/graphics/Bitmap;", "detectOrientation", "rotateBitmap", "rotateIfNeeded", "Lkotlin/Pair;", "Companion", "Orientation", "OrientationResult", "app_debug"})
public final class TextOrientationDetector {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String TAG = "TextOrientationDetector";
    private static final float HORIZONTAL_THRESHOLD = 10.0F;
    private static final float VERTICAL_LOWER = 80.0F;
    private static final float VERTICAL_UPPER = 100.0F;
    private static final float VERTICAL_LOWER_OPPOSITE = 260.0F;
    private static final float VERTICAL_UPPER_OPPOSITE = 280.0F;
    @org.jetbrains.annotations.NotNull()
    public static final com.bridge.translator.processing.TextOrientationDetector.Companion Companion = null;
    
    public TextOrientationDetector() {
        super();
    }
    
    /**
     * Detect text orientation from ML Kit text block
     *
     * @param textBlock ML Kit recognized text block with bounding box
     * @return Orientation classification
     */
    @org.jetbrains.annotations.NotNull()
    public final com.bridge.translator.processing.TextOrientationDetector.Orientation detectOrientation(@org.jetbrains.annotations.NotNull()
    com.google.mlkit.vision.text.Text.TextBlock textBlock) {
        return null;
    }
    
    /**
     * Calculate confidence score for orientation detection
     *
     * Factors:
     * - Text length (longer = higher confidence)
     * - Line straightness (measure deviation from ideal line)
     * - Character consistency (uniform sizing)
     *
     * @param textBlock ML Kit text block
     * @return Confidence score (0.0 to 1.0)
     */
    public final float calculateConfidence(@org.jetbrains.annotations.NotNull()
    com.google.mlkit.vision.text.Text.TextBlock textBlock) {
        return 0.0F;
    }
    
    /**
     * Rotate bitmap if text is significantly vertical
     *
     * Only rotates for clearly vertical text (80-100° or 260-280°)
     * This helps with OCR accuracy on vertical text
     *
     * @param bitmap Image containing the text
     * @param textBlock ML Kit text block
     * @return Pair of (bitmap, rotationAngle applied)
     */
    @org.jetbrains.annotations.NotNull()
    public final kotlin.Pair<android.graphics.Bitmap, java.lang.Float> rotateIfNeeded(@org.jetbrains.annotations.NotNull()
    android.graphics.Bitmap bitmap, @org.jetbrains.annotations.NotNull()
    com.google.mlkit.vision.text.Text.TextBlock textBlock) {
        return null;
    }
    
    /**
     * Full orientation detection with all metadata
     */
    @org.jetbrains.annotations.NotNull()
    public final com.bridge.translator.processing.TextOrientationDetector.OrientationResult detectFull(@org.jetbrains.annotations.NotNull()
    android.graphics.Bitmap bitmap, @org.jetbrains.annotations.NotNull()
    com.google.mlkit.vision.text.Text.TextBlock textBlock) {
        return null;
    }
    
    /**
     * Calculate text angle from bounding box corners using atan2
     *
     * @param textBlock ML Kit text block with cornerPoints
     * @return Angle in degrees (-180 to 180)
     */
    private final float calculateAngle(com.google.mlkit.vision.text.Text.TextBlock textBlock) {
        return 0.0F;
    }
    
    /**
     * Classify angle into orientation bucket
     */
    private final com.bridge.translator.processing.TextOrientationDetector.Orientation classifyAngle(float angle) {
        return null;
    }
    
    /**
     * Calculate how straight the line is (for straightness confidence)
     *
     * Measures deviation from ideal line connecting first and last corners
     */
    private final float calculateLinesStraightness(android.graphics.Point[] corners) {
        return 0.0F;
    }
    
    /**
     * Calculate character consistency confidence
     *
     * Checks if characters are uniformly sized
     */
    private final float calculateCharacterConsistency(com.google.mlkit.vision.text.Text.TextBlock textBlock) {
        return 0.0F;
    }
    
    /**
     * Rotate bitmap by specified angle
     */
    private final android.graphics.Bitmap rotateBitmap(android.graphics.Bitmap bitmap, float angle) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0005\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000b"}, d2 = {"Lcom/bridge/translator/processing/TextOrientationDetector$Companion;", "", "()V", "HORIZONTAL_THRESHOLD", "", "TAG", "", "VERTICAL_LOWER", "VERTICAL_LOWER_OPPOSITE", "VERTICAL_UPPER", "VERTICAL_UPPER_OPPOSITE", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
    
    /**
     * Text orientation classification
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0005\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002j\u0002\b\u0003j\u0002\b\u0004j\u0002\b\u0005\u00a8\u0006\u0006"}, d2 = {"Lcom/bridge/translator/processing/TextOrientationDetector$Orientation;", "", "(Ljava/lang/String;I)V", "HORIZONTAL", "VERTICAL", "ROTATED", "app_debug"})
    public static enum Orientation {
        /*public static final*/ HORIZONTAL /* = new HORIZONTAL() */,
        /*public static final*/ VERTICAL /* = new VERTICAL() */,
        /*public static final*/ ROTATED /* = new ROTATED() */;
        
        Orientation() {
        }
        
        @org.jetbrains.annotations.NotNull()
        public static kotlin.enums.EnumEntries<com.bridge.translator.processing.TextOrientationDetector.Orientation> getEntries() {
            return null;
        }
    }
    
    /**
     * Result of orientation detection
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0019\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B?\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\u0005\u0012\u0006\u0010\t\u001a\u00020\n\u0012\b\u0010\u000b\u001a\u0004\u0018\u00010\f\u0012\u0006\u0010\r\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u000eJ\t\u0010\u001b\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001c\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u001d\u001a\u00020\u0007H\u00c6\u0003J\t\u0010\u001e\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u001f\u001a\u00020\nH\u00c6\u0003J\u000b\u0010 \u001a\u0004\u0018\u00010\fH\u00c6\u0003J\t\u0010!\u001a\u00020\u0005H\u00c6\u0003JQ\u0010\"\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\u00052\b\b\u0002\u0010\t\u001a\u00020\n2\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\f2\b\b\u0002\u0010\r\u001a\u00020\u0005H\u00c6\u0001J\u0013\u0010#\u001a\u00020\n2\b\u0010$\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010%\u001a\u00020&H\u00d6\u0001J\t\u0010\'\u001a\u00020(H\u00d6\u0001R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\b\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0010R\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0013\u0010\u000b\u001a\u0004\u0018\u00010\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u0011\u0010\r\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0010R\u0011\u0010\t\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u001a\u00a8\u0006)"}, d2 = {"Lcom/bridge/translator/processing/TextOrientationDetector$OrientationResult;", "", "text", "Lcom/google/mlkit/vision/text/Text$TextBlock;", "angle", "", "orientation", "Lcom/bridge/translator/processing/TextOrientationDetector$Orientation;", "confidence", "shouldRotate", "", "rotatedBitmap", "Landroid/graphics/Bitmap;", "rotationAngle", "(Lcom/google/mlkit/vision/text/Text$TextBlock;FLcom/bridge/translator/processing/TextOrientationDetector$Orientation;FZLandroid/graphics/Bitmap;F)V", "getAngle", "()F", "getConfidence", "getOrientation", "()Lcom/bridge/translator/processing/TextOrientationDetector$Orientation;", "getRotatedBitmap", "()Landroid/graphics/Bitmap;", "getRotationAngle", "getShouldRotate", "()Z", "getText", "()Lcom/google/mlkit/vision/text/Text$TextBlock;", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "copy", "equals", "other", "hashCode", "", "toString", "", "app_debug"})
    public static final class OrientationResult {
        @org.jetbrains.annotations.NotNull()
        private final com.google.mlkit.vision.text.Text.TextBlock text = null;
        private final float angle = 0.0F;
        @org.jetbrains.annotations.NotNull()
        private final com.bridge.translator.processing.TextOrientationDetector.Orientation orientation = null;
        private final float confidence = 0.0F;
        private final boolean shouldRotate = false;
        @org.jetbrains.annotations.Nullable()
        private final android.graphics.Bitmap rotatedBitmap = null;
        private final float rotationAngle = 0.0F;
        
        public OrientationResult(@org.jetbrains.annotations.NotNull()
        com.google.mlkit.vision.text.Text.TextBlock text, float angle, @org.jetbrains.annotations.NotNull()
        com.bridge.translator.processing.TextOrientationDetector.Orientation orientation, float confidence, boolean shouldRotate, @org.jetbrains.annotations.Nullable()
        android.graphics.Bitmap rotatedBitmap, float rotationAngle) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.google.mlkit.vision.text.Text.TextBlock getText() {
            return null;
        }
        
        public final float getAngle() {
            return 0.0F;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.bridge.translator.processing.TextOrientationDetector.Orientation getOrientation() {
            return null;
        }
        
        public final float getConfidence() {
            return 0.0F;
        }
        
        public final boolean getShouldRotate() {
            return false;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final android.graphics.Bitmap getRotatedBitmap() {
            return null;
        }
        
        public final float getRotationAngle() {
            return 0.0F;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.google.mlkit.vision.text.Text.TextBlock component1() {
            return null;
        }
        
        public final float component2() {
            return 0.0F;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.bridge.translator.processing.TextOrientationDetector.Orientation component3() {
            return null;
        }
        
        public final float component4() {
            return 0.0F;
        }
        
        public final boolean component5() {
            return false;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final android.graphics.Bitmap component6() {
            return null;
        }
        
        public final float component7() {
            return 0.0F;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.bridge.translator.processing.TextOrientationDetector.OrientationResult copy(@org.jetbrains.annotations.NotNull()
        com.google.mlkit.vision.text.Text.TextBlock text, float angle, @org.jetbrains.annotations.NotNull()
        com.bridge.translator.processing.TextOrientationDetector.Orientation orientation, float confidence, boolean shouldRotate, @org.jetbrains.annotations.Nullable()
        android.graphics.Bitmap rotatedBitmap, float rotationAngle) {
            return null;
        }
        
        @java.lang.Override()
        public boolean equals(@org.jetbrains.annotations.Nullable()
        java.lang.Object other) {
            return false;
        }
        
        @java.lang.Override()
        public int hashCode() {
            return 0;
        }
        
        @java.lang.Override()
        @org.jetbrains.annotations.NotNull()
        public java.lang.String toString() {
            return null;
        }
    }
}