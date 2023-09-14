package com.omar.musica.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = PLAYLIST_ENTITY)
data class PlaylistEntity(
    @ColumnInfo(name = PLAYLIST_ID_COLUMN)
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = PLAYLIST_NAME_COLUMN)
    val name: String,
)
