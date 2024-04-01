package com.omar.musica.store.model.album

import com.omar.musica.store.model.song.Song


data class AlbumSong(
    val song: Song,
    val trackNumber: Int? = null
)