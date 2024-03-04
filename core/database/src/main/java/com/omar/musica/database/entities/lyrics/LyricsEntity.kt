package com.omar.musica.database.entities.lyrics

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


const val LYRICS_TITLE_COLUMN = "title"
const val LYRICS_ALBUM_COLUMN = "album"
const val LYRICS_ARTIST_COLUMN = "artist"
const val LYRICS_PLAIN_LYRICS_COLUMN = "plain_lyrics"
const val LYRICS_SYNCED_LYRICS_COLUMN = "synced_lyrics"

const val LYRICS_TABLE = "lyrics"

@Entity(tableName = LYRICS_TABLE, indices = [Index(LYRICS_ARTIST_COLUMN, LYRICS_ALBUM_COLUMN)])
data class LyricsEntity(
    @PrimaryKey(autoGenerate = true)            val id: Int,
    @ColumnInfo(LYRICS_TITLE_COLUMN)            val title: String,
    @ColumnInfo(LYRICS_ALBUM_COLUMN)            val album: String,
    @ColumnInfo(LYRICS_ARTIST_COLUMN)           val artist: String,
    @ColumnInfo(LYRICS_PLAIN_LYRICS_COLUMN)     val plainLyrics: String,
    @ColumnInfo(LYRICS_SYNCED_LYRICS_COLUMN)    val syncedLyrics: String
)