package com.omar.musica.ui.model

import androidx.compose.runtime.Stable
import com.omar.musica.model.prefs.UserPreferences


@Stable
data class UserPreferencesUi(
    val librarySettings: LibrarySettingsUi,

    val uiSettings: UiSettingsUi,

    val playerSettings: PlayerSettingsUi
)

fun UserPreferences.toUiModel() =
    UserPreferencesUi(
        librarySettings.toLibrarySettingsUi(),
        uiSettings.toUiSettingsUi(),
        playerSettings.toPlayerSettingsUi()
    )