package com.omar.musica.albums.viewmodel

import androidx.compose.runtime.Stable
import com.omar.musica.store.model.album.AlbumWithSongs
import com.omar.musica.store.model.album.BasicAlbum


sealed interface AlbumDetailsScreenState {
    data object Loading : AlbumDetailsScreenState

    @Stable
    data class Loaded(
        val albumWithSongs: AlbumWithSongs,
        val otherAlbums: List<BasicAlbum>
    ) : AlbumDetailsScreenState
}