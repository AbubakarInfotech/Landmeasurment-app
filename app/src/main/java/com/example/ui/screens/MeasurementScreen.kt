package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.AppStrings
import com.example.model.CompassDirection
import com.example.model.Language
import com.example.model.PlotShape
import com.example.ui.components.ShapeDiagram
import com.example.ui.theme.AlertRed
import com.example.ui.theme.AlertRedContainer
import com.example.ui.theme.FertileGreenPrimary
import com.example.ui.theme.HarvestGold
import com.example.ui.theme.WarningAmber
import com.example.ui.theme.WarningAmberContainer

data class SideSummaryItem(
    val key: String,
    val label: String,
    val value: String,
    val boundary: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeasurementScreen(
    shape: PlotShape,
    lang: Language,
    inputs: Map<String, String>,
    curvedOffsets: List<String>,
    errorMessage: String?,
    focusedKey: String?,
    sideDirections: Map<String, CompassDirection>,
    sideBoundaries: Map<String, String>,
    duplicateDirection: CompassDirection?,
    isImpossibleTriangle: Boolean,
    onFocusChange: (String?) -> Unit,
    onInputChange: (key: String, value: String) -> Unit,
    onDirectionChange: (key: String, direction: CompassDirection?) -> Unit,
    onBoundaryChange: (key: String, boundary: String) -> Unit,
    onCurvedOffsetChange: (index: Int, value: String) -> Unit,
    onAddCurvedOffset: () -> Unit,
    onRemoveCurvedOffset: () -> Unit,
    onClear: () -> Unit,
    onCalculate: () -> Unit,
    onLoadTestCase: (Int) -> Unit,
    onOpenGpsWalking: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    val focusRequesters = remember {
        mutableMapOf<String, FocusRequester>()
    }
    fun getFocusRequester(key: String): FocusRequester {
        return focusRequesters.getOrPut(key) { FocusRequester() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = shape.getName(lang),
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("measurement_back_btn")
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = onOpenGpsWalking,
                        modifier = Modifier.testTag("measurement_gps_icon_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = AppStrings.measureByWalking(lang),
                            tint = FertileGreenPrimary
                        )
                    }
                    IconButton(
                        onClick = onClear,
                        modifier = Modifier.testTag("measurement_clear_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Clear")
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
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            // REAL-TIME LIVE DIAGRAM WITH COMPASS & DIRECTION PLACEMENT
            ShapeDiagram(
                shape = shape,
                lang = lang,
                inputs = inputs,
                focusedKey = focusedKey,
                sideDirections = sideDirections,
                sideBoundaries = sideBoundaries,
                curvedOffsets = curvedOffsets,
                isImpossibleTriangle = isImpossibleTriangle,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("live_shape_diagram")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // SMALL SUMMARY LIST UNDER THE DIAGRAM (with [Edit] button next to each)
            val summaryItems = getSummaryItems(shape, lang, inputs, sideDirections, sideBoundaries)
            if (summaryItems.isNotEmpty()) {
                MeasurementSummaryChips(
                    items = summaryItems,
                    focusedKey = focusedKey,
                    lang = lang,
                    onEditClick = { key ->
                        onFocusChange(key)
                        try {
                            getFocusRequester(key).requestFocus()
                        } catch (_: Exception) {}
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            // GENTLE WARNING IF TWO SIDES HAVE SAME DIRECTION BY MISTAKE
            if (duplicateDirection != null) {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("duplicate_direction_card"),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
                    border = BorderStroke(1.dp, HarvestGold),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Direction Notice",
                            tint = HarvestGold,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = AppStrings.duplicateDirectionWarning(duplicateDirection.getName(lang), lang),
                            fontSize = 13.sp,
                            color = Color(0xFF5D4037),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Warning Banner for 4-side estimate
            if (shape == PlotShape.FOUR_SIDE_ESTIMATE) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = WarningAmberContainer),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Warning",
                            tint = WarningAmber,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = AppStrings.estimateWarningTitle(lang),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color(0xFF78350F)
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = AppStrings.estimateWarningText(lang),
                                fontSize = 12.sp,
                                color = Color(0xFF92400E),
                                lineHeight = 17.sp
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Quick Test Presets
            when (shape) {
                PlotShape.FOUR_SIDE_ESTIMATE -> {
                    OutlinedButton(
                        onClick = { onLoadTestCase(1) },
                        modifier = Modifier.fillMaxWidth().testTag("load_test_case_1_btn")
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = when (lang) {
                                Language.ENGLISH -> "Load Test Case 1 (Top 65, Bottom 55, Left 295, Right 295 ft)"
                                Language.URDU -> "ٹیسٹ مثال 1 لوڈ کریں (اوپر 65، نیچے 55، بائیں 295، دائیں 295 فٹ)"
                                Language.SINDHI -> "ٽيسٽ مثال 1 لکو (مٿيون 65، هيٺيون 55، کاٻو 295، ساڄو 295 فوٽ)"
                            },
                            fontSize = 12.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
                PlotShape.RECTANGLE -> {
                    OutlinedButton(
                        onClick = { onLoadTestCase(2) },
                        modifier = Modifier.fillMaxWidth().testTag("load_test_case_2_btn")
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = when (lang) {
                                Language.ENGLISH -> "Load Test Case 2 (30 × 36.3 ft = 1 Wesa)"
                                Language.URDU -> "ٹیسٹ مثال 2 لوڈ کریں (30 × 36.3 فٹ = 1 ویسہ)"
                                Language.SINDHI -> "ٽيسٽ مثال 2 لکو (30 × 36.3 فوٽ = 1 ويهه)"
                            },
                            fontSize = 12.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
                PlotShape.TRIANGLE -> {
                    OutlinedButton(
                        onClick = { onLoadTestCase(3) },
                        modifier = Modifier.fillMaxWidth().testTag("load_test_case_3_btn")
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = when (lang) {
                                Language.ENGLISH -> "Load Test Case 3 (Impossible 10, 5, 3 ft)"
                                Language.URDU -> "ٹیسٹ مثال 3 لوڈ کریں (ناممکن مثلث 10، 5، 3 فٹ)"
                                Language.SINDHI -> "ٽيسٽ مثال 3 لکو (ناممڪن ٽڪنڊو 10، 5، 3 فوٽ)"
                            },
                            fontSize = 12.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
                else -> {}
            }

            // Input Fields Section Header
            Text(
                text = AppStrings.enterMeasurements(lang),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(10.dp))

            when (shape) {
                PlotShape.RECTANGLE -> {
                    SideEntryCard(
                        key = "length",
                        defaultLetter = "A",
                        defaultLabel = "Length",
                        value = inputs["length"] ?: "",
                        direction = sideDirections["length"],
                        boundary = sideBoundaries["length"] ?: "",
                        isFocused = focusedKey == "length",
                        lang = lang,
                        focusRequester = getFocusRequester("length"),
                        onFocus = { onFocusChange("length") },
                        onValueChange = { onInputChange("length", it) },
                        onDirectionChange = { onDirectionChange("length", it) },
                        onBoundaryChange = { onBoundaryChange("length", it) },
                        testTag = "input_length"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    SideEntryCard(
                        key = "width",
                        defaultLetter = "B",
                        defaultLabel = "Width",
                        value = inputs["width"] ?: "",
                        direction = sideDirections["width"],
                        boundary = sideBoundaries["width"] ?: "",
                        isFocused = focusedKey == "width",
                        lang = lang,
                        focusRequester = getFocusRequester("width"),
                        onFocus = { onFocusChange("width") },
                        onValueChange = { onInputChange("width", it) },
                        onDirectionChange = { onDirectionChange("width", it) },
                        onBoundaryChange = { onBoundaryChange("width", it) },
                        testTag = "input_width"
                    )
                }
                PlotShape.TRIANGLE -> {
                    listOf("side1" to "A", "side2" to "B", "side3" to "C").forEach { (key, letter) ->
                        SideEntryCard(
                            key = key,
                            defaultLetter = letter,
                            defaultLabel = AppStrings.sideLabel(letter, lang),
                            value = inputs[key] ?: "",
                            direction = sideDirections[key],
                            boundary = sideBoundaries[key] ?: "",
                            isFocused = focusedKey == key,
                            lang = lang,
                            focusRequester = getFocusRequester(key),
                            onFocus = { onFocusChange(key) },
                            onValueChange = { onInputChange(key, it) },
                            onDirectionChange = { onDirectionChange(key, it) },
                            onBoundaryChange = { onBoundaryChange(key, it) },
                            testTag = "input_$key"
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
                PlotShape.TRAPEZIUM -> {
                    SideEntryCard(
                        key = "parallel1",
                        defaultLetter = "A",
                        defaultLabel = "Parallel Side 1",
                        value = inputs["parallel1"] ?: "",
                        direction = sideDirections["parallel1"],
                        boundary = sideBoundaries["parallel1"] ?: "",
                        isFocused = focusedKey == "parallel1",
                        lang = lang,
                        focusRequester = getFocusRequester("parallel1"),
                        onFocus = { onFocusChange("parallel1") },
                        onValueChange = { onInputChange("parallel1", it) },
                        onDirectionChange = { onDirectionChange("parallel1", it) },
                        onBoundaryChange = { onBoundaryChange("parallel1", it) },
                        testTag = "input_parallel1"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    SideEntryCard(
                        key = "parallel2",
                        defaultLetter = "B",
                        defaultLabel = "Parallel Side 2",
                        value = inputs["parallel2"] ?: "",
                        direction = sideDirections["parallel2"],
                        boundary = sideBoundaries["parallel2"] ?: "",
                        isFocused = focusedKey == "parallel2",
                        lang = lang,
                        focusRequester = getFocusRequester("parallel2"),
                        onFocus = { onFocusChange("parallel2") },
                        onValueChange = { onInputChange("parallel2", it) },
                        onDirectionChange = { onDirectionChange("parallel2", it) },
                        onBoundaryChange = { onBoundaryChange("parallel2", it) },
                        testTag = "input_parallel2"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    SideEntryCard(
                        key = "distance",
                        defaultLetter = "H",
                        defaultLabel = "Distance Between Them",
                        value = inputs["distance"] ?: "",
                        direction = null,
                        boundary = "",
                        isFocused = focusedKey == "distance",
                        lang = lang,
                        focusRequester = getFocusRequester("distance"),
                        onFocus = { onFocusChange("distance") },
                        onValueChange = { onInputChange("distance", it) },
                        onDirectionChange = { },
                        onBoundaryChange = { },
                        showDirectionAndBoundary = false,
                        testTag = "input_distance"
                    )
                }
                PlotShape.FOUR_SIDE_DIAGONAL -> {
                    listOf("side1" to "A", "side2" to "B", "side3" to "C", "side4" to "D").forEach { (key, letter) ->
                        SideEntryCard(
                            key = key,
                            defaultLetter = letter,
                            defaultLabel = AppStrings.sideLabel(letter, lang),
                            value = inputs[key] ?: "",
                            direction = sideDirections[key],
                            boundary = sideBoundaries[key] ?: "",
                            isFocused = focusedKey == key,
                            lang = lang,
                            focusRequester = getFocusRequester(key),
                            onFocus = { onFocusChange(key) },
                            onValueChange = { onInputChange(key, it) },
                            onDirectionChange = { onDirectionChange(key, it) },
                            onBoundaryChange = { onBoundaryChange(key, it) },
                            testTag = "input_$key"
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    SideEntryCard(
                        key = "diagonal",
                        defaultLetter = "Diag",
                        defaultLabel = AppStrings.diagonalLabel("", lang),
                        value = inputs["diagonal"] ?: "",
                        direction = null,
                        boundary = "",
                        isFocused = focusedKey == "diagonal",
                        lang = lang,
                        focusRequester = getFocusRequester("diagonal"),
                        onFocus = { onFocusChange("diagonal") },
                        onValueChange = { onInputChange("diagonal", it) },
                        onDirectionChange = { },
                        onBoundaryChange = { },
                        showDirectionAndBoundary = false,
                        testTag = "input_diagonal"
                    )
                }
                PlotShape.FOUR_SIDE_ESTIMATE -> {
                    listOf(
                        Triple("top", "A", "Top"),
                        Triple("bottom", "B", "Bottom"),
                        Triple("left", "C", "Left"),
                        Triple("right", "D", "Right")
                    ).forEach { (key, letter, pos) ->
                        SideEntryCard(
                            key = key,
                            defaultLetter = letter,
                            defaultLabel = "$pos / Side $letter",
                            value = inputs[key] ?: "",
                            direction = sideDirections[key],
                            boundary = sideBoundaries[key] ?: "",
                            isFocused = focusedKey == key,
                            lang = lang,
                            focusRequester = getFocusRequester(key),
                            onFocus = { onFocusChange(key) },
                            onValueChange = { onInputChange(key, it) },
                            onDirectionChange = { onDirectionChange(key, it) },
                            onBoundaryChange = { onBoundaryChange(key, it) },
                            testTag = "input_$key"
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
                PlotShape.MULTI_SIDE_POLYGON -> {
                    listOf("side1" to "A", "side2" to "B", "side3" to "C", "side4" to "D", "side5" to "E").forEach { (key, letter) ->
                        SideEntryCard(
                            key = key,
                            defaultLetter = letter,
                            defaultLabel = AppStrings.sideLabel(letter, lang),
                            value = inputs[key] ?: "",
                            direction = sideDirections[key],
                            boundary = sideBoundaries[key] ?: "",
                            isFocused = focusedKey == key,
                            lang = lang,
                            focusRequester = getFocusRequester(key),
                            onFocus = { onFocusChange(key) },
                            onValueChange = { onInputChange(key, it) },
                            onDirectionChange = { onDirectionChange(key, it) },
                            onBoundaryChange = { onBoundaryChange(key, it) },
                            testTag = "input_$key"
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    SideEntryCard(
                        key = "diag1",
                        defaultLetter = "D1",
                        defaultLabel = AppStrings.diagonalLabel("1", lang),
                        value = inputs["diag1"] ?: "",
                        direction = null,
                        boundary = "",
                        isFocused = focusedKey == "diag1",
                        lang = lang,
                        focusRequester = getFocusRequester("diag1"),
                        onFocus = { onFocusChange("diag1") },
                        onValueChange = { onInputChange("diag1", it) },
                        onDirectionChange = { },
                        onBoundaryChange = { },
                        showDirectionAndBoundary = false,
                        testTag = "input_diag1"
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    SideEntryCard(
                        key = "diag2",
                        defaultLetter = "D2",
                        defaultLabel = AppStrings.diagonalLabel("2", lang),
                        value = inputs["diag2"] ?: "",
                        direction = null,
                        boundary = "",
                        isFocused = focusedKey == "diag2",
                        lang = lang,
                        focusRequester = getFocusRequester("diag2"),
                        onFocus = { onFocusChange("diag2") },
                        onValueChange = { onInputChange("diag2", it) },
                        onDirectionChange = { },
                        onBoundaryChange = { },
                        showDirectionAndBoundary = false,
                        testTag = "input_diag2"
                    )
                }
                PlotShape.CURVED_SIDE -> {
                    SideEntryCard(
                        key = "baseline",
                        defaultLetter = "Base",
                        defaultLabel = "Straight Baseline",
                        value = inputs["baseline"] ?: "",
                        direction = sideDirections["baseline"],
                        boundary = sideBoundaries["baseline"] ?: "",
                        isFocused = focusedKey == "baseline",
                        lang = lang,
                        focusRequester = getFocusRequester("baseline"),
                        onFocus = { onFocusChange("baseline") },
                        onValueChange = { onInputChange("baseline", it) },
                        onDirectionChange = { onDirectionChange("baseline", it) },
                        onBoundaryChange = { onBoundaryChange("baseline", it) },
                        testTag = "input_baseline"
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Offset Heights at Equal Intervals (feet):",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    curvedOffsets.forEachIndexed { idx, offsetVal ->
                        val key = "offset_$idx"
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "#${idx + 1}:",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.width(36.dp)
                            )
                            OutlinedTextField(
                                value = offsetVal,
                                onValueChange = { onCurvedOffsetChange(idx, it) },
                                label = { Text("Offset ${idx + 1} (ft)") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .focusRequester(getFocusRequester(key))
                                    .onFocusChanged { if (it.isFocused) onFocusChange(key) }
                                    .testTag("input_offset_$idx")
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = onAddCurvedOffset,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Add Point")
                        }
                        OutlinedButton(
                            onClick = onRemoveCurvedOffset,
                            modifier = Modifier.weight(1f),
                            enabled = curvedOffsets.size > 2
                        ) {
                            Icon(imageVector = Icons.Default.Remove, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Remove")
                        }
                    }
                }
            }

            // Error Display
            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("error_message_card"),
                    colors = CardDefaults.cardColors(containerColor = AlertRedContainer),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = "Error",
                            tint = AlertRed,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = errorMessage,
                            color = AlertRed,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Button
            Button(
                onClick = onCalculate,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("calculate_area_btn"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = FertileGreenPrimary)
            ) {
                Icon(
                    imageVector = Icons.Default.Calculate,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = AppStrings.calculateArea(lang),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// Side Entry Card with Compass Direction Dropdown and Boundary Note
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SideEntryCard(
    key: String,
    defaultLetter: String,
    defaultLabel: String,
    value: String,
    direction: CompassDirection?,
    boundary: String,
    isFocused: Boolean,
    lang: Language,
    focusRequester: FocusRequester,
    onFocus: () -> Unit,
    onValueChange: (String) -> Unit,
    onDirectionChange: (CompassDirection?) -> Unit,
    onBoundaryChange: (String) -> Unit,
    showDirectionAndBoundary: Boolean = true,
    testTag: String
) {
    var expandedDirectionDropdown by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isFocused) Color(0xFFFFF9ED) else Color(0xFFFAFCF9)
        ),
        border = BorderStroke(
            width = if (isFocused) 1.5.dp else 1.dp,
            color = if (isFocused) Color(0xFFE65100) else Color(0xFFD6DDD5)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Side title + Direction Tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (direction != null) "${direction.getName(lang)} ${AppStrings.sideWord(lang)}" else defaultLabel,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = if (isFocused) Color(0xFFE65100) else FertileGreenPrimary
                )

                if (showDirectionAndBoundary) {
                    Box {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (direction != null) Color(0xFFE8F5E9) else Color(0xFFF1F3F1),
                            border = BorderStroke(1.dp, if (direction != null) FertileGreenPrimary else Color(0xFFCBD5CB)),
                            modifier = Modifier
                                .clickable { expandedDirectionDropdown = true }
                                .testTag("direction_dropdown_trigger_$key")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Explore,
                                    contentDescription = null,
                                    tint = if (direction != null) FertileGreenPrimary else Color(0xFF666666),
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = direction?.getName(lang) ?: AppStrings.direction(lang),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (direction != null) FertileGreenPrimary else Color(0xFF444444)
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = expandedDirectionDropdown,
                            onDismissRequest = { expandedDirectionDropdown = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(AppStrings.noDirection(lang)) },
                                onClick = {
                                    onDirectionChange(null)
                                    expandedDirectionDropdown = false
                                },
                                modifier = Modifier.testTag("direction_option_none_$key")
                            )
                            CompassDirection.values().forEach { dir ->
                                DropdownMenuItem(
                                    text = { Text(dir.getName(lang)) },
                                    onClick = {
                                        onDirectionChange(dir)
                                        expandedDirectionDropdown = false
                                    },
                                    modifier = Modifier.testTag("direction_option_${dir.name.lowercase()}_$key")
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Main feet input field
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                label = { Text("${AppStrings.enterMeasurements(lang)} (${AppStrings.feetUnit(lang)})") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                shape = RoundedCornerShape(12.dp),
                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (isFocused) Color(0xFFE65100) else FertileGreenPrimary,
                    focusedLabelColor = if (isFocused) Color(0xFFE65100) else FertileGreenPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .onFocusChanged { if (it.isFocused) onFocus() }
                    .testTag(testTag)
            )

            // Optional Boundary / Neighbour input
            if (showDirectionAndBoundary) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = boundary,
                    onValueChange = onBoundaryChange,
                    label = { Text(AppStrings.boundaryNeighbour(lang)) },
                    placeholder = { Text(AppStrings.boundaryHint(lang), fontSize = 12.sp) },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_boundary_$key")
                )
            }
        }
    }
}

// Summary chips component under the diagram with Edit buttons
@Composable
private fun MeasurementSummaryChips(
    items: List<SideSummaryItem>,
    focusedKey: String?,
    lang: Language,
    onEditClick: (String) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items.forEach { item ->
                val isFocused = focusedKey == item.key
                val hasValue = item.value.isNotBlank()

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isFocused) Color(0xFFFFF3E0) else if (hasValue) Color(0xFFE8F5E9) else Color(0xFFF1F3F1),
                    border = BorderStroke(
                        width = if (isFocused) 1.5.dp else 1.dp,
                        color = if (isFocused) Color(0xFFE65100) else if (hasValue) FertileGreenPrimary else Color(0xFFCBD5CB)
                    ),
                    modifier = Modifier
                        .clickable { onEditClick(item.key) }
                        .testTag("summary_chip_${item.key}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val valStr = if (hasValue) "${item.value} ft" else "?"
                        val boundaryStr = if (!item.boundary.isNullOrBlank()) " [${item.boundary}]" else ""
                        Text(
                            text = "${item.label}: $valStr$boundaryStr",
                            fontWeight = if (isFocused || hasValue) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp,
                            color = if (isFocused) Color(0xFFE65100) else if (hasValue) Color(0xFF1B5E20) else Color(0xFF555555)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = AppStrings.edit(lang),
                            tint = if (isFocused) Color(0xFFE65100) else Color(0xFF757575),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun getSummaryItems(
    shape: PlotShape,
    lang: Language,
    inputs: Map<String, String>,
    sideDirections: Map<String, CompassDirection>,
    sideBoundaries: Map<String, String>
): List<SideSummaryItem> {
    fun labelFor(key: String, defaultLetter: String): String {
        val dir = sideDirections[key]
        return if (dir != null) {
            "${dir.getName(lang)} ${AppStrings.sideWord(lang)}"
        } else {
            AppStrings.sideLabel(defaultLetter, lang)
        }
    }

    return when (shape) {
        PlotShape.RECTANGLE -> listOf(
            SideSummaryItem("length", labelFor("length", "A (Length)"), inputs["length"] ?: "", sideBoundaries["length"]),
            SideSummaryItem("width", labelFor("width", "B (Width)"), inputs["width"] ?: "", sideBoundaries["width"])
        )
        PlotShape.TRIANGLE -> listOf(
            SideSummaryItem("side1", labelFor("side1", "A"), inputs["side1"] ?: "", sideBoundaries["side1"]),
            SideSummaryItem("side2", labelFor("side2", "B"), inputs["side2"] ?: "", sideBoundaries["side2"]),
            SideSummaryItem("side3", labelFor("side3", "C"), inputs["side3"] ?: "", sideBoundaries["side3"])
        )
        PlotShape.TRAPEZIUM -> listOf(
            SideSummaryItem("parallel1", labelFor("parallel1", "Parallel 1"), inputs["parallel1"] ?: "", sideBoundaries["parallel1"]),
            SideSummaryItem("parallel2", labelFor("parallel2", "Parallel 2"), inputs["parallel2"] ?: "", sideBoundaries["parallel2"]),
            SideSummaryItem("distance", "Distance", inputs["distance"] ?: "", null)
        )
        PlotShape.FOUR_SIDE_DIAGONAL -> listOf(
            SideSummaryItem("side1", labelFor("side1", "A"), inputs["side1"] ?: "", sideBoundaries["side1"]),
            SideSummaryItem("side2", labelFor("side2", "B"), inputs["side2"] ?: "", sideBoundaries["side2"]),
            SideSummaryItem("side3", labelFor("side3", "C"), inputs["side3"] ?: "", sideBoundaries["side3"]),
            SideSummaryItem("side4", labelFor("side4", "D"), inputs["side4"] ?: "", sideBoundaries["side4"]),
            SideSummaryItem("diagonal", AppStrings.diagonalLabel("", lang), inputs["diagonal"] ?: "", null)
        )
        PlotShape.FOUR_SIDE_ESTIMATE -> listOf(
            SideSummaryItem("top", labelFor("top", "A (Top)"), inputs["top"] ?: "", sideBoundaries["top"]),
            SideSummaryItem("bottom", labelFor("bottom", "B (Bottom)"), inputs["bottom"] ?: "", sideBoundaries["bottom"]),
            SideSummaryItem("left", labelFor("left", "C (Left)"), inputs["left"] ?: "", sideBoundaries["left"]),
            SideSummaryItem("right", labelFor("right", "D (Right)"), inputs["right"] ?: "", sideBoundaries["right"])
        )
        PlotShape.MULTI_SIDE_POLYGON -> listOf(
            SideSummaryItem("side1", labelFor("side1", "A"), inputs["side1"] ?: "", sideBoundaries["side1"]),
            SideSummaryItem("side2", labelFor("side2", "B"), inputs["side2"] ?: "", sideBoundaries["side2"]),
            SideSummaryItem("side3", labelFor("side3", "C"), inputs["side3"] ?: "", sideBoundaries["side3"]),
            SideSummaryItem("side4", labelFor("side4", "D"), inputs["side4"] ?: "", sideBoundaries["side4"]),
            SideSummaryItem("side5", labelFor("side5", "E"), inputs["side5"] ?: "", sideBoundaries["side5"]),
            SideSummaryItem("diag1", AppStrings.diagonalLabel("1", lang), inputs["diag1"] ?: "", null),
            SideSummaryItem("diag2", AppStrings.diagonalLabel("2", lang), inputs["diag2"] ?: "", null)
        )
        PlotShape.CURVED_SIDE -> listOf(
            SideSummaryItem("baseline", "Baseline", inputs["baseline"] ?: "", sideBoundaries["baseline"])
        )
    }
}
