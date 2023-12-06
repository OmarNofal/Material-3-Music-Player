package com.omar.musica.playlists.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
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
        composable(
            PLAYLISTS_ROUTE,
            enterTransition = {
                val slidingDirection = when(initialState.destination.route) {
                    "settings_route" -> AnimatedContentTransitionScope.SlideDirection.End
                    else -> AnimatedContentTransitionScope.SlideDirection.Start
                }
                slideIntoContainer(slidingDirection, animationSpec = tween(100))
            },
            exitTransition = {
                val slidingDirection = when(targetState.destination.route) {
                    "settings_route" -> AnimatedContentTransitionScope.SlideDirection.Start
                    else -> AnimatedContentTransitionScope.SlideDirection.End
                }
                slideOutOfContainer(slidingDirection, animationSpec = tween(100))
            }
        ) {
            PlaylistsScreen(modifier = Modifier.fillMaxSize())
        }
    }

}