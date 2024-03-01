package com.omar.musica.tageditor

import android.net.Uri


data class SongMetadata(
    val songUri: Uri,
    val title: String,
    val artistName: String,
    val albumName: String,
    val albumArtist: String,
    val trackNumber: Int,
    val discNumber: Int,
    val genre: String,
)