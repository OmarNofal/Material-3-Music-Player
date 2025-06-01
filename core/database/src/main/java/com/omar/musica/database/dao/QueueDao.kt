package com.omar.musica.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.omar.musica.database.entities.queue.QUEUE_TABLE
import com.omar.musica.database.entities.queue.QueueEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface QueueDao {

  @Transaction
  suspend fun changeQueue(queue: List<QueueEntity>) {
    deleteQueue()
    insertQueue(queue)
  }

  @Insert
  suspend fun insertQueue(queue: List<QueueEntity>)

  @Query("SELECT * FROM $QUEUE_TABLE")
  fun getQueueFlow(): Flow<List<QueueEntity>>

  @Query("DELETE FROM $QUEUE_TABLE")
  suspend fun deleteQueue()

  @Query("SELECT * FROM $QUEUE_TABLE")
  suspend fun getQueue(): List<QueueEntity>

}