package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.GpsWalkingScreen
import com.example.ui.screens.MeasurementScreen
import com.example.ui.screens.ResultScreen
import com.example.ui.viewmodel.Screen
import com.example.ui.screens.ShapeGuideDialog
import com.example.ui.screens.ShapePickerScreen
import com.example.ui.screens.WelcomeScreen
import com.example.ui.theme.LandCalcTheme
import com.example.ui.viewmodel.CalculatorViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: CalculatorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LandCalcTheme {
                MainApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainApp(viewModel: CalculatorViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val isRtl = state.language.isRtl
    val layoutDirection = if (isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr

    // Handle system back navigation
    BackHandler(enabled = state.currentScreen != Screen.WELCOME) {
        viewModel.navigateBack()
    }

    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        Surface(modifier = Modifier.fillMaxSize()) {
            when (state.currentScreen) {
                Screen.WELCOME -> {
                    WelcomeScreen(
                        currentLanguage = state.language,
                        currentRegion = state.region,
                        onLanguageSelected = { viewModel.setLanguage(it) },
                        onRegionSelected = { viewModel.setRegion(it) },
                        onContinue = { viewModel.proceedFromWelcome() }
                    )
                }
                Screen.SHAPE_PICKER -> {
                    ShapePickerScreen(
                        currentLanguage = state.language,
                        onShapeSelected = { viewModel.selectShape(it) },
                        onOpenGuide = { viewModel.openGuideDialog() },
                        onOpenGpsWalking = { viewModel.openGpsWalking() },
                        onBackToSettings = { viewModel.navigateBack() }
                    )
                }
                Screen.MEASUREMENT -> {
                    MeasurementScreen(
                        shape = state.selectedShape,
                        lang = state.language,
                        inputs = state.inputs,
                        curvedOffsets = state.curvedOffsets,
                        errorMessage = state.errorMessage,
                        focusedKey = state.focusedFieldKey,
                        sideDirections = state.sideDirections,
                        sideBoundaries = state.sideBoundaries,
                        duplicateDirection = viewModel.getDuplicateDirection(),
                        isImpossibleTriangle = viewModel.isImpossibleTriangle(),
                        onFocusChange = { viewModel.setFocusedField(it) },
                        onInputChange = { k, v -> viewModel.updateInput(k, v) },
                        onDirectionChange = { k, dir -> viewModel.setSideDirection(k, dir) },
                        onBoundaryChange = { k, note -> viewModel.setSideBoundary(k, note) },
                        onCurvedOffsetChange = { i, v -> viewModel.updateCurvedOffset(i, v) },
                        onAddCurvedOffset = { viewModel.addCurvedOffset() },
                        onRemoveCurvedOffset = { viewModel.removeCurvedOffset() },
                        onClear = { viewModel.clearInputs() },
                        onCalculate = { viewModel.performCalculation() },
                        onLoadTestCase = { viewModel.loadTestCase(it) },
                        onOpenGpsWalking = { viewModel.openGpsWalking() },
                        onBack = { viewModel.navigateBack() }
                    )
                }
                Screen.GPS_WALKING -> {
                    GpsWalkingScreen(
                        lang = state.language,
                        corners = state.gpsCorners,
                        currentLocation = state.currentGpsLocation,
                        isSatellite = state.isSatelliteMap,
                        onLocationUpdate = { lat, lon, acc ->
                            viewModel.updateCurrentGpsLocation(lat, lon, acc)
                        },
                        onMarkCorner = { viewModel.markGpsCorner() },
                        onUndoCorner = { viewModel.undoGpsCorner() },
                        onResetCorners = { viewModel.resetGpsCorners() },
                        onToggleSatellite = { viewModel.toggleSatelliteMap() },
                        onClosePlot = { viewModel.closeGpsPlotAndTransferToManual() },
                        onBack = { viewModel.navigateBack() }
                    )
                }
                Screen.RESULT -> {
                    state.calculationResult?.let { result ->
                        ResultScreen(
                            result = result,
                            region = state.region,
                            lang = state.language,
                            onNewMeasurement = { viewModel.startNewMeasurement() },
                            onBack = { viewModel.navigateBack() }
                        )
                    }
                }
            }

            // Shape Finder Guide Wizard
            if (state.showGuideDialog) {
                ShapeGuideDialog(
                    lang = state.language,
                    onDismiss = { viewModel.closeGuideDialog() },
                    onShapeChosen = { shape ->
                        viewModel.selectShape(shape)
                    }
                )
            }
        }
    }
}
