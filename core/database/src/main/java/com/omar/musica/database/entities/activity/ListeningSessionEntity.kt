package com.omar.musica.database.entities.activity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.omar.musica.database.entities.ALBUM_NAME_COLUMN
import com.omar.musica.database.entities.DURATION_SECONDS_COLUMN
import com.omar.musica.database.entities.LISTENING_SESSION_TABLE
import com.omar.musica.database.entities.SONG_NAME_COLUMN
import com.omar.musica.database.entities.SONG_URI_STRING_COLUMN
import com.omar.musica.database.entities.START_TIME_COLUMN


@Entity(LISTENING_SESSION_TABLE)
data class ListeningSessionEntity(

    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    val id: Long,

    @ColumnInfo(name = SONG_URI_STRING_COLUMN)
    val songUri: String,

    @ColumnInfo(name = SONG_NAME_COLUMN)
    val songName: String,

    @ColumnInfo(name = ALBUM_NAME_COLUMN)
    val albumName: String,

    @ColumnInfo(name = START_TIME_COLUMN)
    val startTimeEpoch: Long,

    @ColumnInfo(name = DURATION_SECONDS_COLUMN)
    val durationSeconds: Int,
)