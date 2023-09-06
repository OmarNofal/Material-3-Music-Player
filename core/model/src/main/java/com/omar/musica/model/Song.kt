package com.omar.musica.model

data class Song(
    val fileName: String,
    val title: String,
    val artist: String,
    val length: Long, // millis
    val size: Long, // bytes
    val album: String,
    val location: String,
    val uriString: String
)
