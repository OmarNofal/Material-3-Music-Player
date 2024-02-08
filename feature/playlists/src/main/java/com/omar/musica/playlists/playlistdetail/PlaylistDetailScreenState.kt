package com.omar.musica.playlists.playlistdetail

import com.omar.musica.ui.model.SongUi

sealed interface PlaylistDetailScreenState {

    data object Loading: PlaylistDetailScreenState

    data class Loaded(
        val name: String,
        val songs: List<SongUi>,
        val numberOfSongs: Int = songs.size
    ): PlaylistDetailScreenState

    data object Deleted: PlaylistDetailScreenState

}