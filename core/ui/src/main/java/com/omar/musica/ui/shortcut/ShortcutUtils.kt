package com.omar.musica.ui.shortcut

import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.Bitmap
import android.graphics.drawable.Icon
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import com.omar.musica.ui.R

object ShortcutUtils {

    fun Context.isPinnedShortcutsSupported(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return false

        val shortcutManager = getSystemService(ShortcutManager::class.java)
        return shortcutManager.isRequestPinShortcutSupported
    }


    fun Context.createPinnedShortcutPlaylist(
        name: String,
        playlistId: Int,
        bitmap: Bitmap?,
        action: ShortcutAction
    ) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val sm = getShortcutManager() ?: return

        val command = when (action) {
            ShortcutAction.OPEN_IN_APP -> ""
            ShortcutAction.PLAY -> ShortcutActivity.PLAY_COMMAND
            ShortcutAction.SHUFFLE -> ShortcutActivity.SHUFFLE_COMMAND
        }

        val extras = bundleOf(
            ShortcutActivity.KEY_COMMAND to command,
            ShortcutActivity.KEY_TYPE to ShortcutActivity.PLAYLIST_TYPE,
            ShortcutActivity.KEY_ID to playlistId
        )

        val intent = Intent(this, ShortcutActivity::class.java)
            .apply {
                this.action = Intent.ACTION_VIEW
                putExtras(extras)
            }

        val icon =
            if (bitmap == null)
                Icon.createWithResource(this, R.drawable.placeholder)
            else
                Icon.createWithBitmap(bitmap)

        val shortcutInfo = ShortcutInfo.Builder(this, "PLAYLIST_$playlistId")
            .setShortLabel(name)
            .setLongLabel(name)
            .setIcon(icon)
            .setIntent(intent)

        sm.requestPinShortcut(shortcutInfo.build(), null)
    }

    fun createPinnedShortcutAlbum() {

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun Context.getShortcutManager(): ShortcutManager? {
        return getSystemService(ShortcutManager::class.java)
    }
}
