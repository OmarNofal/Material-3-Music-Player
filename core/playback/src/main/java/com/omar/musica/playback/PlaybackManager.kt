package com.omar.musica.playback

import android.content.ComponentName
import android.content.Context
import android.util.Log
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Metadata
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import com.omar.musica.model.Song
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton


private const val TAG = "PlaybackManager"


/**
 * This singleton class represents the interface between the application and the media playback service running in the background.
 * It exposes the current state of the MediaSessionService as state flows so UI can update accordingly
 * It provides methods to manipulate the service, like changing the queue, pausing, rewinding, etc...
 */
@Singleton
class PlaybackManager @Inject constructor(@ApplicationContext context: Context) {


    private var mediaController: MediaController? = null

    init {
        initMediaController(context)
    }


    val currentlyPlayingSong =
        MutableStateFlow<String?>(null)


    /**
     * Toggle the player state
     */
    fun togglePlayback() {
        if (mediaController == null) {
            Log.e(TAG, "MediaController not yet initialized")
            return
        }
        mediaController?.playWhenReady = !(mediaController?.playWhenReady ?: true)
    }


    /**
     * Skip forward in currently playing song // TODO depend on user settings
     */
    fun forward() {
        val currentPositionMs = mediaController?.currentPosition ?: return
        mediaController?.seekTo(currentPositionMs + 10000)
    }


    /**
     * Changes the current playlist of the player and starts playing the song at the specified index
     */
    fun setPlaylistAndPlayAtIndex(playlist: List<Song>, index: Int = 0) {
        val mediaItems = playlist.toMediaItems()
        stopPlayback() // release everything
        mediaController?.apply {
            setMediaItems(mediaItems, index, 0)
            prepare()
            play()
        }
    }

    /**
     * Sets a song in the player and starts playing it
     */
    fun setSongAndPlay(song: Song) {
        setSong(song)
        mediaController?.playWhenReady = true
    }

    fun playNext(songs: List<Song>) {
        val mediaItems = songs.toMediaItems()
        val currentIndex = mediaController?.currentMediaItemIndex ?: 0
        mediaController?.addMediaItems(currentIndex + 1, mediaItems)
    }

    private fun setSong(song: Song) {
        val mediaItem = MediaItem.Builder()
            .setUri(song.uriString.toUri())
            .build()
        mediaController?.playWhenReady = false
        mediaController?.stop()
        mediaController?.setMediaItem(mediaItem)
        mediaController?.prepare()
    }

    private fun stopPlayback() {
        mediaController?.stop()
    }

    private fun initMediaController(context: Context) {
        val sessionToken = SessionToken(context, ComponentName(context, PlaybackService::class.java))
        val mediaControllerFuture = MediaController.Builder(context, sessionToken)
            .setApplicationLooper(context.mainLooper)
            .buildAsync()
        mediaControllerFuture.addListener(
            {
                mediaController = mediaControllerFuture.get()
                attachListeners()
            },
            MoreExecutors.directExecutor()
        )
    }

    private fun attachListeners() {
        mediaController?.addListener(object : Player.Listener {
            override fun onTimelineChanged(timeline: Timeline, reason: Int) {
                if (reason == Player.TIMELINE_CHANGE_REASON_SOURCE_UPDATE) return
                Log.d(TAG, "Timeline changed")
                val currentItem = mediaController?.mediaMetadata ?: return
                currentlyPlayingSong.value = currentItem.title.toString()
                Log.d(TAG, currentItem.albumTitle.toString())
            }

            override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                currentlyPlayingSong.value = mediaMetadata.title.toString()
            }
        })
    }


    private fun Song.toMediaItem() =
        MediaItem.Builder()
            .setUri(uriString)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setMediaType(MediaMetadata.MEDIA_TYPE_MUSIC)
                    .setArtist(this.artist)
                    .setAlbumTitle(this.album)
                    .setTitle(this.title)
                    .build()
            )
            .build()

    private fun List<Song>.toMediaItems() = map { it.toMediaItem() }

}