package com.example.bridgetranslator

import com.google.android.gms.tasks.Tasks
import com.google.mlkit.nl.languageid.LanguageIdentifier
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class LanguageDetectorTest {

    @Mock
    private lateinit var mockIdentifier: LanguageIdentifier

    private lateinit var detector: LanguageDetector

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        detector = LanguageDetector(mockIdentifier)
    }

    @Test
    fun `test successful ML Kit detection`() = runBlocking {
        `when`(mockIdentifier.identifyLanguage("Hello world"))
            .thenReturn(Tasks.forResult("en"))
            
        val result = detector.detectLanguage("Hello world")
        assertEquals("en", result)
    }

    @Test
    fun `test fallback detection when ML Kit returns und`() = runBlocking {
        // Mock ML Kit returning unknown
        `when`(mockIdentifier.identifyLanguage("안녕하세요"))
            .thenReturn(Tasks.forResult("und"))
            
        val result = detector.detectLanguage("안녕하세요")
        assertEquals("ko", result)
    }

    @Test
    fun `test fallback detection exception`() = runBlocking {
        `when`(mockIdentifier.identifyLanguage("你好"))
            .thenReturn(Tasks.forException(Exception("API Error")))
            
        val result = detector.detectLanguage("你好")
        assertEquals("zh", result)
    }

    @Test
    fun `test character set analysis Korean`() {
        val scores = detector.analyzeCharacterSets("이것은 한국어 테스트입니다.")
        assert(scores["ko"] ?: 0f > 0.4f)
    }

    @Test
    fun `test character set analysis Chinese`() {
        val scores = detector.analyzeCharacterSets("这是一些中文测试。")
        assert(scores["zh"] ?: 0f > 0.4f)
    }

    @Test
    fun `test character set analysis Japanese`() {
        val scores = detector.analyzeCharacterSets("これは日本語のテストです。")
        assert(scores["ja"] ?: 0f > 0.4f)
    }

    @Test
    fun `test character set analysis mixed language`() {
        // "This is 123" has no specific char sets matching the fallback list, so should be empty
        val scores = detector.analyzeCharacterSets("This is 123")
        assert(scores.isEmpty())
    }
}
