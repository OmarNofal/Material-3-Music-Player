package com.omar.nowplaying.lyrics

import com.omar.musica.model.lyrics.PlainLyrics
import com.omar.musica.model.lyrics.SynchronizedLyrics
import com.omar.musica.store.model.song.Song


sealed interface LyricsScreenState {

    data object Loading: LyricsScreenState
    data object NotPlaying: LyricsScreenState
    data object SearchingLyrics: LyricsScreenState
    data class TextLyrics(
        val plainLyrics: PlainLyrics
    ): LyricsScreenState
    data class SyncedLyrics(
        val syncedLyrics: SynchronizedLyrics
    ): LyricsScreenState
    data class NoLyrics(val reason: NoLyricsReason): LyricsScreenState
}

enum class NoLyricsReason {
    NETWORK_ERROR, NOT_FOUND
}