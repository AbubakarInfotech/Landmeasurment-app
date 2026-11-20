package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculator.LandCalculator
import com.example.localization.AppStrings
import com.example.model.CalculationResult
import com.example.model.Language
import com.example.model.Region
import com.example.ui.theme.AlertRed
import com.example.ui.theme.FertileGreenPrimary
import com.example.ui.theme.HarvestGold
import com.example.ui.theme.WarningAmber
import com.example.ui.theme.WarningAmberContainer

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ResultScreen(
    result: CalculationResult,
    region: Region,
    lang: Language,
    onNewMeasurement: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = AppStrings.resultHeader(lang),
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("result_back_btn")
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = onNewMeasurement,
                        modifier = Modifier.testTag("result_new_measure_icon_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "New")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = FertileGreenPrimary
                )
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 18.dp, vertical = 12.dp)
        ) {
            // Method Used Header Pill
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFFE8F5E9),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = FertileGreenPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = result.getMethodName(lang),
                        color = FertileGreenPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // CRITICAL ESTIMATE WARNING (If 4 sides without diagonal)
            if (result.isEstimate) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("estimate_warning_card"),
                    colors = CardDefaults.cardColors(containerColor = WarningAmberContainer),
                    border = BorderStroke(2.dp, WarningAmber),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Estimate Warning",
                                tint = WarningAmber,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = AppStrings.estimateWarningTitle(lang),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                color = Color(0xFF78350F)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = result.getWarning(lang) ?: AppStrings.estimateWarningText(lang),
                            fontSize = 13.sp,
                            color = Color(0xFF92400E),
                            lineHeight = 19.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(18.dp))
            }

            // PRIMARY REGIONAL UNITS SECTION
            Text(
                text = "${AppStrings.primaryUnitsHeader(lang)} (${if (region == Region.SINDH) "Sindh" else "Punjab"})",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                ),
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (region == Region.SINDH) {
                // Sindh Primary: Wesa, Gunta, Jareeb, Acre
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    UnitCard(
                        title = AppStrings.unitWesa(lang),
                        value = LandCalculator.formatNumberAccurate(result.wesa),
                        formula = "1 Wesa = 1,089 sq ft",
                        isPrimary = true,
                        modifier = Modifier.weight(1f),
                        testTag = "unit_wesa_card"
                    )
                    UnitCard(
                        title = AppStrings.unitGunta(lang),
                        value = LandCalculator.formatNumberAccurate(result.gunta),
                        formula = "1 Gunta = 1,089 sq ft",
                        isPrimary = true,
                        modifier = Modifier.weight(1f),
                        testTag = "unit_gunta_card"
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    UnitCard(
                        title = AppStrings.unitJareeb(lang),
                        value = LandCalculator.formatNumberAccurate(result.jareeb),
                        formula = "1 Jareeb = 20 Wesa",
                        isPrimary = true,
                        modifier = Modifier.weight(1f),
                        testTag = "unit_jareeb_card"
                    )
                    UnitCard(
                        title = AppStrings.unitAcre(lang),
                        value = LandCalculator.formatNumberAccurate(result.acre),
                        formula = "1 Acre = 40 Wesa",
                        isPrimary = true,
                        modifier = Modifier.weight(1f),
                        testTag = "unit_acre_card"
                    )
                }
            } else {
                // Punjab Primary: Marla, Kanal, Acre
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    UnitCard(
                        title = AppStrings.unitMarla(lang),
                        value = LandCalculator.formatNumberAccurate(result.marla),
                        formula = "1 Marla = 272.25 sq ft",
                        isPrimary = true,
                        modifier = Modifier.weight(1f),
                        testTag = "unit_marla_card"
                    )
                    UnitCard(
                        title = AppStrings.unitKanal(lang),
                        value = LandCalculator.formatNumberAccurate(result.kanal),
                        formula = "1 Kanal = 20 Marla",
                        isPrimary = true,
                        modifier = Modifier.weight(1f),
                        testTag = "unit_kanal_card"
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                UnitCard(
                    title = AppStrings.unitAcre(lang),
                    value = LandCalculator.formatNumberAccurate(result.acre),
                    formula = "1 Acre = 8 Kanal = 160 Marla",
                    isPrimary = true,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "unit_acre_card"
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // SECONDARY / OTHER REGIONAL & STANDARD UNITS
            Text(
                text = AppStrings.otherUnitsHeader(lang),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (region == Region.SINDH) {
                // Secondary for Sindh: Marla, Kanal
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    UnitCard(
                        title = AppStrings.unitMarla(lang),
                        value = LandCalculator.formatNumberAccurate(result.marla),
                        formula = "272.25 sq ft",
                        isPrimary = false,
                        modifier = Modifier.weight(1f),
                        testTag = "unit_sec_marla"
                    )
                    UnitCard(
                        title = AppStrings.unitKanal(lang),
                        value = LandCalculator.formatNumberAccurate(result.kanal),
                        formula = "5,445 sq ft",
                        isPrimary = false,
                        modifier = Modifier.weight(1f),
                        testTag = "unit_sec_kanal"
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            } else {
                // Secondary for Punjab: Wesa, Gunta, Jareeb
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    UnitCard(
                        title = AppStrings.unitWesa(lang),
                        value = LandCalculator.formatNumberAccurate(result.wesa),
                        formula = "1,089 sq ft",
                        isPrimary = false,
                        modifier = Modifier.weight(1f),
                        testTag = "unit_sec_wesa"
                    )
                    UnitCard(
                        title = AppStrings.unitJareeb(lang),
                        value = LandCalculator.formatNumberAccurate(result.jareeb),
                        formula = "21,780 sq ft",
                        isPrimary = false,
                        modifier = Modifier.weight(1f),
                        testTag = "unit_sec_jareeb"
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Standard: Sq Feet & Sq Meters
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                UnitCard(
                    title = AppStrings.unitSqFt(lang),
                    value = LandCalculator.formatNumberAccurate(result.sqFeet),
                    formula = "Standard sq ft",
                    isPrimary = false,
                    modifier = Modifier.weight(1f),
                    testTag = "unit_sqft_card"
                )
                UnitCard(
                    title = AppStrings.unitSqM(lang),
                    value = LandCalculator.formatNumberAccurate(result.sqMeters),
                    formula = "1 sq m = 10.764 sq ft",
                    isPrimary = false,
                    modifier = Modifier.weight(1f),
                    testTag = "unit_sqm_card"
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // STEP-BY-STEP CALCULATION PROOF (Verification for buyer & seller)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, Color(0xFFD6DDD5)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = FertileGreenPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = AppStrings.calculationStepsHeader(lang),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            ),
                            color = FertileGreenPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    result.steps.forEachIndexed { index, step ->
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "${index + 1}. ${step.getTitle(lang)}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color(0xFF1E3A24)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFF6FAF4),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = step.getDetail(lang),
                                    fontSize = 13.sp,
                                    color = Color(0xFF333333),
                                    lineHeight = 19.sp,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                        if (index < result.steps.size - 1) {
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Share / Copy Summary Button
            OutlinedButton(
                onClick = {
                    val summaryText = buildString {
                        appendLine("--- ${AppStrings.appTitle(lang)} ---")
                        appendLine("${AppStrings.methodUsed(lang)}: ${result.getMethodName(lang)}")
                        if (result.isEstimate) {
                            appendLine("[${AppStrings.estimateWarningTitle(lang)}]")
                        }
                        val boundaryStep = result.steps.firstOrNull { it.titleEn == "Plot Boundaries & Directions" }
                        if (boundaryStep != null) {
                            appendLine(boundaryStep.getTitle(lang))
                            appendLine(boundaryStep.getDetail(lang))
                        }
                        appendLine("${AppStrings.unitSqFt(lang)}: ${LandCalculator.formatNumberAccurate(result.sqFeet)}")
                        appendLine("${AppStrings.unitWesa(lang)}: ${LandCalculator.formatNumberAccurate(result.wesa)}")
                        appendLine("${AppStrings.unitJareeb(lang)}: ${LandCalculator.formatNumberAccurate(result.jareeb)}")
                        appendLine("${AppStrings.unitMarla(lang)}: ${LandCalculator.formatNumberAccurate(result.marla)}")
                        appendLine("${AppStrings.unitKanal(lang)}: ${LandCalculator.formatNumberAccurate(result.kanal)}")
                        appendLine("${AppStrings.unitAcre(lang)}: ${LandCalculator.formatNumberAccurate(result.acre)}")
                        appendLine("${AppStrings.unitSqM(lang)}: ${LandCalculator.formatNumberAccurate(result.sqMeters)}")
                    }
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("Land Area Result", summaryText))
                    Toast.makeText(context, AppStrings.copiedToast(lang), Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("copy_summary_btn"),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = AppStrings.copySummary(lang),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Start New Measurement Button
            Button(
                onClick = onNewMeasurement,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("start_new_measurement_btn"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = FertileGreenPrimary)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = AppStrings.newMeasurement(lang),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun UnitCard(
    title: String,
    value: String,
    formula: String,
    isPrimary: Boolean,
    modifier: Modifier = Modifier,
    testTag: String
) {
    Card(
        modifier = modifier.testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isPrimary) Color(0xFFF3FAF2) else Color(0xFFF9FAF9)
        ),
        border = BorderStroke(
            width = if (isPrimary) 1.5.dp else 1.dp,
            color = if (isPrimary) FertileGreenPrimary else Color(0xFFDCE2DC)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isPrimary) 2.dp else 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = if (isPrimary) 15.sp else 13.sp,
                fontWeight = if (isPrimary) FontWeight.Bold else FontWeight.Medium,
                color = if (isPrimary) FertileGreenPrimary else Color(0xFF4A554D),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = if (isPrimary) 24.sp else 19.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1E211E),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = formula,
                fontSize = 11.sp,
                color = Color(0xFF7A857D),
                textAlign = TextAlign.Center
            )
        }
    }
}
