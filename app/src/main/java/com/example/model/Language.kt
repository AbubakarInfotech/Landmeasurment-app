package com.example.model

enum class Language(
    val displayName: String,
    val nativeName: String,
    val isRtl: Boolean
) {
    ENGLISH("English", "English", false),
    URDU("Urdu", "اردو", true),
    SINDHI("Sindhi", "سنڌي", true)
}
