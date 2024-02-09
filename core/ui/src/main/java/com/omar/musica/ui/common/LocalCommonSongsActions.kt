package com.omar.musica.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import com.omar.musica.playback.PlaybackManager
import com.omar.musica.store.MediaRepository
import com.omar.musica.ui.actions.AndroidQBelowDeleter
import com.omar.musica.ui.actions.SongDeleteAction
import com.omar.musica.ui.actions.SongPlaybackActions
import com.omar.musica.ui.actions.SongPlaybackActionsImpl
import com.omar.musica.ui.actions.SongShareAction
import com.omar.musica.ui.actions.rememberSongDeleter
import com.omar.musica.ui.playlist.AddToPlaylistDialog
import com.omar.musica.ui.playlist.rememberAddToPlaylistDialog


//@Stable
data class CommonSongsActions(
    val playbackActions: SongPlaybackActions,
    val shareAction: SongShareAction,
    val deleteAction: SongDeleteAction,
    val songInfoDialog: SongInfoDialog,
    val addToPlaylistDialog: AddToPlaylistDialog
)

val LocalCommonSongsAction = staticCompositionLocalOf<CommonSongsActions>
{ throw IllegalArgumentException("not implemented") }

@Composable
fun rememberCommonSongsActions(
    playbackManager: PlaybackManager,
    mediaRepository: MediaRepository
): CommonSongsActions {

    val songPlaybackActions = SongPlaybackActionsImpl(playbackManager)
    val shareAction = SongsSharer
    val deleteAction = rememberSongDeleter(mediaRepository = mediaRepository)
    val songInfoDialog = rememberSongDialog()
    val addToPlaylistDialog = rememberAddToPlaylistDialog()

    return remember {
        CommonSongsActions(
            songPlaybackActions,
            shareAction,
            deleteAction,
            songInfoDialog,
            addToPlaylistDialog
        )
    }
}