package com.omar.musica.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity


@Entity(tableName = PLAYLIST_SONG_ENTITY, primaryKeys = [PLAYLIST_ID_COLUMN, SONG_URI_STRING_COLUMN])
data class PlaylistsSongsEntity(
    @ColumnInfo(name = PLAYLIST_ID_COLUMN)
    val playlistId: Int,

    @ColumnInfo(name = SONG_URI_STRING_COLUMN)
    val songUriString: String
)