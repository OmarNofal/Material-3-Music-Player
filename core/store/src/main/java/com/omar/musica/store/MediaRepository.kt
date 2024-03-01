package com.omar.musica.store

import android.annotation.TargetApi
import android.content.ContentUris
import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.net.toUri
import com.omar.musica.store.model.Song
import com.omar.musica.store.model.SongLibrary
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton


private const val TAG = "MediaRepository"


@Singleton
class MediaRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    userPreferencesRepository: UserPreferencesRepository
) {


    private var mediaSyncJob: Job? = null
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)

    /** A state flow that contains all the songs in the user's device
    Automatically updates when the MediaStore changes
     */
    val songsFlow =
        callbackFlow {

            Timber.d(TAG, "Initializing callback flow to get all songs")

            var lastChangedUri: Uri? = null
            val observer = object : ContentObserver(null) {
                override fun onChange(selfChange: Boolean, uri: Uri?) {
                    // Sometimes Android sends duplicate callbacks when media changes for the same URI
                    // this ensures that we don't sync twice
                    if (uri == lastChangedUri) return
                    lastChangedUri = uri

                    if (mediaSyncJob?.isActive == true) {
                        // we are already syncing, no need to complicate things more
                        return
                    } else {
                        mediaSyncJob = launch {
                            send(getAllSongs())
                            mediaSyncJob = null
                        }
                    }
                }
            }

            context.contentResolver.registerContentObserver(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                true,
                observer
            )

            // Initial Sync
            mediaSyncJob = launch {
                send(getAllSongs())
            }

            awaitClose {
                context.contentResolver.unregisterContentObserver(observer)
            }

        }.combine(
            userPreferencesRepository.librarySettingsFlow.map { it.excludedFolders }
        ) { songs: List<Song>, excludedFolders: List<String> ->

            val filteredSongs = songs.filter { song ->
                !excludedFolders.any { folder ->
                    song.filePath.startsWith(folder)
                }
            }

            SongLibrary(filteredSongs)
        }.flowOn(Dispatchers.IO).stateIn(
                scope = scope,
                started = SharingStarted.Eagerly,
                initialValue = SongLibrary(listOf())
            )

    suspend fun getAllSongs(): List<Song> = withContext(Dispatchers.IO) {

        val projection =
            arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ALBUM_ID
            )

        with(context) {

            val cursor = contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                null, null, null, null
            ) ?: throw Exception("Invalid cursor")

            val results = mutableListOf<Song>()
            cursor.use { c ->
                while (c.moveToNext() && isActive) {
                    val idColumn = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
                    val fileNameColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)
                    val titleColumn = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
                    val artistColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
                    val durationColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)
                    val sizeColumn = cursor.getColumnIndex(MediaStore.Audio.Media.SIZE)
                    val pathColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DATA)
                    val albumColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)
                    val albumIdColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)

                    val fileUri = ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        cursor.getInt(idColumn).toLong()
                    )

                    try {
                        Song(
                            title = c.getString(titleColumn),
                            artist = c.getString(artistColumn) ?: "<unknown>",
                            album = c.getString(albumColumn) ?: "<unknown>",
                            length = c.getLong(durationColumn),
                            location = c.getString(pathColumn),
                            size = c.getLong(sizeColumn),
                            fileName = cursor.getString(fileNameColumn),
                            uriString = fileUri.toString(),
                            albumId = cursor.getLong(albumIdColumn)
                        ).apply { Timber.d(this.toString()) }.also(results::add)
                    } catch (e: Exception) {
                        Timber.e(e) // ignore the song for now if any problems occurred
                    }
                }
            }

            results
        }
    }


    @TargetApi(29)
    fun deleteSong(song: Song) {

        Timber.d("Deleting song $song")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Timber.e("Attempting to delete song in R or Higher. Use Activity Contracts instead")
            return
        }

        try {
            val file = File(song.filePath)
            file.delete()
            context.contentResolver.delete(song.uri, null, null)
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

}