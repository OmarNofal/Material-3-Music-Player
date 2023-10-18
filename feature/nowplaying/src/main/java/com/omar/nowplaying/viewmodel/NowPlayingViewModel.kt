package com.omar.nowplaying.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omar.musica.playback.PlaybackManager
import com.omar.musica.ui.model.toUiSongModel
import com.omar.nowplaying.NowPlayingState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


@HiltViewModel
class NowPlayingViewModel @Inject constructor(
    private val playbackManager: PlaybackManager
) : ViewModel() {


    private val _state: StateFlow<NowPlayingState> =
        playbackManager.state
            .map { playbackManagerState ->
                val song = playbackManagerState.currentSong?.toUiSongModel()
                    ?: return@map NowPlayingState.NotPlaying
                NowPlayingState.Playing(song, playbackManagerState.playbackState)
            }.stateIn(viewModelScope, SharingStarted.Eagerly, NowPlayingState.NotPlaying)


    val state: StateFlow<NowPlayingState>
        get() = _state

    fun currentSongProgress() = playbackManager.currentSongProgress

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