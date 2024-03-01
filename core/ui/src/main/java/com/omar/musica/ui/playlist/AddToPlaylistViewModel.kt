package com.omar.musica.ui.playlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omar.musica.model.playlist.PlaylistInfo
import com.omar.musica.store.PlaylistsRepository
import com.omar.musica.store.model.song.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


@HiltViewModel
class AddToPlaylistViewModel @Inject constructor(
    private val playlistsRepository: PlaylistsRepository
) : ViewModel() {


    val state = playlistsRepository.playlistsWithInfoFlows.map {
        AddToPlaylistState.Success(it)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, AddToPlaylistState.Loading)

    fun addSongsToPlaylists(songs: List<Song>, playlists: List<PlaylistInfo>) {
        playlistsRepository.addSongsToPlaylists(songs.map { it.uri.toString() }, playlists)
    }

}


sealed interface AddToPlaylistState {
    data object Loading : AddToPlaylistState
    data class Success(val playlists: List<PlaylistInfo>) : AddToPlaylistState
}