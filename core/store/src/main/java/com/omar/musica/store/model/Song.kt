package com.omar.musica.store.model

import android.net.Uri
import com.omar.musica.model.song.BasicSongMetadata


/**
 * Represents a song from the point of view of Android.
 * A song contains a [Uri] to identify it in the MediaStore
 * and a file path to find it on disk, as well as the metadata of the song
 */
data class Song(
    val uri: Uri,
    val filePath: String,
    val metadata: BasicSongMetadata,
)
