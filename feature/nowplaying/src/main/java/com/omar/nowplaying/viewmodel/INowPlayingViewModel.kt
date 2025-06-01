package com.omar.nowplaying.viewmodel

interface INowPlayingViewModel {
  fun currentSongProgress(): Float
  fun togglePlayback()
  fun nextSong()
  fun jumpForward()
  fun jumpBackward()
  fun onUserSeek(progress: Float)
  fun previousSong()
  fun toggleRepeatMode()
  fun toggleShuffleMode()
  fun toggleFavorite()
}