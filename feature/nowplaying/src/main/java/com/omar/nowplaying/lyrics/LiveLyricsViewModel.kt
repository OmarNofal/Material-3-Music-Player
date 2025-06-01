package com.omar.nowplaying.lyrics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omar.musica.network.data.NetworkMonitor
import com.omar.musica.network.model.NetworkStatus
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
  private val lyricsRepository: LyricsRepository,
  private val networkMonitor: NetworkMonitor
) : ViewModel() {

  private val _state = MutableStateFlow<LyricsScreenState>(LyricsScreenState.Loading)
  val state: StateFlow<LyricsScreenState>
    get() = _state
  init {
    viewModelScope.launch {
      playbackManager.state.distinctUntilChanged { old, new -> old.core.currentPlayingSong == new.core.currentPlayingSong }
        .collect {
          if (it.core.currentPlayingSong == null) {
            _state.value = LyricsScreenState.NotPlaying
          } else {
            loadLyrics(it.core.currentPlayingSong!!)
          }
        }
    }
    viewModelScope.launch {
      networkMonitor.state.collect {
        if (it == NetworkStatus.CONNECTED)
          onRegainedNetworkConnection()
      }
    }
  }

  private fun onRegainedNetworkConnection() {
    val currentState = _state.value
    if (currentState is LyricsScreenState.NoLyrics && currentState.reason == NoLyricsReason.NETWORK_ERROR) {
      onRetry()
    }
  }

  fun onRetry() {
    val currentSong = (playbackManager.state.value.core.currentPlayingSong) ?: return
    viewModelScope.launch {
      loadLyrics(currentSong)
    }
  }

  private suspend fun loadLyrics(song: Song) = withContext(Dispatchers.Default) {
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
        LyricsScreenState.TextLyrics(lyricsResult.plainLyrics, lyricsResult.lyricsSource)

      is LyricsResult.FoundSyncedLyrics ->
        LyricsScreenState.SyncedLyrics(lyricsResult.syncedLyrics, lyricsResult.lyricsSource)
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

  fun saveExternalLyricsToSongFile() {
    viewModelScope.launch {
      val currentPlayingSongUri = playbackManager.state.value.core.currentPlayingSong ?: return@launch
      lyricsRepository.saveExternalLyricsToSongFile(
        currentPlayingSongUri.uri,
        currentPlayingSongUri.metadata.title,
        currentPlayingSongUri.metadata.albumName.orEmpty(),
        currentPlayingSongUri.metadata.artistName.orEmpty()
      )
    }
  }
}