package com.omar.musica.songs.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import androidx.navigation.navigation
import com.omar.musica.songs.ui.SearchScreen
import com.omar.musica.songs.ui.SongsScreen


const val SONGS_NAVIGATION_GRAPH = "songs_graph"
const val SONGS_ROUTE = "songs_route"
private const val SEARCH_ROUTE = "search_route"


fun NavController.navigateToSongs(navOptions: NavOptions? = null) {
    navigate(SONGS_NAVIGATION_GRAPH, navOptions)
}

fun NavController.navigateToSearch(navOptions: NavOptions? = null) {
    navigate(SEARCH_ROUTE, navOptions)
}

fun NavGraphBuilder.songsGraph(
    navController: NavController,
    enableBackPress: Boolean = true,
) {

    navigation(
        route = SONGS_NAVIGATION_GRAPH,
        startDestination = SONGS_ROUTE,
    ) {
        composable(
            SONGS_ROUTE,
            enterTransition = {
                EnterTransition.None
            },
            exitTransition = {
                if (targetState.destination.route != SEARCH_ROUTE) {
                    slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start, animationSpec = tween(200))
                } else
                    fadeOut(tween(200)) +
                            slideOutVertically(
                                animationSpec = tween(200, easing = FastOutSlowInEasing),
                                targetOffsetY = { -it / 4}
                            )
            },
            popEnterTransition = {
                if (initialState.destination.route != SEARCH_ROUTE) {
                    slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End, animationSpec = tween(200))
                } else
                    fadeIn(tween(200)) +
                            slideInVertically (
                                animationSpec = tween(200, easing = FastOutSlowInEasing),
                                initialOffsetY = { it -> -it / 4}
                            )
            }
        ) {
            SongsScreen(
                Modifier.fillMaxSize(),
                onSearchClicked = {
                    navController.navigateToSearch(
                        navOptions = navOptions {
                            anim {
                                popEnter
                            }

                        }
                    )
                }
            )
        }

        composable(
            SEARCH_ROUTE,
            enterTransition = {
                fadeIn(tween(200)) +
                        slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up,
                            tween(200, easing = FastOutSlowInEasing),
                            initialOffset = { it -> it / 2 }
                        )
            },
            popExitTransition = {
                fadeOut(tween(200)) +
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Down,
                            tween(200, easing = FastOutSlowInEasing),
                            targetOffset = { it -> it / 2}
                        )
            }
        )
        {
            SearchScreen(
                modifier = Modifier.fillMaxSize(),
                onBackPressed = navController::popBackStack,
                enableBackPress = enableBackPress
            )
        }
    }

}