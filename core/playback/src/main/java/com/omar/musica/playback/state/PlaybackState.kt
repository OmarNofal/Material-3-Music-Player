package com.omar.musica.playback.state

import android.net.Uri


data class PlaybackState(
    val currentSongUri: Uri?,
    val playbackState: PlayerState,
) {

    companion object {
        val emptyState = PlaybackState(null, PlayerState.PAUSED)
    }
}


enum class PlayerState {
    PLAYING, PAUSED, BUFFERING
}