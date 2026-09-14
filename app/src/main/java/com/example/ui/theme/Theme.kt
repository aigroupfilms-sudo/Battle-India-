package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val BattleIndiaColorScheme = darkColorScheme(
    primary = TacticalOrange,
    onPrimary = Color.Black,
    primaryContainer = TacticalOrangeDark,
    onPrimaryContainer = Color.White,
    secondary = AmberGold,
    onSecondary = Color.Black,
    secondaryContainer = GunmetalMedium,
    onSecondaryContainer = AmberGold,
    tertiary = CyberCyan,
    onTertiary = Color.Black,
    background = DarkBlack,
    onBackground = TextHighContrast,
    surface = GunmetalDark,
    onSurface = TextHighContrast,
    surfaceVariant = GunmetalMedium,
    onSurfaceVariant = TextMuted,
    outline = GunmetalBorder,
    error = DangerRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = BattleIndiaColorScheme,
        typography = Typography,
        content = content
    )
}

