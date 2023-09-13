package com.omar.musica.ui.model

import androidx.core.net.toUri
import com.omar.musica.model.Song

data class SongUi(
    val fileName: String,
    val title: String,
    val artist: String?,
    val length: Long, // millis
    val size: Long, // bytes
    val album: String?,
    val location: String,
    val uriString: String,
    val albumId: Long? = null,
    val albumArtUri: String? = null
)

val SongUi.uri
    get() = uriString.toUri()

fun List<SongUi>.toSongModels() =
    map { it.toSongModel() }

fun SongUi.toSongModel() =
    Song(
        fileName, title, artist, length, size, album, location, uriString, albumId, albumArtUri
    )

fun List<Song>.toUiSongModels() =
    map { it.toUiSongModel() }

fun Song.toUiSongModel() =
    SongUi(
        fileName, title, artist, length, size, album, location, uriString, albumId, albumArtUri
    )
