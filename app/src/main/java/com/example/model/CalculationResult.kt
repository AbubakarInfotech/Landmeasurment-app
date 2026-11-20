package com.example.model

data class CalculationStep(
    val titleEn: String,
    val titleUr: String,
    val titleSd: String,
    val detailEn: String,
    val detailUr: String,
    val detailSd: String
) {
    fun getTitle(lang: Language): String = when (lang) {
        Language.ENGLISH -> titleEn
        Language.URDU -> titleUr
        Language.SINDHI -> titleSd
    }

    fun getDetail(lang: Language): String = when (lang) {
        Language.ENGLISH -> detailEn
        Language.URDU -> detailUr
        Language.SINDHI -> detailSd
    }
}

data class CalculationResult(
    val shape: PlotShape,
    val sqFeet: Double,
    val sqMeters: Double,
    val wesa: Double,
    val gunta: Double,
    val jareeb: Double,
    val acre: Double,
    val marla: Double,
    val kanal: Double,
    val isEstimate: Boolean,
    val methodNameEn: String,
    val methodNameUr: String,
    val methodNameSd: String,
    val steps: List<CalculationStep>,
    val warningEn: String? = null,
    val warningUr: String? = null,
    val warningSd: String? = null
) {
    fun getMethodName(lang: Language): String = when (lang) {
        Language.ENGLISH -> methodNameEn
        Language.URDU -> methodNameUr
        Language.SINDHI -> methodNameSd
    }

    fun getWarning(lang: Language): String? = when (lang) {
        Language.ENGLISH -> warningEn
        Language.URDU -> warningUr
        Language.SINDHI -> warningSd
    }
}
