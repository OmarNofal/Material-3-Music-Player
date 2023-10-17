package com.omar.nowplaying.queue

import androidx.lifecycle.ViewModel
import com.omar.musica.store.QueueRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class QueueViewModel @Inject constructor(
    private val queueRepository: QueueRepository
) : ViewModel() {

}