package com.omar.musica.songs.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omar.musica.model.Song
import com.omar.musica.playback.PlaybackManager
import com.omar.musica.songs.SongsScreenUiState
import com.omar.musica.songs.ui.SortOption
import com.omar.musica.store.MediaRepository
import com.omar.musica.ui.model.SongUi
import com.omar.musica.ui.model.toSongModel
import com.omar.musica.ui.model.toSongModels
import com.omar.musica.ui.model.toUiSongModels
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


@HiltViewModel
class SongsViewModel @Inject constructor(
    private val mediaRepository: MediaRepository,
    private val mediaPlaybackManager: PlaybackManager
) : ViewModel() {

    private val sortOptionFlow = MutableStateFlow(SortOption.TITLE to true)

    val state: StateFlow<SongsScreenUiState> =
        mediaRepository.songsFlow
            .map { it.toUiSongModels() }
            .combine(sortOptionFlow) { songList, sortOptionPair ->
                // Sort the list according to the sort option
                val ascending = sortOptionPair.second
                val sortedList = if (ascending)
                    songList.sortedByOptionAscending(sortOptionPair.first)
                else
                    songList.sortedByOptionDescending(sortOptionPair.first)
                SongsScreenUiState.Success(sortedList, sortOptionPair.first, sortOptionPair.second)
            }
            .stateIn(
                viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = SongsScreenUiState.Success(listOf())
            )


    /**
     * User clicked a song in the list. Default action is to play
     */
    fun onSongClicked(song: SongUi, index: Int) {
        val songs = (state.value as SongsScreenUiState.Success).songs
        mediaPlaybackManager.setPlaylistAndPlayAtIndex(songs.toSongModels(), index)
    }

    fun onPlayNext(songs: List<SongUi>) {
        mediaPlaybackManager.playNext(songs.toSongModels())
    }

    /**
     * User changed the sorting order of the songs screen
     */
    fun onSortOptionChanged(sortOption: SortOption, isAscending: Boolean) {
        sortOptionFlow.value = sortOption to isAscending
    }

    /**
     * User wants to delete songs.
     * This is only intended for Android versions lower than R, since R and higher have different methods to delete songs.
     * Mainly, in Android R and above, we will have to send an intent to delete a media item and the system will ask the user for permission.
     * So they are implemented as part of the UI in Jetpack Compose
     */
    fun onDelete(songs: List<SongUi>) {
        mediaRepository.deleteSong(songs[0].toSongModel())
    }

    private fun List<SongUi>.sortedByOptionAscending(sortOption: SortOption): List<SongUi> =
        when (sortOption) {
            SortOption.TITLE -> this.sortedBy { it.title }
            SortOption.ARTIST -> this.sortedBy { it.artist }
            SortOption.FileSize -> this.sortedBy { it.size }
            SortOption.ALBUM -> this.sortedBy { it.album }
            SortOption.Duration -> this.sortedBy { it.length }
        }


    private fun List<SongUi>.sortedByOptionDescending(sortOption: SortOption): List<SongUi> =
        when (sortOption) {
            SortOption.TITLE -> this.sortedByDescending { it.title }
            SortOption.ARTIST -> this.sortedByDescending { it.artist }
            SortOption.FileSize -> this.sortedByDescending { it.size }
            SortOption.ALBUM -> this.sortedByDescending { it.album }
            SortOption.Duration -> this.sortedByDescending { it.length }
        }


}