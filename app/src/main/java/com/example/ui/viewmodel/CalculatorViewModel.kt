package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.calculator.CalcOutcome
import com.example.calculator.LandCalculator
import com.example.model.CalculationResult
import com.example.model.Language
import com.example.model.PlotShape
import com.example.model.Region
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class Screen {
    WELCOME,
    SHAPE_PICKER,
    MEASUREMENT,
    RESULT,
    GPS_WALKING,
    QUICK_MEASURE
}

data class UiState(
    val language: Language = Language.URDU,
    val region: Region = Region.SINDH,
    val currentScreen: Screen = Screen.WELCOME,
    val selectedShape: PlotShape = PlotShape.RECTANGLE,
    val inputs: Map<String, String> = emptyMap(),
    val curvedOffsets: List<String> = listOf("", "", "", "", ""),
    val showGuideDialog: Boolean = false,
    val calculationResult: CalculationResult? = null,
    val errorMessage: String? = null,
    val focusedFieldKey: String? = null,
    val sideDirections: Map<String, com.example.model.CompassDirection> = emptyMap(),
    val sideBoundaries: Map<String, String> = emptyMap(),
    val gpsCorners: List<com.example.gps.GpsPoint> = emptyList(),
    val currentGpsLocation: com.example.gps.GpsPoint? = null,
    val isSatelliteMap: Boolean = false,
    val gpsPermissionGranted: Boolean = false,
    val quickMeasureParts: List<com.example.model.QuickMeasurePart> = listOf(com.example.model.QuickMeasurePart(name = "Part 1")),
    val activeQuickMeasureIndex: Int = 0
)

class CalculatorViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun setLanguage(lang: Language) {
        _uiState.update { it.copy(language = lang, errorMessage = null) }
    }

    fun setRegion(region: Region) {
        _uiState.update { it.copy(region = region) }
    }

    fun proceedFromWelcome() {
        _uiState.update { it.copy(currentScreen = Screen.SHAPE_PICKER) }
    }

    fun selectShape(shape: PlotShape) {
        _uiState.update {
            it.copy(
                selectedShape = shape,
                currentScreen = Screen.MEASUREMENT,
                inputs = emptyMap(),
                curvedOffsets = listOf("", "", "", "", ""),
                errorMessage = null,
                calculationResult = null
            )
        }
    }

    fun setFocusedField(key: String?) {
        _uiState.update { it.copy(focusedFieldKey = key) }
    }

    fun setSideDirection(key: String, direction: com.example.model.CompassDirection?) {
        _uiState.update { current ->
            val updated = current.sideDirections.toMutableMap()
            if (direction == null) {
                updated.remove(key)
            } else {
                updated[key] = direction
            }
            current.copy(sideDirections = updated)
        }
    }

    fun setSideBoundary(key: String, boundary: String) {
        _uiState.update { current ->
            val updated = current.sideBoundaries.toMutableMap()
            if (boundary.isBlank()) {
                updated.remove(key)
            } else {
                updated[key] = boundary
            }
            current.copy(sideBoundaries = updated)
        }
    }

    fun getDuplicateDirection(): com.example.model.CompassDirection? {
        val dirs = _uiState.value.sideDirections.values
        val counts = dirs.groupingBy { it }.eachCount()
        return counts.entries.firstOrNull { it.value > 1 }?.key
    }

    fun getSideDisplayName(key: String, defaultLetter: String, lang: Language): String {
        val dir = _uiState.value.sideDirections[key]
        return if (dir != null) {
            "${dir.getName(lang)} ${com.example.localization.AppStrings.sideWord(lang)}"
        } else {
            com.example.localization.AppStrings.sideLabel(defaultLetter, lang)
        }
    }

    fun isImpossibleTriangle(): Boolean {
        val state = _uiState.value
        if (state.selectedShape != PlotShape.TRIANGLE) return false
        val a = state.inputs["side1"]?.toDoubleOrNull() ?: return false
        val b = state.inputs["side2"]?.toDoubleOrNull() ?: return false
        val c = state.inputs["side3"]?.toDoubleOrNull() ?: return false
        return a + b <= c || a + c <= b || b + c <= a
    }

    fun openGpsWalkingMode() {
        _uiState.update {
            it.copy(
                currentScreen = Screen.GPS_WALKING,
                gpsCorners = emptyList(),
                errorMessage = null
            )
        }
    }

    fun setGpsPermission(granted: Boolean) {
        _uiState.update { it.copy(gpsPermissionGranted = granted) }
    }

    fun updateCurrentGpsLocation(lat: Double, lon: Double, accuracy: Float) {
        _uiState.update {
            it.copy(currentGpsLocation = com.example.gps.GpsPoint(lat, lon, accuracy))
        }
    }

    fun markGpsCorner() {
        val loc = _uiState.value.currentGpsLocation ?: return
        _uiState.update {
            it.copy(gpsCorners = it.gpsCorners + loc)
        }
    }

    fun undoGpsCorner() {
        _uiState.update {
            if (it.gpsCorners.isNotEmpty()) {
                it.copy(gpsCorners = it.gpsCorners.dropLast(1))
            } else it
        }
    }

    fun resetGpsCorners() {
        _uiState.update { it.copy(gpsCorners = emptyList()) }
    }

    fun toggleSatelliteMap() {
        _uiState.update { it.copy(isSatelliteMap = !it.isSatelliteMap) }
    }

    fun closeGpsPlotAndTransferToManual() {
        val corners = _uiState.value.gpsCorners
        if (corners.size < 3) return

        val n = corners.size
        val sides = mutableListOf<Double>()
        val directions = mutableListOf<com.example.model.CompassDirection>()

        for (i in 0 until n) {
            val p1 = corners[i]
            val p2 = corners[(i + 1) % n]
            val dist = com.example.gps.GpsCalculator.haversineDistanceFeet(
                p1.latitude, p1.longitude, p2.latitude, p2.longitude
            )
            val bearing = com.example.gps.GpsCalculator.calculateBearing(
                p1.latitude, p1.longitude, p2.latitude, p2.longitude
            )
            val dir = com.example.gps.GpsCalculator.bearingToCompassDirection(bearing)
            sides.add(dist)
            directions.add(dir)
        }

        // Map into appropriate plot shape and side inputs
        val df = java.text.DecimalFormat("#0.0")
        val inputsMap = mutableMapOf<String, String>()
        val dirMap = mutableMapOf<String, com.example.model.CompassDirection>()
        val chosenShape: PlotShape

        when (n) {
            3 -> {
                chosenShape = PlotShape.TRIANGLE
                inputsMap["side1"] = df.format(sides[0])
                inputsMap["side2"] = df.format(sides[1])
                inputsMap["side3"] = df.format(sides[2])
                dirMap["side1"] = directions[0]
                dirMap["side2"] = directions[1]
                dirMap["side3"] = directions[2]
            }
            4 -> {
                chosenShape = PlotShape.FOUR_SIDE_ESTIMATE
                inputsMap["top"] = df.format(sides[0])
                inputsMap["right"] = df.format(sides[1])
                inputsMap["bottom"] = df.format(sides[2])
                inputsMap["left"] = df.format(sides[3])
                dirMap["top"] = directions[0]
                dirMap["right"] = directions[1]
                dirMap["bottom"] = directions[2]
                dirMap["left"] = directions[3]
            }
            else -> {
                chosenShape = PlotShape.MULTI_SIDE_POLYGON
                val keys = listOf("side1", "side2", "side3", "side4", "side5")
                for (i in 0 until minOf(5, n)) {
                    inputsMap[keys[i]] = df.format(sides[i])
                    dirMap[keys[i]] = directions[i]
                }
                // Approximate diagonals if 5 corners
                if (n >= 5) {
                    val d1 = com.example.gps.GpsCalculator.haversineDistanceFeet(
                        corners[0].latitude, corners[0].longitude, corners[2].latitude, corners[2].longitude
                    )
                    val d2 = com.example.gps.GpsCalculator.haversineDistanceFeet(
                        corners[0].latitude, corners[0].longitude, corners[3].latitude, corners[3].longitude
                    )
                    inputsMap["diag1"] = df.format(d1)
                    inputsMap["diag2"] = df.format(d2)
                }
            }
        }

        _uiState.update {
            it.copy(
                selectedShape = chosenShape,
                currentScreen = Screen.MEASUREMENT,
                inputs = inputsMap,
                sideDirections = dirMap,
                errorMessage = null
            )
        }
    }

    fun updateInput(key: String, value: String) {
        _uiState.update { current ->
            val updated = current.inputs.toMutableMap()
            updated[key] = value
            current.copy(inputs = updated, errorMessage = null)
        }
    }

    fun updateCurvedOffset(index: Int, value: String) {
        _uiState.update { current ->
            val list = current.curvedOffsets.toMutableList()
            if (index in list.indices) {
                list[index] = value
            }
            current.copy(curvedOffsets = list, errorMessage = null)
        }
    }

    fun addCurvedOffset() {
        _uiState.update { current ->
            if (current.curvedOffsets.size < 10) {
                current.copy(curvedOffsets = current.curvedOffsets + "")
            } else current
        }
    }

    fun removeCurvedOffset() {
        _uiState.update { current ->
            if (current.curvedOffsets.size > 2) {
                current.copy(curvedOffsets = current.curvedOffsets.dropLast(1))
            } else current
        }
    }

    fun openGuideDialog() {
        _uiState.update { it.copy(showGuideDialog = true) }
    }

    fun closeGuideDialog() {
        _uiState.update { it.copy(showGuideDialog = false) }
    }

    fun clearInputs() {
        _uiState.update {
            it.copy(
                inputs = emptyMap(),
                curvedOffsets = listOf("", "", "", "", ""),
                errorMessage = null
            )
        }
    }

    fun navigateBack() {
        _uiState.update { current ->
            when (current.currentScreen) {
                Screen.WELCOME -> current
                Screen.SHAPE_PICKER -> current.copy(currentScreen = Screen.WELCOME)
                Screen.MEASUREMENT -> current.copy(currentScreen = Screen.SHAPE_PICKER, errorMessage = null)
                Screen.RESULT -> current.copy(currentScreen = Screen.MEASUREMENT)
                Screen.GPS_WALKING -> current.copy(currentScreen = Screen.SHAPE_PICKER)
                Screen.QUICK_MEASURE -> current.copy(currentScreen = Screen.WELCOME)
            }
        }
    }

    fun startNewMeasurement() {
        _uiState.update {
            it.copy(
                currentScreen = Screen.SHAPE_PICKER,
                inputs = emptyMap(),
                curvedOffsets = listOf("", "", "", "", ""),
                errorMessage = null,
                calculationResult = null
            )
        }
    }

    // Load pre-configured test cases from requirements
    fun loadTestCase(testCaseIndex: Int) {
        when (testCaseIndex) {
            1 -> {
                // Top 65, bottom 55, left 295, right 295 (4-side estimate) = 17,700 sq ft = about 16.25 wesa
                _uiState.update {
                    it.copy(
                        selectedShape = PlotShape.FOUR_SIDE_ESTIMATE,
                        currentScreen = Screen.MEASUREMENT,
                        inputs = mapOf(
                            "top" to "65",
                            "bottom" to "55",
                            "left" to "295",
                            "right" to "295"
                        ),
                        errorMessage = null
                    )
                }
            }
            2 -> {
                // Rectangle 30 x 36.3 ft = 1,089 sq ft = exactly 1 wesa
                _uiState.update {
                    it.copy(
                        selectedShape = PlotShape.RECTANGLE,
                        currentScreen = Screen.MEASUREMENT,
                        inputs = mapOf(
                            "length" to "36.3",
                            "width" to "30"
                        ),
                        errorMessage = null
                    )
                }
            }
            3 -> {
                // Triangle with sides 10, 5, 3 (Impossible triangle test)
                _uiState.update {
                    it.copy(
                        selectedShape = PlotShape.TRIANGLE,
                        currentScreen = Screen.MEASUREMENT,
                        inputs = mapOf(
                            "side1" to "10",
                            "side2" to "5",
                            "side3" to "3"
                        ),
                        errorMessage = null
                    )
                }
            }
        }
    }

    fun performCalculation() {
        val state = _uiState.value
        val lang = state.language
        val outcome: CalcOutcome = when (state.selectedShape) {
            PlotShape.RECTANGLE -> {
                val l = state.inputs["length"]?.toDoubleOrNull()
                val w = state.inputs["width"]?.toDoubleOrNull()
                LandCalculator.calculateRectangle(l, w)
            }
            PlotShape.TRIANGLE -> {
                val a = state.inputs["side1"]?.toDoubleOrNull()
                val b = state.inputs["side2"]?.toDoubleOrNull()
                val c = state.inputs["side3"]?.toDoubleOrNull()
                LandCalculator.calculateTriangle(a, b, c)
            }
            PlotShape.TRAPEZIUM -> {
                val p1 = state.inputs["parallel1"]?.toDoubleOrNull()
                val p2 = state.inputs["parallel2"]?.toDoubleOrNull()
                val d = state.inputs["distance"]?.toDoubleOrNull()
                LandCalculator.calculateTrapezium(p1, p2, d)
            }
            PlotShape.FOUR_SIDE_DIAGONAL -> {
                val s1 = state.inputs["side1"]?.toDoubleOrNull()
                val s2 = state.inputs["side2"]?.toDoubleOrNull()
                val s3 = state.inputs["side3"]?.toDoubleOrNull()
                val s4 = state.inputs["side4"]?.toDoubleOrNull()
                val diag = state.inputs["diagonal"]?.toDoubleOrNull()
                LandCalculator.calculateFourSideDiagonal(s1, s2, s3, s4, diag)
            }
            PlotShape.FOUR_SIDE_ESTIMATE -> {
                val t = state.inputs["top"]?.toDoubleOrNull()
                val b = state.inputs["bottom"]?.toDoubleOrNull()
                val l = state.inputs["left"]?.toDoubleOrNull()
                val r = state.inputs["right"]?.toDoubleOrNull()
                LandCalculator.calculateFourSideEstimate(t, b, l, r)
            }
            PlotShape.MULTI_SIDE_POLYGON -> {
                val s1 = state.inputs["side1"]?.toDoubleOrNull()
                val s2 = state.inputs["side2"]?.toDoubleOrNull()
                val s3 = state.inputs["side3"]?.toDoubleOrNull()
                val s4 = state.inputs["side4"]?.toDoubleOrNull()
                val s5 = state.inputs["side5"]?.toDoubleOrNull()
                val d1 = state.inputs["diag1"]?.toDoubleOrNull()
                val d2 = state.inputs["diag2"]?.toDoubleOrNull()
                LandCalculator.calculatePentagon(s1, s2, s3, s4, s5, d1, d2)
            }
            PlotShape.CURVED_SIDE -> {
                val base = state.inputs["baseline"]?.toDoubleOrNull()
                val offsets = state.curvedOffsets.map { it.toDoubleOrNull() }
                LandCalculator.calculateCurvedSide(base, offsets)
            }
        }

        when (outcome) {
            is CalcOutcome.Success -> {
                val enhancedResult = if (state.sideDirections.isNotEmpty() || state.sideBoundaries.isNotEmpty()) {
                    val dirList = mutableListOf<String>()
                    val dirListUr = mutableListOf<String>()
                    val dirListSd = mutableListOf<String>()
                    state.inputs.forEach { (key, value) ->
                        if (value.isNotBlank()) {
                            val dir = state.sideDirections[key]
                            val boundary = state.sideBoundaries[key]
                            val dirEn = if (dir != null) "${dir.englishName} side" else "Side $key"
                            val dirUr = if (dir != null) "${dir.urduName} طرف" else "ضلع $key"
                            val dirSd = if (dir != null) "${dir.sindhiName} طرف" else "پاسو $key"
                            val bSuffixEn = if (!boundary.isNullOrBlank()) " [Boundary: $boundary]" else ""
                            val bSuffixUr = if (!boundary.isNullOrBlank()) " [حد: $boundary]" else ""
                            val bSuffixSd = if (!boundary.isNullOrBlank()) " [حد: $boundary]" else ""
                            dirList.add("• $dirEn ($value ft)$bSuffixEn")
                            dirListUr.add("• $dirUr ($value فٹ)$bSuffixUr")
                            dirListSd.add("• $dirSd ($value فوٽ)$bSuffixSd")
                        }
                    }
                    val boundaryStep = com.example.model.CalculationStep(
                        titleEn = "Plot Boundaries & Directions",
                        titleUr = "پلاٹ کی سمتیں اور چہار دیواری / حدود",
                        titleSd = "پلاٽ جون طرفون ۽ چئوطرف حدون",
                        detailEn = dirList.joinToString("\n"),
                        detailUr = dirListUr.joinToString("\n"),
                        detailSd = dirListSd.joinToString("\n")
                    )
                    outcome.result.copy(steps = listOf(boundaryStep) + outcome.result.steps)
                } else {
                    outcome.result
                }
                _uiState.update {
                    it.copy(
                        calculationResult = enhancedResult,
                        currentScreen = Screen.RESULT,
                        errorMessage = null
                    )
                }
            }
            is CalcOutcome.Error -> {
                _uiState.update {
                    it.copy(
                        errorMessage = outcome.getMessage(lang),
                        calculationResult = null
                    )
                }
            }
        }
    }

    fun openGpsWalking() {
        _uiState.update { it.copy(currentScreen = Screen.GPS_WALKING) }
    }

    // QUICK MEASURE METHODS
    fun openQuickMeasure() {
        _uiState.update { it.copy(currentScreen = Screen.QUICK_MEASURE, errorMessage = null) }
        calculateActiveQuickMeasure()
    }

    fun setQuickMeasurePartIndex(index: Int) {
        _uiState.update {
            if (index in it.quickMeasureParts.indices) {
                it.copy(activeQuickMeasureIndex = index)
            } else it
        }
    }

    fun addQuickMeasurePart() {
        _uiState.update { current ->
            val newNum = current.quickMeasureParts.size + 1
            val newPart = com.example.model.QuickMeasurePart(name = "Part $newNum")
            val updated = current.quickMeasureParts + newPart
            current.copy(
                quickMeasureParts = updated,
                activeQuickMeasureIndex = updated.size - 1
            )
        }
    }

    fun removeQuickMeasurePart(index: Int) {
        _uiState.update { current ->
            if (current.quickMeasureParts.size <= 1) return@update current
            val updated = current.quickMeasureParts.filterIndexed { i, _ -> i != index }
            val newIndex = (current.activeQuickMeasureIndex - 1).coerceAtLeast(0).coerceAtMost(updated.size - 1)
            current.copy(
                quickMeasureParts = updated,
                activeQuickMeasureIndex = newIndex
            )
        }
    }

    fun setQuickMeasureInputMode(isListMode: Boolean) {
        _uiState.update { current ->
            val idx = current.activeQuickMeasureIndex
            val parts = current.quickMeasureParts.toMutableList()
            val part = parts[idx]
            // If switching modes, convert representation
            val updatedPart = if (isListMode) {
                val nonBlank = part.individualSides.filter { it.isNotBlank() }
                part.copy(isListMode = true, rawInputText = if (nonBlank.isNotEmpty()) nonBlank.joinToString(", ") else part.rawInputText)
            } else {
                val parsed = LandCalculator.parseSidesList(part.rawInputText)
                val newIndiv = if (parsed.isNotEmpty()) parsed.map { if (it % 1.0 == 0.0) it.toLong().toString() else it.toString() } else listOf("", "", "", "")
                part.copy(isListMode = false, individualSides = if (newIndiv.size < 4) newIndiv + List(4 - newIndiv.size) { "" } else newIndiv)
            }
            parts[idx] = updatedPart
            current.copy(quickMeasureParts = parts)
        }
        calculateActiveQuickMeasure()
    }

    fun updateQuickMeasureInputText(text: String) {
        _uiState.update { current ->
            val idx = current.activeQuickMeasureIndex
            val parts = current.quickMeasureParts.toMutableList()
            parts[idx] = parts[idx].copy(rawInputText = text)
            current.copy(quickMeasureParts = parts)
        }
        calculateActiveQuickMeasure()
    }

    fun updateQuickMeasureIndividualSide(sideIndex: Int, value: String) {
        _uiState.update { current ->
            val idx = current.activeQuickMeasureIndex
            val parts = current.quickMeasureParts.toMutableList()
            val part = parts[idx]
            val sides = part.individualSides.toMutableList()
            if (sideIndex in sides.indices) {
                sides[sideIndex] = value
            }
            parts[idx] = part.copy(individualSides = sides)
            current.copy(quickMeasureParts = parts)
        }
        calculateActiveQuickMeasure()
    }

    fun addQuickMeasureIndividualSide() {
        _uiState.update { current ->
            val idx = current.activeQuickMeasureIndex
            val parts = current.quickMeasureParts.toMutableList()
            val part = parts[idx]
            parts[idx] = part.copy(individualSides = part.individualSides + "")
            current.copy(quickMeasureParts = parts)
        }
    }

    fun removeQuickMeasureIndividualSide(sideIndex: Int) {
        _uiState.update { current ->
            val idx = current.activeQuickMeasureIndex
            val parts = current.quickMeasureParts.toMutableList()
            val part = parts[idx]
            if (part.individualSides.size > 3 && sideIndex in part.individualSides.indices) {
                val sides = part.individualSides.filterIndexed { i, _ -> i != sideIndex }
                parts[idx] = part.copy(individualSides = sides)
            }
            current.copy(quickMeasureParts = parts)
        }
        calculateActiveQuickMeasure()
    }

    fun setQuickMeasureDiagonal(value: String) {
        _uiState.update { current ->
            val idx = current.activeQuickMeasureIndex
            val parts = current.quickMeasureParts.toMutableList()
            parts[idx] = parts[idx].copy(diagonalInput = value, hasDiagonal = true)
            current.copy(quickMeasureParts = parts)
        }
        calculateActiveQuickMeasure()
    }

    fun toggleQuickMeasureDiagonal(enabled: Boolean) {
        _uiState.update { current ->
            val idx = current.activeQuickMeasureIndex
            val parts = current.quickMeasureParts.toMutableList()
            parts[idx] = parts[idx].copy(hasDiagonal = enabled)
            current.copy(quickMeasureParts = parts)
        }
        calculateActiveQuickMeasure()
    }

    fun setQuickMeasureSideDirection(sideIndex: Int, direction: com.example.model.CompassDirection?) {
        _uiState.update { current ->
            val idx = current.activeQuickMeasureIndex
            val parts = current.quickMeasureParts.toMutableList()
            val part = parts[idx]
            val dirs = part.sideDirections.toMutableMap()
            if (direction == null) dirs.remove(sideIndex) else dirs[sideIndex] = direction
            parts[idx] = part.copy(sideDirections = dirs)
            current.copy(quickMeasureParts = parts)
        }
    }

    fun setQuickMeasureSideBoundary(sideIndex: Int, boundary: String) {
        _uiState.update { current ->
            val idx = current.activeQuickMeasureIndex
            val parts = current.quickMeasureParts.toMutableList()
            val part = parts[idx]
            val bounds = part.sideBoundaries.toMutableMap()
            if (boundary.isBlank()) bounds.remove(sideIndex) else bounds[sideIndex] = boundary
            parts[idx] = part.copy(sideBoundaries = bounds)
            current.copy(quickMeasureParts = parts)
        }
    }

    fun setQuickMeasureFocusedSide(sideIndex: Int?) {
        _uiState.update { current ->
            val idx = current.activeQuickMeasureIndex
            val parts = current.quickMeasureParts.toMutableList()
            parts[idx] = parts[idx].copy(focusedSideIndex = sideIndex)
            current.copy(quickMeasureParts = parts)
        }
    }

    fun calculateActiveQuickMeasure() {
        val current = _uiState.value
        val idx = current.activeQuickMeasureIndex
        if (idx !in current.quickMeasureParts.indices) return
        val part = current.quickMeasureParts[idx]
        val sides = part.getParsedSides()
        val diagonals = if (part.hasDiagonal && part.diagonalInput.isNotBlank()) {
            listOfNotNull(part.diagonalInput.toDoubleOrNull())
        } else emptyList()

        if (sides.isEmpty()) {
            _uiState.update { state ->
                val parts = state.quickMeasureParts.toMutableList()
                parts[idx] = part.copy(calculationResult = null, errorMessage = null)
                state.copy(quickMeasureParts = parts)
            }
            return
        }

        when (val outcome = LandCalculator.calculateQuickMeasure(sides, diagonals, part.sideDirections)) {
            is CalcOutcome.Success -> {
                _uiState.update { state ->
                    val parts = state.quickMeasureParts.toMutableList()
                    parts[idx] = parts[idx].copy(calculationResult = outcome.result, errorMessage = null)
                    state.copy(quickMeasureParts = parts)
                }
            }
            is CalcOutcome.Error -> {
                _uiState.update { state ->
                    val parts = state.quickMeasureParts.toMutableList()
                    parts[idx] = parts[idx].copy(
                        errorMessage = outcome.getMessage(state.language),
                        calculationResult = null
                    )
                    state.copy(quickMeasureParts = parts)
                }
            }
        }
    }

    fun loadQuickMeasureTestCase(testCaseIndex: Int) {
        when (testCaseIndex) {
            1 -> {
                // "65, 295, 55, 295" -> ~17,700 sq ft (~16.25 wesa), ESTIMATE
                _uiState.update { current ->
                    val idx = current.activeQuickMeasureIndex
                    val parts = current.quickMeasureParts.toMutableList()
                    parts[idx] = parts[idx].copy(
                        isListMode = true,
                        rawInputText = "65, 295, 55, 295",
                        hasDiagonal = false,
                        diagonalInput = ""
                    )
                    current.copy(quickMeasureParts = parts)
                }
                calculateActiveQuickMeasure()
            }
            2 -> {
                // "33, 33, 33, 33" -> 1,089 sq ft = 1 wesa, EXACT
                _uiState.update { current ->
                    val idx = current.activeQuickMeasureIndex
                    val parts = current.quickMeasureParts.toMutableList()
                    parts[idx] = parts[idx].copy(
                        isListMode = true,
                        rawInputText = "33, 33, 33, 33",
                        hasDiagonal = false,
                        diagonalInput = ""
                    )
                    current.copy(quickMeasureParts = parts)
                }
                calculateActiveQuickMeasure()
            }
            3 -> {
                // "10, 5, 3" -> Impossible shape, REJECT
                _uiState.update { current ->
                    val idx = current.activeQuickMeasureIndex
                    val parts = current.quickMeasureParts.toMutableList()
                    parts[idx] = parts[idx].copy(
                        isListMode = true,
                        rawInputText = "10, 5, 3",
                        hasDiagonal = false,
                        diagonalInput = ""
                    )
                    current.copy(quickMeasureParts = parts)
                }
                calculateActiveQuickMeasure()
            }
        }
    }
}
