package com.omar.musica.model

data class UserPreferences(

    /**
     * The order of the songs on the main screen
     */
    val songsSortOrder: String,

    /**
     * Light or Dark or System
     */
    val theme: AppTheme,

    /**
     * Whether to load the actual album art of the song or
     * to cache album arts of songs of the same album
     */
    val cacheAlbumCoverArt: Boolean,

    /**
     * Whether to use Android 12+ dynamic theming
     */
    val isUsingDynamicColor: Boolean,

    /**
     * Files in these folders should not appear in the app
     */
    val excludedFolders: List<String>,

    /**
     * Songs shorter than this will not appear
     */
    val minDurationMillis: Long,

)




enum class AppTheme {
    LIGHT, DARK, SYSTEM
}