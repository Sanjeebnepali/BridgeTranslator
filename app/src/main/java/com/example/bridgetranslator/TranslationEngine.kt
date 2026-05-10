package com.example.bridgetranslator

/**
 * Compatibility wrapper for the older camera/manual UI.
 * The actual ML Kit implementation now lives in com.bridge.translator.engine.TranslationEngine.
 */
class TranslationEngine {

    private val supportedCodes = setOf(
        "af", "ar", "be", "bg", "bn", "ca", "cs", "cy", "da", "de", "el", "en",
        "eo", "es", "et", "fa", "fi", "fr", "ga", "gl", "gu", "he", "hi", "hr",
        "hu", "id", "is", "it", "ja", "ka", "kn", "ko", "lt", "lv", "mk", "mr",
        "ms", "mt", "nl", "no", "pa", "pl", "pt", "ro", "ru", "sk", "sl", "sq",
        "sr", "sv", "sw", "ta", "te", "th", "tl", "tr", "uk", "ur", "vi", "zh", "zu"
    )

    fun isSupported(code: String) = code.lowercase() in supportedCodes

    fun translate(
        text: String,
        srcCode: String,
        tgtCode: String,
        wifiOnly: Boolean = false,
        onDownloading: () -> Unit = {},
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val src = srcCode.lowercase()
        val tgt = tgtCode.lowercase()

        if (!isSupported(src)) {
            onError("Source language '$srcCode' is not supported for on-device translation.")
            return
        }
        if (!isSupported(tgt)) {
            onError("Target language '$tgtCode' is not supported for on-device translation.")
            return
        }
        if (src == tgt) {
            onSuccess(text)
            return
        }

        com.bridge.translator.engine.TranslationEngine.translate(
            text = text,
            sourceCode = src,
            targetCode = tgt,
            wifiOnly = wifiOnly,
            onDownloading = onDownloading,
            onResult = onSuccess,
            onError = onError
        )
    }

    fun close() = com.bridge.translator.engine.TranslationEngine.close()
}
