package com.omar.musica.ui.actions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.omar.musica.ui.shortcut.ShortcutAction
import com.omar.musica.ui.shortcut.ShortcutDialog
import com.omar.musica.ui.shortcut.ShortcutDialogData
import com.omar.musica.ui.shortcut.ShortcutDialogUi
import com.omar.musica.ui.shortcut.ShortcutUtils.createPinnedShortcutAlbum
import com.omar.musica.ui.shortcut.ShortcutUtils.createPinnedShortcutPlaylist


@Composable
fun rememberCreatePlaylistShortcutDialog(): ShortcutDialog {
  var currentData by remember {
    mutableStateOf<ShortcutDialogData?>(null)
  }

  if (currentData != null) {

    val data = currentData

    val name = when (data) {
      is ShortcutDialogData.AlbumShortcutDialogData -> data.albumName
      is ShortcutDialogData.PlaylistShortcutDialogData -> data.playlistName
      null -> "Null"
    }

    val context = LocalContext.current

    val onSubmit: (String, ShortcutAction) -> Unit = { shortcutName, action ->
      when (data) {
        is ShortcutDialogData.AlbumShortcutDialogData -> context.createPinnedShortcutAlbum(
          shortcutName,
          data.albumId,
          data.albumBitmap,
          action
        )
        is ShortcutDialogData.PlaylistShortcutDialogData -> context.createPinnedShortcutPlaylist(
          shortcutName,
          data.playlistId,
          data.playlistBitmap,
          action
        )

        else -> TODO()
      }
    }

    ShortcutDialogUi(
      listName = name,
      onSubmit = onSubmit,
      onDismissRequest = { currentData = null }
    )
  }

  return remember {
    object : ShortcutDialog {
      override fun launchForPlaylist(data: ShortcutDialogData.PlaylistShortcutDialogData) {
        currentData = data
      }

      override fun launchForAlbum(data: ShortcutDialogData.AlbumShortcutDialogData) {
        currentData = data
      }
    }
  }
}