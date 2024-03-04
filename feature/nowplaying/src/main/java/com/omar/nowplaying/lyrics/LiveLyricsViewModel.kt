package com.omar.nowplaying.lyrics

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omar.musica.playback.PlaybackManager
import com.omar.musica.store.lyrics.LyricsRepository
import com.omar.musica.store.lyrics.LyricsResult
import com.omar.musica.store.model.song.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class LiveLyricsViewModel @Inject constructor(
    private val playbackManager: PlaybackManager,
    private val lyricsRepository: LyricsRepository
) : ViewModel() {


    private val _state = MutableStateFlow<LyricsScreenState>(LyricsScreenState.Loading)
    val state: StateFlow<LyricsScreenState>
        get() = _state

    init {
        viewModelScope.launch {
            playbackManager.state.distinctUntilChanged { old, new -> old.currentPlayingSong == new.currentPlayingSong }
                .collect {
                    if (it.currentPlayingSong == null) {
                        _state.value = LyricsScreenState.NotPlaying
                    } else {
                        loadLyrics(it.currentPlayingSong!!)
                    }
                }
        }
    }


    private suspend fun loadLyrics(song: Song) = withContext(Dispatchers.Default) {

        Log.d("Synced", "Loading Lyrics".toString())

        _state.value = LyricsScreenState.SearchingLyrics

        val lyricsResult = lyricsRepository
            .getLyrics(
                song.uri,
                song.metadata.title,
                song.metadata.albumName.orEmpty(),
                song.metadata.artistName.orEmpty(),
                song.metadata.durationMillis.toInt() / 1000
            )

        val newState = when (lyricsResult) {
            is LyricsResult.NotFound ->
                LyricsScreenState.NoLyrics(NoLyricsReason.NOT_FOUND)

            is LyricsResult.NetworkError ->
                LyricsScreenState.NoLyrics(NoLyricsReason.NETWORK_ERROR)

            is LyricsResult.FoundPlainLyrics ->
                LyricsScreenState.TextLyrics(lyricsResult.plainLyrics)

            is LyricsResult.FoundSyncedLyrics ->
                LyricsScreenState.SyncedLyrics(lyricsResult.syncedLyrics)
        }

        if (isActive)
            _state.value = newState
    }

    fun songProgressMillis(): Long {
        return playbackManager.currentSongProgressMillis
    }

    fun setSongProgressMillis(millis: Long) {
        return playbackManager.seekToPositionMillis(millis)
    }

}