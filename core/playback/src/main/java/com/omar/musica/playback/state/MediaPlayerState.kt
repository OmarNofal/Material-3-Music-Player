package com.omar.musica.playback.state

import com.omar.musica.model.playback.PlaybackState
import com.omar.musica.store.model.song.Song

data class MediaPlayerState(
    val currentPlayingSong: Song?,
    val songIndex: Int,
    val playbackState: PlaybackState
) {

    companion object {
        val empty = MediaPlayerState(null, -1, PlaybackState.emptyState)
    }

}
