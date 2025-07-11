package com.omar.musica.ui.shortcut

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.core.net.toUri
import com.omar.musica.playback.PlaybackManager
import com.omar.musica.store.AlbumsRepository
import com.omar.musica.store.PlaylistsRepository
import com.omar.musica.ui.showShortToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import javax.inject.Inject


@AndroidEntryPoint
class ShortcutActivity : ComponentActivity() {

    @Inject
    lateinit var playbackManager: PlaybackManager

    @Inject
    lateinit var albumsRepository: AlbumsRepository

    @Inject
    lateinit var playlistsRepository: PlaylistsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val data = intent?.extras ?: return finish()

        val command = data.getString(KEY_COMMAND, PLAY_COMMAND)
        val type = data.getString(KEY_TYPE, PLAYLIST_TYPE)

        try {
            if (type == ALBUM_TYPE)
                handleAlbum(command, data.getInt(KEY_ID, -1))
            else if (type == PLAYLIST_TYPE)
                handlePlaylist(command, data.getInt(KEY_ID, -1))
        } catch (_: Exception) {

        } finally {
            finish()
        }
    }

    private fun handleAlbum(command: String, albumId: Int) {

        if (command == VIEW_COMMAND) {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = "musica://albums/$albumId".toUri()
            }
            startActivity(intent)
            return
        }

        runBlocking {
            albumsRepository.waitUntilAlbumsReady()
        }
        val albumSongs = albumsRepository.albums
            .value.find { it.albumInfo.id == albumId } ?: return

        val songs = albumSongs.songs.sortedBy { it.trackNumber }.map { it.song }

        runBlocking { playbackManager.waitUntilReady() }
        if (command == SHUFFLE_COMMAND)
            playbackManager.shuffle(songs)
        else if (command == PLAY_COMMAND)
            playbackManager.setPlaylistAndPlayAtIndex(songs)

        showShortToast("${albumSongs.albumInfo.name} started playing")
    }

    private fun handlePlaylist(command: String, playlist: Int) {

        if (playlist == -1) return

        if (command == VIEW_COMMAND) {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = "musica://playlists/$playlist".toUri()
            }
            startActivity(intent)
            return
        }

        val playlistInfo = runBlocking {
            playlistsRepository.getPlaylistWithSongsFlow(playlist)
                .firstOrNull()
        }

        runBlocking { playbackManager.waitUntilReady() }
        if (command == SHUFFLE_COMMAND)
            playbackManager.shufflePlaylist(playlist)
        else if (command == PLAY_COMMAND)
            playbackManager.playPlaylist(playlist)

        val name = playlistInfo?.playlistInfo?.name ?: "Playlist"
        showShortToast("$name started playing")
    }

    companion object {
        const val KEY_COMMAND = "COMMAND"
        const val KEY_TYPE = "TYPE"
        const val KEY_ID = "NAME"

        const val PLAY_COMMAND = "PLAY"
        const val SHUFFLE_COMMAND = "SHUFFLE"
        const val VIEW_COMMAND = "VIEW"

        const val ALBUM_TYPE = "ALBUM"
        const val PLAYLIST_TYPE = "PLAYLIST"
    }

}