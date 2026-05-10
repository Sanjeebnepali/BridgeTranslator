package com.example.bridgetranslator

data class Language(
    val code: String,
    val name: String,
    val flagEmoji: String
) {
    companion object {
        val SUPPORTED_LANGUAGES = listOf(
            Language("af", "Afrikaans", "AF"),
            Language("ar", "Arabic", "AR"),
            Language("be", "Belarusian", "BE"),
            Language("bg", "Bulgarian", "BG"),
            Language("bn", "Bengali", "BN"),
            Language("ca", "Catalan", "CA"),
            Language("cs", "Czech", "CS"),
            Language("cy", "Welsh", "CY"),
            Language("da", "Danish", "DA"),
            Language("de", "German", "DE"),
            Language("el", "Greek", "EL"),
            Language("en", "English", "EN"),
            Language("eo", "Esperanto", "EO"),
            Language("es", "Spanish", "ES"),
            Language("et", "Estonian", "ET"),
            Language("fa", "Persian", "FA"),
            Language("fi", "Finnish", "FI"),
            Language("fr", "French", "FR"),
            Language("ga", "Irish", "GA"),
            Language("gl", "Galician", "GL"),
            Language("gu", "Gujarati", "GU"),
            Language("he", "Hebrew", "HE"),
            Language("hi", "Hindi", "HI"),
            Language("hr", "Croatian", "HR"),
            Language("hu", "Hungarian", "HU"),
            Language("id", "Indonesian", "ID"),
            Language("is", "Icelandic", "IS"),
            Language("it", "Italian", "IT"),
            Language("ja", "Japanese", "JA"),
            Language("ka", "Georgian", "KA"),
            Language("kn", "Kannada", "KN"),
            Language("ko", "Korean", "KO"),
            Language("lt", "Lithuanian", "LT"),
            Language("lv", "Latvian", "LV"),
            Language("mk", "Macedonian", "MK"),
            Language("mr", "Marathi", "MR"),
            Language("ms", "Malay", "MS"),
            Language("mt", "Maltese", "MT"),
            Language("nl", "Dutch", "NL"),
            Language("no", "Norwegian", "NO"),
            Language("pa", "Punjabi", "PA"),
            Language("pl", "Polish", "PL"),
            Language("pt", "Portuguese", "PT"),
            Language("ro", "Romanian", "RO"),
            Language("ru", "Russian", "RU"),
            Language("sk", "Slovak", "SK"),
            Language("sl", "Slovenian", "SL"),
            Language("sq", "Albanian", "SQ"),
            Language("sr", "Serbian", "SR"),
            Language("sv", "Swedish", "SV"),
            Language("sw", "Swahili", "SW"),
            Language("ta", "Tamil", "TA"),
            Language("te", "Telugu", "TE"),
            Language("th", "Thai", "TH"),
            Language("tl", "Tagalog", "TL"),
            Language("tr", "Turkish", "TR"),
            Language("uk", "Ukrainian", "UK"),
            Language("ur", "Urdu", "UR"),
            Language("vi", "Vietnamese", "VI"),
            Language("zh", "Chinese", "ZH"),
            Language("zu", "Zulu", "ZU"),
        )

        val COMMON_LANGUAGES = listOf(
            "en", "ko", "es", "fr", "de", "ja", "zh", "hi", "ar", "pt", "ru", "it"
        ).mapNotNull(::getLanguageByCode)

        fun getLanguageByCode(code: String): Language? {
            return SUPPORTED_LANGUAGES.find { it.code.equals(code, ignoreCase = true) }
        }
    }
}
