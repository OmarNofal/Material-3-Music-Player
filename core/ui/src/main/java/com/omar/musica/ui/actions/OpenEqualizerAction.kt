package com.omar.musica.ui.actions

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.audiofx.AudioEffect
import com.omar.musica.ui.showShortToast


interface OpenEqualizerAction {
    fun open()
}


class EqualizerOpener(private val activity: Activity): OpenEqualizerAction {
    override fun open() {
        with(activity) {
            if (isEqualizerAvailable())
                openEqualizerScreen()
            else
                showShortToast("Your device doesn't have an equalizer")
        }
    }
}

@SuppressLint("QueryPermissionsNeeded")
fun Context.isEqualizerAvailable(): Boolean {
    val intent = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
    return intent.resolveActivity(packageManager) != null
}

fun Activity.openEqualizerScreen() {
    val intent = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
    startActivityForResult(intent, 1)
}
