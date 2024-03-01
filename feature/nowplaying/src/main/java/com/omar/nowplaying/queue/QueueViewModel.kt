package com.omar.nowplaying.queue

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omar.musica.playback.PlaybackManager
import com.omar.musica.store.MediaRepository
import com.omar.musica.store.PlaylistsRepository
import com.omar.musica.store.QueueRepository
import com.omar.musica.store.model.song.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


@HiltViewModel
class QueueViewModel @Inject constructor(
    queueRepository: QueueRepository,
    mediaRepository: MediaRepository,
    private val playlistsRepository: PlaylistsRepository,
    private val playbackManager: PlaybackManager
) : ViewModel() {


    val queueScreenState = combine(
        queueRepository.observeQueueUris(), mediaRepository.songsFlow, playbackManager.state
    ) { uris, library, playerState ->
        val songs = library.getSongsByUris(uris)
        val currentSongIndex = uris.indexOf(playerState.currentPlayingSong?.uri.toString())
        QueueScreenState.Loaded(songs, currentSongIndex)
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
            (queueScreenState.value as QueueScreenState.Loaded).songs.map { it.uri.toString() }
        playlistsRepository.createPlaylistAndAddSongs(name, songs)
    }
}


sealed interface QueueScreenState {

    data class Loaded(
        val songs: List<Song>, val currentSongIndex: Int
    ) : QueueScreenState

    data object Loading : QueueScreenState
}