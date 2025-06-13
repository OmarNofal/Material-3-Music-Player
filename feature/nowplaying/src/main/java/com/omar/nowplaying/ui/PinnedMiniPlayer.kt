package com.omar.nowplaying.ui


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Pause
import androidx.compose.material.icons.twotone.PlayArrow
import androidx.compose.material.icons.twotone.SkipNext
import androidx.compose.material.icons.twotone.SkipPrevious
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.omar.musica.model.playback.PlayerState
import com.omar.musica.ui.albumart.toSongAlbumArtModel
import com.omar.musica.ui.theme.ManropeFontFamily
import com.omar.musica.ui.theme.font
import com.omar.nowplaying.NowPlayingState
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MiniPlayer(
    modifier: Modifier,
    nowPlayingState: NowPlayingState,
    showExtraControls: Boolean,
    songProgressProvider: () -> Float,
    enabled: Boolean,
    onTogglePlayback: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
) {

    if (nowPlayingState is NowPlayingState.NotPlaying) {
        return
    }

    val state = (nowPlayingState as NowPlayingState.Playing)
    val song = state.queue[state.songIndex]

    Column(modifier = modifier) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            CrossFadingAlbumArt(
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1.0f)
                    .scale(0.7f)
                    .shadow(2.dp, shape = RoundedCornerShape(4.dp))
                    .clip(RoundedCornerShape(8.dp)),
                containerModifier = Modifier.padding(start = 4.dp),
                songAlbumArtModel = song.toSongAlbumArtModel(),
                errorPainterType = ErrorPainterType.PLACEHOLDER
            )

            Spacer(modifier = Modifier.width(4.dp))

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier = Modifier.basicMarquee(Int.MAX_VALUE),
                    text = song.metadata.title,
                    fontWeight = FontWeight.Normal,
                    style = MaterialTheme.typography.bodyLarge,
                    fontFamily = ManropeFontFamily
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    modifier = Modifier,
                    text = song.metadata.artistName.orEmpty(),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Light,
                    maxLines = 1,
                    fontFamily = ManropeFontFamily
                )
            }

            Spacer(modifier = Modifier.width(3.dp))

            Row {
                AnimatedVisibility(visible = showExtraControls) {
                    IconButton(onClick = onPrevious, enabled = enabled) {
                        Icon(
                            imageVector = Icons.TwoTone.SkipPrevious,
                            contentDescription = "Previous"
                        )
                    }
                }
                Box(contentAlignment = Alignment.Center) {
                    IconButton(
                        onClick = onTogglePlayback,
                        enabled = enabled
                    ) {
                        val icon =
                            if (state.playbackState == PlayerState.PLAYING) Icons.TwoTone.Pause else Icons.TwoTone.PlayArrow
                        Icon(imageVector = icon, contentDescription = null)
                    }
                }
                AnimatedVisibility(visible = showExtraControls) {
                    IconButton(onClick = onNext, enabled = enabled) {
                        Icon(imageVector = Icons.TwoTone.SkipNext, contentDescription = "Next")
                    }
                }
            }

        }

        SongLinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
                .height(3.dp),
            songProgressProvider
        )
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
        progress = { progress.value },
        modifier = modifier,
        strokeCap = StrokeCap.Round,
        strokeWidth = 2.dp,
        trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
    )
}

@Composable
fun SongLinearProgressIndicator(
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
    LinearProgressIndicator(
        progress = { progress.value },
        modifier = modifier,
        strokeCap = StrokeCap.Square,
        trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),

    )
}


enum class BarState {
    COLLAPSED, EXPANDED
}

