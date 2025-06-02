package com.omar.musica.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Album
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.Album
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.People
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.omar.musica.playlists.navigation.PLAYLISTS_NAVIGATION_GRAPH
import com.omar.musica.settings.navigation.SETTINGS_NAVIGATION_GRAPH
import com.omar.musica.albums.navigation.ALBUMS_NAVIGATION_GRAPH
import com.omar.musica.artists.navigation.ARTISTS_NAVIGATION_GRAPH
import com.omar.musica.folders.navigation.FOLDERS_NAVIGATION_GRAPH
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

  ALBUMS(
    Icons.Rounded.Album,
    Icons.Outlined.Album,
    "Albums",
    ALBUMS_NAVIGATION_GRAPH
  ),

  ARTISTS(
    Icons.Rounded.People,
    Icons.Outlined.People,
    "Artists",
    ARTISTS_NAVIGATION_GRAPH
  ),

  FOLDERS(
    Icons.Rounded.Folder,
    Icons.Outlined.Folder,
    "Folders",
    FOLDERS_NAVIGATION_GRAPH
  ),

  SETTINGS(
    Icons.Rounded.Settings,
    Icons.Outlined.Settings,
    "Settings",
    SETTINGS_NAVIGATION_GRAPH
  )
}