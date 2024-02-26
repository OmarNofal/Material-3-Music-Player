package com.omar.musica.playlists.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.omar.musica.playlists.playlistdetail.PlaylistDetailScreen
import com.omar.musica.playlists.playlists.PlaylistsScreen


const val PLAYLISTS_ROUTE = "playlists"
const val PLAYLIST_DETAILS_ROUTE = "playlist_detail"
const val ANIMATION_DURATION = 300
const val PLAYLISTS_NAVIGATION_GRAPH = "playlists_graph"

fun NavController.navigateToPlaylistDetails(id: Int) {
    navigate("$PLAYLIST_DETAILS_ROUTE/$id")
}



fun NavGraphBuilder.playlistsGraph(
    contentModifier: MutableState<Modifier>,
    navController: NavController
) {

    navigation(route = PLAYLISTS_NAVIGATION_GRAPH, startDestination = PLAYLISTS_ROUTE) {
        composable(
            PLAYLISTS_ROUTE,
            enterTransition = ol@{

                val initialRoute = initialState.destination.route ?: return@ol null
                if (initialRoute.contains(PLAYLIST_DETAILS_ROUTE)) {
                    return@ol fadeIn(tween(200), 0.5f) + scaleIn(tween(200), initialScale = 1.2f)
                }

                val slidingDirection = when(initialState.destination.route) {
                    "settings_route" -> AnimatedContentTransitionScope.SlideDirection.End
                    else -> AnimatedContentTransitionScope.SlideDirection.Start
                }
                slideIntoContainer(slidingDirection, animationSpec = tween(200))
            },
            exitTransition = ol@{
                val targetRoute = targetState.destination.route ?: return@ol null

                if (targetRoute.contains(PLAYLIST_DETAILS_ROUTE))
                    return@ol fadeOut(tween(200))

                val slidingDirection = when(targetState.destination.route) {
                    "settings_route" -> AnimatedContentTransitionScope.SlideDirection.Start
                    else -> AnimatedContentTransitionScope.SlideDirection.End
                }
                slideOutOfContainer(slidingDirection, animationSpec = tween(200))
            }
        ) {
            PlaylistsScreen(
                modifier = contentModifier.value,
                navController::navigateToPlaylistDetails
            )
        }


        composable(
            "$PLAYLIST_DETAILS_ROUTE/{id}",
            enterTransition = {
                scaleIn(tween(ANIMATION_DURATION), initialScale = .9f) + fadeIn(tween(300), 0.3f)
            },
            exitTransition = {
                scaleOut(tween(100), targetScale = .9f) + fadeOut(tween(100))
            }

            ) {
            PlaylistDetailScreen(modifier = contentModifier.value, {navController.popBackStack()})
        }

    }

}