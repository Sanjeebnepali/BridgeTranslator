package com.example.bridgetranslator

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.util.Xml
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.util.zip.ZipInputStream
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FileTextExtractor(private val context: Context) {

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    suspend fun extract(uri: Uri, mimeType: String): String {
        return when {
            mimeType.startsWith("image/") -> extractFromImage(uri)
            mimeType == "application/pdf" -> extractFromPdf(uri)
            mimeType == "text/plain" -> extractFromText(uri)
            mimeType.contains("wordprocessingml") || mimeType.contains("msword") ->
                extractFromDocx(uri)
            mimeType.contains("presentationml") || mimeType.contains("powerpoint") ->
                extractFromPptx(uri)
            else -> throw UnsupportedOperationException(
                "Format not supported. Supported: images, PDF, TXT, DOCX, PPTX."
            )
        }
    }

    // - Image (JPG / PNG / WEBP / BMP) -

    private suspend fun extractFromImage(uri: Uri): String {
        val image = InputImage.fromFilePath(context, uri)
        return recognizeText(image)
    }

    // - PDF (renders each page to bitmap, then OCR) -

    private suspend fun extractFromPdf(uri: Uri): String {
        val fd = context.contentResolver.openFileDescriptor(uri, "r")
            ?: throw IllegalStateException("Cannot open PDF file")
        val renderer = PdfRenderer(fd)
        val pageCount = minOf(renderer.pageCount, 8) // cap at 8 pages for performance
        val sb = StringBuilder()

        for (i in 0 until pageCount) {
            val page = renderer.openPage(i)
            // Scale up 2x for better OCR accuracy
            val bmp = Bitmap.createBitmap(page.width * 2, page.height * 2, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bmp)
            canvas.drawColor(Color.WHITE)
            page.render(bmp, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            page.close()

            val text = recognizeText(InputImage.fromBitmap(bmp, 0))
            if (text.isNotBlank()) {
                sb.append("[Page ${i + 1}]\n")
                sb.append(text.trim())
                sb.append("\n\n")
            }
            bmp.recycle()
        }

        renderer.close()
        fd.close()

        val result = sb.toString().trim()
        return result.ifEmpty { throw IllegalStateException("No readable text found in PDF") }
    }

    // - Plain text -

    private fun extractFromText(uri: Uri): String {
        return context.contentResolver.openInputStream(uri)?.use { it.readBytes().toString(Charsets.UTF_8) }
            ?: throw IllegalStateException("Cannot read text file")
    }

    // - DOCX (ZIP -> word/document.xml -> <w:t> elements) -

    private fun extractFromDocx(uri: Uri): String {
        val sb = StringBuilder()
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            ZipInputStream(inputStream).use { zis ->
                var entry = zis.nextEntry
                while (entry != null) {
                    if (entry.name == "word/document.xml") {
                        val bytes = zis.readBytes()
                        sb.append(extractXmlText(ByteArrayInputStream(bytes), "w:t"))
                        break
                    }
                    zis.closeEntry()
                    entry = zis.nextEntry
                }
            }
        }
        val result = sb.toString().trim()
        return result.ifEmpty { throw IllegalStateException("No text found in DOCX file") }
    }

    // - PPTX (ZIP -> ppt/slides/slide*.xml -> <a:t> elements) -

    private fun extractFromPptx(uri: Uri): String {
        val slides = mutableMapOf<String, String>()
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            ZipInputStream(inputStream).use { zis ->
                var entry = zis.nextEntry
                while (entry != null) {
                    if (entry.name.matches(Regex("ppt/slides/slide\\d+\\.xml"))) {
                        val bytes = zis.readBytes()
                        val text = extractXmlText(ByteArrayInputStream(bytes), "a:t")
                        if (text.isNotBlank()) slides[entry.name] = text
                    }
                    zis.closeEntry()
                    entry = zis.nextEntry
                }
            }
        }
        val result = slides.entries
            .sortedBy { it.key }
            .joinToString("\n\n") { (name, text) ->
                val slideNum = name.filter { it.isDigit() }
                "[Slide $slideNum]\n${text.trim()}"
            }
        return result.ifEmpty { throw IllegalStateException("No text found in PPTX file") }
    }

    // - XML text extractor (namespace-prefix aware) -

    private fun extractXmlText(stream: InputStream, targetElement: String): String {
        val sb = StringBuilder()
        try {
            val parser = Xml.newPullParser()
            parser.setInput(stream, "UTF-8")
            var eventType = parser.eventType
            var capture = false
            while (eventType != org.xmlpull.v1.XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    org.xmlpull.v1.XmlPullParser.START_TAG ->
                        capture = parser.name == targetElement
                    org.xmlpull.v1.XmlPullParser.TEXT ->
                        if (capture && parser.text.isNotBlank()) {
                            sb.append(parser.text)
                            sb.append(' ')
                        }
                    org.xmlpull.v1.XmlPullParser.END_TAG -> capture = false
                }
                eventType = parser.next()
            }
        } catch (_: Exception) { /* malformed XML - return what we have */ }
        return sb.toString()
    }

    // - ML Kit OCR wrapper as suspend function -

    private suspend fun recognizeText(image: InputImage): String = suspendCoroutine { cont ->
        recognizer.process(image)
            .addOnSuccessListener { cont.resume(it.text) }
            .addOnFailureListener { cont.resumeWithException(it) }
    }

    fun close() = recognizer.close()
}
