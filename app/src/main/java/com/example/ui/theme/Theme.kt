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
    primary = CyberCyan,
    onPrimary = MidnightDark,
    primaryContainer = SurfaceDarkVariant,
    onPrimaryContainer = CyberCyan,
    secondary = CyberCyanDim,
    onSecondary = MidnightDark,
    secondaryContainer = OutlineDark,
    onSecondaryContainer = TextWhite,
    tertiary = ShieldGreen,
    onTertiary = MidnightDark,
    background = MidnightDark,
    onBackground = TextWhite,
    surface = SurfaceDark,
    onSurface = TextWhite,
    surfaceVariant = SurfaceDarkVariant,
    onSurfaceVariant = TextGray,
    outline = OutlineDark,
    error = ShieldRed,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = Color.White,
    primaryContainer = LightSurfaceVariant,
    onPrimaryContainer = LightPrimary,
    secondary = DeepSapphire,
    onSecondary = Color.White,
    secondaryContainer = LightSurfaceVariant,
    onSecondaryContainer = LightTextDark,
    tertiary = ShieldGreen,
    onTertiary = Color.White,
    background = LightBg,
    onBackground = LightTextDark,
    surface = LightSurface,
    onSurface = LightTextDark,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = TextMuted,
    outline = LightOutline,
    error = ShieldRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
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
