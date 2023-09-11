package com.omar.nowplaying.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omar.musica.playback.PlaybackManager
import com.omar.musica.store.MediaRepository
import com.omar.nowplaying.NowPlayingState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class NowPlayingViewModel @Inject constructor(
    private val mediaRepository: MediaRepository,
    private val playbackManager: PlaybackManager
) : ViewModel() {


    val currentPlayingSong: StateFlow<NowPlayingState> =
        playbackManager.state
            .map {
                Timber.d("New Uri: $it")
                val song = mediaRepository.getSong(it.currentSongUri) ?: return@map NowPlayingState.emptyState
                val currentProgress = playbackManager.currentSongProgress
                val playbackState = it.playbackState
                NowPlayingState(song, playbackState, currentProgress)
            }
            .stateIn(viewModelScope, SharingStarted.Eagerly, NowPlayingState.emptyState)


    fun togglePlayback() {
        playbackManager.togglePlayback()
    }

    fun nextSong() {
        playbackManager.playNextSong()
    }

    fun rewind() {
        playbackManager.playPreviousSong()
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

}