package com.omar.musica.model


/**
 * Settings applied to the library and main screen.
 */
data class LibrarySettings(

    /**
     * The order of the songs on the main screen
     */
    val songsSortOrder: String,

    /**
     * Whether to load the actual album art of the song or
     * to cache album arts of songs of the same album
     */
    val cacheAlbumCoverArt: Boolean,

    /**
     * Files in these folders should not appear in the app
     */
    val excludedFolders: List<String>
) {

    companion object {
        val DEFAULT_LIBRARY_SETTINGS = LibrarySettings("", true, listOf())
    }

}
