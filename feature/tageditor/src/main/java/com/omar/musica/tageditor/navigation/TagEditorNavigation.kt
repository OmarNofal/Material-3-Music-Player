package com.omar.musica.tageditor.navigation

import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
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
) {

    navigation(
        route = "$TAG_EDITOR_GRAPH/{uri}",
        startDestination = TAG_EDITOR_SCREEN
    ) {
        composable(
            route = TAG_EDITOR_SCREEN,
            arguments = listOf(navArgument("uri") { type = NavType.StringType })
        ) {
            TagEditorScreen(
                contentModifier.value,
                onClose = { navController.popBackStack() }
            )
        }
    }
}