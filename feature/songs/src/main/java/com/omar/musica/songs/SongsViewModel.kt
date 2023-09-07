package com.omar.musica.songs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omar.musica.store.MediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


@HiltViewModel
class SongsViewModel @Inject constructor(
    mediaRepository: MediaRepository
): ViewModel() {

    val state =
        mediaRepository.songsFlow
            .map {
                SongsScreenUiState.Success(it)
            }
            .stateIn(
                viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = SongsScreenUiState.Success(listOf())
            )


    fun songClicked(index: Int) {

    }

}