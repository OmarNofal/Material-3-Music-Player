package com.omar.nowplaying.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.rounded.Lyrics
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import com.omar.musica.model.playback.RepeatMode
import com.omar.musica.store.model.song.Song


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerFooter(
  modifier: Modifier,
  songUi: Song,
  isFavorite: Boolean,
  isShuffleOn: Boolean,
  repeatMode: RepeatMode,
  isLyricsOpen: Boolean,
  onToggleFavorite: () -> Unit,
  onOpenQueue: () -> Unit,
  onToggleLyrics: () -> Unit,
  onToggleRepeatMode: () -> Unit,
  onToggleShuffle: () -> Unit,
) {
  Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.Center
  ) {
    // Favorite button
    TooltipBox(
      tooltip = {
        PlainTooltip { Text(if (isFavorite) "Remove from favorites" else "Add to favorites") }
      },
      state = rememberTooltipState(),
      positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider()
    ) {
      IconButton(
        onClick = onToggleFavorite
      ) {
        Icon(
          imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
          contentDescription = if (isFavorite) "Favorited" else "Not favorited"
          // 可选：为图标添加颜色 tint = if (isFavorite) MaterialTheme.colorScheme.primary else LocalContentColor.current
        )
      }
    }

    IconButton(
      onClick = onOpenQueue
    ) {
      Icon(
        imageVector = Icons.AutoMirrored.Rounded.QueueMusic,
        contentDescription = "Queue"
      )
    }
    // spacer
    //Spacer(modifier = Modifier.weight(1f))
    // icons
    TooltipBox(
      tooltip = {
        PlainTooltip { Text("Lyrics") }
      },
      state = rememberTooltipState(),
      positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider()
    ) {
      IconButton(onClick = onToggleLyrics) {
        Icon(
          modifier = if (isLyricsOpen) Modifier else Modifier.alpha(0.5f),
          imageVector = Icons.Rounded.Lyrics,
          contentDescription = "Lyrics"
        )
      }
    }
    TooltipBox(
      tooltip = {
        PlainTooltip {
          Text(
            text = when (repeatMode) {
              RepeatMode.REPEAT_ALL -> "Repeat all"
              RepeatMode.REPEAT_SONG -> "Repeat this song"
              RepeatMode.NO_REPEAT -> "Don't Repeat"
            }
          )
        }
      },
      state = rememberTooltipState(),
      positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider()
    ) {
      IconButton(onClick = onToggleRepeatMode) {
        Icon(imageVector = repeatMode.getIconVector(), contentDescription = "Repeat Mode")
      }
    }
    TooltipBox(
      tooltip = {
        PlainTooltip {
          Text(text = "Shuffle Mode")
        }
      },
      state = rememberTooltipState(),
      positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider()
    ) {
      IconButton(onClick = onToggleShuffle) {
        Icon(
          modifier = if (isShuffleOn) Modifier else Modifier.alpha(0.5f),
          imageVector = Icons.Rounded.Shuffle,
          contentDescription = "Repeat Mode"
        )
      }
    }
    NowPlayingOverflowMenu(options = rememberNowPlayingOptions(songUi = songUi))
  }
}