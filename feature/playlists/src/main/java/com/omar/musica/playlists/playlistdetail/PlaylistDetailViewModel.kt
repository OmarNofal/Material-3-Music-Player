package com.omar.musica.playlists.playlistdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omar.musica.model.playlist.PlaylistInfo
import com.omar.musica.playback.PlaybackManager
import com.omar.musica.store.PlaylistsRepository
import com.omar.musica.store.model.song.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class PlaylistDetailViewModel @Inject constructor(
  savedStateHandle: SavedStateHandle,
  private val playlistDao: PlaylistsRepository,
  private val playbackManager: PlaybackManager
) : ViewModel(), PlaylistActions {

  private val _state = MutableStateFlow<PlaylistDetailScreenState>(PlaylistDetailScreenState.Loading)
  val state: StateFlow<PlaylistDetailScreenState> get() = _state
  private var collectionJob: Job
  val playlistId: Int

  init {
    val id: String = savedStateHandle.get<String>("id") ?: throw IllegalArgumentException("Playlist Id not given")
    playlistId = id.toIntOrNull() ?: throw IllegalArgumentException("Invalid Playlist Id: $id")
    collectionJob = viewModelScope.launch {
      playlistDao.getPlaylistWithSongsFlow(playlistId, playlistId != PlaylistInfo.RECENT_PLAYED_PLAYLIST_ID)
        .collect {
          _state.emit(
            PlaylistDetailScreenState.Loaded(
              it.playlistInfo.id,
              it.playlistInfo.name,
              it.songs
            )
          )
        }
    }
  }

  fun onSongClicked(song: Song) {
    val state = _state.value
    if (state !is PlaylistDetailScreenState.Loaded) return

    val songs = state.songs
    val index = songs.indexOf(song)
    if (index == -1) return


    playbackManager.setPlaylistAndPlayAtIndex(songs, index)
  }

  override fun removeSongs(songUris: List<String>) {
    playlistDao.removeSongsFromPlaylist(playlistId, songUris)
  }

  override fun delete() {
    collectionJob.cancel()
    playlistDao.deletePlaylist(playlistId)
    _state.value = PlaylistDetailScreenState.Deleted
  }

  override fun playNext() {
    val state = _state.value
    if (state !is PlaylistDetailScreenState.Loaded) return

    val songs = state.songs
    playbackManager.playNext(songs)
  }

  override fun addToQueue() {
    val state = _state.value
    if (state !is PlaylistDetailScreenState.Loaded) return

    val songs = state.songs
    playbackManager.addToQueue(songs)
  }

  override fun shuffle() {
    val state = _state.value
    if (state !is PlaylistDetailScreenState.Loaded) return

    val songs = state.songs
    playbackManager.shuffle(songs)
  }

  override fun shuffleNext() {
    val state = _state.value
    if (state !is PlaylistDetailScreenState.Loaded) return

    val songs = state.songs
    playbackManager.shuffleNext(songs)
  }

  override fun rename(newName: String) {
    playlistDao.renamePlaylist(playlistId, newName)
  }

  override fun play() {
    val state = _state.value
    if (state !is PlaylistDetailScreenState.Loaded) return

    val songs = state.songs
    playbackManager.setPlaylistAndPlayAtIndex(songs)
  }
}

interface PlaylistActions {
  fun play()
  fun shuffle()
  fun playNext()
  fun shuffleNext()
  fun rename(newName: String)
  fun addToQueue()
  fun delete()
  fun removeSongs(songUris: List<String>)
}
