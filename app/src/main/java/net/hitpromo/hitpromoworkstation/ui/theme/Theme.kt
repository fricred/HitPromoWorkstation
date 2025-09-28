package net.hitpromo.hitpromoworkstation.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Industrial Dark Color Scheme - For night shifts and low-light environments
private val IndustrialDarkColorScheme = darkColorScheme(
    primary = HitPromoOrange,
    onPrimary = TextOnColor,
    primaryContainer = HitPromoOrangeDark,
    onPrimaryContainer = TextOnColor,

    secondary = HitPromoBlue,
    onSecondary = TextOnColor,
    secondaryContainer = IndustrialGray,
    onSecondaryContainer = TextOnDark,

    tertiary = InfoBlue,
    onTertiary = TextOnColor,
    tertiaryContainer = IndustrialGray,
    onTertiaryContainer = TextOnDark,

    background = IndustrialDark,
    onBackground = TextOnDark,
    surface = SurfaceDark,
    onSurface = TextOnDark,
    surfaceVariant = IndustrialGray,
    onSurfaceVariant = TextOnDark,

    error = AlertRed,
    onError = TextOnColor,
    errorContainer = AlertRed,
    onErrorContainer = TextOnColor,

    outline = BorderDark,
    outlineVariant = BorderLight,
    scrim = IndustrialDark
)

// Industrial Light Color Scheme - Primary scheme for production floor
private val IndustrialLightColorScheme = lightColorScheme(
    primary = HitPromoOrange,
    onPrimary = TextOnColor,
    primaryContainer = IndustrialWhite,
    onPrimaryContainer = TextPrimary,

    secondary = HitPromoBlue,
    onSecondary = TextOnColor,
    secondaryContainer = SurfaceSecondary,
    onSecondaryContainer = TextPrimary,

    tertiary = InfoBlue,
    onTertiary = TextOnColor,
    tertiaryContainer = SurfaceSecondary,
    onTertiaryContainer = TextPrimary,

    background = IndustrialWhite,
    onBackground = TextPrimary,
    surface = SurfacePrimary,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceSecondary,
    onSurfaceVariant = TextSecondary,

    error = AlertRed,
    onError = TextOnColor,
    errorContainer = SurfaceSecondary,
    onErrorContainer = AlertRed,

    outline = BorderLight,
    outlineVariant = BorderAccent,
    scrim = TextSecondary
)

@Composable
fun HitPromoWorkstationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Disable dynamic color for consistent industrial appearance
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        // Force industrial color scheme for consistency in production environment
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> IndustrialDarkColorScheme
        else -> IndustrialLightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}