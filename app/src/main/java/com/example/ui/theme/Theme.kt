package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = TealStarlight,
    onPrimary = Color(0xFF021E1A),
    primaryContainer = Color(0xFF0F3832),
    onPrimaryContainer = Color(0xFF99F6E4),
    secondary = CyanNebula,
    onSecondary = Color(0xFF032238),
    tertiary = AmberGargantua,
    onTertiary = Color(0xFF381E00),
    background = SpaceBlack,
    onBackground = TextPrimary,
    surface = SpaceDeepNavy,
    onSurface = TextPrimary,
    surfaceVariant = SpaceSurface,
    onSurfaceVariant = TextSecondary,
    outline = SpaceBorder
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
