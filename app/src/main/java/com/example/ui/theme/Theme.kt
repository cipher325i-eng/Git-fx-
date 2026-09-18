package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = NeonGreen,
    onPrimary = CyberBlack,
    primaryContainer = Color(0xFF003814),
    onPrimaryContainer = NeonGreen,
    secondary = AccentCyan,
    onSecondary = CyberBlack,
    tertiary = AccentGold,
    background = CyberBlack,
    onBackground = TextPrimary,
    surface = CyberDarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = CyberCardBg,
    onSurfaceVariant = TextSecondary,
    outline = CyberCardBorder,
    outlineVariant = CyberCardBorderActive
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Default to cyber dark theme
  dynamicColor: Boolean = false, // Keep cyber neon identity
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = DarkColorScheme,
    typography = Typography,
    content = content
  )
}
