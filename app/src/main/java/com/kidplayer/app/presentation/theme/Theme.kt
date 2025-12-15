package com.kidplayer.app.presentation.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Kid-friendly color scheme - always use light theme for consistency
private val KidPlayerColorScheme = lightColorScheme(
    primary = BrightBlue,
    onPrimary = TextOnPrimary,
    primaryContainer = BrightBlue.copy(alpha = 0.2f),
    onPrimaryContainer = BrightBlue,

    secondary = CheerfulOrange,
    onSecondary = TextOnPrimary,
    secondaryContainer = CheerfulOrange.copy(alpha = 0.2f),
    onSecondaryContainer = CheerfulOrange,

    tertiary = FriendlyGreen,
    onTertiary = TextOnPrimary,
    tertiaryContainer = FriendlyGreen.copy(alpha = 0.2f),
    onTertiaryContainer = FriendlyGreen,

    background = LightBackground,
    onBackground = TextPrimary,

    surface = SurfaceWhite,
    onSurface = TextPrimary,
    surfaceVariant = LightGray,
    onSurfaceVariant = TextSecondary,

    error = GentleRed,
    onError = TextOnPrimary,
    errorContainer = GentleRed.copy(alpha = 0.2f),
    onErrorContainer = GentleRed,

    outline = LightGray,
    outlineVariant = LightGray.copy(alpha = 0.5f)
)

@Composable
fun KidPlayerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Force light theme for kids - consistent and bright UI
    forceLightTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = KidPlayerColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = KidPlayerTypography,
        content = content
    )
}
