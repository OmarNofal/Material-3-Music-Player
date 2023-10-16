package com.omar.musica.songs

import androidx.compose.runtime.Immutable
import com.omar.musica.model.SortOption
import com.omar.musica.ui.model.SongUi


sealed interface SongsScreenUiState {

    @Immutable
    data class Success(
        val songs: List<SongUi>,
        val sortOption: SortOption = SortOption.TITLE,
        val isSortedAscendingly: Boolean = true
    ) : SongsScreenUiState
}