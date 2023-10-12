package com.omar.nowplaying.ui

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import com.omar.musica.playback.state.PlayerState
import com.omar.musica.ui.common.SongAlbumArtImage
import com.omar.nowplaying.NowPlayingState


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NowPlayingBarHeader(
    modifier: Modifier,
    nowPlayingState: NowPlayingState,
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

        SongAlbumArtImage(
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(1.0f),
            song = song
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
            CircularProgressIndicator(modifier = Modifier.padding(end = 4.dp), progress = nowPlayingState.songProgress, strokeCap = StrokeCap.Round)
        }
    }

}

enum class BarState {
    COLLAPSED, EXPANDED
}

