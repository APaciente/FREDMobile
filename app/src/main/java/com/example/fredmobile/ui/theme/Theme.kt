package com.example.fredmobile.ui.theme

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

/**
 * Default dark color scheme for FRED Mobile when dynamic color is not used.
 */
private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

/**
 * Default light color scheme for FRED Mobile when dynamic color is not used.
 */
private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

/**
 * Top-level theme wrapper for FRED Mobile.
 *
 * Chooses between light/dark and dynamic color schemes, then applies them to
 * [MaterialTheme] for all composables in the app.
 *
 * @param darkTheme whether to use a dark color scheme.
 * @param dynamicColor whether to use Material 3 dynamic color on Android 12+.
 * @param largeText whether to scale typography up for accessibility.
 */
@Composable
fun FredmobileTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    largeText: Boolean = false,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    // Pick the color scheme (same logic as the M3 template)
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // Use your app's base Typography (defined in Type.kt as `val Typography = ...`)
    val baseTypography = Typography
    val scaledTypography =
        if (largeText) baseTypography.scaled(1.15f)   // about +15% larger text
        else baseTypography

    // Keep status bar color in sync with theme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = scaledTypography,
        content = content
    )
}

/**
 * Helper that returns a copy of this Typography with font sizes and line heights
 * scaled by [factor].
 *
 * Note: we fully qualify the type here so we do not clash with your `val Typography`
 * defined in Type.kt.
 */
private fun androidx.compose.material3.Typography.scaled(
    factor: Float
): androidx.compose.material3.Typography =
    this.copy(
        displayLarge = displayLarge.copy(
            fontSize = displayLarge.fontSize * factor,
            lineHeight = displayLarge.lineHeight * factor
        ),
        displayMedium = displayMedium.copy(
            fontSize = displayMedium.fontSize * factor,
            lineHeight = displayMedium.lineHeight * factor
        ),
        displaySmall = displaySmall.copy(
            fontSize = displaySmall.fontSize * factor,
            lineHeight = displaySmall.lineHeight * factor
        ),
        headlineLarge = headlineLarge.copy(
            fontSize = headlineLarge.fontSize * factor,
            lineHeight = headlineLarge.lineHeight * factor
        ),
        headlineMedium = headlineMedium.copy(
            fontSize = headlineMedium.fontSize * factor,
            lineHeight = headlineMedium.lineHeight * factor
        ),
        headlineSmall = headlineSmall.copy(
            fontSize = headlineSmall.fontSize * factor,
            lineHeight = headlineSmall.lineHeight * factor
        ),
        titleLarge = titleLarge.copy(
            fontSize = titleLarge.fontSize * factor,
            lineHeight = titleLarge.lineHeight * factor
        ),
        titleMedium = titleMedium.copy(
            fontSize = titleMedium.fontSize * factor,
            lineHeight = titleMedium.lineHeight * factor
        ),
        titleSmall = titleSmall.copy(
            fontSize = titleSmall.fontSize * factor,
            lineHeight = titleSmall.lineHeight * factor
        ),
        bodyLarge = bodyLarge.copy(
            fontSize = bodyLarge.fontSize * factor,
            lineHeight = bodyLarge.lineHeight * factor
        ),
        bodyMedium = bodyMedium.copy(
            fontSize = bodyMedium.fontSize * factor,
            lineHeight = bodyMedium.lineHeight * factor
        ),
        bodySmall = bodySmall.copy(
            fontSize = bodySmall.fontSize * factor,
            lineHeight = bodySmall.lineHeight * factor
        ),
        labelLarge = labelLarge.copy(
            fontSize = labelLarge.fontSize * factor,
            lineHeight = labelLarge.lineHeight * factor
        ),
        labelMedium = labelMedium.copy(
            fontSize = labelMedium.fontSize * factor,
            lineHeight = labelMedium.lineHeight * factor
        ),
        labelSmall = labelSmall.copy(
            fontSize = labelSmall.fontSize * factor,
            lineHeight = labelSmall.lineHeight * factor
        )
    )
