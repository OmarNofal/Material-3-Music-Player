package com.omar.musica.store.model.album

import com.omar.musica.model.album.BasicAlbumInfo
import com.omar.musica.store.model.song.Song


data class BasicAlbum(
    val albumInfo: BasicAlbumInfo,
    /**
     * Used to get the album cover art
     */
    val firstSong: Song? = null
)

data class AlbumWithSongs(
    val albumInfo: BasicAlbumInfo,
    val songs: List<AlbumSong>
)