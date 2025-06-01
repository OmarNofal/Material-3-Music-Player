package com.omar.musica.model.playlist


/**
 * Represents the details of a playlist without containing the actual songs.
 * The playlist object which contains the actual songs are inside the data layer.
 */
data class PlaylistInfo(
  val id: Int,
  val name: String,
  val numberOfSongs: Int,
){
  companion object{
    const val RECENT_PLAYED_PLAYLIST_ID = 1
    const val FAVORITE_PLAYLIST_ID = 2
    fun isBuildInPlaylist(playlistId: Int): Boolean {
      return playlistId == RECENT_PLAYED_PLAYLIST_ID || playlistId == FAVORITE_PLAYLIST_ID
    }
  }
}

