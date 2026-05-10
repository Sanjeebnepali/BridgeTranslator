# BridgeTranslator — Project Report (Summary)

## Overview

Android app for seamless screen translation via floating bubble. Captures screen, detects text using ML Kit OCR, translates, and overlays translations directly over original text positions.

**Tech**: Kotlin, Android (min SDK 24, target SDK 34), Google ML Kit (OCR/translation/language ID), TensorFlow Lite, CameraX, Room database, Coroutines

## Key Features Built

1. **Floating Translation Bubble** — Draggable, snaps to edge, tappable to start translation
2. **Screen Capture Flow** — MediaProjection API for screen capture via foreground service
3. **OCR Text Detection** — ML Kit multi-script support (Korean, Chinese, Japanese, etc.)
4. **Automatic Language Detection** — ML Kit Language Identification with English fallback
5. **On-Device Translation** — ML Kit on-device models, no API keys exposed
6. **Translation Overlay** — Full-screen transparent overlay with text views at original positions
7. **One-Time Language Picker** — Target language saved in SharedPreferences
8. **Legacy Features** — Camera translation, manual input, history, settings, file extraction

## Main Architecture

```
FloatingBubbleService (main)
  ├── ScreenCaptureManager (MediaProjection capture)
  ├── TranslationEngine (ML Kit wrapper)
  ├── ScreenAnalyser (OCR + language detection)
  ├── ScreenTranslationOverlay (overlay window)
  └── BitmapTextEraser (text rendering)
```

**Flow**: User taps bubble → capture screen → OCR detection → language ID → parallel translation → overlay display

## Two Package Structures

**Active (com.bridge.translator.*)**: Modern service-based architecture with FloatingBubbleService, TranslationEngine, overlay management

**Legacy (com.example.bridgetranslator.*)**: Home, Camera, Translate, History, Settings screens + Room database for history

## Technology Stack

| Area | Technology |
|------|-----------|
| Language | Kotlin |
| Platform | Android |
| UI | XML + ViewBinding |
| Services | MediaProjection, foreground service |
| OCR | ML Kit Text Recognition |
| Language ID | ML Kit Language Identification |
| Translation | ML Kit On-device Translation |
| Camera | CameraX |
| Storage | SharedPreferences, DataStore, Room |

## Why Google ML Kit (Not Ollama)

1. **On-device translation** — Models download once, run locally
2. **Android-native** — Direct integration with Android callbacks
3. **No exposed API keys** — Secure (no APK reverse-engineering risk)
4. **Lower latency** — Once model installed, fast per-block translation
5. **Integrated stack** — OCR + language ID + translation in one framework

Ollama is a desktop runtime, not designed for Android. Remote Ollama would require backend infrastructure, expose API keys, and drain battery/data.

## Project Cleanup Status

✅ Gradle DSL unified (Kotlin DSL only)
✅ Active vs legacy code separated
✅ Text encoding fixed
✅ Components registered intentionally
✅ Translation engine consolidated
✅ Model download UX improved
⏳ Tests still recommended (Gradle wrapper now available)

## Summary

Strongest feature: floating bubble screen-translation flow. Google ML Kit is the right foundation — Android-native, on-device, secure, integrated with OCR and language ID. Ollama should be optional backend enhancement, not core engine.
