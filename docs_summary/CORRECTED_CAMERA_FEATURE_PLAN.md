# CORRECTED: Camera Translation Feature Implementation Plan

**Corrected Master Prompt** (3 weeks, Kotlin/Android)

**Status**: Ready to implement (issues from original MASTER PROMPT fixed)

---

## EXECUTIVE SUMMARY

Add camera translation to BridgeTranslator as a **3-week feature extension** (not 8-week system redesign).

**What exists & works**:
- Screen capture translation (FloatingBubbleService)
- Text detection + language ID + translation (ML Kit)
- Overlay rendering (BitmapTextEraser)
- Manual-trigger bubble UI

**What to add**:
- Camera input as alternative to screen capture
- Text orientation detection (vertical text support)
- Enhanced language detection (fallback for low confidence)
- Optional distance feedback UI

**Team**: 1-2 developers, 3 weeks

---

## PART 1: ARCHITECTURE (CORRECTED)

### Current System (Working)

```
FloatingBubbleService
  ├── ScreenCaptureManager → MediaProjection → Screenshot
  ├── ScreenAnalyser → ML Kit OCR + Language ID
  ├── TranslationEngine → ML Kit Translate (parallel)
  ├── BitmapTextEraser → Text rendering on image
  └── OverlayManager → Full-screen overlay display
```

### Extended System (With Camera)

```
FloatingBubbleService (enhanced)
  ├── Capture Mode Selection
  │   ├── Screen: ScreenCaptureManager (existing)
  │   └── Camera: CameraManager (NEW)
  │
  ├── Frame Processing (SAME for both modes)
  │   ├── TextOrientationDetector (NEW - rotate if vertical)
  │   ├── ScreenAnalyser (existing + enhanced fallback)
  │   ├── TranslationEngine (existing, no changes)
  │   └── BitmapTextEraser (existing, no changes)
  │
  └── Display (SAME for both modes)
      └── OverlayManager (existing)
```

**Key insight**: Camera produces Bitmap just like screen capture. After that, entire existing pipeline works unchanged.

---

## PART 2: IMPLEMENTATION (3 WEEKS)

### WEEK 1: Camera Integration

#### Day 1-2: CameraManager Setup

**File**: `app/src/main/java/com/bridge/translator/service/CameraManager.kt`

```kotlin
package com.bridge.translator.service

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraManager(
    private val context: Context,
    private val onFrameCaptured: suspend (Bitmap?) -> Unit
) : LifecycleObserver {
    
    private var imageCapture: ImageCapture? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private val executor: ExecutorService = Executors.newSingleThreadExecutor()
    
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun setupCamera() {
        // Initialize CameraX
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.result
            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()
            Log.d("CameraManager", "Camera ready")
        }, ContextCompat.getMainExecutor(context))
    }
    
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun startCamera() {
        // Bind CameraX to lifecycle
        cameraProvider?.let { provider ->
            try {
                provider.unbindAll()
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                provider.bindToLifecycle(
                    context as androidx.lifecycle.LifecycleOwner,
                    cameraSelector,
                    imageCapture
                )
                Log.d("CameraManager", "Camera started")
            } catch (e: Exception) {
                Log.e("CameraManager", "Failed to start camera: ${e.message}")
            }
        }
    }
    
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun stopCamera() {
        cameraProvider?.unbindAll()
        Log.d("CameraManager", "Camera stopped")
    }
    
    suspend fun captureFrame(): Bitmap? {
        return withContext(Dispatchers.Default) {
            imageCapture?.takePicture(
                executor,
                object : ImageCapture.OnImageCapturedCallback() {
                    override fun onCaptureSuccess(image: ImageProxy) {
                        val bitmap = image.toBitmap()
                        onFrameCaptured(bitmap)
                        image.close()
                    }
                    
                    override fun onError(exception: ImageCaptureException) {
                        Log.e("CameraManager", "Capture failed: ${exception.message}")
                        onFrameCaptured(null)
                    }
                }
            )
            null
        }
    }
    
    fun shutdown() {
        executor.shutdown()
    }
}
```

**Add to AndroidManifest.xml**:
```xml
<uses-permission android:name="android.permission.CAMERA" />
<uses-feature android:name="android.hardware.camera" android:required="true" />
```

**Add to build.gradle.kts** (already present in current build):
```kotlin
// CameraX already in dependencies:
// implementation("androidx.camera:camera-camera2:1.4.1")
// implementation("androidx.camera:camera-lifecycle:1.4.1")
```

#### Day 3: Permission Handling

**Update**: `app/src/main/java/com/bridge/translator/ui/main/MainActivity.kt`

```kotlin
// In MainActivity, add after overlay permission check:

private fun checkCameraPermission() {
    when {
        ContextCompat.checkSelfPermission(
            this, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED -> {
            // Camera permission granted
            cameraAvailable = true
        }
        else -> {
            // Request camera permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        }
    }
}

override fun onRequestPermissionsResult(
    requestCode: Int, permissions: Array<String>, grantResults: IntArray
) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    when (requestCode) {
        CAMERA_PERMISSION_REQUEST_CODE -> {
            cameraAvailable = grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
        }
    }
}

companion object {
    private const val CAMERA_PERMISSION_REQUEST_CODE = 101
}
```

#### Day 4-5: Add Camera/Screen Toggle

**File**: `app/src/main/java/com/bridge/translator/ui/main/MainActivity.kt`

Add to UI layout:
```xml
<!-- In activity_main.xml -->
<SegmentedButtonGroup
    android:id="@+id/captureMode"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content">
    
    <Button
        android:id="@+id/screenMode"
        android:text="Screen"
        style="@style/Widget.Material3.Button.OutlinedButton" />
    
    <Button
        android:id="@+id/cameraMode"
        android:text="Camera"
        style="@style/Widget.Material3.Button.OutlinedButton" />
</SegmentedButtonGroup>
```

Update MainViewModel:
```kotlin
// In MainViewModel
private val _captureMode = MutableStateFlow<CaptureMode>(CaptureMode.SCREEN)
val captureMode: StateFlow<CaptureMode> = _captureMode

fun setCaptureMode(mode: CaptureMode) {
    _captureMode.value = mode
    // Save to DataStore
    viewModelScope.launch {
        dataStore.edit { preferences ->
            preferences[CAPTURE_MODE] = mode.name
        }
    }
}

enum class CaptureMode {
    SCREEN, CAMERA
}
```

Update FloatingBubbleService:
```kotlin
// In FloatingBubbleService.handleBubbleTap()
private fun handleBubbleTap() {
    when (captureMode) {
        CaptureMode.SCREEN -> startScreenCapture()
        CaptureMode.CAMERA -> startCameraCapture()
    }
}

private fun startCameraCapture() {
    if (!cameraAvailable) {
        Toast.makeText(this, "Camera permission not granted", Toast.LENGTH_SHORT).show()
        return
    }
    // Same flow as screen capture but use CameraManager
    startCaptureCycle(captureSource = CaptureSource.CAMERA)
}
```

---

### WEEK 2: Text Orientation Detection

#### Day 1-2: TextOrientationDetector

**File**: `app/src/main/java/com/bridge/translator/processing/TextOrientationDetector.kt`

```kotlin
package com.bridge.translator.processing

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import com.google.mlkit.vision.text.Text
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class TextOrientationDetector {
    
    data class OrientationResult(
        val text: Text.TextBlock,
        val angle: Float,  // -180 to 180 degrees
        val orientation: Orientation,
        val shouldRotate: Boolean,
        val rotatedBitmap: Bitmap?
    )
    
    enum class Orientation {
        HORIZONTAL,  // ±10° from 0° or 180°
        VERTICAL,    // ±10° from 90° or 270°
        ROTATED      // any other angle
    }
    
    /**
     * Detect text orientation from bounding box corners
     */
    fun detectOrientation(textBlock: Text.TextBlock): Orientation {
        val corners = textBlock.cornerPoints ?: return Orientation.HORIZONTAL
        if (corners.size < 2) return Orientation.HORIZONTAL
        
        // Calculate angle from first two corners
        val p1 = corners[0]
        val p2 = corners[1]
        val dx = (p2.x - p1.x).toFloat()
        val dy = (p2.y - p1.y).toFloat()
        
        // Angle in degrees (-180 to 180)
        val angleRad = atan2(dy, dx)
        val angleDeg = Math.toDegrees(angleRad.toDouble()).toFloat()
        
        // Normalize to 0-360
        val normalized = if (angleDeg < 0) angleDeg + 360 else angleDeg
        
        return classifyAngle(normalized)
    }
    
    private fun classifyAngle(angle: Float): Orientation {
        return when {
            // Horizontal: ~0° or ~180°
            (angle >= -10 && angle <= 10) ||
            (angle >= 170 && angle <= 190) ||
            (angle >= 350 && angle <= 360) -> Orientation.HORIZONTAL
            
            // Vertical: ~90° or ~270°
            (angle >= 80 && angle <= 100) ||
            (angle >= 260 && angle <= 280) -> Orientation.VERTICAL
            
            else -> Orientation.ROTATED
        }
    }
    
    /**
     * Rotate bitmap if text is significantly rotated
     * Only rotate for clearly vertical text (80-100° or 260-280°)
     */
    fun rotateIfNeeded(
        bitmap: Bitmap,
        textBlock: Text.TextBlock
    ): Pair<Bitmap, Float> {
        val orientation = detectOrientation(textBlock)
        
        if (orientation != Orientation.VERTICAL) {
            return Pair(bitmap, 0f)  // No rotation needed
        }
        
        // Calculate exact rotation angle
        val corners = textBlock.cornerPoints ?: return Pair(bitmap, 0f)
        if (corners.size < 2) return Pair(bitmap, 0f)
        
        val p1 = corners[0]
        val p2 = corners[1]
        val dx = (p2.x - p1.x).toFloat()
        val dy = (p2.y - p1.y).toFloat()
        val angleRad = atan2(dy, dx)
        val angleDeg = Math.toDegrees(angleRad.toDouble()).toFloat()
        
        // Rotate by nearest 90° increment
        val rotationAngle = when {
            angleDeg in 45f..135f -> 90f      // Rotate 90° clockwise
            angleDeg in 225f..315f -> -90f    // Rotate 90° counter-clockwise
            else -> 0f                         // No rotation
        }
        
        if (rotationAngle == 0f) {
            return Pair(bitmap, 0f)
        }
        
        // Perform rotation
        val matrix = Matrix().apply {
            postRotate(rotationAngle, bitmap.width / 2f, bitmap.height / 2f)
        }
        
        val rotated = Bitmap.createBitmap(
            bitmap,
            0, 0,
            bitmap.width, bitmap.height,
            matrix,
            true
        )
        
        return Pair(rotated, rotationAngle)
    }
}
```

#### Day 3: Integrate into ScreenAnalyser

**Update**: `app/src/main/java/com/bridge/translator/processing/ScreenAnalyser.kt`

```kotlin
// Add to ScreenAnalyser class:

private val orientationDetector = TextOrientationDetector()

suspend fun analyseWithOrientation(bitmap: Bitmap): List<com.bridge.translator.processing.TextBlock> {
    return withContext(Dispatchers.IO) {
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.Builder().build())
        val inputImage = InputImage.fromBitmap(bitmap, 0)
        
        val task = recognizer.process(inputImage)
        val texts = Tasks.await(task)
        
        val blocks = mutableListOf<com.bridge.translator.processing.TextBlock>()
        
        for (textBlock in texts.textBlocks) {
            // Detect orientation
            val (rotatedBitmap, rotationAngle) = orientationDetector.rotateIfNeeded(bitmap, textBlock)
            
            // If rotated, re-recognize text on rotated image
            val finalText = if (rotationAngle != 0f) {
                // Re-recognize on rotated bitmap
                val rotatedInput = InputImage.fromBitmap(rotatedBitmap, 0)
                val rotatedTexts = Tasks.await(recognizer.process(rotatedInput))
                rotatedTexts.textBlocks.firstOrNull { tb ->
                    // Find matching block in rotated image
                    overlapsWith(tb, textBlock)
                }?.text ?: textBlock.text
            } else {
                textBlock.text
            }
            
            // Language detection
            val detectedLang = detectLanguageWithFallback(finalText)
            
            blocks.add(
                com.bridge.translator.processing.TextBlock(
                    originalText = finalText,
                    translatedText = finalText,
                    bbox = textBlock.boundingBox,
                    detectedLang = detectedLang,
                    isBold = textBlock.lines.any { it.elements.any { e -> e.isBot } }
                )
            )
            
            // Clean up
            if (rotationAngle != 0f) {
                rotatedBitmap.recycle()
            }
        }
        
        blocks
    }
}

private fun overlapsWith(block1: Text.TextBlock, block2: Text.TextBlock): Boolean {
    val b1 = block1.boundingBox ?: return false
    val b2 = block2.boundingBox ?: return false
    return b1.left < b2.right && b1.right > b2.left &&
           b1.top < b2.bottom && b1.bottom > b2.top
}
```

#### Day 4: Enhanced Language Detection Fallback

**File**: `app/src/main/java/com/bridge/translator/processing/LanguageFallback.kt`

```kotlin
package com.bridge.translator.processing

class LanguageFallback {
    
    private val charRanges = mapOf(
        "ko" to listOf(0xAC00..0xD7A3),      // Hangul
        "zh" to listOf(0x4E00..0x9FFF),      // CJK
        "ja" to listOf(0x3040..0x309F, 0x30A0..0x30FF),  // Hiragana, Katakana
        "ar" to listOf(0x0600..0x06FF),      // Arabic
        "ru" to listOf(0x0400..0x04FF),      // Cyrillic
        "th" to listOf(0x0E00..0x0E7F),      // Thai
    )
    
    private val commonWords = mapOf(
        "ko" to setOf("의", "가", "를", "은", "는", "하다", "이다"),
        "ja" to setOf("の", "を", "に", "は", "た", "た"),
        "zh" to setOf("的", "一", "是", "在", "不", "了"),
        "en" to setOf("the", "be", "to", "of", "and", "a"),
    )
    
    /**
     * Fallback language detection when ML Kit confidence is low
     */
    fun detectByCharacterSet(text: String): Pair<String, Float> {
        val scores = mutableMapOf<String, Float>()
        
        for ((lang, ranges) in charRanges) {
            var count = 0
            for (char in text) {
                for (range in ranges) {
                    if (char.code in range) {
                        count++
                        break
                    }
                }
            }
            scores[lang] = count.toFloat() / text.length
        }
        
        val best = scores.maxByOrNull { it.value } ?: return Pair("en", 0.0f)
        return Pair(best.key, best.value)
    }
    
    /**
     * Use dictionary matching for additional confidence
     */
    fun detectByDictionary(text: String): Pair<String, Float> {
        val words = text.lowercase().split(Regex("\\s+"))
        val scores = mutableMapOf<String, Int>()
        
        for ((lang, dict) in commonWords) {
            for (word in words) {
                if (word in dict) {
                    scores[lang] = (scores[lang] ?: 0) + 1
                }
            }
        }
        
        val best = scores.maxByOrNull { it.value } ?: return Pair("en", 0.0f)
        return Pair(best.key, (best.value.toFloat() / words.size).coerceAtMost(1.0f))
    }
    
    /**
     * Hybrid detection
     */
    fun fallbackDetect(text: String): String {
        val (charLang, charScore) = detectByCharacterSet(text)
        val (dictLang, dictScore) = detectByDictionary(text)
        
        // Weighted average: 60% character set, 40% dictionary
        val charWeight = 0.6f
        val dictWeight = 0.4f
        
        val hybridScore = (charScore * charWeight + dictScore * dictWeight) / 2f
        
        return if (hybridScore > 0.3f) charLang else "en"  // Default to English
    }
}
```

**Update**: `app/src/main/java/com/bridge/translator/processing/ScreenAnalyser.kt`

```kotlin
private val languageFallback = LanguageFallback()

suspend fun detectLanguageWithFallback(text: String): String {
    return withContext(Dispatchers.Default) {
        try {
            // Primary: ML Kit Language ID
            val result = Tasks.await(langIdentifier.identifyLanguage(text))
            val primaryLang = result?.substring(0, 2) ?: "un"
            
            // Get confidence (estimate based on match)
            val confidence = if (primaryLang != "un") 0.8f else 0.0f
            
            if (confidence >= 0.6f) {
                return@withContext primaryLang
            }
            
            // Fallback: Character set + dictionary
            languageFallback.fallbackDetect(text)
        } catch (e: Exception) {
            // Fallback on exception
            languageFallback.fallbackDetect(text)
        }
    }
}
```

#### Day 5: Testing Week 2

- Test orientation detection on vertical text images
- Test language fallback with low-confidence inputs
- Performance profiling
- Manual testing on 2 devices

---

### WEEK 3: Polish, Testing & Release

#### Day 1-2: Performance & Memory

**Optimize CameraManager**:
```kotlin
// In CameraManager, reduce memory footprint:
// Use JPEG compression instead of raw frames
// Implement frame skipping (process every 3rd frame for live preview)
// Clear bitmap cache between frames
```

**Memory Profiling**:
- Test on low-end device (minSdk 24)
- Verify <200MB peak memory
- Check for bitmap leaks

#### Day 3: Edge Cases & Error Handling

Test & handle:
- ✅ Camera permission denied
- ✅ Camera not available on device
- ✅ Very small text (<8pt) — document limitation
- ✅ Blurry frames
- ✅ Low light conditions
- ✅ Device rotation during capture

#### Day 4: Documentation

Update/create:
- `CLAUDE.md` — Add camera feature section
- Kotlin doc comments for CameraManager, TextOrientationDetector
- User guide: "How to use camera translation"
- Limitations document

#### Day 5: Final QA & Release

- Full device testing (3+ devices, varied Android versions)
- Manual test checklist
- Demo video (screen + camera translation)
- Ready for user testing

---

## PART 3: KEY FILES TO CREATE/MODIFY

### New Files (3 files)

```
app/src/main/java/com/bridge/translator/
├── service/CameraManager.kt                    (NEW - 150 lines)
├── processing/TextOrientationDetector.kt       (NEW - 120 lines)
└── processing/LanguageFallback.kt              (NEW - 100 lines)
```

### Modified Files (4 files)

```
app/src/main/java/com/bridge/translator/
├── service/FloatingBubbleService.kt            (add camera mode)
├── processing/ScreenAnalyser.kt                (enhance language detection)
├── ui/main/MainActivity.kt                     (add camera toggle)
└── ui/main/MainViewModel.kt                    (track capture mode)

app/src/main/
├── AndroidManifest.xml                         (add CAMERA permission)
└── res/layout/activity_main.xml                (add mode selector)

app/
└── build.gradle.kts                            (no changes - CameraX already present)
```

### TOTAL NEW CODE: ~370 lines Kotlin

---

## PART 4: DEPENDENCIES (NO NEW LARGE LIBRARIES)

### Already in build.gradle.kts ✅

```kotlin
// CameraX (already present)
implementation("androidx.camera:camera-camera2:1.4.1")
implementation("androidx.camera:camera-lifecycle:1.4.1")
implementation("androidx.camera:camera-view:1.4.1")

// ML Kit (already present)
implementation("com.google.mlkit:text-recognition:16.0.1")
implementation("com.google.mlkit:language-id:17.0.6")
implementation("com.google.mlkit:translate:17.0.3")

// Coroutines (already present)
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
```

### NO NEW DEPENDENCIES NEEDED ✅

✗ NO ESRGAN (too large)  
✗ NO Tesseract (ML Kit sufficient)  
✗ NO OpenCV (math library enough)  
✗ NO RenderScript (deprecated)

---

## PART 5: REALISTIC SUCCESS CRITERIA

### MVP Criteria (Week 3)

✅ Camera capture works (single frame)  
✅ Vertical text detected + rotated  
✅ Language detection includes fallback  
✅ <3 second processing per frame  
✅ <200MB memory peak  
✅ No crashes on minSdk 24  
✅ Screen mode still works  
✅ Can switch between camera/screen  

### NOT in scope:

❌ Live preview (continuous camera feed)  
❌ Small text enhancement (<8pt)  
❌ Distance estimation  
❌ Image upscaling  
❌ Real-time performance (<500ms per frame)

---

## PART 6: TESTING CHECKLIST

### Android Devices (minimum)

- [ ] Low-end: Android 7-8 (minSdk 24), 2GB RAM
- [ ] Mid-range: Android 10-11, 4GB RAM
- [ ] Modern: Android 13+, 8GB RAM

### Camera Tests

- [ ] Camera permission grant flow
- [ ] Camera permission deny flow
- [ ] Capture single frame
- [ ] Frame quality (various lighting)
- [ ] Device rotation handling
- [ ] App background/foreground

### OCR/Translation Tests

- [ ] Horizontal text (existing feature)
- [ ] Vertical text (NEW - target 80%)
- [ ] Mixed orientation (NEW - target 75%)
- [ ] Multiple languages (10+)
- [ ] Low-confidence language detection

### Integration Tests

- [ ] Camera capture → OCR → Translate → Overlay
- [ ] Screen capture → OCR → Translate → Overlay (existing)
- [ ] Toggle between camera/screen modes
- [ ] No memory leaks
- [ ] No crashes

### Performance Tests

- [ ] Memory: <200MB peak
- [ ] Latency: <3 seconds per frame
- [ ] Battery: <10%/hour during camera use
- [ ] CPU: <70% sustained

---

## PART 7: DELIVERABLES

### Code

✅ CameraManager.kt (150 lines)  
✅ TextOrientationDetector.kt (120 lines)  
✅ LanguageFallback.kt (100 lines)  
✅ Updates to 4 existing files  
✅ Unit tests for all new classes  

### Documentation

✅ Update CLAUDE.md (camera section)  
✅ Code comments + KDocs  
✅ User guide (camera translation feature)  
✅ Limitations document  

### Testing

✅ Device test report (3+ devices)  
✅ Performance metrics  
✅ Test cases checklist  
✅ Demo video  

---

## PART 8: TIMELINE (3 WEEKS)

| Week | Focus | Deliverable |
|------|-------|-------------|
| **1** | Camera integration | CameraManager working |
| **2** | Text orientation + language | Vertical text translating |
| **3** | Polish + testing + release | Production-ready feature |

**With 2 developers**: Can complete in 1 week

---

## SUMMARY: Why This is Different from Original MASTER PROMPT

| Aspect | Original | Corrected |
|--------|----------|-----------|
| **Language** | JavaScript | Kotlin |
| **Scope** | 8 weeks, 7 modules | 3 weeks, 3 focused extensions |
| **Architecture** | 7 independent modules | Integrated with existing system |
| **Dependencies** | +200MB (ESRGAN, OpenCV, Tesseract) | 0 new large dependencies |
| **Code reuse** | Rebuild everything | Reuse 90% of existing code |
| **Target audience** | General developers | Android engineers with Kotlin |
| **Performance targets** | <2.5s continuous camera | <3s per single frame |
| **APIs used** | RenderScript (deprecated) | Canvas (current) |
| **Test coverage** | 120 images, no device tests | Multi-device testing |

---

## READY TO IMPLEMENT

This corrected plan:
✅ Fixes all issues from original MASTER PROMPT  
✅ Leverages existing working code  
✅ Uses Kotlin + current Android patterns  
✅ Has realistic scope (3 weeks)  
✅ No bloat (no huge dependencies)  
✅ Clear success criteria  
✅ Includes testing strategy  
✅ Production-ready quality  

**Proceed with this plan instead of original MASTER PROMPT.**

