package com.omar.musica.ui

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavBackStackEntry
import com.omar.musica.playlists.navigation.PLAYLISTS_ROUTE
import com.omar.musica.playlists.navigation.PLAYLIST_DETAILS_ROUTE
import com.omar.musica.settings.navigation.SETTINGS_ROUTE
import com.omar.musica.albums.navigation.ALBUMS_ROUTE
import com.omar.musica.albums.navigation.ALBUM_DETAIL_ROUTE
import com.omar.musica.songs.navigation.SEARCH_ROUTE
import com.omar.musica.songs.navigation.SONGS_ROUTE
import com.omar.musica.tageditor.navigation.TAG_EDITOR_SCREEN
import com.omar.musica.ui.anim.OPEN_SCREEN_ENTER_ANIMATION
import com.omar.musica.ui.anim.OPEN_SCREEN_EXIT_ANIMATION
import com.omar.musica.ui.anim.POP_SCREEN_ENTER_ANIMATION
import com.omar.musica.ui.anim.POP_SCREEN_EXIT_ANIMATION
import com.omar.musica.ui.anim.SLIDE_DOWN_EXIT_ANIMATION
import com.omar.musica.ui.anim.SLIDE_UP_ENTER_ANIMATION
/*
    Determines the animation that screen should enter with
    when it is being opened. It takes the screen route and the
    Animation Scope which contains the NavBackstackEntry to determine
    which screen is being closed to choose the appropriate animation
 */
fun getEnterAnimationForRoute(
    route: String,
    scope: AnimatedContentTransitionScope<NavBackStackEntry>
): EnterTransition {

    val initialRoute = scope.initialState.destination.route

    if (route == SONGS_ROUTE)
        return when {
            initialRoute?.startsWith(SEARCH_ROUTE) == true -> fadeIn()
            else -> POP_SCREEN_ENTER_ANIMATION
        }

    if (route == ALBUMS_ROUTE)
        return OPEN_SCREEN_ENTER_ANIMATION

    if (route == ALBUM_DETAIL_ROUTE)
        return OPEN_SCREEN_ENTER_ANIMATION

    if (route == PLAYLIST_DETAILS_ROUTE)
        return OPEN_SCREEN_ENTER_ANIMATION

    if (route == PLAYLISTS_ROUTE)
        return OPEN_SCREEN_ENTER_ANIMATION

    if (route == TAG_EDITOR_SCREEN)
        return OPEN_SCREEN_ENTER_ANIMATION

    if (route == SETTINGS_ROUTE)
        return OPEN_SCREEN_ENTER_ANIMATION

    if (route == SEARCH_ROUTE)
        return SLIDE_UP_ENTER_ANIMATION

    return fadeIn()
}


fun getExitAnimationForRoute(
    route: String,
    scope: AnimatedContentTransitionScope<NavBackStackEntry>
): ExitTransition {

    val destinationRoute = scope.targetState.destination.route

    if (route == SONGS_ROUTE)
        return when {
            destinationRoute?.contains(SEARCH_ROUTE) == true -> fadeOut()
            else -> OPEN_SCREEN_EXIT_ANIMATION
        }

    if (route == ALBUMS_ROUTE)
        return OPEN_SCREEN_EXIT_ANIMATION

    if (route == ALBUM_DETAIL_ROUTE)
        return OPEN_SCREEN_EXIT_ANIMATION

    if (route == PLAYLIST_DETAILS_ROUTE)
        return OPEN_SCREEN_EXIT_ANIMATION

    if (route == PLAYLISTS_ROUTE)
        return OPEN_SCREEN_EXIT_ANIMATION

    if (route == TAG_EDITOR_SCREEN)
        return OPEN_SCREEN_EXIT_ANIMATION

    if (route == SETTINGS_ROUTE)
        return OPEN_SCREEN_EXIT_ANIMATION

    if (route == SEARCH_ROUTE)
        return SLIDE_DOWN_EXIT_ANIMATION

    return fadeOut()
}



fun getPopEnterAnimationForRoute(
    route: String,
    scope: AnimatedContentTransitionScope<NavBackStackEntry>
): EnterTransition {

    val initialRoute = scope.initialState.destination.route

    if (route == SONGS_ROUTE)
        return when {
            initialRoute?.contains(SEARCH_ROUTE) == true -> fadeIn()
            else -> OPEN_SCREEN_ENTER_ANIMATION
        }

    if (route == ALBUMS_ROUTE)
        return POP_SCREEN_ENTER_ANIMATION

    if (route == ALBUM_DETAIL_ROUTE)
        return POP_SCREEN_ENTER_ANIMATION

    if (route == PLAYLIST_DETAILS_ROUTE)
        return POP_SCREEN_ENTER_ANIMATION

    if (route == PLAYLISTS_ROUTE)
        return POP_SCREEN_ENTER_ANIMATION

    if (route == TAG_EDITOR_SCREEN)
        return POP_SCREEN_ENTER_ANIMATION

    if (route == SETTINGS_ROUTE)
        return POP_SCREEN_ENTER_ANIMATION

    if (route == SEARCH_ROUTE)
        return SLIDE_UP_ENTER_ANIMATION

    return fadeIn()
}



fun getPopExitAnimationForRoute(
    route: String,
    scope: AnimatedContentTransitionScope<NavBackStackEntry>
): ExitTransition {

    val destinationRoute = scope.targetState.destination.route

    if (route == SONGS_ROUTE)
        return when {
            destinationRoute?.contains(SEARCH_ROUTE) == true -> fadeOut()
            else -> OPEN_SCREEN_EXIT_ANIMATION
        }


    if (route == ALBUMS_ROUTE)
        return POP_SCREEN_EXIT_ANIMATION

    if (route == ALBUM_DETAIL_ROUTE)
        return POP_SCREEN_EXIT_ANIMATION

    if (route == PLAYLIST_DETAILS_ROUTE)
        return POP_SCREEN_EXIT_ANIMATION

    if (route == PLAYLISTS_ROUTE)
        return POP_SCREEN_EXIT_ANIMATION

    if (route == TAG_EDITOR_SCREEN)
        return POP_SCREEN_EXIT_ANIMATION

    if (route == SETTINGS_ROUTE)
        return POP_SCREEN_EXIT_ANIMATION

    if (route == SEARCH_ROUTE)
        return SLIDE_DOWN_EXIT_ANIMATION

    return fadeOut()
}
