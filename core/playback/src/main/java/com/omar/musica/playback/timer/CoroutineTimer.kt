package com.omar.musica.playback.timer

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class CoroutineTimer(
    private val onFinish: () -> Unit
) {

    private val scope = CoroutineScope(Dispatchers.Main)

    private var delayJob: Job? = null


    private val isActive: Boolean
        get() = delayJob?.isActive ?: false

    fun schedule(minutes: Int) {
        delayJob?.cancel()
        val totalMillis: Long = minutes * (60L * 1000L)
        delayJob = scope.launch {
            delay(totalMillis)
            if (isActive)
                onFinish()
        }
    }

    fun cancel() {
        delayJob?.cancel()
        delayJob = null
    }
}