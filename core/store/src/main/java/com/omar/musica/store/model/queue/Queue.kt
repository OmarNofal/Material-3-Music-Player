package com.omar.musica.store.model.queue

import com.omar.musica.store.model.song.Song


/**
 * Represents the playing queue in the player.
 *
 * @param items The list of queue items in the queue.
 * The list is sorted according to the playing order inside the player
 */
data class Queue(
    val items: List<QueueItem>
) {
    companion object {
        val EMPTY = Queue(listOf())
    }
}


/**
 * A single song inside the queue
 *
 * @param song The song to be played
 * @param originalIndex The index in the original non-shuffled queue. Can be used as id in LazyLists
 */
data class QueueItem(
    val song: Song,
    val originalIndex: Int
)