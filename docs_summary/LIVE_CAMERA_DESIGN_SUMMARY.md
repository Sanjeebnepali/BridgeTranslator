# Live Camera Translation Feature Design (Summary)

## Current Problems (Google Lens Reference)

| Issue | Impact |
|-------|--------|
| Vertical text (90°, 270°) | ~30% accuracy (target: 85%) |
| Small text (<8pt) | 40-50% accuracy (target: 85%) |
| UI layout | Overlaps, clutter |
| Distance support | 20cm-1.5m optimal (target: 20cm-2m) |
| Mixed orientations | Fails |

## Solution: 7-Module Pipeline

```
Camera Frame
  ↓ Preprocessing (auto-rotate, contrast, ROI)
  ↓ [1] Text Detection (ML Kit OCR)
  ↓ [2] Orientation Detection (horizontal/vertical/rotated)
  ↓ [3] Language Detection (primary: ML Kit ID; fallback: char set + dict)
  ↓ [4] Small Text Enhancement (upscale 2-3x if <8pt)
  ↓ [5] Batch Translation (Google API, parallel, cached)
  ↓ [6] Smart Banner Positioning (avoid overlap, fit text)
  ↓ [7] Distance & Quality Feedback (estimate distance, quality score)
  ↓ Display (translated text + distance indicator)
```

## Module Capabilities

| Module | Input | Output | Latency | Accuracy |
|--------|-------|--------|---------|----------|
| Orientation | Text blocks | Angle + class | 50ms | 95% |
| Language ID | Text | Language code | 100ms | 90% |
| Small Text | Blocks <8pt | Enhanced OCR | 300ms | 85% |
| Translation | Text + lang | Translated | 1000ms | 90% |
| Positioning | Text + banners | Positioned UI | 100ms | 98% |
| Distance | Frame | Distance + quality | 100ms | ±10cm |
| Integration | All above | Complete system | <2500ms | >85% |

## Optimal Detection Range

```
<20cm: RED (distorted)
20-100cm: GREEN (optimal, 85-95% accuracy)
100-150cm: YELLOW (80-90%)
150-200cm: ORANGE (70-80%)
>200cm: RED (too far, <70%)
```

## Tech Stack

- **Text Detection**: ML Kit Vision (on-device)
- **Orientation**: OpenCV + TensorFlow (angle calculation)
- **Language ID**: ML Kit Language ID (50+ languages, 95%+ accuracy)
- **Small Text**: TensorFlow ESRGAN (upscale) + Tesseract OCR (fallback)
- **Translation**: Google Cloud Translation API (batch mode)
- **Object Detection**: ML Kit Object Detection (shape context)
- **Rendering**: Custom Canvas (performance-optimized)

## Key Algorithms

### Orientation Detection
```
angle = atan2(Δy, Δx) in degrees
Classification:
  HORIZONTAL: ±10° or 170-190° or 350-360°
  VERTICAL: 80-100° or 260-280°
  ROTATED: any other
```

### Language Detection Fallback
```
Primary: ML Kit (confidence ≥ 0.6)
Fallback 1: Character set analysis (Hangul, CJK, etc.)
Fallback 2: Dictionary lookup (500 words per language)
Hybrid: 60% char score + 40% dict score
```

### Small Text Enhancement
```
1. Upscale 2-3× (ESRGAN or OpenCV)
2. Denoise (bilateral filter)
3. Re-OCR (ML Kit + Tesseract parallel)
4. Validate results match (boost confidence if agree)
Output: >85% accuracy on 4-8pt text
```

### Smart Banner Positioning
```
Priority:
  1. Below (if space >40px)
  2. Above (if space >40px)
  3. Side with transparency
  4. Popup (no space)
Check overlaps, apply text direction (LTR/RTL)
```

### Batch Translation
```
Group by language
Check cache (return if hit)
Send uncached in parallel batches
Retry failed (exponential backoff)
Cache 30 days
```

### Distance Estimation
```
Detect object type (can diameter ~6.6cm reference)
Calculate: distance = (ref_size × focal_length) / pixel_size
Fallback: text size heuristic
Confidence-weighted result: ±10cm accuracy
```

### Quality Score
```
score = (ocrConf × 0.35) + (sharpness × 0.25) + 
        (distanceBucket × 0.20) + (contrast × 0.10) + (stability × 0.10)
Result: 0-100% quality indicator
```

## Performance Targets

- End-to-end latency: <2.5s
- OCR accuracy: ≥95%
- Language detection: ≥90%
- Banner placement: ≥98% no overlap
- Distance accuracy: ±10cm
- Memory: <200MB peak
- Battery: <15%/min
- Startup: <1s

## Development Checklist

**Pre-Dev**: Understand modules, set up dev env, get API keys  
**Development**: 1-2 days per module (8-10 weeks total estimate)  
**Testing**: Unit, integration, performance, 120-image test suite, user testing  
**Deployment**: Code review, security audit, monitoring

## File Structure

```
src/
  ├── modules/
  │   ├── orientation_detector
  │   ├── language_detector
  │   ├── small_text_enhancer
  │   ├── banner_renderer
  │   ├── translator
  │   ├── distance_estimator
  │   └── system_integrator
  ├── utils/ (cache, error, logger, config)
  └── main (orchestrator)
test/ (unit, integration, performance, test_data)
docs/ (architecture, api, troubleshooting)
```

## Success Metrics

**Must Have** (ship blocker):
- OCR ≥95%, no crashes, latency <3s, <5% overlap

**Should Have**:
- Language detection ≥90%, small text support, vertical text, distance ±10cm

**Nice to Have**:
- Offline fallback, multi-language UI, custom colors, translation history
