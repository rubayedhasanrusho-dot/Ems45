package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = EduRankPurpleLight,
    onPrimary = Color.White,
    primaryContainer = EduRankPurpleDark,
    onPrimaryContainer = Color.White,
    secondary = EduRankAccent,
    onSecondary = Color.Black,
    background = BackgroundDark,
    surface = SurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark,
    outline = SurfaceVariantDark
)

private val LightColorScheme = lightColorScheme(
    primary = EduRankPurple,
    onPrimary = Color.White,
    primaryContainer = EduRankPurpleContainer,
    onPrimaryContainer = EduRankOnPurpleContainer,
    secondary = EduRankAccent,
    onSecondary = Color.Black,
    background = BackgroundLight,
    surface = SurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    outline = BorderLight
)

@Composable
fun EduRankTheme(
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

// Backward-compatible alias
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    EduRankTheme(darkTheme = darkTheme, content = content)
}
