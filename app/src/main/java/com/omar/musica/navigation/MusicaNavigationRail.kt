package com.omar.musica.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination

@Composable
fun MusicaNavigationRail(
    modifier: Modifier,
    topLevelDestinations: List<TopLevelDestination>,
    currentDestination: NavDestination?,
    onDestinationSelected: (TopLevelDestination) -> Unit
) {

    NavigationRail(
        modifier = modifier
    ) {
        topLevelDestinations.forEach { item ->
            val isSelected = currentDestination.isTopLevelDestinationInHierarchy(item)
            NavRailItem(item = item, isSelected = isSelected) {
                onDestinationSelected(item)
            }
        }
    }

}

@Composable
fun NavRailItem(
    item: TopLevelDestination,
    isSelected: Boolean,
    onDestinationSelected: () -> Unit
) {

    val icon = if (isSelected) item.iconSelected else item.iconNotSelected
    NavigationRailItem(
        selected = isSelected,
        onClick = onDestinationSelected,
        icon = { Icon(imageVector = icon, contentDescription = null) },
        label = { Text(text = item.title) },
        alwaysShowLabel = false
    )

}
