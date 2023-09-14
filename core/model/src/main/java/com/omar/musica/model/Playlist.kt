package com.omar.musica.model



data class Playlist(
    val playlistInfo: PlaylistInfo,
    val songs: List<Song>
)

data class PlaylistInfo(
    val id: Int,
    val name: String,
    val numberOfSongs: Int,
)

