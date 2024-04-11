package com.omar.nowplaying.floating

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import com.omar.musica.ui.albumart.BlurTransformation
import com.omar.musica.ui.albumart.SongAlbumArtModel
import com.omar.musica.ui.albumart.toSongAlbumArtModel
import com.omar.nowplaying.NowPlayingState
import com.omar.nowplaying.ui.CrossFadingAlbumArt
import com.omar.nowplaying.ui.ErrorPainterType


@Composable
fun FloatingMiniPlayer(
    modifier: Modifier,
    nowPlayingState: NowPlayingState,
    showExtraControls: Boolean,
    songProgressProvider: () -> Float,
    enabled: Boolean,
    onTogglePlayback: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
) {
    if (nowPlayingState !is NowPlayingState.Playing) return
    Box(modifier, contentAlignment = Alignment.Center) {

        // show background

        SongBlurredBackground(
            modifier = Modifier.fillMaxSize(),
            songAlbumArtModel = nowPlayingState.song.toSongAlbumArtModel()
        )

        // draw content
    }
}

@Composable
fun SongBlurredBackground(
    modifier: Modifier,
    songAlbumArtModel: SongAlbumArtModel
) {
    CrossFadingAlbumArt(
        modifier = modifier,
        songAlbumArtModel = songAlbumArtModel,
        errorPainterType = ErrorPainterType.SOLID_COLOR,
        blurTransformation = remember { BlurTransformation() },
        colorFilter = ColorFilter.tint(
            Color(0xFFBBBBBB), BlendMode.Multiply
        )
    )
}