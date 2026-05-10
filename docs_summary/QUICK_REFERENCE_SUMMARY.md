# Live Camera Translation — Quick Reference (Summary)

## Current Problems

```
Vertical text:     30% → 85% target
Small text:        40-50% → 85% target
UI overlaps:       Frequent → <5% target
Distance support:  20cm-1.5m → 20cm-2m target
Mixed orientation: Fails → Handle all angles target
```

## 7-Module Solution Overview

| Module | Input | Output | Time | Goal |
|--------|-------|--------|------|------|
| **1. Orientation** | Text blocks | Angle + class | 50ms | Detect H/V/rotated |
| **2. Language** | Text | Language + conf | 100ms | Accurate detection |
| **3. Small Text** | <8pt blocks | Enhanced OCR | 300ms | Boost tiny-font accuracy |
| **4. Translation** | Text | Translated | 1000ms | Batch API calls |
| **5. Banner** | Text + blocks | Positioned UI | 100ms | No overlaps, readable |
| **6. Distance** | Frame | Distance + score | 100ms | ±10cm + quality |
| **7. Integration** | All above | Complete system | <2.5s | Orchestrate all modules |

## Architecture Flow

```
Camera Input → Preprocessing → OCR → Orientation → Language ID → Small Text → 
Translation → Banner Positioning → Distance/Quality → Display
```

## Detection Range Performance

```
<20cm:      ❌ Distorted (quality <40%)
20-100cm:   ✅ OPTIMAL (85-95% accuracy)
100-150cm:  ✅ Good (80-90%)
150-200cm:  ⚠️ Degraded (70-80%)
>200cm:     ❌ Too far (<70%)
```

## Language Support

**Tier 1** (95%+): English, Korean, Chinese, Japanese, Spanish, French  
**Tier 2** (90%+): German, Portuguese, Russian, Italian, Dutch, Polish  
**Tier 3** (85%+): Arabic, Thai, Vietnamese, Indonesian, Turkish, Hindi  
**Tier 4** (75%+): Greek, Czech, Hungarian, Swedish, Danish, Norwegian

## Key Algorithms

### 1️⃣ Orientation Detection
```
angle = atan2(Δy, Δx) in degrees
HORIZONTAL: ±10° or 170-190° or 350-360°
VERTICAL:   80-100° or 260-280°
ROTATED:    any other
Confidence: based on text length + straightness + consistency
```

### 2️⃣ Language Detection Fallback
```
Primary: ML Kit (conf ≥0.6)
→ Fallback 1: Character set (Hangul, CJK, Arabic, etc.)
→ Fallback 2: Dictionary (500 words per language)
→ Hybrid: (charSetScore × 0.6) + (dictScore × 0.4)
```

### 3️⃣ Small Text Enhancement
```
1. Upscale 2-3× (ESRGAN)
2. Denoise (bilateral filter)
3. Re-OCR (ML Kit + Tesseract parallel)
4. Validate (if both agree, boost confidence)
Result: >85% accuracy on 4-8pt text
```

### 4️⃣ Smart Banner Positioning
```
Priority: Below > Above > Side > Popup
Check overlaps with other text
Detect alignment (left/center/right)
Apply text direction (LTR/RTL)
```

### 5️⃣ Batch Translation
```
Group by language
Check cache (hit → return)
Send uncached in parallel batches
Retry failed (exponential backoff)
Cache new results (30 days)
```

### 6️⃣ Distance Estimation
```
Detect object (can diameter ~6.6cm)
Calculate: distance = (ref_size × focal_length) / pixel_size
Fallback: text size heuristic
Confidence weighted result: ±10cm
```

### 7️⃣ Quality Score
```
score = (ocrConf × 0.35) +
        (textSharpness × 0.25) +
        (distanceBucket × 0.20) +
        (contrastRatio × 0.10) +
        (stability × 0.10)
Range: 0-100% quality indicator
```

## Common Issues & Solutions

| Issue | Cause | Solution |
|-------|-------|----------|
| Vertical text fails | Poor angle detection | Increase text length threshold |
| Translation gibberish | Language ID wrong | Use fallback detection |
| Small text unreadable | OCR confidence low | Apply enhancement pipeline |
| Banners overlap | No collision detection | Implement overlap checks |
| Slow processing | Large images | Reduce resolution adaptively |
| Wrong distance | No object detection | Use text size heuristic |
| Battery drain | Too many API calls | Implement caching aggressively |
| Crashes on rotation | Orientation not handled | Use reactive updates (RxJava) |

## Performance Targets

- **End-to-End Latency**: <2.5s
- **OCR Accuracy**: ≥95%
- **Language Detection**: ≥90%
- **Banner Placement**: ≥98% success (no overlaps)
- **Distance Accuracy**: ±10cm
- **Memory**: <200MB peak
- **Battery**: <15%/min during active use
- **Startup**: <1s to first translation

## Development Checklist

**Pre-Development**:
- ✓ Understand all 7 modules
- ✓ Review architecture
- ✓ Set up dev environment
- ✓ Get API keys (Google Cloud, Firebase)

**Development Phase** (6-8 weeks):
- ✓ Orientation detection (1-2d)
- ✓ Language detection (1-2d)
- ✓ Small text enhancement (2-3d)
- ✓ Batch translation (1-2d)
- ✓ Banner positioning (1-2d)
- ✓ Distance & quality (1-2d)
- ✓ Integration & testing (2-3d)

**Testing Phase**:
- ✓ Unit tests per module
- ✓ Integration tests
- ✓ Performance profiling
- ✓ 120-image real-world suite
- ✓ User testing (5+ users)

**Deployment**:
- ✓ Code review
- ✓ Security audit
- ✓ Production deployment
- ✓ Monitoring & feedback

## Key Files & References

- **Live_Camera_Translation_Feature_Design.md** — Complete technical spec
- **AGENT_EXECUTION_PROMPTS.md** — Ready-to-use development prompts
- **QUICK_REFERENCE_GUIDE.md** — This quick reference

## Success Metrics

**Must Have** (ship blocker):
- OCR ≥95%, no crashes, latency <3s, <5% overlap

**Should Have**:
- Language detection ≥90%, small text support, vertical text, distance ±10cm

**Nice to Have**:
- Offline fallback, multi-language UI, custom colors, translation history
