package com.omar.musica.playback


interface PlaylistPlaybackActions {
  fun shufflePlaylistNext(playlistId: Int)
  fun shufflePlaylist(playlistId: Int)
  fun addPlaylistToQueue(playlistId: Int)
  fun addPlaylistToNext(playlistId: Int)
  fun playPlaylist(playlistId: Int)
}