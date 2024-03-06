package com.omar.musica.store.lyrics

import android.net.Uri
import com.omar.musica.database.dao.LyricsDao
import com.omar.musica.database.entities.lyrics.LyricsEntity
import com.omar.musica.model.lyrics.LyricsFetchSource
import com.omar.musica.model.lyrics.PlainLyrics
import com.omar.musica.model.lyrics.SynchronizedLyrics
import com.omar.musica.network.data.LyricsSource
import com.omar.musica.network.model.NotFoundException
import com.omar.musica.store.MediaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class LyricsRepository @Inject constructor(
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
            val lyrics = tags.getFirstField(FieldKey.LYRICS)?.toString() ?: return@run
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

        // check in the DB
        kotlin.run {
            val lyricsEntity = lyricsDao.getSongLyrics(title, album, artist) ?: return@run

            if (lyricsEntity.syncedLyrics.isNotBlank()) {
                val synced = SynchronizedLyrics.fromString(lyricsEntity.syncedLyrics)
                if (synced != null) {
                    return@withContext LyricsResult.FoundSyncedLyrics(
                        synced,
                        LyricsFetchSource.FROM_INTERNET
                    )
                } else {
                    return@withContext LyricsResult.FoundPlainLyrics(
                        PlainLyrics.fromString(lyricsEntity.plainLyrics),
                        LyricsFetchSource.FROM_INTERNET
                    )
                }
            }
        }

        // finally check from the API
        return@withContext try {
            val lyricsNetwork =
                lyricsDataSource.getSongLyrics(artist, title, album, durationSeconds)
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
            LyricsResult.NotFound
        } catch (e: Exception) {
            LyricsResult.NetworkError
        }
    }


}
