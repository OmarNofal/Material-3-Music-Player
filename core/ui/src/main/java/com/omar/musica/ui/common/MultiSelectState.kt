package com.omar.musica.ui.common

import androidx.compose.runtime.mutableStateListOf
import com.omar.musica.store.model.song.Song

data class MultiSelectState(
    val selected: MutableList<Song> = mutableStateListOf()
) {
    private fun selectSong(songUi: Song) {
        selected.add(songUi)
    }

    fun toggleSong(songUi: Song) {
        if (selected.contains(songUi)) {
            deselectSong(songUi)
        } else {
            selectSong(songUi)
        }
    }

    fun clear() {
        selected.clear()
    }

    private fun deselectSong(songUi: Song) {
        selected.remove(songUi)
    }
}