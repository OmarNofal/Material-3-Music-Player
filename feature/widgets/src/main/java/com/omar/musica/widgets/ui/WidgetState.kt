package com.omar.musica.widgets.ui

import android.graphics.Bitmap


sealed interface WidgetState {
    data object NoQueue : WidgetState
    data class Playback(
        val title: String,
        val artist: String,
        val isPlaying: Boolean,
        val image: Bitmap?
    ) : WidgetState
}