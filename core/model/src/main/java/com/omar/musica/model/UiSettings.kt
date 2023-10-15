package com.omar.musica.model

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
)