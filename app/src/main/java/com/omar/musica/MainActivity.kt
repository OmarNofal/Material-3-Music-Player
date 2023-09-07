package com.omar.musica

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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.session.MediaController
import com.omar.musica.model.Song
import com.omar.musica.playback.PlaybackManager
import com.omar.musica.store.MediaRepository
import com.omar.musica.ui.theme.MusicaTheme
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.math.pow

class MainActivity : ComponentActivity() {


    private lateinit var mediaPlayerManager: PlaybackManager
    private var songs by mutableStateOf(listOf<Song>())

    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val musicRepository = MediaRepository(applicationContext)



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

                        itemsIndexed(songs) { index, song ->

                            Column(
                                Modifier
                                    .fillMaxWidth()
                                    .combinedClickable(onLongClick = {

                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                            Log.d("MainActivity", "Deleintg file")
                                            val intent = MediaStore.createDeleteRequest(
                                                contentResolver,
                                                mutableListOf(song.uriString.toUri())
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
                                        playMusic(index)
                                        Toast
                                            .makeText(
                                                applicationContext,
                                                song.uriString,
                                                Toast.LENGTH_SHORT
                                            )
                                            .show()
                                    }) {
                                Text(text = song.title)
                                Text(text = song.artist)
                                Text(text = (song.length / 1000).toString())
                                Text(text = (song.size / 2.0.pow(20)).toString() + "MB")
                                Text(text = song.fileName)
                                Text(text = song.location)
                            }

                            Divider(Modifier.fillMaxSize())

                            Spacer(modifier = Modifier.height(24.dp))

                        }

                        item {
                            Button(onClick = mediaPlayerManager::togglePlayback) {
                                Text(text = "Toggle Playback")
                            }
                        }

                        item {
                            Button(onClick = mediaPlayerManager::forward) {
                                Text(text = "Seek Forward")
                            }
                        }

                        item {
                            val currentSong by mediaPlayerManager.currentlyPlayingSong.collectAsState()
                            Text(text = "Currently playing: $currentSong")
                        }

                    }

                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        mediaPlayerManager = PlaybackManager(this)
        return
    }

    private fun playMusic(index: Int) {
        mediaPlayerManager.setPlaylistAndPlayAtIndex(songs, index)
    }


    override fun onDestroy() {
        Log.d(TAG, "On destroy")
        super.onDestroy()
    }

    companion object {
        const val TAG = "MainActivity"
    }
}
