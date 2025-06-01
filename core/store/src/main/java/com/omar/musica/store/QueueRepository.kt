package com.omar.musica.store

import android.net.Uri
import androidx.core.net.toUri
import com.omar.musica.database.dao.QueueDao
import com.omar.musica.database.entities.queue.QueueEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class QueueRepository @Inject constructor(
  private val queueDao: QueueDao
) {

  private val scope = CoroutineScope(Dispatchers.IO)

  suspend fun getQueue(): List<DBQueueItem> =
    queueDao.getQueue()
      .map { it.toDBQueueItem() }

  fun observeQueueUris(): Flow<List<String>> =
    queueDao.getQueueFlow()
      .map { it.map { queueItem -> queueItem.songUri } }

  fun saveQueueFromDBQueueItems(songs: List<DBQueueItem>) {
    scope.launch {
      queueDao.changeQueue(songs.map { it.toQueueEntity() })
    }
  }

  private fun DBQueueItem.toQueueEntity() =
    QueueEntity(
      0,
      songUri.toString(),
      title,
      artist,
      album
    )

  private fun QueueEntity.toDBQueueItem(): DBQueueItem {
    return DBQueueItem(
      songUri = songUri.toUri(),
      title = title,
      artist = artist.orEmpty(),
      album = albumTitle.orEmpty(),
    )
  }

}

data class DBQueueItem(
  val songUri: Uri,
  val title: String,
  val artist: String,
  val album: String
)