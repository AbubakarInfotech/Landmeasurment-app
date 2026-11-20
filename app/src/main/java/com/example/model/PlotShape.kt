package com.example.model

enum class PlotShape(
    val englishName: String,
    val urduName: String,
    val sindhiName: String,
    val englishDesc: String,
    val urduDesc: String,
    val sindhiDesc: String
) {
    RECTANGLE(
        englishName = "Rectangle / Square",
        urduName = "مستطیل یا چورس",
        sindhiName = "مستطيل يا چورس",
        englishDesc = "4 right angles (Length × Width)",
        urduDesc = "چاروں کونے سیدھے (لمبائی × چوڑائی)",
        sindhiDesc = "چارئي ڪنڊون سڌيون (ڊيگهه × ويڪر)"
    ),
    TRIANGLE(
        englishName = "Triangle (3 Sides)",
        urduName = "مثلث (3 اضلاع)",
        sindhiName = "ٽڪنڊو (3 پاسا)",
        englishDesc = "3 straight sides (Heron's Formula)",
        urduDesc = "تینوں اطراف کی پیمائش (ہیروئن فارمولا)",
        sindhiDesc = "ٽنهي پاسن جي ماپ (هيرون فارمولو)"
    ),
    TRAPEZIUM(
        englishName = "Trapezium",
        urduName = "ذواربعۃ الاضلاع / ٹریپیزیم",
        sindhiName = "ٽريپيزيم / ٻه برابري رخ",
        englishDesc = "2 parallel sides + distance between them",
        urduDesc = "دو متوازی اضلاع اور ان کے درمیان فاصلہ",
        sindhiDesc = "ٻه متوازي پاسا ۽ انهن جي وچ ۾ فاصلو"
    ),
    FOUR_SIDE_DIAGONAL(
        englishName = "4 Sides with Diagonal",
        urduName = "چار رخی پلاٹ مع وتر",
        sindhiName = "چار پاسا وتر (ترڇي لڪير) سان",
        englishDesc = "Exact legal area using 2 triangles",
        urduDesc = "درست اور پکی پیمائش (دو تکونوں میں تقسیم)",
        sindhiDesc = "صحيح ۽ پڪي ماپ (ٻن ٽڪنڊن ۾ ورهائي)"
    ),
    FOUR_SIDE_ESTIMATE(
        englishName = "4 Sides (No Diagonal)",
        urduName = "چار رخی پلاٹ (بغیر وتر)",
        sindhiName = "چار پاسا (بغير وتر جي)",
        englishDesc = "ESTIMATE ONLY (Average method)",
        urduDesc = "صرف تخمینہ (اوسط فارمولا)",
        sindhiDesc = "رڳو اندازو (سراسري طريقو)"
    ),
    MULTI_SIDE_POLYGON(
        englishName = "5 or More Sides",
        urduName = "پانچ یا زائد اضلاع",
        sindhiName = "5 يا وڌيڪ پاسا",
        englishDesc = "Split into triangles using diagonals",
        urduDesc = "وتر کے ذریعے تکونوں میں تقسیم کر کے",
        sindhiDesc = "وتر جي ذريعي ٽڪنڊن ۾ ورهائي"
    ),
    CURVED_SIDE(
        englishName = "Plot with One Curved Side",
        urduName = "ایک طرف موڑ یا ٹیڑھی حد",
        sindhiName = "هڪ پاسو گول يا ونگ واري حد",
        englishDesc = "Trapezoidal rule from straight baseline",
        urduDesc = "سیدھی لائن سے برابر فاصلوں پر پیمائش",
        sindhiDesc = "سڌي لڪير کان برابر فاصلي تي آف سيٽ ماپ"
    );

    fun getName(lang: Language): String = when (lang) {
        Language.ENGLISH -> englishName
        Language.URDU -> urduName
        Language.SINDHI -> sindhiName
    }

    fun getDescription(lang: Language): String = when (lang) {
        Language.ENGLISH -> englishDesc
        Language.URDU -> urduDesc
        Language.SINDHI -> sindhiDesc
    }
}
