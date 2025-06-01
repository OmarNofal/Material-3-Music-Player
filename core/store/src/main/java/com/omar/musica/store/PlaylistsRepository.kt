package com.omar.musica.store

import com.omar.musica.database.dao.PlaylistDao
import com.omar.musica.database.entities.playlist.PlaylistEntity
import com.omar.musica.database.model.PlaylistInfoWithNumberOfSongs
import com.omar.musica.model.playlist.PlaylistInfo
import com.omar.musica.store.model.playlist.Playlist
import com.omar.musica.store.model.song.Song
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class PlaylistsRepository @Inject constructor(
  private val playlistsDao: PlaylistDao,
  private val mediaRepository: MediaRepository,
) {

  private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)

  val playlistsWithInfoFlows =
    playlistsDao.getPlaylistsInfoFlow()
      .map {
        it.toDomainPlaylists()
      }
      .stateIn(coroutineScope, SharingStarted.WhileSubscribed(5000), listOf())

  fun createPlaylist(name: String) {
    coroutineScope.launch {
      val playlist = PlaylistEntity(name = name)
      playlistsDao.createPlaylist(playlist)
    }
  }

  fun createPlaylistAndAddSongs(name: String, songUris: List<String>) {
    coroutineScope.launch {
      playlistsDao.createPlaylistAndAddSongs(name, songUris)
    }
  }

  fun addSongsToPlaylists(songsUris: List<String>, playlists: List<PlaylistInfo>) {
    coroutineScope.launch {
      playlistsDao.insertSongsToPlaylists(songsUris, playlists.toDBEntities())
    }
  }

  fun addSongToFavorites(songUri: String) {
    coroutineScope.launch {
      playlistsDao.insertToFavorites(songUri)
    }
  }

  fun insertSongToRecentPlaylist(songUri: String) {
    coroutineScope.launch {
      playlistsDao.insertToRecentPlaylist(songUri)
    }
  }

  fun deletePlaylist(id: Int) {
    coroutineScope.launch {
      playlistsDao.deletePlaylistWithSongs(id)
    }
  }

  fun renamePlaylist(id: Int, newName: String) {
    coroutineScope.launch {
      playlistsDao.renamePlaylist(id, newName)
    }
  }

  fun removeSongsFromPlaylist(id: Int, songsUris: List<String>) {
    coroutineScope.launch {
      playlistsDao.removeSongsFromPlaylist(id, songsUris)
    }
  }

  fun removeSongFromFavorites(songUri: String) {
    coroutineScope.launch {
      playlistsDao.deleteFromFavorites(songUri)
    }
  }

  suspend fun getPlaylistSongs(id: Int): List<Song> {
    val songUris = playlistsDao.getPlaylistSongs(id)
    val songLibrary = mediaRepository.songsFlow.value
    return songLibrary.getSongsByUris(songUris)
  }

  fun getPlaylistWithSongsFlow(playlistId: Int, timeOrder: Boolean): Flow<Playlist> =
    combine(
      mediaRepository.songsFlow,
      playlistsDao.getPlaylistWithSongsFlow(playlistId)
    ) { library, playlistWithSongs ->
      // Convert the songs to a map to enable fast retrieval
      val songsSet = library.songs.associateBy { it.uri.toString() }
      // The uris of the song
      val playlistSongEts = playlistWithSongs.songUris.run {
        if (timeOrder) {
          sortedBy { it.addedAt }
        } else {
          sortedByDescending { it.addedAt }
        }
      }
      val playlistSongs = mutableListOf<Song>()
      for (songEt in playlistSongEts) {
        val song = songsSet[songEt.songUriString]
        if (song != null) {
          playlistSongs.add(song)
        }
      }
      val playlistInfo = playlistWithSongs.playlistEntity
      Playlist(
        PlaylistInfo(playlistInfo.id, playlistInfo.name, playlistSongs.size),
        playlistSongs
      )
    }

  fun getIsFavoriteFlow(songUri: String): Flow<Boolean> = playlistsDao.isFavoriteFlow(songUri)

  private fun PlaylistInfo.toDBEntity() =
    PlaylistEntity(id, name)

  private fun List<PlaylistInfo>.toDBEntities() =
    map { it.toDBEntity() }

  private fun PlaylistInfoWithNumberOfSongs.toDomainPlaylist() =
    PlaylistInfo(
      id = playlistEntity.id,
      name = playlistEntity.name,
      numberOfSongs = numberOfSongs
    )

  private fun List<PlaylistInfoWithNumberOfSongs>.toDomainPlaylists() =
    map { it.toDomainPlaylist() }


}