package com.omar.musica.playback.state

import androidx.media3.common.Player

enum class RepeatMode {
    REPEAT_ALL, REPEAT_SONG, NO_REPEAT;

    companion object {
        fun fromPlayer(repeatMode: Int): RepeatMode =
            when (repeatMode) {
                Player.REPEAT_MODE_OFF -> NO_REPEAT
                Player.REPEAT_MODE_ONE -> REPEAT_SONG
                else -> REPEAT_ALL
            }
    }

    fun next() = when (this) {
        REPEAT_ALL -> REPEAT_SONG
        REPEAT_SONG -> NO_REPEAT
        NO_REPEAT -> REPEAT_ALL
    }

    fun toPlayer() = when(this) {
        REPEAT_ALL -> Player.REPEAT_MODE_ALL
        REPEAT_SONG -> Player.REPEAT_MODE_ONE
        NO_REPEAT -> Player.REPEAT_MODE_OFF
    }
}