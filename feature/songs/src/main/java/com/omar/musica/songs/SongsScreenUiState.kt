package com.omar.musica.songs

import com.omar.musica.model.Song


sealed interface SongsScreenUiState {

    data class Success(
        val songs: List<Song>
    )
}