package com.omar.nowplaying

import com.omar.musica.model.Song
import com.omar.musica.playback.state.PlayerState


sealed interface NowPlayingState {


    data object NotPlaying: NowPlayingState

    data class Playing(
        val song: Song,
        val playbackState: PlayerState,
        val songProgress: Float, // 0 to 1
    ): NowPlayingState

}

