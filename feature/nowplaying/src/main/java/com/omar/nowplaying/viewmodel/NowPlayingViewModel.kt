package com.omar.nowplaying.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omar.musica.playback.PlaybackManager
import com.omar.nowplaying.NowPlayingState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


@HiltViewModel
class NowPlayingViewModel @Inject constructor(
    private val playbackManager: PlaybackManager,
) : ViewModel(), INowPlayingViewModel {


    private val _state: StateFlow<NowPlayingState> =
        playbackManager.state.combine(playbackManager.queue) {
            mediaPlayerState, queue ->
            val song = mediaPlayerState.currentPlayingSong
                ?: return@combine NowPlayingState.NotPlaying
            NowPlayingState.Playing(
                queue = queue.items.map { it.song },
                songIndex = mediaPlayerState.songIndex,
                mediaPlayerState.playbackState.playerState,
                repeatMode = mediaPlayerState.playbackState.repeatMode,
                isShuffleOn = mediaPlayerState.playbackState.isShuffleOn
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

    override fun playSongAtIndex(index: Int) {
        playbackManager.playSongAtIndex(index)
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