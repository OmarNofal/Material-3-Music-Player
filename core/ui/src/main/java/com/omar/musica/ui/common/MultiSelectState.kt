package com.omar.musica.ui.common

import androidx.compose.runtime.mutableStateListOf
import com.omar.musica.ui.model.SongUi

data class MultiSelectState(
    val selected: MutableList<SongUi> = mutableStateListOf()
) {
    private fun selectSong(songUi: SongUi) {
        selected.add(songUi)
    }

    fun toggleSong(songUi: SongUi) {
        if (selected.contains(songUi)) {
            deselectSong(songUi)
        } else {
            selectSong(songUi)
        }
    }

    fun clear() {
        selected.clear()
    }

    private fun deselectSong(songUi: SongUi) {
        selected.remove(songUi)
    }
}