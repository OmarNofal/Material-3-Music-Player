package com.omar.musica.songs.analytics



sealed interface AnalyticsScreenState {
    data object Loading: AnalyticsScreenState
    data class Loaded(
        val averageListeningTimePerDay: Int
    ): AnalyticsScreenState
}