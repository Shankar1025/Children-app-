package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val BrutalColorScheme = lightColorScheme(
    primary = NeoYellow,
    secondary = NeoBlue,
    tertiary = NeoPink,
    background = BrutalPaper,
    surface = Color.White,
    onPrimary = BrutalBlack,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = BrutalBlack,
    onSurface = BrutalBlack,
    error = NeoPink,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit,
) {
    // For a child-centric Neo-Brutalism theme, we enforce our custom high-contrast,
    // cartoon-style color scheme rather than standard system dynamic gray-schemes.
    MaterialTheme(
        colorScheme = BrutalColorScheme,
        typography = Typography,
        content = content
    )
}
