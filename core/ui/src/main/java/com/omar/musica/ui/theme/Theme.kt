package com.omar.musica.ui.theme

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
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
import com.google.android.material.color.utilities.Scheme
import com.omar.musica.ui.albumart.LocalEfficientThumbnailImageLoader
import com.omar.musica.ui.albumart.LocalInefficientThumbnailImageLoader
import com.omar.musica.ui.albumart.efficientAlbumArtImageLoader
import com.omar.musica.ui.albumart.inefficientAlbumArtImageLoader
import com.omar.musica.ui.common.AppColorScheme
import com.omar.musica.ui.common.LocalAppColorScheme
import com.omar.musica.ui.common.LocalUserPreferences
import com.omar.musica.ui.model.AppThemeUi
import com.omar.musica.ui.model.UserPreferencesUi

val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

@SuppressLint("RestrictedApi")
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

    val userAccentColor = userPreferences.uiSettings.accentColor
    val context = LocalContext.current

    val lightColorScheme = when {
        userPreferences.uiSettings.isUsingDynamicColor
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
        -> dynamicLightColorScheme(context)
        else -> Scheme.light(userAccentColor).toLightComposeColorScheme()
    }

    val darkColorScheme = when {
        userPreferences.uiSettings.isUsingDynamicColor
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (userPreferences.uiSettings.blackBackgroundForDarkTheme)
                dynamicAmoledTheme(context)
            else
                dynamicDarkColorScheme(context)
        }

        else -> {
            val colorScheme = Scheme.dark(userAccentColor).toDarkComposeColorScheme()
            if (userPreferences.uiSettings.blackBackgroundForDarkTheme)
                colorScheme.copy(surface = Color.Black, background = Color.Black)
            else
                colorScheme
        }
    }

    val appThemeColorScheme = AppColorScheme(lightColorScheme, darkColorScheme)
    val colorScheme = if (darkTheme) darkColorScheme else lightColorScheme

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

    val efficientImageLoader = remember { context.efficientAlbumArtImageLoader() }
    val inefficientImageLoader = remember { context.inefficientAlbumArtImageLoader() }
    CompositionLocalProvider(
        LocalEfficientThumbnailImageLoader provides efficientImageLoader,
        LocalInefficientThumbnailImageLoader provides inefficientImageLoader,
        LocalAppColorScheme provides appThemeColorScheme
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

@SuppressLint("RestrictedApi")
fun Scheme.toLightComposeColorScheme() =
    lightColorScheme(
        primary = Color(primary),
        primaryContainer = Color(primaryContainer),
        onPrimary = Color(onPrimary),
        surface = Color(surface),
        surfaceVariant = Color(surfaceVariant),
        background = Color(background),
        secondary = Color(secondary),
        secondaryContainer = Color(secondaryContainer),
        onSecondary = Color(onSecondary),
        tertiary = Color(tertiary),
        tertiaryContainer = Color(tertiaryContainer),
        onTertiary = Color(onTertiary),
        onTertiaryContainer = Color(onTertiaryContainer),
        onPrimaryContainer = Color(onPrimaryContainer),
        inversePrimary = Color(inversePrimary),
        onSecondaryContainer = Color(onSecondaryContainer),
        onSurface = Color(onSurface),
        onBackground = Color(onBackground),
        onSurfaceVariant = Color(onSurfaceVariant),
    )

@SuppressLint("RestrictedApi")
fun Scheme.toDarkComposeColorScheme() =
    darkColorScheme(
        primary = Color(primary),
        primaryContainer = Color(primaryContainer),
        onPrimary = Color(onPrimary),
        surface = Color(surface),
        surfaceVariant = Color(surfaceVariant),
        background = Color(background),
        secondary = Color(secondary),
        secondaryContainer = Color(secondaryContainer),
        onSecondary = Color(onSecondary),
        tertiary = Color(tertiary),
        tertiaryContainer = Color(tertiaryContainer),
        onTertiary = Color(onTertiary),
        onTertiaryContainer = Color(onTertiaryContainer),
        onPrimaryContainer = Color(onPrimaryContainer),
        inversePrimary = Color(inversePrimary),
        onSecondaryContainer = Color(onSecondaryContainer),
        onSurface = Color(onSurface),
        onBackground = Color(onBackground),
        onSurfaceVariant = Color(onSurfaceVariant),
    )

@RequiresApi(Build.VERSION_CODES.S)
private fun dynamicAmoledTheme(context: Context): ColorScheme {
    val darkColorScheme = dynamicDarkColorScheme(context)
    return darkColorScheme.copy(background = Color.Black, surface = Color.Black)
}

@Composable
fun isAppInDarkTheme(): Boolean {
    return when(LocalUserPreferences.current.uiSettings.theme) {
        AppThemeUi.DARK -> true
        AppThemeUi.LIGHT -> false
        AppThemeUi.SYSTEM -> isSystemInDarkTheme()
    }
}