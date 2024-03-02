package com.omar.musica.store.model.tags

import android.graphics.Bitmap
import android.net.Uri
import com.omar.musica.model.song.ExtendedSongMetadata


data class SongTags(
    val uri: Uri,
    val artwork: Bitmap? = null,
    val metadata: ExtendedSongMetadata
)