package com.omar.musica.albums.viewmodel

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omar.musica.store.AlbumsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


@HiltViewModel
class AlbumDetailsViewModel @Inject constructor(
    albumsRepository: AlbumsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val albumName = Uri.decode(savedStateHandle.get<String>(ALBUM_NAME_KEY)!!)
    private val artistName = Uri.decode(savedStateHandle.get<String>(ARTIST_NAME_KEY)!!)

    val state: StateFlow<AlbumDetailsScreenState> =
        combine(
            albumsRepository.getAlbumWithSongs(albumName),
            albumsRepository.getArtistAlbums(artistName).map { it.filter { it.albumInfo.name != albumName } }
        ) { album, otherAlbums ->
            AlbumDetailsScreenState.Loaded(album, otherAlbums)
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, AlbumDetailsScreenState.Loading)



    companion object {
        const val ALBUM_NAME_KEY = "ALBUM_NAME"
        const val ARTIST_NAME_KEY = "ARTIST_NAME"
    }

}