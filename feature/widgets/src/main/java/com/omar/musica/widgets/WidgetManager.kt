package com.omar.musica.widgets

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.glance.appwidget.updateAll
import coil.request.ImageRequest
import coil.request.SuccessResult
import coil.size.Size
import coil.transform.CircleCropTransformation
import com.omar.musica.model.playback.PlayerState
import com.omar.musica.playback.PlaybackManager
import com.omar.musica.ui.albumart.SongAlbumArtModel
import com.omar.musica.ui.albumart.inefficientAlbumArtImageLoader
import com.omar.musica.ui.albumart.toSongAlbumArtModel
import com.omar.musica.widgets.ui.WidgetState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class WidgetManager @Inject constructor(
    @ApplicationContext private val context: Context,
    playbackManager: PlaybackManager
) {

    private val scope = CoroutineScope(Dispatchers.IO)
    private val imageLoader = context.inefficientAlbumArtImageLoader()

    val state = playbackManager.state.map {
        updateWidgets()

        if (it.core.currentPlayingSong == null)
            return@map WidgetState.NoQueue

        val metadata = it.core.currentPlayingSong!!.metadata
        val bitmap = getSongBitmap(it.core.currentPlayingSong.toSongAlbumArtModel())
        val isPlaying = it.core.playbackState.playerState == PlayerState.PLAYING

        WidgetState.Playback(
            title = metadata.title,
            artist = metadata.artistName ?: "<unknown>",
            isPlaying = isPlaying,
            image = bitmap
        )
    }.stateIn(scope, SharingStarted.Eagerly, WidgetState.NoQueue)

    private suspend fun getSongBitmap(songAlbumArtModel: SongAlbumArtModel): Bitmap? =
        withContext(Dispatchers.IO) {
            val request = ImageRequest.Builder(context)
                .data(songAlbumArtModel)
                .transformations(CircleCropTransformation())
                .build()

            val result = imageLoader.execute(request)
            if (result !is SuccessResult) return@withContext null

            (result.drawable as BitmapDrawable).bitmap
        }


    private fun updateWidgets() {
        scope.launch {
            CardWidget().updateAll(context)
            CircleWidget().updateAll(context)
        }
    }

}