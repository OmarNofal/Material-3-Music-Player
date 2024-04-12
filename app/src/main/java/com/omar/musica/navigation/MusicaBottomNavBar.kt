package com.omar.musica.navigation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.navOptions
import com.omar.musica.settings.navigation.SETTINGS_NAVIGATION_GRAPH
import com.omar.musica.playlists.navigation.PLAYLISTS_NAVIGATION_GRAPH
import com.omar.musica.albums.navigation.ALBUMS_NAVIGATION_GRAPH
import com.omar.musica.songs.navigation.SONGS_NAVIGATION_GRAPH


@Composable
fun MusicaBottomNavBar(
    modifier: Modifier,
    topLevelDestinations: List<TopLevelDestination>,
    currentDestination: NavDestination?,
    onDestinationSelected: (TopLevelDestination) -> Unit
) {

    NavigationBar(
        modifier = modifier
    ) {
        topLevelDestinations.forEach { item ->
            val isSelected = currentDestination.isTopLevelDestinationInHierarchy(item)
            BottomNavItem(item = item, isSelected = isSelected) {
                onDestinationSelected(item)
            }
        }
    }

}

@Composable
fun RowScope.BottomNavItem(
    item: TopLevelDestination,
    isSelected: Boolean,
    onDestinationSelected: () -> Unit
) {

    val icon = if (isSelected) item.iconSelected else item.iconNotSelected
    NavigationBarItem(
        selected = isSelected,
        onClick = onDestinationSelected,
        icon = { Icon(imageVector = icon, contentDescription = null) },
        label = { Text(text = item.title) },
        alwaysShowLabel = false
    )

}


fun NavHostController.navigateToTopLevelDestination(topLevelDestination: TopLevelDestination) {
    val navOptions = navOptions {
        popUpTo(this@navigateToTopLevelDestination.graph.findStartDestination().id) {
            saveState = true
        }
        // Avoid multiple copies of the same destination when
        // reselecting the same item
        launchSingleTop = true
        // Restore state when reselecting a previously selected item
        restoreState = true
    }

    when (topLevelDestination) {
        TopLevelDestination.SONGS -> navigate(SONGS_NAVIGATION_GRAPH, navOptions)
        TopLevelDestination.PLAYLISTS -> navigate(PLAYLISTS_NAVIGATION_GRAPH, navOptions)
        TopLevelDestination.SETTINGS -> navigate(SETTINGS_NAVIGATION_GRAPH, navOptions)
        TopLevelDestination.ALBUMS -> navigate(ALBUMS_NAVIGATION_GRAPH, navOptions)
    }
}

fun NavDestination?.isTopLevelDestinationInHierarchy(destination: TopLevelDestination) =
    this?.hierarchy?.any {
        it.route?.contains(destination.route, true) ?: false
    } ?: false
