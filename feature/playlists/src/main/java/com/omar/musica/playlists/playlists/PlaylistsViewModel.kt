package com.omar.musica.playlists.playlists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omar.musica.store.PlaylistsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


@HiltViewModel
class PlaylistsViewModel @Inject constructor(
    playlistsRepository: PlaylistsRepository
): ViewModel() {


    val state: StateFlow<PlaylistsScreenState> =
        playlistsRepository.playlistsWithInfoFlows
            .map {
                PlaylistsScreenState.Success(it)
            }
            .stateIn(viewModelScope, SharingStarted.Eagerly, PlaylistsScreenState.Loading)


}
