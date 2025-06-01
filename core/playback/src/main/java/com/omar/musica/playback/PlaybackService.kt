package com.omar.musica.playback

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.media.AudioManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C.AUDIO_CONTENT_TYPE_MUSIC
import androidx.media3.common.C.USAGE_MEDIA
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ShuffleOrder
import androidx.media3.exoplayer.source.ShuffleOrder.UnshuffledShuffleOrder
import androidx.media3.session.CommandButton
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionResult
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.omar.musica.model.prefs.DEFAULT_JUMP_DURATION_MILLIS
import com.omar.musica.model.prefs.PlayerSettings
import com.omar.musica.playback.activity.ListeningAnalytics
import com.omar.musica.playback.extensions.toDBQueueItem
import com.omar.musica.playback.extensions.toMediaItem
import com.omar.musica.playback.timer.SleepTimerManager
import com.omar.musica.playback.timer.SleepTimerManagerListener
import com.omar.musica.playback.volume.AudioVolumeChangeListener
import com.omar.musica.playback.volume.VolumeChangeObserver
import com.omar.musica.store.PlaylistsRepository
import com.omar.musica.store.QueueRepository
import com.omar.musica.store.preferences.UserPreferencesRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class PlaybackService :
  MediaSessionService(),
  SleepTimerManagerListener,
  AudioVolumeChangeListener,
  Player.Listener {

  /*------------------------------ Properties ------------------------------*/
  @Inject
  lateinit var userPreferencesRepository: UserPreferencesRepository

  @Inject
  lateinit var playlistsRepository: PlaylistsRepository

  @Inject
  lateinit var queueRepository: QueueRepository

  @Inject
  lateinit var listeningAnalytics: ListeningAnalytics

  private lateinit var player: ExoPlayer
  private lateinit var mediaSession: MediaSession

  private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

  private lateinit var playerSettings: StateFlow<PlayerSettings>

  private lateinit var volumeObserver: VolumeChangeObserver

  private lateinit var sleepTimerManager: SleepTimerManager

  // We use this queue to restore back the original queue when
  // shuffle mode is enabled/disabled
  private var originalQueue: List<MediaItem> = listOf()

  /*------------------------------ Methods ------------------------------*/


  override fun onCreate() {
    super.onCreate()

    player = buildPlayer().apply { addListener(this@PlaybackService) }
    attachAnalyticsListener()

    mediaSession = buildMediaSession()

    sleepTimerManager = SleepTimerManager(this)
    player.addListener(sleepTimerManager)


    playerSettings = userPreferencesRepository.playerSettingsFlow
      .stateIn(
        scope,
        started = SharingStarted.Eagerly,
        PlayerSettings(
          DEFAULT_JUMP_DURATION_MILLIS,
          pauseOnVolumeZero = false,
          resumeWhenVolumeIncreases = false
        )
      )

    volumeObserver = VolumeChangeObserver(
      applicationContext,
      Handler(Looper.myLooper() ?: Looper.getMainLooper()),
      AudioManager.STREAM_MUSIC
    ).apply { register(this@PlaybackService) }

    recoverQueue()
    scope.launch(Dispatchers.Main) {
      while (isActive) {
        delay(10_000)
        saveCurrentPosition()
      }
    }
  }



  private fun attachAnalyticsListener() {
    player.addListener(listeningAnalytics)
  }

  private fun buildPendingIntent(): PendingIntent {
    val intent = Intent(this, Class.forName("com.omar.musica.MainActivity"))
    intent.action = VIEW_MEDIA_SCREEN_ACTION
    return PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_IMMUTABLE)
  }

  private suspend fun saveCurrentPosition() {
    val uriString = player.currentMediaItem?.requestMetadata?.mediaUri
    val position = player.currentPosition
    withContext(Dispatchers.IO) {
      userPreferencesRepository.saveCurrentPosition(uriString.toString(), position)
    }
  }

  private suspend fun restorePosition() = userPreferencesRepository.getSavedPosition()

  private fun buildCommandButtons(): List<CommandButton> {
    val rewindCommandButton = CommandButton.Builder()
      .setEnabled(true)
      .setDisplayName("Jump Backward")
      .setSessionCommand(SessionCommand(Commands.JUMP_BACKWARD, Bundle()))
      .setIconResId(R.drawable.outline_fast_rewind_24).build()
    val fastForwardCommandButton = CommandButton.Builder()
      .setEnabled(true)
      .setSessionCommand(SessionCommand(Commands.JUMP_FORWARD, Bundle()))
      .setDisplayName("Jump Forward")
      .setIconResId(R.drawable.outline_fast_forward_24).build()
    return listOf(rewindCommandButton, fastForwardCommandButton)
  }

  @SuppressLint("TimberArgCount")
  override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession {
    Timber.i(TAG, "Controller request: ${controllerInfo.packageName}")
    return mediaSession
  }

  @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
  private fun buildMediaSession(): MediaSession {
    return MediaSession
      .Builder(applicationContext, player)
      .setCallback(buildCustomCallback())
      .setCustomLayout(buildCommandButtons())
      .setSessionActivity(buildPendingIntent())
      .build()
  }

  private fun buildPlayer(): ExoPlayer {
    return ExoPlayer.Builder(applicationContext)
      .setAudioAttributes(
        AudioAttributes.Builder().setContentType(AUDIO_CONTENT_TYPE_MUSIC)
          .setUsage(USAGE_MEDIA).build(),
        true
      )
      .setHandleAudioBecomingNoisy(true)
      .build().apply {
        repeatMode = Player.REPEAT_MODE_ALL
      }
  }


  /**
   * Saves the currently playing queue in the database to retrieve it when starting
   * the application.
   */
  private fun saveQueue() {
    val mediaItems = List(player.mediaItemCount) { player.getMediaItemAt(it) }
    queueRepository.saveQueueFromDBQueueItems(mediaItems.map { it.toDBQueueItem() })
  }

  private fun recoverQueue() {
    scope.launch(Dispatchers.Main) {
      val queue = queueRepository.getQueue()

      val (lastSongUri, lastPosition) = restorePosition()
      val songIndex = queue.indexOfFirst { it.songUri.toString() == lastSongUri }

      player.setMediaItems(
        queue.mapIndexed { index, item -> item.toMediaItem(index) },
        if (songIndex in queue.indices) songIndex else 0,
        lastPosition
      )
      player.prepare()
    }
  }


  @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
  override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
    if (shuffleModeEnabled) {
      // user enabled shuffle, we have to store the current MediaItems

      val currentMediaItemIndex = player.currentMediaItemIndex
      val originalMediaItems = List(player.mediaItemCount) { i -> player.getMediaItemAt(i) }

      val shuffledQueue = originalMediaItems.toMutableList()
        .shuffled()
        .toMutableList()
        .apply {
          // remove the current playing media item because we will move it
          remove(player.getMediaItemAt(currentMediaItemIndex))
        }

      player.moveMediaItem(currentMediaItemIndex, 0)
      player.replaceMediaItems(1, Int.MAX_VALUE, shuffledQueue)
      player.setShuffleOrder(UnshuffledShuffleOrder(player.mediaItemCount))

      originalQueue = originalMediaItems
    } else {

      // user disabled shuffle mode, now we have to restore the original queue
      // and try to maintain the original order

      val currentMediaItemIndex = player.currentMediaItemIndex
      val currentMediaItem = player.getMediaItemAt(currentMediaItemIndex)

      // hashset to determine quickly if a media item was removed when the
      // user had shuffle enabled
      val mediaItemsSet = HashSet<MediaItem>()
      for (i in 0 until player.mediaItemCount) {
        mediaItemsSet.add(player.getMediaItemAt(i))
      }

      val songsBeforeCurrentPlaying = mutableListOf<MediaItem>()
      val songsAfterCurrentPlaying = mutableListOf<MediaItem>()

      var passedCurrentPlaying = false
      for (i in originalQueue) {
        if (i == currentMediaItem)
          passedCurrentPlaying = true
        else {
          if (i !in mediaItemsSet) continue
          if (passedCurrentPlaying)
            songsAfterCurrentPlaying.add(i)
          else
            songsBeforeCurrentPlaying.add(i)
        }
      }

      player.replaceMediaItems(0, currentMediaItemIndex, songsBeforeCurrentPlaying)
      player.replaceMediaItems(player.currentMediaItemIndex + 1, Int.MAX_VALUE, songsAfterCurrentPlaying)
    }
  }

  @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
  override fun onTimelineChanged(timeline: Timeline, reason: Int) {
    if (reason == Player.TIMELINE_CHANGE_REASON_PLAYLIST_CHANGED) {
      if (player.shuffleModeEnabled) {
        player.setShuffleOrder(UnshuffledShuffleOrder(player.mediaItemCount))
      }
      saveQueue()
    }
  }

  private fun buildCustomCallback(): MediaSession.Callback {
    val customCommands = buildCommandButtons()
    return object : MediaSession.Callback {
      override fun onConnect(
        session: MediaSession,
        controller: MediaSession.ControllerInfo
      ): MediaSession.ConnectionResult {
        val connectionResult = super.onConnect(session, controller)
        val availableSessionCommands = connectionResult.availableSessionCommands.buildUpon()
          .add(SessionCommand(Commands.SET_SLEEP_TIMER, Bundle.EMPTY))
          .add(SessionCommand(Commands.CANCEL_SLEEP_TIMER, Bundle.EMPTY))
        customCommands.forEach { commandButton ->
          // Add custom command to available session commands.
          commandButton.sessionCommand?.let { availableSessionCommands.add(it) }
        }
        return MediaSession.ConnectionResult.accept(
          availableSessionCommands.build(),
          connectionResult.availablePlayerCommands
        )
      }

      override fun onCustomCommand(
        session: MediaSession,
        controller: MediaSession.ControllerInfo,
        customCommand: SessionCommand,
        args: Bundle
      ): ListenableFuture<SessionResult> {
        if (Commands.JUMP_FORWARD == customCommand.customAction) {
          seekForward()
        }
        if (Commands.JUMP_BACKWARD == customCommand.customAction) {
          seekBackward()
        }
        if (Commands.SET_SLEEP_TIMER == customCommand.customAction) {
          val minutes = args.getInt("MINUTES", 0)
          val finishLastSong = args.getBoolean("FINISH_LAST_SONG", false)
          sleepTimerManager.schedule(minutes, finishLastSong)
        }
        if (Commands.CANCEL_SLEEP_TIMER == customCommand.customAction) {
          sleepTimerManager.deleteTimer()
        }
        return Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
      }
    }
  }


  private var pausedDueToVolume = false
  override fun onVolumeChanged(level: Int) {
    val shouldPause = playerSettings.value.pauseOnVolumeZero
    val shouldResume = playerSettings.value.resumeWhenVolumeIncreases
    if (level < 1 && shouldPause && player.playWhenReady) {
      player.pause()
      if (shouldResume)
        pausedDueToVolume = true
    }
    if (level >= 1 && pausedDueToVolume && shouldResume && !player.playWhenReady) {
      player.play()
      pausedDueToVolume = false
    }
    if (player.playWhenReady) pausedDueToVolume = false
  }

  override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
    // to avoid resuming playback when the headphones disconnect
    if (reason == Player.PLAY_WHEN_READY_CHANGE_REASON_AUDIO_BECOMING_NOISY)
      pausedDueToVolume = false
  }

  @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class) // If using UnstableApi for MediaItem
  override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
    super.onMediaItemTransition(mediaItem, reason) // It's good practice to call super
    if (mediaItem != null) {
      // MediaItem has transitioned. Let's get the uriString.
      // Based on your saveCurrentPosition, the URI is in requestMetadata.mediaUri
      val uriString = mediaItem.requestMetadata.mediaUri?.toString()
      if (uriString != null) {
        playlistsRepository.insertSongToRecentPlaylist(uriString)
        Timber.tag(TAG).i("MediaItem transitioned. New URI: $uriString (Reason: $reason)")
      } else {
        Timber.tag(TAG).w("MediaItem transitioned, but URI string is null. (Reason: $reason)")
      }
    } else {
      // This might happen if the playlist ends or is cleared.
      Timber.tag(TAG).i("MediaItem transitioned to null (e.g., end of playlist or queue cleared). (Reason: $reason)")
    }
  }

  fun seekForward() {
    val currentPosition = player.currentPosition
    player.seekTo(currentPosition + playerSettings.value.jumpInterval)
  }

  fun seekBackward() {
    val currentPosition = player.currentPosition
    player.seekTo(currentPosition - playerSettings.value.jumpInterval)
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    super.onStartCommand(intent, flags, startId)
    return START_STICKY
  }

  override fun onSleepTimerFinished() {
    player.pause()
  }

  override fun onDestroy() {
    scope.cancel()
    runBlocking {
      saveCurrentPosition()
    }
    mediaSession.run {
      player.release()
      release()
    }
    volumeObserver.unregister()
    super.onDestroy()
  }

  override fun onTaskRemoved(rootIntent: Intent?) {
    super.onTaskRemoved(rootIntent)
    if (!player.playWhenReady) {
      stopSelf()
    }
  }


  companion object {
    const val TAG = "MEDIA_SESSION"
    const val VIEW_MEDIA_SCREEN_ACTION = "MEDIA_SCREEN_ACTION"
  }

}