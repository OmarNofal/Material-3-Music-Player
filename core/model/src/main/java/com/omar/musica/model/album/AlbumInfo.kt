package com.omar.musica.model.album

data class BasicAlbumInfo(
  val id: Int,
  val name: String,
  val artist: String,
  val numberOfSongs: Int
)

data class ExtendedAlbumInfo(
  val name: String,
  val artist: String,
  val numberOfSongs: Int,
  val totalDurationMillis: Long,
  val genre: String
)