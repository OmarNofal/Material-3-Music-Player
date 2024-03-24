package com.omar.musica.songs.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omar.musica.model.SongSortOption
import com.omar.musica.playback.PlaybackManager
import com.omar.musica.songs.SongsScreenUiState
import com.omar.musica.store.MediaRepository
import com.omar.musica.store.model.song.Song
import com.omar.musica.store.preferences.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SongsViewModel @Inject constructor(
    private val mediaRepository: MediaRepository,
    private val mediaPlaybackManager: PlaybackManager,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val sortOptionFlow = userPreferencesRepository.librarySettingsFlow
        .map { it.songsSortOrder }.distinctUntilChanged()

    val state: StateFlow<SongsScreenUiState> =
        mediaRepository.songsFlow
            .map { it.songs }
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
    fun onSongClicked(song: Song, index: Int) {
        val songs = (state.value as SongsScreenUiState.Success).songs
        mediaPlaybackManager.setPlaylistAndPlayAtIndex(songs, index)
    }

    fun onPlayNext(songs: List<Song>) {
        mediaPlaybackManager.playNext(songs)
    }

    /**
     * User changed the sorting order of the songs screen
     */
    fun onSortOptionChanged(songSortOption: SongSortOption, isAscending: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.changeLibrarySortOrder(songSortOption, isAscending)
        }
    }

    /**
     * User wants to delete songs.
     * This is only intended for Android versions lower than R, since R and higher have different methods to delete songs.
     * Mainly, in Android R and above, we will have to send an intent to delete a media item and the system will ask the user for permission.
     * So they are implemented as part of the UI in Jetpack Compose
     */
    fun onDelete(songs: List<Song>) {
        mediaRepository.deleteSong(songs[0])
    }

    private fun List<Song>.sortedByOptionAscending(songSortOption: SongSortOption): List<Song> =
        when (songSortOption) {
            SongSortOption.TITLE -> this.sortedBy { it.metadata.title.lowercase() }
            SongSortOption.ARTIST -> this.sortedBy { it.metadata.artistName?.lowercase() }
            SongSortOption.FileSize -> this.sortedBy { it.metadata.sizeBytes }
            SongSortOption.ALBUM -> this.sortedBy { it.metadata.albumName }
            SongSortOption.Duration -> this.sortedBy { it.metadata.durationMillis }
        }


    private fun List<Song>.sortedByOptionDescending(songSortOption: SongSortOption): List<Song> =
        when (songSortOption) {
            SongSortOption.TITLE -> this.sortedByDescending { it.metadata.title.lowercase() }
            SongSortOption.ARTIST -> this.sortedByDescending { it.metadata.artistName?.lowercase() }
            SongSortOption.FileSize -> this.sortedByDescending { it.metadata.sizeBytes }
            SongSortOption.ALBUM -> this.sortedByDescending { it.metadata.albumName }
            SongSortOption.Duration -> this.sortedByDescending { it.metadata.durationMillis }
        }


}