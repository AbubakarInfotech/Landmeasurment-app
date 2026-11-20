package com.example.calculator

import com.example.model.CalculationResult
import com.example.model.CalculationStep
import com.example.model.Language
import com.example.model.PlotShape
import java.text.DecimalFormat
import kotlin.math.sqrt

sealed class CalcOutcome {
    data class Success(val result: CalculationResult) : CalcOutcome()
    data class Error(
        val messageEn: String,
        val messageUr: String,
        val messageSd: String
    ) : CalcOutcome() {
        fun getMessage(lang: Language): String = when (lang) {
            Language.ENGLISH -> messageEn
            Language.URDU -> messageUr
            Language.SINDHI -> messageSd
        }
    }
}

object LandCalculator {

    // Conversion Constants strictly as defined
    const val SQ_FT_PER_WESA = 1089.0
    const val SQ_FT_PER_GUNTA = 1089.0
    const val SQ_FT_PER_JAREEB = 20.0 * 1089.0 // 21,780 sq ft
    const val SQ_FT_PER_ACRE = 43560.0        // 40 wesa = 43,560 sq ft
    const val SQ_FT_PER_MARLA = 272.25
    const val SQ_FT_PER_KANAL = 20.0 * 272.25 // 5,445 sq ft
    const val SQ_FT_PER_SQ_METER = 10.7639104

    private val df2 = DecimalFormat("#,##0.00")
    private val df4 = DecimalFormat("#,##0.0000")

    fun formatNumber(value: Double): String {
        return if (value % 1.0 == 0.0) {
            DecimalFormat("#,##0").format(value)
        } else {
            df2.format(value)
        }
    }

    fun formatNumberAccurate(value: Double): String {
        return if (value % 1.0 == 0.0) {
            DecimalFormat("#,##0").format(value)
        } else {
            val s2 = df2.format(value)
            val s4 = df4.format(value)
            if (s4.endsWith("00")) s2 else s4
        }
    }

    private fun validatePositive(name: String, value: Double?): CalcOutcome.Error? {
        if (value == null) {
            return CalcOutcome.Error(
                messageEn = "Please enter a value for $name.",
                messageUr = "براہ کرم $name کی پیمائش درج کریں۔",
                messageSd = "مهرباني ڪري $name جي ماپ لکو."
            )
        }
        if (value <= 0.0) {
            return CalcOutcome.Error(
                messageEn = "$name must be greater than 0 feet (cannot be 0 or negative).",
                messageUr = "$name صفر یا منفی نہیں ہو سکتا، 0 سے بڑی پیمائش درج کریں۔",
                messageSd = "$name ٻُڙي (0) يا ڪاٽو نٿو ٿي سگهي، 0 کان وڏي ماپ لکو."
            )
        }
        return null
    }

    private fun checkTriangleInequality(
        a: Double,
        b: Double,
        c: Double,
        labelA: String = "Side 1",
        labelB: String = "Side 2",
        labelC: String = "Side 3"
    ): CalcOutcome.Error? {
        if (a + b <= c || a + c <= b || b + c <= a) {
            val maxSide = maxOf(a, b, c)
            val sumOtherTwo = (a + b + c) - maxSide
            return CalcOutcome.Error(
                messageEn = "Impossible triangle! The longest side (${formatNumber(maxSide)} ft) is greater than or equal to the sum of the other two sides (${formatNumber(sumOtherTwo)} ft). Please re-check measurements.",
                messageUr = "یہ مثلث بننا ناممکن ہے! سب سے لمبا ضلع (${formatNumber(maxSide)} فٹ) باقی دونوں اضلاع کے مجموعے (${formatNumber(sumOtherTwo)} فٹ) کے برابر یا بڑا ہے۔ پیمائش دوبارہ چیک کریں۔",
                messageSd = "هي ٽڪنڊو ٺهڻ ناممڪن آهي! سڀ کان ڊگهو پاسو (${formatNumber(maxSide)} فوٽ) ٻين ٻن پاسن جي جوڙ (${formatNumber(sumOtherTwo)} فوٽ) جي برابر يا ان کان وڏو آهي. ماپ وري چيڪ ڪريو."
            )
        }
        return null
    }

    private fun createResult(
        shape: PlotShape,
        sqFeet: Double,
        isEstimate: Boolean,
        methodNameEn: String,
        methodNameUr: String,
        methodNameSd: String,
        steps: List<CalculationStep>,
        warningEn: String? = null,
        warningUr: String? = null,
        warningSd: String? = null
    ): CalculationResult {
        // Conversions calculated directly without early rounding
        val wesa = sqFeet / SQ_FT_PER_WESA
        val gunta = sqFeet / SQ_FT_PER_GUNTA
        val jareeb = sqFeet / SQ_FT_PER_JAREEB
        val acre = sqFeet / SQ_FT_PER_ACRE
        val marla = sqFeet / SQ_FT_PER_MARLA
        val kanal = sqFeet / SQ_FT_PER_KANAL
        val sqMeters = sqFeet / SQ_FT_PER_SQ_METER

        val conversionSteps = mutableListOf<CalculationStep>()
        conversionSteps.addAll(steps)

        conversionSteps.add(
            CalculationStep(
                titleEn = "Total Area in Square Feet",
                titleUr = "مربع فٹ میں کل رقبہ",
                titleSd = "چورس فوٽ ۾ ڪل رقبو",
                detailEn = "Area = ${formatNumberAccurate(sqFeet)} sq ft",
                detailUr = "رقبہ = ${formatNumberAccurate(sqFeet)} مربع فٹ",
                detailSd = "رقبو = ${formatNumberAccurate(sqFeet)} چورس فوٽ"
            )
        )

        conversionSteps.add(
            CalculationStep(
                titleEn = "Conversion to Sindh Units (Wesa, Gunta, Jareeb)",
                titleUr = "سندھ کی اکائیوں میں تبدیلی (ویسہ، گنٹہ، جریب)",
                titleSd = "سنڌ جي ماپن ۾ تبديلي (ويهه، گنٽو، جريب)",
                detailEn = "• Wesa = Area ÷ 1,089 = ${formatNumberAccurate(sqFeet)} ÷ 1,089 = ${formatNumberAccurate(wesa)} wesa\n" +
                        "• Gunta = 1 Wesa = ${formatNumberAccurate(gunta)} gunta\n" +
                        "• Jareeb = Area ÷ 21,780 (20 wesa) = ${formatNumberAccurate(jareeb)} jareeb",
                detailUr = "• ویسہ = رقبہ ÷ 1,089 = ${formatNumberAccurate(sqFeet)} ÷ 1,089 = ${formatNumberAccurate(wesa)} ویسہ\n" +
                        "• گنٹہ = 1 ویسہ = ${formatNumberAccurate(gunta)} گنٹہ\n" +
                        "• جریب = رقبہ ÷ 21,780 (20 ویسہ) = ${formatNumberAccurate(jareeb)} جریب",
                detailSd = "• ويهه = رقبو ÷ 1,089 = ${formatNumberAccurate(sqFeet)} ÷ 1,089 = ${formatNumberAccurate(wesa)} ويهه\n" +
                        "• گنٽو = 1 ويهه = ${formatNumberAccurate(gunta)} گنٽو\n" +
                        "• جريب = رقبو ÷ 21,780 (20 ويهه) = ${formatNumberAccurate(jareeb)} جريب"
            )
        )

        conversionSteps.add(
            CalculationStep(
                titleEn = "Conversion to Punjab Units (Marla, Kanal, Acre)",
                titleUr = "پنجاب کی اکائیوں میں تبدیلی (مرلہ، کنال، ایکڑ)",
                titleSd = "پنجاب جي ماپن ۾ تبديلي (مرلو، ڪنال، ايڪڙ)",
                detailEn = "• Marla = Area ÷ 272.25 = ${formatNumberAccurate(sqFeet)} ÷ 272.25 = ${formatNumberAccurate(marla)} marla\n" +
                        "• Kanal = Area ÷ 5,445 (20 marla) = ${formatNumberAccurate(kanal)} kanal\n" +
                        "• Acre = Area ÷ 43,560 (8 kanal) = ${formatNumberAccurate(acre)} acre",
                detailUr = "• مرلہ = رقبہ ÷ 272.25 = ${formatNumberAccurate(sqFeet)} ÷ 272.25 = ${formatNumberAccurate(marla)} مرلہ\n" +
                        "• کنال = رقبہ ÷ 5,445 (20 مرلہ) = ${formatNumberAccurate(kanal)} کنال\n" +
                        "• ایکڑ = رقبہ ÷ 43,560 (8 کنال) = ${formatNumberAccurate(acre)} ایکڑ",
                detailSd = "• مرلو = رقبو ÷ 272.25 = ${formatNumberAccurate(sqFeet)} ÷ 272.25 = ${formatNumberAccurate(marla)} مرلو\n" +
                        "• ڪنال = رقبو ÷ 5,445 (20 مرلا) = ${formatNumberAccurate(kanal)} ڪنال\n" +
                        "• ايڪڙ = رقبو ÷ 43,560 (8 ڪنال) = ${formatNumberAccurate(acre)} ايڪڙ"
            )
        )

        conversionSteps.add(
            CalculationStep(
                titleEn = "Standard Units (Square Meters & Acre)",
                titleUr = "معیاری اکائیاں (مربع میٹر اور ایکڑ)",
                titleSd = "معياري ماپون (چورس ميٽر ۽ ايڪڙ)",
                detailEn = "• Square Meters = Area ÷ 10.7639 = ${formatNumberAccurate(sqMeters)} sq m\n" +
                        "• Acre = ${formatNumberAccurate(acre)} acre",
                detailUr = "• مربع میٹر = رقبہ ÷ 10.7639 = ${formatNumberAccurate(sqMeters)} مربع میٹر\n" +
                        "• ایکڑ = ${formatNumberAccurate(acre)} ایکڑ",
                detailSd = "• چورس ميٽر = رقبو ÷ 10.7639 = ${formatNumberAccurate(sqMeters)} چورس ميٽر\n" +
                        "• ايڪڙ = ${formatNumberAccurate(acre)} ايڪڙ"
            )
        )

        return CalculationResult(
            shape = shape,
            sqFeet = sqFeet,
            sqMeters = sqMeters,
            wesa = wesa,
            gunta = gunta,
            jareeb = jareeb,
            acre = acre,
            marla = marla,
            kanal = kanal,
            isEstimate = isEstimate,
            methodNameEn = methodNameEn,
            methodNameUr = methodNameUr,
            methodNameSd = methodNameSd,
            steps = conversionSteps,
            warningEn = warningEn,
            warningUr = warningUr,
            warningSd = warningSd
        )
    }

    // 1. RECTANGLE / SQUARE
    fun calculateRectangle(length: Double?, width: Double?): CalcOutcome {
        validatePositive("Length", length)?.let { return it }
        validatePositive("Width", width)?.let { return it }
        val l = length!!
        val w = width!!

        val sqFeet = l * w
        val steps = listOf(
            CalculationStep(
                titleEn = "Rectangle Formula: Length × Width",
                titleUr = "مستطیل کا فارمولا: لمبائی × چوڑائی",
                titleSd = "مستطيل جو فارمولو: ڊيگهه × ويڪر",
                detailEn = "Length = ${formatNumber(l)} ft, Width = ${formatNumber(w)} ft\nArea = ${formatNumber(l)} × ${formatNumber(w)} = ${formatNumberAccurate(sqFeet)} sq ft",
                detailUr = "لمبائی = ${formatNumber(l)} فٹ، چوڑائی = ${formatNumber(w)} فٹ\nرقبہ = ${formatNumber(l)} × ${formatNumber(w)} = ${formatNumberAccurate(sqFeet)} مربع فٹ",
                detailSd = "ڊيگهه = ${formatNumber(l)} فوٽ، ويڪر = ${formatNumber(w)} فوٽ\nرقبو = ${formatNumber(l)} × ${formatNumber(w)} = ${formatNumberAccurate(sqFeet)} چورس فوٽ"
            )
        )

        return CalcOutcome.Success(
            createResult(
                shape = PlotShape.RECTANGLE,
                sqFeet = sqFeet,
                isEstimate = false,
                methodNameEn = "Length × Width (Exact)",
                methodNameUr = "لمبائی ضرب چوڑائی (درست)",
                methodNameSd = "ڊيگهه ضرب ويڪر (پڪي ماپ)",
                steps = steps
            )
        )
    }

    // 2. TRIANGLE (Heron's Formula)
    fun calculateTriangle(a: Double?, b: Double?, c: Double?): CalcOutcome {
        validatePositive("Side 1", a)?.let { return it }
        validatePositive("Side 2", b)?.let { return it }
        validatePositive("Side 3", c)?.let { return it }
        val sa = a!!
        val sb = b!!
        val sc = c!!

        checkTriangleInequality(sa, sb, sc)?.let { return it }

        val s = (sa + sb + sc) / 2.0
        val radicand = s * (s - sa) * (s - sb) * (s - sc)
        val sqFeet = sqrt(radicand)

        val steps = listOf(
            CalculationStep(
                titleEn = "Step 1: Calculate Semi-perimeter (s)",
                titleUr = "مرحلہ 1: نصف احاطہ (s) معلوم کریں",
                titleSd = "قدم 1: اڌ احاطو (s) معلوم ڪريو",
                detailEn = "s = (Side 1 + Side 2 + Side 3) ÷ 2\ns = (${formatNumber(sa)} + ${formatNumber(sb)} + ${formatNumber(sc)}) ÷ 2 = ${formatNumber(sa + sb + sc)} ÷ 2 = ${formatNumberAccurate(s)} ft",
                detailUr = "s = (ضلع 1 + ضلع 2 + ضلع 3) ÷ 2\ns = (${formatNumber(sa)} + ${formatNumber(sb)} + ${formatNumber(sc)}) ÷ 2 = ${formatNumber(sa + sb + sc)} ÷ 2 = ${formatNumberAccurate(s)} فٹ",
                detailSd = "s = (پاسو 1 + پاسو 2 + پاسو 3) ÷ 2\ns = (${formatNumber(sa)} + ${formatNumber(sb)} + ${formatNumber(sc)}) ÷ 2 = ${formatNumber(sa + sb + sc)} ÷ 2 = ${formatNumberAccurate(s)} فوٽ"
            ),
            CalculationStep(
                titleEn = "Step 2: Heron's Formula",
                titleUr = "مرحلہ 2: ہیروئن فارمولا کا نفاذ",
                titleSd = "قدم 2: هيرون فارمولو لاڳو ڪريو",
                detailEn = "Area = √[s × (s - a) × (s - b) × (s - c)]\n" +
                        "(s - a) = ${formatNumberAccurate(s - sa)}\n" +
                        "(s - b) = ${formatNumberAccurate(s - sb)}\n" +
                        "(s - c) = ${formatNumberAccurate(s - sc)}\n" +
                        "Radicand = ${formatNumberAccurate(s)} × ${formatNumberAccurate(s - sa)} × ${formatNumberAccurate(s - sb)} × ${formatNumberAccurate(s - sc)} = ${formatNumberAccurate(radicand)}\n" +
                        "Area = √${formatNumberAccurate(radicand)} = ${formatNumberAccurate(sqFeet)} sq ft",
                detailUr = "رقبہ = √[s × (s - a) × (s - b) × (s - c)]\n" +
                        "(s - a) = ${formatNumberAccurate(s - sa)}\n" +
                        "(s - b) = ${formatNumberAccurate(s - sb)}\n" +
                        "(s - c) = ${formatNumberAccurate(s - sc)}\n" +
                        "ضرب = ${formatNumberAccurate(radicand)}\n" +
                        "رقبہ = √${formatNumberAccurate(radicand)} = ${formatNumberAccurate(sqFeet)} مربع فٹ",
                detailSd = "رقبو = √[s × (s - a) × (s - b) × (s - c)]\n" +
                        "(s - a) = ${formatNumberAccurate(s - sa)}\n" +
                        "(s - b) = ${formatNumberAccurate(s - sb)}\n" +
                        "(s - c) = ${formatNumberAccurate(s - sc)}\n" +
                        "ضرب = ${formatNumberAccurate(radicand)}\n" +
                        "رقبو = √${formatNumberAccurate(radicand)} = ${formatNumberAccurate(sqFeet)} چورس فوٽ"
            )
        )

        return CalcOutcome.Success(
            createResult(
                shape = PlotShape.TRIANGLE,
                sqFeet = sqFeet,
                isEstimate = false,
                methodNameEn = "Heron's Formula from 3 Sides (Exact)",
                methodNameUr = "تین اضلاع سے ہیروئن فارمولا (درست)",
                methodNameSd = "ٽن پاسن مان هيرون فارمولو (پڪي ماپ)",
                steps = steps
            )
        )
    }

    // 3. TRAPEZIUM
    fun calculateTrapezium(parallelSide1: Double?, parallelSide2: Double?, distance: Double?): CalcOutcome {
        validatePositive("Parallel Side 1", parallelSide1)?.let { return it }
        validatePositive("Parallel Side 2", parallelSide2)?.let { return it }
        validatePositive("Distance Between Sides", distance)?.let { return it }
        val p1 = parallelSide1!!
        val p2 = parallelSide2!!
        val h = distance!!

        val avgParallel = (p1 + p2) / 2.0
        val sqFeet = avgParallel * h

        val steps = listOf(
            CalculationStep(
                titleEn = "Trapezium Formula: (Side 1 + Side 2) ÷ 2 × Distance",
                titleUr = "ٹریپیزیم فارمولا: (پہلا متوازی + دوسرا متوازی) ÷ 2 × فاصلہ",
                titleSd = "ٽريپيزيم فارمولو: (پريون پاسو + ٻيون پاسو) ÷ 2 × وڇوٽي",
                detailEn = "Average of parallel sides = (${formatNumber(p1)} + ${formatNumber(p2)}) ÷ 2 = ${formatNumber(p1 + p2)} ÷ 2 = ${formatNumberAccurate(avgParallel)} ft\n" +
                        "Area = ${formatNumberAccurate(avgParallel)} × ${formatNumber(h)} = ${formatNumberAccurate(sqFeet)} sq ft",
                detailUr = "متوازی اضلاع کی اوسط = (${formatNumber(p1)} + ${formatNumber(p2)}) ÷ 2 = ${formatNumber(p1 + p2)} ÷ 2 = ${formatNumberAccurate(avgParallel)} فٹ\n" +
                        "رقبہ = ${formatNumberAccurate(avgParallel)} × ${formatNumber(h)} = ${formatNumberAccurate(sqFeet)} مربع فٹ",
                detailSd = "متوازي پاسن جي سراسري = (${formatNumber(p1)} + ${formatNumber(p2)}) ÷ 2 = ${formatNumber(p1 + p2)} ÷ 2 = ${formatNumberAccurate(avgParallel)} فوٽ\n" +
                        "رقبو = ${formatNumberAccurate(avgParallel)} × ${formatNumber(h)} = ${formatNumberAccurate(sqFeet)} چورس فوٽ"
            )
        )

        return CalcOutcome.Success(
            createResult(
                shape = PlotShape.TRAPEZIUM,
                sqFeet = sqFeet,
                isEstimate = false,
                methodNameEn = "Trapezium Formula (Exact)",
                methodNameUr = "ٹریپیزیم فارمولا (درست)",
                methodNameSd = "ٽريپيزيم فارمولو (پڪي ماپ)",
                steps = steps
            )
        )
    }

    // 4. IRREGULAR 4-SIDED WITH DIAGONAL (Accurate Triangulation)
    fun calculateFourSideDiagonal(
        side1: Double?,
        side2: Double?,
        side3: Double?,
        side4: Double?,
        diagonal: Double?
    ): CalcOutcome {
        validatePositive("Side 1", side1)?.let { return it }
        validatePositive("Side 2", side2)?.let { return it }
        validatePositive("Side 3", side3)?.let { return it }
        validatePositive("Side 4", side4)?.let { return it }
        validatePositive("Diagonal", diagonal)?.let { return it }
        val s1 = side1!!
        val s2 = side2!!
        val s3 = side3!!
        val s4 = side4!!
        val d = diagonal!!

        // Triangle 1: s1, s2, d
        checkTriangleInequality(s1, s2, d, "Side 1", "Side 2", "Diagonal")?.let {
            return CalcOutcome.Error(
                messageEn = "Triangle 1 (Side 1, Side 2, Diagonal) is invalid: ${it.messageEn}",
                messageUr = "پہلی تکون (ضلع 1، ضلع 2، وتر) ممکن نہیں: ${it.messageUr}",
                messageSd = "پھريون ٽڪنڊو (پاسو 1، پاسو 2، وتر) ممڪن ناهي: ${it.messageSd}"
            )
        }

        // Triangle 2: s3, s4, d
        checkTriangleInequality(s3, s4, d, "Side 3", "Side 4", "Diagonal")?.let {
            return CalcOutcome.Error(
                messageEn = "Triangle 2 (Side 3, Side 4, Diagonal) is invalid: ${it.messageEn}",
                messageUr = "دوسری تکون (ضلع 3، ضلع 4، وتر) ممکن نہیں: ${it.messageUr}",
                messageSd = "ٻيون ٽڪنڊو (پاسو 3، پاسو 4، وتر) ممڪن ناهي: ${it.messageSd}"
            )
        }

        val semi1 = (s1 + s2 + d) / 2.0
        val area1 = sqrt(semi1 * (semi1 - s1) * (semi1 - s2) * (semi1 - d))

        val semi2 = (s3 + s4 + d) / 2.0
        val area2 = sqrt(semi2 * (semi2 - s3) * (semi2 - s4) * (semi2 - d))

        val sqFeet = area1 + area2

        val steps = listOf(
            CalculationStep(
                titleEn = "Triangle 1 (Sides: ${formatNumber(s1)}, ${formatNumber(s2)}, Diagonal: ${formatNumber(d)} ft)",
                titleUr = "پہلی تکون (اضلاع: ${formatNumber(s1)}، ${formatNumber(s2)}، وتر: ${formatNumber(d)} فٹ)",
                titleSd = "پهريون ٽڪنڊو (پاسا: ${formatNumber(s1)}، ${formatNumber(s2)}، وتر: ${formatNumber(d)} فوٽ)",
                detailEn = "Semi-perimeter s1 = (${formatNumber(s1)} + ${formatNumber(s2)} + ${formatNumber(d)}) ÷ 2 = ${formatNumberAccurate(semi1)} ft\n" +
                        "Area 1 = √[${formatNumberAccurate(semi1)} × ${formatNumberAccurate(semi1 - s1)} × ${formatNumberAccurate(semi1 - s2)} × ${formatNumberAccurate(semi1 - d)}] = ${formatNumberAccurate(area1)} sq ft",
                detailUr = "نصف احاطہ s1 = (${formatNumber(s1)} + ${formatNumber(s2)} + ${formatNumber(d)}) ÷ 2 = ${formatNumberAccurate(semi1)} فٹ\n" +
                        "رقبہ 1 = ${formatNumberAccurate(area1)} مربع فٹ",
                detailSd = "اڌ احاطو s1 = (${formatNumber(s1)} + ${formatNumber(s2)} + ${formatNumber(d)}) ÷ 2 = ${formatNumberAccurate(semi1)} فوٽ\n" +
                        "رقبو 1 = ${formatNumberAccurate(area1)} چورس فوٽ"
            ),
            CalculationStep(
                titleEn = "Triangle 2 (Sides: ${formatNumber(s3)}, ${formatNumber(s4)}, Diagonal: ${formatNumber(d)} ft)",
                titleUr = "دوسری تکون (اضلاع: ${formatNumber(s3)}، ${formatNumber(s4)}، وتر: ${formatNumber(d)} فٹ)",
                titleSd = "ٻيون ٽڪنڊو (پاسا: ${formatNumber(s3)}، ${formatNumber(s4)}، وتر: ${formatNumber(d)} فوٽ)",
                detailEn = "Semi-perimeter s2 = (${formatNumber(s3)} + ${formatNumber(s4)} + ${formatNumber(d)}) ÷ 2 = ${formatNumberAccurate(semi2)} ft\n" +
                        "Area 2 = √[${formatNumberAccurate(semi2)} × ${formatNumberAccurate(semi2 - s3)} × ${formatNumberAccurate(semi2 - s4)} × ${formatNumberAccurate(semi2 - d)}] = ${formatNumberAccurate(area2)} sq ft",
                detailUr = "نصف احاطہ s2 = (${formatNumber(s3)} + ${formatNumber(s4)} + ${formatNumber(d)}) ÷ 2 = ${formatNumberAccurate(semi2)} فٹ\n" +
                        "رقبہ 2 = ${formatNumberAccurate(area2)} مربع فٹ",
                detailSd = "اڌ احاطو s2 = (${formatNumber(s3)} + ${formatNumber(s4)} + ${formatNumber(d)}) ÷ 2 = ${formatNumberAccurate(semi2)} فوٽ\n" +
                        "رقبو 2 = ${formatNumberAccurate(area2)} چورس فوٽ"
            ),
            CalculationStep(
                titleEn = "Sum of Both Triangles",
                titleUr = "دونوں تکونوں کا مجموعہ",
                titleSd = "ٻنهي ٽڪنڊن جو جوڙ",
                detailEn = "Total Area = Area 1 + Area 2 = ${formatNumberAccurate(area1)} + ${formatNumberAccurate(area2)} = ${formatNumberAccurate(sqFeet)} sq ft",
                detailUr = "کل رقبہ = رقبہ 1 + رقبہ 2 = ${formatNumberAccurate(area1)} + ${formatNumberAccurate(area2)} = ${formatNumberAccurate(sqFeet)} مربع فٹ",
                detailSd = "ڪل رقبو = رقبو 1 + رقبو 2 = ${formatNumberAccurate(area1)} + ${formatNumberAccurate(area2)} = ${formatNumberAccurate(sqFeet)} چورس فوٽ"
            )
        )

        return CalcOutcome.Success(
            createResult(
                shape = PlotShape.FOUR_SIDE_DIAGONAL,
                sqFeet = sqFeet,
                isEstimate = false,
                methodNameEn = "Triangulation with Diagonal (Exact Legal)",
                methodNameUr = "وتر کے ذریعے دو تکونوں میں تقسیم (درست اور پکی پیمائش)",
                methodNameSd = "وتر جي ذريعي ٻن ٽڪنڊن ۾ ورهائي (پڪي ۽ صحيح ماپ)",
                steps = steps
            )
        )
    }

    // 5. 4 SIDES WITHOUT DIAGONAL (ESTIMATE ONLY)
    // Formula: (Top + Bottom) / 2 * (Left + Right) / 2
    fun calculateFourSideEstimate(
        top: Double?,
        bottom: Double?,
        left: Double?,
        right: Double?
    ): CalcOutcome {
        validatePositive("Top Side", top)?.let { return it }
        validatePositive("Bottom Side", bottom)?.let { return it }
        validatePositive("Left Side", left)?.let { return it }
        validatePositive("Right Side", right)?.let { return it }
        val t = top!!
        val b = bottom!!
        val l = left!!
        val r = right!!

        val avgTopBottom = (t + b) / 2.0
        val avgLeftRight = (l + r) / 2.0
        val sqFeet = avgTopBottom * avgLeftRight

        val steps = listOf(
            CalculationStep(
                titleEn = "Average Method Formula (ESTIMATE)",
                titleUr = "چار اضلاع کا اوسط فارمولا (صرف تخمینہ)",
                titleSd = "چئن پاسن جو سراسري فارمولو (رڳو اندازو)",
                detailEn = "1. Average Top & Bottom = (${formatNumber(t)} + ${formatNumber(b)}) ÷ 2 = ${formatNumber(t + b)} ÷ 2 = ${formatNumberAccurate(avgTopBottom)} ft\n" +
                        "2. Average Left & Right = (${formatNumber(l)} + ${formatNumber(r)}) ÷ 2 = ${formatNumber(l + r)} ÷ 2 = ${formatNumberAccurate(avgLeftRight)} ft\n" +
                        "3. Estimated Area = ${formatNumberAccurate(avgTopBottom)} × ${formatNumberAccurate(avgLeftRight)} = ${formatNumberAccurate(sqFeet)} sq ft",
                detailUr = "1. اوپر اور نیچے کے اضلاع کی اوسط = (${formatNumber(t)} + ${formatNumber(b)}) ÷ 2 = ${formatNumber(t + b)} ÷ 2 = ${formatNumberAccurate(avgTopBottom)} فٹ\n" +
                        "2. بائیں اور دائیں اضلاع کی اوسط = (${formatNumber(l)} + ${formatNumber(r)}) ÷ 2 = ${formatNumber(l + r)} ÷ 2 = ${formatNumberAccurate(avgLeftRight)} فٹ\n" +
                        "3. تخمینہ رقبہ = ${formatNumberAccurate(avgTopBottom)} × ${formatNumberAccurate(avgLeftRight)} = ${formatNumberAccurate(sqFeet)} مربع فٹ",
                detailSd = "1. مٿئين ۽ هيٺئين پاسن جي سراسري = (${formatNumber(t)} + ${formatNumber(b)}) ÷ 2 = ${formatNumber(t + b)} ÷ 2 = ${formatNumberAccurate(avgTopBottom)} فوٽ\n" +
                        "2. کاٻي ۽ ساڄي پاسن جي سراسري = (${formatNumber(l)} + ${formatNumber(r)}) ÷ 2 = ${formatNumber(l + r)} ÷ 2 = ${formatNumberAccurate(avgLeftRight)} فوٽ\n" +
                        "3. اندازي رقبو = ${formatNumberAccurate(avgTopBottom)} × ${formatNumberAccurate(avgLeftRight)} = ${formatNumberAccurate(sqFeet)} چورس فوٽ"
            )
        )

        return CalcOutcome.Success(
            createResult(
                shape = PlotShape.FOUR_SIDE_ESTIMATE,
                sqFeet = sqFeet,
                isEstimate = true,
                methodNameEn = "Average of Opposite Sides (ESTIMATE ONLY)",
                methodNameUr = "مخالف اضلاع کی اوسط (صرف تخمینہ / اندازہ)",
                methodNameSd = "آمهون سامهون پاسن جي سراسري (رڳو اندازو)",
                steps = steps,
                warningEn = "ESTIMATE ONLY: This 4-side average is an approximation. A corner-to-corner diagonal measurement is strongly recommended to avoid disputes and get an exact legal area.",
                warningUr = "صرف تخمینہ: یہ پیمائش بغیر وتر کے کی گئی ہے اور صرف اندازہ ہے۔ زمین کے کسی تنازع سے بچنے اور پکی قانونی پیمائش کے لیے ایک کونے سے دوسرے کونے تک وتر ضرور ناپیں۔",
                warningSd = "خبردار: هي رڳو اندازو آهي! وتر کانسواءِ چئن پاسن مان ڪڍيل رقبو پڪو ناهي. جهيڙي يا نقصان کان بچڻ لاءِ هڪ ڪنڊ کان سامهون ڪنڊ تائين وتر ماپڻ ضروري آهي."
            )
        )
    }

    // 6. MULTI-SIDED (5 SIDES with 2 diagonals -> 3 Triangles)
    fun calculatePentagon(
        side1: Double?,
        side2: Double?,
        side3: Double?,
        side4: Double?,
        side5: Double?,
        diag1: Double?,
        diag2: Double?
    ): CalcOutcome {
        validatePositive("Side 1", side1)?.let { return it }
        validatePositive("Side 2", side2)?.let { return it }
        validatePositive("Side 3", side3)?.let { return it }
        validatePositive("Side 4", side4)?.let { return it }
        validatePositive("Side 5", side5)?.let { return it }
        validatePositive("Diagonal 1 (Corner 1 to 3)", diag1)?.let { return it }
        validatePositive("Diagonal 2 (Corner 1 to 4)", diag2)?.let { return it }
        val s1 = side1!!
        val s2 = side2!!
        val s3 = side3!!
        val s4 = side4!!
        val s5 = side5!!
        val d1 = diag1!!
        val d2 = diag2!!

        // Triangle A: s1, s2, d1
        checkTriangleInequality(s1, s2, d1, "Side 1", "Side 2", "Diagonal 1")?.let {
            return CalcOutcome.Error(
                messageEn = "Triangle 1 (Side 1, Side 2, Diag 1) is invalid: ${it.messageEn}",
                messageUr = "پہلی تکون (ضلع 1، ضلع 2، وتر 1) ناممکن ہے: ${it.messageUr}",
                messageSd = "پھريون ٽڪنڊو (پاسو 1، پاسو 2، وتر 1) ممڪن ناهي: ${it.messageSd}"
            )
        }

        // Triangle B: d1, s3, d2
        checkTriangleInequality(d1, s3, d2, "Diagonal 1", "Side 3", "Diagonal 2")?.let {
            return CalcOutcome.Error(
                messageEn = "Triangle 2 (Diag 1, Side 3, Diag 2) is invalid: ${it.messageEn}",
                messageUr = "دوسری تکون (وتر 1، ضلع 3، وتر 2) ناممکن ہے: ${it.messageUr}",
                messageSd = "ٻيون ٽڪنڊو (وتر 1، پاسو 3، وتر 2) ممڪن ناهي: ${it.messageSd}"
            )
        }

        // Triangle C: d2, s4, s5
        checkTriangleInequality(d2, s4, s5, "Diagonal 2", "Side 4", "Side 5")?.let {
            return CalcOutcome.Error(
                messageEn = "Triangle 3 (Diag 2, Side 4, Side 5) is invalid: ${it.messageEn}",
                messageUr = "تیسری تکون (وتر 2، ضلع 4، ضلع 5) ناممکن ہے: ${it.messageUr}",
                messageSd = "ٽيون ٽڪنڊو (وتر 2، پاسو 4، پاسو 5) ممڪن ناهي: ${it.messageSd}"
            )
        }

        val semiA = (s1 + s2 + d1) / 2.0
        val areaA = sqrt(semiA * (semiA - s1) * (semiA - s2) * (semiA - d1))

        val semiB = (d1 + s3 + d2) / 2.0
        val areaB = sqrt(semiB * (semiB - d1) * (semiB - s3) * (semiB - d2))

        val semiC = (d2 + s4 + s5) / 2.0
        val areaC = sqrt(semiC * (semiC - d2) * (semiC - s4) * (semiC - s5))

        val sqFeet = areaA + areaB + areaC

        val steps = listOf(
            CalculationStep(
                titleEn = "Triangle 1 (Side 1: ${formatNumber(s1)}, Side 2: ${formatNumber(s2)}, Diag 1: ${formatNumber(d1)})",
                titleUr = "پہلی تکون (ضلع 1: ${formatNumber(s1)}، ضلع 2: ${formatNumber(s2)}، وتر 1: ${formatNumber(d1)})",
                titleSd = "پهريون ٽڪنڊو (پاسو 1: ${formatNumber(s1)}، پاسو 2: ${formatNumber(s2)}، وتر 1: ${formatNumber(d1)})",
                detailEn = "Area 1 = ${formatNumberAccurate(areaA)} sq ft",
                detailUr = "رقبہ 1 = ${formatNumberAccurate(areaA)} مربع فٹ",
                detailSd = "رقبو 1 = ${formatNumberAccurate(areaA)} چورس فوٽ"
            ),
            CalculationStep(
                titleEn = "Triangle 2 (Diag 1: ${formatNumber(d1)}, Side 3: ${formatNumber(s3)}, Diag 2: ${formatNumber(d2)})",
                titleUr = "دوسری تکون (وتر 1: ${formatNumber(d1)}، ضلع 3: ${formatNumber(s3)}، وتر 2: ${formatNumber(d2)})",
                titleSd = "ٻيون ٽڪنڊو (وتر 1: ${formatNumber(d1)}، پاسو 3: ${formatNumber(s3)}، وتر 2: ${formatNumber(d2)})",
                detailEn = "Area 2 = ${formatNumberAccurate(areaB)} sq ft",
                detailUr = "رقبہ 2 = ${formatNumberAccurate(areaB)} مربع فٹ",
                detailSd = "رقبو 2 = ${formatNumberAccurate(areaB)} چورس فوٽ"
            ),
            CalculationStep(
                titleEn = "Triangle 3 (Diag 2: ${formatNumber(d2)}, Side 4: ${formatNumber(s4)}, Side 5: ${formatNumber(s5)})",
                titleUr = "تیسری تکون (وتر 2: ${formatNumber(d2)}، ضلع 4: ${formatNumber(s4)}، ضلع 5: ${formatNumber(s5)})",
                titleSd = "ٽيون ٽڪنڊو (وتر 2: ${formatNumber(d2)}، پاسو 4: ${formatNumber(s4)}، پاسو 5: ${formatNumber(s5)})",
                detailEn = "Area 3 = ${formatNumberAccurate(areaC)} sq ft",
                detailUr = "رقبہ 3 = ${formatNumberAccurate(areaC)} مربع فٹ",
                detailSd = "رقبو 3 = ${formatNumberAccurate(areaC)} چورس فوٽ"
            ),
            CalculationStep(
                titleEn = "Total Combined Area",
                titleUr = "تینوں تکونوں کا مجموعہ",
                titleSd = "ٽنهي ٽڪنڊن جو ڪل رقبو",
                detailEn = "Total Area = ${formatNumberAccurate(areaA)} + ${formatNumberAccurate(areaB)} + ${formatNumberAccurate(areaC)} = ${formatNumberAccurate(sqFeet)} sq ft",
                detailUr = "کل رقبہ = ${formatNumberAccurate(areaA)} + ${formatNumberAccurate(areaB)} + ${formatNumberAccurate(areaC)} = ${formatNumberAccurate(sqFeet)} مربع فٹ",
                detailSd = "ڪل رقبو = ${formatNumberAccurate(areaA)} + ${formatNumberAccurate(areaB)} + ${formatNumberAccurate(areaC)} = ${formatNumberAccurate(sqFeet)} چورس فوٽ"
            )
        )

        return CalcOutcome.Success(
            createResult(
                shape = PlotShape.MULTI_SIDE_POLYGON,
                sqFeet = sqFeet,
                isEstimate = false,
                methodNameEn = "Triangulation into 3 Triangles (Exact)",
                methodNameUr = "دو وتروں سے تین تکونوں میں تقسیم (درست)",
                methodNameSd = "ٻن وترن سان ٽن ٽڪنڊن ۾ ورهايل (پڪي ماپ)",
                steps = steps
            )
        )
    }

    // 7. CURVED SIDE (Trapezoidal rule)
    fun calculateCurvedSide(
        baselineLength: Double?,
        offsets: List<Double?>
    ): CalcOutcome {
        validatePositive("Baseline Length", baselineLength)?.let { return it }
        val b = baselineLength!!

        if (offsets.size < 2) {
            return CalcOutcome.Error(
                messageEn = "Please provide at least 2 offset heights (start and end).",
                messageUr = "براہ کرم کم از کم 2 آف سیٹ پیمائشیں درج کریں۔",
                messageSd = "مهرباني ڪري گهٽ ۾ گهٽ 2 آف سيٽ ماپون لکو."
            )
        }

        val validOffsets = mutableListOf<Double>()
        for (i in offsets.indices) {
            val v = offsets[i]
            if (v == null || v < 0.0) {
                return CalcOutcome.Error(
                    messageEn = "Offset #${i + 1} cannot be empty or negative (enter 0 if curve touches baseline).",
                    messageUr = "آف سیٹ #${i + 1} خالی یا منفی نہیں ہو سکتا (اگر موڑ لائن کو چھو رہا ہو تو 0 لکھیں)۔",
                    messageSd = "آف سيٽ #${i + 1} خالي يا ڪاٽو نٿو ٿي سگهي (جيڪڏهن لڪير تي آهي ته 0 لکو)."
                )
            }
            validOffsets.add(v)
        }

        val nIntervals = validOffsets.size - 1
        val intervalDistance = b / nIntervals.toDouble()

        val first = validOffsets.first()
        val last = validOffsets.last()
        val innerSum = validOffsets.subList(1, validOffsets.size - 1).sum()

        val sqFeet = intervalDistance * (((first + last) / 2.0) + innerSum)

        val steps = listOf(
            CalculationStep(
                titleEn = "Step 1: Interval Spacing (d)",
                titleUr = "مرحلہ 1: ہر پوائنٹ کے درمیان فاصلہ (d)",
                titleSd = "قدم 1: هر پوائنٽ جي وچ ۾ وڇوٽي (d)",
                detailEn = "Baseline = ${formatNumber(b)} ft, Intervals = $nIntervals\nSpacing d = ${formatNumber(b)} ÷ $nIntervals = ${formatNumberAccurate(intervalDistance)} ft",
                detailUr = "بیس لائن = ${formatNumber(b)} فٹ، وقفے = $nIntervals\nفاصلہ d = ${formatNumber(b)} ÷ $nIntervals = ${formatNumberAccurate(intervalDistance)} فٹ",
                detailSd = "بيس لائين = ${formatNumber(b)} فوٽ، وڇوٽيون = $nIntervals\nفاصلو d = ${formatNumber(b)} ÷ $nIntervals = ${formatNumberAccurate(intervalDistance)} فوٽ"
            ),
            CalculationStep(
                titleEn = "Step 2: Trapezoidal Rule Formula",
                titleUr = "مرحلہ 2: ٹریپیزائیڈل رول فارمولا",
                titleSd = "قدم 2: ٽريپيزائيڊل اصول",
                detailEn = "Area = d × [(h_first + h_last) ÷ 2 + sum of middle heights]\n" +
                        "Ends average = (${formatNumber(first)} + ${formatNumber(last)}) ÷ 2 = ${formatNumberAccurate((first + last) / 2.0)}\n" +
                        "Middle heights sum = ${formatNumberAccurate(innerSum)}\n" +
                        "Total Area = ${formatNumberAccurate(intervalDistance)} × [${formatNumberAccurate((first + last) / 2.0)} + ${formatNumberAccurate(innerSum)}] = ${formatNumberAccurate(sqFeet)} sq ft",
                detailUr = "رقبہ = d × [(پہلا + آخری) ÷ 2 + درمیانی بلندیوں کا مجموعہ]\n" +
                        "کناروں کی اوسط = (${formatNumber(first)} + ${formatNumber(last)}) ÷ 2 = ${formatNumberAccurate((first + last) / 2.0)}\n" +
                        "درمیانی بلندیوں کا جوڑ = ${formatNumberAccurate(innerSum)}\n" +
                        "کل رقبہ = ${formatNumberAccurate(sqFeet)} مربع فٹ",
                detailSd = "رقبو = d × [(پهريون + آخري) ÷ 2 + وچين اڀارن جو جوڙ]\n" +
                        "ڪنارن جي سراسري = (${formatNumber(first)} + ${formatNumber(last)}) ÷ 2 = ${formatNumberAccurate((first + last) / 2.0)}\n" +
                        "وچين اوچاين جو جوڙ = ${formatNumberAccurate(innerSum)}\n" +
                        "ڪل رقبو = ${formatNumberAccurate(sqFeet)} چورس فوٽ"
            )
        )

        return CalcOutcome.Success(
            createResult(
                shape = PlotShape.CURVED_SIDE,
                sqFeet = sqFeet,
                isEstimate = false,
                methodNameEn = "Trapezoidal Rule from Baseline",
                methodNameUr = "ٹریپیزائیڈل رول (برابر فاصلوں پر بلندی)",
                methodNameSd = "ٽريپيزائيڊل طريقو (برابر وڇوٽين تي ماپ)",
                steps = steps
            )
        )
    }

    fun calculateGpsShoelace(
        points: List<com.example.gps.GpsPoint>
    ): CalcOutcome {
        if (points.size < 3) {
            return CalcOutcome.Error(
                messageEn = "GPS walking requires at least 3 corners.",
                messageUr = "جی پی ایس پیمائش کے لیے کم از کم 3 کونوں کی ضرورت ہے۔",
                messageSd = "GPS ماپ لاءِ گهٽ ۾ گهٽ 3 ڪنڊن جي ضرورت آهي."
            )
        }
        val sqFeet = com.example.gps.GpsCalculator.shoelaceAreaSqFeet(points)
        val avgAcc = if (points.isNotEmpty()) points.map { it.accuracyMeters.toDouble() }.average() else 0.0
        val steps = listOf(
            CalculationStep(
                titleEn = "GPS Corners Coordinates",
                titleUr = "جی پی ایس کونوں کے کوآرڈینیٹس",
                titleSd = "GPS ڪنڊن جا ڪوآرڊينيٽس",
                detailEn = "${points.size} corners marked using device GPS.\nAverage accuracy: ${formatNumber(avgAcc)} meters",
                detailUr = "ڈیوائس GPS سے ${points.size} کونے لگائے گئے۔\nاوسط درستی: ${formatNumber(avgAcc)} میٹر",
                detailSd = "ڊوائيس GPS سان ${points.size} ڪنڊون رڪارڊ ڪيون ويون.\nسراسري سچائي: ${formatNumber(avgAcc)} ميٽر"
            ),
            CalculationStep(
                titleEn = "Shoelace Formula Calculation",
                titleUr = "شولیس (Shoelace) فارمولا",
                titleSd = "شوليس فارمولا حساب",
                detailEn = "Area calculated from GPS polygon coordinates = ${formatNumberAccurate(sqFeet)} sq ft",
                detailUr = "GPS پولیگون سے حاصل کردہ رقبہ = ${formatNumberAccurate(sqFeet)} مربع فٹ",
                detailSd = "GPS پولينگون ڪوآرڊينيٽس مان نڪتل رقبو = ${formatNumberAccurate(sqFeet)} چورس فوٽ"
            )
        )
        return CalcOutcome.Success(
            createResult(
                shape = PlotShape.MULTI_SIDE_POLYGON,
                sqFeet = sqFeet,
                isEstimate = true,
                methodNameEn = "GPS Walking Measurement (Shoelace Formula)",
                methodNameUr = "جی پی ایس واکنگ پیمائش (شولیس پولیگون فارمولا)",
                methodNameSd = "GPS هلڻ ذريعي ماپ (شوليس فارمولا)",
                steps = steps
            )
        )
    }

    // Parses a comma or space separated string of numbers into a list of doubles
    fun parseSidesList(text: String): List<Double> {
        if (text.isBlank()) return emptyList()
        return text.split(Regex("[,\\s;]+"))
            .mapNotNull { it.trim().toDoubleOrNull() }
    }

    // QUICK MEASURE CALCULATION (no shape needed)
    fun calculateQuickMeasure(
        sides: List<Double>,
        diagonals: List<Double> = emptyList(),
        sideDirections: Map<Int, com.example.model.CompassDirection> = emptyMap()
    ): CalcOutcome {
        if (sides.isEmpty()) {
            return CalcOutcome.Error(
                messageEn = "Please enter side measurements.",
                messageUr = "براہ کرم اضلاع کی پیمائش درج کریں۔",
                messageSd = "مهرباني ڪري پاسن جي ماپ لکو."
            )
        }

        if (sides.size < 3) {
            return CalcOutcome.Error(
                messageEn = "A closed land plot requires at least 3 sides.",
                messageUr = "ایک بند قطعہ زمین کے لیے کم از کم 3 اضلاع درکار ہیں۔",
                messageSd = "بند زمين يا پلاٽ لاءِ گھٽ ۾ گھٽ 3 پاسا هجڻ ضروري آهن."
            )
        }

        for (i in sides.indices) {
            val s = sides[i]
            if (s <= 0.0 || s.isNaN()) {
                val sideNum = i + 1
                return CalcOutcome.Error(
                    messageEn = "Side $sideNum must be greater than 0 feet (cannot be 0 or negative).",
                    messageUr = "ضلع $sideNum صفر یا منفی نہیں ہو سکتا، 0 سے بڑی پیمائش درج کریں۔",
                    messageSd = "پاسو $sideNum ٻُڙي (0) يا ڪاٽو نٿو ٿي سگهي، 0 کان وڏي ماپ لکو."
                )
            }
        }

        // Polygon inequality rule: the longest side must be strictly less than the sum of all other sides
        val maxSide = sides.maxOrNull() ?: 0.0
        val sumOthers = sides.sum() - maxSide
        if (maxSide >= sumOthers) {
            return CalcOutcome.Error(
                messageEn = "Impossible shape! Side ${formatNumber(maxSide)} ft is longer than or equal to all other sides combined (${formatNumber(sumOthers)} ft).",
                messageUr = "یہ شکل بننا ناممکن ہے! ${formatNumber(maxSide)} فٹ کا ضلع باقی تمام اضلاع کے مجموعے (${formatNumber(sumOthers)} فٹ) کے برابر یا بڑا ہے۔",
                messageSd = "هي نقشو ٺهڻ ناممڪن آهي! ${formatNumber(maxSide)} فوٽ وارو پاسو باقي سڀني پاسن جي ڪل جوڙ (${formatNumber(sumOthers)} فوٽ) جي برابر يا وڏو آهي."
            )
        }

        val count = sides.size

        // 1. THREE SIDES (Heron's Formula - Exact)
        if (count == 3) {
            val s1 = sides[0]
            val s2 = sides[1]
            val s3 = sides[2]
            val ineqError = checkTriangleInequality(s1, s2, s3)
            if (ineqError != null) return ineqError

            val s = (s1 + s2 + s3) / 2.0
            val radicand = s * (s - s1) * (s - s2) * (s - s3)
            val sqFeet = sqrt(radicand)

            val steps = listOf(
                CalculationStep(
                    titleEn = "Semi-Perimeter (s)",
                    titleUr = "نصف احاطہ (s)",
                    titleSd = "اڌ احاطو (s)",
                    detailEn = "s = (${formatNumber(s1)} + ${formatNumber(s2)} + ${formatNumber(s3)}) ÷ 2 = ${formatNumberAccurate(s)} ft",
                    detailUr = "s = (${formatNumber(s1)} + ${formatNumber(s2)} + ${formatNumber(s3)}) ÷ 2 = ${formatNumberAccurate(s)} فٹ",
                    detailSd = "s = (${formatNumber(s1)} + ${formatNumber(s2)} + ${formatNumber(s3)}) ÷ 2 = ${formatNumberAccurate(s)} فوٽ"
                ),
                CalculationStep(
                    titleEn = "Heron's Formula (EXACT)",
                    titleUr = "ہیروئن فارمولا (درست اور پکا)",
                    titleSd = "هيرون فارمولو (پڪو ۽ صحيح)",
                    detailEn = "Area = √[s × (s - a) × (s - b) × (s - c)] = ${formatNumberAccurate(sqFeet)} sq ft",
                    detailUr = "رقبہ = √[s × (s - a) × (s - b) × (s - c)] = ${formatNumberAccurate(sqFeet)} مربع فٹ",
                    detailSd = "رقبو = √[s × (s - a) × (s - b) × (s - c)] = ${formatNumberAccurate(sqFeet)} چورس فوٽ"
                )
            )

            return CalcOutcome.Success(
                createResult(
                    shape = PlotShape.TRIANGLE,
                    sqFeet = sqFeet,
                    isEstimate = false,
                    methodNameEn = "Heron's Formula (3 Sides - EXACT)",
                    methodNameUr = "ہیروئن فارمولا (3 اضلاع - درست اور پکا)",
                    methodNameSd = "هيرون فارمولو (3 پاسا - پڪو ۽ صحيح)",
                    steps = steps
                )
            )
        }

        // 2. FOUR SIDES
        if (count == 4) {
            val s1 = sides[0] // Side A
            val s2 = sides[1] // Side B
            val s3 = sides[2] // Side C
            val s4 = sides[3] // Side D

            val hasDiagonal = diagonals.isNotEmpty() && diagonals[0] > 0.0

            // Case A: Opposite sides are equal (s1 == s3 and s2 == s4) -> Rectangle / Square (EXACT)
            if (!hasDiagonal && s1 == s3 && s2 == s4) {
                val sqFeet = s1 * s2
                val steps = listOf(
                    CalculationStep(
                        titleEn = "Equal Opposite Sides (Rectangle / Square - EXACT)",
                        titleUr = "مخالف اضلاع برابر (مستطیل یا چورس - پکا)",
                        titleSd = "آمهون سامهون برابر پاسا (مستطيل يا چورس - پڪو)",
                        detailEn = "Side A (${formatNumber(s1)} ft) = Side C (${formatNumber(s3)} ft) and Side B (${formatNumber(s2)} ft) = Side D (${formatNumber(s4)} ft).\n" +
                                "Area = Length × Width = ${formatNumber(s1)} × ${formatNumber(s2)} = ${formatNumberAccurate(sqFeet)} sq ft",
                        detailUr = "ضلع A (${formatNumber(s1)} فٹ) = ضلع C (${formatNumber(s3)} فٹ) اور ضلع B (${formatNumber(s2)} فٹ) = ضلع D (${formatNumber(s4)} فٹ)۔\n" +
                                "رقبہ = لمبائی × چوڑائی = ${formatNumber(s1)} × ${formatNumber(s2)} = ${formatNumberAccurate(sqFeet)} مربع فٹ",
                        detailSd = "پاسو A (${formatNumber(s1)} فوٽ) = پاسو C (${formatNumber(s3)} فوٽ) ۽ پاسو B (${formatNumber(s2)} فوٽ) = پاسو D (${formatNumber(s4)} فوٽ).\n" +
                                "رقبو = ڊيگهه × ويڪر = ${formatNumber(s1)} × ${formatNumber(s2)} = ${formatNumberAccurate(sqFeet)} چورس فوٽ"
                    )
                )

                return CalcOutcome.Success(
                    createResult(
                        shape = PlotShape.RECTANGLE,
                        sqFeet = sqFeet,
                        isEstimate = false,
                        methodNameEn = "Opposite Equal Sides (Rectangle - EXACT)",
                        methodNameUr = "مخالف اضلاع برابر (مستطیل یا چورس - درست اور پکا)",
                        methodNameSd = "آمهون سامهون برابر پاسا (مستطيل يا چورس - پڪو ۽ صحيح)",
                        steps = steps
                    )
                )
            }

            // Case B: 4 sides with a diagonal -> Triangulation into 2 triangles (EXACT)
            if (hasDiagonal) {
                val diag = diagonals[0]
                val err1 = checkTriangleInequality(s1, s2, diag, "Side A", "Side B", "Diagonal")
                if (err1 != null) return err1
                val err2 = checkTriangleInequality(s3, s4, diag, "Side C", "Side D", "Diagonal")
                if (err2 != null) return err2

                val semi1 = (s1 + s2 + diag) / 2.0
                val area1 = sqrt(semi1 * (semi1 - s1) * (semi1 - s2) * (semi1 - diag))

                val semi2 = (s3 + s4 + diag) / 2.0
                val area2 = sqrt(semi2 * (semi2 - s3) * (semi2 - s4) * (semi2 - diag))

                val sqFeet = area1 + area2

                val steps = listOf(
                    CalculationStep(
                        titleEn = "Triangle 1 (Sides: ${formatNumber(s1)}, ${formatNumber(s2)}, Diagonal: ${formatNumber(diag)} ft)",
                        titleUr = "پہلی تکون (اضلاع: ${formatNumber(s1)}، ${formatNumber(s2)}، وتر: ${formatNumber(diag)} فٹ)",
                        titleSd = "پهريون ٽڪنڊو (پاسا: ${formatNumber(s1)}، ${formatNumber(s2)}، وتر: ${formatNumber(diag)} فوٽ)",
                        detailEn = "Area 1 = ${formatNumberAccurate(area1)} sq ft",
                        detailUr = "رقبہ 1 = ${formatNumberAccurate(area1)} مربع فٹ",
                        detailSd = "رقبو 1 = ${formatNumberAccurate(area1)} چورس فوٽ"
                    ),
                    CalculationStep(
                        titleEn = "Triangle 2 (Sides: ${formatNumber(s3)}, ${formatNumber(s4)}, Diagonal: ${formatNumber(diag)} ft)",
                        titleUr = "دوسری تکون (اضلاع: ${formatNumber(s3)}، ${formatNumber(s4)}، وتر: ${formatNumber(diag)} فٹ)",
                        titleSd = "ٻيون ٽڪنڊو (پاسا: ${formatNumber(s3)}، ${formatNumber(s4)}، وتر: ${formatNumber(diag)} فوٽ)",
                        detailEn = "Area 2 = ${formatNumberAccurate(area2)} sq ft",
                        detailUr = "رقبہ 2 = ${formatNumberAccurate(area2)} مربع فٹ",
                        detailSd = "رقبو 2 = ${formatNumberAccurate(area2)} چورس فوٽ"
                    ),
                    CalculationStep(
                        titleEn = "Total Area (Sum of Both Triangles - EXACT)",
                        titleUr = "کل رقبہ (دونوں تکونوں کا مجموعہ - پکا)",
                        titleSd = "ڪل رقبو (ٻنهي ٽڪنڊن جو جوڙ - پڪو)",
                        detailEn = "Total Area = ${formatNumberAccurate(area1)} + ${formatNumberAccurate(area2)} = ${formatNumberAccurate(sqFeet)} sq ft",
                        detailUr = "کل رقبہ = ${formatNumberAccurate(area1)} + ${formatNumberAccurate(area2)} = ${formatNumberAccurate(sqFeet)} مربع فٹ",
                        detailSd = "ڪل رقبو = ${formatNumberAccurate(area1)} + ${formatNumberAccurate(area2)} = ${formatNumberAccurate(sqFeet)} چورس فوٽ"
                    )
                )

                return CalcOutcome.Success(
                    createResult(
                        shape = PlotShape.FOUR_SIDE_DIAGONAL,
                        sqFeet = sqFeet,
                        isEstimate = false,
                        methodNameEn = "Triangulation with Diagonal (EXACT LEGAL)",
                        methodNameUr = "وتر کے ذریعے دو تکونوں میں تقسیم (درست اور پکی پیمائش)",
                        methodNameSd = "وتر جي ذريعي ٻن ٽڪنڊن ۾ ورهائي (پڪي ۽ صحيح ماپ)",
                        steps = steps
                    )
                )
            }

            // Case C: 4 sides without diagonal -> ((a + c) / 2) * ((b + d) / 2) (ESTIMATE ONLY)
            val avgOpposite1 = (s1 + s3) / 2.0 // (Side A + Side C) / 2
            val avgOpposite2 = (s2 + s4) / 2.0 // (Side B + Side D) / 2
            val sqFeet = avgOpposite1 * avgOpposite2

            val steps = listOf(
                CalculationStep(
                    titleEn = "Average Method Formula (ESTIMATE ONLY)",
                    titleUr = "چار اضلاع کا اوسط فارمولا (صرف تخمینہ)",
                    titleSd = "چئن پاسن جو سراسري فارمولو (رڳو اندازو)",
                    detailEn = "1. Average Opposite Sides A & C = (${formatNumber(s1)} + ${formatNumber(s3)}) ÷ 2 = ${formatNumberAccurate(avgOpposite1)} ft\n" +
                            "2. Average Opposite Sides B & D = (${formatNumber(s2)} + ${formatNumber(s4)}) ÷ 2 = ${formatNumberAccurate(avgOpposite2)} ft\n" +
                            "3. Estimated Area = ${formatNumberAccurate(avgOpposite1)} × ${formatNumberAccurate(avgOpposite2)} = ${formatNumberAccurate(sqFeet)} sq ft",
                    detailUr = "1. آمنے سامنے کے اضلاع A اور C کی اوسط = (${formatNumber(s1)} + ${formatNumber(s3)}) ÷ 2 = ${formatNumberAccurate(avgOpposite1)} فٹ\n" +
                            "2. آمنے سامنے کے اضلاع B اور D کی اوسط = (${formatNumber(s2)} + ${formatNumber(s4)}) ÷ 2 = ${formatNumberAccurate(avgOpposite2)} فٹ\n" +
                            "3. تخمینہ رقبہ = ${formatNumberAccurate(avgOpposite1)} × ${formatNumberAccurate(avgOpposite2)} = ${formatNumberAccurate(sqFeet)} مربع فٹ",
                    detailSd = "1. آمهون سامهون پاسن A ۽ C جي سراسري = (${formatNumber(s1)} + ${formatNumber(s3)}) ÷ 2 = ${formatNumberAccurate(avgOpposite1)} فوٽ\n" +
                            "2. آمهون سامهون پاسن B ۽ D جي سراسري = (${formatNumber(s2)} + ${formatNumber(s4)}) ÷ 2 = ${formatNumberAccurate(avgOpposite2)} فوٽ\n" +
                            "3. اندازي رقبو = ${formatNumberAccurate(avgOpposite1)} × ${formatNumberAccurate(avgOpposite2)} = ${formatNumberAccurate(sqFeet)} چورس فوٽ"
                )
            )

            return CalcOutcome.Success(
                createResult(
                    shape = PlotShape.FOUR_SIDE_ESTIMATE,
                    sqFeet = sqFeet,
                    isEstimate = true,
                    methodNameEn = "Opposite Sides Average Method (ESTIMATE ONLY)",
                    methodNameUr = "مخالف اضلاع کا اوسط فارمولا (صرف تخمینہ)",
                    methodNameSd = "آمهون سامهون پاسن جو سراسري طريقو (رڳو اندازو)",
                    steps = steps,
                    warningEn = "ESTIMATE ONLY: This 4-side average is an approximation. What would make it exact: Measure a diagonal from corner 1 to corner 3 (or corner 2 to corner 4).",
                    warningUr = "صرف تخمینہ: یہ پیمائش بغیر وتر کے اوسط فارمولے سے کی گئی ہے۔ پیمائش کو پکا بنانے کے لیے کونے 1 سے کونے 3 تک وتر (ترچھی لائن) ناپیں۔",
                    warningSd = "رڳو اندازو: هيءَ ماپ بغير وتر جي سراسري طريقو آهي. ماپ پڪي ڪرڻ لاءِ ڪنڊ 1 کان ڪنڊ 3 تائين وتر (ترڇي لڪير) ماپيو."
                )
            )
        }

        // 3. FIVE OR MORE SIDES
        // If diagonals provided to triangulate
        if (diagonals.size >= count - 3 && diagonals.all { it > 0.0 }) {
            // Triangulate sequential triangles
            var totalArea = 0.0
            val steps = mutableListOf<CalculationStep>()
            var prevDiag = sides[0]
            for (i in 0 until count - 2) {
                val sideA = if (i == 0) sides[1] else diagonals[i - 1]
                val sideB = sides[i + 2]
                val diag = if (i == count - 3) sides[count - 1] else diagonals[i]
                val ineq = checkTriangleInequality(sideA, sideB, diag)
                if (ineq != null) return ineq
                val s = (sideA + sideB + diag) / 2.0
                val triArea = sqrt(s * (s - sideA) * (s - sideB) * (s - diag))
                totalArea += triArea
                steps.add(
                    CalculationStep(
                        titleEn = "Triangle ${i + 1}",
                        titleUr = "تکون ${i + 1}",
                        titleSd = "ٽڪنڊو ${i + 1}",
                        detailEn = "Area ${i + 1} = ${formatNumberAccurate(triArea)} sq ft",
                        detailUr = "رقبہ ${i + 1} = ${formatNumberAccurate(triArea)} مربع فٹ",
                        detailSd = "رقبو ${i + 1} = ${formatNumberAccurate(triArea)} چورس فوٽ"
                    )
                )
            }
            return CalcOutcome.Success(
                createResult(
                    shape = PlotShape.MULTI_SIDE_POLYGON,
                    sqFeet = totalArea,
                    isEstimate = false,
                    methodNameEn = "Multi-Sided Triangulation with Diagonals (EXACT)",
                    methodNameUr = "کثیر الاضلاع وتروں کے ذریعے تکونوں میں تقسیم (پکا اور درست)",
                    methodNameSd = "گهڻ پاسن واري زمين وترن ذريعي ٽڪنڊن ۾ (پڪو ۽ صحيح)",
                    steps = steps
                )
            )
        }

        // No diagonals -> Show Rough Estimate by grouping opposing sides
        val group1 = mutableListOf<Double>()
        val group2 = mutableListOf<Double>()
        for (i in sides.indices) {
            if (i % 2 == 0) group1.add(sides[i]) else group2.add(sides[i])
        }
        val avg1 = group1.average()
        val avg2 = group2.average()
        val sqFeet = avg1 * avg2

        val steps = listOf(
            CalculationStep(
                titleEn = "Opposite Groups Average ($count Sides - ROUGH ESTIMATE)",
                titleUr = "مخالف اضلاع کے گروپس کی اوسط ($count اضلاع - کچا اندازہ)",
                titleSd = "آمهون سامهون پاسن جي گروپن جي سراسري ($count پاسا - ڪچو اندازو)",
                detailEn = "1. Group 1 Average (${group1.size} sides) = ${formatNumberAccurate(avg1)} ft\n" +
                        "2. Group 2 Average (${group2.size} sides) = ${formatNumberAccurate(avg2)} ft\n" +
                        "3. Rough Estimated Area = ${formatNumberAccurate(avg1)} × ${formatNumberAccurate(avg2)} = ${formatNumberAccurate(sqFeet)} sq ft",
                detailUr = "1. گروپ 1 کی اوسط (${group1.size} اضلاع) = ${formatNumberAccurate(avg1)} فٹ\n" +
                        "2. گروپ 2 کی اوسط (${group2.size} اضلاع) = ${formatNumberAccurate(avg2)} فٹ\n" +
                        "3. کچا تخمینہ رقبہ = ${formatNumberAccurate(avg1)} × ${formatNumberAccurate(avg2)} = ${formatNumberAccurate(sqFeet)} مربع فٹ",
                detailSd = "1. گروپ 1 جي سراسري (${group1.size} پاسا) = ${formatNumberAccurate(avg1)} فوٽ\n" +
                        "2. گروپ 2 جي سراسري (${group2.size} پاسا) = ${formatNumberAccurate(avg2)} فوٽ\n" +
                        "3. ڪچو اندازي رقبو = ${formatNumberAccurate(avg1)} × ${formatNumberAccurate(avg2)} = ${formatNumberAccurate(sqFeet)} چورس فوٽ"
            )
        )

        return CalcOutcome.Success(
            createResult(
                shape = PlotShape.MULTI_SIDE_POLYGON,
                sqFeet = sqFeet,
                isEstimate = true,
                methodNameEn = "Multi-Sided Average Approximation (ROUGH ESTIMATE)",
                methodNameUr = "کثیر الاضلاع اوسط تخمینہ (کچا اندازہ)",
                methodNameSd = "گهڻ پاسن واري زمين جو سراسري اندازو (ڪچو اندازو)",
                steps = steps,
                warningEn = "ROUGH ESTIMATE: For 5+ sided land, please measure diagonals between non-adjacent corners (e.g. corner 1 to corner 3) to get an exact legal area.",
                warningUr = "کچا اندازہ: 5 یا زائد اضلاع کی زمین کے لیے کونوں کے درمیانی وتر ناپیں تاکہ پکی قانونی پیمائش مل سکے۔",
                warningSd = "ڪچو اندازو: 5 يا وڌيڪ پاسن واري زمين لاءِ ڪنڊن جا وتر ماپيو ته جيئن پڪي ۽ قانوني ماپ ملي سگهي."
            )
        )
    }
}
