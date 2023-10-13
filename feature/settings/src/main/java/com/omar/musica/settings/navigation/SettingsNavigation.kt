package com.omar.musica.settings.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.omar.musica.settings.SettingsScreen


const val SETTINGS_NAVIGATION_GRAPH = "settings_graph"
const val SETTINGS_ROUTE = "settings_route"


fun NavGraphBuilder.settingsGraph() {

    navigation(
        route = SETTINGS_NAVIGATION_GRAPH,
        startDestination = SETTINGS_ROUTE,
    ) {
        composable(
            SETTINGS_ROUTE
        ) {
            SettingsScreen(Modifier.fillMaxSize())
        }
    }

}