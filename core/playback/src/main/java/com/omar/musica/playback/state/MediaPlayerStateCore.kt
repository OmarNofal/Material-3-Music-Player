package com.omar.musica.playback.state

import com.omar.musica.model.playback.PlaybackState
import com.omar.musica.store.model.song.Song

data class MediaPlayerStateCore(
  val currentPlayingSong: Song?,
  val playbackState: PlaybackState
) {
  companion object {
    val empty = MediaPlayerStateCore(null, PlaybackState.emptyState)
  }
}

data class MediaPlayerState(
  val core: MediaPlayerStateCore,
  val isSongFavorite: Boolean,
){
  companion object {
    val empty = MediaPlayerState(
      core = MediaPlayerStateCore.empty,
      isSongFavorite = false
    )
  }
}