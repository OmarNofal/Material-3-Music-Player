package com.omar.musica.playback

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C.AUDIO_CONTENT_TYPE_MUSIC
import androidx.media3.common.C.USAGE_MEDIA
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.SeekParameters
import androidx.media3.session.CommandButton
import androidx.media3.session.DefaultMediaNotificationProvider
import androidx.media3.session.MediaNotification
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionResult
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture

class PlaybackService : MediaSessionService() {

    private lateinit var player : Player
    private lateinit var mediaSession: MediaSession

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    override fun onCreate() {
        super.onCreate()

        player = ExoPlayer.Builder(applicationContext)
            .setAudioAttributes(
                AudioAttributes.Builder().setContentType(AUDIO_CONTENT_TYPE_MUSIC).setUsage(USAGE_MEDIA).build(),
                true
            )
            .setHandleAudioBecomingNoisy(true)
            .build()



        val customCommands = buildCommandButtons()
        val callback = object : MediaSession.Callback {
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
                if (CUSTOM_COMMAND_JUMP_FORWARD == customCommand.customAction) {
                    player.seekForward()
                } else if (CUSTOM_COMMAND_JUMP_BACKWARD == customCommand.customAction) {
                    player.seekBack()
                }
                return Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
            }
        }

        mediaSession = MediaSession
            .Builder(applicationContext, player)
            .setCallback(callback)
            .setCustomLayout(buildCommandButtons())
            .setSessionActivity(buildPendingIntent())
            .build()
    }


    private fun buildPendingIntent(): PendingIntent {
        val intent = Intent(this, Class.forName("com.omar.musica.MainActivity")).apply {
            action = VIEW_MEDIA_SCREEN_ACTION
        }
        return PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_IMMUTABLE)
    }



    private fun buildCommandButtons(): List<CommandButton> {
        val rewindCommandButton = CommandButton.Builder()
            .setEnabled(true)
            .setDisplayName("Jump Backward")
            .setSessionCommand(SessionCommand(CUSTOM_COMMAND_JUMP_BACKWARD, Bundle()))
            .setIconResId(R.drawable.baseline_fast_rewind_24).build()
        val fastForwardCommandButton = CommandButton.Builder()
            .setEnabled(true)
            .setSessionCommand(SessionCommand(CUSTOM_COMMAND_JUMP_FORWARD, Bundle()))
            .setDisplayName("Jump Forward")
            .setIconResId(R.drawable.baseline_fast_forward_24).build()
        return listOf(rewindCommandButton, fastForwardCommandButton)
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        Log.i(TAG, "Controller request: ${controllerInfo.packageName}")
        return mediaSession
    }

    override fun onDestroy() {
        mediaSession.run {
            player.release()
            release()
        }
        super.onDestroy()
    }

    companion object {
        const val TAG = "MEDIA_SESSION"
        const val ACTION_PLAY = "PLAY"
        const val EXTRA_URI = "EXTRA_URI"
        const val VIEW_MEDIA_SCREEN_ACTION = "MEDIA_SCREEN_ACTION"
        const val CUSTOM_COMMAND_JUMP_FORWARD = "JUMP_FORWARD"
        const val CUSTOM_COMMAND_JUMP_BACKWARD = "JUMP_BACKWARD"
    }

}