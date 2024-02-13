package com.omar.musica.ui.model

import androidx.compose.runtime.Stable
import androidx.core.net.toUri
import com.omar.musica.model.Song

@Stable
data class SongUi(
    val fileName: String,
    val title: String,
    val artist: String?,
    val length: Long, // millis
    val size: Long, // bytes
    val album: String?,
    val location: String,
    val uriString: String,
    val albumId: Long? = null
)

val SongUi.uri
    get() = uriString.toUri()

fun List<SongUi>.toSongModels() =
    map { it.toSongModel() }

fun SongUi.toSongModel() =
    Song(
        fileName, title, artist, length, size, album, location, uriString, albumId
    )

fun List<Song>.toUiSongModels() =
    map { it.toUiSongModel() }

fun Song.toUiSongModel() =
    SongUi(
        fileName, title, artist, length, size, album, location, uriString, albumId
    )
