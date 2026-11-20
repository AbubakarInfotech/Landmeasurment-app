package com.example.localization

import com.example.model.Language

object AppStrings {

    fun appTitle(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Land Measurement Calculator"
        Language.URDU -> "زمین کی پیمائش کا کیلکولیٹر"
        Language.SINDHI -> "زمين جي ماپ جو ڪيلڪيوليٽر"
    }

    fun appSubtitle(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Simple, trusted plot calculator for Sindh & Punjab"
        Language.URDU -> "سندھ اور پنجاب کے لیے آسان اور قابلِ اعتماد پیمائش"
        Language.SINDHI -> "سنڌ ۽ پنجاب لاءِ سولو ۽ پڪو زمين ڪيلڪيوليٽر"
    }

    fun chooseLanguage(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Choose Language"
        Language.URDU -> "زبان منتخب کریں"
        Language.SINDHI -> "ٻولي چونڊيو"
    }

    fun chooseRegion(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Preferred Land Units"
        Language.URDU -> "علاقائی اکائیاں منتخب کریں"
        Language.SINDHI -> "علاقائي ماپون چونڊيو"
    }

    fun sindhUnitsDesc(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Shows Wesa, Gunta, Jareeb, and Acre first"
        Language.URDU -> "ویسہ، گنٹہ، جریب اور ایکڑ پہلے دکھائے جائیں گے"
        Language.SINDHI -> "ويهه، گنٽو، جريب ۽ ايڪڙ اڳيان ڏيکاربا"
    }

    fun punjabUnitsDesc(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Shows Marla, Kanal, and Acre first"
        Language.URDU -> "مرلہ، کنال اور ایکڑ پہلے دکھائے جائیں گے"
        Language.SINDHI -> "مرلو، ڪنال ۽ ايڪڙ اڳيان ڏيکاربا"
    }

    fun selectShapeHeader(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Select Plot Shape"
        Language.URDU -> "پلاٹ یا قطعہ کی شکل منتخب کریں"
        Language.SINDHI -> "زمين يا پلاٽ جي شڪل چونڊيو"
    }

    fun selectShapePrompt(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Choose the picture that matches your land plot"
        Language.URDU -> "اپنی زمین کے نقشے سے ملتی جلتی تصویر پر ٹیپ کریں"
        Language.SINDHI -> "پنهنجي زمين جي نقشي جهڙي تصوير تي ٽيپ ڪريو"
    }

    fun dontKnowShapeButton(lang: Language): String = when (lang) {
        Language.ENGLISH -> "I Don't Know the Shape (Help Me)"
        Language.URDU -> "مجھے شکل معلوم نہیں (رہنمائی حاصل کریں)"
        Language.SINDHI -> "مون کي شڪل جي خبر ناهي (رهنمائي وٺو)"
    }

    fun generalPolygonButton(lang: Language): String = when (lang) {
        Language.ENGLISH -> "General 5+ Sided Plot"
        Language.URDU -> "پانچ یا زائد اضلاع والا قطعہ"
        Language.SINDHI -> "5 يا وڌيڪ پاسن وارو قطعو"
    }

    fun enterMeasurements(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Enter Measurements (in Feet)"
        Language.URDU -> "پیمائش درج کریں (فٹ میں)"
        Language.SINDHI -> "ماپون لکو (فوٽن ۾)"
    }

    fun feetUnit(lang: Language): String = when (lang) {
        Language.ENGLISH -> "feet"
        Language.URDU -> "فٹ"
        Language.SINDHI -> "فوٽ"
    }

    fun calculateArea(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Calculate Area"
        Language.URDU -> "رقبہ معلوم کریں"
        Language.SINDHI -> "رقبو ڪڍو"
    }

    fun clearFields(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Clear"
        Language.URDU -> "صاف کریں"
        Language.SINDHI -> "صاف ڪريو"
    }

    fun newMeasurement(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Start New Measurement"
        Language.URDU -> "نئی پیمائش شروع کریں"
        Language.SINDHI -> "نئين ماپ شروع ڪريو"
    }

    fun resultHeader(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Measurement Results"
        Language.URDU -> "پیمائش کا نتیجہ"
        Language.SINDHI -> "ماپ جو نتيجو"
    }

    fun primaryUnitsHeader(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Primary Units"
        Language.URDU -> "بنیادی اکائیاں"
        Language.SINDHI -> "مکيه ماپون"
    }

    fun otherUnitsHeader(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Other Regional & Standard Units"
        Language.URDU -> "دیگر علاقائی اور معیاری اکائیاں"
        Language.SINDHI -> "ٻيون علاقائي ۽ معياري ماپون"
    }

    fun calculationStepsHeader(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Calculation Steps (Proof for Buyer & Seller)"
        Language.URDU -> "حساب کے مراحل (خریدار اور بیچنے والے کے لیے ثبوت)"
        Language.SINDHI -> "حساب جا مرحلا (خريدار ۽ وڪرو ڪندڙ لاءِ پڪ)"
    }

    fun methodUsed(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Method Used"
        Language.URDU -> "طریقہ کار"
        Language.SINDHI -> "طريقو"
    }

    fun estimateWarningTitle(lang: Language): String = when (lang) {
        Language.ENGLISH -> "ATTENTION: ESTIMATE ONLY"
        Language.URDU -> "توجہ فرمائیں: صرف تخمینہ"
        Language.SINDHI -> "ڌيان ڏيو: رڳو اندازو"
    }

    fun estimateWarningText(lang: Language): String = when (lang) {
        Language.ENGLISH -> "This 4-sided average method gives an approximation only. To prevent dispute between buyer and seller, measure a corner-to-corner diagonal across the land for an exact legal calculation."
        Language.URDU -> "یہ پیمائش چار اضلاع کی اوسط سے نکالی گئی ہے جو صرف تخمینہ ہے۔ خریدار اور بیچنے والے کے درمیان کسی تنازع سے بچنے کے لیے ایک کونے سے دوسرے کونے تک وتر ضرور ناپیں۔"
        Language.SINDHI -> "هي چارئي پاسن جي سراسري مان ڪڍيل رڳو اندازو آهي. خريدار ۽ وڪرو ڪندڙ جي وچ ۾ جهيڙي کان بچڻ لاءِ هڪ ڪنڊ کان سامهون ڪنڊ تائين وتر (ترڇي لڪير) ضرور ماپيو."
    }

    fun copySummary(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Share / Copy Summary"
        Language.URDU -> "خلاصہ کاپی / شیئر کریں"
        Language.SINDHI -> "نتيجو ڪاپي / شيئر ڪريو"
    }

    fun copiedToast(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Copied to clipboard!"
        Language.URDU -> "کلپ بورڈ پر کاپی ہو گیا!"
        Language.SINDHI -> "ڪلپ بورڊ تي ڪاپي ٿي ويو!"
    }

    fun loadTestExample(lang: Language, number: Int): String = when (lang) {
        Language.ENGLISH -> "Load Test Case $number"
        Language.URDU -> "مثال $number درج کریں"
        Language.SINDHI -> "مثال $number لکو"
    }

    // Wizard strings
    fun wizardTitle(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Shape Finder Guide"
        Language.URDU -> "شکل کی پہچان میں رہنمائی"
        Language.SINDHI -> "شڪل سڃاڻڻ جي رهنمائي"
    }

    fun wizardQ1(lang: Language): String = when (lang) {
        Language.ENGLISH -> "How many sides does the land plot have?"
        Language.URDU -> "زمین کے کل کتنے رخ یا بازو (اضلاع) ہیں؟"
        Language.SINDHI -> "زمين جا ڪل ڪيترا پاسا آهن؟"
    }

    fun wizardQ2(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Are any two opposite sides parallel (straight and equal distance apart)?"
        Language.URDU -> "کیا آمنے سامنے کے کوئی دو اضلاع بالکل سیدھے اور برابر فاصلے پر ہیں؟"
        Language.SINDHI -> "ڇا آمهون سامهون جا ڪي به ٻه پاسا بلڪل سڌا ۽ برابر فاصلي تي آهن؟"
    }

    fun wizardQ3(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Can you measure a diagonal from one corner to the opposite corner across the plot?"
        Language.URDU -> "کیا آپ زمین کے اندر ایک کونے سے دوسرے کونے تک ترچھی لکیر (وتر) ناپ سکتے ہیں؟"
        Language.SINDHI -> "ڇا توهان زمين جي اندر هڪ ڪنڊ کان سامهون ڪنڊ تائين ترڇي لڪير (وتر) ماپي سگهو ٿا؟"
    }

    fun yes(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Yes"
        Language.URDU -> "ہاں"
        Language.SINDHI -> "ها"
    }

    fun no(lang: Language): String = when (lang) {
        Language.ENGLISH -> "No"
        Language.URDU -> "نہیں"
        Language.SINDHI -> "نه"
    }

    fun threeSides(lang: Language): String = when (lang) {
        Language.ENGLISH -> "3 Sides (Triangle)"
        Language.URDU -> "3 اضلاع (تکون)"
        Language.SINDHI -> "3 پاسا (ٽڪنڊو)"
    }

    fun fourSides(lang: Language): String = when (lang) {
        Language.ENGLISH -> "4 Sides (Most Common)"
        Language.URDU -> "4 اضلاع (عام پلاٹ)"
        Language.SINDHI -> "4 پاسا (عام زمين)"
    }

    fun fiveOrMoreSides(lang: Language): String = when (lang) {
        Language.ENGLISH -> "5 or More Sides"
        Language.URDU -> "5 یا اس سے زیادہ اضلاع"
        Language.SINDHI -> "5 يا ان کان وڌيڪ پاسا"
    }

    fun curvedSide(lang: Language): String = when (lang) {
        Language.ENGLISH -> "One side is curved or bent"
        Language.URDU -> "ایک طرف موڑ یا گھماؤ ہے"
        Language.SINDHI -> "هڪ پاسو گول يا موڙ وارو آهي"
    }

    fun wizardRecommendation(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Recommended Method"
        Language.URDU -> "تجویز کردہ طریقہ"
        Language.SINDHI -> "سفارش ڪيل طريقو"
    }

    fun continueWithMethod(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Continue with This Shape"
        Language.URDU -> "اس شکل سے آگے بڑھیں"
        Language.SINDHI -> "هن شڪل سان اڳتي وڌو"
    }

    fun cancel(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Close"
        Language.URDU -> "بند کریں"
        Language.SINDHI -> "بند ڪريو"
    }

    fun back(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Back"
        Language.URDU -> "پیچھے"
        Language.SINDHI -> "واپس"
    }

    // Units
    fun unitWesa(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Wesa"
        Language.URDU -> "ویسہ"
        Language.SINDHI -> "ويهه / ويسو"
    }

    fun unitGunta(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Gunta"
        Language.URDU -> "گنٹہ"
        Language.SINDHI -> "گنٽو"
    }

    fun unitJareeb(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Jareeb"
        Language.URDU -> "جریب"
        Language.SINDHI -> "جريب"
    }

    fun unitAcre(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Acre"
        Language.URDU -> "ایکڑ"
        Language.SINDHI -> "ايڪڙ"
    }

    fun unitMarla(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Marla"
        Language.URDU -> "مرلہ"
        Language.SINDHI -> "مرلو"
    }

    fun unitKanal(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Kanal"
        Language.URDU -> "کنال"
        Language.SINDHI -> "ڪنال"
    }

    fun unitSqFt(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Square Feet"
        Language.URDU -> "مربع فٹ"
        Language.SINDHI -> "چورس فوٽ"
    }

    fun unitSqM(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Square Meters"
        Language.URDU -> "مربع میٹر"
        Language.SINDHI -> "چورس ميٽر"
    }

    fun pleaseMeasureDiagonal(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Please measure this diagonal"
        Language.URDU -> "براہ کرم یہ وتر ناپیں"
        Language.SINDHI -> "مهرباني ڪري هي وتر ماپيو"
    }

    fun measurementsSummary(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Current Measurements"
        Language.URDU -> "موجودہ پیمائشیں"
        Language.SINDHI -> "موجوده ماپون"
    }

    fun edit(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Edit"
        Language.URDU -> "تبدیل"
        Language.SINDHI -> "تبديل"
    }

    fun sideLabel(letter: String, lang: Language): String = when (lang) {
        Language.ENGLISH -> "Side $letter"
        Language.URDU -> "ضلع $letter"
        Language.SINDHI -> "پاسو $letter"
    }

    fun diagonalLabel(number: String = "", lang: Language): String = when (lang) {
        Language.ENGLISH -> if (number.isEmpty()) "Diagonal" else "Diagonal $number"
        Language.URDU -> if (number.isEmpty()) "وتر" else "وتر $number"
        Language.SINDHI -> if (number.isEmpty()) "وتر" else "وتر $number"
    }

    fun direction(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Direction"
        Language.URDU -> "سمت"
        Language.SINDHI -> "طرف / طرفون"
    }

    fun boundaryNeighbour(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Boundary / Neighbour (Optional)"
        Language.URDU -> "حد / پڑوسی (اختیاری)"
        Language.SINDHI -> "حد / پاڙيسري (اختياري)"
    }

    fun boundaryHint(lang: Language): String = when (lang) {
        Language.ENGLISH -> "e.g. Road, Canal, Ali's land"
        Language.URDU -> "مثلاً سڑک، نہر، علی کی زمین"
        Language.SINDHI -> "مثال رستو، واهه، علي جي زمين"
    }

    fun sideWord(lang: Language): String = when (lang) {
        Language.ENGLISH -> "side"
        Language.URDU -> "طرف"
        Language.SINDHI -> "طرف"
    }

    fun duplicateDirectionWarning(directionName: String, lang: Language): String = when (lang) {
        Language.ENGLISH -> "Notice: Multiple sides have been assigned '$directionName'. Please check for accuracy."
        Language.URDU -> "توجہ فرمائیں: ایک سے زیادہ اضلاع کو '$directionName' منتخب کیا گیا ہے۔ براہ کرم تصدیق کریں۔"
        Language.SINDHI -> "ڌيان ڏيو: هڪ کان وڌيڪ پاسن کي '$directionName' چونڊيو ويو آهي. مهرباني ڪري چڪاس ڪريو."
    }

    fun noDirection(lang: Language): String = when (lang) {
        Language.ENGLISH -> "None (Side A, B...)"
        Language.URDU -> "کوئی نہیں (ضلع A، B)"
        Language.SINDHI -> "ڪوبه نه (پاسو A، B)"
    }

    fun compassNorth(lang: Language): String = when (lang) {
        Language.ENGLISH -> "N"
        Language.URDU -> "ش"
        Language.SINDHI -> "ا"
    }

    fun measureByWalking(lang: Language): String = when (lang) {
        Language.ENGLISH -> "🚶 Measure by Walking (GPS)"
        Language.URDU -> "🚶 پیدل چل کر ناپیں (GPS)"
        Language.SINDHI -> "🚶 پنڌ ڪري ماپيو (GPS)"
    }

    fun measureByWalkingShort(lang: Language): String = when (lang) {
        Language.ENGLISH -> "GPS Walking"
        Language.URDU -> "پیدل ناپیں"
        Language.SINDHI -> "پنڌ ماپ"
    }

    fun markCorner(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Mark Corner"
        Language.URDU -> "کونہ نشان لگائیں"
        Language.SINDHI -> "ڪنڊ نشان لڳايو"
    }

    fun closeThePlot(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Close the Plot"
        Language.URDU -> "قطعہ مکمل کریں"
        Language.SINDHI -> "قطعو پورو ڪريو"
    }

    fun undoLastCorner(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Undo Corner"
        Language.URDU -> "آخری نشان مٹائیں"
        Language.SINDHI -> "آخري نشان هٽايو"
    }

    fun startAgain(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Start Again"
        Language.URDU -> "دوبارہ شروع کریں"
        Language.SINDHI -> "وري شروع ڪريو"
    }

    fun liveDistanceSoFar(distanceFeet: String, dirName: String, lang: Language): String = when (lang) {
        Language.ENGLISH -> "$dirName side: $distanceFeet ft so far"
        Language.URDU -> "$dirName طرف: اب تک $distanceFeet فٹ"
        Language.SINDHI -> "$dirName طرف: هينئر تائين $distanceFeet فوٽ"
    }

    fun gpsAccuracy(meters: String, lang: Language): String = when (lang) {
        Language.ENGLISH -> "GPS Accuracy: about $meters m"
        Language.URDU -> "GPS درستگی: تقریباً $meters میٹر"
        Language.SINDHI -> "GPS درستي: اٽڪل $meters ميٽر"
    }

    fun gpsLowAccuracyWarning(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Warning: GPS signal is weak (> 10 m). Measurements may be less accurate."
        Language.URDU -> "توجہ: GPS سگنل کمزور ہے (> 10 میٹر)۔ پیمائش میں فرق ہو سکتا ہے۔"
        Language.SINDHI -> "خبردار: GPS سگنل ڪمزور آهي (> 10 ميٽر). ماپ ۾ فرق ٿي سگهي ٿو."
    }

    fun gpsEstimateDisputeNotice(lang: Language): String = when (lang) {
        Language.ENGLISH -> "GPS measurement is an estimate. For a sale, confirm with a tape measure."
        Language.URDU -> "GPS پیمائش صرف تخمینہ ہے۔ زمین کی خرید و فروخت کے لیے فیتے (ٹیپ) سے تصدیق ضرور کریں۔"
        Language.SINDHI -> "GPS ماپ صرف اندازو آهي. زمين جي وڪري لاءِ فتي (ٽيپ) سان تصديق ضرور ڪريو."
    }

    fun locationPermissionNeeded(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Location Permission Required"
        Language.URDU -> "لوکیشن کی اجازت درکار ہے"
        Language.SINDHI -> "لوڪيشن جي اجازت گهربل آهي"
    }

    fun locationPermissionDesc(lang: Language): String = when (lang) {
        Language.ENGLISH -> "To measure your plot boundaries by walking, please allow location access."
        Language.URDU -> "پلاٹ کے گرد چل کر پیمائش کرنے کے لیے لوکیشن کی اجازت فراہم کریں۔"
        Language.SINDHI -> "پلاٽ جي چئوطرف گهمي ماپڻ لاءِ لوڪيشن جي اجازت ڏيو."
    }

    fun grantPermission(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Enable GPS Location"
        Language.URDU -> "GPS آن کریں"
        Language.SINDHI -> "GPS شروع ڪريو"
    }

    fun continueManual(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Continue with Manual Entry"
        Language.URDU -> "دستی پیمائش درج کریں"
        Language.SINDHI -> "هٿ سان ماپ لکو"
    }

    fun satellite(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Satellite"
        Language.URDU -> "سیٹلائٹ"
        Language.SINDHI -> "سيٽلائيٽ"
    }

    fun streetMap(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Street Map"
        Language.URDU -> "نقشہ"
        Language.SINDHI -> "نقشو"
    }

    fun cornersMarked(count: Int, lang: Language): String = when (lang) {
        Language.ENGLISH -> "Corners Marked: $count"
        Language.URDU -> "نشان شدہ کونے: $count"
        Language.SINDHI -> "نشان لڳل ڪنڊون: $count"
    }

    // Quick Measure Strings
    fun quickMeasureTitle(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Quick Measure (No Shape Needed)"
        Language.URDU -> "فوری پیمائش (بغیر شکل منتخب کیے)"
        Language.SINDHI -> "تڪڙي ماپ (بغير شڪل چونڊڻ جي)"
    }

    fun quickMeasureSubtitle(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Enter sides as a list or one by one. Diagram & area update live."
        Language.URDU -> "اضلاع کی فہرست درج کریں یا ایک ایک کر کے لکھیں۔ رقبہ اور نقشہ فوراً تیار ہوگا۔"
        Language.SINDHI -> "پاسن جي لسٽ لکو يا هڪ هڪ ڪري درج ڪريو. رقبو ۽ نقشو ترت ٺهي ويندو."
    }

    fun quickMeasureInputList(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Type List of Sides"
        Language.URDU -> "فہرست درج کریں"
        Language.SINDHI -> "لسٽ لکو"
    }

    fun quickMeasureInputOneByOne(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Enter One by One"
        Language.URDU -> "ایک ایک کر کے"
        Language.SINDHI -> "هڪ هڪ ڪري"
    }

    fun quickMeasureListLabel(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Enter sides in order around plot (e.g. 65, 295, 55, 295)"
        Language.URDU -> "پلاٹ کے گرد ترتیب وار اضلاع لکھیں (مثلاً 65, 295, 55, 295)"
        Language.SINDHI -> "زمين جي چئوطرف ترتيب سان پاسا لکو (مثال 65, 295, 55, 295)"
    }

    fun quickMeasureListPlaceholder(lang: Language): String = when (lang) {
        Language.ENGLISH -> "65, 295, 55, 295"
        Language.URDU -> "65, 295, 55, 295"
        Language.SINDHI -> "65, 295, 55, 295"
    }

    fun addSide(lang: Language): String = when (lang) {
        Language.ENGLISH -> "+ Add Side"
        Language.URDU -> "+ نیا ضلع شامل کریں"
        Language.SINDHI -> "+ نئون پاسو شامل ڪريو"
    }

    fun removeSide(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Remove"
        Language.URDU -> "حذف کریں"
        Language.SINDHI -> "هٽايو"
    }

    fun addDiagonal(lang: Language): String = when (lang) {
        Language.ENGLISH -> "+ Add Diagonal (Optional)"
        Language.URDU -> "+ وتر شامل کریں (اختیاری)"
        Language.SINDHI -> "+ وتر شامل ڪريو (اختياري)"
    }

    fun removeDiagonal(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Remove Diagonal"
        Language.URDU -> "وتر ہٹائیں"
        Language.SINDHI -> "وتر هٽايو"
    }

    fun diagonalFieldLabel(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Diagonal (Corner 1 to Corner 3)"
        Language.URDU -> "وتر (کونے 1 سے کونے 3 تک)"
        Language.SINDHI -> "وتر (ڪنڊ 1 کان ڪنڊ 3 تائين)"
    }

    fun badgeExact(lang: Language): String = when (lang) {
        Language.ENGLISH -> "EXACT"
        Language.URDU -> "پکا اور درست"
        Language.SINDHI -> "پڪو ۽ صحيح"
    }

    fun badgeEstimate(lang: Language): String = when (lang) {
        Language.ENGLISH -> "ESTIMATE ONLY"
        Language.URDU -> "صرف تخمینہ"
        Language.SINDHI -> "رڳو اندازو"
    }

    fun badgeRoughEstimate(lang: Language): String = when (lang) {
        Language.ENGLISH -> "ROUGH ESTIMATE"
        Language.URDU -> "کچا اندازہ"
        Language.SINDHI -> "ڪچو اندازو"
    }

    fun whatMakesItExactTitle(lang: Language): String = when (lang) {
        Language.ENGLISH -> "How to make this measurement exact:"
        Language.URDU -> "پیمائش کو پکا بنانے کی ہدایت:"
        Language.SINDHI -> "ماپ پڪي ڪرڻ لاءِ هدايت:"
    }

    fun addAnotherPart(lang: Language): String = when (lang) {
        Language.ENGLISH -> "+ Add Another Part (Piece of Land)"
        Language.URDU -> "+ زمین کا اگلا ٹکڑا / حصہ شامل کریں"
        Language.SINDHI -> "+ زمين جو ٻيو ٽڪرو / ڀاڱو شامل ڪريو"
    }

    fun deletePart(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Remove This Part"
        Language.URDU -> "یہ حصہ ختم کریں"
        Language.SINDHI -> "هي ڀاڱو ختم ڪريو"
    }

    fun partTabLabel(partNumber: Int, lang: Language): String = when (lang) {
        Language.ENGLISH -> "Part $partNumber"
        Language.URDU -> "حصہ $partNumber"
        Language.SINDHI -> "ڀاڱو $partNumber"
    }

    fun combinedTotalTitle(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Total Combined Area (All Parts)"
        Language.URDU -> "تمام ٹکڑوں کا کل مجموعی رقبہ"
        Language.SINDHI -> "سڀني ٽڪرن جو گڏيل ڪل رقبو"
    }

    fun partsCountSubtitle(count: Int, lang: Language): String = when (lang) {
        Language.ENGLISH -> "Total of $count pieces of land combined"
        Language.URDU -> "کل $count ٹکڑوں کا مشترکہ مجموعہ"
        Language.SINDHI -> "ڪل $count ٽڪرن جو گڏيل مجموعو"
    }

    fun chooseShapeModeBtn(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Choose by Shape"
        Language.URDU -> "شکل کے ذریعے ناپیں"
        Language.SINDHI -> "شڪل جي ذريعي ماپيو"
    }

    fun quickMeasureModeBtn(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Quick Measure (No Shape)"
        Language.URDU -> "فوری پیمائش (بغیر شکل)"
        Language.SINDHI -> "تڪڙي ماپ (بغير شڪل)"
    }

    fun testCaseChip1(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Test 1: 65, 295, 55, 295 (Estimate)"
        Language.URDU -> "ٹیسٹ 1: 65، 295، 55، 295 (تخمینہ)"
        Language.SINDHI -> "ٽيسٽ 1: 65، 295، 55، 295 (اندازو)"
    }

    fun testCaseChip2(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Test 2: 33, 33, 33, 33 (1 Wesa Exact)"
        Language.URDU -> "ٹیسٹ 2: 33، 33، 33، 33 (1 ویسہ پکا)"
        Language.SINDHI -> "ٽيسٽ 2: 33، 33، 33، 33 (1 ويهو پڪو)"
    }

    fun testCaseChip3(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Test 3: 10, 5, 3 (Impossible)"
        Language.URDU -> "ٹیسٹ 3: 10، 5، 3 (ناممکن)"
        Language.SINDHI -> "ٽيسٽ 3: 10، 5، 3 (ناممڪن)"
    }
}
