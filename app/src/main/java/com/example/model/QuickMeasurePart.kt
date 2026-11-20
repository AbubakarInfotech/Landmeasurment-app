package com.example.model

import com.example.calculator.LandCalculator
import java.util.UUID

data class QuickMeasurePart(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "Part 1",
    val rawInputText: String = "",
    val individualSides: List<String> = listOf("", "", "", ""),
    val isListMode: Boolean = true,
    val diagonalInput: String = "",
    val hasDiagonal: Boolean = false,
    val sideDirections: Map<Int, CompassDirection> = emptyMap(),
    val sideBoundaries: Map<Int, String> = emptyMap(),
    val calculationResult: CalculationResult? = null,
    val errorMessage: String? = null,
    val focusedSideIndex: Int? = null
) {
    fun getParsedSides(): List<Double> {
        return if (isListMode) {
            LandCalculator.parseSidesList(rawInputText)
        } else {
            individualSides.mapNotNull { it.trim().toDoubleOrNull() }
        }
    }
}
