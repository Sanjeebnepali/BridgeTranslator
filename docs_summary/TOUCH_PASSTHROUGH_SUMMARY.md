# BridgeTranslator — Touch Passthrough & Text Fit (Summary)

## Two Bugs Fixed

### Bug 1: Sequential Tap (Not Simultaneous)

**Problem**: User taps overlay → overlay dismisses → user taps same spot AGAIN to click the button

**Solution**: Use `AccessibilityService.dispatchGesture()` for synthetic touch injection
```
1. User taps overlay
2. overlay.instantHideAndDetach() (removeView synchronous)
3. dispatchGesture(tap at same x,y)
4. System delivers synthetic tap to app underneath
Total lag: ~30ms (imperceptible)
```

**Implementation**:
- `OverlayManager.onOverlayTap` callback captures tap coordinates
- `TranslatorAccessibilityService.dispatchTap(x, y)` injects synthetic gesture
- `FloatingBubbleService` wires callback to accessibility service
- **Banking safety**: Skip synthetic tap in BANKING_PACKAGES

**Manifest requirement**: `android:canPerformGestures="true"` in accessibility_config.xml

### Bug 2: Text Overflow & Misalignment

**Problem**: "Golf John Park, Hong..." became massive, cut off, overflowed. Inconsistent fonts, poor alignment.

**Solution**: Three-stage auto-fit algorithm in `BitmapTextEraser`

```
STEP 1: Sample background (median RGB from 4px ring outside bbox)
STEP 2: Sample text color (most distant pixels from bg)
STEP 3: Erase (fill box + 2px feather)
STEP 4: Choose natural font size = bbox.height × 65% (min 22px)

STEP 5: Fit — three strategies:
  5a) Single line at natural size → if fits, use it
  5b) Shrink font (to 70% min) → if fits, use it
  5c) Wrap to StaticLayout (max 4 lines, ellipsize)
      If wrapped height > bbox.height, expand erase rect downward

STEP 6: Alignment detection
  - Header (bold + tall + centered) → center
  - Right column (price/time) → right-align
  - Default → left-align

STEP 7: Vertical centering (single-line or StaticLayout)
```

**Constants**:
```kotlin
MIN_FONT_PX = 22f
MAX_FONT_PX_RATIO = 0.65f  // font ≤ 65% block height
OVERFLOW_SHRINK_LIMIT = 0.7f
MAX_LINES = 4
EDGE_FEATHER_PX = 2
```

**Helpers**:
- `sampleRingMedianColor(bitmap, rect): Int`
- `sampleTextColor(bitmap, rect, bgColor): Int`
- `colorDistance(c1, c2): Double`
- `relativeLuminance(c): Double`
- `detectAlignment(block, bbox, imageWidth): Layout.Alignment`

Use `IntArray + bitmap.getPixels()` (never nested `getPixel()` calls — slow).

## Test Plan

1. Korean app → tap bubble → translation overlay appears
2. Tap button on overlay → overlay vanishes instantly AND button is pressed (no second tap needed)
3. Dismiss feels instant (no fade lag)
4. Banking app (BANKING_PACKAGES) → no synthetic tap injected
5. User's reported screenshot (Albahein Country app):
   - "Golf John Park, Hong..." block fits its rectangle
   - Wrapped blocks stay ≤4 lines
   - Headers center-aligned, prices right-aligned
   - No overlapping text
   - No solid card/transparency leak
   - Consistent Sans-Serif font

## Acceptance

User says all three:
- "When I tap, my click goes through normally"
- "Translation looks like it belongs on page, not stuck on top"
- "I can read every block — nothing cut off or oversized"
