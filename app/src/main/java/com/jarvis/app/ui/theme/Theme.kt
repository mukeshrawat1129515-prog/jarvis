package com.jarvis.app.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color

private val DarkColors = darkColorScheme(
    primary = JarvisAccent,
    background = JarvisBg,
    surface = JarvisCard,
    onPrimary = Color.Black,
    onBackground = JarvisText,
    onSurface = JarvisText,
    onSurfaceVariant = JarvisMuted,
    error = Color(0xFFFF6B6B),
)

@Composable
fun JarvisTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = DarkColors,
        content = content,
    )
}