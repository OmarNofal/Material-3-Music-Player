package com.omar.musica.albums.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omar.musica.model.AlbumsSortOption
import com.omar.musica.model.prefs.IsAscending
import com.omar.musica.playback.PlaybackManager
import com.omar.musica.store.AlbumsRepository
import com.omar.musica.store.MediaRepository
import com.omar.musica.store.model.album.BasicAlbum
import com.omar.musica.store.model.song.Song
import com.omar.musica.store.preferences.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AlbumsViewModel @Inject constructor(
  private val albumsRepository: AlbumsRepository,
  private val userPreferencesRepository: UserPreferencesRepository,
  private val playbackManager: PlaybackManager,
  private val mediaRepository: MediaRepository
) : ViewModel(), AlbumsScreenActions {

  private val _state: StateFlow<AlbumsScreenState> = albumsRepository.basicAlbums
    .combine(
      userPreferencesRepository.librarySettingsFlow.map { it.albumsSortOrder }
        .stateIn(viewModelScope, SharingStarted.Eagerly, AlbumsSortOption.NAME to true)
    ) { albums, sortOption ->
      val sortedAlbums = albums
        .let {
          val sortProperty: Comparator<BasicAlbum> = when (sortOption.first) {
            AlbumsSortOption.NAME -> compareBy { it.albumInfo.name }
            AlbumsSortOption.ARTIST -> compareBy { it.albumInfo.artist }
            AlbumsSortOption.NUMBER_OF_SONGS -> compareBy { it.albumInfo.numberOfSongs }
          }
          if (sortOption.second)
            it.sortedWith(sortProperty)
          else
            it.sortedWith(sortProperty).reversed()
        }
      AlbumsScreenState(sortedAlbums)
    }
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      AlbumsScreenState(albumsRepository.basicAlbums.value)
    )

  val state: StateFlow<AlbumsScreenState>
    get() = _state


  override fun changeGridSize(newSize: Int) {
    viewModelScope.launch {
      userPreferencesRepository.changeAlbumsGridSize(newSize)
    }
  }

  override fun changeSortOptions(sortOption: AlbumsSortOption, isAscending: IsAscending) {
    viewModelScope.launch {
      userPreferencesRepository.changeAlbumsSortOrder(sortOption, isAscending)
    }
  }

  override fun playAlbums(albumNames: List<BasicAlbum>) {
    val songs = getAlbumsSongs(albumNames.map { it.albumInfo.name })
    playbackManager.setPlaylistAndPlayAtIndex(songs, 0)
  }

  override fun playAlbumsNext(albumNames: List<BasicAlbum>) {
    val songs = getAlbumsSongs(albumNames.map { it.albumInfo.name })
    playbackManager.playNext(songs)
  }

  override fun addAlbumsToQueue(albumNames: List<BasicAlbum>) {
    val songs = getAlbumsSongs(albumNames.map { it.albumInfo.name })
    playbackManager.addToQueue(songs)
  }

  override fun shuffleAlbums(albumNames: List<BasicAlbum>) {
    val songs = getAlbumsSongs(albumNames.map { it.albumInfo.name })
    playbackManager.shuffle(songs)
  }

  override fun shuffleAlbumsNext(albumNames: List<BasicAlbum>) {
    val songs = getAlbumsSongs(albumNames.map { it.albumInfo.name })
    playbackManager.shuffleNext(songs)
  }

  override fun addAlbumsToPlaylist(albumNames: List<BasicAlbum>, playlistName: String) {
    TODO("Not yet implemented")
  }

  private fun getAlbumSongs(albumName: String): List<Song> {
    return mediaRepository.songsFlow.value.songs
      .filter { it.metadata.albumName == albumName }
  }

  private fun getAlbumsSongs(albumNames: List<String>): List<Song> {
    return albumNames
      .map { getAlbumSongs(it) }
      .flatten()
  }
}