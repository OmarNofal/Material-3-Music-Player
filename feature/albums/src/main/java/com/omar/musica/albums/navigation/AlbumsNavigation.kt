package com.omar.musica.songs.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.omar.musica.albums.ui.AlbumsScreen


const val ALBUMS_NAVIGATION_GRAPH = "albums_graph"
const val ALBUMS_ROUTE = "albums"
const val ALBUM_DETAIL_ROUTE = "album/{name}"


fun NavController.navigateToAlbums(navOptions: NavOptions? = null) {
    navigate(ALBUMS_NAVIGATION_GRAPH, navOptions)
}

fun NavController.navigateToAlbumDetail(albumName: String, navOptions: NavOptions? = null) {
    navigate("album/${albumName}", navOptions)
}

fun NavGraphBuilder.albumsGraph(
    contentModifier: MutableState<Modifier>,
    navController: NavController,
    enableBackPress: MutableState<Boolean>,
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
        route = ALBUMS_NAVIGATION_GRAPH,
        startDestination = ALBUMS_ROUTE,
    ) {
        composable(
            ALBUMS_ROUTE,
            enterTransition = {
                enterAnimationFactory(ALBUMS_ROUTE, this)
            },
            exitTransition = {
                exitAnimationFactory(ALBUMS_ROUTE, this)
            },
            popEnterTransition = {
                popEnterAnimationFactory(ALBUMS_ROUTE, this)
            },
            popExitTransition = {
                popExitAnimationFactory(ALBUMS_ROUTE, this)
            }
        ) {
            AlbumsScreen(modifier = contentModifier.value)
        }

        composable(
            ALBUM_DETAIL_ROUTE,
            enterTransition = {
                enterAnimationFactory(ALBUM_DETAIL_ROUTE, this)
            },
            popExitTransition = {
                popExitAnimationFactory(ALBUM_DETAIL_ROUTE, this)
            }
        )
        {

        }
    }

}