package com.omar.nowplaying.queue

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omar.musica.playback.PlaybackManager
import com.omar.musica.store.MediaRepository
import com.omar.musica.store.QueueRepository
import com.omar.musica.ui.model.SongUi
import com.omar.musica.ui.model.toUiSongModels
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.annotation.concurrent.Immutable
import javax.inject.Inject


@HiltViewModel
class QueueViewModel @Inject constructor(
    queueRepository: QueueRepository,
    mediaRepository: MediaRepository,
    private val playbackManager: PlaybackManager
) : ViewModel() {


    val queueScreenState = combine(
        queueRepository.observeQueueUris(), mediaRepository.songsFlow, playbackManager.state
    ) { uris, library, playerState ->
        val songs = library.getSongsByUris(uris)
        val currentSongIndex = uris.indexOf(playerState.currentSong?.uriString)
        QueueScreenState.Loaded(songs.toUiSongModels(), currentSongIndex)
    }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(500, 500), QueueScreenState.Loading
    )

    fun onSongClicked(index: Int) {
        playbackManager.playSongAtIndex(index)
    }

    fun onRemoveFromQueue(index: Int) {
        playbackManager.removeSongAtIndex(index)
    }

    fun reorderSong(from: Int, to: Int) {
        playbackManager.reorderSong(from, to)
    }

    fun onClearQueue() {
        playbackManager.clearQueue()
    }

}


sealed interface QueueScreenState {

    data class Loaded(
        val songs: List<SongUi>, val currentSongIndex: Int
    ) : QueueScreenState

    data object Loading : QueueScreenState
}