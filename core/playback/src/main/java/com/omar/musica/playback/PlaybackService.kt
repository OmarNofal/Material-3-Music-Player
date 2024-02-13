package com.omar.musica.playback

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C.AUDIO_CONTENT_TYPE_MUSIC
import androidx.media3.common.C.USAGE_MEDIA
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.CommandButton
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionResult
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.omar.musica.model.prefs.DEFAULT_JUMP_DURATION_MILLIS
import com.omar.musica.model.prefs.PlayerSettings
import com.omar.musica.store.QueueItem
import com.omar.musica.store.QueueRepository
import com.omar.musica.store.UserPreferencesRepository
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
class PlaybackService : MediaSessionService() {

    @Inject
    lateinit var userPreferencesRepository: UserPreferencesRepository

    @Inject
    lateinit var queueRepository: QueueRepository

    private lateinit var player: Player
    private lateinit var mediaSession: MediaSession

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private lateinit var playerSettings: StateFlow<PlayerSettings>


    override fun onCreate() {
        super.onCreate()

        player = buildPlayer()
        mediaSession = buildMediaSession()

        playerSettings = userPreferencesRepository.playerSettingsFlow
            .stateIn(
                scope,
                started = SharingStarted.Eagerly,
                PlayerSettings(DEFAULT_JUMP_DURATION_MILLIS)
            )

        recoverQueue()
        scope.launch(Dispatchers.Main) {
            while (isActive) {
                delay(10_000)
                saveCurrentPosition()
            }
        }
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

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    private fun buildPlayer(): ExoPlayer {
        return ExoPlayer.Builder(applicationContext)
            .setAudioAttributes(
                AudioAttributes.Builder().setContentType(AUDIO_CONTENT_TYPE_MUSIC)
                    .setUsage(USAGE_MEDIA).build(),
                true
            )
            .setHandleAudioBecomingNoisy(true)
            .setUseLazyPreparation(false)
            .build().apply {
                repeatMode = Player.REPEAT_MODE_ALL
            }
    }

    private fun recoverQueue() {
        scope.launch(Dispatchers.Main) {
            val queue = queueRepository.getQueue()

            val (lastSongUri, lastPosition) = restorePosition()
            val songIndex = queue.indexOfFirst { it.uri.toString() == lastSongUri }

            player.setMediaItems(
                queue.map { it.toMediaItem() },
                if (songIndex in queue.indices) songIndex else 0,
                lastPosition
            )
            player.prepare()
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
                } else if (Commands.JUMP_BACKWARD == customCommand.customAction) {
                    seekBackward()
                }
                return Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
            }
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

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
        runBlocking {
            saveCurrentPosition()
        }
        mediaSession.run {
            player.release()
            release()
        }
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        if (!player.playWhenReady) {
            onDestroy()
            stopSelf()
        }
    }

    private fun QueueItem.toMediaItem() =
        MediaItem.Builder()
            .setUri(uri.toString())
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setMediaType(MediaMetadata.MEDIA_TYPE_MUSIC)
                    .setArtist(artist)
                    .setAlbumTitle(albumTitle)
                    .setTitle(title)
                    .build()
            )
            .setRequestMetadata(
                MediaItem.RequestMetadata.Builder().setMediaUri(uri)
                    .build() // to be able to retrieve the URI easily
            )
            .build()


    companion object {
        const val TAG = "MEDIA_SESSION"
        const val VIEW_MEDIA_SCREEN_ACTION = "MEDIA_SCREEN_ACTION"
    }

}