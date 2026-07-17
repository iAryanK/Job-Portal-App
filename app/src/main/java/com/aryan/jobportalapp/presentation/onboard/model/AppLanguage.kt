package com.aryan.jobportalapp.presentation.onboard.model

enum class AppLanguage(
    val displayName: String,
    val nativeName: String,
    val code: String
) {
    ENGLISH(displayName = "English", nativeName = "English", code = "en"),
    HINDI(displayName = "Hindi", nativeName = "हिन्दी", code = "hi"),
    TELUGU(displayName = "Telugu", nativeName = "తెలుగు", code = "te")
}
