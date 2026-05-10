# Integration Guide: 3 New Modules into BridgeTranslator

**Status**: Ready to integrate CameraManager, TextOrientationDetector, LanguageFallback into existing system

---

## PART 1: Architecture Overview

### Current System (Existing)

```
FloatingBubbleService (main orchestrator)
  ├── ScreenCaptureManager (screen capture via MediaProjection)
  ├── ScreenAnalyser (ML Kit OCR + language detection)
  ├── TranslationEngine (ML Kit translation)
  ├── BitmapTextEraser (text rendering)
  └── OverlayManager (full-screen overlay)
```

### Extended System (With Camera)

```
FloatingBubbleService (ENHANCED - add camera mode)
  ├── Capture Manager Selection
  │   ├── ScreenCaptureManager (EXISTING - unchanged)
  │   └── CameraManager (NEW - camera input)
  │
  ├── Frame Processing (SAME for both modes)
  │   ├── TextOrientationDetector (NEW - rotate vertical text)
  │   ├── ScreenAnalyser (ENHANCED - use fallback language detection)
  │   ├── TranslationEngine (EXISTING - no changes)
  │   └── BitmapTextEraser (EXISTING - no changes)
  │
  └── Display (SAME for both modes)
      └── OverlayManager (EXISTING - no changes)
```

**Key Insight**: Camera produces Bitmap just like screen capture. Entire pipeline after that works unchanged.

---

## PART 2: Integration Points

### 1. FloatingBubbleService Changes

**File**: `app/src/main/java/com/bridge/translator/service/FloatingBubbleService.kt`

**Changes needed**:
```kotlin
// Add camera manager field
private var cameraManager: CameraManager? = null
private var captureMode: CaptureMode = CaptureMode.SCREEN

// Add camera manager to lifecycle
override fun onCreate() {
    super.onCreate()
    cameraManager = CameraManager(this, previewView = null)  // No preview for now
    lifecycle.addObserver(cameraManager!!)
}

// Modify startCaptureCycle to support both modes
private fun startCaptureCycle() {
    val bitmap = when (captureMode) {
        CaptureMode.SCREEN -> screenCaptureManager.captureFrame()
        CaptureMode.CAMERA -> cameraManager?.captureFrame()  // NEW
    }
    // Rest of pipeline continues unchanged
}

enum class CaptureMode {
    SCREEN, CAMERA
}
```

**Lines to modify**: ~20 lines (add fields, enhance onCreate, enhance startCaptureCycle)

### 2. ScreenAnalyser Changes

**File**: `app/src/main/java/com/bridge/translator/processing/ScreenAnalyser.kt`

**Changes needed**:
```kotlin
// Add orientation detector and language fallback
private val orientationDetector = TextOrientationDetector()
private val languageFallback = LanguageFallback()

// Enhance analyzeFrame to use orientation detection
suspend fun analyzeFrame(bitmap: Bitmap): List<TextBlock> {
    val textBlocks = mutableListOf<TextBlock>()
    
    for (detectedBlock in recognizer.recognize(bitmap)) {
        // NEW: Check orientation and rotate if needed
        val (rotatedBitmap, rotationAngle) = orientationDetector.rotateIfNeeded(bitmap, detectedBlock)
        
        // If rotated, re-recognize on rotated bitmap
        val finalText = if (rotationAngle != 0f) {
            // Re-recognize on rotated bitmap
            recognizer.recognize(rotatedBitmap).firstOrNull()?.text ?: detectedBlock.text
        } else {
            detectedBlock.text
        }
        
        // NEW: Use fallback language detection
        val detectedLang = detectLanguageWithFallback(finalText)
        
        textBlocks.add(TextBlock(...))
    }
    return textBlocks
}

// NEW: Add fallback language detection method
private suspend fun detectLanguageWithFallback(text: String): String {
    return languageFallback.detectLanguageWithFallback(text)
}
```

**Lines to modify**: ~30 lines (add fields, enhance loop, add method)

### 3. MainActivity Changes

**File**: `app/src/main/java/com/bridge/translator/ui/main/MainActivity.kt`

**Changes needed**:
```kotlin
// Add camera permission check
private fun checkCameraPermission() {
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) 
        != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_CODE
        )
    }
}

// Add camera/screen mode selector
private fun setupCaptureModSelector() {
    val screenBtn = findViewById<Button>(R.id.screenModeBtn)
    val cameraBtn = findViewById<Button>(R.id.cameraModeBtn)
    
    screenBtn.setOnClickListener {
        viewModel.setCaptureMode(CaptureMode.SCREEN)
    }
    cameraBtn.setOnClickListener {
        viewModel.setCaptureMode(CaptureMode.CAMERA)
    }
}

// Call in onCreate
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    checkCameraPermission()  // NEW
    setupCaptureModSelector()  // NEW
}
```

**Lines to modify**: ~25 lines (add permission check, add mode selector)

### 4. MainViewModel Changes

**File**: `app/src/main/java/com/bridge/translator/ui/main/MainViewModel.kt`

**Changes needed**:
```kotlin
// Add capture mode tracking
private val _captureMode = MutableStateFlow<CaptureMode>(CaptureMode.SCREEN)
val captureMode: StateFlow<CaptureMode> = _captureMode.asStateFlow()

// Add setter
fun setCaptureMode(mode: CaptureMode) {
    _captureMode.value = mode
    // Save to DataStore
    viewModelScope.launch {
        dataStore.edit { preferences ->
            preferences[CAPTURE_MODE_KEY] = mode.name
        }
    }
}

// Define capture mode enum (can also go in FloatingBubbleService)
enum class CaptureMode {
    SCREEN, CAMERA
}
```

**Lines to modify**: ~15 lines (add fields, add setter, add DataStore key)

### 5. AndroidManifest Changes

**File**: `app/src/main/AndroidManifest.xml`

**Changes needed**:
```xml
<!-- Add inside <manifest> tag, before <application> -->
<uses-permission android:name="android.permission.CAMERA" />
<uses-feature android:name="android.hardware.camera" android:required="false" />
```

**Lines to add**: 2 lines (CAMERA permission)

### 6. Layout XML Changes

**File**: `app/src/main/res/layout/activity_main.xml`

**Changes needed**:
```xml
<!-- Add mode selector after existing button group -->
<LinearLayout
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="horizontal"
    android:gravity="center"
    android:paddingTop="16dp">
    
    <Button
        android:id="@+id/screenModeBtn"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_weight="1"
        android:text="Screen"
        style="@style/Widget.MaterialComponents.Button.OutlinedButton" />
    
    <Button
        android:id="@+id/cameraModeBtn"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_weight="1"
        android:text="Camera"
        android:layout_marginStart="8dp"
        style="@style/Widget.MaterialComponents.Button.OutlinedButton" />
</LinearLayout>
```

**Lines to add**: ~18 lines (button group)

---

## PART 3: Data Flow - How It Works

### Screen Translation Flow (Existing)
```
User taps bubble (IDLE state)
  ↓
FloatingBubbleService.startCaptureCycle()
  ↓
ScreenCaptureManager.captureFrame()  ← MediaProjection screenshot
  ↓
ScreenAnalyser.analyze()
  - ML Kit OCR
  - Language detection (NEW: with fallback)
  ↓
TranslationEngine.translate() [parallel]
  ↓
BitmapTextEraser.render()
  ↓
OverlayManager.show()
  ↓
Overlay displayed, bubble → SHOWING state
```

### Camera Translation Flow (New)
```
User selects "Camera" mode, taps bubble (IDLE state)
  ↓
FloatingBubbleService.startCaptureCycle()
  ↓
CameraManager.captureFrame()  ← Camera frame capture
  ↓
ScreenAnalyser.analyze()
  - TextOrientationDetector (NEW: rotate if vertical)
  - ML Kit OCR
  - Language detection (NEW: with fallback)
  ↓
TranslationEngine.translate() [parallel]  ← SAME CODE
  ↓
BitmapTextEraser.render()  ← SAME CODE
  ↓
OverlayManager.show()  ← SAME CODE
  ↓
Overlay displayed, bubble → SHOWING state
```

**Key**: Everything after capture is identical for both modes!

---

## PART 4: Module Usage Examples

### CameraManager Usage

```kotlin
// In FloatingBubbleService
private val cameraManager = CameraManager(this)
lifecycle.addObserver(cameraManager)

// Capture a frame
val bitmap = cameraManager.captureFrame()  // Returns Bitmap or null
```

### TextOrientationDetector Usage

```kotlin
// In ScreenAnalyser.analyze()
val orientationDetector = TextOrientationDetector()

for (textBlock in recognizedText) {
    // Check orientation
    val orientation = orientationDetector.detectOrientation(textBlock)
    
    // Rotate if vertical
    val (rotatedBitmap, angle) = orientationDetector.rotateIfNeeded(bitmap, textBlock)
    
    // Get full result with confidence
    val result = orientationDetector.detectFull(bitmap, textBlock)
}
```

### LanguageFallback Usage

```kotlin
// In ScreenAnalyser.analyze()
private val languageFallback = LanguageFallback()

val language = languageFallback.detectLanguageWithFallback(text)

// Or just character set detection (faster)
val (lang, confidence) = languageFallback.detectByCharacterSet(text)

// Or dictionary detection
val (lang, confidence) = languageFallback.detectByDictionary(text)
```

---

## PART 5: Testing Integration

### Unit Tests

```kotlin
// Test CameraManager initialization
fun testCameraManagerInitialization() {
    val manager = CameraManager(context)
    assertTrue(manager.isInitialized)
}

// Test TextOrientationDetector
fun testVerticalTextDetection() {
    val detector = TextOrientationDetector()
    val orientation = detector.detectOrientation(verticalTextBlock)
    assertEquals(Orientation.VERTICAL, orientation)
}

// Test LanguageFallback
fun testKoreanDetection() {
    val fallback = LanguageFallback()
    val lang = fallback.detectLanguageWithFallback("안녕하세요")
    assertEquals("ko", lang)
}
```

### Integration Tests

```kotlin
// Full pipeline test
fun testScreenToCamera ModeSwitch() {
    // Start in SCREEN mode
    assertEquals(CaptureMode.SCREEN, viewModel.captureMode.value)
    
    // Switch to CAMERA mode
    viewModel.setCaptureMode(CaptureMode.CAMERA)
    assertEquals(CaptureMode.CAMERA, viewModel.captureMode.value)
    
    // Verify both pipelines work
}

// Test orientation handling in full pipeline
fun testVerticalTextTranslation() {
    // Capture vertical text image
    // Process through analyzer
    // Verify text is rotated and recognized
    // Verify translation overlay is correct
}
```

---

## PART 6: Migration Checklist

Before integrating, ensure:

### Code Review
- [ ] Review CameraManager.kt (320 lines)
- [ ] Review TextOrientationDetector.kt (380 lines)
- [ ] Review LanguageFallback.kt (380 lines)
- [ ] Review 6 files to modify (identified above)

### Dependencies
- [ ] CameraX already in build.gradle ✅
- [ ] ML Kit already in build.gradle ✅
- [ ] Kotlin Coroutines already in build.gradle ✅
- [ ] No new large dependencies needed ✅

### Permissions
- [ ] Add CAMERA permission to Manifest
- [ ] Add camera permission check in MainActivity
- [ ] Test permission grant/deny flows

### UI
- [ ] Add camera/screen mode selector to activity_main.xml
- [ ] Test button visibility and interaction
- [ ] Verify responsive layout on different screen sizes

### Testing
- [ ] Unit test each new class
- [ ] Integration test full pipeline
- [ ] Test on 3 devices (low/mid/high end)
- [ ] Test permission flows
- [ ] Test mode switching
- [ ] Verify no regressions in screen translation

### Documentation
- [ ] Update CLAUDE.md with camera feature
- [ ] Add code comments to modifications
- [ ] Update user guide

---

## PART 7: Rollback Plan

If issues arise:

1. **Remove CameraManager** — Delete file, remove from FloatingBubbleService
2. **Remove TextOrientationDetector** — Delete file, revert ScreenAnalyser
3. **Remove LanguageFallback** — Delete file, revert ScreenAnalyser
4. **Revert Manifest** — Remove CAMERA permission
5. **Revert Layouts** — Remove mode selector buttons
6. **Revert ViewModel** — Remove captureMode state

All changes are isolated and removable. Screen translation will continue working.

---

## SUMMARY

**Files Created**: 3 new classes (1,080 lines total)
**Files Modified**: 6 existing files (110 lines added)
**New Dependencies**: 0
**Lines of Integration Code**: ~110
**Risk Level**: Low (isolated changes, no core modifications)
**Rollback Complexity**: Easy (all changes are additive)

**Ready to proceed with code modifications?** ✅
