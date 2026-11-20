package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.localization.AppStrings
import com.example.model.Language
import com.example.model.PlotShape
import com.example.ui.theme.FertileGreenPrimary
import com.example.ui.theme.HarvestGold
import com.example.ui.theme.WarningAmber

@Composable
fun ShapeGuideDialog(
    lang: Language,
    onDismiss: () -> Unit,
    onShapeChosen: (PlotShape) -> Unit
) {
    var step by remember { mutableIntStateOf(1) }
    var recommendedShape by remember { mutableStateOf<PlotShape?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFFE8F5E9), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.HelpOutline,
                                contentDescription = null,
                                tint = FertileGreenPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = AppStrings.wizardTitle(lang),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (recommendedShape == null) {
                    when (step) {
                        1 -> {
                            // Question 1: Number of sides
                            Text(
                                text = AppStrings.wizardQ1(lang),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 17.sp
                                ),
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            GuideOptionButton(
                                title = AppStrings.fourSides(lang),
                                subtitle = "Rectangle, Trapezium, or irregular 4-sided",
                                onClick = { step = 2 },
                                testTag = "guide_q1_4sides"
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            GuideOptionButton(
                                title = AppStrings.threeSides(lang),
                                subtitle = "Heron's formula (a, b, c)",
                                onClick = { recommendedShape = PlotShape.TRIANGLE },
                                testTag = "guide_q1_3sides"
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            GuideOptionButton(
                                title = AppStrings.fiveOrMoreSides(lang),
                                subtitle = "Polygon split into triangles",
                                onClick = { recommendedShape = PlotShape.MULTI_SIDE_POLYGON },
                                testTag = "guide_q1_5sides"
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            GuideOptionButton(
                                title = AppStrings.curvedSide(lang),
                                subtitle = "Baseline with offset measurements",
                                onClick = { recommendedShape = PlotShape.CURVED_SIDE },
                                testTag = "guide_q1_curved"
                            )
                        }
                        2 -> {
                            // Question 2: Are 2 opposite sides parallel?
                            Text(
                                text = AppStrings.wizardQ2(lang),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 17.sp
                                ),
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            GuideOptionButton(
                                title = AppStrings.yes(lang),
                                subtitle = "Sides run equally apart (Trapezium or Rectangle)",
                                onClick = { recommendedShape = PlotShape.TRAPEZIUM },
                                testTag = "guide_q2_yes"
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            GuideOptionButton(
                                title = AppStrings.no(lang),
                                subtitle = "All 4 sides are at irregular angles",
                                onClick = { step = 3 },
                                testTag = "guide_q2_no"
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedButton(
                                onClick = { step = 1 },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(AppStrings.back(lang))
                            }
                        }
                        3 -> {
                            // Question 3: Can a diagonal be measured?
                            Text(
                                text = AppStrings.wizardQ3(lang),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 17.sp
                                ),
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            GuideOptionButton(
                                title = AppStrings.yes(lang) + " - " + AppStrings.unitAcre(lang) + " (Accurate)",
                                subtitle = "Measure corner-to-corner across the plot (Accurate & dispute-free)",
                                onClick = { recommendedShape = PlotShape.FOUR_SIDE_DIAGONAL },
                                testTag = "guide_q3_yes"
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            GuideOptionButton(
                                title = AppStrings.no(lang) + " (Estimate Only)",
                                subtitle = "Only 4 outer boundary sides known (Approximation)",
                                onClick = { recommendedShape = PlotShape.FOUR_SIDE_ESTIMATE },
                                testTag = "guide_q3_no"
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedButton(
                                onClick = { step = 2 },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(AppStrings.back(lang))
                            }
                        }
                    }
                } else {
                    // Recommendation Result
                    val shape = recommendedShape!!

                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = FertileGreenPrimary,
                        modifier = Modifier.size(54.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = AppStrings.wizardRecommendation(lang),
                        style = MaterialTheme.typography.labelLarge,
                        color = HarvestGold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = shape.getName(lang),
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        ),
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = shape.getDescription(lang),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    if (shape == PlotShape.FOUR_SIDE_ESTIMATE) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3CD)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = WarningAmber,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = AppStrings.estimateWarningText(lang),
                                    fontSize = 12.sp,
                                    color = Color(0xFF664D03)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            onShapeChosen(shape)
                            onDismiss()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("guide_accept_shape_btn"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FertileGreenPrimary)
                    ) {
                        Text(
                            text = AppStrings.continueWithMethod(lang),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = {
                            recommendedShape = null
                            step = 1
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(AppStrings.clearFields(lang))
                    }
                }
            }
        }
    }
}

@Composable
private fun GuideOptionButton(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    testTag: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag(testTag),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF6FAF4)),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD6E3D4))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 16.dp)
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFF1E3A24)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = Color(0xFF5D6B5E)
            )
        }
    }
}
