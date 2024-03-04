package com.omar.musica.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.omar.musica.database.entities.lyrics.LYRICS_ALBUM_COLUMN
import com.omar.musica.database.entities.lyrics.LYRICS_ARTIST_COLUMN
import com.omar.musica.database.entities.lyrics.LYRICS_TABLE
import com.omar.musica.database.entities.lyrics.LYRICS_TITLE_COLUMN
import com.omar.musica.database.entities.lyrics.LyricsEntity


@Dao
interface LyricsDao {

    @Insert
    suspend fun saveSongLyrics(lyricsEntity: LyricsEntity)

    @Query(
        "SELECT * FROM $LYRICS_TABLE " +
                "WHERE $LYRICS_TITLE_COLUMN = :title AND $LYRICS_ALBUM_COLUMN = :album " +
                "AND $LYRICS_ARTIST_COLUMN = :artist"
    )
    suspend fun getSongLyrics(
        title: String,
        album: String,
        artist: String
    ): LyricsEntity?

    @Query("DELETE FROM $LYRICS_TABLE")
    suspend fun deleteAll()

}