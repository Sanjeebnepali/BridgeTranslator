# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**BridgeTranslator** is an Android application that provides seamless screen translation via a floating bubble. It captures the screen, detects text using ML Kit OCR, automatically translates it, and overlays translations directly over the original text positions.

**Core Tech Stack**: Kotlin, Android (min SDK 24, target SDK 34), Google ML Kit (OCR, translation, language ID), TensorFlow Lite, CameraX, Room database, Coroutines

## Build & Development Commands

### Prerequisites

- Android SDK (API 34)
- Kotlin 1.9.22
- Gradle 9.2.0+ (via wrapper)
- Local development environment: Create `local.properties` with SDK path if building locally

### Build Commands

```bash
# Build debug APK (test on device/emulator)
./gradlew assembleDebug

# Build release APK (optimized, requires signing config)
./gradlew assembleRelease

# Full build with connected tests
./gradlew build

# Clean build artifacts
./gradlew clean
```

### Running & Debugging

```bash
# Install debug APK on connected device/emulator
./gradlew installDebug

# Run app on device (requires device/emulator running)
./gradlew installDebug && adb shell am start -n com.bridge.translator/.ui.main.MainActivity

# Monitor app logs in real-time
adb logcat | grep "bridgetranslator\|BridgeTranslator"
```

### Testing

```bash
# Run all unit tests
./gradlew test

# Run specific test class
./gradlew test --tests com.bridge.translator.processing.BitmapTextEraserTest

# Run with coverage report
./gradlew test --tests <test-class> --debug

# Instrumented tests (requires emulator/device)
./gradlew connectedAndroidTest
```

**Test Structure**: Unit tests are in `app/src/test/`. Main test files include text processing, shape detection, and speech engine tests. Robolectric is configured for Android-specific unit tests.

## Architecture Overview

The project has **two active package structures**:

### Active Package: `com.bridge.translator.*`

The modern, polished translation engine and service-based architecture:

- **`engine/TranslationEngine.kt`** — Core ML Kit translation wrapper. Handles text recognition, language detection, and on-device translation. Used by all UI flows.
- **`service/FloatingBubbleService.kt`** — Main foreground service. Manages the floating bubble, screen capture, OCR, translation, and overlay display.
- **`service/ScreenCaptureManager.kt`** — Wraps Android MediaProjection API. Converts screen frames into bitmaps for processing.
- **`service/ScreenTranslationOverlay.kt`** — Full-screen overlay that places translated text views at original text positions. Touch-transparent to allow interaction with underlying app.
- **`service/TextEraseHelper.kt`** & **`processing/BitmapTextEraser.kt`** — Algorithms for erasing original text from screenshots and rendering clean backgrounds (solid color on simple backgrounds, translucent card on complex/textured backgrounds).
- **`processing/ScreenAnalyser.kt`** — Detects text blocks and estimates background complexity via color variance.
- **`processing/TranslationPipeline.kt`** — Orchestrates the full flow: capture → analyze → translate → display.
- **`overlay/OverlayRenderer.kt`** & **`overlay/OverlayManager.kt`** — Modern overlay rendering and management.
- **`ui/main/MainActivity.kt`** — App entry point. Handles overlay permission checks and bubble service lifecycle.
- **`ui/main/MainViewModel.kt`** — State management for main screen (service running state, target language).

**Key design**: The active flow is single-tap: user taps bubble → instant detach → screenshot → OCR/translate → overlay shown. Touch-down on overlay dismisses it.

### Legacy Package: `com.example.bridgetranslator.*`

Earlier implementation features (still useful, but not primary flow):

- **`HomeActivity.kt`** — Dashboard with cards for different translation modes.
- **`CameraActivity.kt`** — Live camera translation (CameraX + ML Kit OCR).
- **`TranslateActivity.kt`** — Manual text input/paste translation.
- **`HistoryActivity.kt`** — Saved translations (Room database).
- **`SettingsActivity.kt`** — User preferences.
- **`AppDatabase.kt`, `HistoryEntity.kt`, `HistoryDao.kt`** — Room database schema for translation history.
- **`FileTextExtractor.kt`** — Text extraction from images, PDFs, DOCX, PPTX (OCR or XML parsing).

These files remain in the codebase for reference and feature consolidation but are not part of the main entry flow defined in `AndroidManifest.xml`.

## Key Architectural Patterns

### Screen Translation Flow

1. **FloatingBubbleService** creates a draggable bubble using `WindowManager`.
2. On user tap: bubble detaches (not in screenshot), `ScreenCaptureManager` captures the screen.
3. `ScreenAnalyser` detects text blocks and bounding boxes via ML Kit OCR.
4. `TranslationEngine` auto-detects source language and translates each block.
5. `ScreenTranslationOverlay` places `TextView`s at original text positions.
6. Touch-down anywhere on overlay dismisses it (FLAG_NOT_FOCUSABLE, OnTouchListener intercepts ACTION_DOWN).

### Background Handling

- **Simple backgrounds**: Solid-color erase algorithm with feathered edges. Font auto-sized to fit bounding box.
- **Complex backgrounds (gradients/images)**: Fallback to translucent card (`#99000000`) if color variance > `COMPLEXITY_VARIANCE_THRESHOLD` (0.12f).

### ML Kit Integration

- **OCR**: Multi-script support (Korean, Chinese, Japanese, Devanagari, Latin).
- **Language detection**: Automatic source language ID with fallback to English.
- **Translation**: On-device models downloaded on-demand. No API keys exposed in APK.

### State & Preferences

- **Target language**: Saved in DataStore (on-device secure preference).
- **History**: Room database with HistoryEntity, HistoryDao, AppDatabase.
- **Language preferences**: LanguageManager (DataStore) for source/target languages.

## Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/bridge/translator/
│   │   │   ├── engine/                    # Translation engine
│   │   │   ├── service/                   # Foreground service, overlay, capture
│   │   │   ├── processing/                # OCR analysis, pipeline, erasers
│   │   │   ├── overlay/                   # Overlay rendering
│   │   │   ├── ui/main/                   # Main activity & viewmodel
│   │   │   ├── ui/overlay/                # Overlay activity (minimal)
│   │   │   ├── translation/               # Translation cache
│   │   │   ├── analysis/                  # Additional analysis utilities
│   │   │   ├── camera/                    # Camera-related features
│   │   │   ├── tts/                       # Speech synthesis
│   │   │   └── MyApplication.kt           # App class
│   │   ├── java/com/example/bridgetranslator/
│   │   │   └── (legacy UI flows)
│   │   ├── res/
│   │   │   ├── layout/                    # XML layouts
│   │   │   ├── drawable/                  # Icons, drawables
│   │   │   ├── values/                    # Colors, strings, themes
│   │   │   └── values-night/              # Dark mode colors
│   │   └── AndroidManifest.xml            # Permissions, components
│   ├── test/
│   │   └── java/                          # Unit tests (Robolectric, JUnit, Mockito)
│   └── androidTest/
│       └── (Instrumented tests - no test files yet)
└── build.gradle.kts                        # App-level build config
```

## Key Files & Navigation

| File | Purpose |
|------|---------|
| `app/build.gradle.kts` | Dependencies (ML Kit, CameraX, Room, Coroutines, TFLite) |
| `app/src/main/AndroidManifest.xml` | Permissions (SYSTEM_ALERT_WINDOW, CAMERA, RECORD_AUDIO, INTERNET, MANAGE_EXTERNAL_STORAGE), components |
| `com.bridge.translator.engine.TranslationEngine` | Core translation logic — start here to understand OCR/translate flow |
| `com.bridge.translator.service.FloatingBubbleService` | Main service entry point — orchestrates bubble, capture, and overlay |
| `com.bridge.translator.processing.TranslationPipeline` | Full processing chain from bitmap to translated overlay |
| `com.example.bridgetranslator.AppDatabase` | Room database for history — reference for legacy persistence |

## Important Conventions

### Permissions

- **SYSTEM_ALERT_WINDOW**: Required for floating bubble (`TYPE_APPLICATION_OVERLAY`).
- **CAMERA**: Used by CameraActivity (legacy) and camera translation features.
- **MANAGE_EXTERNAL_STORAGE**: File extraction on legacy flows.
- **RECORD_AUDIO**: TTS (SpeechEngine).
- **INTERNET**: ML Kit model downloads, optional cloud services.

### Threading

- **Coroutines**: Used for async translation and capture operations.
- **UI thread**: WindowManager and overlay operations must be on main thread.
- **Service**: FloatingBubbleService runs as a foreground service (required for screen capture on Android 10+).

### ML Kit Models

- Text recognition models are downloaded on-demand (automatically cached by ML Kit).
- Translation models are cached after download (first translation in a language may take 1-2 seconds).
- Language ID model is small (~100KB) and loads quickly.

### Touch & Input

- **Overlay**: Uses `FLAG_NOT_FOCUSABLE` + `FLAG_LAYOUT_IN_SCREEN` to span display but intercept touches.
- **Touch passthrough**: OnTouchListener intercepts ACTION_DOWN to dismiss overlay; other touches pass through to underlying app.

## Testing Strategy

- **Unit tests** (Robolectric): Bitmap erasure, shape detection, translation pipeline.
- **Mock-heavy tests**: ML Kit OCR/translation are mocked in unit tests; real models tested via manual testing on device.
- **Device testing**: Manual testing on physical device (emulator may lack proper overlay behavior).

Run specific test:
```bash
./gradlew test --tests com.bridge.translator.processing.BitmapTextEraserTest
```

## Common Development Tasks

### Adding a New Feature to Active Flow

1. **Service-side logic** → Add to FloatingBubbleService or create a new helper class in `service/`.
2. **Processing logic** → Add to TranslationPipeline or create a new class in `processing/`.
3. **UI changes** → Update MainActivity or overlay views in `ui/` or `overlay/`.
4. **Test coverage** → Add unit tests in `app/src/test/`.

### Modifying ML Kit Integration

- Edit `com.bridge.translator.engine.TranslationEngine` for OCR, language ID, translation logic.
- Update `app/build.gradle.kts` if adding new ML Kit modules (e.g., new language scripts).
- Remember to set `androidResources.noCompress += "tflite"` in build.gradle for TFLite models.

### Debugging Screen Capture

- Use `adb logcat` to monitor capture/overlay logs.
- Test on actual device (emulator's overlay behavior differs).
- Check `ScreenCaptureManager` for MediaProjection state and bitmap creation.

### Adding New Language Support

1. Update language list in `Language.kt` (if using legacy UI).
2. Update ML Kit dependencies in `build.gradle.kts` (e.g., `text-recognition-arabic`).
3. Test with `TranslationEngine.translate()` — ML Kit auto-downloads models on first use.

## Troubleshooting

| Issue | Check |
|-------|-------|
| Bubble doesn't appear | Overlay permission granted? Check `MainActivity.kt` permission request. |
| Screen capture fails | MediaProjection permission? Is service running as foreground service? Check `ScreenCaptureManager`. |
| Text not detected | Is ML Kit text recognition model downloaded? Check logcat for model load errors. |
| Translation slow | First translation downloads model (~40-50MB per language). Subsequent translations cached by ML Kit. |
| Overlay text overlaps original | Check `TextEraseHelper` and background complexity detection in `ScreenAnalyser`. |
| App crashes on Android 10+ | FloatingBubbleService must be foreground service with notification. Check manifest. |

## Resources & References

- **ProjectReport.md**: Detailed project history, architecture decisions, why ML Kit instead of Ollama.
- **Live_Camera_Translation_Feature_Design.md**: Camera feature spec (modular architecture, performance targets).
- **QuickReferenceGuide.md**: Live camera translation module overview.
- **README.md**: Conditional overlay rendering logic, seamless single-tap flow, touch passthrough.
