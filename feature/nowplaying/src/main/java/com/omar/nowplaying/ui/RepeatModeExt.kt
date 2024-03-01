package com.omar.nowplaying.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.RepeatOne
import androidx.compose.material.icons.rounded.TextRotationNone
import com.omar.musica.model.playback.RepeatMode


fun RepeatMode.getIconVector() = when(this) {
    RepeatMode.REPEAT_SONG -> Icons.Rounded.RepeatOne
    RepeatMode.REPEAT_ALL -> Icons.Rounded.Repeat
    else -> Icons.Rounded.TextRotationNone
}