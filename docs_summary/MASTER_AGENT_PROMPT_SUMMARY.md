# Master Agent Prompt: Live Camera Translation (Summary)

## Mission

Build production-grade live camera translation system that improves on Google Lens. Handle text in multiple orientations, small fonts, various distances, real-time user feedback.

**Targets**:
- Vertical text: 85-90% accuracy (vs. Google Lens 30%)
- Small text (<8pt): 80-85% accuracy (vs. 40-50%)
- UI overlaps: <5% (vs. current 20%+)
- Distance: 20cm-2m (vs. 20cm-1.5m)
- All text orientations: supported
- End-to-end latency: <2.5s

## Problem Analysis

### Issue 1: Text Orientation Failure
- Horizontal: Works
- Vertical (90°/270°): ~30% success (needs 85%+)
- Rotated: Fails completely
- Mixed: UI chaos
- **Impact**: 40-60% of product labels are vertical

### Issue 2: Small Text Accuracy
- <8pt font: >25% error rate
- Nutrition facts (4-6pt): 40-50% accuracy
- Ingredient lists (6-8pt): 60-70% accuracy
- **Root**: ML Kit OCR struggles without enhancement

### Issue 3: UI/UX Layout
- Banners extend beyond screen
- Text overlaps original
- Poor spacing/alignment
- Small text becomes obscured

### Issue 4: Shape & Distance Limits
- Only rectangular objects
- No 3D surface mapping
- Optimal: 20-50cm; fails <20cm or >1.5m

### Issue 5: Language Detection
- Short text fails (no context)
- Brand names misidentified
- Single words: low confidence
- Mixed language: wrong primary

### Issue 6: User Guidance
- No capture quality feedback
- No distance indicators
- No "move closer/back" suggestions
- No distance estimation

## 7-Module Architecture

```
Input: Camera Frame
  ↓ Preprocessing (auto-rotate, enhance, ROI)
  ↓ [1] Text Detection (ML Kit OCR, 300ms)
  ↓ [2] Orientation Detection (50ms)
  ↓ [3] Language Detection (100ms)
  ↓ [4] Small Text Enhancement (300ms if triggered)
  ↓ [5] Batch Translation (1000ms typical)
  ↓ [6] Banner Rendering (100ms)
  ↓ [7] Distance & Quality (100ms)
  ↓ Display

TOTAL TARGET: <2.5 seconds
```

## Technology Stack

| Component | Tech | Why |
|-----------|------|-----|
| Text Detection | ML Kit Vision | Real-time, on-device |
| Orientation | OpenCV + TensorFlow | Precise angle calculation |
| Language ID | ML Kit Language ID (uselangid: true) | 50+ languages, 95%+ |
| Small Text | TensorFlow ESRGAN + Tesseract | Better tiny-font accuracy |
| Translation | Google Cloud Translation (batch) | Lower latency than sequential |
| Object Detection | ML Kit Object Detection | Shape context |
| Rendering | Custom Canvas | Performance-optimized |

## Module Specifications (TL;DR)

### [1] Orientation Detection
- Input: ML Kit text blocks
- Algorithm: angle = atan2(Δy, Δx), classify by bucket
- Output: text + angle + class (H/V/R) + confidence
- Accuracy target: >95%, <100ms

### [2] Language Detection with Fallback
- Primary: ML Kit Language ID (confidence ≥0.6)
- Fallback 1: Character set analysis (Hangul, CJK, etc.)
- Fallback 2: Dictionary lookup (500 words per language)
- Hybrid scoring: 60% char + 40% dict
- Accuracy target: >90%, <50ms

### [3] Small Text Enhancement
- Upscale 2-3× (ESRGAN)
- Denoise (bilateral filter)
- Re-OCR (ML Kit + Tesseract parallel)
- Accuracy target: >85% on <8pt, <300ms

### [4] Batch Translation
- Group by language
- Check cache (30-day)
- Parallel API calls
- Retry with backoff
- Latency target: 1000ms (batched)

### [5] Smart Banner Positioning
- Priority: below > above > side > popup
- Collision detection
- Alignment: left/center/right
- Success target: >98% no overlap

### [6] Distance & Quality
- Distance: ~6.6cm can reference, atan focal length
- Quality score: weighted average of OCR/sharpness/distance/contrast/stability
- Accuracy target: ±10cm, <100ms

### [7] System Integration
- Orchestrator manages pipeline
- Unit tests per module
- Integration tests
- 120-image test suite
- User testing (5+ users)

## Performance Targets

| Metric | Target | Measurement |
|--------|--------|------------|
| End-to-end latency | <2.5s | Timer capture to screen |
| OCR accuracy | ≥95% | Character-level comparison |
| Language detection | ≥90% | vs. known labels |
| Banner placement | ≥98% | No overlaps |
| Distance accuracy | ±10cm | Measured vs. estimated |
| Memory | <200MB peak | Android Profiler |
| Battery | <15%/min | Full-drain test |
| Startup | <1s | First translation display |

## Success Criteria

Real user runs app 5 minutes:
1. Never warm device
2. No log spam between taps
3. Overlay only on deliberate tap
4. Instant tap-to-dismiss
5. Translated text clean, native-looking

## Development Timeline

- **Pre-Dev** (1 week): Module design, API setup, environment
- **Development** (6-8 weeks): 1-2 days per module
- **Testing** (2 weeks): Unit, integration, performance, user testing
- **Deployment** (1 week): Review, security audit, monitoring

**Estimate**: 8-10 weeks with 2-3 developers

## File Structure

```
src/
  ├── modules/
  │   ├── [1] orientation_detector
  │   ├── [2] language_detector
  │   ├── [3] small_text_enhancer
  │   ├── [4] translator (batch)
  │   ├── [5] banner_renderer
  │   ├── [6] distance_estimator
  │   └── [7] system_integrator
  ├── utils/ (cache, error, logger, config)
  └── main (orchestrator)
test/ (unit, integration, performance, 120-image suite)
docs/ (architecture, api, troubleshooting)
```
