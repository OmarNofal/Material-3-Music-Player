package com.omar.musica.store.lyrics

import com.omar.musica.model.lyrics.LyricsFetchSource
import com.omar.musica.model.lyrics.PlainLyrics
import com.omar.musica.model.lyrics.SynchronizedLyrics


sealed interface LyricsResult {

    data object NotFound: LyricsResult

    data object NetworkError: LyricsResult

    data class FoundPlainLyrics(
        val plainLyrics: PlainLyrics,
        val lyricsSource: LyricsFetchSource
    ): LyricsResult

    data class FoundSyncedLyrics(
        val syncedLyrics: SynchronizedLyrics,
        val lyricsSource: LyricsFetchSource
    ): LyricsResult
}
