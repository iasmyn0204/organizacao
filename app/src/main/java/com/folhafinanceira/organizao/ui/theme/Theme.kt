package com.folhafinanceira.organizao.ui.theme

import android.app.Activity
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
    primary = LeafPrimaryDarkTheme,
    onPrimary = Color.Black,
    primaryContainer = LeafLightDarkTheme,
    onPrimaryContainer = Color.White,
    secondary = LeafAccent,
    onSecondary = Color.Black,
    background = LeafBackgroundDark,
    onBackground = Color(0xFFE0E2E0),
    surface = LeafSurfaceDark,
    onSurface = Color(0xFFE0E2E0),
    surfaceVariant = Color(0xFF2C382E),
    onSurfaceVariant = Color(0xFFC0C8C2)
)

private val LightColorScheme = lightColorScheme(
    primary = LeafPrimary,
    onPrimary = LeafOnPrimary,
    primaryContainer = LeafLight,
    onPrimaryContainer = LeafPrimaryDark,
    secondary = LeafMedium,
    onSecondary = Color.White,
    background = LeafBackgroundLight,
    onBackground = Color(0xFF1A1C1A),
    surface = LeafSurfaceLight,
    onSurface = Color(0xFF1A1C1A),
    surfaceVariant = Color(0xFFEAF2EA),
    onSurfaceVariant = Color(0xFF424942)
)

@Composable
fun OrganizaçãoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Default false to ensure consistent verde-folha branding across all devices
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
