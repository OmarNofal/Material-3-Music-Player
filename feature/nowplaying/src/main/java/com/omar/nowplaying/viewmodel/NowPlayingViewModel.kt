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
) : ViewModel(), INowPlayingViewModel {


    private val _state: StateFlow<NowPlayingState> =
        playbackManager.state
            .map { playbackManagerState ->
                val song = playbackManagerState.currentSong?.toUiSongModel()
                    ?: return@map NowPlayingState.NotPlaying
                NowPlayingState.Playing(
                    song,
                    playbackManagerState.playbackState,
                    repeatMode = playbackManagerState.repeatMode,
                    isShuffleOn = playbackManagerState.isShuffleOn
                )
            }.stateIn(viewModelScope, SharingStarted.Eagerly, NowPlayingState.NotPlaying)


    val state: StateFlow<NowPlayingState>
        get() = _state

    override fun currentSongProgress() = playbackManager.currentSongProgress

    override fun togglePlayback() {
        playbackManager.togglePlayback()
    }

    override fun nextSong() {
        playbackManager.playNextSong()
    }

    override fun jumpForward() {
        playbackManager.forward()
    }

    override fun jumpBackward() {
        playbackManager.backward()
    }

    override fun onUserSeek(progress: Float) {
        playbackManager.seekToPosition(progress)
    }

    override fun previousSong() {
        playbackManager.playPreviousSong()
    }

    override fun toggleRepeatMode() {
        playbackManager.toggleRepeatMode()
    }

    override fun toggleShuffleMode() {
        playbackManager.toggleShuffleMode()
    }

}