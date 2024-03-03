package com.omar.musica.tageditor.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.omar.musica.tageditor.ui.TagEditorScreen


const val TAG_EDITOR_GRAPH = "tag_editor_graph"
const val TAG_EDITOR_SCREEN = "tag_editor_screen"

fun NavGraphBuilder.tagEditorGraph(
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

    navigation(
        route = "$TAG_EDITOR_GRAPH/{uri}",
        startDestination = TAG_EDITOR_SCREEN
    ) {
        composable(
            route = TAG_EDITOR_SCREEN,
            arguments = listOf(navArgument("uri") { type = NavType.StringType }),
            enterTransition = { enterAnimationFactory(TAG_EDITOR_SCREEN, this) },
            exitTransition = { exitAnimationFactory(TAG_EDITOR_SCREEN, this) },
            popEnterTransition = { popEnterAnimationFactory(TAG_EDITOR_SCREEN, this) },
            popExitTransition = { popExitAnimationFactory(TAG_EDITOR_SCREEN, this)}
        ) {
            TagEditorScreen(
                contentModifier.value,
                onClose = { navController.popBackStack() }
            )
        }
    }
}