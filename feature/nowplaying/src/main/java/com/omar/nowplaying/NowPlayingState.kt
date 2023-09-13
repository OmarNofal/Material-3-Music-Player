package com.omar.nowplaying

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.omar.musica.playback.state.PlayerState
import com.omar.musica.ui.model.SongUi


@Immutable
sealed interface NowPlayingState {


    @Immutable
    data object NotPlaying : NowPlayingState

    @Immutable
    data class Playing(
        @Stable
        val song: SongUi,
        val playbackState: PlayerState,
        val songProgress: Float, // 0 to 1
    ) : NowPlayingState

}
