package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val PaviDarkColorScheme = darkColorScheme(
    primary = CobraGreen,
    onPrimary = Color.Black,
    primaryContainer = CobraGreenDark,
    onPrimaryContainer = AmberGlow,
    secondary = RoyalGold,
    onSecondary = Color.Black,
    secondaryContainer = RoyalGoldDark,
    onSecondaryContainer = Color.White,
    tertiary = RubyRed,
    onTertiary = Color.White,
    background = JungleDark,
    onBackground = SurfaceText,
    surface = JungleSurface,
    onSurface = SurfaceText,
    surfaceVariant = JungleCard,
    onSurfaceVariant = SurfaceTextMuted,
    outline = JungleCardBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep consistent bold arcade branding
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = PaviDarkColorScheme,
        typography = Typography,
        content = content
    )
}
