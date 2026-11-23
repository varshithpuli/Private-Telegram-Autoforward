package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val TeleSyncDarkColorScheme = darkColorScheme(
    primary = TeleSyncGreen,
    onPrimary = Color(0xFF041206),
    primaryContainer = TeleSyncGreenContainer,
    onPrimaryContainer = OnTeleSyncGreenContainer,
    secondary = TeleSyncGreenLight,
    onSecondary = Color(0xFF041206),
    secondaryContainer = ElevatedCarbonSurface,
    onSecondaryContainer = TextPrimaryWhite,
    tertiary = AmberWarning,
    background = ObsidianBackground,
    onBackground = TextPrimaryWhite,
    surface = CarbonSurfaceCard,
    onSurface = TextPrimaryWhite,
    surfaceVariant = ElevatedCarbonSurface,
    onSurfaceVariant = TextSecondaryMuted,
    outline = SubtleBorder,
    outlineVariant = DividerStroke,
    error = ErrorCrimson
)

@Composable
fun TeleSyncTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = false
                insetsController.isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = TeleSyncDarkColorScheme,
        typography = Typography,
        content = content
    )
}
