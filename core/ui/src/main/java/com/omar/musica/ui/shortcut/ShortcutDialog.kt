package com.omar.musica.ui.shortcut

import android.graphics.Bitmap


interface ShortcutDialog {

    fun launchForPlaylist(data: ShortcutDialogData.PlaylistShortcutDialogData)

    fun launchForAlbum(data: ShortcutDialogData.AlbumShortcutDialogData)
}


sealed class ShortcutDialogData {
    data class PlaylistShortcutDialogData(
        val playlistName: String,
        val playlistId: Int,
        val playlistBitmap: Bitmap? = null
    ) : ShortcutDialogData()

    data class AlbumShortcutDialogData(
        val albumName: String
    ) : ShortcutDialogData()
}