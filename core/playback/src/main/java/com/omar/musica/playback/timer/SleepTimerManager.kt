package com.omar.musica.playback.timer

import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch


interface SleepTimerManagerListener {
  fun onSleepTimerFinished()
}

class SleepTimerManager(
  private val listener: SleepTimerManagerListener
) : Player.Listener {

  private var shouldPauseAtEndOfSong = false

  private var finishLastSong = false
  private val timer = CoroutineTimer(::onTimerFinish)

  private fun onTimerFinish() {
    if (!finishLastSong)
      listener.onSleepTimerFinished()
    else
      shouldPauseAtEndOfSong = true
  }

  override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
    if (
      shouldPauseAtEndOfSong &&
      (reason == Player.MEDIA_ITEM_TRANSITION_REASON_AUTO ||
        reason == Player.MEDIA_ITEM_TRANSITION_REASON_REPEAT)
    ) {
      listener.onSleepTimerFinished()
      shouldPauseAtEndOfSong = false
    }
  }


  fun schedule(minutes: Int, finishLastSong: Boolean) {
    timer.schedule(minutes)
    this.finishLastSong = finishLastSong
    shouldPauseAtEndOfSong = false
  }

  fun deleteTimer() {
    timer.cancel()
    shouldPauseAtEndOfSong = false
  }
}