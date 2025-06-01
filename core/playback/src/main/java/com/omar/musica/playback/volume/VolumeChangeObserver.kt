package com.omar.musica.playback.volume

import android.content.Context
import android.os.Handler
import android.provider.Settings


class VolumeChangeObserver(
    private val context: Context,
    private val handler: Handler,
    private val audioStreamType: Int
) {

    private var contentObserver: VolumeChangeContentObserver? = null

    // TODO: Muse的设计更好
    fun register(volumeChangeListener: AudioVolumeChangeListener) {
        contentObserver = VolumeChangeContentObserver(
            context, handler,
            audioStreamType, volumeChangeListener
        )

        context.contentResolver.registerContentObserver(
            Settings.System.CONTENT_URI, true, contentObserver!!
        )
    }

    fun unregister() {
        if (contentObserver != null) {
            context.contentResolver.unregisterContentObserver(contentObserver!!)
        }
    }

}