package com.omar.musica.widgets

import android.content.Context
import android.graphics.Bitmap
import androidx.glance.appwidget.updateAll
import com.omar.musica.model.playback.PlayerState
import com.omar.musica.playback.PlaybackManager
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class WidgetManager @Inject constructor(
    private val playbackManager: PlaybackManager,
    @ApplicationContext private val context: Context
) {

    private val scope = CoroutineScope(Dispatchers.IO)

    private fun updateWidgets() {
        scope.launch {
            PlaybackWidget().updateAll(context)
        }
    }

    val state = playbackManager.state.map {
        updateWidgets()
        if (it.currentPlayingSong == null)
            return@map WidgetState.NoQueue
        val metadata = it.currentPlayingSong!!.metadata
        WidgetState.Playback(
            metadata.title,
            metadata.artistName ?: "<unknown>",
            isPlaying = it.playbackState.playerState == PlayerState.PLAYING,
            null
        )
    }

    fun toggle() {
        playbackManager.togglePlayback()
    }

    fun next() {
        playbackManager.playNextSong()
    }

    fun previous() {
        playbackManager.playPreviousSong()
    }

}

sealed interface WidgetState {
    data object NoQueue : WidgetState
    data class Playback(
        val title: String,
        val artist: String,
        val isPlaying: Boolean,
        val image: Bitmap?
    ) : WidgetState
}