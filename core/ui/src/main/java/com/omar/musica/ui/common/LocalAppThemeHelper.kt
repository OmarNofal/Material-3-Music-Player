package com.omar.musica.ui.common

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.staticCompositionLocalOf
import java.lang.IllegalStateException


/**
 * Provides dark and light color schemes of the app.
 * This is mainly to be provided for NowPlayingScreen
 * because the color scheme it uses can be different from the app's
 * normal color scheme
 */
data class AppColorScheme(
    val lightColorScheme: ColorScheme,
    val darkColorScheme: ColorScheme
)


val LocalAppColorScheme = staticCompositionLocalOf<AppColorScheme> {
    throw IllegalStateException("AppColorScheme not provided")
}