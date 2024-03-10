package com.omar.musica.playback.extensions

import android.net.Uri
import androidx.core.os.bundleOf
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.omar.musica.store.DBQueueItem


const val EXTRA_SONG_ORIGINAL_INDEX = "og"

/**
 * Converts a queue item to media item for use in the player
 */
internal fun DBQueueItem.toMediaItem(originalIndex: Int) =
    MediaItem.Builder()
        .setUri(songUri.toString())
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setMediaType(MediaMetadata.MEDIA_TYPE_MUSIC)
                .setArtist(artist)
                .setAlbumTitle(album)
                .setTitle(title)
                .build()
        )
        .setRequestMetadata(
            MediaItem.RequestMetadata.Builder().setMediaUri(songUri).setExtras(
                bundleOf(EXTRA_SONG_ORIGINAL_INDEX to originalIndex)
            )
                .build() // to be able to retrieve the URI easily
        )
        .build()


internal fun MediaItem.toDBQueueItem() =
    DBQueueItem(
        requestMetadata.mediaUri ?: Uri.EMPTY,
        mediaMetadata.title.toString(),
        mediaMetadata.artist.toString(),
        mediaMetadata.albumTitle.toString()
    )