package com.example.model

enum class CompassDirection(
    val englishName: String,
    val urduName: String,
    val sindhiName: String,
    val shortEn: String,
    val shortUr: String,
    val shortSd: String
) {
    NORTH("North", "شمال", "اتر", "N", "ش", "ا"),
    SOUTH("South", "جنوب", "ڏکڻ", "S", "ج", "ڏ"),
    EAST("East", "مشرق", "اوڀر", "E", "م", "او"),
    WEST("West", "مغرب", "اولهه", "W", "مغ", "او"),
    NORTH_EAST("North-East", "شمال مشرق", "اتر اوڀر", "NE", "ش-م", "ا-او"),
    NORTH_WEST("North-West", "شمال مغرب", "اتر اولهه", "NW", "ش-مغ", "ا-او"),
    SOUTH_EAST("South-East", "جنوب مشرق", "ڏکڻ اوڀر", "SE", "ج-م", "ڏ-او"),
    SOUTH_WEST("South-West", "جنوب مغرب", "ڏکڻ اولهه", "SW", "ج-مغ", "ڏ-او");

    fun getName(lang: Language): String = when (lang) {
        Language.ENGLISH -> englishName
        Language.URDU -> urduName
        Language.SINDHI -> sindhiName
    }

    fun getShort(lang: Language): String = when (lang) {
        Language.ENGLISH -> shortEn
        Language.URDU -> shortUr
        Language.SINDHI -> shortSd
    }
}
