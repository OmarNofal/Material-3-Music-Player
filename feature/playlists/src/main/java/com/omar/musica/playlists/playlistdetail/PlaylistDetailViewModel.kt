package com.omar.musica.playlists.playlistdetail

import androidx.compose.runtime.MutableState
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omar.musica.database.dao.PlaylistDao
import com.omar.musica.playback.PlaybackManager
import com.omar.musica.store.PlaylistsRepository
import com.omar.musica.ui.model.SongUi
import com.omar.musica.ui.model.toSongModels
import com.omar.musica.ui.model.toUiSongModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class PlaylistDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val playlistDao: PlaylistsRepository,
    private val playbackManager: PlaybackManager,
): ViewModel() {

    private val _state = MutableStateFlow<PlaylistDetailScreenState>(PlaylistDetailScreenState.Loading)
    val state: StateFlow<PlaylistDetailScreenState> get() = _state

    private var collectionJob: Job

    private val id: String = savedStateHandle.get<String>("id") ?: throw IllegalArgumentException("Playlist Id not given")

    init {

        collectionJob = viewModelScope.launch {
            playlistDao.getPlaylistWithSongsFlow(id.toInt())
                .collect {
                    _state.emit(
                        PlaylistDetailScreenState.Loaded(
                            it.playlistInfo.name,
                            it.songs.map { song -> song.toUiSongModel() }
                        )
                    )
                }
        }

    }

    fun onSongClicked(song: SongUi) {
        val state = _state.value
        if (state !is PlaylistDetailScreenState.Loaded) return

        val songs = state.songs
        val index = songs.indexOf(song)
        if (index == -1) return


        playbackManager.setPlaylistAndPlayAtIndex(songs.toSongModels(), index)
    }

    fun onRemoveSong(song: SongUi) {

    }

    fun onDeletePlaylist() {
        collectionJob.cancel()
        playlistDao.deletePlaylist(id.toInt())
        _state.value = PlaylistDetailScreenState.Deleted
    }

    fun onPlayNext() {
        val state = _state.value
        if (state !is PlaylistDetailScreenState.Loaded) return

        val songs = state.songs
        playbackManager.playNext(songs.toSongModels())
    }

    fun addToQueue() {
        val state = _state.value
        if (state !is PlaylistDetailScreenState.Loaded) return

        val songs = state.songs
        playbackManager.addToQueue(songs.toSongModels())
    }

    fun onShuffle() {
        val state = _state.value
        if (state !is PlaylistDetailScreenState.Loaded) return

        val songs = state.songs
        playbackManager.shuffle(songs.toSongModels())
    }

    fun onShuffleNext() {
        val state = _state.value
        if (state !is PlaylistDetailScreenState.Loaded) return

        val songs = state.songs
        playbackManager.shuffleNext(songs.toSongModels())
    }

    fun onRename(newName: String) {
        playlistDao.renamePlaylist(id.toInt(), newName)
    }

    fun onPlay() {
        val state = _state.value
        if (state !is PlaylistDetailScreenState.Loaded) return

        val songs = state.songs
        playbackManager.setPlaylistAndPlayAtIndex(songs.toSongModels())
    }


}