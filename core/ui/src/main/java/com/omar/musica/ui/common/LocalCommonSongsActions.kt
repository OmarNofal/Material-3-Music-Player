package com.omar.musica.ui.common

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import com.omar.musica.playback.PlaybackManager
import com.omar.musica.store.MediaRepository
import com.omar.musica.ui.actions.EqualizerOpener
import com.omar.musica.ui.actions.OpenTagEditorAction
import com.omar.musica.ui.actions.SetRingtone
import com.omar.musica.ui.actions.SetRingtoneAction
import com.omar.musica.ui.actions.SongDeleteAction
import com.omar.musica.ui.actions.SongPlaybackActions
import com.omar.musica.ui.actions.SongPlaybackActionsImpl
import com.omar.musica.ui.actions.SongShareAction
import com.omar.musica.ui.actions.SongsSharer
import com.omar.musica.ui.actions.rememberCreatePlaylistShortcutDialog
import com.omar.musica.ui.actions.rememberSongDeleter
import com.omar.musica.ui.playlist.AddToPlaylistDialog
import com.omar.musica.ui.playlist.rememberAddToPlaylistDialog
import com.omar.musica.ui.shortcut.ShortcutDialog
import com.omar.musica.ui.songs.SongInfoDialog
import com.omar.musica.ui.songs.rememberSongDialog


data class CommonSongsActions(
    val playbackActions: SongPlaybackActions,
    val shareAction: SongShareAction,
    val deleteAction: SongDeleteAction,
    val songInfoDialog: SongInfoDialog,
    val addToPlaylistDialog: AddToPlaylistDialog,
    val openEqualizer: EqualizerOpener,
    val setRingtoneAction: SetRingtoneAction,
    val openTagEditorAction: OpenTagEditorAction,
    val createShortcutDialog: ShortcutDialog
)

val LocalCommonSongsAction = staticCompositionLocalOf<CommonSongsActions>
{ throw IllegalArgumentException("not implemented") }

@Composable
fun rememberCommonSongsActions(
    playbackManager: PlaybackManager,
    mediaRepository: MediaRepository,
    openTagEditorAction: OpenTagEditorAction
): CommonSongsActions {

    val context = LocalContext.current
    val songPlaybackActions = SongPlaybackActionsImpl(context, playbackManager)
    val shareAction = SongsSharer
    val deleteAction = rememberSongDeleter(mediaRepository = mediaRepository)
    val songInfoDialog = rememberSongDialog()
    val addToPlaylistDialog = rememberAddToPlaylistDialog()
    val openEqualizer = remember { EqualizerOpener(context as Activity) }
    val setRingtoneAction = remember { SetRingtone(context) }
    val shortcutDialog = rememberCreatePlaylistShortcutDialog()

    return remember {
        CommonSongsActions(
            songPlaybackActions,
            shareAction,
            deleteAction,
            songInfoDialog,
            addToPlaylistDialog,
            openEqualizer,
            setRingtoneAction,
            openTagEditorAction,
            shortcutDialog
        )
    }
}