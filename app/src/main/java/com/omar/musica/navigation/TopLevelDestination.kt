package com.omar.musica.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.omar.musica.SETTINGS_NAVIGATION_GRAPH
import com.omar.musica.playlists.navigation.PLAYLISTS_NAVIGATION_GRAPH
import com.omar.musica.songs.navigation.SONGS_NAVIGATION_GRAPH

enum class TopLevelDestination(
    val iconSelected: ImageVector,
    val iconNotSelected: ImageVector,
    val title: String,
    val route: String
) {
    SONGS(
        Icons.Rounded.MusicNote,
        Icons.Outlined.MusicNote,
        "Songs",
        SONGS_NAVIGATION_GRAPH
    ),


    PLAYLISTS(
        Icons.Rounded.LibraryMusic,
        Icons.Outlined.LibraryMusic,
        "Playlists",
        PLAYLISTS_NAVIGATION_GRAPH
    ),


    SETTINGS(
        Icons.Rounded.Settings,
        Icons.Outlined.Settings,
        "Settings",
        SETTINGS_NAVIGATION_GRAPH
    )


}