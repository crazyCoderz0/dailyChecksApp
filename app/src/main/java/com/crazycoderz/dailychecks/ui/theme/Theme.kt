package com.crazycoderz.dailychecks.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF2A5D67),
    onPrimary = Color.White,
    secondary = Color(0xFF4D7368),
    background = Color(0xFFF4F7F6),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFD9E7E4),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF8CC7D2),
    secondary = Color(0xFF9FC8BC),
    background = Color(0xFF101617),
    surface = Color(0xFF162022),
    surfaceVariant = Color(0xFF314245),
)

@Composable
fun DailyChecksTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = Typography,
        content = content,
    )
}

