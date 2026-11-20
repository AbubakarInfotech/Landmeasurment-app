package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.AppStrings
import com.example.model.Language
import com.example.model.PlotShape
import com.example.ui.components.ShapeDiagram
import com.example.ui.theme.FertileGreenPrimary
import com.example.ui.theme.HarvestGold

import androidx.compose.material.icons.filled.FlashOn

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShapePickerScreen(
    currentLanguage: Language,
    onShapeSelected: (PlotShape) -> Unit,
    onOpenGuide: () -> Unit,
    onOpenGpsWalking: () -> Unit,
    onOpenQuickMeasure: () -> Unit,
    onBackToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = AppStrings.selectShapeHeader(currentLanguage),
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackToSettings,
                        modifier = Modifier.testTag("shape_picker_back_btn")
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = onBackToSettings,
                        modifier = Modifier.testTag("shape_picker_settings_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings")
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
        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // "Quick Measure (No Shape Needed)" Banner
            item(span = { GridItemSpan(maxLineSpan) }) {
                Button(
                    onClick = onOpenQuickMeasure,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp)
                        .testTag("shape_picker_quick_measure_banner_btn"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FertileGreenPrimary),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FlashOn,
                        contentDescription = null,
                        tint = HarvestGold,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = AppStrings.quickMeasureModeBtn(currentLanguage),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // "Measure by Walking (GPS)" Banner
            item(span = { GridItemSpan(maxLineSpan) }) {
                Button(
                    onClick = onOpenGpsWalking,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp)
                        .testTag("measure_by_walking_banner_btn"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FertileGreenPrimary),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = AppStrings.measureByWalking(currentLanguage),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // "I Don't Know the Shape" Help Banner
            item(span = { GridItemSpan(maxLineSpan) }) {
                Button(
                    onClick = onOpenGuide,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("dont_know_shape_banner_btn"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = HarvestGold),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.HelpOutline,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = AppStrings.dontKnowShapeButton(currentLanguage),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Subtitle prompt
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    text = AppStrings.selectShapePrompt(currentLanguage),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }

            // Cards for all shapes
            items(PlotShape.values()) { shape ->
                ShapeCard(
                    shape = shape,
                    currentLanguage = currentLanguage,
                    onClick = { onShapeSelected(shape) }
                )
            }

            // Extra padding at bottom
            item(span = { GridItemSpan(maxLineSpan) }) {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun ShapeCard(
    shape: PlotShape,
    currentLanguage: Language,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("shape_card_${shape.name.lowercase()}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, Color(0xFFD6DDD5))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Visual Diagram miniature
            ShapeDiagram(
                shape = shape,
                lang = currentLanguage,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Shape Names in All 3 Languages under each picture (PRD requirement)
            Column(modifier = Modifier.fillMaxWidth()) {
                // Primary language name (highlighted)
                Text(
                    text = shape.getName(currentLanguage),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = FertileGreenPrimary
                )

                Spacer(modifier = Modifier.height(4.dp))

                // The other 2 languages
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (currentLanguage != Language.URDU) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFF1F6ED),
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            Text(
                                text = "اردو: ${shape.urduName}",
                                fontSize = 13.sp,
                                color = Color(0xFF2E4E35),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    if (currentLanguage != Language.SINDHI) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFFBF4E6),
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            Text(
                                text = "سنڌي: ${shape.sindhiName}",
                                fontSize = 13.sp,
                                color = Color(0xFF6B4700),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    if (currentLanguage != Language.ENGLISH) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFEEF2F6),
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            Text(
                                text = "EN: ${shape.englishName}",
                                fontSize = 12.sp,
                                color = Color(0xFF334A60),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = shape.getDescription(currentLanguage),
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
