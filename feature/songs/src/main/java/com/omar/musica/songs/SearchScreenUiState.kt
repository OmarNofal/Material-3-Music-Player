package com.omar.musica.songs

import androidx.compose.runtime.Immutable
import com.omar.musica.ui.model.SongUi


@Immutable
data class SearchScreenUiState(
    val searchQuery: String,
    val songs: List<SongUi>
) {
    companion object {
        val emptyState = SearchScreenUiState("", listOf())
    }
}