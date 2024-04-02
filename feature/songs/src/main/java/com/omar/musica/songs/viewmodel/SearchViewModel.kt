package com.omar.musica.songs.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omar.musica.playback.PlaybackManager
import com.omar.musica.songs.SearchScreenUiState
import com.omar.musica.store.AlbumsRepository
import com.omar.musica.store.MediaRepository
import com.omar.musica.store.model.album.BasicAlbum
import com.omar.musica.store.model.song.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


@HiltViewModel
class SearchViewModel @Inject constructor(
    mediaRepository: MediaRepository,
    albumsRepository: AlbumsRepository,
    private val playbackManager: PlaybackManager
) : ViewModel() {

    private val currentQuery = MutableStateFlow("")

    private val _state: StateFlow<SearchScreenUiState> =
        combine(
            currentQuery,
            mediaRepository.songsFlow.map { it.songs },
            albumsRepository.basicAlbums,
            transform = ::getNewState
        ).stateIn(viewModelScope, SharingStarted.Eagerly, SearchScreenUiState.emptyState)

    val state: StateFlow<SearchScreenUiState>
        get() = _state

    private fun getNewState(query: String, songs: List<Song>, albums: List<BasicAlbum>): SearchScreenUiState {
        if (query.isBlank()) return SearchScreenUiState.emptyState
        val filteredSongs = songs.filter { song ->
            song.metadata.title.contains(query, ignoreCase = true)
                    || (song.metadata.albumName?.contains(query, ignoreCase = true) ?: false)
                    || (song.metadata.artistName?.contains(query, ignoreCase = true)
                        ?: false)
        }
        val filteredAlbums = albums.filter { it.albumInfo.name.contains(query, ignoreCase = true) }
        return SearchScreenUiState(query, filteredSongs, filteredAlbums)
    }

    fun onSearchQueryChanged(query: String) {
        currentQuery.value = query
    }

    fun onSongClicked(song: Song, index: Int) {
        val songs = _state.value.songs
        playbackManager.setPlaylistAndPlayAtIndex(songs, index)
    }
}