package com.omar.musica.ui.model

import androidx.compose.runtime.Stable
import androidx.core.net.toUri
import com.omar.musica.model.song.Song

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
