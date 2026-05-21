package com.sonzaix.streaming.presentation.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Accent,
    secondary = AccentSecondary,
    background = Background,
    surface = Surface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    surfaceVariant = CardColor,
    onSurfaceVariant = TextSecondary
)

@Composable
fun SonzaiXTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
