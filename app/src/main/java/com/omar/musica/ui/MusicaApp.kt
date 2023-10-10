package com.omar.musica.ui

import android.app.Activity
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.util.Consumer
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.omar.musica.PLAYLIST_NAVIGATION_GRAPH
import com.omar.musica.SETTINGS_NAVIGATION_GRAPH
import com.omar.musica.navigation.MusicaBottomNavBar
import com.omar.musica.navigation.TopLevelDestination
import com.omar.musica.navigation.navigateToTopLevelDestination
import com.omar.musica.playback.PlaybackService
import com.omar.musica.songs.navigation.SONGS_NAVIGATION_GRAPH
import com.omar.musica.songs.navigation.songsGraph
import com.omar.musica.state.rememberMusicaAppState
import com.omar.nowplaying.ui.BarState
import com.omar.nowplaying.ui.NowPlayingScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


val topLevelDestinations =
    listOf(
        TopLevelDestination.SONGS,
        TopLevelDestination.PLAYLISTS,
        TopLevelDestination.SETTINGS
    )


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MusicaApp(
    modifier: Modifier
) {

    val navController = rememberNavController()

    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val anchorState = remember {
        AnchoredDraggableState(
            BarState.COLLAPSED,
            positionalThreshold = { distance: Float -> 0.5f * distance },
            velocityThreshold = { with(density) { 100.dp.toPx() } },
            animationSpec = tween()
        )
    }



    val barHeight = 64.dp
    val barHeightPx = with(density) { barHeight.toPx() }

    var boxMinOffset by remember { mutableFloatStateOf(0.0f) }

    // The box which contains the App Scaffold and the
    // Now Playing Bar

    val scrollProvider = { 1 - (anchorState.offset / boxMinOffset) }


    val isExpanded by remember {
        derivedStateOf { (1 - (anchorState.offset / boxMinOffset)) >= 0.9f }
    }


    val appState = rememberMusicaAppState(
        navHostController = navController,
        isNowPlayingExpanded = isExpanded,
        nowPlayingViewModel = hiltViewModel(),
        nowPlayingVisibilityProvider = scrollProvider,
    )



    val shouldShowNowPlayingBar by appState.shouldShowNowPlayingScreen.collectAsState(initial = false)

    val context = LocalContext.current
    LaunchedEffect(key1 = Unit) {
        delay(500)
        val activity = (context as? Activity) ?: return@LaunchedEffect
        val action = activity.intent.action
        if (action == PlaybackService.VIEW_MEDIA_SCREEN_ACTION && shouldShowNowPlayingBar) {
            anchorState.animateTo(BarState.EXPANDED)
        }
    }

    // App itself
    Box(modifier = modifier) {

        // DrawContentFirst
        NavHost(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp + if (shouldShowNowPlayingBar) barHeight else 0.dp)
                .navigationBarsPadding(),
            navController = navController,
            startDestination = SONGS_NAVIGATION_GRAPH
        ) {
            songsGraph(navController, enableBackPress = !appState.isNowPlayingExpanded)

            composable(PLAYLIST_NAVIGATION_GRAPH) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Playlists")
                }
            }

            composable(SETTINGS_NAVIGATION_GRAPH) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Settings")
                }
            }

        }

        val navigationBarInsets = WindowInsets.navigationBars
        AnimatedVisibility(
            visible = shouldShowNowPlayingBar,
            enter = slideInVertically(
                tween(500),
                initialOffsetY = { barHeightPx.roundToInt() * 2 }),
            exit = slideOutVertically(spring(), targetOffsetY = { -barHeightPx.roundToInt() })
        ) {

            NowPlayingScreen(
                barHeight = barHeight,
                modifier = Modifier
                    .fillMaxSize()
                    .offset {
                        IntOffset(
                            // 2
                            x = 0,
                            y = anchorState
                                .requireOffset()
                                .roundToInt() - ((1 - scrollProvider()) * navigationBarInsets.getBottom(
                                this
                            )).toInt(),
                        )
                    }

                    .onSizeChanged { layoutSize ->
                        anchorState.updateAnchors(
                            DraggableAnchors {
                                // 5
                                val bottomNavBarHeightPx =
                                    with(density) { 80.dp.toPx() }
                                val offset =
                                    (-barHeightPx + layoutSize.height - bottomNavBarHeightPx)
                                boxMinOffset = offset
                                BarState.COLLAPSED at offset
                                BarState.EXPANDED at 0.0f
                            }
                        )
                    }
                    .anchoredDraggable(anchorState, Orientation.Vertical),
                onCollapseNowPlaying = {
                    scope.launch {
                        anchorState.animateTo(BarState.COLLAPSED)
                    }
                },
                onExpandNowPlaying = {
                    scope.launch {
                        anchorState.animateTo(BarState.EXPANDED)
                    }
                },
                isExpanded = isExpanded,
                progressProvider = { 1 - (anchorState.offset / boxMinOffset) },
                viewModel = appState.nowPlayingViewModel
            )
        }


        // Finally draw the bottom nav bar
        val backStackState by appState.navHostController.currentBackStackEntryAsState()
        MusicaBottomNavBar(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .graphicsLayer { alpha = 1 - scrollProvider() }
                .offset {
                    val navigationBarHeight = 80.dp.toPx()
                    IntOffset(0, (navigationBarHeight * scrollProvider()).toInt())
                },
            topLevelDestinations = topLevelDestinations,
            currentDestination = backStackState?.destination,
            onDestinationSelected = {
                navController.navigateToTopLevelDestination(
                    it
                )
            }
        )


    }



    DisposableEffect(navController) {
        val listener = Consumer<Intent> {
            if (it.action == PlaybackService.VIEW_MEDIA_SCREEN_ACTION) {
                scope.launch {
                    anchorState.animateTo(BarState.EXPANDED)
                }
            }
        }
        val activity = (context as? ComponentActivity) ?: return@DisposableEffect onDispose {  }
        activity.addOnNewIntentListener(listener)
        onDispose { activity.removeOnNewIntentListener(listener) }
    }


}