package com.example.model

enum class Region(
    val englishName: String,
    val urduName: String,
    val sindhiName: String
) {
    PUNJAB(
        englishName = "Punjab Units (Marla, Kanal, Acre)",
        urduName = "پنجاب اکائیاں (مرلہ، کنال، ایکڑ)",
        sindhiName = "پنجاب ماپون (مرلو، ڪنال، ايڪڙ)"
    ),
    SINDH(
        englishName = "Sindh Units (Wesa, Gunta, Jareeb, Acre)",
        urduName = "سندھ اکائیاں (ویسہ، گنٹہ، جریب، ایکڑ)",
        sindhiName = "سنڌ ماپون (ويهه، گنٽو، جريب، ايڪڙ)"
    );

    fun getLabel(lang: Language): String = when (lang) {
        Language.ENGLISH -> englishName
        Language.URDU -> urduName
        Language.SINDHI -> sindhiName
    }
}
