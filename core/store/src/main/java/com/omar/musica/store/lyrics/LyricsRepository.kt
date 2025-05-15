package com.omar.musica.store.lyrics

import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import com.omar.musica.database.dao.LyricsDao
import com.omar.musica.database.entities.lyrics.LyricsEntity
import com.omar.musica.model.lyrics.LyricsFetchSource
import com.omar.musica.model.lyrics.PlainLyrics
import com.omar.musica.model.lyrics.SynchronizedLyrics
import com.omar.musica.network.data.LyricsSource
import com.omar.musica.network.model.NotFoundException
import com.omar.musica.store.MediaRepository
import com.shabinder.jaudiotagger.audio.AudioFileIO
import com.shabinder.jaudiotagger.tag.FieldKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class LyricsRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val lyricsDataSource: LyricsSource,
    private val lyricsDao: LyricsDao,
    private val mediaRepository: MediaRepository
) {

    /**
     * Gets lyrics of some song with specific URI.
     * The song file itself is checked for any embedded lyrics,
     * if they are not found in the file, then we check the database for any cached
     * lyrics, and if they are not found in the database, we use the API service.
     */
    suspend fun getLyrics(
        uri: Uri,
        title: String,
        album: String,
        artist: String,
        durationSeconds: Int
    ): LyricsResult = withContext(Dispatchers.IO) {

        val audioFileIO = AudioFileIO().readFile(File(mediaRepository.getSongPath(uri)))
        val tags = audioFileIO.tagOrCreateAndSetDefault

        // check for embedded lyrics first
        kotlin.run {
            val lyrics = tags.getFirst(FieldKey.LYRICS) ?: return@run
            val syncedLyrics = SynchronizedLyrics.fromString(lyrics)
            if (syncedLyrics != null)
                return@withContext LyricsResult.FoundSyncedLyrics(
                    syncedLyrics,
                    LyricsFetchSource.FROM_SONG_METADATA
                )
            else if (lyrics.isNotBlank())
                return@withContext LyricsResult.FoundPlainLyrics(
                    PlainLyrics.fromString(lyrics),
                    LyricsFetchSource.FROM_SONG_METADATA
                )
        }

        return@withContext downloadLyricsFromInternet(title, album, artist, durationSeconds)
    }

    /**
     * First checks the database if the query is cached
     * and then falls back to the API
     */
    suspend fun downloadLyricsFromInternet(
        title: String,
        album: String,
        artist: String,
        durationSeconds: Int
    ): LyricsResult = withContext(Dispatchers.IO) {
        // check in the DB
        Log.d("lyrics", "Starting lyrics fetch")
        kotlin.run {
            Log.d("lyrics", "Checking DB")
            val lyricsEntity = lyricsDao.getSongLyrics(title, album, artist)

            Log.d("lyrics", "DB result: $lyricsEntity")
            if (lyricsEntity != null && lyricsEntity.syncedLyrics.isNotBlank()) {
                val synced = SynchronizedLyrics.fromString(lyricsEntity.syncedLyrics)
                if (synced != null) {
                    return@withContext LyricsResult.FoundSyncedLyrics(
                        synced,
                        LyricsFetchSource.FROM_INTERNET
                    )
                } else if (lyricsEntity.plainLyrics.isNotBlank()) {
                    return@withContext LyricsResult.FoundPlainLyrics(
                        PlainLyrics.fromString(lyricsEntity.plainLyrics),
                        LyricsFetchSource.FROM_INTERNET
                    )
                }
            }
        }

        Log.d("lyrics", "Downloading Lyrics")
        // finally check from the API
        return@withContext try {
            val lyricsNetwork =
                lyricsDataSource.getSongLyrics(artist, title, album, durationSeconds)
            Log.d("lyrics", "Downloaded: $lyricsNetwork")
            val syncedLyrics = SynchronizedLyrics.fromString(lyricsNetwork.syncedLyrics)
            lyricsDao.saveSongLyrics(
                LyricsEntity(
                    0,
                    title,
                    album,
                    artist,
                    lyricsNetwork.plainLyrics,
                    lyricsNetwork.syncedLyrics
                )
            )
            if (syncedLyrics != null)
                LyricsResult.FoundSyncedLyrics(
                    syncedLyrics,
                    LyricsFetchSource.FROM_INTERNET
                )
            else LyricsResult.FoundPlainLyrics(
                PlainLyrics.fromString(
                    lyricsNetwork.plainLyrics,
                ),
                LyricsFetchSource.FROM_INTERNET
            )
        } catch (e: NotFoundException) {
            Log.d("lyrics", "Downloaded: Not found")
            LyricsResult.NotFound
        } catch (e: Exception) {
            Log.d("lyrics", "Downloaded: ${e.stackTraceToString()}")
            LyricsResult.NetworkError
        }
    }



}
