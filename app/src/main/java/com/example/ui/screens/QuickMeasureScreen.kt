package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculator.LandCalculator
import com.example.localization.AppStrings
import com.example.model.CalculationResult
import com.example.model.CompassDirection
import com.example.model.Language
import com.example.model.QuickMeasurePart
import com.example.model.Region
import com.example.ui.components.QuickMeasureDiagram
import com.example.ui.theme.FertileGreenPrimary
import com.example.ui.theme.HarvestGold

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun QuickMeasureScreen(
    parts: List<QuickMeasurePart>,
    activePartIndex: Int,
    currentLanguage: Language,
    currentRegion: Region,
    onPartSelect: (Int) -> Unit,
    onAddPart: () -> Unit,
    onRemovePart: (Int) -> Unit,
    onInputModeChange: (Boolean) -> Unit,
    onInputTextChange: (String) -> Unit,
    onIndividualSideChange: (Int, String) -> Unit,
    onAddIndividualSide: () -> Unit,
    onRemoveIndividualSide: (Int) -> Unit,
    onDiagonalChange: (String) -> Unit,
    onToggleDiagonal: (Boolean) -> Unit,
    onSideDirectionChange: (Int, CompassDirection?) -> Unit,
    onSideBoundaryChange: (Int, String) -> Unit,
    onSideFocusChange: (Int?) -> Unit,
    onCalculate: () -> Unit,
    onLoadTestCase: (Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val layoutDirection = if (currentLanguage.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr
    val activePart = parts.getOrElse(activePartIndex) { parts.first() }
    val parsedSides = activePart.getParsedSides()
    val diagValue = activePart.diagonalInput.toDoubleOrNull()

    // Aggregate calculations for multi-part land
    val calculatedParts = parts.filter { it.calculationResult != null }
    val totalSqFeet = parts.sumOf { it.calculationResult?.sqFeet ?: 0.0 }
    val hasMultipleParts = parts.size > 1

    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = AppStrings.quickMeasureTitle(currentLanguage),
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Text(
                                text = AppStrings.quickMeasureSubtitle(currentLanguage),
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.testTag("quick_measure_back_btn")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = AppStrings.back(currentLanguage)
                            )
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .testTag("quick_measure_scroll_column"),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Multi-Part Land Tabs ("Add another part")
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Layers,
                                    contentDescription = null,
                                    tint = FertileGreenPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (hasMultipleParts) {
                                        AppStrings.partsCountSubtitle(parts.size, currentLanguage)
                                    } else {
                                        AppStrings.partTabLabel(1, currentLanguage)
                                    },
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = FertileGreenPrimary
                                )
                            }

                            // "+ Add Another Part" button
                            OutlinedButton(
                                onClick = onAddPart,
                                modifier = Modifier.testTag("quick_measure_add_part_btn"),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.2.dp, FertileGreenPrimary),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    tint = FertileGreenPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = AppStrings.addAnotherPart(currentLanguage),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FertileGreenPrimary
                                )
                            }
                        }

                        if (parts.size > 1) {
                            Spacer(modifier = Modifier.height(10.dp))
                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                itemsIndexed(parts) { idx, part ->
                                    val isSelected = idx == activePartIndex
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (isSelected) FertileGreenPrimary else Color(0xFFF1F6ED),
                                        border = BorderStroke(1.dp, if (isSelected) FertileGreenPrimary else Color(0xFFC7D7C4)),
                                        modifier = Modifier
                                            .clickable { onPartSelect(idx) }
                                            .testTag("quick_measure_part_tab_$idx")
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = AppStrings.partTabLabel(idx + 1, currentLanguage),
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                fontSize = 13.sp,
                                                color = if (isSelected) Color.White else Color(0xFF1E211E)
                                            )
                                            if (part.calculationResult != null) {
                                                Spacer(modifier = Modifier.width(6.dp))
                                                val areaDisplay = LandCalculator.formatNumber(part.calculationResult.wesa)
                                                Text(
                                                    text = "($areaDisplay wesa)",
                                                    fontSize = 11.sp,
                                                    color = if (isSelected) Color(0xFFE8F5E9) else Color(0xFF2E8B4C)
                                                )
                                            }
                                            if (parts.size > 1 && isSelected) {
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = AppStrings.deletePart(currentLanguage),
                                                    tint = Color.White,
                                                    modifier = Modifier
                                                        .size(16.dp)
                                                        .clickable { onRemovePart(idx) }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 2. Test Cases Quick Chips
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Standard Test Cases (Tap to Test):",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFFFF8E1),
                                border = BorderStroke(1.dp, Color(0xFFFFB300)),
                                modifier = Modifier
                                    .clickable { onLoadTestCase(1) }
                                    .testTag("quick_measure_chip_test1")
                            ) {
                                Text(
                                    text = AppStrings.testCaseChip1(currentLanguage),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF8D5B00),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFE8F5E9),
                                border = BorderStroke(1.dp, Color(0xFF81C784)),
                                modifier = Modifier
                                    .clickable { onLoadTestCase(2) }
                                    .testTag("quick_measure_chip_test2")
                            ) {
                                Text(
                                    text = AppStrings.testCaseChip2(currentLanguage),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1B5E20),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFFFEBEE),
                                border = BorderStroke(1.dp, Color(0xFFE57373)),
                                modifier = Modifier
                                    .clickable { onLoadTestCase(3) }
                                    .testTag("quick_measure_chip_test3")
                            ) {
                                Text(
                                    text = AppStrings.testCaseChip3(currentLanguage),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFB71C1C),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

                // 3. Live Scaled Diagram with Compass and Side Summary List
                item {
                    val isImpossible = activePart.errorMessage != null && activePart.errorMessage.contains("Impossible", ignoreCase = true)
                    QuickMeasureDiagram(
                        sides = parsedSides,
                        focusedIndex = activePart.focusedSideIndex,
                        sideDirections = activePart.sideDirections,
                        sideBoundaries = activePart.sideBoundaries,
                        hasDiagonal = activePart.hasDiagonal,
                        diagonalValue = diagValue,
                        lang = currentLanguage,
                        isImpossible = isImpossible,
                        errorMessage = activePart.errorMessage,
                        onSideClick = { sideIdx -> onSideFocusChange(sideIdx) }
                    )
                }

                // 4. Input Mode Toggle (List vs One-by-One)
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        border = BorderStroke(1.dp, Color(0xFFD6DDD5))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilterChip(
                                    selected = activePart.isListMode,
                                    onClick = { onInputModeChange(true) },
                                    label = { Text(AppStrings.quickMeasureInputList(currentLanguage), fontWeight = FontWeight.Bold) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("quick_measure_tab_list"),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = FertileGreenPrimary,
                                        selectedLabelColor = Color.White
                                    )
                                )

                                FilterChip(
                                    selected = !activePart.isListMode,
                                    onClick = { onInputModeChange(false) },
                                    label = { Text(AppStrings.quickMeasureInputOneByOne(currentLanguage), fontWeight = FontWeight.Bold) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("quick_measure_tab_one_by_one"),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = FertileGreenPrimary,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            if (activePart.isListMode) {
                                // List Mode Input
                                Text(
                                    text = AppStrings.quickMeasureListLabel(currentLanguage),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedTextField(
                                    value = activePart.rawInputText,
                                    onValueChange = onInputTextChange,
                                    placeholder = { Text(AppStrings.quickMeasureListPlaceholder(currentLanguage)) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("quick_measure_input_text"),
                                    shape = RoundedCornerShape(12.dp),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = FertileGreenPrimary,
                                        unfocusedBorderColor = Color(0xFFB0B9AE)
                                    )
                                )
                            } else {
                                // One by One Mode Input
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    activePart.individualSides.forEachIndexed { sIdx, sideVal ->
                                        IndividualSideRow(
                                            index = sIdx,
                                            value = sideVal,
                                            direction = activePart.sideDirections[sIdx],
                                            boundary = activePart.sideBoundaries[sIdx] ?: "",
                                            canRemove = activePart.individualSides.size > 3,
                                            lang = currentLanguage,
                                            onValueChange = { newVal -> onIndividualSideChange(sIdx, newVal) },
                                            onDirectionChange = { newDir -> onSideDirectionChange(sIdx, newDir) },
                                            onBoundaryChange = { newBound -> onSideBoundaryChange(sIdx, newBound) },
                                            onRemove = { onRemoveIndividualSide(sIdx) },
                                            onFocus = { onSideFocusChange(sIdx) }
                                        )
                                    }

                                    Button(
                                        onClick = onAddIndividualSide,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("quick_measure_add_side_btn"),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE8F5E9)),
                                        contentPadding = PaddingValues(vertical = 10.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = null,
                                            tint = FertileGreenPrimary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = AppStrings.addSide(currentLanguage),
                                            color = FertileGreenPrimary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Optional Diagonal Section
                            if (!activePart.hasDiagonal) {
                                OutlinedButton(
                                    onClick = { onToggleDiagonal(true) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("quick_measure_toggle_diagonal_btn"),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, Color(0xFFC47D00))
                                ) {
                                    Text(
                                        text = AppStrings.addDiagonal(currentLanguage),
                                        color = Color(0xFFC47D00),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            } else {
                                Surface(
                                    color = Color(0xFFFFF8E1),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, Color(0xFFFFB300)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = AppStrings.diagonalFieldLabel(currentLanguage),
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF7A4A00)
                                            )
                                            Text(
                                                text = AppStrings.removeDiagonal(currentLanguage),
                                                fontSize = 12.sp,
                                                color = Color(0xFFD32F2F),
                                                fontWeight = FontWeight.SemiBold,
                                                modifier = Modifier
                                                    .clickable { onToggleDiagonal(false) }
                                                    .testTag("quick_measure_remove_diagonal_btn")
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))

                                        OutlinedTextField(
                                            value = activePart.diagonalInput,
                                            onValueChange = onDiagonalChange,
                                            placeholder = { Text("e.g. 150") },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("quick_measure_diagonal_input"),
                                            shape = RoundedCornerShape(10.dp),
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = Color(0xFFC47D00),
                                                unfocusedBorderColor = Color(0xFFFFB300)
                                            )
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Calculate Button
                            Button(
                                onClick = onCalculate,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .testTag("quick_measure_calculate_btn"),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = FertileGreenPrimary)
                            ) {
                                Text(
                                    text = AppStrings.calculateArea(currentLanguage),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // 5. Error Banner (if error)
                if (activePart.errorMessage != null) {
                    item {
                        Surface(
                            color = Color(0xFFFFEBEE),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.2.dp, Color(0xFFE57373)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("quick_measure_error_banner")
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = Color(0xFFD32F2F),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = activePart.errorMessage,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFFB71C1C)
                                )
                            }
                        }
                    }
                }

                // 6. Calculation Results Card
                if (activePart.calculationResult != null) {
                    item {
                        QuickMeasureResultCard(
                            result = activePart.calculationResult,
                            lang = currentLanguage,
                            region = currentRegion
                        )
                    }
                }

                // 7. Combined Total Card (when multiple parts exist)
                if (hasMultipleParts && totalSqFeet > 0.0) {
                    item {
                        CombinedPartsTotalCard(
                            parts = parts,
                            totalSqFeet = totalSqFeet,
                            lang = currentLanguage,
                            region = currentRegion
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun IndividualSideRow(
    index: Int,
    value: String,
    direction: CompassDirection?,
    boundary: String,
    canRemove: Boolean,
    lang: Language,
    onValueChange: (String) -> Unit,
    onDirectionChange: (CompassDirection?) -> Unit,
    onBoundaryChange: (String) -> Unit,
    onRemove: () -> Unit,
    onFocus: () -> Unit
) {
    val letter = ('A' + index).toString()
    var showDirectionMenu by remember { mutableStateOf(false) }

    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.outlinedCardColors(containerColor = Color(0xFFFAFCF9)),
        border = BorderStroke(1.dp, Color(0xFFD6DDD5))
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Side badge
                Surface(
                    shape = CircleShape,
                    color = FertileGreenPrimary,
                    modifier = Modifier.size(28.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = letter,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Side Value input
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    label = { Text("${AppStrings.sideWord(lang)} $letter (ft)") },
                    modifier = Modifier
                        .weight(1f)
                        .onFocusChanged { if (it.isFocused) onFocus() }
                        .testTag("quick_measure_side_input_$index"),
                    shape = RoundedCornerShape(10.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(6.dp))

                // Direction Dropdown Button
                Box {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (direction != null) Color(0xFFE8F5E9) else Color(0xFFEEEEEE),
                        border = BorderStroke(1.dp, if (direction != null) FertileGreenPrimary else Color.LightGray),
                        modifier = Modifier
                            .clickable { showDirectionMenu = true }
                            .testTag("quick_measure_dir_btn_$index")
                    ) {
                        Text(
                            text = direction?.getName(lang) ?: AppStrings.chooseDirectionPrompt(lang),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (direction != null) FertileGreenPrimary else Color.DarkGray,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = showDirectionMenu,
                        onDismissRequest = { showDirectionMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("None") },
                            onClick = {
                                onDirectionChange(null)
                                showDirectionMenu = false
                            }
                        )
                        CompassDirection.values().forEach { d ->
                            DropdownMenuItem(
                                text = { Text(d.getName(lang)) },
                                onClick = {
                                    onDirectionChange(d)
                                    showDirectionMenu = false
                                }
                            )
                        }
                    }
                }

                if (canRemove) {
                    IconButton(
                        onClick = onRemove,
                        modifier = Modifier.testTag("quick_measure_remove_side_$index")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Remove",
                            tint = Color(0xFFD32F2F),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Boundary / Neighbour note input
            OutlinedTextField(
                value = boundary,
                onValueChange = onBoundaryChange,
                placeholder = { Text(AppStrings.boundaryNoteHint(lang), fontSize = 12.sp) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("quick_measure_boundary_input_$index"),
                shape = RoundedCornerShape(8.dp),
                singleLine = true
            )
        }
    }
}

@Composable
private fun QuickMeasureResultCard(
    result: CalculationResult,
    lang: Language,
    region: Region
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("quick_measure_result_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.5.dp, Color(0xFFC7D7C4))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header with badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = AppStrings.resultHeader(lang),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = FertileGreenPrimary
                )

                // Status Badge (EXACT vs ESTIMATE)
                val isEstimate = result.isEstimate
                val isRough = result.methodNameEn.contains("ROUGH", ignoreCase = true)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when {
                        isRough -> Color(0xFFFFF3E0)
                        isEstimate -> Color(0xFFFFF8E1)
                        else -> Color(0xFFE8F5E9)
                    },
                    border = BorderStroke(
                        1.dp,
                        when {
                            isRough -> Color(0xFFFF9800)
                            isEstimate -> Color(0xFFFFB300)
                            else -> Color(0xFF4CAF50)
                        }
                    )
                ) {
                    Text(
                        text = when {
                            isRough -> AppStrings.badgeRoughEstimate(lang)
                            isEstimate -> AppStrings.badgeEstimate(lang)
                            else -> AppStrings.badgeExact(lang)
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            isRough -> Color(0xFFBF360C)
                            isEstimate -> Color(0xFFB78103)
                            else -> Color(0xFF1B5E20)
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Prominent Primary Unit (Based on Sindh / Punjab preference)
            val primaryValue = if (region == Region.SINDH) {
                "${LandCalculator.formatNumberAccurate(result.wesa)} ${AppStrings.unitWesa(lang)}"
            } else {
                "${LandCalculator.formatNumberAccurate(result.marla)} ${AppStrings.unitMarla(lang)}"
            }

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFFF1F8F1),
                border = BorderStroke(1.dp, Color(0xFFC7D7C4)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = primaryValue,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = FertileGreenPrimary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "= ${LandCalculator.formatNumber(result.sqFeet)} ${AppStrings.unitSqFt(lang)}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF495546)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Method Used Box
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFF7FAF6),
                border = BorderStroke(1.dp, Color(0xFFE0E7DE)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "${AppStrings.methodUsed(lang)}: ${result.getMethodName(lang)}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E6B38)
                    )
                }
            }

            // Warning / What Would Make It Exact
            val warning = result.getWarning(lang)
            if (!warning.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFFFF8E1),
                    border = BorderStroke(1.dp, Color(0xFFFFD54F)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = Color(0xFFC47D00),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = warning,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF6B4700)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = Color(0xFFE0E7DE))
            Spacer(modifier = Modifier.height(10.dp))

            // Complete 8-Unit Breakdown Table
            Text(
                text = "Area in All Units:",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))

            val unitsList = if (region == Region.SINDH) {
                listOf(
                    Pair(AppStrings.unitWesa(lang), LandCalculator.formatNumberAccurate(result.wesa)),
                    Pair(AppStrings.unitGunta(lang), LandCalculator.formatNumberAccurate(result.gunta)),
                    Pair(AppStrings.unitJareeb(lang), LandCalculator.formatNumberAccurate(result.jareeb)),
                    Pair(AppStrings.unitAcre(lang), LandCalculator.formatNumberAccurate(result.acre)),
                    Pair(AppStrings.unitMarla(lang), LandCalculator.formatNumberAccurate(result.marla)),
                    Pair(AppStrings.unitKanal(lang), LandCalculator.formatNumberAccurate(result.kanal)),
                    Pair(AppStrings.unitSqFt(lang), LandCalculator.formatNumber(result.sqFeet)),
                    Pair(AppStrings.unitSqM(lang), LandCalculator.formatNumberAccurate(result.sqMeters))
                )
            } else {
                listOf(
                    Pair(AppStrings.unitMarla(lang), LandCalculator.formatNumberAccurate(result.marla)),
                    Pair(AppStrings.unitKanal(lang), LandCalculator.formatNumberAccurate(result.kanal)),
                    Pair(AppStrings.unitAcre(lang), LandCalculator.formatNumberAccurate(result.acre)),
                    Pair(AppStrings.unitWesa(lang), LandCalculator.formatNumberAccurate(result.wesa)),
                    Pair(AppStrings.unitGunta(lang), LandCalculator.formatNumberAccurate(result.gunta)),
                    Pair(AppStrings.unitJareeb(lang), LandCalculator.formatNumberAccurate(result.jareeb)),
                    Pair(AppStrings.unitSqFt(lang), LandCalculator.formatNumber(result.sqFeet)),
                    Pair(AppStrings.unitSqM(lang), LandCalculator.formatNumberAccurate(result.sqMeters))
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                unitsList.forEach { (name, valStr) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = name, fontSize = 13.sp, color = Color(0xFF495546))
                        Text(text = valStr, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E211E))
                    }
                }
            }

            // Calculation Steps
            if (result.steps.isNotEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = Color(0xFFE0E7DE))
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Calculation Steps:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = FertileGreenPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                result.steps.forEach { step ->
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        Text(
                            text = step.getTitle(lang),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = FertileGreenPrimary
                        )
                        Text(
                            text = step.getDetail(lang),
                            fontSize = 12.sp,
                            color = Color(0xFF333333)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CombinedPartsTotalCard(
    parts: List<QuickMeasurePart>,
    totalSqFeet: Double,
    lang: Language,
    region: Region
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("quick_measure_combined_total_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E6B38)),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Layers,
                    contentDescription = null,
                    tint = HarvestGold,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = AppStrings.combinedTotalTitle(lang),
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Large Grand Total
            val totalWesa = totalSqFeet / LandCalculator.SQ_FT_PER_WESA
            val totalMarla = totalSqFeet / LandCalculator.SQ_FT_PER_MARLA
            val totalJareeb = totalSqFeet / LandCalculator.SQ_FT_PER_JAREEB
            val totalKanal = totalSqFeet / LandCalculator.SQ_FT_PER_KANAL
            val totalAcre = totalSqFeet / LandCalculator.SQ_FT_PER_ACRE

            val grandPrimary = if (region == Region.SINDH) {
                "${LandCalculator.formatNumberAccurate(totalWesa)} ${AppStrings.unitWesa(lang)}"
            } else {
                "${LandCalculator.formatNumberAccurate(totalMarla)} ${AppStrings.unitMarla(lang)}"
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0x33FFFFFF),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = grandPrimary,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = HarvestGold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "= ${LandCalculator.formatNumber(totalSqFeet)} ${AppStrings.unitSqFt(lang)}",
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Breakdown by parts
            Text(
                text = "Breakdown of Each Piece:",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFBE1A2)
            )
            Spacer(modifier = Modifier.height(6.dp))

            parts.forEachIndexed { idx, p ->
                val partArea = p.calculationResult?.let {
                    if (region == Region.SINDH) {
                        "${LandCalculator.formatNumberAccurate(it.wesa)} wesa"
                    } else {
                        "${LandCalculator.formatNumberAccurate(it.marla)} marla"
                    }
                } ?: "Pending"

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = AppStrings.partTabLabel(idx + 1, lang),
                        fontSize = 13.sp,
                        color = Color.White
                    )
                    Text(
                        text = partArea,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFBE1A2)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color(0x44FFFFFF))
            Spacer(modifier = Modifier.height(10.dp))

            // Other units
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${AppStrings.unitJareeb(lang)}: ${LandCalculator.formatNumberAccurate(totalJareeb)}",
                    fontSize = 12.sp,
                    color = Color.White
                )
                Text(
                    text = "${AppStrings.unitKanal(lang)}: ${LandCalculator.formatNumberAccurate(totalKanal)}",
                    fontSize = 12.sp,
                    color = Color.White
                )
                Text(
                    text = "${AppStrings.unitAcre(lang)}: ${LandCalculator.formatNumberAccurate(totalAcre)}",
                    fontSize = 12.sp,
                    color = Color.White
                )
            }
        }
    }
}
