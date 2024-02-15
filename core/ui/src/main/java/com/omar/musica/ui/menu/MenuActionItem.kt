package com.omar.musica.ui.menu

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddToPhotos
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.PlaylistRemove
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.TextFormat
import androidx.compose.ui.graphics.vector.ImageVector
import com.omar.musica.ui.actions.SongDeleteAction
import com.omar.musica.ui.actions.SongPlaybackActions
import com.omar.musica.ui.actions.SongShareAction
import com.omar.musica.ui.songs.SongInfoDialog
import com.omar.musica.ui.model.SongUi
import com.omar.musica.ui.playlist.AddToPlaylistDialog

data class MenuActionItem(
    val icon: ImageVector,
    val title: String,
    val callback: () -> Unit
)


fun MutableList<MenuActionItem>.delete(callback: () -> Unit) =
    add(MenuActionItem(Icons.Rounded.Delete, "Delete", callback))

fun MutableList<MenuActionItem>.playNext(callback: () -> Unit) =
    add(MenuActionItem(Icons.Rounded.SkipNext, "Play Next", callback))

fun MutableList<MenuActionItem>.play(callback: () -> Unit) =
    add(MenuActionItem(Icons.Rounded.PlayArrow, "Play", callback))

fun MutableList<MenuActionItem>.shuffle(callback: () -> Unit) =
    add(MenuActionItem(Icons.Rounded.Shuffle, "Shuffle", callback))

fun MutableList<MenuActionItem>.shuffleNext(callback: () -> Unit) =
    add(MenuActionItem(Icons.Rounded.Shuffle, "Shuffle Next", callback))

fun MutableList<MenuActionItem>.addToQueue(callback: () -> Unit) =
    add(MenuActionItem(Icons.Rounded.AddToPhotos, "Add to Playing Queue", callback))

fun MutableList<MenuActionItem>.edit(callback: () -> Unit) =
    add(MenuActionItem(Icons.Rounded.Edit, "Edit", callback))

fun MutableList<MenuActionItem>.rename(callback: () -> Unit) =
    add(MenuActionItem(Icons.Rounded.TextFormat, "Rename", callback))


fun MutableList<MenuActionItem>.addToPlaylists(callback: () -> Unit) =
    add(MenuActionItem(Icons.Rounded.AddToPhotos, "Add to Playlists", callback))

fun MutableList<MenuActionItem>.share(callback: () -> Unit) =
    add(MenuActionItem(Icons.Rounded.Share, "Share", callback))

fun MutableList<MenuActionItem>.songInfo(callback: () -> Unit) =
    add(MenuActionItem(Icons.Rounded.Info, "Song Info", callback))

fun MutableList<MenuActionItem>.removeFromPlaylist(callback: () -> Unit) =
    add(MenuActionItem(Icons.Rounded.PlaylistRemove, "Remove from Playlist", callback))


fun buildCommonSongActions(
    song: SongUi,
    context: Context,
    songPlaybackActions: SongPlaybackActions,
    songInfoDialog: SongInfoDialog,
    addToPlaylistDialog: AddToPlaylistDialog,
    shareAction: SongShareAction,
    songDeleteAction: SongDeleteAction
): MutableList<MenuActionItem> {
    val songList = listOf(song)
    val list = mutableListOf<MenuActionItem>().apply {
        playNext { songPlaybackActions.playNext(songList) }
        addToQueue { songPlaybackActions.addToQueue(songList) }
        addToPlaylists { addToPlaylistDialog.launch(songList) }
        share { shareAction.share(context, songList) }
        songInfo { songInfoDialog.open(song) }
        delete { songDeleteAction.deleteSongs(songList) }
    }
    return list
}

fun buildCommonMultipleSongsActions(
    songs: List<SongUi>,
    context: Context,
    songPlaybackActions: SongPlaybackActions,
    addToPlaylistDialog: AddToPlaylistDialog,
    shareAction: SongShareAction,
): MutableList<MenuActionItem> {
    val list = mutableListOf<MenuActionItem>().apply {
        playNext { songPlaybackActions.playNext(songs) }
        addToQueue { songPlaybackActions.addToQueue(songs) }
        shuffleNext { songPlaybackActions.shuffleNext(songs) }
        addToPlaylists { addToPlaylistDialog.launch(songs) }
        share { shareAction.share(context, songs) }
    }
    return list
}