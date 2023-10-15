package com.omar.musica.ui.model

import androidx.compose.runtime.Stable
import com.omar.musica.model.LibrarySettings


@Stable
/**
 * Settings applied to the library and main screen.
 */
data class LibrarySettingsUi(

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
)


fun LibrarySettings.toLibrarySettingsUi() =
    LibrarySettingsUi(
        songsSortOrder, cacheAlbumCoverArt, excludedFolders
    )