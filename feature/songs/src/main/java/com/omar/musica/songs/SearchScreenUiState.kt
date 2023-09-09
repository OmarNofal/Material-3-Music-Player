package com.omar.musica.songs

import com.omar.musica.model.Song


data class SearchScreenUiState(
    val searchQuery: String,
    val songs: List<Song>
) {
    companion object {
        val emptyState = SearchScreenUiState("", listOf())
    }
}