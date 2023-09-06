package com.omar.musica

import android.content.ComponentName
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Player.COMMAND_PLAY_PAUSE
import androidx.media3.common.Player.COMMAND_PREPARE
import androidx.media3.common.Player.COMMAND_SET_MEDIA_ITEM
import androidx.media3.common.Timeline
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import com.omar.musica.model.Song
import com.omar.musica.playback.PlaybackService
import com.omar.musica.store.MediaRepository
import com.omar.musica.ui.theme.MusicaTheme
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.math.pow

class MainActivity : ComponentActivity() {

    private lateinit var controller: MediaController


    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        val musicRepository = MediaRepository(applicationContext)

        var songs by mutableStateOf(listOf<Song>())

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                musicRepository.songsFlow
                    .onEach {
                        songs = it
                    }
                    .collect()
            }
        }


        setContent {
            MusicaTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                    ) {

                    LazyColumn(Modifier.fillMaxSize()) {

                        items(songs) {

                            Column(
                                Modifier
                                    .fillMaxWidth()
                                    .combinedClickable(onLongClick = {

                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                            Log.d("MainActivity", "Deleintg file")
                                            val intent = MediaStore.createDeleteRequest(
                                                contentResolver,
                                                mutableListOf(it.uriString.toUri())
                                            )

                                            startIntentSenderForResult(
                                                intent.intentSender,
                                                0,
                                                null,
                                                0,
                                                0,
                                                0
                                            )
                                        }
                                    }) {
                                        playMusic(it.uriString)
                                        Toast
                                            .makeText(
                                                applicationContext,
                                                it.uriString,
                                                Toast.LENGTH_SHORT
                                            )
                                            .show()
                                    }) {
                                Text(text = it.title)
                                Text(text = it.artist)
                                Text(text = (it.length / 1000).toString())
                                Text(text = (it.size / 2.0.pow(20)).toString() + "MB")
                                Text(text = it.fileName)
                                Text(text = it.location)
                            }

                            Divider(Modifier.fillMaxSize())

                            Spacer(modifier = Modifier.height(24.dp))

                        }

                    }

                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        if (::controller.isInitialized)
            return

        val sessionToken = SessionToken(this, ComponentName(this, PlaybackService::class.java))

        val controllerFuture = MediaController.Builder(this, sessionToken)
            .buildAsync()

        controllerFuture.addListener({

            controller = controllerFuture.get()

            initController()

        }, MoreExecutors.directExecutor())


    }

    private fun playMusic(musicUri: String) {
        val mediaItem = MediaItem.Builder()
            .setUri(musicUri).build()
        controller.apply {
            //stop()
            //clearMediaItems()
            addMediaItem(mediaItem)
            if (!playWhenReady) {
                playWhenReady = true
                prepare()
            }
        }


    }

    private fun initController() {
        //controller.playWhenReady = true
        controller.addListener(object : Player.Listener {

            override fun onTimelineChanged(timeline: Timeline, reason: Int) {
                Log.i(TAG, "Timeline change: ${timeline.periodCount} periods")
                Log.i(TAG, timeline.toString())
            }
            override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                super.onMediaMetadataChanged(mediaMetadata)
                Log.d(TAG,"onMediaMetadataChanged=$mediaMetadata")
            }



            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                Log.d(TAG,"onIsPlayingChanged=$isPlaying")
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                Log.d(TAG,"onPlaybackStateChanged=?}")
            }

            override fun onPlayerError(error: PlaybackException) {
                super.onPlayerError(error)
                Log.d(TAG,"onPlayerError=${error.stackTraceToString()}")
            }

            override fun onPlayerErrorChanged(error: PlaybackException?) {
                super.onPlayerErrorChanged(error)
                Log.d(TAG,"onPlayerErrorChanged=${error?.stackTraceToString()}")
            }
        })
        Log.d(TAG,"start=}")
        Log.d(TAG,"COMMAND_PREPARE=${controller.isCommandAvailable(COMMAND_PREPARE)}")
        Log.d(TAG,"COMMAND_SET_MEDIA_ITEM=${controller.isCommandAvailable(COMMAND_SET_MEDIA_ITEM)}")
        Log.d(TAG,"COMMAND_PLAY_PAUSE=${controller.isCommandAvailable(COMMAND_PLAY_PAUSE)}")
    }

    override fun onDestroy() {
        Log.d(TAG, "On destroy")
        controller.release()
        super.onDestroy()
    }

    companion object {
        const val TAG = "MainActivity"
    }
}
