package com.omar.musica.playback

import android.content.ComponentName
import android.content.Context
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaItem.RequestMetadata
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.common.Tracks
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import com.omar.musica.model.Song
import com.omar.musica.playback.state.PlaybackState
import com.omar.musica.playback.state.PlayerState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber
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


    private val _state =
        MutableStateFlow(PlaybackState.emptyState)

    val state: StateFlow<PlaybackState>
        get() = _state

    val currentSongProgress: Float
        get() = (mediaController?.currentPosition?.toFloat()
            ?: 0.0f) / (mediaController?.duration?.toFloat() ?: 1.0f)


    val playbackState: PlayerState
        get() {
            return when (mediaController?.playbackState) {
                Player.STATE_READY -> {
                    if (mediaController?.playWhenReady == true) PlayerState.PLAYING
                    else PlayerState.PAUSED
                }

                Player.STATE_BUFFERING -> PlayerState.BUFFERING
                else -> PlayerState.PAUSED
            }
        }

    /**
     * Toggle the player state
     */
    fun togglePlayback() {
        if (mediaController == null) {
            Timber.e("MediaController not yet initialized")
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
     * Skip backward in currently playing song
     */
    fun backward() {
        val currentPositionMs = mediaController?.currentPosition ?: return
        mediaController?.seekTo(currentPositionMs - 10000)
    }

    /**
     * Jumps to the next song in the queue
     */
    fun playNextSong() {
        mediaController?.seekToNext()
    }

    /**
     * Jumps to the previous song in the queue
     */
    fun playPreviousSong() {
        mediaController?.seekToPrevious()
    }


    /**
     * Starts the current song from the beginning
     */
    fun restartCurrentSong() {
        mediaController?.seekToDefaultPosition()
    }

    fun seekToPosition(progress: Float) {
        val controller = mediaController ?: return
        val songDuration = controller.duration
        controller.seekTo((songDuration * progress).toLong())
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
        val mediaItem = song.toMediaItem()
        mediaController?.playWhenReady = false
        mediaController?.stop()
        mediaController?.setMediaItem(mediaItem)
        mediaController?.prepare()
    }

    private fun updateState() {
        val controller = mediaController ?: return
        val currentMediaItem = controller.currentMediaItem ?: return updateToEmptyState()
        val songUri = currentMediaItem.requestMetadata.mediaUri ?: return updateToEmptyState()
        _state.value = PlaybackState(songUri, playbackState)
    }

    private fun updateToEmptyState() {
        _state.value = PlaybackState.emptyState
    }

    private fun stopPlayback() {
        mediaController?.stop()
    }

    private fun initMediaController(context: Context) {
        val sessionToken =
            SessionToken(context, ComponentName(context, PlaybackService::class.java))
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
                //if (reason == Player.TIMELINE_CHANGE_REASON_SOURCE_UPDATE) return
                updateState()
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                //super.onMediaItemTransition(mediaItem, reason)

                Timber.d("Media transitioned to ${mediaItem?.requestMetadata?.mediaUri}")
                updateState()
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                updateState()
            }

            override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                updateState()
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                updateState()
            }

            override fun onTracksChanged(tracks: Tracks) {
                //updateState()
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
            .setRequestMetadata(
                RequestMetadata.Builder().setMediaUri(uriString.toUri()).build() // to be able to retrieve the URI easily
            )
            .build()

    private fun List<Song>.toMediaItems() = map { it.toMediaItem() }


}