package com.omar.musica.playback.volume

import android.content.Context
import android.database.ContentObserver
import android.media.AudioManager
import android.net.Uri
import android.os.Handler


class VolumeChangeContentObserver(
    context: Context,
    handler: Handler,
    private val audioStreamType: Int,
    private val volumeChangeListener: AudioVolumeChangeListener
): ContentObserver(handler) {

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager?

    private var lastVolume = audioManager?.getStreamVolume(audioStreamType) ?: 1

    override fun onChange(selfChange: Boolean, uri: Uri?) {
        if (audioManager != null) {
            val currentVolume = audioManager.getStreamVolume(audioStreamType)
            if (currentVolume != lastVolume) {
                lastVolume = currentVolume
                volumeChangeListener.onVolumeChanged(currentVolume)
            }
        }
    }

}