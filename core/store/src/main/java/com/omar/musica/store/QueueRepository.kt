package com.omar.musica.store

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import com.omar.musica.database.dao.QueueDao
import com.omar.musica.database.entities.QueueEntity
import com.omar.musica.model.Song
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class QueueRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val queueDao: QueueDao
) {

    private val scope = CoroutineScope(Dispatchers.IO)

    suspend fun getQueue(): List<QueueItem> =
        queueDao.getQueue().map { it.toQueueItem() }

    fun observeQueueUris(): Flow<List<String>> =
        queueDao.getQueueFlow()
            .map { it.map { queueItem -> queueItem.songUri } }


    fun saveQueueFromSongs(songs: List<Song>) {
        scope.launch {
            queueDao.changeQueue(songs.map { it.toQueueEntity() })
        }
    }

    fun saveQueueFromQueueItems(songs: List<QueueItem>) {
        scope.launch {
            queueDao.changeQueue(songs.distinctBy { it.uri }.map { it.toQueueEntity() })
        }
    }

    private fun QueueItem.toQueueEntity() =
        QueueEntity(
            0, this.uri.toString(), title, artist, albumTitle
        )

    private fun Song.toQueueEntity() =
        QueueEntity(0, this.uriString, this.title, artist, album)

    private fun QueueEntity.toQueueItem() =
        QueueItem(
            uri = this.songUri.toUri(),
            title, artist, albumTitle
        )

}

data class QueueItem(
    val uri: Uri,
    val title: String,
    val artist: String?,
    val albumTitle: String?
)