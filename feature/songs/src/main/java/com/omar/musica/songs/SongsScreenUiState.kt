package com.omar.musica.songs

import com.omar.musica.model.Song
import com.omar.musica.songs.ui.SortOption


sealed interface SongsScreenUiState {

    data class Success(
        val songs: List<Song>,
        val sortOption: SortOption = SortOption.TITLE,
        val isSortedAscendingly: Boolean = true
    ): SongsScreenUiState
}