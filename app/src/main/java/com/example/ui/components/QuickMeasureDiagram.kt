package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculator.LandCalculator
import com.example.localization.AppStrings
import com.example.model.CompassDirection
import com.example.model.Language
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun QuickMeasureDiagram(
    sides: List<Double>,
    focusedIndex: Int?,
    sideDirections: Map<Int, CompassDirection>,
    sideBoundaries: Map<Int, String>,
    hasDiagonal: Boolean,
    diagonalValue: Double?,
    lang: Language,
    isImpossible: Boolean,
    errorMessage: String?,
    onSideClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // Check for duplicate directions warning
    val dirCounts = sideDirections.values.groupingBy { it }.eachCount()
    val duplicateDirs = dirCounts.filter { it.value > 1 }.keys

    Column(modifier = modifier.fillMaxWidth()) {
        // Main Diagram Canvas Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .background(Color(0xFFF3F7F1), RoundedCornerShape(18.dp))
                .border(1.5.dp, if (isImpossible) Color(0xFFBA1A1A) else Color(0xFFC7D7C4), RoundedCornerShape(18.dp))
                .padding(8.dp)
                .testTag("quick_measure_canvas_container")
        ) {
            val boundaryColor = Color(0xFF1E6B38)
            val fillColor = Color(0x221E6B38)
            val markerColor = Color(0xFFC47D00)
            val highlightColor = Color(0xFFE65100)
            val missingColor = Color(0xFF8A9A86)
            val errorColor = Color(0xFFBA1A1A)

            val textPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.rgb(20, 24, 20)
                textSize = 28f
                isAntiAlias = true
                textAlign = android.graphics.Paint.Align.CENTER
                typeface = android.graphics.Typeface.DEFAULT_BOLD
            }

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(224.dp)
                    .testTag("quick_measure_canvas")
            ) {
                val w = size.width
                val h = size.height

                // Draw Compass Rose
                val compassX = if (lang.isRtl) 40f else w - 40f
                val compassY = 36f
                drawQuickCompassRose(compassX, compassY, lang)

                if (sides.isEmpty()) {
                    // Empty placeholder
                    drawEmptyPlotPlaceholder(w, h, lang, missingColor, textPaint)
                    return@Canvas
                }

                when (sides.size) {
                    3 -> drawQuickTriangle(
                        w, h, sides, focusedIndex, sideDirections, sideBoundaries, lang, isImpossible,
                        boundaryColor, fillColor, highlightColor, markerColor, errorColor, textPaint
                    )
                    4 -> drawQuickQuad(
                        w, h, sides, focusedIndex, sideDirections, sideBoundaries, hasDiagonal, diagonalValue,
                        lang, isImpossible, boundaryColor, fillColor, highlightColor, missingColor, markerColor, errorColor, textPaint
                    )
                    else -> drawQuickPolygon(
                        w, h, sides, focusedIndex, sideDirections, sideBoundaries, lang, isImpossible,
                        boundaryColor, fillColor, highlightColor, markerColor, errorColor, textPaint
                    )
                }
            }
        }

        // Duplicate Direction Warning if user accidentally tagged two sides with the same direction
        if (duplicateDirs.isNotEmpty()) {
            Spacer(modifier = Modifier.height(6.dp))
            Surface(
                color = Color(0xFFFFF3E0),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFB74D)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = Color(0xFFE65100),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = AppStrings.duplicateDirectionWarning(duplicateDirs.first(), lang),
                        fontSize = 12.sp,
                        color = Color(0xFF5D2E00)
                    )
                }
            }
        }

        // Interactive Side Summary List beneath the diagram
        if (sides.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("quick_measure_sides_summary_list"),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                sides.forEachIndexed { index, length ->
                    val isFocused = focusedIndex == index
                    val letter = ('A' + index).toString()
                    val dir = sideDirections[index]
                    val boundary = sideBoundaries[index]

                    val label = buildString {
                        if (dir != null) {
                            append("${dir.getName(lang)}: ")
                        } else {
                            append("${AppStrings.sideWord(lang)} $letter: ")
                        }
                        append("${LandCalculator.formatNumber(length)} ${AppStrings.feetUnit(lang)}")
                        if (!boundary.isNullOrBlank()) {
                            append(" ($boundary)")
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isFocused) Color(0xFFFFE0B2) else Color(0xFFE8F5E9),
                        border = androidx.compose.foundation.BorderStroke(
                            1.5.dp,
                            if (isFocused) Color(0xFFE65100) else Color(0xFFC7D7C4)
                        ),
                        modifier = Modifier
                            .clickable { onSideClick(index) }
                            .testTag("quick_summary_side_$index")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = label,
                                fontSize = 13.sp,
                                fontWeight = if (isFocused) FontWeight.Bold else FontWeight.Medium,
                                color = if (isFocused) Color(0xFFBF360C) else Color(0xFF1E6B38)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit",
                                tint = if (isFocused) Color(0xFFE65100) else Color(0xFF2E8B4C),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }

                // If diagonal entered, show it in the summary chip list as well
                if (hasDiagonal && diagonalValue != null && diagonalValue > 0.0) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFFFF8E1),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFFB300)),
                        modifier = Modifier.testTag("quick_summary_diagonal")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${AppStrings.diagonalFieldLabel(lang)}: ${LandCalculator.formatNumber(diagonalValue)} ${AppStrings.feetUnit(lang)}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFB78103)
                            )
                        }
                    }
                }
            }
        }
    }
}

// Draw Compass Rose
private fun DrawScope.drawQuickCompassRose(x: Float, y: Float, lang: Language) {
    val radius = 18f
    drawCircle(Color.White, radius = radius, center = Offset(x, y))
    drawCircle(Color(0xFF1E6B38), radius = radius, center = Offset(x, y), style = Stroke(width = 2.2f))

    val northArrow = Path().apply {
        moveTo(x, y - radius + 3f)
        lineTo(x - 4.5f, y)
        lineTo(x, y - 2f)
        close()
    }
    drawPath(northArrow, Color(0xFFD32F2F))

    val southArrow = Path().apply {
        moveTo(x, y + radius - 3f)
        lineTo(x + 4.5f, y)
        lineTo(x, y + 2f)
        close()
    }
    drawPath(southArrow, Color(0xFF9E9E9E))
    drawCircle(Color(0xFFC47D00), radius = 3f, center = Offset(x, y))

    val northLetter = AppStrings.compassNorth(lang)
    val paint = android.graphics.Paint().apply {
        color = android.graphics.Color.rgb(211, 47, 47)
        textSize = 18f
        isAntiAlias = true
        textAlign = android.graphics.Paint.Align.CENTER
        typeface = android.graphics.Typeface.DEFAULT_BOLD
    }
    drawContext.canvas.nativeCanvas.drawText(northLetter, x, y - radius - 3f, paint)
}

private fun DrawScope.drawEmptyPlotPlaceholder(
    w: Float,
    h: Float,
    lang: Language,
    missingColor: Color,
    paint: android.graphics.Paint
) {
    val dash = PathEffect.dashPathEffect(floatArrayOf(12f, 10f), 0f)
    val cx = w * 0.5f
    val cy = h * 0.5f
    val r = min(w, h) * 0.35f
    drawCircle(missingColor, radius = r, center = Offset(cx, cy), style = Stroke(width = 3f, pathEffect = dash))

    val prompt = AppStrings.quickMeasureListLabel(lang)
    val textPaint = android.graphics.Paint().apply {
        color = android.graphics.Color.rgb(120, 130, 120)
        textSize = 24f
        isAntiAlias = true
        textAlign = android.graphics.Paint.Align.CENTER
    }
    drawContext.canvas.nativeCanvas.drawText(prompt, cx, cy + 8f, textPaint)
}

// 3 Sides Triangle
private fun DrawScope.drawQuickTriangle(
    w: Float,
    h: Float,
    sides: List<Double>,
    focusedIndex: Int?,
    sideDirections: Map<Int, CompassDirection>,
    sideBoundaries: Map<Int, String>,
    lang: Language,
    isImpossible: Boolean,
    boundary: Color,
    fill: Color,
    highlight: Color,
    marker: Color,
    error: Color,
    paint: android.graphics.Paint
) {
    val s1 = sides.getOrElse(0) { 1.0 }
    val s2 = sides.getOrElse(1) { 1.0 }
    val s3 = sides.getOrElse(2) { 1.0 }

    // Coordinates scaled to proportions
    val p1 = Offset(w * 0.5f, h * 0.18f)
    val p2 = Offset(w * 0.85f, h * 0.82f)
    val p3 = Offset(w * 0.15f, h * 0.82f)

    val path = Path().apply {
        moveTo(p1.x, p1.y); lineTo(p2.x, p2.y); lineTo(p3.x, p3.y); close()
    }
    drawPath(path, if (isImpossible) Color(0x22BA1A1A) else fill)

    val maxSide = sides.maxOrNull() ?: 0.0

    // Side 1: p1 to p2
    val s1Color = if (isImpossible && s1 == maxSide) error else if (focusedIndex == 0) highlight else boundary
    drawLine(s1Color, p1, p2, if (focusedIndex == 0) 12f else 6f)

    // Side 2: p2 to p3
    val s2Color = if (isImpossible && s2 == maxSide) error else if (focusedIndex == 1) highlight else boundary
    drawLine(s2Color, p2, p3, if (focusedIndex == 1) 12f else 6f)

    // Side 3: p3 to p1
    val s3Color = if (isImpossible && s3 == maxSide) error else if (focusedIndex == 2) highlight else boundary
    drawLine(s3Color, p3, p1, if (focusedIndex == 2) 12f else 6f)

    drawSurveyMarker(p1, focusedIndex == 0 || focusedIndex == 2, marker)
    drawSurveyMarker(p2, focusedIndex == 0 || focusedIndex == 1, marker)
    drawSurveyMarker(p3, focusedIndex == 1 || focusedIndex == 2, marker)

    // Labels
    drawSideLabel(formatSideLabel(0, s1, sideDirections, sideBoundaries, lang), (p1.x + p2.x) / 2f + 25f, (p1.y + p2.y) / 2f, focusedIndex == 0, isImpossible && s1 == maxSide, paint)
    drawSideLabel(formatSideLabel(1, s2, sideDirections, sideBoundaries, lang), (p2.x + p3.x) / 2f, p2.y + 24f, focusedIndex == 1, isImpossible && s2 == maxSide, paint)
    drawSideLabel(formatSideLabel(2, s3, sideDirections, sideBoundaries, lang), (p3.x + p1.x) / 2f - 25f, (p3.y + p1.y) / 2f, focusedIndex == 2, isImpossible && s3 == maxSide, paint)
}

// 4 Sides Quadrilateral
private fun DrawScope.drawQuickQuad(
    w: Float,
    h: Float,
    sides: List<Double>,
    focusedIndex: Int?,
    sideDirections: Map<Int, CompassDirection>,
    sideBoundaries: Map<Int, String>,
    hasDiagonal: Boolean,
    diagonalValue: Double?,
    lang: Language,
    isImpossible: Boolean,
    boundary: Color,
    fill: Color,
    highlight: Color,
    missing: Color,
    marker: Color,
    error: Color,
    paint: android.graphics.Paint
) {
    val s1 = sides.getOrElse(0) { 1.0 } // Left (A)
    val s2 = sides.getOrElse(1) { 1.0 } // Top (B)
    val s3 = sides.getOrElse(2) { 1.0 } // Right (C)
    val s4 = sides.getOrElse(3) { 1.0 } // Bottom (D)

    // Scale aspect ratio based on (s2 + s4) vs (s1 + s3)
    val avgWidth = (s2 + s4) / 2.0
    val avgHeight = (s1 + s3) / 2.0
    val ratio = (avgWidth / avgHeight.coerceAtLeast(1.0)).coerceIn(0.5, 2.0)

    val spanX = if (ratio >= 1.0) w * 0.76f else w * 0.76f * (ratio.toFloat())
    val spanY = if (ratio < 1.0) h * 0.72f else h * 0.72f / (ratio.toFloat())

    val cx = w * 0.5f
    val cy = h * 0.52f

    val tl = Offset(cx - spanX * 0.45f, cy - spanY * 0.45f)
    val tr = Offset(cx + spanX * 0.45f, cy - spanY * 0.45f)
    val br = Offset(cx + spanX * 0.45f, cy + spanY * 0.45f)
    val bl = Offset(cx - spanX * 0.45f, cy + spanY * 0.45f)

    val path = Path().apply {
        moveTo(tl.x, tl.y); lineTo(tr.x, tr.y); lineTo(br.x, br.y); lineTo(bl.x, bl.y); close()
    }
    drawPath(path, if (isImpossible) Color(0x22BA1A1A) else fill)

    val maxSide = sides.maxOrNull() ?: 0.0

    // Side 1 (A): Left (bl to tl)
    val s1Color = if (isImpossible && s1 == maxSide) error else if (focusedIndex == 0) highlight else boundary
    drawLine(s1Color, bl, tl, if (focusedIndex == 0) 12f else 6f)

    // Side 2 (B): Top (tl to tr)
    val s2Color = if (isImpossible && s2 == maxSide) error else if (focusedIndex == 1) highlight else boundary
    drawLine(s2Color, tl, tr, if (focusedIndex == 1) 12f else 6f)

    // Side 3 (C): Right (tr to br)
    val s3Color = if (isImpossible && s3 == maxSide) error else if (focusedIndex == 2) highlight else boundary
    drawLine(s3Color, tr, br, if (focusedIndex == 2) 12f else 6f)

    // Side 4 (D): Bottom (br to bl)
    val s4Color = if (isImpossible && s4 == maxSide) error else if (focusedIndex == 3) highlight else boundary
    drawLine(s4Color, br, bl, if (focusedIndex == 3) 12f else 6f)

    // Diagonal
    val dash = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
    if (hasDiagonal && diagonalValue != null && diagonalValue > 0.0) {
        drawLine(Color(0xFFC47D00), tl, br, strokeWidth = 5f, pathEffect = dash)
        drawSideLabel("${AppStrings.diagonalFieldLabel(lang)}: ${LandCalculator.formatNumber(diagonalValue)} ft", (tl.x + br.x) / 2f + 32f, (tl.y + br.y) / 2f, false, false, paint)
    } else {
        // Suggested diagonal if no diagonal entered
        drawLine(Color(0xFFE65100), tl, br, strokeWidth = 3f, pathEffect = dash)
        drawSideLabel(AppStrings.pleaseMeasureDiagonal(lang), (tl.x + br.x) / 2f, (tl.y + br.y) / 2f, false, false, paint)
    }

    drawSurveyMarker(tl, focusedIndex == 0 || focusedIndex == 1, marker)
    drawSurveyMarker(tr, focusedIndex == 1 || focusedIndex == 2, marker)
    drawSurveyMarker(br, focusedIndex == 2 || focusedIndex == 3, marker)
    drawSurveyMarker(bl, focusedIndex == 3 || focusedIndex == 0, marker)

    // Labels positioned on exact sides
    drawSideLabel(formatSideLabel(0, s1, sideDirections, sideBoundaries, lang), tl.x - 30f, (tl.y + bl.y) / 2f, focusedIndex == 0, isImpossible && s1 == maxSide, paint)
    drawSideLabel(formatSideLabel(1, s2, sideDirections, sideBoundaries, lang), (tl.x + tr.x) / 2f, tl.y - 12f, focusedIndex == 1, isImpossible && s2 == maxSide, paint)
    drawSideLabel(formatSideLabel(2, s3, sideDirections, sideBoundaries, lang), tr.x + 30f, (tr.y + br.y) / 2f, focusedIndex == 2, isImpossible && s3 == maxSide, paint)
    drawSideLabel(formatSideLabel(3, s4, sideDirections, sideBoundaries, lang), (bl.x + br.x) / 2f, br.y + 24f, focusedIndex == 3, isImpossible && s4 == maxSide, paint)
}

// 5+ Sides Polygon
private fun DrawScope.drawQuickPolygon(
    w: Float,
    h: Float,
    sides: List<Double>,
    focusedIndex: Int?,
    sideDirections: Map<Int, CompassDirection>,
    sideBoundaries: Map<Int, String>,
    lang: Language,
    isImpossible: Boolean,
    boundary: Color,
    fill: Color,
    highlight: Color,
    marker: Color,
    error: Color,
    paint: android.graphics.Paint
) {
    val count = sides.size
    val cx = w * 0.5f
    val cy = h * 0.52f
    val radius = min(w, h) * 0.36f

    // Generate vertices around polygon
    val points = mutableListOf<Offset>()
    val angleStep = (2.0 * PI) / count
    val startAngle = -PI / 2.0 // start top
    for (i in 0 until count) {
        val angle = startAngle + i * angleStep
        val px = (cx + radius * cos(angle)).toFloat()
        val py = (cy + radius * sin(angle)).toFloat()
        points.add(Offset(px, py))
    }

    val path = Path().apply {
        if (points.isNotEmpty()) {
            moveTo(points[0].x, points[0].y)
            for (i in 1 until points.size) {
                lineTo(points[i].x, points[i].y)
            }
            close()
        }
    }
    drawPath(path, if (isImpossible) Color(0x22BA1A1A) else fill)

    val maxSide = sides.maxOrNull() ?: 0.0

    // Draw lines and labels
    for (i in 0 until count) {
        val pStart = points[i]
        val pEnd = points[(i + 1) % count]
        val isFocused = focusedIndex == i
        val sideLength = sides[i]
        val isLongest = isImpossible && sideLength == maxSide

        val color = if (isLongest) error else if (isFocused) highlight else boundary
        drawLine(color, pStart, pEnd, if (isFocused) 12f else 6f)

        drawSurveyMarker(pStart, isFocused, marker)

        val midX = (pStart.x + pEnd.x) / 2f
        val midY = (pStart.y + pEnd.y) / 2f
        val offsetDirX = (midX - cx) * 0.22f
        val offsetDirY = (midY - cy) * 0.22f

        drawSideLabel(
            formatSideLabel(i, sideLength, sideDirections, sideBoundaries, lang),
            midX + offsetDirX,
            midY + offsetDirY,
            isFocused,
            isLongest,
            paint
        )
    }
}

// Marker pin
private fun DrawScope.drawSurveyMarker(pos: Offset, isFocused: Boolean, markerColor: Color) {
    drawCircle(if (isFocused) Color(0xFFE65100) else markerColor, radius = if (isFocused) 7f else 5f, center = pos)
    drawCircle(Color.White, radius = if (isFocused) 3f else 2f, center = pos)
}

// Side label pill
private fun DrawScope.drawSideLabel(
    text: String,
    x: Float,
    y: Float,
    isFocused: Boolean,
    isError: Boolean,
    paint: android.graphics.Paint
) {
    val textWidth = paint.measureText(text)
    val pillW = textWidth + 24f
    val pillH = 34f
    val left = x - pillW / 2f
    val top = y - pillH / 2f

    val pillColor = when {
        isError -> Color(0xFFBA1A1A)
        isFocused -> Color(0xFFE65100)
        else -> Color(0xEEFFFFFF)
    }
    val strokeColor = when {
        isError -> Color(0xFF7F0000)
        isFocused -> Color(0xFFBF360C)
        else -> Color(0xFF2E8B4C)
    }

    drawRoundRect(
        color = pillColor,
        topLeft = Offset(left, top),
        size = androidx.compose.ui.geometry.Size(pillW, pillH),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(12f, 12f)
    )
    drawRoundRect(
        color = strokeColor,
        topLeft = Offset(left, top),
        size = androidx.compose.ui.geometry.Size(pillW, pillH),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(12f, 12f),
        style = Stroke(width = if (isFocused) 2.5f else 1.2f)
    )

    paint.color = if (isFocused || isError) android.graphics.Color.WHITE else android.graphics.Color.rgb(20, 24, 20)
    paint.textSize = 21f
    drawContext.canvas.nativeCanvas.drawText(text, x, y + 7f, paint)
}

// Format side string with direction and boundary
private fun formatSideLabel(
    index: Int,
    length: Double,
    sideDirections: Map<Int, CompassDirection>,
    sideBoundaries: Map<Int, String>,
    lang: Language
): String {
    val letter = ('A' + index).toString()
    val dir = sideDirections[index]
    val sideTitle = if (dir != null) {
        "${dir.getName(lang)} ${AppStrings.sideWord(lang)}"
    } else {
        AppStrings.sideLabel(letter, lang)
    }

    val unit = when (lang) {
        Language.ENGLISH -> "ft"
        Language.URDU -> "فٹ"
        Language.SINDHI -> "فوٽ"
    }

    val base = "$sideTitle: ${LandCalculator.formatNumber(length)} $unit"
    val boundary = sideBoundaries[index]
    return if (!boundary.isNullOrBlank()) {
        "$base ($boundary)"
    } else base
}
