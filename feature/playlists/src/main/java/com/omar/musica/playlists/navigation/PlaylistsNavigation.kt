package com.omar.musica.playlists.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
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
import androidx.navigation.NavBackStackEntry
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
  navController: NavController,
  enterAnimationFactory:
    (String, AnimatedContentTransitionScope<NavBackStackEntry>) -> EnterTransition,
  exitAnimationFactory:
    (String, AnimatedContentTransitionScope<NavBackStackEntry>) -> ExitTransition,
  popEnterAnimationFactory:
    (String, AnimatedContentTransitionScope<NavBackStackEntry>) -> EnterTransition,
  popExitAnimationFactory:
    (String, AnimatedContentTransitionScope<NavBackStackEntry>) -> ExitTransition,
) {

  navigation(route = PLAYLISTS_NAVIGATION_GRAPH, startDestination = PLAYLISTS_ROUTE) {
    composable(
      PLAYLISTS_ROUTE,
      enterTransition = {
        enterAnimationFactory(PLAYLISTS_ROUTE, this)
      },
      exitTransition = ol@{
        exitAnimationFactory(PLAYLISTS_ROUTE, this)
      },
      popEnterTransition =  {
        popEnterAnimationFactory(PLAYLISTS_ROUTE, this)
      },
      popExitTransition = {
        popExitAnimationFactory(PLAYLISTS_ROUTE, this)
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
        enterAnimationFactory(PLAYLIST_DETAILS_ROUTE, this)
      },
      exitTransition = {
        exitAnimationFactory(PLAYLIST_DETAILS_ROUTE, this)
      },
      popEnterTransition = { popEnterAnimationFactory(PLAYLIST_DETAILS_ROUTE, this) },
      popExitTransition = { popExitAnimationFactory(PLAYLIST_DETAILS_ROUTE, this) }
    ) {
      PlaylistDetailScreen(modifier = contentModifier.value, {navController.popBackStack()})
    }
  }
}