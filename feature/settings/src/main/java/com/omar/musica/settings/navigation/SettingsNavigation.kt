package com.omar.musica.settings.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.omar.musica.settings.SettingsScreen


const val SETTINGS_NAVIGATION_GRAPH = "settings_graph"
const val SETTINGS_ROUTE = "settings_route"


fun NavGraphBuilder.settingsGraph(
    contentModifier: MutableState<Modifier>,
) {

    navigation(
        route = SETTINGS_NAVIGATION_GRAPH,
        startDestination = SETTINGS_ROUTE,
    ) {
        composable(
            SETTINGS_ROUTE,
            enterTransition = {
                val animationDuration = when (initialState.destination.route) {
                    "songs_route" -> 400
                    else -> 200
                }
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, animationSpec = tween(200))
            },
            exitTransition = {
                val animationDuration = when (targetState.destination.route) {
                    "songs_route" -> 400
                    else -> 100
                }
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, animationSpec = tween(200))
            }
        ) {
            SettingsScreen(modifier = contentModifier.value)
        }
    }

}