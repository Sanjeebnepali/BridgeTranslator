# BridgeTranslator — Bug Analysis & Fix Plan (Summary)

## User Complaints → Root Causes

| Complaint | Root Cause | Impact |
|-----------|-----------|--------|
| "Bubble on home screen" | Bubble window always attached, never detached | Overlay appears over launcher |
| "Overlay lingers, stale" | Unsupported-screen signal only hides overlay, not a hard gate | Old translations remain visible |
| "Slow post-gesture lag" | 800ms touch debounce + 120ms capture + 500-1000ms OCR + 200ms translate = 1.5-2s | Feels sluggish |
| "Retranslates every frame" | Hash dedup fails on video/animation (pixels change every frame) | Overlay flickers, device heats |

## Solution: 4-Layer Architecture

### Layer 1 — Context Tracking
Add `ForegroundAppTracker` to FloatingBubbleService tracking current package, supported status, sensitive status.

### Layer 2 — Bubble Visibility to Context
- Hide bubble in launchers, system UI, video calls, banking apps, lock screen
- Re-attach when user enters supported app
- Prevents activation in unsupported contexts

### Layer 3 — Motion-Aware Translation Gating (Most Important)
```
1. Capture frame A → hash A
2. Wait 250ms
3. Capture frame B → hash B
4. If hash A != hash B → motion detected
   - Don't translate
   - Schedule retry with backoff (500ms → 1.5s → 4s → pause)
5. If hash A == hash B → stable, do OCR + translate
```
**Result**: Videos, animations, ads → no translation until stable. Single biggest UX improvement.

### Layer 4 — Reduced & Smarter Cycling
- Increase idle poll from 10s to 15s (or disable in motion-paused state)
- Only retranslate if hash changes
- Adaptive debounce: 500ms tap, 800ms scroll, 1.2s after motion

## Why This Approach

| Alternative | Why Rejected |
|-----------|------------|
| Aggressive overlay hiding | Treats symptom, not cause |
| Whitelist supported apps | Brittle, punishes new apps |
| Manual-trigger only | Defeats auto-translation value |
| TYPE_WINDOW_CONTENT_CHANGED for motion | False positives/negatives; pixel hash is ground truth |

**Precedent**: Same as Google Translate camera mode, Microsoft overlay translator.

## Acceptance Criteria

1. Bubble never appears on launcher/system UI/video-calls/banking
2. Video playing → overlay appears once, NOT repeated refresh
3. Tap/scroll → overlay disappears <50ms, reappears <1s
4. Device doesn't get warm during 5min scrolling
5. CPU usage ~0 when no translatable app in foreground

## Implementation Files to Change

- `service/FloatingBubbleService.kt` — context fields, enter/leave-mode, motion-gating in runCycle, idle-poll guard
- `service/TranslatorAccessibilityService.kt` — single `updateForegroundContext()` call
- `overlay/OverlayManager.kt` — helper `isAttached()` method
