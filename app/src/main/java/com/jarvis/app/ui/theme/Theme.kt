package com.jarvis.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColors = darkColorScheme(
    primary = Color(0xFF38BDF8),
    background = Color(0xFF05080F),
    surface = Color(0xFF0D1422),
    onPrimary = Color.Black,
    onBackground = Color(0xFFE6F0FF),
    onSurface = Color(0xFFE6F0FF),
    onSurfaceVariant = Color(0xFF7F93B5),
    error = Color(0xFFFF6B6B),
)

@Composable
fun JarvisTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = DarkColors, content = content)
}