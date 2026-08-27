package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = CyanPrimary,
    onPrimary = DarkCanvas,
    primaryContainer = IndigoAccent,
    onPrimaryContainer = Color.White,
    secondary = VioletAccent,
    onSecondary = Color.White,
    secondaryContainer = DarkSurfaceElevated,
    onSecondaryContainer = TextPrimary,
    tertiary = EmeraldSuccess,
    onTertiary = Color.White,
    background = DarkCanvas,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceElevated,
    onSurfaceVariant = TextSecondary,
    outline = DarkSurfaceBorder,
    error = RoseError,
    onError = Color.White
)

private val LightColorScheme = darkColorScheme(
    primary = CyanPrimary,
    onPrimary = DarkCanvas,
    primaryContainer = IndigoAccent,
    onPrimaryContainer = Color.White,
    secondary = VioletAccent,
    onSecondary = Color.White,
    secondaryContainer = DarkSurfaceElevated,
    onSecondaryContainer = TextPrimary,
    tertiary = EmeraldSuccess,
    onTertiary = Color.White,
    background = DarkCanvas,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceElevated,
    onSurfaceVariant = TextSecondary,
    outline = DarkSurfaceBorder,
    error = RoseError,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    // Default to the tailored premium dark cyberpunk palette for an immersive media downloader feel
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
