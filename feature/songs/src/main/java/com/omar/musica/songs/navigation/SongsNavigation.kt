package com.omar.musica.songs.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
    navController: NavController
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
                if (initialState.destination.route != SEARCH_ROUTE) {
                    fadeOut()
                } else
                    fadeOut(tween(300)) +
                            slideOutOfContainer(
                                AnimatedContentTransitionScope.SlideDirection.Up,
                                tween(300, easing = FastOutSlowInEasing)
                            )
            },
            popEnterTransition = {
                if (initialState.destination.route != SEARCH_ROUTE) {
                    fadeIn()
                } else
                    fadeIn(tween(300)) +
                            slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.Down,
                                tween(300, easing = FastOutSlowInEasing)
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
                fadeIn(tween(300)) +
                        slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up,
                            tween(300, easing = FastOutSlowInEasing),
                            initialOffset = { it -> it }
                        )
            },
            popExitTransition = {
                fadeOut(tween(300)) +
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Down,
                            tween(300, easing = FastOutSlowInEasing)
                        )
            }
        )
        {
            SearchScreen(
                modifier = Modifier.fillMaxSize(),
                onBackPressed = navController::popBackStack
            )
        }
    }

}