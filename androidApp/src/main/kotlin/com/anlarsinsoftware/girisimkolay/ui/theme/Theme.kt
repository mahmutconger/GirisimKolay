package com.anlarsinsoftware.girisimkolay.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Define core colors based on HTML files
val NavyPrimary = Color(0xFF031632)
val NavyPrimaryContainer = Color(0xFF1A2B48)
val OnNavyPrimaryContainer = Color(0xFF8293B5)
val EmeraldSecondary = Color(0xFF006C49)
val EmeraldSecondaryContainer = Color(0xFF6CF8BB)
val OnEmeraldSecondaryContainer = Color(0xFF00714D)
val AppBackground = Color(0xFFFBF9FB)
val AppSurface = Color(0xFFFFFFFF)
val SurfaceContainerLowest = Color(0xFFFFFFFF)
val SurfaceContainerLow = Color(0xFFF5F3F6)
val SurfaceContainer = Color(0xFFEFEDF0)
val OutlineVariant = Color(0xFFC5C6CE)
val Outline = Color(0xFF75777E)
val OnSurfaceVariant = Color(0xFF44474D)
val ErrorColor = Color(0xFFBA1A1A)
val ErrorContainer = Color(0xFFFFDAD6)
val OnErrorContainer = Color(0xFF93000A)

private val LightColorScheme = lightColorScheme(
    primary = NavyPrimary,
    onPrimary = Color.White,
    primaryContainer = NavyPrimaryContainer,
    onPrimaryContainer = OnNavyPrimaryContainer,
    secondary = EmeraldSecondary,
    onSecondary = Color.White,
    secondaryContainer = EmeraldSecondaryContainer,
    onSecondaryContainer = OnEmeraldSecondaryContainer,
    background = AppBackground,
    onBackground = Color(0xFF1B1B1E),
    surface = AppSurface,
    onSurface = Color(0xFF1B1B1E),
    surfaceVariant = SurfaceContainer,
    onSurfaceVariant = OnSurfaceVariant,
    outline = Outline,
    outlineVariant = OutlineVariant,
    error = ErrorColor,
    onError = Color.White,
    errorContainer = ErrorContainer,
    onErrorContainer = OnErrorContainer
)

@Composable
fun GirisimKolayTheme(
    content: @Composable () -> Unit
) {
    // Keep it light scheme since user mockups are light/clean themed
    val colorScheme = LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
