package com.omar.musica.playlists.playlists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omar.musica.playback.PlaybackManager
import com.omar.musica.playback.PlaylistPlaybackActions
import com.omar.musica.store.PlaylistsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


@HiltViewModel
class PlaylistsViewModel @Inject constructor(
  private val playlistsRepository: PlaylistsRepository,
  playbackManager: PlaybackManager
): ViewModel(), PlaylistPlaybackActions by playbackManager {

  val state: StateFlow<PlaylistsScreenState> =
    playlistsRepository.playlistsWithInfoFlows
      .map {
        PlaylistsScreenState.Success(it)
      }
      .stateIn(viewModelScope, SharingStarted.Eagerly, PlaylistsScreenState.Loading)


  fun onDelete(id: Int) {
    playlistsRepository.deletePlaylist(id)
  }

  fun onRename(id: Int, name: String) {
    playlistsRepository.renamePlaylist(id, name)
  }

}
