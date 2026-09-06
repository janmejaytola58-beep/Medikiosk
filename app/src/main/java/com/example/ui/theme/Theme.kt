package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme =
  darkColorScheme(
    primary = HealthBlueAccent,
    onPrimary = Color.White,
    primaryContainer = HealthNavy,
    onPrimaryContainer = Color.White,
    secondary = HealthBlueSoft,
    onSecondary = HealthNavy,
    background = Color(0xFF0F172A),
    onBackground = Color(0xFFF8FAFC),
    surface = Color(0xFF1E293B),
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0xFF334155),
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = Color(0xFF475569)
  )

private val LightColorScheme =
  lightColorScheme(
    primary = HealthNavy,
    onPrimary = Color.White,
    primaryContainer = HealthBlueSoft,
    onPrimaryContainer = HealthNavy,
    secondary = HealthBlueAccent,
    onSecondary = Color.White,
    secondaryContainer = HealthBlueSoft,
    onSecondaryContainer = HealthNavy,
    tertiary = PastelPeach,
    onTertiary = HealthNavy,
    tertiaryContainer = PastelPeach,
    background = HealthBackground,
    onBackground = HealthTextPrimary,
    surface = HealthCardBg,
    onSurface = HealthTextPrimary,
    surfaceVariant = HealthCardBg,
    onSurfaceVariant = HealthTextSecondary,
    outline = HealthCardBorder
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
