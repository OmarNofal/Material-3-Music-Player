package com.omar.nowplaying.viewmodel

import androidx.lifecycle.ViewModel
import com.omar.musica.playback.PlaybackManager
import com.omar.musica.store.MediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel


@HiltViewModel
class NowPlayingViewModel(
    val mediaRepository: MediaRepository,
    val playbackManager: PlaybackManager
): ViewModel() {






}