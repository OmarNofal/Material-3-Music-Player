package com.omar.musica.albums.viewmodel

import com.omar.musica.store.model.album.BasicAlbum


data class AlbumsScreenState(
    val albums: List<BasicAlbum>
)
