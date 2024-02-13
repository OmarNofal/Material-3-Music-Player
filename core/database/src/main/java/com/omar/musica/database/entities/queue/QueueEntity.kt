package com.omar.musica.database.entities.queue

import androidx.room.Entity
import androidx.room.PrimaryKey

const val QUEUE_TABLE = "queue"

/**
 * The queue table is only used when the application starts from scratch
 * to set the player to the previous queue before it closed. It is not used
 * as the single source of truth.
 */
@Entity(QUEUE_TABLE)
data class QueueEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int,

    val songUri: String,

    val title: String,

    val artist: String?,

    val albumTitle: String?

)