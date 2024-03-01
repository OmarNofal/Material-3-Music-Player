package com.omar.musica.playback.state

import com.omar.musica.model.playback.PlaybackState
import com.omar.musica.store.model.song.Song

data class MediaPlayerState(
    val currentPlayingSong: Song?,
    val playbackState: PlaybackState
) {

    companion object {
        val empty = MediaPlayerState(null, PlaybackState.emptyState)
    }

}
