package com.omar.musica.database.model

import androidx.room.Embedded
import androidx.room.Relation
import com.omar.musica.database.entities.PLAYLIST_ID_COLUMN
import com.omar.musica.database.entities.playlist.PlaylistEntity
import com.omar.musica.database.entities.playlist.PlaylistsSongsEntity

data class PlaylistWithSongsUri(
  @Embedded
  val playlistEntity: PlaylistEntity,

  @Relation(entity = PlaylistsSongsEntity::class, parentColumn = PLAYLIST_ID_COLUMN, entityColumn = PLAYLIST_ID_COLUMN)
  val songUris: List<PlaylistsSongsEntity>
)
