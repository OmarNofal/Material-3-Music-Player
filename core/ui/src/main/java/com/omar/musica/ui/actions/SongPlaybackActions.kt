package com.omar.musica.ui.actions

import android.content.Context
import com.omar.musica.playback.PlaybackManager
import com.omar.musica.store.model.song.Song
import com.omar.musica.ui.showSongsAddedToNextToast
import com.omar.musica.ui.showSongsAddedToQueueToast

interface SongPlaybackActions {

  fun playNext(songs: List<Song>)
  fun addToQueue(songs: List<Song>)
  fun shuffleNext(songs: List<Song>)
  fun shuffle(songs: List<Song>)
  fun play(songs: List<Song>)

}

class SongPlaybackActionsImpl(
  private val context: Context,
  private val playbackManager: PlaybackManager
) : SongPlaybackActions {

  override fun playNext(songs: List<Song>) {
    playbackManager.playNext(songs)
    context.showSongsAddedToNextToast(songs.size)
  }

  override fun addToQueue(songs: List<Song>) {
    playbackManager.addToQueue(songs)
    context.showSongsAddedToQueueToast(songs.size)
  }

  override fun shuffleNext(songs: List<Song>) {
    playbackManager.shuffleNext(songs)
    context.showSongsAddedToNextToast(songs.size)
  }

  override fun shuffle(songs: List<Song>) {
    playbackManager.shuffle(songs)
  }

  override fun play(songs: List<Song>) {
    playbackManager.setPlaylistAndPlayAtIndex(songs)
  }
}