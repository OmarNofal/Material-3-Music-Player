package com.omar.nowplaying.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import com.omar.musica.playback.state.RepeatMode
import com.omar.musica.ui.model.SongUi


@Composable
fun PlayerFooter(
    modifier: Modifier,
    songUi: SongUi,
    isShuffleOn: Boolean,
    repeatMode: RepeatMode,
    onOpenQueue: () -> Unit,
    onToggleRepeatMode: () -> Unit,
    onToggleShuffle: () -> Unit,
) {

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        // queue button
        TextButton(
            onClick = onOpenQueue
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.QueueMusic,
                contentDescription = "Queue"
            )
            Text(text = "Queue")
        }


        // spacer
        Spacer(modifier = Modifier.weight(1f))

        // icons
        IconButton(onClick = onToggleRepeatMode) {
            Icon(imageVector = repeatMode.getIconVector(), contentDescription = "Repeat Mode")
        }
        IconButton(onClick = onToggleShuffle) {
            Icon(
                modifier = if (isShuffleOn) Modifier else Modifier.alpha(0.5f),
                imageVector = Icons.Rounded.Shuffle,
                contentDescription = "Repeat Mode"
            )
        }
        NowPlayingOverflowMenu(options = rememberNowPlayingOptions(songUi = songUi))
    }

}