package com.omar.musica.store.model.playlist

import com.omar.musica.model.playlist.PlaylistInfo
import com.omar.musica.store.model.song.Song


data class Playlist(
    val playlistInfo: PlaylistInfo,
    val songs: List<Song>
)