package com.omar.musica.store

import android.content.ContentUris
import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.omar.musica.model.Song
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Singleton


private const val TAG = "MediaRepository"


@Singleton
class MediaRepository(
    private val context: Context,
    scope: CoroutineScope = CoroutineScope(Dispatchers.Default),
) {


    private var mediaSyncJob: Job? = null

    /** A state flow that contains all the songs in the user's device
    Automatically updates when the MediaStore changes
     */
    val songsFlow =
        callbackFlow {

            Log.d(TAG, "Initializing callback flow to get all songs")
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

        }.flowOn(Dispatchers.IO).stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5000, 5000),
            initialValue = listOf()
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
                MediaStore.Audio.Media.ALBUM
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
                    Song(
                        title = c.getString(titleColumn),
                        artist = c.getString(artistColumn),
                        album = c.getString(albumColumn),
                        length = c.getLong(durationColumn),
                        location = c.getString(pathColumn),
                        size = c.getLong(sizeColumn),
                        fileName = cursor.getString(fileNameColumn),
                        uriString = ContentUris.withAppendedId(
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                            cursor.getInt(idColumn).toLong()
                        ).toString()
                    ).also(results::add)
                }
            }

            results
        }
    }

}