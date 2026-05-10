# BridgeTranslator

A seamlessly integrated screen translation application for Android.

## Conditional Overlay Rendering & Seamless Translation Flow

### Conditional Background Handling
The application dynamically detects the complexity of the background immediately surrounding translated text blocks.
- **Simple Backgrounds:** Uses a solid-color erase algorithm (with a 2-pixel feathered edge) and automatically sizes the font to fit the bounding box naturally.
- **Complex Backgrounds (Gradients/Images):** If the color variance of the surrounding 4-pixel ring exceeds the `COMPLEXITY_VARIANCE_THRESHOLD` (0.12f), the app falls back to a translucent card style (`#99000000`). This ensures perfect readability over textured backgrounds without sacrificing the clean aesthetic on simple backgrounds.

### Seamless Single-Tap Flow
Translation is executed via a true single-tap architecture:
1. Tap the bubble.
2. The bubble instantly detaches (so it doesn't appear in the screenshot).
3. The screen is captured, processed, and translated.
4. The translation overlay is shown immediately, and the bubble transforms into a dismiss button (`SHOWING` state).
No intermediate taps or actions are required from the user.

### Touch Passthrough & Dismissal
The overlay actively intercepts the very first touch (`ACTION_DOWN`) to dismiss itself. It uses `FLAG_NOT_FOCUSABLE` and `FLAG_LAYOUT_IN_SCREEN` to span the display but relies on a targeted `OnTouchListener` to ensure it hides instantly when the user taps anywhere on the screen, smoothly getting out of the way for the underlying app.
