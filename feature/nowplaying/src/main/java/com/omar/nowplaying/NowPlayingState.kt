package com.omar.nowplaying

import androidx.compose.runtime.Immutable
import com.omar.musica.model.playback.PlayerState
import com.omar.musica.model.playback.RepeatMode
import com.omar.musica.store.model.song.Song


@Immutable
sealed interface NowPlayingState {
  @Immutable
  data object NotPlaying : NowPlayingState

  @Immutable
  data class Playing(
    val song: Song,
    val isFavorite: Boolean,
    val playbackState: PlayerState,
    val repeatMode: RepeatMode,
    val isShuffleOn: Boolean,
  ) : NowPlayingState
}

