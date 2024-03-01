package com.omar.musica.playback.state


import com.omar.musica.model.song.Song


data class PlaybackState(
    val currentSong: Song?,
    val playbackState: PlayerState,
    val isShuffleOn: Boolean = false,
    val repeatMode: RepeatMode
) {

    companion object {
        val emptyState = PlaybackState(
            null,
            PlayerState.PAUSED,
            false,
            RepeatMode.REPEAT_ALL
        )
    }
}


enum class PlayerState {
    PLAYING, PAUSED, BUFFERING
}