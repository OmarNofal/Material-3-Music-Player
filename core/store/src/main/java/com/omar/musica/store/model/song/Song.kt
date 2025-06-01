package com.omar.musica.store.model.song

import android.net.Uri
import com.omar.musica.model.song.BasicSongMetadata


/**
 * Represents a song from the point of view of Android.
 * A song contains a [Uri] to identify it in the MediaStore
 * and a file path to find it on disk, album id, as well as the metadata of the song
 */
data class Song(
  val id: Long,
  val uri: Uri,
  val filePath: String,
  val albumId: Long?,
  val metadata: BasicSongMetadata,
)
