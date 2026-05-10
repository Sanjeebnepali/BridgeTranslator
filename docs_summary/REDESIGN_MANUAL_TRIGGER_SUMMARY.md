# BridgeTranslator — Manual-Trigger Redesign (Summary)

## Verdict: Manual Trigger is Better

**Current**: Auto-translate on scroll/touch/page-change (complex, unreliable)  
**Proposed**: Single-tap manual trigger (simple, predictable)

## Why Manual Wins

| Concern | Auto | Manual |
|---------|------|--------|
| User control | App decides | User decides |
| Predictability | Hidden heuristics | One tap = one translation |
| Battery/CPU | Background polling | Zero work between taps |
| Video/animation | Fights motion, flickers | User pauses, taps, done |
| Complexity | High (debounce, motion gate, idle poll) | Low (one entry point) |
| Reliability | Accessibility-dependent | User gesture always works |

**Precedent**: Google Lens, Apple Live Text, Microsoft Translator — all use manual trigger.

## Four-Layer Architecture

### Layer 1 — Bubble State Machine

| State | Visual | Behavior |
|-------|--------|----------|
| IDLE | Grey circle | Tap → start capture |
| CAPTURING | Blue spinner | Bubble hidden, OCR+translate running |
| SHOWING | Red ✕ | Tap → dismiss overlay |

### Layer 2 — Single Capture Pipeline

```
User taps bubble (IDLE)
  ↓ Detach bubble
  ↓ delay 80ms
  ↓ captureFrame()
  ↓ Re-attach (CAPTURING state + spinner)
  ↓ OCR (parallel language detection)
  ↓ Translate all blocks (parallel)
  ↓ BitmapTextEraser (clean erase + redraw)
  ↓ Attach overlay
  ↓ Bubble switches to SHOWING (red ✕)
```

Tap overlay → overlay dismisses → bubble back to IDLE.

### Layer 3 — Clean Text Rendering (Erase + Redraw)

1. **Sample background** — 4-pixel ring outside box → median RGB
2. **Sample text color** — most distant pixels from background
3. **Erase** — fill box with background color + 2px feather
4. **Auto-fit** — single-line at natural size; if overflow ≤30%, shrink; if more, wrap to 4 lines
5. **Alignment** — detect (left/center/right) based on margins
6. **Render** — draw translated text in sampled color

**Result**: No translucent leakage, native-looking, clean layouts.

### Layer 4 — Long-Press Cancel Zone (Existing)

Hold 500ms → red ✕ at bottom-center. Drag bubble over it → service stops.

## What the User Trades

**Gives up**: Zero-effort auto-translate (must tap per screen)  
**Gets back**: Reliability, zero flicker, no battery drain, clean translations, familiar UX (Google Lens model)

## Files to Modify

- `service/FloatingBubbleService.kt` — major rewrite (state machine, single pipeline)
- `overlay/OverlayManager.kt` — remove touch intercept, just show/dismiss
- `service/TranslatorAccessibilityService.kt` — strip down (optional context tracking)
- `processing/BitmapTextEraser.kt` — full rewrite (erase + redraw algorithm)

## Acceptance

User runs app 5 minutes:
- Phone never gets warm
- No log spam between taps
- Overlay only after deliberate tap
- Dismissed instantly by tapping overlay/bubble
- Text sits directly on page color, no transparency leak
