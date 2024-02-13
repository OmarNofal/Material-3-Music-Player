package com.omar.musica.model.prefs

/**
 * Settings related to Ui of the Application
 */
data class UiSettings(
    /**
     * Light or Dark or System
     */
    val theme: AppTheme,

    /**
     * Android 12+ dynamic theming
     */
    val isUsingDynamicColor: Boolean,

    /**
     * Solid or blur player theme
     */
    val playerTheme: PlayerTheme,

    /**
     * Useful for Amoled Screens
     */
    val blackBackgroundForDarkTheme: Boolean
)

enum class PlayerTheme {
    SOLID, BLUR
}