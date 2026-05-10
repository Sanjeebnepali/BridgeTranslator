# BridgeTranslator — Language Direction Fix & True Passthrough (Summary)

## Three Bugs Fixed

### Bug 1: Wrong Translation Direction

**Problem**: User sets target=English, but English text translates TO Korean instead of FROM English

**Root cause**: Source/target flipped or language-skip check is wrong

**Fix in `TranslationEngine.translate()`**:
```kotlin
suspend fun translate(text: String, sourceLang: String, targetLang: String): String? {
    if (text.isBlank()) return text

    val src = sourceLang.lowercase().take(2)
    val tgt = targetLang.lowercase().take(2)

    // CRITICAL: never round-trip. If src == tgt, return original.
    if (src == tgt) return text

    // Fallback undetermined to Korean
    val effectiveSource = if (src.isBlank() || src == "un" || src == "und")
        TranslateLanguage.KOREAN else src

    if (effectiveSource == tgt) return text

    // ... translate effectiveSource → tgt ...
}
```

Also fix `ScreenAnalyser`: set `detectedLang` to valid 2-letter code (never empty, never target).

Add debug log: `Log.d(FLOW_TAG, "translate src=$effectiveSource tgt=$tgt textLen=${text.length}")`

### Bug 2 + 3: True Touch Passthrough (Not Intercepting Tap)

**Problem**: 
- Tap overlay → overlay dismisses → user taps AGAIN to click button
- Scroll → first scroll dismissed, second scroll needed
- Root: Overlay is FLAG_TOUCHABLE, Android latches gesture to first window; removing it mid-gesture cancels the gesture

**Architecture Fix: Stop Intercepting**

Make overlay completely non-touchable (FLAG_NOT_TOUCHABLE). User dismisses by tapping the **bubble** (already in SHOWING state with red ✕ icon). Same UX as Google Lens / Apple Live Text.

**`OverlayManager.kt`**:
```kotlin
private val staticFlags =
    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
    WindowManager.LayoutParams.FLAG_FULLSCREEN
```
Delete `setOnTouchListener` block. Overlay is visual-only.

**`FloatingBubbleService.kt`**:
```kotlin
private fun handleBubbleTap() {
    when (state) {
        BubbleState.IDLE -> startCaptureCycle()
        BubbleState.SHOWING -> dismissOverlay()
        BubbleState.CAPTURING -> { /* ignore */ }
    }
}

private fun dismissOverlay() {
    overlayManager.remove()
    state = BubbleState.IDLE
    updateBubbleVisual()
}
```

Delete:
- `overlayManager.onOverlayTap` wiring
- `TranslatorAccessibilityService.dispatchTap()` call
- All synthetic-touch injection logic
- Banking-aware gesture guards

**`TranslatorAccessibilityService.kt`**: Delete `dispatchTap()` method and `GestureDescription` imports.

**`accessibility_config.xml`**: Remove `android:canPerformGestures="true"` if added.

**Bubble visual must clearly show close icon in SHOWING**:
```kotlin
BubbleState.SHOWING -> {
    view.setImageResource(R.drawable.ic_close)
    view.background = circleDrawable(Color.parseColor("#E94560"))
    view.imageTintList = ColorStateList.valueOf(Color.WHITE)
    view.contentDescription = "Close translation"
}
```

**Optional polish**: Pulse bubble briefly when overlay first shows (scale 1 → 1.18 → 1 in 400ms) to draw user's eye to close affordance.

## Test Plan

1. Set target=English, tap Korean text → translates Korean → English ✓
2. Set target=Korean, tap English text → translates English → Korean ✓
3. Logcat shows "translate src=ko tgt=en" (correct direction)
4. Tap overlay → taps only **pass through** to underlying app (clicks work, scrolls continue)
5. Tap bubble (red ✕) → overlay dismisses
6. Only the bubble is a touch target; overlay is invisible to input

## Result

- No synthetic gesture replay needed
- Zero "tap twice" experience
- Scrolls, swipes, pinches all flow naturally to app
- User closes overlay by tapping bubble (tiny dedicated target)
- Translation direction always correct (source → user's target)
