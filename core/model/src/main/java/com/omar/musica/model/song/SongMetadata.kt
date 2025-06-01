package com.omar.musica.model.song

/**
 * Represents basic song metadata used commonly through the App
 * contains title, artist, album, duration, and file size
 */
data class BasicSongMetadata(
  val title: String,
  val artistName: String?,
  val albumName: String?,
  val durationMillis: Long,
  val sizeBytes: Long,
  val trackNumber: Int? = null
)

/**
 * Contains more song metadata including everything in [BasicSongMetadata] plus
 * album artist name, track number, lyrics, disc number, genre, composer and year
 */
data class ExtendedSongMetadata(
  val basicSongMetadata: BasicSongMetadata,
  val albumArtist: String,
  val composer: String,
  val trackNumber: String,
  val discNumber: String,
  val genre: String,
  val year: String,
  val lyrics: String
)