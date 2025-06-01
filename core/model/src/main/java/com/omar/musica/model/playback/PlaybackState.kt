package com.omar.musica.model.playback


/**
 * Contains info about the current playback state of the media player,
 * such as whether the player is playing media or paused, shuffle mode and repeat mode
 */
data class PlaybackState(
  val playerState: PlayerState,
  val isShuffleOn: Boolean = false,
  val repeatMode: RepeatMode
) {
  companion object {
    val emptyState = PlaybackState(
      PlayerState.PAUSED,
      false,
      RepeatMode.REPEAT_ALL
    )
  }
}

enum class PlayerState {
  PLAYING, PAUSED, BUFFERING
}