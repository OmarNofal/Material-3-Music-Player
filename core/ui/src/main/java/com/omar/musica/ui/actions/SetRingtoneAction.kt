package com.omar.musica.ui.actions

import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import com.omar.musica.ui.showShortToast
import timber.log.Timber


interface SetRingtoneAction {
    fun setRingtone(uri: Uri)
}


class SetRingtone(private val context: Context) : SetRingtoneAction {
    override fun setRingtone(uri: Uri) {
        try {
            if (context.canEditSystemSettings()) {
                RingtoneManager.setActualDefaultRingtoneUri(
                    context, RingtoneManager.TYPE_RINGTONE, uri
                )
                context.showShortToast("Ringtone set successfully")
            }
            else  {
                if (Build.VERSION.SDK_INT >= 23) {
                    context.showShortToast("Allow the app to edit system settings")
                    context.startActivity(Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS).apply {
                        data = Uri.parse("package:" + context.packageName)
                    })
                } else {
                    context.showShortToast("Your device doesn't allow setting ringtone")
                }
            }
        } catch (e: Exception) {
            context.showShortToast("Failed to set ringtone")
            Timber.e("Failed to set ringtone:  ${e.message}")
        }
    }
}


fun Context.canEditSystemSettings() =
    if (Build.VERSION.SDK_INT >= 23) {
        Settings.System.canWrite(this)
    } else true
