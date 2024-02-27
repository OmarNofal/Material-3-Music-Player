package com.omar.musica.playback

import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaItem.RequestMetadata
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.session.MediaController
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import com.omar.musica.model.song.Song
import com.omar.musica.playback.state.PlaybackState
import com.omar.musica.playback.state.PlayerState
import com.omar.musica.playback.state.RepeatMode
import com.omar.musica.store.MediaRepository
import com.omar.musica.store.PlaylistsRepository
import com.omar.musica.store.QueueItem
import com.omar.musica.store.QueueRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton


/**
 * This singleton class represents the interface between the application and the media playback service running in the background.
 * It exposes the current state of the MediaSessionService as state flows so UI can update accordingly
 * It provides methods to manipulate the service, like changing the queue, pausing, rewinding, etc...
 */
@Singleton
class PlaybackManager @Inject constructor(
    @ApplicationContext context: Context,
    private val queueRepository: QueueRepository,
    private val mediaRepository: MediaRepository,
    private val playlistsRepository: PlaylistsRepository
) : PlaylistPlaybackActions {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private lateinit var mediaController: MediaController

    init {
        initMediaController(context)
    }

    private val _state = MutableStateFlow(PlaybackState.emptyState)

    val state: StateFlow<PlaybackState>
        get() = _state

    val currentSongProgress: Float
        get() = mediaController.currentPosition.toFloat() / mediaController.duration.toFloat()

    val playbackParameters: Pair<Float, Float>
        get() {
            val p = mediaController.playbackParameters
            return p.speed to p.pitch
        }

    private val playbackState: PlayerState
        get() {
            return when (mediaController.playbackState) {
                Player.STATE_READY -> {
                    if (mediaController.playWhenReady) PlayerState.PLAYING
                    else PlayerState.PAUSED
                }

                Player.STATE_BUFFERING -> PlayerState.BUFFERING
                else -> PlayerState.PAUSED
            }
        }


    fun clearQueue() {
        mediaController.clearMediaItems()
    }

    /**
     * Toggle the player state
     */
    fun togglePlayback() {
        mediaController.prepare()
        mediaController.playWhenReady = !mediaController.playWhenReady
    }

    /**
     * Skip forward in currently playing song
     */
    fun forward() {
        mediaController.sendCustomCommand(
            SessionCommand(Commands.JUMP_FORWARD, bundleOf()),
            bundleOf()
        )
    }

    /**
     * Skip backward in currently playing song
     */
    fun backward() {
        mediaController.sendCustomCommand(
            SessionCommand(Commands.JUMP_BACKWARD, bundleOf()),
            bundleOf()
        )
    }

    /**
     * Jumps to the next song in the queue
     */
    fun playNextSong() {
        mediaController.prepare()
        mediaController.seekToNext()
    }

    /**
     * Jumps to the previous song in the queue
     */
    fun playPreviousSong() {
        mediaController.prepare()
        mediaController.seekToPrevious()
    }

    fun playSongAtIndex(index: Int) {
        mediaController.seekTo(index, 0)
    }

    fun removeSongAtIndex(index: Int) {
        mediaController.removeMediaItem(index)
    }

    fun reorderSong(from: Int, to: Int) {
        mediaController.moveMediaItem(from, to)
    }

    fun seekToPosition(progress: Float) {
        val controller = mediaController
        val songDuration = controller.duration
        controller.seekTo((songDuration * progress).toLong())
    }

    /**
     * Changes the current playlist of the player and starts playing the song at the specified index
     */
    fun setPlaylistAndPlayAtIndex(playlist: List<Song>, index: Int = 0) {
        val mediaItems = playlist.toMediaItems()
        stopPlayback() // release everything
        mediaController.apply {
            setMediaItems(mediaItems, index, 0)
            prepare()
            play()
        }
    }

    /** Randomize the order of the list of songs and play */
    fun shuffle(songs: List<Song>) {
        val shuffled = songs.shuffled()
        stopPlayback()
        mediaController.apply {
            setMediaItems(shuffled.toMediaItems(), 0, 0)
            prepare()
            play()
        }
    }

    fun shuffleNext(songs: List<Song>) {
        val shuffled = songs.shuffled()
        val currentIndex = mediaController.currentMediaItemIndex
        mediaController.apply {
            addMediaItems(currentIndex + 1, shuffled.toMediaItems())
        }
    }

    fun playNext(songs: List<Song>) {
        val mediaItems = songs.toMediaItems()
        val currentIndex = mediaController.currentMediaItemIndex
        mediaController.addMediaItems(currentIndex + 1, mediaItems)
        mediaController.prepare()
    }

    fun addToQueue(songs: List<Song>) {
        val mediaItems = songs.toMediaItems()
        mediaController.addMediaItems(mediaItems)
        mediaController.prepare()
    }

    override fun playPlaylist(playlistId: Int) {
        coroutineScope.launch(Dispatchers.IO) {
            val songs = playlistsRepository.getPlaylistSongs(playlistId)
            withContext(Dispatchers.Main) {
                setPlaylistAndPlayAtIndex(songs)
            }
        }
    }

    override fun addPlaylistToNext(playlistId: Int) {
        coroutineScope.launch(Dispatchers.IO) {
            val songs = playlistsRepository.getPlaylistSongs(playlistId)
            withContext(Dispatchers.Main) {
                playNext(songs)
            }
        }
    }

    override fun addPlaylistToQueue(playlistId: Int) {
        coroutineScope.launch(Dispatchers.IO) {
            val songs = playlistsRepository.getPlaylistSongs(playlistId)
            withContext(Dispatchers.Main) {
                addToQueue(songs)
            }
        }
    }

    override fun shufflePlaylist(playlistId: Int) {
        coroutineScope.launch(Dispatchers.IO) {
            val songs = playlistsRepository.getPlaylistSongs(playlistId)
            withContext(Dispatchers.Main) {
                shuffle(songs)
            }
        }
    }

    override fun shufflePlaylistNext(playlistId: Int) {
        coroutineScope.launch(Dispatchers.IO) {
            val songs = playlistsRepository.getPlaylistSongs(playlistId)
            withContext(Dispatchers.Main) {
                shuffleNext(songs)
            }
        }
    }

    fun setSleepTimer(minutes: Int, finishLastSong: Boolean) {
        mediaController.sendCustomCommand(
            SessionCommand(Commands.SET_SLEEP_TIMER, bundleOf()),
            bundleOf(
                "MINUTES" to minutes,
                "FINISH_LAST_SONG" to finishLastSong
            )
        )
    }

    fun setPlaybackParameters(speed: Float, pitch: Float) {
        mediaController.playbackParameters = PlaybackParameters(speed, pitch)
    }

    fun deleteSleepTimer() {
        mediaController.sendCustomCommand(
            SessionCommand(Commands.CANCEL_SLEEP_TIMER, Bundle.EMPTY), Bundle.EMPTY
        )
    }

    fun toggleRepeatMode() {
        mediaController.repeatMode = RepeatMode.fromPlayer(mediaController.repeatMode).next().toPlayer()
    }

    fun toggleShuffleMode() {
        mediaController.shuffleModeEnabled = !mediaController.shuffleModeEnabled
    }

    private fun updateState() {
        val currentMediaItem = mediaController.currentMediaItem ?: return updateToEmptyState()
        val songUri = currentMediaItem.requestMetadata.mediaUri ?: return updateToEmptyState()
        _state.value = PlaybackState(
            mediaRepository.songsFlow.value.getSongByUri(songUri.toString()),
            playbackState,
            mediaController.shuffleModeEnabled,
            RepeatMode.fromPlayer(mediaController.repeatMode)
        )
    }

    private fun updateToEmptyState() {
        _state.value = PlaybackState.emptyState
    }

    private fun stopPlayback() {
        mediaController.stop()
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
                updateState()
                attachListeners()
            },
            MoreExecutors.directExecutor()
        )
    }

    private fun attachListeners() {
        mediaController.addListener(object : Player.Listener {
            override fun onTimelineChanged(timeline: Timeline, reason: Int) {
                updateState()
                if (reason == Player.TIMELINE_CHANGE_REASON_PLAYLIST_CHANGED) {
                    savePlayerQueue()
                }
            }

            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                updateState()
            }

            override fun onRepeatModeChanged(repeatMode: Int) {
                updateState()
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
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

        })
    }


    private fun savePlayerQueue() {
        val queue = getQueueFromPlayer()
        queueRepository.saveQueueFromQueueItems(queue)
    }

    private fun getQueueFromPlayer(): List<QueueItem> {
        val count = mediaController.mediaItemCount
        return List(count) { i ->
            val mediaItem = mediaController.getMediaItemAt(i)
            val metadata = mediaItem.mediaMetadata
            val requestMetadata = mediaItem.requestMetadata

            QueueItem(
                requestMetadata.mediaUri!!,
                metadata.title.toString(),
                metadata.artist.toString(),
                metadata.albumTitle.toString()
            )
        }
    }

    private fun Song.toMediaItem() =
        MediaItem.Builder()
            .setUri(uriString)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setMediaType(MediaMetadata.MEDIA_TYPE_MUSIC)
                    .setArtist(artist)
                    .setAlbumTitle(album)
                    .setTitle(title)
                    .build()
            )
            .setRequestMetadata(
                RequestMetadata.Builder().setMediaUri(uriString.toUri())
                    .build() // to be able to retrieve the URI easily
            )
            .build()

    private fun List<Song>.toMediaItems() = map { it.toMediaItem() }

}