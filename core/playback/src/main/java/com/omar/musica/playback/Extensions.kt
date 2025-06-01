package com.omar.musica.playback

import androidx.media3.common.Player
import com.omar.musica.model.playback.RepeatMode


fun getRepeatModeFromPlayer(repeatMode: Int): RepeatMode =
  when (repeatMode) {
    Player.REPEAT_MODE_OFF -> RepeatMode.NO_REPEAT
    Player.REPEAT_MODE_ONE -> RepeatMode.REPEAT_SONG
    else -> RepeatMode.REPEAT_ALL
  }


fun RepeatMode.toPlayer() = when (this) {
  RepeatMode.REPEAT_ALL -> Player.REPEAT_MODE_ALL
  RepeatMode.REPEAT_SONG -> Player.REPEAT_MODE_ONE
  RepeatMode.NO_REPEAT -> Player.REPEAT_MODE_OFF
}