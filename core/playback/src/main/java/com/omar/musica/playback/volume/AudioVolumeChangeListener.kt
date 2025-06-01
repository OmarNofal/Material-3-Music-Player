package com.omar.musica.playback.volume


interface AudioVolumeChangeListener {
  fun onVolumeChanged(level: Int)
}