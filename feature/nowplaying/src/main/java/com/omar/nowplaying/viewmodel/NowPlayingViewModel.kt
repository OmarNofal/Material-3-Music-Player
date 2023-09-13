package com.omar.nowplaying.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omar.musica.playback.PlaybackManager
import com.omar.musica.store.MediaRepository
import com.omar.musica.ui.model.SongUi
import com.omar.musica.ui.model.toUiSongModel
import com.omar.nowplaying.NowPlayingState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class NowPlayingViewModel @Inject constructor(
    private val mediaRepository: MediaRepository,
    private val playbackManager: PlaybackManager
) : ViewModel() {


    /**
     * Used to remind the state to update the song progress
     */
    private val songProgressFlow = flow {
        while (true) {
            emit(Unit)
            delay(1000)
        }
    }.cancellable()


    // Used to cache and prevent searching again
    private var currentPlayingSong: SongUi? = null

    private val _state: StateFlow<NowPlayingState> =

        combine(
            playbackManager.state,
            songProgressFlow,
            mediaRepository.songsFlow
        ) { playbackManagerState, _, songs ->

            Timber.d("New Uri: ${playbackManagerState.currentSongUri}")

            val song = when (currentPlayingSong?.uriString) {
                playbackManagerState.currentSongUri.toString() -> currentPlayingSong
                else -> songs.find { it.uriString == playbackManagerState.currentSongUri.toString() }
                    ?.toUiSongModel()
            }.also { currentPlayingSong = it }

            val currentProgress = playbackManager.currentSongProgress
            val playbackState = playbackManagerState.playbackState

            if (song == null) return@combine NowPlayingState.NotPlaying

            NowPlayingState.Playing(song, playbackState, currentProgress)
        }.stateIn(viewModelScope, SharingStarted.Eagerly, NowPlayingState.NotPlaying)


    val state: StateFlow<NowPlayingState>
        get() = _state


    fun togglePlayback() {
        playbackManager.togglePlayback()
    }

    fun nextSong() {
        playbackManager.playNextSong()
    }

    fun jumpForward() {
        playbackManager.forward()
    }

    fun jumpBackward() {
        playbackManager.backward()
    }

    fun onUserSeek(progress: Float) {
        playbackManager.seekToPosition(progress)
    }

    fun previousSong() {
        playbackManager.playPreviousSong()
    }

}