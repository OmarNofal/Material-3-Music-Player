package com.omar.musica.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color



val LightBlueColorScheme = lightColorScheme(
    primary = Color(0xFF1A73E8),              // Brandeis Blue
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD2E3FC),     // Light blue for buttons, etc.
    onPrimaryContainer = Color(0xFF001E41),

    secondary = Color(0xFFFFB300),            // Selective Yellow
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFFFFE082),
    onSecondaryContainer = Color(0xFF201B00),

    background = Color(0xFFFFFFFF),           // Ivory
    onBackground = Color(0xFF0D1B2A),         // Rich Black

    surface = Color(0xFFF0F8FF),
    onSurface = Color(0xFF0D1B2A),
    surfaceVariant = Color(0xFFE6EDF4),       // Light surface background for nav, cards
    onSurfaceVariant = Color(0xFF374D63),
    surfaceContainer = Color(0xFFF9F9F5),

    error = Color(0xFFB02E0C),                // Rufous
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD4),
    onErrorContainer = Color(0xFF410001),

    inversePrimary = Color(0xFF90CAF9),
    inverseSurface = Color(0xFF0D1B2A),
    inverseOnSurface = Color(0xFFF9FBF2),

    outline = Color(0xFF8A99A8),
    outlineVariant = Color(0xFFDDE4EB),

    surfaceTint = Color.Transparent,          // IMPORTANT to override the purple
    scrim = Color(0x66000000),
)


val DarkBlueColorScheme = darkColorScheme(
    primary = Color(0xFF1A73E8),            // Your Brandeis Blue
    onPrimary = Color.White,
    primaryContainer = Color(0xFF174A8C),   // Darker blue for buttons/cards
    onPrimaryContainer = Color.White,

    secondary = Color(0xFFFFB300),          // Selective Yellow
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF7A5E00),
    onSecondaryContainer = Color.White,

    background = Color(0xFF0D1B2A),         // Rich Black
    onBackground = Color(0xFFF9FBF2),       // Ivory

    surface = Color(0xFF142A3D),
    onSurface = Color(0xFFF9FBF2),
    surfaceVariant = Color(0xFF1F3245),     // For cards, nav bar bg
    onSurfaceVariant = Color(0xFFDDE4EB),

    error = Color(0xFFB02E0C),              // Rufous
    onError = Color.White,
    errorContainer = Color(0xFF5C1300),
    onErrorContainer = Color.White,

    inversePrimary = Color(0xFF90CAF9),
    inverseSurface = Color(0xFFF9FBF2),
    inverseOnSurface = Color(0xFF0D1B2A),

    outline = Color(0xFF5E748A),
    outlineVariant = Color(0xFF374D63),

    surfaceTint = Color(0xFF000000),        // ← this is VERY important!
    scrim = Color(0x66000000),
)

