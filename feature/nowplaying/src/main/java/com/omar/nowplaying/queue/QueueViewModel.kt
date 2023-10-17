package com.omar.nowplaying.queue

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omar.musica.store.MediaRepository
import com.omar.musica.store.QueueRepository
import com.omar.musica.ui.model.SongUi
import com.omar.musica.ui.model.toUiSongModels
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


@HiltViewModel
class QueueViewModel @Inject constructor(
    queueRepository: QueueRepository,
    mediaRepository: MediaRepository
) : ViewModel() {


    val queueState = queueRepository.observeQueueUris()
        .combine(mediaRepository.songsFlow) { uris, library ->
            val songs = library.getSongsByUris(uris)
            QueueState.Loaded(songs.toUiSongModels())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(500, 500), QueueState.Loading)



}



sealed interface QueueState {
    data class Loaded(val songs: List<SongUi>): QueueState
    data object Loading: QueueState
}