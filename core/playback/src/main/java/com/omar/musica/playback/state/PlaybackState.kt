package com.omar.musica.playback.state


import com.omar.musica.model.song.Song


data class PlaybackState(
    val currentSong: Song?,
    val playbackState: PlayerState,
) {

    companion object {
        val emptyState = PlaybackState(null, PlayerState.PAUSED)
    }
}


enum class PlayerState {
    PLAYING, PAUSED, BUFFERING
}