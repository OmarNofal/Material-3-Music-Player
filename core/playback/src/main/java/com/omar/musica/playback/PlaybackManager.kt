package com.omar.musica.playback

import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaItem.RequestMetadata
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.session.MediaController
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import com.omar.musica.model.playback.PlaybackState
import com.omar.musica.model.playback.PlayerState
import com.omar.musica.playback.extensions.EXTRA_SONG_ORIGINAL_INDEX
import com.omar.musica.playback.state.MediaPlayerState
import com.omar.musica.playback.state.MediaPlayerStateCore
import com.omar.musica.store.MediaRepository
import com.omar.musica.store.PlaylistsRepository
import com.omar.musica.store.model.queue.Queue
import com.omar.musica.store.model.queue.QueueItem
import com.omar.musica.store.model.song.Song
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton


/**
 * This singleton class represents the interface between the application and the media playback service running in the background.
 * It exposes the current state of the MediaSessionService as state flows so UI can update accordingly
 * It provides methods to manipulate the service, like changing the queue, pausing, rewinding, etc...
 */
@Singleton
class PlaybackManager @Inject constructor(
  @ApplicationContext context: Context,
  private val mediaRepository: MediaRepository,
  private val playlistsRepository: PlaylistsRepository
) : PlaylistPlaybackActions {

  private val coroutineScope = CoroutineScope(Dispatchers.IO)
  private lateinit var mediaController: MediaController

  init {
    initMediaController(context)
  }

  private val _stateCore = MutableStateFlow(MediaPlayerStateCore.empty)

  // 2. 公开的 state 将结合 _playerCoreState 和 isFavoriteFlow
  val state: StateFlow<MediaPlayerState> = _stateCore
    .flatMapLatest { core -> // 当 song 改变时，切换到新的 isFavoriteFlow
      if (core.currentPlayingSong == null || core.currentPlayingSong.uri.toString().isEmpty()) {
        flowOf(MediaPlayerState(core, false)) // 没有歌曲或URI为空
      } else {
        val str = core.currentPlayingSong.uri.toString()
        playlistsRepository.getIsFavoriteFlow(core.currentPlayingSong.uri.toString())
          .map { isFavorite -> // 将 isFavorite 值映射到完整的 MediaPlayerState
            MediaPlayerState(core, isFavorite)
          }
      }
    }
    .distinctUntilChanged() // 避免重复发射相同的状态
    .stateIn( // 将结果 Flow 转换为 StateFlow
      scope = coroutineScope,
      started = SharingStarted.WhileSubscribed(5000L), // 5秒后没有订阅者则停止上游
      initialValue = MediaPlayerState.empty // 初始值
    )

  //  val state: StateFlow<MediaPlayerStateCore>
//    get() = _stateCore

  val queue = MutableStateFlow(Queue.EMPTY)

  val currentSongProgress: Float
    get() = mediaController.currentPosition.toFloat() / mediaController.duration.toFloat()

  val currentSongProgressMillis
    get() = mediaController.currentPosition

  val playbackParameters: Pair<Float, Float>
    get() {
      val p = mediaController.playbackParameters
      return p.speed to p.pitch
    }

  private val playbackState: PlayerState
    get() {
      return when (mediaController.playbackState) {
        Player.STATE_READY -> {
          if (mediaController.playWhenReady) PlayerState.PLAYING
          else PlayerState.PAUSED
        }

        Player.STATE_BUFFERING -> PlayerState.BUFFERING
        else -> PlayerState.PAUSED
      }
    }


  fun clearQueue() {
    mediaController.clearMediaItems()
  }

  fun toggleFavorite() {
    val currentSong = _stateCore.value.currentPlayingSong ?: return
    if (currentSong.uri.toString().isEmpty()) return
    val isFavorite = state.value.isSongFavorite
    if (isFavorite) {
      playlistsRepository.removeSongFromFavorites(currentSong.uri.toString())
    } else {
      playlistsRepository.addSongToFavorites(currentSong.uri.toString())
    }
  }

  /**
   * Toggle the player state
   */
  fun togglePlayback() {
    mediaController.prepare()
    mediaController.playWhenReady = !mediaController.playWhenReady
  }

  /**
   * Skip forward in currently playing song
   */
  fun forward() {
    mediaController.sendCustomCommand(
      SessionCommand(Commands.JUMP_FORWARD, bundleOf()),
      bundleOf()
    )
  }

  /**
   * Skip backward in currently playing song
   */
  fun backward() {
    mediaController.sendCustomCommand(
      SessionCommand(Commands.JUMP_BACKWARD, bundleOf()),
      bundleOf()
    )
  }

  /**
   * Jumps to the next song in the queue
   */
  fun playNextSong() {
    mediaController.prepare()
    mediaController.seekToNext()
  }

  /**
   * Jumps to the previous song in the queue
   */
  fun playPreviousSong() {
    mediaController.prepare()
    mediaController.seekToPrevious()
  }

  fun playSongAtIndex(index: Int) {
    mediaController.seekTo(index, 0)
  }

  fun removeSongAtIndex(index: Int) {
    mediaController.removeMediaItem(index)
  }

  fun reorderSong(from: Int, to: Int) {
    mediaController.moveMediaItem(from, to)
  }

  fun seekToPosition(progress: Float) {
    val controller = mediaController
    val songDuration = controller.duration
    controller.seekTo((songDuration * progress).toLong())
  }

  fun seekToPositionMillis(millis: Long) {
    mediaController.seekTo(millis)
  }

  /**
   * Changes the current playlist of the player and starts playing the song at the specified index
   */
  fun setPlaylistAndPlayAtIndex(playlist: List<Song>, index: Int = 0) {
    if (playlist.isEmpty()) return
    val mediaItems = playlist.toMediaItems(0)
    stopPlayback() // release everything
    mediaController.apply {
      setMediaItems(mediaItems, index, 0)
      prepare()
      play()
    }
  }

  /** Randomize the order of the list of songs and play */
  fun shuffle(songs: List<Song>) {
    if (songs.isEmpty()) return
    val shuffled = songs.shuffled()
    stopPlayback()
    mediaController.apply {
      setMediaItems(shuffled.toMediaItems(0), 0, 0)
      prepare()
      play()
    }
  }

  fun shuffleNext(songs: List<Song>) {
    val shuffled = songs.shuffled()
    val currentIndex = mediaController.currentMediaItemIndex
    mediaController.apply {
      addMediaItems(currentIndex + 1, shuffled.toMediaItems(getMaximumOriginalId() + 1))
    }
  }

  fun playNext(songs: List<Song>) {
    if (songs.isEmpty()) return
    val mediaItems = songs.toMediaItems(getMaximumOriginalId() + 1)
    val currentIndex = mediaController.currentMediaItemIndex
    mediaController.addMediaItems(currentIndex + 1, mediaItems)
    mediaController.prepare()
  }

  fun addToQueue(songs: List<Song>) {
    val mediaItems = songs.toMediaItems(getMaximumOriginalId() + 1)
    mediaController.addMediaItems(mediaItems)
    mediaController.prepare()
  }

  override fun playPlaylist(playlistId: Int) {
    coroutineScope.launch(Dispatchers.IO) {
      val songs = playlistsRepository.getPlaylistSongs(playlistId)
      withContext(Dispatchers.Main) {
        setPlaylistAndPlayAtIndex(songs)
      }
    }
  }

  override fun addPlaylistToNext(playlistId: Int) {
    coroutineScope.launch(Dispatchers.IO) {
      val songs = playlistsRepository.getPlaylistSongs(playlistId)
      withContext(Dispatchers.Main) {
        playNext(songs)
      }
    }
  }

  override fun addPlaylistToQueue(playlistId: Int) {
    coroutineScope.launch(Dispatchers.IO) {
      val songs = playlistsRepository.getPlaylistSongs(playlistId)
      withContext(Dispatchers.Main) {
        addToQueue(songs)
      }
    }
  }

  override fun shufflePlaylist(playlistId: Int) {
    coroutineScope.launch(Dispatchers.IO) {
      val songs = playlistsRepository.getPlaylistSongs(playlistId)
      if (songs.isEmpty()) return@launch
      withContext(Dispatchers.Main) {
        shuffle(songs)
      }
    }
  }

  override fun shufflePlaylistNext(playlistId: Int) {
    coroutineScope.launch(Dispatchers.IO) {
      val songs = playlistsRepository.getPlaylistSongs(playlistId)
      withContext(Dispatchers.Main) {
        shuffleNext(songs)
      }
    }
  }

  private fun getMaximumOriginalId(): Int {
    val count = mediaController.mediaItemCount
    if (count == 0) return 0
    return (0 until count).maxOf {
      val mediaItem = mediaController.getMediaItemAt(it)
      mediaItem.requestMetadata.extras!!.getInt(EXTRA_SONG_ORIGINAL_INDEX)
    }
  }

  fun getCurrentSongIndex() = mediaController.currentMediaItemIndex

  fun setSleepTimer(minutes: Int, finishLastSong: Boolean) {
    mediaController.sendCustomCommand(
      SessionCommand(Commands.SET_SLEEP_TIMER, bundleOf()),
      bundleOf(
        "MINUTES" to minutes,
        "FINISH_LAST_SONG" to finishLastSong
      )
    )
  }

  fun setPlaybackParameters(speed: Float, pitch: Float) {
    mediaController.playbackParameters = PlaybackParameters(speed, pitch)
  }

  fun deleteSleepTimer() {
    mediaController.sendCustomCommand(
      SessionCommand(Commands.CANCEL_SLEEP_TIMER, Bundle.EMPTY), Bundle.EMPTY
    )
  }

  fun toggleRepeatMode() {
    mediaController.repeatMode =
      getRepeatModeFromPlayer(mediaController.repeatMode).next().toPlayer()
  }

  fun toggleShuffleMode() {
    mediaController.shuffleModeEnabled = !mediaController.shuffleModeEnabled
  }

  private fun updateState() {
    val currentMediaItem = mediaController.currentMediaItem ?: return updateToEmptyState()
    val songUri = currentMediaItem.requestMetadata.mediaUri ?: return updateToEmptyState()
    val song = mediaRepository.songsFlow.value.getSongByUri(songUri.toString())
      ?: return updateToEmptyState()
    val playbackState = PlaybackState(
      playbackState,
      mediaController.shuffleModeEnabled,
      getRepeatModeFromPlayer(mediaController.repeatMode)
    )
    _stateCore.value = MediaPlayerStateCore(song, playbackState)
  }

  private fun updateToEmptyState() {
    _stateCore.value = MediaPlayerStateCore.empty
  }

  private fun stopPlayback() {
    mediaController.stop()
  }

  private fun initMediaController(context: Context) {
    val sessionToken =
      SessionToken(context, ComponentName(context, PlaybackService::class.java))
    val mediaControllerFuture = MediaController.Builder(context, sessionToken)
      .setApplicationLooper(context.mainLooper)
      .buildAsync()
    mediaControllerFuture.addListener(
      {
        mediaController = mediaControllerFuture.get()
        updateState()
        updateQueue()
        attachListeners()
      },
      MoreExecutors.directExecutor()
    )
  }

  private fun attachListeners() {
    mediaController.addListener(object : Player.Listener {
      override fun onTimelineChanged(timeline: Timeline, reason: Int) {
        updateState()
        if (reason == Player.TIMELINE_CHANGE_REASON_PLAYLIST_CHANGED) {
          updateQueue()
        }
      }

      override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
        updateState()
      }

      override fun onRepeatModeChanged(repeatMode: Int) {
        updateState()
      }

      override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        Timber.d("Media transitioned to ${mediaItem?.requestMetadata?.mediaUri}")
        updateState()
      }

      override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)
        updateState()
      }

      override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
        updateState()
      }

      override fun onIsPlayingChanged(isPlaying: Boolean) {
        updateState()
      }

    })
  }

  private fun updateQueue() {
    val count = mediaController.mediaItemCount

    val songsLibrary = mediaRepository.songsFlow.value

    if (count <= 0) {
      val q = Queue(listOf())
      queue.value = q
      return
    }

    val queueItems = (0 until count).mapNotNull { i ->
      val mediaItem = mediaController.getMediaItemAt(i)
      val requestMetadata = mediaItem.requestMetadata
      val song = songsLibrary.getSongByUri(requestMetadata.mediaUri.toString())
        ?: return@mapNotNull null
      QueueItem(
        song,
        requestMetadata.extras?.getInt(EXTRA_SONG_ORIGINAL_INDEX, i) ?: i
      )
    }

    val q = Queue(queueItems)
    queue.value = q
  }

  private fun Song.toMediaItem(index: Int) =
    MediaItem.Builder()
      .setUri(uri)
      .setMediaMetadata(
        MediaMetadata.Builder()
          .setMediaType(MediaMetadata.MEDIA_TYPE_MUSIC)
          .setArtist(metadata.artistName)
          .setAlbumTitle(metadata.albumName)
          .setTitle(metadata.title)
          .build()
      )
      .setRequestMetadata(
        RequestMetadata.Builder().setMediaUri(uri)
          .setExtras(bundleOf(EXTRA_SONG_ORIGINAL_INDEX to index))
          .build() // to be able to retrieve the URI easily
      )
      .build()

  private fun List<Song>.toMediaItems(startingIndex: Int) = mapIndexed { index, song ->
    song.toMediaItem(startingIndex + index)
  }

}