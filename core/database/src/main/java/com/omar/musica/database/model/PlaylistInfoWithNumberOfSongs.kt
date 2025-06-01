package com.omar.musica.database.model

import androidx.room.Embedded
import com.omar.musica.database.entities.playlist.PlaylistEntity

data class PlaylistInfoWithNumberOfSongs (
  @Embedded
  val playlistEntity: PlaylistEntity,
  val numberOfSongs: Int
)