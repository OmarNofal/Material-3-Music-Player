package com.omar.musica.widgets

import android.content.ComponentName
import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.omar.musica.playback.PlaybackService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class TogglePlaybackAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) = withContext(Dispatchers.IO) {
        val mc = getMediaControllerFuture(context.applicationContext).get()
        withContext(Dispatchers.Main) {
            mc.prepare()
            mc.playWhenReady = !mc.playWhenReady
        }
    }
}

class NextSongAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) = withContext(Dispatchers.IO) {
        val mc = getMediaControllerFuture(context.applicationContext).get()
        withContext(Dispatchers.Main) {
            mc.seekToNext()
        }
    }
}

class PreviousSongAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) = withContext(Dispatchers.IO) {
        val mc = getMediaControllerFuture(context.applicationContext).get()
        withContext(Dispatchers.Main) {
            mc.seekToPrevious()
        }
    }
}

private fun getMediaControllerFuture(context: Context): ListenableFuture<MediaController> {
    val sessionToken =
        SessionToken(context, ComponentName(context, PlaybackService::class.java))
    return MediaController.Builder(context, sessionToken)
        .setApplicationLooper(context.mainLooper)
        .buildAsync()
}