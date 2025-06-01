package com.omar.musica.albums.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omar.musica.albums.ui.albumdetail.AlbumDetailActions
import com.omar.musica.playback.PlaybackManager
import com.omar.musica.store.AlbumsRepository
import com.omar.musica.store.model.song.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


@HiltViewModel
class AlbumDetailsViewModel @Inject constructor(
  albumsRepository: AlbumsRepository,
  savedStateHandle: SavedStateHandle,
  private val playbackManager: PlaybackManager
) : ViewModel(), AlbumDetailActions {

  private val albumId = savedStateHandle.get<Int>(ALBUM_ID_KEY)!!

  val state: StateFlow<AlbumDetailsScreenState> =
    albumsRepository.getAlbumWithSongs(albumId).map { album ->
      if (album == null) return@map AlbumDetailsScreenState.Loading
      val artistName = album.albumInfo.artist

      val otherAlbums = albumsRepository.getArtistAlbums(artistName)
        .map { it.filter { it.albumInfo.id != albumId } }.firstOrNull() ?: listOf()


      AlbumDetailsScreenState.Loaded(album, otherAlbums)
    }
      .stateIn(viewModelScope, SharingStarted.Eagerly, AlbumDetailsScreenState.Loading)


  override fun play() {
    playbackManager.setPlaylistAndPlayAtIndex(getAlbumSongs())
  }

  override fun playAtIndex(index: Int) {
    playbackManager.setPlaylistAndPlayAtIndex(getAlbumSongs(), index)
  }

  override fun playNext() {
    playbackManager.playNext(getAlbumSongs())
  }

  override fun shuffle() {
    playbackManager.shuffle(getAlbumSongs())
  }

  override fun shuffleNext() {
    playbackManager.shuffleNext(getAlbumSongs())
  }

  override fun addToQueue() {
    playbackManager.addToQueue(getAlbumSongs())
  }

  private fun getAlbumSongs(): List<Song> {
    return (state.value as? AlbumDetailsScreenState.Loaded)?.albumWithSongs?.songs?.map { it.song }
      ?: listOf()
  }

  companion object {
    const val ALBUM_ID_KEY = "ALBUM_ID"
  }
}