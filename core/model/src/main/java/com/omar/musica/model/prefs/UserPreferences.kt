package com.omar.musica.model.prefs



data class UserPreferences(

    val librarySettings: LibrarySettings,

    val uiSettings: UiSettings,

    val playerSettings: PlayerSettings

)



enum class AppTheme {
    LIGHT, DARK, SYSTEM
}