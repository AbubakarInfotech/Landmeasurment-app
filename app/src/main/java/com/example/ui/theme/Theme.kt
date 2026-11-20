package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = FertileGreenPrimary,
    onPrimary = Color.White,
    primaryContainer = FertileGreenContainer,
    onPrimaryContainer = OnFertileGreenContainer,
    secondary = HarvestGold,
    onSecondary = Color.White,
    secondaryContainer = HarvestGoldContainer,
    onSecondaryContainer = OnHarvestGoldContainer,
    tertiary = FertileGreenLight,
    background = EarthBackground,
    onBackground = EarthTextPrimary,
    surface = EarthSurface,
    onSurface = EarthTextPrimary,
    surfaceVariant = EarthSurfaceVariant,
    onSurfaceVariant = EarthTextSecondary,
    outline = EarthOutline,
    error = AlertRed,
    errorContainer = AlertRedContainer
)

private val DarkColorScheme = darkColorScheme(
    primary = FertileGreenLight,
    onPrimary = Color.Black,
    primaryContainer = FertileGreenDark,
    onPrimaryContainer = FertileGreenContainer,
    secondary = HarvestGoldLight,
    onSecondary = Color.Black,
    secondaryContainer = HarvestGoldDark,
    onSecondaryContainer = HarvestGoldContainer,
    background = Color(0xFF111411),
    onBackground = Color(0xFFE2E4DE),
    surface = Color(0xFF191C19),
    onSurface = Color(0xFFE2E4DE),
    surfaceVariant = Color(0xFF282F26),
    onSurfaceVariant = Color(0xFFBCC3B8),
    error = Color(0xFFFFB4AB),
    errorContainer = Color(0xFF93000A)
)

@Composable
fun LandCalcTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
