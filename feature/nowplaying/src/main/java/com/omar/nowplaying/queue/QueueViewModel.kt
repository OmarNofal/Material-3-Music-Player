package com.omar.nowplaying.queue

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omar.musica.playback.PlaybackManager
import com.omar.musica.store.PlaylistsRepository
import com.omar.musica.store.model.queue.Queue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


@HiltViewModel
class QueueViewModel @Inject constructor(
    private val playlistsRepository: PlaylistsRepository,
    private val playbackManager: PlaybackManager
) : ViewModel() {


    val queueScreenState =
        combine(playbackManager.queue, playbackManager.state) { queue, playerState ->
            if (queue.items.isEmpty()) return@combine QueueScreenState.Loading
            val currentPlayingIndex = playbackManager.getCurrentSongIndex()
            val currentSongId = queue.items[currentPlayingIndex].originalIndex
            QueueScreenState.Loaded(queue, currentPlayingIndex, currentSongId)
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

    fun onSaveAsPlaylist(name: String) {
        val songs =
            (queueScreenState.value as QueueScreenState.Loaded).queue.items.map { it.song.uri.toString() }
        playlistsRepository.createPlaylistAndAddSongs(name, songs)
    }
}


sealed interface QueueScreenState {

    data class Loaded(
        val queue: Queue,
        val currentSongIndex: Int,
        val currentSongId: Int
    ) : QueueScreenState

    data object Loading : QueueScreenState
}