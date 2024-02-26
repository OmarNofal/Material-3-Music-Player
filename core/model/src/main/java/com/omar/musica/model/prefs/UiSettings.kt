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
    val blackBackgroundForDarkTheme: Boolean,

    /**
     * Whether to pin MiniPlayer or show it as a FAB
     */
    val miniPlayerMode: MiniPlayerMode = MiniPlayerMode.PINNED
)

enum class PlayerTheme {
    SOLID, BLUR
}