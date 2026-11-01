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

private val DarkColorScheme = darkColorScheme(
    primary = ForestGreenPrimaryDark,
    onPrimary = Color(0xFF003914),
    primaryContainer = Color(0xFF005322),
    onPrimaryContainer = Color(0xFF86F9A2),
    secondary = AquaBlueDark,
    onSecondary = Color(0xFF00344F),
    secondaryContainer = Color(0xFF004D72),
    onSecondaryContainer = Color(0xFFC7E7FF),
    tertiary = GoldenPaddy,
    background = AgriBackgroundDark,
    onBackground = TextPrimaryLight,
    surface = AgriSurfaceDark,
    onSurface = TextPrimaryLight,
    surfaceVariant = AgriSurfaceVariantDark,
    onSurfaceVariant = TextSecondaryLight,
    outline = AgriBorderDark
)

private val LightColorScheme = lightColorScheme(
    primary = ForestGreenPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDCFCE7),
    onPrimaryContainer = Color(0xFF052E16),
    secondary = AquaBlue,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE0F2FE),
    onSecondaryContainer = Color(0xFF0369A1),
    tertiary = GoldenPaddy,
    background = AgriBackgroundLight,
    onBackground = TextPrimaryDark,
    surface = AgriSurfaceLight,
    onSurface = TextPrimaryDark,
    surfaceVariant = AgriSurfaceVariantLight,
    onSurfaceVariant = TextSecondaryDark,
    outline = AgriBorderLight
)

@Composable
fun AquaSmartTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep branded agricultural palette consistent
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
