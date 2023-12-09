package com.omar.musica.ui.theme

import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.omar.musica.ui.albumart.LocalEfficientThumbnailImageLoader
import com.omar.musica.ui.albumart.LocalInefficientThumbnailImageLoader
import com.omar.musica.ui.albumart.efficientAlbumArtImageLoader
import com.omar.musica.ui.albumart.inefficientAlbumArtImageLoader
import com.omar.musica.ui.model.AppThemeUi
import com.omar.musica.ui.model.UserPreferencesUi

val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

val amoledColorScheme = darkColorScheme(
    surface = Color.Black,
    background = Color.Black
)

@Composable
fun MusicaTheme(
    userPreferences: UserPreferencesUi,
    content: @Composable () -> Unit
) {
    val darkTheme = when (userPreferences.uiSettings.theme) {
        AppThemeUi.DARK -> true
        AppThemeUi.LIGHT -> false
        AppThemeUi.SYSTEM -> isSystemInDarkTheme()
    }

    val colorScheme = when {
        userPreferences.uiSettings.isUsingDynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) {
                if (userPreferences.uiSettings.blackBackgroundForDarkTheme)
                    dynamicAmoledTheme(context)
                else
                    dynamicDarkColorScheme(context)
            } else {
                dynamicLightColorScheme(context)
            }
        }

        darkTheme -> if (userPreferences.uiSettings.blackBackgroundForDarkTheme) amoledColorScheme else DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    SideEffect {
        val window = (view.context as Activity).window

        window.statusBarColor = Color.Transparent.toArgb()
        window.navigationBarColor = Color.Transparent.toArgb()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }

        val windowsInsetsController = WindowCompat.getInsetsController(window, view)

        windowsInsetsController.isAppearanceLightStatusBars = !darkTheme
        windowsInsetsController.isAppearanceLightNavigationBars = !darkTheme
    }

    val context = LocalContext.current
    val efficientImageLoader = remember { context.efficientAlbumArtImageLoader() }
    val inefficientImageLoader = remember { context.inefficientAlbumArtImageLoader() }
    CompositionLocalProvider(
        LocalEfficientThumbnailImageLoader provides efficientImageLoader,
        LocalInefficientThumbnailImageLoader provides inefficientImageLoader
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

private fun dynamicAmoledTheme(context: Context): ColorScheme {
    val darkColorScheme = dynamicDarkColorScheme(context)
    return darkColorScheme.copy(background = Color.Black, surface = Color.Black)
}