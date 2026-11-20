package com.example

import com.example.calculator.CalcOutcome
import com.example.calculator.LandCalculator
import com.example.model.Language
import com.example.model.PlotShape
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LandCalculatorTest {

    @Test
    fun testCase1_fourSideEstimate() {
        // Top 65, bottom 55, left 295, right 295 (4-side estimate) = 17,700 sq ft = about 16.25 wesa
        val outcome = LandCalculator.calculateFourSideEstimate(
            top = 65.0,
            bottom = 55.0,
            left = 295.0,
            right = 295.0
        )

        assertTrue("Calculation should succeed", outcome is CalcOutcome.Success)
        val result = (outcome as CalcOutcome.Success).result

        assertEquals(17700.0, result.sqFeet, 0.001)
        // 17,700 / 1,089 = 16.2534435...
        assertEquals(16.2534, result.wesa, 0.005)
        assertEquals(PlotShape.FOUR_SIDE_ESTIMATE, result.shape)
        assertTrue("Must be flagged as estimate", result.isEstimate)
        assertNotNull("Must provide warning in Urdu", result.getWarning(Language.URDU))
        assertNotNull("Must provide warning in Sindhi", result.getWarning(Language.SINDHI))
        assertNotNull("Must provide warning in English", result.getWarning(Language.ENGLISH))
    }

    @Test
    fun testCase2_rectangleExactOneWesa() {
        // Rectangle 30 x 36.3 ft = 1,089 sq ft = exactly 1 wesa
        val outcome = LandCalculator.calculateRectangle(
            length = 36.3,
            width = 30.0
        )

        assertTrue("Calculation should succeed", outcome is CalcOutcome.Success)
        val result = (outcome as CalcOutcome.Success).result

        assertEquals(1089.0, result.sqFeet, 0.001)
        assertEquals(1.0, result.wesa, 0.0001)
        assertEquals(1.0, result.gunta, 0.0001)
        assertEquals(1.0 / 20.0, result.jareeb, 0.0001)
        assertEquals(1.0 / 40.0, result.acre, 0.0001)
        assertEquals(false, result.isEstimate)
    }

    @Test
    fun testCase3_impossibleTriangleRejected() {
        // Triangle with sides 10, 5, 3 must be rejected because 5 + 3 = 8 < 10
        val outcome = LandCalculator.calculateTriangle(
            a = 10.0,
            b = 5.0,
            c = 3.0
        )

        assertTrue("Triangle with sides 10, 5, 3 must be rejected as an error", outcome is CalcOutcome.Error)
        val error = outcome as CalcOutcome.Error
        assertTrue("Error message in English must mention impossible triangle", error.messageEn.contains("Impossible triangle"))
        assertTrue("Error message in Urdu must be present", error.messageUr.isNotEmpty())
        assertTrue("Error message in Sindhi must be present", error.messageSd.isNotEmpty())
    }

    @Test
    fun testValidTriangle_heronFormula() {
        // 3-4-5 right triangle -> Area = 6 sq ft
        val outcome = LandCalculator.calculateTriangle(3.0, 4.0, 5.0)
        assertTrue(outcome is CalcOutcome.Success)
        val res = (outcome as CalcOutcome.Success).result
        assertEquals(6.0, res.sqFeet, 0.001)
    }

    @Test
    fun testTrapezium() {
        // Parallel sides 20 and 30, distance 10 -> (20+30)/2 * 10 = 250 sq ft
        val outcome = LandCalculator.calculateTrapezium(20.0, 30.0, 10.0)
        assertTrue(outcome is CalcOutcome.Success)
        val res = (outcome as CalcOutcome.Success).result
        assertEquals(250.0, res.sqFeet, 0.001)
    }

    @Test
    fun testNegativeAndZeroRejection() {
        val outcomeZero = LandCalculator.calculateRectangle(0.0, 50.0)
        assertTrue("Zero must be rejected", outcomeZero is CalcOutcome.Error)

        val outcomeNegative = LandCalculator.calculateRectangle(-10.0, 50.0)
        assertTrue("Negative values must be rejected", outcomeNegative is CalcOutcome.Error)

        val outcomeNull = LandCalculator.calculateRectangle(null, 50.0)
        assertTrue("Null inputs must be rejected", outcomeNull is CalcOutcome.Error)
    }

    @Test
    fun testLiveDiagram_realTimeInputAndHighlight() {
        val viewModel = com.example.ui.viewmodel.CalculatorViewModel()
        viewModel.selectShape(PlotShape.FOUR_SIDE_ESTIMATE)

        // Select Side A field (focused)
        viewModel.setFocusedField("top")
        assertEquals("top", viewModel.uiState.value.focusedFieldKey)

        // Type 65 in Side A
        viewModel.updateInput("top", "65")
        assertEquals("65", viewModel.uiState.value.inputs["top"])

        // Verify labels in all 3 languages
        val enLabel = com.example.localization.AppStrings.sideLabel("A", Language.ENGLISH)
        assertTrue("English label should contain Side A", enLabel.contains("Side A"))
        val urLabel = com.example.localization.AppStrings.sideLabel("A", Language.URDU)
        assertTrue("Urdu label should contain ضلع A", urLabel.contains("ضلع A"))
        val sdLabel = com.example.localization.AppStrings.sideLabel("A", Language.SINDHI)
        assertTrue("Sindhi label should contain پاسو A", sdLabel.contains("پاسو A"))

        // Real-time impossible triangle detection
        viewModel.selectShape(PlotShape.TRIANGLE)
        viewModel.updateInput("side1", "10")
        viewModel.updateInput("side2", "5")
        viewModel.updateInput("side3", "3")
        assertTrue("Triangle 10, 5, 3 must be flagged impossible in real time", viewModel.isImpossibleTriangle())
    }

    @Test
    fun testDirection_aubharInSindhiLabelsAndPlacesOnRightSide() {
        // Choosing "Aubhar" in Sindhi must label that side "اوڀر" on the diagram and place it on the right side
        val viewModel = com.example.ui.viewmodel.CalculatorViewModel()
        viewModel.setLanguage(Language.SINDHI)
        viewModel.selectShape(PlotShape.FOUR_SIDE_ESTIMATE)

        // Verify Sindhi name for EAST is Aubhar (اوڀر)
        assertEquals("اوڀر", com.example.model.CompassDirection.EAST.getName(Language.SINDHI))

        // Assign CompassDirection.EAST to Side A (key "top")
        viewModel.setSideDirection("top", com.example.model.CompassDirection.EAST)
        viewModel.updateInput("top", "65")

        // 1. Verify label format: must contain "اوڀر" and "65"
        val label = com.example.ui.components.formatSideWithDirection(
            key = "top",
            defaultLetter = "A",
            inputs = viewModel.uiState.value.inputs,
            sideDirections = viewModel.uiState.value.sideDirections,
            sideBoundaries = viewModel.uiState.value.sideBoundaries,
            lang = Language.SINDHI
        )
        assertTrue("Label on diagram must contain اوڀر (Aubhar)", label.contains("اوڀر"))
        assertTrue("Label on diagram must contain 65", label.contains("65"))

        // 2. Verify placement on the right side of the diagram:
        // DiagramPosition.RIGHT must resolve to "top" because it is tagged with EAST (Aubhar)
        val rightSideKey = com.example.ui.components.resolveKeyForPosition(
            position = com.example.ui.components.DiagramPosition.RIGHT,
            sideDirections = viewModel.uiState.value.sideDirections,
            defaultKey = "right"
        )
        assertEquals("Side marked Aubhar (East) must sit on the right side of the diagram", "top", rightSideKey)

        // 3. Verify duplicate direction detection
        viewModel.setSideDirection("bottom", com.example.model.CompassDirection.EAST)
        assertEquals("Duplicate direction EAST must be detected", com.example.model.CompassDirection.EAST, viewModel.getDuplicateDirection())
    }

    @Test
    fun testGpsWalking_twoPoints33FtApart() {
        // Test case: marking two points about 33 ft apart must show a distance close to 33 ft
        val lat1 = 30.000000
        val lon1 = 70.000000
        // 33 feet is 10.0584 meters. Approx 0.00009050288 degrees latitude
        val lat2 = 30.00009050288
        val lon2 = 70.000000

        val distanceFeet = com.example.gps.GpsCalculator.haversineDistanceFeet(lat1, lon1, lat2, lon2)
        assertEquals("Distance between points must be approximately 33 ft", 33.0, distanceFeet, 0.5)

        // Test bearing: walking North
        val bearing = com.example.gps.GpsCalculator.calculateBearing(lat1, lon1, lat2, lon2)
        val dir = com.example.gps.GpsCalculator.bearingToCompassDirection(bearing)
        assertEquals("Direction must be NORTH", com.example.model.CompassDirection.NORTH, dir)
    }

    @Test
    fun testGpsWalking_shoelaceSquarePlotArea() {
        // 4 corners of a 33x33 ft plot = 1,089 sq ft = 1 wesa
        val dLat = 0.00009050288
        val dLon = 0.000104443 // accounting for cos(30 degrees)
        val p1 = com.example.gps.GpsPoint(30.0, 70.0, 3f)
        val p2 = com.example.gps.GpsPoint(30.0 + dLat, 70.0, 3f)
        val p3 = com.example.gps.GpsPoint(30.0 + dLat, 70.0 + dLon, 3f)
        val p4 = com.example.gps.GpsPoint(30.0, 70.0 + dLon, 3f)

        val area = com.example.gps.GpsCalculator.shoelaceAreaSqFeet(listOf(p1, p2, p3, p4))
        // Verify area is close to 1089 sq ft
        assertEquals(1089.0, area, 15.0)

        val outcome = LandCalculator.calculateGpsShoelace(listOf(p1, p2, p3, p4))
        assertTrue(outcome is CalcOutcome.Success)
        val res = (outcome as CalcOutcome.Success).result
        assertTrue(res.isEstimate)
        assertEquals(PlotShape.MULTI_SIDE_POLYGON, res.shape)
    }
}
