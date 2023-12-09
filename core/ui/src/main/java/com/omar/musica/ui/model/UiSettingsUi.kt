package com.omar.musica.ui.model

import androidx.compose.runtime.Stable
import com.omar.musica.model.AppTheme
import com.omar.musica.model.PlayerTheme
import com.omar.musica.model.UiSettings


@Stable
/**
 * Settings related to Ui of the Application
 */
data class UiSettingsUi(
    /**
     * Light or Dark or System
     */
    val theme: AppThemeUi,

    /**
     * Android 12+ dynamic theming
     */
    val isUsingDynamicColor: Boolean,

    val playerThemeUi: PlayerThemeUi,

    val blackBackgroundForDarkTheme: Boolean
)

@Stable
enum class PlayerThemeUi {
    SOLID, BLUR
}

fun PlayerThemeUi.toPlayerTheme() =
    PlayerTheme.valueOf(this.toString())

fun PlayerTheme.toPlayerThemeUi() =
    PlayerThemeUi.valueOf(this.toString())



@Stable
enum class AppThemeUi {
    SYSTEM, LIGHT, DARK
}

fun AppTheme.toAppThemeUi() =
    AppThemeUi.valueOf(this.toString())

fun AppThemeUi.toAppTheme() =
    AppTheme.valueOf(this.toString())

fun UiSettings.toUiSettingsUi() =
    UiSettingsUi(theme.toAppThemeUi(), isUsingDynamicColor, playerTheme.toPlayerThemeUi(), blackBackgroundForDarkTheme)