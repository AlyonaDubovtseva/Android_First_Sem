package com.example.roomapp.ui.theme
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = PastelPink,
    onPrimary = TextPrimary,
    primaryContainer = SoftPink,
    onPrimaryContainer = TextPrimary,

    secondary = LightRose,
    onSecondary = TextPrimary,
    secondaryContainer = PearlWhite,
    onSecondaryContainer = TextSecondary,

    tertiary = RosePink,
    onTertiary = TextPrimary,

    background = SoftPink,
    onBackground = TextPrimary,

    surface = PearlWhite,
    onSurface = TextPrimary,

    surfaceVariant = LightGray,
    onSurfaceVariant = TextSecondary,

    outline = BlushPink,
    outlineVariant = PastelPink.copy(alpha = 0.3f),
    error = ErrorRed,
    onError = Color.White,
    errorContainer = ErrorRed.copy(alpha = 0.1f),
    onErrorContainer = ErrorRed,
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}