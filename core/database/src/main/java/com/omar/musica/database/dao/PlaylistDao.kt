package com.omar.musica.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.omar.musica.database.entities.PLAYLIST_ENTITY
import com.omar.musica.database.entities.PLAYLIST_ID_COLUMN
import com.omar.musica.database.entities.PLAYLIST_NAME_COLUMN
import com.omar.musica.database.entities.PLAYLIST_SONG_ENTITY
import com.omar.musica.database.entities.PlaylistEntity
import com.omar.musica.database.entities.SONG_URI_STRING_COLUMN
import com.omar.musica.database.model.PlaylistInfoWithNumberOfSongs
import com.omar.musica.database.model.PlaylistWithSongsUri
import kotlinx.coroutines.flow.Flow


@Dao
interface PlaylistDao {

    @Query(
        "SELECT P.*, S.song_uri FROM $PLAYLIST_ENTITY P JOIN $PLAYLIST_SONG_ENTITY S " +
                "ON P.${PLAYLIST_ID_COLUMN} = S.${PLAYLIST_ID_COLUMN} WHERE P.$PLAYLIST_ID_COLUMN = :playlistId"
    )
    fun getPlaylistWithSongsFlow(playlistId: Int): Flow<PlaylistWithSongsUri>

    @Insert
    suspend fun createPlaylist(playlistEntity: PlaylistEntity)

    @Transaction
    suspend fun deletePlaylistWithSongs(playlistId: Int) {
        deletePlaylistEntity(playlistId)
        deletePlaylistSongs(playlistId)
    }

    @Query(
        "DELETE FROM $PLAYLIST_ENTITY WHERE $PLAYLIST_ID_COLUMN = :id"
    )
    suspend fun deletePlaylistEntity(id: Int)

    @Query(
        "DELETE FROM $PLAYLIST_SONG_ENTITY WHERE $PLAYLIST_ID_COLUMN = :playlistId"
    )
    suspend fun deletePlaylistSongs(playlistId: Int)


    @Query(
        "UPDATE $PLAYLIST_ENTITY SET $PLAYLIST_NAME_COLUMN = :newName " +
                "WHERE $PLAYLIST_ID_COLUMN = :playlistId"
    )
    suspend fun renamePlaylist(playlistId: Int, newName: String)


    @Query(
        "SELECT P.*, COUNT(S.$SONG_URI_STRING_COLUMN) as 'numberOfSongs' FROM $PLAYLIST_ENTITY P LEFT OUTER JOIN $PLAYLIST_SONG_ENTITY S " +
                "ON P.${PLAYLIST_ID_COLUMN} = S.${PLAYLIST_ID_COLUMN} GROUP BY P.$PLAYLIST_ID_COLUMN"
    )
    fun getPlaylistsInfoFlow(): Flow<List<PlaylistInfoWithNumberOfSongs>>

}