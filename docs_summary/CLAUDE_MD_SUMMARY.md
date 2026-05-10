# CLAUDE.md Summary

Complete developer guidance for BridgeTranslator. See full CLAUDE.md in root for details.

## Build Commands

```bash
./gradlew assembleDebug          # Build debug APK
./gradlew assembleRelease        # Build release APK
./gradlew installDebug           # Install on device
./gradlew test                   # Run unit tests
./gradlew test --tests <class>   # Run specific test
```

## Project Overview

Android app: floating bubble → screen capture → OCR → translate → overlay display

**Stack**: Kotlin, Android 24-34, ML Kit, TensorFlow Lite, CameraX, Room, Coroutines

## Two Package Structures

**Active (com.bridge.translator.*)**:
- FloatingBubbleService, ScreenCaptureManager, TranslationEngine
- ScreenAnalyser, ScreenTranslationOverlay, BitmapTextEraser
- OverlayManager, MainActivity, MainViewModel

**Legacy (com.example.bridgetranslator.*)**:
- HomeActivity, CameraActivity, TranslateActivity, HistoryActivity, SettingsActivity
- AppDatabase, HistoryEntity, HistoryDao, FileTextExtractor
- Older FloatingBubbleService, overlay implementations

## Screen Translation Flow

```
1. FloatingBubbleService creates draggable bubble
2. User taps bubble → ScreenCaptureManager captures screen
3. ScreenAnalyser OCR detects text blocks + language
4. TranslationEngine translates each block (parallel)
5. ScreenTranslationOverlay places TextViews at original positions
6. Overlay is touch-transparent (FLAG_NOT_FOCUSABLE)
7. User touches to dismiss, interacts with app below
```

## Background Handling

- **Simple**: Solid-color erase with 2px feathered edges
- **Complex** (gradients/images): Translucent card `#99000000`
- Detection: color variance threshold (0.12f)

## Permissions

- `SYSTEM_ALERT_WINDOW` (bubble)
- `CAMERA` (legacy camera flow)
- `MANAGE_EXTERNAL_STORAGE` (file extraction)
- `RECORD_AUDIO` (TTS)
- `INTERNET` (ML Kit models)

## State & Storage

- **Target language**: DataStore (secure on-device)
- **History**: Room database
- **Language prefs**: LanguageManager (DataStore)

## Key Files

| File | Purpose |
|------|---------|
| `engine/TranslationEngine.kt` | ML Kit OCR, language ID, translation |
| `service/FloatingBubbleService.kt` | Main service, orchestration |
| `service/ScreenCaptureManager.kt` | MediaProjection capture |
| `service/ScreenTranslationOverlay.kt` | Full-screen overlay |
| `processing/ScreenAnalyser.kt` | OCR + language detection |
| `processing/BitmapTextEraser.kt` | Text rendering algorithm |
| `overlay/OverlayManager.kt` | Overlay window management |

## Threading

- **Coroutines**: Async translation, capture
- **UI thread**: WindowManager + overlay operations (main thread only)
- **Service**: FloatingBubbleService = foreground service (required for Android 10+)

## ML Kit Notes

- **OCR models**: Downloaded on-demand, cached automatically
- **Translation models**: ~100-500MB per language pair, cached after download
- **Language ID**: Small model, loads quickly
- **No API keys**: On-device, no reverse-engineer risk

## Testing

- **Unit tests**: Robolectric (bitmap erasure, shape detection, pipeline)
- **Mock ML Kit**: Unit tests mock OCR/translation (real models tested manually)
- **Device testing**: Manual on physical device (emulator overlay behavior differs)

## Development Tasks

**Adding features**: Edit service/ for backend, ui/ for UI, processing/ for logic  
**Modifying ML Kit**: Edit TranslationEngine, update build.gradle.kts for new modules  
**Debugging capture**: Use `adb logcat`, test on real device, check ScreenCaptureManager  
**Adding languages**: Update Language.kt, add ML Kit dependency, test with TranslationEngine  

## Common Issues

| Issue | Check |
|-------|-------|
| Bubble doesn't appear | Overlay permission in MainActivity |
| Capture fails | MediaProjection permission, foreground service |
| Text not detected | ML Kit model download (logcat) |
| Slow translation | First translation downloads model (~100-500ms) |
| Text overlaps | TextEraseHelper + ScreenAnalyser background detection |
| Crashes on Android 10+ | FloatingBubbleService is foreground with notification |

## References

- **PROJECT_REPORT.md**: Architecture decisions, tech stack, why ML Kit
- **SESSION_REPORT.md**: Performance optimizations, session changes
- **BUG_ANALYSIS_AND_FIX_PROMPT.md**: Motion detection, context tracking
- **REDESIGN_MANUAL_TRIGGER.md**: Manual-trigger architecture
- **PROMPT_TOUCH_PASSTHROUGH_AND_TEXT_FIT.md**: Text rendering quality
- **PROMPT_LANG_DIRECTION_AND_TRUE_PASSTHROUGH.md**: Translation direction, true passthrough
- **Live_Camera_Translation_Feature_Design.md**: Camera feature spec
- **QUICK_REFERENCE_GUIDE.md**: Module overview, algorithms
- **MASTER_AGENT_PROMPT.md**: Complete live camera system design
