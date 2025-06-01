package com.omar.nowplaying.lyrics

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CopyAll
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.omar.musica.model.lyrics.LyricsFetchSource

/**
 * Shows actions which can be done to the lyrics depending on whether the lyrics
 * are fetched from API or from the song metadata
 */
@Composable
fun LyricsActions(
  modifier: Modifier,
  isShown: Boolean,
  lyricsFetchSource: LyricsFetchSource,
  onSaveToSongFile: () -> Unit,
  onFetchWebVersion: () -> Unit,
  onCopy: () -> Unit,
) {
  AnimatedVisibility(modifier = modifier, visible = isShown, enter = fadeIn(), exit = fadeOut()) {
    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.End) {

      val rippleColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.26f)
      IconButton(
        modifier = Modifier.background(rippleColor, CircleShape),
        onClick = if (lyricsFetchSource == LyricsFetchSource.FROM_INTERNET) onSaveToSongFile else onFetchWebVersion
      ) {
        val icon = if (lyricsFetchSource == LyricsFetchSource.FROM_INTERNET) Icons.Rounded.Save else Icons.Rounded.Language
        Icon(imageVector = icon, contentDescription = null)
      }
      Spacer(modifier = Modifier.height(6.dp))
      IconButton(
        modifier = Modifier.background(rippleColor, CircleShape),
        onClick = onCopy
      ) {
        Icon(imageVector = Icons.Rounded.CopyAll, contentDescription = null)
      }
    }
  }
}