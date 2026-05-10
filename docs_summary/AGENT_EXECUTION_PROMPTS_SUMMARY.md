# Agent Execution Prompts (Summary)

## Seven Self-Contained Prompts for Development Agents

All prompts provided separately; this summary covers scope and key requirements for each.

### Prompt 1: Text Orientation Detection Engine

**Goal**: Identify text orientation (horizontal/vertical/rotated) from ML Kit blocks

**Input**: Text blocks with bounding boxes from ML Kit  
**Output**: Text + angle + orientation class + confidence

**Algorithm**:
- Calculate angle from bbox corners using atan2(Δy, Δx)
- Classify: HORIZONTAL (±10°), VERTICAL (80-100°), ROTATED (other)
- Confidence: based on text length + line straightness + character consistency

**Deliverables**:
- orientation_detector module
- Unit tests (50+ cases: horizontal, vertical, rotated, mixed, curved, upside-down)
- Performance: <100ms per image
- Accuracy target: >95%

---

### Prompt 2: Multi-Layer Language Detection

**Goal**: Detect language with >90% accuracy, including short text, brand names, mixed languages

**Primary**: Google ML Kit Language ID (confidence ≥ 0.6)  
**Fallback 1**: Character set analysis (Hangul, CJK, Arabic, etc.)  
**Fallback 2**: Dictionary lookup (500 words per language)  
**Hybrid**: 60% char score + 40% dict score

**Edge cases**:
- Single word (use char set primarily)
- Brand names (not in dict, use char set)
- Numbers only (return "UNKNOWN")
- Mixed language (return dominant + flag)
- Abbreviated text (weight dict lower)

**Languages**: 15+ (English, Korean, Chinese, Japanese, Spanish, French, German, Portuguese, Russian, Arabic, Thai, Vietnamese, Indonesian, Turkish, Hindi)

**Deliverables**:
- language_detector module with fallback strategies
- Language dictionaries (500 words × 15 languages)
- 100+ test cases (edge cases)
- Latency: <50ms per block
- Accuracy: >90%

---

### Prompt 3: Small Text Enhancement Pipeline

**Goal**: Achieve >85% accuracy on tiny fonts (<8pt)

**Stage 1: Pre-Processing**
- Upscale 2-3× (ESRGAN or TensorFlow)
- Contrast enhancement (histogram equalization)
- Denoise (bilateral filter)
- Optional binarization (Otsu threshold)

**Stage 2: Multi-Source OCR**
- ML Kit recognition (accept if confidence ≥0.75)
- Fallback: Tesseract OCR (pre-specify language)
- Compare results (if both agree, boost confidence to 0.85-0.95)

**Deliverables**:
- enhancement_pipeline module
- Confidence validation logic
- 100+ test cases (4pt-14pt text)
- Latency: 300-400ms when triggered
- Accuracy: 85%+ on <8pt, 95%+ on 8-12pt

---

### Prompt 4: Smart Banner Positioning

**Goal**: Position translation banners without overlaps, preserve readability

**Priority order**:
1. Below (if space >40px)
2. Above (if space >40px)
3. Side with transparency
4. Popup (no space)

**Checks**: Overlap detection, text direction (LTR/RTL), alignment detection

**Deliverables**:
- banner_renderer module
- Collision detection algorithm
- 100+ test cases
- Latency: <100ms
- Success rate: >98% no overlap

---

### Prompt 5: Batch Translation System

**Goal**: Fast parallel translation with caching (30-day cache)

**Algorithm**:
- Group by language
- Check cache (return if hit)
- Send uncached in parallel batches to Google API
- Retry failed items (exponential backoff)
- Cache new results

**Deliverables**:
- translator module
- Cache manager (LRU, 30-day expiry)
- Retry logic (exponential backoff)
- 100+ test cases
- Latency: 1000ms typical (batched)

---

### Prompt 6: Distance & Quality Estimation

**Goal**: Estimate distance (±10cm) and quality score for user feedback

**Distance Algorithm**:
- Detect object type (can diameter ~6.6cm reference)
- Calculate: distance = (ref_size × focal_length) / pixel_size
- Fallback: text size heuristic

**Quality Score**:
```
score = (ocrConf × 0.35) + (sharpness × 0.25) + 
        (distanceBucket × 0.20) + (contrast × 0.10) + (stability × 0.10)
```

**Deliverables**:
- distance_estimator module
- Quality scoring logic
- 100+ test cases
- Latency: <100ms
- Distance accuracy: ±10cm

---

### Prompt 7: System Integration & Testing

**Goal**: Assemble all 6 modules into a complete end-to-end pipeline

**Tests**:
- Unit tests per module
- Integration tests (all modules together)
- Performance profiling
- 120-image real-world test suite
- User testing (5+ users)

**Acceptance**:
- End-to-end latency <2.5s
- OCR accuracy ≥95%
- Language detection ≥90%
- Banner placement ≥98%
- Distance ±10cm
- No crashes

## Summary

Each prompt is 500-1000 lines, self-contained, includes:
- Objective and problem statement
- Technical details and algorithms
- Edge cases to handle
- Testing requirements
- Deliverables checklist
- Acceptance criteria


