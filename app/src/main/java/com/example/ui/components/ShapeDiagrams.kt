package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import com.example.localization.AppStrings
import com.example.model.CompassDirection
import com.example.model.Language
import com.example.model.PlotShape
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

enum class DiagramPosition {
    TOP, BOTTOM, LEFT, RIGHT
}

@Composable
fun ShapeDiagram(
    shape: PlotShape,
    lang: Language,
    inputs: Map<String, String> = emptyMap(),
    focusedKey: String? = null,
    sideDirections: Map<String, CompassDirection> = emptyMap(),
    sideBoundaries: Map<String, String> = emptyMap(),
    curvedOffsets: List<String> = emptyList(),
    isImpossibleTriangle: Boolean = false,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(230.dp)
            .background(Color(0xFFF3F7F1), RoundedCornerShape(18.dp))
            .border(1.5.dp, Color(0xFFC7D7C4), RoundedCornerShape(18.dp))
            .padding(8.dp)
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

        Canvas(modifier = Modifier.fillMaxWidth().height(214.dp)) {
            val w = size.width
            val h = size.height

            // Draw Compass Rose / North Arrow at top right (or top left for RTL)
            val compassX = if (lang.isRtl) 40f else w - 40f
            val compassY = 36f
            drawCompassRose(compassX, compassY, lang)

            when (shape) {
                PlotShape.RECTANGLE -> {
                    drawLiveRectangle(
                        w, h, inputs, focusedKey, sideDirections, sideBoundaries, lang,
                        boundaryColor, fillColor, highlightColor, missingColor, markerColor, textPaint
                    )
                }
                PlotShape.TRIANGLE -> {
                    drawLiveTriangle(
                        w, h, inputs, focusedKey, sideDirections, sideBoundaries, lang, isImpossibleTriangle,
                        boundaryColor, fillColor, highlightColor, missingColor, markerColor, errorColor, textPaint
                    )
                }
                PlotShape.TRAPEZIUM -> {
                    drawLiveTrapezium(
                        w, h, inputs, focusedKey, sideDirections, sideBoundaries, lang,
                        boundaryColor, fillColor, highlightColor, missingColor, markerColor, textPaint
                    )
                }
                PlotShape.FOUR_SIDE_DIAGONAL -> {
                    drawLiveFourSideDiagonal(
                        w, h, inputs, focusedKey, sideDirections, sideBoundaries, lang,
                        boundaryColor, fillColor, highlightColor, missingColor, markerColor, textPaint
                    )
                }
                PlotShape.FOUR_SIDE_ESTIMATE -> {
                    drawLiveFourSideEstimate(
                        w, h, inputs, focusedKey, sideDirections, sideBoundaries, lang,
                        boundaryColor, fillColor, highlightColor, missingColor, markerColor, textPaint
                    )
                }
                PlotShape.MULTI_SIDE_POLYGON -> {
                    drawLivePolygon(
                        w, h, inputs, focusedKey, sideDirections, sideBoundaries, lang,
                        boundaryColor, fillColor, highlightColor, missingColor, markerColor, textPaint
                    )
                }
                PlotShape.CURVED_SIDE -> {
                    drawLiveCurved(
                        w, h, inputs, curvedOffsets, focusedKey, lang,
                        boundaryColor, fillColor, highlightColor, missingColor, markerColor, textPaint
                    )
                }
            }
        }
    }
}

// Compass Rose dial drawing
private fun DrawScope.drawCompassRose(x: Float, y: Float, lang: Language) {
    val radius = 20f
    drawCircle(Color.White, radius = radius, center = Offset(x, y))
    drawCircle(Color(0xFF1E6B38), radius = radius, center = Offset(x, y), style = Stroke(width = 2.5f))

    // North arrow tip (red)
    val northArrow = Path().apply {
        moveTo(x, y - radius + 4f)
        lineTo(x - 5f, y)
        lineTo(x, y - 2f)
        close()
    }
    drawPath(northArrow, Color(0xFFD32F2F))

    // South arrow tip (grey)
    val southArrow = Path().apply {
        moveTo(x, y + radius - 4f)
        lineTo(x + 5f, y)
        lineTo(x, y + 2f)
        close()
    }
    drawPath(southArrow, Color(0xFF9E9E9E))

    drawCircle(Color(0xFFC47D00), radius = 3.5f, center = Offset(x, y))

    // Compass North initial in active script
    val northLetter = AppStrings.compassNorth(lang)
    val paint = android.graphics.Paint().apply {
        color = android.graphics.Color.rgb(211, 47, 47)
        textSize = 19f
        isAntiAlias = true
        textAlign = android.graphics.Paint.Align.CENTER
        typeface = android.graphics.Typeface.DEFAULT_BOLD
    }
    drawContext.canvas.nativeCanvas.drawText(northLetter, x, y - radius - 4f, paint)
}

// Format side label with direction and boundary
fun formatSideWithDirection(
    key: String,
    defaultLetter: String,
    inputs: Map<String, String>,
    sideDirections: Map<String, CompassDirection>,
    sideBoundaries: Map<String, String>,
    lang: Language
): String {
    val dir = sideDirections[key]
    val sideTitle = if (dir != null) {
        "${dir.getName(lang)} ${AppStrings.sideWord(lang)}"
    } else {
        AppStrings.sideLabel(defaultLetter, lang)
    }

    val valueStr = inputs[key]
    val unitSuffix = when (lang) {
        Language.ENGLISH -> "ft"
        Language.URDU -> "فٹ"
        Language.SINDHI -> "فوٽ"
    }
    val questionMark = when (lang) {
        Language.ENGLISH -> "?"
        Language.URDU -> "؟"
        Language.SINDHI -> "؟"
    }

    val mainText = if (!valueStr.isNullOrBlank()) {
        "$sideTitle: $valueStr $unitSuffix"
    } else {
        "$sideTitle: $questionMark"
    }

    val boundary = sideBoundaries[key]
    return if (!boundary.isNullOrBlank()) {
        "$mainText [$boundary]"
    } else {
        mainText
    }
}

// Draw label with pill background
private fun DrawScope.drawLabelPill(
    text: String,
    x: Float,
    y: Float,
    isFocused: Boolean,
    isError: Boolean,
    paint: android.graphics.Paint
) {
    val bounds = android.graphics.Rect()
    paint.getTextBounds(text, 0, text.length, bounds)
    val padH = 12f
    val padV = 6f
    val rect = android.graphics.RectF(
        x - bounds.width() / 2f - padH,
        y - bounds.height() - padV,
        x + bounds.width() / 2f + padH,
        y + padV
    )

    val bgPaint = android.graphics.Paint().apply {
        isAntiAlias = true
        color = when {
            isError -> android.graphics.Color.argb(240, 186, 26, 26)
            isFocused -> android.graphics.Color.argb(245, 230, 81, 0)
            else -> android.graphics.Color.argb(225, 255, 255, 255)
        }
    }

    val strokePaint = android.graphics.Paint().apply {
        isAntiAlias = true
        style = android.graphics.Paint.Style.STROKE
        strokeWidth = 2f
        color = when {
            isError -> android.graphics.Color.rgb(186, 26, 26)
            isFocused -> android.graphics.Color.rgb(255, 255, 255)
            else -> android.graphics.Color.rgb(190, 205, 190)
        }
    }

    drawContext.canvas.nativeCanvas.drawRoundRect(rect, 10f, 10f, bgPaint)
    drawContext.canvas.nativeCanvas.drawRoundRect(rect, 10f, 10f, strokePaint)

    paint.color = when {
        isError || isFocused -> android.graphics.Color.WHITE
        else -> android.graphics.Color.rgb(20, 30, 20)
    }

    drawContext.canvas.nativeCanvas.drawText(text, x, y, paint)
}

private fun DrawScope.drawSurveyMarker(point: Offset, isFocused: Boolean, markerColor: Color) {
    if (isFocused) {
        drawCircle(Color(0xFFFFB74D), radius = 12f, center = point)
        drawCircle(Color(0xFFE65100), radius = 8f, center = point)
        drawCircle(Color.White, radius = 3.5f, center = point)
    } else {
        drawCircle(Color.White, radius = 8f, center = point)
        drawCircle(markerColor, radius = 5.5f, center = point)
        drawCircle(Color.White, radius = 2f, center = point)
    }
}

// Map a side key to the physical position (TOP, BOTTOM, LEFT, RIGHT)
// "the side marked East sits on the right, West on the left, North at the top, South at the bottom"
fun resolveKeyForPosition(
    position: DiagramPosition,
    sideDirections: Map<String, CompassDirection>,
    defaultKey: String
): String {
    val matchingKey = sideDirections.entries.firstOrNull { entry ->
        when (position) {
            DiagramPosition.TOP -> entry.value == CompassDirection.NORTH || entry.value == CompassDirection.NORTH_WEST || entry.value == CompassDirection.NORTH_EAST
            DiagramPosition.BOTTOM -> entry.value == CompassDirection.SOUTH || entry.value == CompassDirection.SOUTH_WEST || entry.value == CompassDirection.SOUTH_EAST
            DiagramPosition.RIGHT -> entry.value == CompassDirection.EAST
            DiagramPosition.LEFT -> entry.value == CompassDirection.WEST
        }
    }?.key

    return matchingKey ?: defaultKey
}

// 1. LIVE RECTANGLE
private fun DrawScope.drawLiveRectangle(
    w: Float,
    h: Float,
    inputs: Map<String, String>,
    focusedKey: String?,
    sideDirections: Map<String, CompassDirection>,
    sideBoundaries: Map<String, String>,
    lang: Language,
    boundary: Color,
    fill: Color,
    highlight: Color,
    missing: Color,
    marker: Color,
    paint: android.graphics.Paint
) {
    val topKey = resolveKeyForPosition(DiagramPosition.TOP, sideDirections, "length")
    val rightKey = resolveKeyForPosition(DiagramPosition.RIGHT, sideDirections, "width")
    val bottomKey = resolveKeyForPosition(DiagramPosition.BOTTOM, sideDirections, "length")
    val leftKey = resolveKeyForPosition(DiagramPosition.LEFT, sideDirections, "width")

    val lengthVal = inputs["length"]?.toDoubleOrNull()
    val widthVal = inputs["width"]?.toDoubleOrNull()

    val ratio = if (lengthVal != null && widthVal != null && lengthVal > 0 && widthVal > 0) {
        (lengthVal / widthVal).coerceIn(0.4, 2.5).toFloat()
    } else 1.25f

    val baseW = w * 0.65f
    val baseH = h * 0.55f
    val rw = if (ratio >= 1f) baseW else baseW * ratio
    val rh = if (ratio >= 1f) baseH / ratio else baseH

    val left = (w - rw) / 2f
    val right = left + rw
    val top = (h - rh) / 2f
    val bottom = top + rh

    val isTopFocused = focusedKey == topKey
    val isRightFocused = focusedKey == rightKey
    val isBottomFocused = focusedKey == bottomKey
    val isLeftFocused = focusedKey == leftKey

    val hasTop = !inputs[topKey].isNullOrBlank()
    val hasRight = !inputs[rightKey].isNullOrBlank()
    val hasBottom = !inputs[bottomKey].isNullOrBlank()
    val hasLeft = !inputs[leftKey].isNullOrBlank()

    val path = Path().apply {
        moveTo(left, top); lineTo(right, top); lineTo(right, bottom); lineTo(left, bottom); close()
    }
    drawPath(path, fill)

    val dash = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)

    // Top Side
    drawLine(
        color = if (isTopFocused) highlight else if (hasTop) boundary else missing,
        start = Offset(left, top), end = Offset(right, top),
        strokeWidth = if (isTopFocused) 12f else 6f,
        pathEffect = if (!hasTop && !isTopFocused) dash else null
    )

    // Bottom Side
    drawLine(
        color = if (isBottomFocused) highlight else if (hasBottom) boundary else missing,
        start = Offset(left, bottom), end = Offset(right, bottom),
        strokeWidth = if (isBottomFocused) 12f else 6f,
        pathEffect = if (!hasBottom && !isBottomFocused) dash else null
    )

    // Left Side
    drawLine(
        color = if (isLeftFocused) highlight else if (hasLeft) boundary else missing,
        start = Offset(left, top), end = Offset(left, bottom),
        strokeWidth = if (isLeftFocused) 12f else 6f,
        pathEffect = if (!hasLeft && !isLeftFocused) dash else null
    )

    // Right Side
    drawLine(
        color = if (isRightFocused) highlight else if (hasRight) boundary else missing,
        start = Offset(right, top), end = Offset(right, bottom),
        strokeWidth = if (isRightFocused) 12f else 6f,
        pathEffect = if (!hasRight && !isRightFocused) dash else null
    )

    drawSurveyMarker(Offset(left, top), isTopFocused || isLeftFocused, marker)
    drawSurveyMarker(Offset(right, top), isTopFocused || isRightFocused, marker)
    drawSurveyMarker(Offset(right, bottom), isBottomFocused || isRightFocused, marker)
    drawSurveyMarker(Offset(left, bottom), isBottomFocused || isLeftFocused, marker)

    // Labels
    drawLabelPill(
        formatSideWithDirection(topKey, "A", inputs, sideDirections, sideBoundaries, lang),
        (left + right) / 2f, top - 12f, isTopFocused, false, paint
    )

    drawLabelPill(
        formatSideWithDirection(rightKey, "B", inputs, sideDirections, sideBoundaries, lang),
        right + 30f, (top + bottom) / 2f + 8f, isRightFocused, false, paint
    )

    drawLabelPill(
        formatSideWithDirection(bottomKey, "A", inputs, sideDirections, sideBoundaries, lang),
        (left + right) / 2f, bottom + 24f, isBottomFocused, false, paint
    )

    drawLabelPill(
        formatSideWithDirection(leftKey, "B", inputs, sideDirections, sideBoundaries, lang),
        left - 30f, (top + bottom) / 2f + 8f, isLeftFocused, false, paint
    )
}

// 2. LIVE TRIANGLE
private fun DrawScope.drawLiveTriangle(
    w: Float,
    h: Float,
    inputs: Map<String, String>,
    focusedKey: String?,
    sideDirections: Map<String, CompassDirection>,
    sideBoundaries: Map<String, String>,
    lang: Language,
    isImpossibleParam: Boolean,
    boundary: Color,
    fill: Color,
    highlight: Color,
    missing: Color,
    marker: Color,
    errorColor: Color,
    paint: android.graphics.Paint
) {
    val a = inputs["side1"]?.toDoubleOrNull()
    val b = inputs["side2"]?.toDoubleOrNull()
    val c = inputs["side3"]?.toDoubleOrNull()

    val isImpossible = isImpossibleParam || (a != null && b != null && c != null && (a + b <= c || a + c <= b || b + c <= a))

    var p1 = Offset(w * 0.5f, h * 0.18f)
    var p2 = Offset(w * 0.18f, h * 0.82f)
    var p3 = Offset(w * 0.82f, h * 0.82f)

    if (a != null && b != null && c != null && !isImpossible && a > 0 && b > 0 && c > 0) {
        val cosB = ((a * a + b * b - c * c) / (2.0 * a * b)).coerceIn(-1.0, 1.0)
        val angleB = acos(cosB)
        val rawX = (b * cos(angleB)).toFloat()
        val rawY = (b * sin(angleB)).toFloat()

        val maxX = max(a.toFloat(), max(0f, rawX))
        val minX = min(0f, min(a.toFloat(), rawX))
        val spanX = max(1f, maxX - minX)
        val spanY = max(1f, rawY)

        val scale = min((w * 0.65f) / spanX, (h * 0.55f) / spanY)
        val offsetX = (w - spanX * scale) / 2f - minX * scale
        val offsetY = (h - spanY * scale) / 2f + spanY * scale

        p2 = Offset(offsetX, offsetY)
        p3 = Offset(offsetX + a.toFloat() * scale, offsetY)
        p1 = Offset(offsetX + rawX * scale, offsetY - rawY * scale)
    }

    val dash = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)

    val s1Focused = focusedKey == "side1"
    val s2Focused = focusedKey == "side2"
    val s3Focused = focusedKey == "side3"

    val hasS1 = !inputs["side1"].isNullOrBlank()
    val hasS2 = !inputs["side2"].isNullOrBlank()
    val hasS3 = !inputs["side3"].isNullOrBlank()

    val path = Path().apply {
        moveTo(p1.x, p1.y); lineTo(p3.x, p3.y); lineTo(p2.x, p2.y); close()
    }
    drawPath(path, if (isImpossible) Color(0x33BA1A1A) else fill)

    drawLine(
        color = if (isImpossible) errorColor else if (s1Focused) highlight else if (hasS1) boundary else missing,
        start = p2, end = p3, strokeWidth = if (s1Focused) 12f else 6f,
        pathEffect = if (!hasS1 && !s1Focused && !isImpossible) dash else null
    )

    drawLine(
        color = if (isImpossible) errorColor else if (s2Focused) highlight else if (hasS2) boundary else missing,
        start = p1, end = p2, strokeWidth = if (s2Focused) 12f else 6f,
        pathEffect = if (!hasS2 && !s2Focused && !isImpossible) dash else null
    )

    drawLine(
        color = if (isImpossible) errorColor else if (s3Focused) highlight else if (hasS3) boundary else missing,
        start = p1, end = p3, strokeWidth = if (s3Focused) 12f else 6f,
        pathEffect = if (!hasS3 && !s3Focused && !isImpossible) dash else null
    )

    drawSurveyMarker(p1, s2Focused || s3Focused, if (isImpossible) errorColor else marker)
    drawSurveyMarker(p2, s1Focused || s2Focused, if (isImpossible) errorColor else marker)
    drawSurveyMarker(p3, s1Focused || s3Focused, if (isImpossible) errorColor else marker)

    drawLabelPill(formatSideWithDirection("side1", "A", inputs, sideDirections, sideBoundaries, lang), (p2.x + p3.x) / 2f, p2.y + 24f, s1Focused, isImpossible, paint)
    drawLabelPill(formatSideWithDirection("side2", "B", inputs, sideDirections, sideBoundaries, lang), (p1.x + p2.x) / 2f - 30f, (p1.y + p2.y) / 2f, s2Focused, isImpossible, paint)
    drawLabelPill(formatSideWithDirection("side3", "C", inputs, sideDirections, sideBoundaries, lang), (p1.x + p3.x) / 2f + 30f, (p1.y + p3.y) / 2f, s3Focused, isImpossible, paint)

    if (isImpossible) {
        val errorText = when (lang) {
            Language.ENGLISH -> "Impossible triangle!"
            Language.URDU -> "ناممکن مثلث!"
            Language.SINDHI -> "ناممڪن ٽڪنڊو!"
        }
        drawLabelPill(errorText, w * 0.5f, h * 0.48f, isFocused = false, isError = true, paint = paint)
    }
}

// 3. LIVE TRAPEZIUM
private fun DrawScope.drawLiveTrapezium(
    w: Float,
    h: Float,
    inputs: Map<String, String>,
    focusedKey: String?,
    sideDirections: Map<String, CompassDirection>,
    sideBoundaries: Map<String, String>,
    lang: Language,
    boundary: Color,
    fill: Color,
    highlight: Color,
    missing: Color,
    marker: Color,
    paint: android.graphics.Paint
) {
    val p1Val = inputs["parallel1"]?.toDoubleOrNull()
    val p2Val = inputs["parallel2"]?.toDoubleOrNull()
    val ratio = if (p1Val != null && p2Val != null && p1Val > 0 && p2Val > 0) {
        (p1Val / p2Val).coerceIn(0.2, 1.8).toFloat()
    } else 0.55f

    val topW = (w * 0.65f * ratio).coerceIn(w * 0.25f, w * 0.75f)
    val botW = w * 0.78f

    val tl = Offset((w - topW) / 2f, h * 0.22f)
    val tr = Offset(tl.x + topW, h * 0.22f)
    val br = Offset((w + botW) / 2f, h * 0.82f)
    val bl = Offset((w - botW) / 2f, h * 0.82f)

    val p1Focused = focusedKey == "parallel1"
    val p2Focused = focusedKey == "parallel2"
    val distFocused = focusedKey == "distance"

    val hasP1 = !inputs["parallel1"].isNullOrBlank()
    val hasP2 = !inputs["parallel2"].isNullOrBlank()
    val hasDist = !inputs["distance"].isNullOrBlank()

    val path = Path().apply {
        moveTo(tl.x, tl.y); lineTo(tr.x, tr.y); lineTo(br.x, br.y); lineTo(bl.x, bl.y); close()
    }
    drawPath(path, fill)

    val dash = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)

    drawLine(if (p1Focused) highlight else if (hasP1) boundary else missing, tl, tr, if (p1Focused) 12f else 6f, pathEffect = if (!hasP1 && !p1Focused) dash else null)
    drawLine(if (p2Focused) highlight else if (hasP2) boundary else missing, bl, br, if (p2Focused) 12f else 6f, pathEffect = if (!hasP2 && !p2Focused) dash else null)
    drawLine(boundary, tl, bl, strokeWidth = 5f)
    drawLine(boundary, tr, br, strokeWidth = 5f)

    val hX = w * 0.5f
    drawLine(if (distFocused) highlight else Color(0xFFC47D00), Offset(hX, tl.y), Offset(hX, bl.y), if (distFocused) 8f else 4f, pathEffect = dash)

    drawSurveyMarker(tl, p1Focused, marker)
    drawSurveyMarker(tr, p1Focused, marker)
    drawSurveyMarker(bl, p2Focused, marker)
    drawSurveyMarker(br, p2Focused, marker)

    drawLabelPill(formatSideWithDirection("parallel1", "Parallel 1", inputs, sideDirections, sideBoundaries, lang), (tl.x + tr.x) / 2f, tl.y - 12f, p1Focused, false, paint)
    drawLabelPill(formatSideWithDirection("parallel2", "Parallel 2", inputs, sideDirections, sideBoundaries, lang), (bl.x + br.x) / 2f, bl.y + 24f, p2Focused, false, paint)
    drawLabelPill(formatSideWithDirection("distance", "Distance", inputs, sideDirections, sideBoundaries, lang), hX + 40f, (tl.y + bl.y) / 2f + 8f, distFocused, false, paint)
}

// 4. LIVE 4-SIDED WITH DIAGONAL
private fun DrawScope.drawLiveFourSideDiagonal(
    w: Float,
    h: Float,
    inputs: Map<String, String>,
    focusedKey: String?,
    sideDirections: Map<String, CompassDirection>,
    sideBoundaries: Map<String, String>,
    lang: Language,
    boundary: Color,
    fill: Color,
    highlight: Color,
    missing: Color,
    marker: Color,
    paint: android.graphics.Paint
) {
    val topKey = resolveKeyForPosition(DiagramPosition.TOP, sideDirections, "side2")
    val rightKey = resolveKeyForPosition(DiagramPosition.RIGHT, sideDirections, "side3")
    val bottomKey = resolveKeyForPosition(DiagramPosition.BOTTOM, sideDirections, "side4")
    val leftKey = resolveKeyForPosition(DiagramPosition.LEFT, sideDirections, "side1")

    val pTop = Offset(w * 0.38f, h * 0.18f)
    val pRight = Offset(w * 0.85f, h * 0.36f)
    val pBottom = Offset(w * 0.68f, h * 0.82f)
    val pLeft = Offset(w * 0.15f, h * 0.72f)

    val isTopFocused = focusedKey == topKey
    val isRightFocused = focusedKey == rightKey
    val isBottomFocused = focusedKey == bottomKey
    val isLeftFocused = focusedKey == leftKey
    val diagFocused = focusedKey == "diagonal"

    val hasTop = !inputs[topKey].isNullOrBlank()
    val hasRight = !inputs[rightKey].isNullOrBlank()
    val hasBottom = !inputs[bottomKey].isNullOrBlank()
    val hasLeft = !inputs[leftKey].isNullOrBlank()
    val hasDiag = !inputs["diagonal"].isNullOrBlank()

    val path = Path().apply {
        moveTo(pTop.x, pTop.y); lineTo(pRight.x, pRight.y); lineTo(pBottom.x, pBottom.y); lineTo(pLeft.x, pLeft.y); close()
    }
    drawPath(path, fill)

    val dash = PathEffect.dashPathEffect(floatArrayOf(12f, 10f), 0f)

    // Left (pLeft to pTop)
    drawLine(if (isLeftFocused) highlight else if (hasLeft) boundary else missing, pLeft, pTop, if (isLeftFocused) 12f else 6f, pathEffect = if (!hasLeft && !isLeftFocused) dash else null)
    // Top (pTop to pRight)
    drawLine(if (isTopFocused) highlight else if (hasTop) boundary else missing, pTop, pRight, if (isTopFocused) 12f else 6f, pathEffect = if (!hasTop && !isTopFocused) dash else null)
    // Right (pRight to pBottom)
    drawLine(if (isRightFocused) highlight else if (hasRight) boundary else missing, pRight, pBottom, if (isRightFocused) 12f else 6f, pathEffect = if (!hasRight && !isRightFocused) dash else null)
    // Bottom (pBottom to pLeft)
    drawLine(if (isBottomFocused) highlight else if (hasBottom) boundary else missing, pBottom, pLeft, if (isBottomFocused) 12f else 6f, pathEffect = if (!hasBottom && !isBottomFocused) dash else null)

    // Diagonal
    val diagColor = if (diagFocused) highlight else if (hasDiag) Color(0xFFC47D00) else Color(0xFFE65100)
    drawLine(diagColor, pTop, pBottom, strokeWidth = if (diagFocused) 10f else 6f, pathEffect = dash)

    drawSurveyMarker(pTop, isLeftFocused || isTopFocused || diagFocused, marker)
    drawSurveyMarker(pRight, isTopFocused || isRightFocused, marker)
    drawSurveyMarker(pBottom, isRightFocused || isBottomFocused || diagFocused, marker)
    drawSurveyMarker(pLeft, isLeftFocused || isBottomFocused, marker)

    drawLabelPill(formatSideWithDirection(leftKey, "A", inputs, sideDirections, sideBoundaries, lang), (pLeft.x + pTop.x) / 2f - 24f, (pLeft.y + pTop.y) / 2f, isLeftFocused, false, paint)
    drawLabelPill(formatSideWithDirection(topKey, "B", inputs, sideDirections, sideBoundaries, lang), (pTop.x + pRight.x) / 2f + 24f, (pTop.y + pRight.y) / 2f - 10f, isTopFocused, false, paint)
    drawLabelPill(formatSideWithDirection(rightKey, "C", inputs, sideDirections, sideBoundaries, lang), (pRight.x + pBottom.x) / 2f + 30f, (pRight.y + pBottom.y) / 2f + 12f, isRightFocused, false, paint)
    drawLabelPill(formatSideWithDirection(bottomKey, "D", inputs, sideDirections, sideBoundaries, lang), (pLeft.x + pBottom.x) / 2f, pBottom.y + 24f, isBottomFocused, false, paint)

    if (hasDiag) {
        drawLabelPill(formatSideWithDirection("diagonal", "Diagonal", inputs, sideDirections, sideBoundaries, lang), (pTop.x + pBottom.x) / 2f + 36f, (pTop.y + pBottom.y) / 2f, diagFocused, false, paint)
    } else {
        drawLabelPill(AppStrings.pleaseMeasureDiagonal(lang), (pTop.x + pBottom.x) / 2f + 42f, (pTop.y + pBottom.y) / 2f, diagFocused, false, paint)
    }
}

// 5. LIVE 4-SIDED ESTIMATE (NO DIAGONAL)
// East side sits on the right, West on the left, North at the top, South at the bottom!
private fun DrawScope.drawLiveFourSideEstimate(
    w: Float,
    h: Float,
    inputs: Map<String, String>,
    focusedKey: String?,
    sideDirections: Map<String, CompassDirection>,
    sideBoundaries: Map<String, String>,
    lang: Language,
    boundary: Color,
    fill: Color,
    highlight: Color,
    missing: Color,
    marker: Color,
    paint: android.graphics.Paint
) {
    // Determine which key sits at each physical edge based on user's chosen direction!
    val topKey = resolveKeyForPosition(DiagramPosition.TOP, sideDirections, "top")
    val rightKey = resolveKeyForPosition(DiagramPosition.RIGHT, sideDirections, "right")
    val bottomKey = resolveKeyForPosition(DiagramPosition.BOTTOM, sideDirections, "bottom")
    val leftKey = resolveKeyForPosition(DiagramPosition.LEFT, sideDirections, "left")

    val tl = Offset(w * 0.22f, h * 0.2f)
    val tr = Offset(w * 0.78f, h * 0.17f)
    val br = Offset(w * 0.86f, h * 0.82f)
    val bl = Offset(w * 0.14f, h * 0.84f)

    val topFocused = focusedKey == topKey
    val rightFocused = focusedKey == rightKey
    val botFocused = focusedKey == bottomKey
    val leftFocused = focusedKey == leftKey

    val hasTop = !inputs[topKey].isNullOrBlank()
    val hasRight = !inputs[rightKey].isNullOrBlank()
    val hasBot = !inputs[bottomKey].isNullOrBlank()
    val hasLeft = !inputs[leftKey].isNullOrBlank()

    val path = Path().apply {
        moveTo(tl.x, tl.y); lineTo(tr.x, tr.y); lineTo(br.x, br.y); lineTo(bl.x, bl.y); close()
    }
    drawPath(path, Color(0x22C47D00))

    val dash = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)

    // Top (North)
    drawLine(if (topFocused) highlight else if (hasTop) boundary else missing, tl, tr, if (topFocused) 12f else 6f, pathEffect = if (!hasTop && !topFocused) dash else null)
    // Bottom (South)
    drawLine(if (botFocused) highlight else if (hasBot) boundary else missing, bl, br, if (botFocused) 12f else 6f, pathEffect = if (!hasBot && !botFocused) dash else null)
    // Left (West)
    drawLine(if (leftFocused) highlight else if (hasLeft) boundary else missing, tl, bl, if (leftFocused) 12f else 6f, pathEffect = if (!hasLeft && !leftFocused) dash else null)
    // Right (East)
    drawLine(if (rightFocused) highlight else if (hasRight) boundary else missing, tr, br, if (rightFocused) 12f else 6f, pathEffect = if (!hasRight && !rightFocused) dash else null)

    // Recommended diagonal line
    drawLine(Color(0xFFD97706), tl, br, 4f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f))

    drawSurveyMarker(tl, topFocused || leftFocused, marker)
    drawSurveyMarker(tr, topFocused || rightFocused, marker)
    drawSurveyMarker(br, botFocused || rightFocused, marker)
    drawSurveyMarker(bl, botFocused || leftFocused, marker)

    // Labels positioned on the exact physical side
    drawLabelPill(formatSideWithDirection(topKey, "A", inputs, sideDirections, sideBoundaries, lang), (tl.x + tr.x) / 2f, tl.y - 12f, topFocused, false, paint)
    drawLabelPill(formatSideWithDirection(rightKey, "D", inputs, sideDirections, sideBoundaries, lang), tr.x + 30f, (tr.y + br.y) / 2f + 8f, rightFocused, false, paint)
    drawLabelPill(formatSideWithDirection(bottomKey, "B", inputs, sideDirections, sideBoundaries, lang), (bl.x + br.x) / 2f, bl.y + 24f, botFocused, false, paint)
    drawLabelPill(formatSideWithDirection(leftKey, "C", inputs, sideDirections, sideBoundaries, lang), tl.x - 30f, (tl.y + bl.y) / 2f + 8f, leftFocused, false, paint)

    drawLabelPill(AppStrings.pleaseMeasureDiagonal(lang), (tl.x + br.x) / 2f, (tl.y + br.y) / 2f, isFocused = false, isError = false, paint = paint)
}

// 6. LIVE 5+ SIDES POLYGON
private fun DrawScope.drawLivePolygon(
    w: Float,
    h: Float,
    inputs: Map<String, String>,
    focusedKey: String?,
    sideDirections: Map<String, CompassDirection>,
    sideBoundaries: Map<String, String>,
    lang: Language,
    boundary: Color,
    fill: Color,
    highlight: Color,
    missing: Color,
    marker: Color,
    paint: android.graphics.Paint
) {
    val p1 = Offset(w * 0.5f, h * 0.16f)
    val p2 = Offset(w * 0.86f, h * 0.38f)
    val p3 = Offset(w * 0.74f, h * 0.82f)
    val p4 = Offset(w * 0.26f, h * 0.82f)
    val p5 = Offset(w * 0.14f, h * 0.38f)

    val s1Focused = focusedKey == "side1"
    val s2Focused = focusedKey == "side2"
    val s3Focused = focusedKey == "side3"
    val s4Focused = focusedKey == "side4"
    val s5Focused = focusedKey == "side5"
    val d1Focused = focusedKey == "diag1"
    val d2Focused = focusedKey == "diag2"

    val hasS1 = !inputs["side1"].isNullOrBlank()
    val hasS2 = !inputs["side2"].isNullOrBlank()
    val hasS3 = !inputs["side3"].isNullOrBlank()
    val hasS4 = !inputs["side4"].isNullOrBlank()
    val hasS5 = !inputs["side5"].isNullOrBlank()
    val hasD1 = !inputs["diag1"].isNullOrBlank()

    val path = Path().apply {
        moveTo(p1.x, p1.y); lineTo(p2.x, p2.y); lineTo(p3.x, p3.y); lineTo(p4.x, p4.y); lineTo(p5.x, p5.y); close()
    }
    drawPath(path, fill)

    val dash = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)

    drawLine(if (s1Focused) highlight else if (hasS1) boundary else missing, p1, p2, if (s1Focused) 12f else 6f, pathEffect = if (!hasS1 && !s1Focused) dash else null)
    drawLine(if (s2Focused) highlight else if (hasS2) boundary else missing, p2, p3, if (s2Focused) 12f else 6f, pathEffect = if (!hasS2 && !s2Focused) dash else null)
    drawLine(if (s3Focused) highlight else if (hasS3) boundary else missing, p3, p4, if (s3Focused) 12f else 6f, pathEffect = if (!hasS3 && !s3Focused) dash else null)
    drawLine(if (s4Focused) highlight else if (hasS4) boundary else missing, p4, p5, if (s4Focused) 12f else 6f, pathEffect = if (!hasS4 && !s4Focused) dash else null)
    drawLine(if (s5Focused) highlight else if (hasS5) boundary else missing, p5, p1, if (s5Focused) 12f else 6f, pathEffect = if (!hasS5 && !s5Focused) dash else null)

    drawLine(if (d1Focused) highlight else if (hasD1) Color(0xFFC47D00) else Color(0xFFD97706), p1, p3, if (d1Focused) 10f else 5f, pathEffect = dash)
    drawLine(if (d2Focused) highlight else if (hasD1) Color(0xFFC47D00) else Color(0xFFD97706), p1, p4, if (d2Focused) 10f else 5f, pathEffect = dash)

    drawSurveyMarker(p1, s1Focused || s5Focused || d1Focused || d2Focused, marker)
    drawSurveyMarker(p2, s1Focused || s2Focused, marker)
    drawSurveyMarker(p3, s2Focused || s3Focused || d1Focused, marker)
    drawSurveyMarker(p4, s3Focused || s4Focused || d2Focused, marker)
    drawSurveyMarker(p5, s4Focused || s5Focused, marker)

    drawLabelPill(formatSideWithDirection("side1", "A", inputs, sideDirections, sideBoundaries, lang), (p1.x + p2.x) / 2f + 20f, (p1.y + p2.y) / 2f, s1Focused, false, paint)
    drawLabelPill(formatSideWithDirection("side2", "B", inputs, sideDirections, sideBoundaries, lang), (p2.x + p3.x) / 2f + 25f, (p2.y + p3.y) / 2f, s2Focused, false, paint)
    drawLabelPill(formatSideWithDirection("side3", "C", inputs, sideDirections, sideBoundaries, lang), (p3.x + p4.x) / 2f, p3.y + 24f, s3Focused, false, paint)
    drawLabelPill(formatSideWithDirection("side4", "D", inputs, sideDirections, sideBoundaries, lang), (p4.x + p5.x) / 2f - 25f, (p4.y + p5.y) / 2f, s4Focused, false, paint)
    drawLabelPill(formatSideWithDirection("side5", "E", inputs, sideDirections, sideBoundaries, lang), (p5.x + p1.x) / 2f - 20f, (p5.y + p1.y) / 2f, s5Focused, false, paint)

    if (!hasD1) {
        drawLabelPill(AppStrings.diagonalLabel("1", lang), (p1.x + p3.x) / 2f + 20f, (p1.y + p3.y) / 2f, d1Focused, false, paint)
    }
}

// 7. LIVE CURVED SIDE
private fun DrawScope.drawLiveCurved(
    w: Float,
    h: Float,
    inputs: Map<String, String>,
    curvedOffsets: List<String>,
    focusedKey: String?,
    lang: Language,
    boundary: Color,
    fill: Color,
    highlight: Color,
    missing: Color,
    marker: Color,
    paint: android.graphics.Paint
) {
    val bLeft = Offset(w * 0.12f, h * 0.82f)
    val bRight = Offset(w * 0.88f, h * 0.82f)

    val baseFocused = focusedKey == "baseline"
    val hasBase = !inputs["baseline"].isNullOrBlank()

    val curvePath = Path().apply {
        moveTo(bLeft.x, bLeft.y); lineTo(bLeft.x, h * 0.45f)
        cubicTo(w * 0.35f, h * 0.15f, w * 0.65f, h * 0.38f, bRight.x, h * 0.52f)
        lineTo(bRight.x, bRight.y); close()
    }
    drawPath(curvePath, fill)
    drawPath(curvePath, boundary, style = Stroke(width = 6f))

    drawLine(if (baseFocused) highlight else if (hasBase) boundary else missing, bLeft, bRight, strokeWidth = if (baseFocused) 12f else 8f)

    val n = max(2, curvedOffsets.size)
    val step = (bRight.x - bLeft.x) / (n - 1)
    val dash = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)

    for (i in 0 until n) {
        val x = bLeft.x + i * step
        val topY = when (i % 5) {
            0 -> h * 0.45f; 1 -> h * 0.24f; 2 -> h * 0.29f; 3 -> h * 0.42f; else -> h * 0.52f
        }
        val isOffsetFocused = focusedKey == "offset_$i"
        val hasOffset = i < curvedOffsets.size && curvedOffsets[i].isNotBlank()

        drawLine(if (isOffsetFocused) highlight else if (hasOffset) Color(0xFFC47D00) else missing, Offset(x, bLeft.y), Offset(x, topY), if (isOffsetFocused) 8f else 3f, pathEffect = dash)
        drawSurveyMarker(Offset(x, topY), isOffsetFocused, marker)
    }

    drawSurveyMarker(bLeft, baseFocused, marker)
    drawSurveyMarker(bRight, baseFocused, marker)

    val baseTitle = when (lang) {
        Language.ENGLISH -> "Baseline"
        Language.URDU -> "بیس لائن"
        Language.SINDHI -> "بنيادي لڪير"
    }
    drawLabelPill(formatSideWithDirection("baseline", baseTitle, inputs, emptyMap(), emptyMap(), lang), w * 0.5f, bLeft.y + 24f, baseFocused, false, paint)
}
