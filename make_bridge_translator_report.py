from reportlab.lib import colors
from reportlab.lib.enums import TA_CENTER, TA_LEFT
from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import ParagraphStyle, getSampleStyleSheet
from reportlab.lib.units import inch
from reportlab.platypus import (
    BaseDocTemplate,
    Frame,
    PageTemplate,
    Paragraph,
    Spacer,
    Table,
    TableStyle,
    PageBreak,
)


OUTPUT = "Bridge_Translator_Project_Report.pdf"


def header_footer(canvas, doc):
    canvas.saveState()
    width, height = A4
    canvas.setStrokeColor(colors.HexColor("#D6DEE8"))
    canvas.setLineWidth(0.8)
    canvas.line(0.65 * inch, height - 0.58 * inch, width - 0.65 * inch, height - 0.58 * inch)
    canvas.setFont("Helvetica", 8)
    canvas.setFillColor(colors.HexColor("#637083"))
    canvas.drawString(0.65 * inch, height - 0.42 * inch, "Bridge Translator - Project Report")
    canvas.drawRightString(width - 0.65 * inch, 0.42 * inch, f"Page {doc.page}")
    canvas.restoreState()


def styles():
    base = getSampleStyleSheet()
    return {
        "title": ParagraphStyle(
            "Title",
            parent=base["Title"],
            fontName="Helvetica-Bold",
            fontSize=26,
            leading=31,
            textColor=colors.HexColor("#0E1B2A"),
            alignment=TA_CENTER,
            spaceAfter=14,
        ),
        "subtitle": ParagraphStyle(
            "Subtitle",
            parent=base["BodyText"],
            fontName="Helvetica",
            fontSize=11.5,
            leading=16,
            textColor=colors.HexColor("#526173"),
            alignment=TA_CENTER,
            spaceAfter=28,
        ),
        "h1": ParagraphStyle(
            "Heading1",
            parent=base["Heading1"],
            fontName="Helvetica-Bold",
            fontSize=16,
            leading=20,
            textColor=colors.HexColor("#123B63"),
            spaceBefore=12,
            spaceAfter=8,
        ),
        "h2": ParagraphStyle(
            "Heading2",
            parent=base["Heading2"],
            fontName="Helvetica-Bold",
            fontSize=12.5,
            leading=16,
            textColor=colors.HexColor("#0E1B2A"),
            spaceBefore=8,
            spaceAfter=5,
        ),
        "body": ParagraphStyle(
            "Body",
            parent=base["BodyText"],
            fontName="Helvetica",
            fontSize=10,
            leading=14,
            textColor=colors.HexColor("#1C2733"),
            spaceAfter=7,
        ),
        "small": ParagraphStyle(
            "Small",
            parent=base["BodyText"],
            fontName="Helvetica",
            fontSize=8.5,
            leading=11.5,
            textColor=colors.HexColor("#526173"),
        ),
        "callout": ParagraphStyle(
            "Callout",
            parent=base["BodyText"],
            fontName="Helvetica-Bold",
            fontSize=10.5,
            leading=14,
            textColor=colors.HexColor("#0E1B2A"),
            leftIndent=0,
            spaceAfter=0,
        ),
        "cell": ParagraphStyle(
            "Cell",
            parent=base["BodyText"],
            fontName="Helvetica",
            fontSize=8.2,
            leading=10.5,
            textColor=colors.HexColor("#1C2733"),
        ),
        "cell_bold": ParagraphStyle(
            "CellBold",
            parent=base["BodyText"],
            fontName="Helvetica-Bold",
            fontSize=8.2,
            leading=10.5,
            textColor=colors.HexColor("#0E1B2A"),
        ),
    }


def p(text, style):
    return Paragraph(text, style)


def bullets(items, style):
    flow = []
    for item in items:
        flow.append(Paragraph(f"- {item}", style))
    return flow


def section_card(text, style):
    table = Table([[p(text, style)]], colWidths=[6.7 * inch])
    table.setStyle(
        TableStyle(
            [
                ("BACKGROUND", (0, 0), (-1, -1), colors.HexColor("#EAF3FF")),
                ("BOX", (0, 0), (-1, -1), 0.8, colors.HexColor("#B7D4F3")),
                ("LEFTPADDING", (0, 0), (-1, -1), 12),
                ("RIGHTPADDING", (0, 0), (-1, -1), 12),
                ("TOPPADDING", (0, 0), (-1, -1), 10),
                ("BOTTOMPADDING", (0, 0), (-1, -1), 10),
            ]
        )
    )
    return table


def simple_table(rows, col_widths):
    data = [[p(cell, S["cell_bold"] if r == 0 else S["cell"]) for cell in row] for r, row in enumerate(rows)]
    table = Table(data, colWidths=col_widths, repeatRows=1)
    table.setStyle(
        TableStyle(
            [
                ("BACKGROUND", (0, 0), (-1, 0), colors.HexColor("#123B63")),
                ("TEXTCOLOR", (0, 0), (-1, 0), colors.white),
                ("GRID", (0, 0), (-1, -1), 0.35, colors.HexColor("#D6DEE8")),
                ("VALIGN", (0, 0), (-1, -1), "TOP"),
                ("LEFTPADDING", (0, 0), (-1, -1), 7),
                ("RIGHTPADDING", (0, 0), (-1, -1), 7),
                ("TOPPADDING", (0, 0), (-1, -1), 6),
                ("BOTTOMPADDING", (0, 0), (-1, -1), 6),
                ("BACKGROUND", (0, 1), (-1, -1), colors.white),
            ]
        )
    )
    return table


S = styles()

doc = BaseDocTemplate(
    OUTPUT,
    pagesize=A4,
    leftMargin=0.7 * inch,
    rightMargin=0.7 * inch,
    topMargin=0.82 * inch,
    bottomMargin=0.72 * inch,
)
frame = Frame(doc.leftMargin, doc.bottomMargin, doc.width, doc.height, id="normal")
doc.addPageTemplates([PageTemplate(id="report", frames=[frame], onPage=header_footer)])

story = []

story.append(Spacer(1, 0.18 * inch))
story.append(p("Bridge Translator", S["title"]))
story.append(
    p(
        "Project report for the Android Kotlin screen translation app: architecture, implementation flow, file responsibilities, fixes completed, and remaining testing notes.",
        S["subtitle"],
    )
)
story.append(section_card("Final product direction: floating bubble + MediaProjection screenshot + ML Kit OCR/translation + touch-through overlay. Accessibility is used only as an event trigger for scroll/page/app changes.", S["callout"]))
story.append(Spacer(1, 0.25 * inch))

story.append(p("1. Project Goal", S["h1"]))
story.append(
    p(
        "Bridge Translator translates visible text from any app screen. The intended user experience is close to Google Translate screen translation: original text is visually hidden, translated text appears in the same location, and the user can still interact with the target app.",
        S["body"],
    )
)
story.extend(
    bullets(
        [
            "Support real app screens such as Korean job-listing pages.",
            "Keep the original app usable while translation is active.",
            "Avoid messy double text, black pills, or large dim overlays.",
            "Refresh translation when the user scrolls, changes route/page, or opens a new app.",
        ],
        S["body"],
    )
)

story.append(p("2. Final Architecture", S["h1"]))
arch_rows = [
    ["Layer", "Responsibility"],
    ["Floating bubble", "Draggable activation control. Starts/stops translation and opens language picker."],
    ["MediaProjection capture", "Captures clean screenshots after hiding app overlays and bubble."],
    ["ML Kit OCR", "Detects text lines and bounding boxes from screenshots."],
    ["ML Kit translation", "Performs on-device translation using downloaded language models."],
    ["Translation canvas", "Touch-through overlay that erases original text boxes and draws translated text in place."],
    ["Accessibility trigger", "Detects scroll/window/content changes and tells the bubble service to refresh. It does not rewrite app TextViews."],
]
story.append(simple_table(arch_rows, [1.55 * inch, 5.15 * inch]))

story.append(p("3. Runtime Flow", S["h1"]))
flow_rows = [
    ["Step", "What Happens"],
    ["1", "User taps floating bubble."],
    ["2", "App asks for screen capture permission if needed."],
    ["3", "Bubble, bottom bar, and overlay are temporarily hidden."],
    ["4", "MediaProjection captures a clean bitmap."],
    ["5", "ML Kit OCR returns text lines and exact bounding boxes."],
    ["6", "ML Kit translates detected text into the selected target language."],
    ["7", "Canvas draws sampled background over each original text box."],
    ["8", "Canvas draws translated text inside the same OCR rectangle."],
    ["9", "When scroll/page/app change happens, Accessibility Service triggers a new capture."],
]
story.append(simple_table(flow_rows, [0.65 * inch, 6.05 * inch]))

story.append(PageBreak())

story.append(p("4. File Responsibilities", S["h1"]))
file_rows = [
    ["File", "Purpose"],
    ["FloatingBubbleService.kt", "Main foreground service. Manages bubble, permissions, capture, OCR, translation, language picker, overlay, and refresh events."],
    ["ScreenCaptureManager.kt", "Owns MediaProjection, VirtualDisplay, ImageReader, and bitmap capture lifecycle."],
    ["ScreenCaptureHelper.kt", "Small wrapper around ScreenCaptureManager."],
    ["TranslationCanvasView.kt", "Touch-through drawing layer. Erases original text and draws translated text inside exact OCR boxes."],
    ["TextEraseHelper.kt", "Builds translated blocks, samples background color, computes screenshot hash, and checks whether content changed."],
    ["TranslatedBlock.kt", "Data object for one translated OCR region."],
    ["TranslationBottomBarView.kt", "Clickable bottom bar for source/target language, translate action, and close action."],
    ["LanguagePickerOverlayView.kt", "Overlay language picker used by bubble and bottom bar."],
    ["TranslatorAccessibilityService.kt", "Event trigger only. Detects scroll/content/window changes and broadcasts refresh requests."],
    ["TranslationEngine.kt", "ML Kit OCR and on-device translation wrapper."],
    ["AndroidManifest.xml", "Registers permissions, services, activities, and accessibility configuration."],
]
story.append(simple_table(file_rows, [2.15 * inch, 4.55 * inch]))

story.append(p("5. Major Problems Fixed", S["h1"]))
story.extend(
    bullets(
        [
            "Removed the messy double-text behavior by erasing original OCR regions before drawing translation.",
            "Removed the dark dim layer and black pill UI because it made the target app look unnatural.",
            "Changed the overlay to FLAG_NOT_TOUCHABLE so it does not block scrolling or normal app use.",
            "Replaced fixed polling loops with event-driven refresh from Accessibility scroll/content/window events.",
            "Fixed MediaProjection crash caused by attempting to recreate VirtualDisplay with the same projection token.",
            "Added translation cache behavior in the active service path to avoid repeated translation work.",
            "Added Korean OCR support for better recognition of Korean app screens.",
            "Cleaned out the ACTION_SET_TEXT approach because it is unreliable for read-only TextViews in other apps.",
        ],
        S["body"],
    )
)

story.append(p("6. Why We Use ML Kit Instead of Ollama API", S["h1"]))
ollama_rows = [
    ["Option", "Fit for this app"],
    ["ML Kit On-Device Translation", "Best mobile fit. Runs on Android, supports offline after model download, no API key, lower latency, privacy-friendly."],
    ["Ollama API", "Not ideal for normal Android users. Usually requires a local/server model, network setup, more battery/latency cost, and extra deployment complexity."],
]
story.append(simple_table(ollama_rows, [2.1 * inch, 4.6 * inch]))

story.append(PageBreak())

story.append(p("7. Current Behavior", S["h1"]))
story.extend(
    bullets(
        [
            "User starts translation from the bubble.",
            "The overlay replaces detected text visually, without blocking touches.",
            "The user can scroll or navigate in the original app.",
            "Accessibility auto-refresh must be enabled so scroll/page/app changes trigger a new screenshot and translation pass.",
            "Bottom bar remains clickable for language and close controls.",
        ],
        S["body"],
    )
)

story.append(p("8. Testing Checklist", S["h1"]))
test_rows = [
    ["Area", "What to test"],
    ["Build", "Sync and build in Android Studio. The shell workspace does not currently include gradlew.bat."],
    ["Permissions", "Overlay permission, screen capture permission, notification permission, and Accessibility auto-refresh service."],
    ["Korean app screen", "Open Korean job-listing screen, start bubble, confirm original text is hidden and translated text appears in same positions."],
    ["Touch-through", "Scroll the target app while overlay is active. Overlay should not block gestures."],
    ["Refresh", "Scroll, change page, and open a new app. Translation should refresh from Accessibility event trigger."],
    ["Model download", "First use may require ML Kit model download. Test online first, then offline behavior."],
    ["Visual quality", "Check small boxes, long translated text, background blending, and font fitting."],
]
story.append(simple_table(test_rows, [1.35 * inch, 5.35 * inch]))

story.append(p("9. Remaining Improvement Opportunities", S["h1"]))
story.extend(
    bullets(
        [
            "Improve background sampling for complex image or gradient backgrounds.",
            "Tune OCR grouping so multi-line cards translate as natural blocks where useful.",
            "Add a small model-download progress state for first-run clarity.",
            "Add debug screenshots/logging switch for testing alignment on difficult apps.",
            "Consider an Accessibility event permission explanation screen so users understand why auto-refresh is required.",
        ],
        S["body"],
    )
)

story.append(Spacer(1, 0.16 * inch))
story.append(section_card("Summary: Bridge Translator is now best defined as a touch-through OCR translation overlay with event-driven refresh. This preserves the real app interaction model while avoiding Android's unreliable ACTION_SET_TEXT limitations.", S["callout"]))

doc.build(story)
print(OUTPUT)
