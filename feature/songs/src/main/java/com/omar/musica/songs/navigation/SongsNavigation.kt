package com.omar.musica.songs.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.omar.musica.songs.ui.SearchScreen
import com.omar.musica.songs.ui.SongsScreen


const val SONGS_NAVIGATION_ROUTE_PATTERN = "songs_graph"
const val SONGS_ROUTE = "songs_route"
private const val SEARCH_ROUTE = "search_route"


fun NavController.navigateToSongs(navOptions: NavOptions? = null) {
    navigate(SONGS_NAVIGATION_ROUTE_PATTERN, navOptions)
}

fun NavController.navigateToSearch(navOptions: NavOptions? = null) {
    navigate(SEARCH_ROUTE, navOptions)
}

fun NavGraphBuilder.songsGraph(
    navController: NavController,
    onOpenNowPlaying: () -> Unit,
) {

    navigation(
        route = SONGS_NAVIGATION_ROUTE_PATTERN,
        startDestination = SONGS_ROUTE,
    ) {
        composable(
            SONGS_ROUTE,
            enterTransition = {
                EnterTransition.None
            },
            exitTransition = {
                fadeOut(tween(300))+
                        slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Up,
                            tween(300, easing = FastOutSlowInEasing)
                        )
            },
            popEnterTransition = {
                fadeIn(tween(300))+
                        slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Down,
                            tween(300, easing = FastOutSlowInEasing)
                        )
            }
        ) {
            SongsScreen(
                Modifier.fillMaxSize(),
                onSearchClicked = {
                    navController.navigateToSearch()
                },
                onOpenNowPlaying = onOpenNowPlaying
            )
        }

        composable(
            SEARCH_ROUTE,
            enterTransition = {
                fadeIn(tween(300))+
                        slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up,
                            tween(300, easing = FastOutSlowInEasing),
                            initialOffset = { it -> it}
                        )
            },
            popExitTransition =  {
                fadeOut(tween(300))+
                        slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down,
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