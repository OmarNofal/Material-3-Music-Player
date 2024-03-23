package com.omar.musica.albums.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omar.musica.store.AlbumsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


@HiltViewModel
class AlbumsViewModel @Inject constructor(
    private val albumsRepository: AlbumsRepository
): ViewModel() {


    private val _state: StateFlow<AlbumsScreenState>
        = albumsRepository.basicAlbums
        .map { AlbumsScreenState(it) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, AlbumsScreenState(albumsRepository.basicAlbums.value))

    val state: StateFlow<AlbumsScreenState>
        get() = _state

}