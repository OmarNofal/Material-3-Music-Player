package com.omar.musica.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.omar.musica.database.entities.PLAYLIST_ENTITY
import com.omar.musica.database.entities.PLAYLIST_ID_COLUMN
import com.omar.musica.database.entities.PLAYLIST_NAME_COLUMN
import com.omar.musica.database.entities.PLAYLIST_SONG_ENTITY
import com.omar.musica.database.entities.playlist.PlaylistEntity
import com.omar.musica.database.entities.playlist.PlaylistsSongsEntity
import com.omar.musica.database.entities.SONG_URI_STRING_COLUMN
import com.omar.musica.database.model.PlaylistInfoWithNumberOfSongs
import com.omar.musica.database.model.PlaylistWithSongsUri
import com.omar.musica.model.playlist.PlaylistInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {

  @Query(
    "SELECT P.*, S.song_uri, S.playlist_song_add_time FROM $PLAYLIST_ENTITY P LEFT OUTER JOIN $PLAYLIST_SONG_ENTITY S " +
      "ON P.${PLAYLIST_ID_COLUMN} = S.${PLAYLIST_ID_COLUMN} WHERE P.$PLAYLIST_ID_COLUMN = :playlistId"
  )
  fun getPlaylistWithSongsFlow(playlistId: Int): Flow<PlaylistWithSongsUri>

  @Query(
    "SELECT S.song_uri FROM $PLAYLIST_SONG_ENTITY S WHERE $PLAYLIST_ID_COLUMN = :playlistId"
  )
  suspend fun getPlaylistSongs(playlistId: Int): List<String>

  @Query(
    "SELECT EXISTS (SELECT 1 FROM $PLAYLIST_SONG_ENTITY S WHERE $PLAYLIST_ID_COLUMN = ${PlaylistInfo.FAVORITE_PLAYLIST_ID} " +
      "AND S.$SONG_URI_STRING_COLUMN = :songUri LIMIT 1)"
  )
  fun isFavoriteFlow(songUri: String): Flow<Boolean>

  @Insert
  suspend fun createPlaylist(playlistEntity: PlaylistEntity): Long

  @Transaction
  suspend fun deletePlaylistWithSongs(playlistId: Int) {
    deletePlaylistEntity(playlistId)
    deletePlaylistSongs(playlistId)
  }

  @Transaction
  suspend fun createPlaylistAndAddSongs(name: String, songsUris: List<String>) {
    val newId = createPlaylist(PlaylistEntity(name = name)).toInt()
    insertSongsToPlaylist(songsUris.map { PlaylistsSongsEntity(newId, it, System.currentTimeMillis()) })
  }

  @Query(
    "DELETE FROM $PLAYLIST_ENTITY WHERE $PLAYLIST_ID_COLUMN = :id"
  )
  suspend fun deletePlaylistEntity(id: Int)

  @Query(
    "DELETE FROM $PLAYLIST_SONG_ENTITY WHERE $PLAYLIST_ID_COLUMN = :playlistId"
  )
  suspend fun deletePlaylistSongs(playlistId: Int)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertSongsToPlaylist(playlistsSongsEntity: List<PlaylistsSongsEntity>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertSongToPlaylist(playlistSongEntity: PlaylistsSongsEntity)

  suspend fun insertToRecentPlaylist(songUri: String) {
    insertSongToPlaylist(
      PlaylistsSongsEntity(
        playlistId = PlaylistInfo.RECENT_PLAYED_PLAYLIST_ID,
        songUriString = songUri,
        addedAt = System.currentTimeMillis()
      )
    )
  }

  suspend fun insertToFavorites(songUri: String) {
    insertSongToPlaylist(
      PlaylistsSongsEntity(
        playlistId = PlaylistInfo.FAVORITE_PLAYLIST_ID,
        songUriString = songUri,
        addedAt = System.currentTimeMillis()
      )
    )
  }

  @Query(
    "DELETE FROM $PLAYLIST_SONG_ENTITY WHERE $PLAYLIST_ID_COLUMN = ${PlaylistInfo.FAVORITE_PLAYLIST_ID} " +
      "AND $SONG_URI_STRING_COLUMN = :songUri"
  )
  suspend fun deleteFromFavorites(songUri: String);

  @Transaction
  suspend fun insertSongsToPlaylists(
    songUris: List<String>,
    playlistsSongsEntity: List<PlaylistEntity>
  ) {
    for (playlist in playlistsSongsEntity) {
      val songsEntities = songUris.map { PlaylistsSongsEntity(playlist.id, it, System.currentTimeMillis()) }
      insertSongsToPlaylist(songsEntities)
    }
  }

  @Query(
    "UPDATE $PLAYLIST_ENTITY SET $PLAYLIST_NAME_COLUMN = :newName " +
      "WHERE $PLAYLIST_ID_COLUMN = :playlistId"
  )
  suspend fun renamePlaylist(playlistId: Int, newName: String)

  @Query(
    "DELETE FROM $PLAYLIST_SONG_ENTITY WHERE $PLAYLIST_ID_COLUMN = :playlistId AND $SONG_URI_STRING_COLUMN IN (:songUris)"
  )
  suspend fun removeSongsFromPlaylist(playlistId: Int, songUris: List<String>)

  @Query(
    "SELECT P.*, COUNT(S.$SONG_URI_STRING_COLUMN) as 'numberOfSongs' FROM $PLAYLIST_ENTITY P LEFT OUTER JOIN $PLAYLIST_SONG_ENTITY S " +
      "ON P.${PLAYLIST_ID_COLUMN} = S.${PLAYLIST_ID_COLUMN} GROUP BY P.$PLAYLIST_ID_COLUMN"
  )
  fun getPlaylistsInfoFlow(): Flow<List<PlaylistInfoWithNumberOfSongs>>
}