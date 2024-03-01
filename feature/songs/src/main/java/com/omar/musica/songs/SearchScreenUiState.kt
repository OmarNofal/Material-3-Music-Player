package com.omar.musica.songs

import androidx.compose.runtime.Immutable
import com.omar.musica.store.model.song.Song


@Immutable
data class SearchScreenUiState(
    val searchQuery: String,
    val songs: List<Song>
) {
    companion object {
        val emptyState = SearchScreenUiState("", listOf())
    }
}