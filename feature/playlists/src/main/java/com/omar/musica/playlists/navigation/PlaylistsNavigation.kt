package com.omar.musica.playlists.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.omar.musica.playlists.playlists.PlaylistsScreen


const val PLAYLISTS_ROUTE = "playlists"

const val PLAYLISTS_NAVIGATION_GRAPH = "playlists_graph"

fun NavGraphBuilder.playlistsGraph() {

    navigation(route = PLAYLISTS_NAVIGATION_GRAPH, startDestination = PLAYLISTS_ROUTE) {
        composable(PLAYLISTS_ROUTE) {
            PlaylistsScreen(modifier = Modifier.fillMaxSize())
        }
    }

}