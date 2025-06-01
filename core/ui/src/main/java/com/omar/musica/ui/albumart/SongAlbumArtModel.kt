package com.omar.musica.ui.albumart

import android.net.Uri
import androidx.compose.runtime.Stable
import com.omar.musica.store.model.song.Song


/**
 * Data class containing the necessary information for Coil to enable it
 * extract cover arts from [Song]s
 */
@Stable
data class SongAlbumArtModel(
  val albumId: Long? = null,
  val uri: Uri
)

fun Song?.toSongAlbumArtModel() = if (this == null) SongAlbumArtModel(null, Uri.EMPTY) else SongAlbumArtModel(albumId, uri)