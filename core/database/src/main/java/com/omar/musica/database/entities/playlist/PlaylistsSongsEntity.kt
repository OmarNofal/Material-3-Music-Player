package com.omar.musica.database.entities.playlist

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.omar.musica.database.entities.PLAYLIST_ID_COLUMN
import com.omar.musica.database.entities.PLAYLIST_SONG_ADD_TIME_COLUMN
import com.omar.musica.database.entities.PLAYLIST_SONG_ENTITY
import com.omar.musica.database.entities.SONG_URI_STRING_COLUMN


@Entity(tableName = PLAYLIST_SONG_ENTITY, primaryKeys = [PLAYLIST_ID_COLUMN, SONG_URI_STRING_COLUMN])
data class PlaylistsSongsEntity(
  @ColumnInfo(name = PLAYLIST_ID_COLUMN)
  val playlistId: Int,

  @ColumnInfo(name = SONG_URI_STRING_COLUMN)
  val songUriString: String,

  @ColumnInfo(name = PLAYLIST_SONG_ADD_TIME_COLUMN)
  val addedAt: Long,
)