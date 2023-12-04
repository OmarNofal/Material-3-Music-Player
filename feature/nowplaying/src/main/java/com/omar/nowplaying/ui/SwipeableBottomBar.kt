package com.omar.nowplaying.ui


import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import com.omar.musica.playback.state.PlayerState
import com.omar.nowplaying.NowPlayingState
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NowPlayingBarHeader(
    modifier: Modifier,
    nowPlayingState: NowPlayingState,
    songProgressProvider: () -> Float,
    enabled: Boolean,
    onTogglePlayback: () -> Unit,
) {

    if (nowPlayingState is NowPlayingState.NotPlaying) {
        return
    }

    val state = (nowPlayingState as NowPlayingState.Playing)
    val song = state.song


    Row(
        modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {

        CrossFadingAlbumArt(
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(1.0f),
            song = song,
            errorPainterType = ErrorPainterType.PLACEHOLDER
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            modifier = Modifier
                .weight(1f)
                .basicMarquee(),
            text = "${song.title} • ${song.album.orEmpty()}"
        )

        Spacer(modifier = Modifier.width(16.dp))

        Box(contentAlignment = Alignment.Center) {
            IconButton(
                modifier = Modifier.padding(end = 4.dp),
                onClick = onTogglePlayback,
                enabled = enabled
            ) {
                val icon =
                    if (state.playbackState == PlayerState.PLAYING) Icons.Rounded.Pause else Icons.Rounded.PlayArrow
                Icon(imageVector = icon, contentDescription = null)
            }
            SongCircularProgressIndicator(
                modifier = Modifier.padding(end = 4.dp),
                songProgressProvider
            )
        }
    }

}

@Composable
fun SongCircularProgressIndicator(
    modifier: Modifier,
    songProgressProvider: () -> Float,
) {
    val progress = remember {
        Animatable(0.0f)
    }
    LaunchedEffect(key1 = Unit) {
        while (isActive) {
            val newProgress = songProgressProvider()
            progress.animateTo(newProgress)
            delay(1000)
        }
    }
    CircularProgressIndicator(
        modifier = modifier,
        progress = progress.value,
        strokeCap = StrokeCap.Round
    )
}

enum class BarState {
    COLLAPSED, EXPANDED
}

