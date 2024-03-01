package com.omar.musica.songs.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omar.musica.playback.PlaybackManager
import com.omar.musica.songs.SearchScreenUiState
import com.omar.musica.store.MediaRepository
import com.omar.musica.store.model.song.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SearchViewModel @Inject constructor(
    mediaRepository: MediaRepository,
    private val playbackManager: PlaybackManager
) : ViewModel() {


    private val _state: MutableStateFlow<SearchScreenUiState> =
        MutableStateFlow(SearchScreenUiState.emptyState)
    val state: StateFlow<SearchScreenUiState>
        get() = _state

    val songs = mediaRepository.songsFlow
        .map { it.songs }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), listOf())

    init {
        viewModelScope.launch {
            songs.onEach { updateSongList(it) }
                .collect()
        }
    }

    private fun updateState(searchQuery: String, songs: List<Song>) {
        _state.getAndUpdate {
            if (searchQuery.isBlank()) return@getAndUpdate SearchScreenUiState.emptyState
            val sortedSongs = songs.filter { song ->
                song.metadata.title.contains(searchQuery, ignoreCase = true)
                        ||
                        (song.metadata.albumName?.contains(searchQuery, ignoreCase = true) ?: false)
                        ||
                        (song.metadata.artistName?.contains(searchQuery, ignoreCase = true)
                            ?: false)
            }
            SearchScreenUiState(searchQuery, sortedSongs)
        }
    }

    private fun updateSongList(songs: List<Song>) {
        updateState(state.value.searchQuery, songs)
    }

    fun onSearchQueryChanged(query: String) {
        updateState(query, songs.value)
    }

    fun onSongClicked(song: Song, index: Int) {
        val songs = _state.value.songs
        playbackManager.setPlaylistAndPlayAtIndex(songs, index)
    }
}