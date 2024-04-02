package com.omar.musica.songs.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import androidx.navigation.navigation
import com.omar.musica.songs.ui.search.SearchScreen
import com.omar.musica.songs.ui.SongsScreen
import com.omar.musica.store.model.album.BasicAlbum


const val SONGS_NAVIGATION_GRAPH = "songs_graph"
const val SONGS_ROUTE = "songs_route"
const val SEARCH_ROUTE = "search_route"


fun NavController.navigateToSongs(navOptions: NavOptions? = null) {
    navigate(SONGS_NAVIGATION_GRAPH, navOptions)
}

fun NavController.navigateToSearch(navOptions: NavOptions? = null) {
    navigate(SEARCH_ROUTE, navOptions)
}

fun NavGraphBuilder.songsGraph(
    contentModifier: MutableState<Modifier>,
    navController: NavController,
    enableBackPress: MutableState<Boolean>,
    onNavigateToAlbum: (BasicAlbum) -> Unit,
    enterAnimationFactory:
        (String, AnimatedContentTransitionScope<NavBackStackEntry>) -> EnterTransition,
    exitAnimationFactory:
        (String, AnimatedContentTransitionScope<NavBackStackEntry>) -> ExitTransition,
    popEnterAnimationFactory:
        (String, AnimatedContentTransitionScope<NavBackStackEntry>) -> EnterTransition,
    popExitAnimationFactory:
        (String, AnimatedContentTransitionScope<NavBackStackEntry>) -> ExitTransition,
    ) {

    navigation(
        route = SONGS_NAVIGATION_GRAPH,
        startDestination = SONGS_ROUTE,
    ) {
        composable(
            SONGS_ROUTE,
            enterTransition = {
                enterAnimationFactory(SONGS_ROUTE, this)
            },
            exitTransition = {
                exitAnimationFactory(SONGS_ROUTE, this)
            },
            popEnterTransition = {
                popEnterAnimationFactory(SONGS_ROUTE, this)
            },
            popExitTransition = {
                popExitAnimationFactory(SONGS_ROUTE, this)
            }
        ) {
            SongsScreen(
                contentModifier.value,
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
                            tween(300, easing = FastOutSlowInEasing),
                            initialOffset = { it / 2 }
                        )
            },
            popExitTransition = {
                fadeOut(tween(200)) +
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Down,
                            tween(200, easing = FastOutSlowInEasing),
                            targetOffset = { it / 2}
                        )
            }
        )
        {
            SearchScreen(
                modifier = contentModifier.value,
                onBackPressed = navController::popBackStack,
                onNavigateToAlbum = onNavigateToAlbum,
                enableBackPress = enableBackPress.value
            )
        }
    }

}