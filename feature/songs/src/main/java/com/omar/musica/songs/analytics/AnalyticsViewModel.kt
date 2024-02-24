package com.omar.musica.songs.analytics

import android.icu.util.IslamicCalendar
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omar.musica.store.AnalyticsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val analyticsRepository: AnalyticsRepository
): ViewModel() {




    init {
        viewModelScope.launch {
            updateAnalytics()
        }
    }


    private suspend fun updateAnalytics() {
        val listeningSessions = analyticsRepository.getAllListeningSessions()

    }


}