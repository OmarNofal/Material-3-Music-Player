package com.omar.nowplaying

import com.omar.musica.model.Song
import com.omar.musica.playback.state.PlayerState


data class NowPlayingState(
    val song: Song?,
    val playbackState: PlayerState,
    val songProgress: Float, // 0 to 1
) {

    companion object {
        val emptyState = NowPlayingState(null, PlayerState.PAUSED, 0.0f)
    }
}