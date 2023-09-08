package com.omar.musica.songs

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omar.musica.model.Song
import com.omar.musica.playback.PlaybackManager
import com.omar.musica.store.MediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


@HiltViewModel
class SongsViewModel @Inject constructor(
    private val mediaRepository: MediaRepository,
    private val mediaPlaybackManager: PlaybackManager
): ViewModel() {

    val state: StateFlow<SongsScreenUiState> =
        mediaRepository.songsFlow
            .map {
                SongsScreenUiState.Success(it)
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
        val songs = (state.value as SongsScreenUiState.Success)?.songs ?: return
        mediaPlaybackManager.setPlaylistAndPlayAtIndex(songs, index)
    }

    fun onPlayNext(songs: List<Song>) {
        mediaPlaybackManager.playNext(songs)
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

}