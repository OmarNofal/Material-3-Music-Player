package com.omar.musica.ui

import android.app.Activity
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.util.Consumer
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import com.omar.musica.navigation.TopLevelDestination
import com.omar.musica.navigation.navigateToTopLevelDestination
import com.omar.musica.playback.PlaybackService
import com.omar.musica.playlists.navigation.playlistsGraph
import com.omar.musica.settings.navigation.settingsGraph
import com.omar.musica.songs.navigation.SONGS_NAVIGATION_GRAPH
import com.omar.musica.songs.navigation.songsGraph
import com.omar.musica.state.rememberMusicaAppState
import com.omar.musica.tageditor.navigation.tagEditorGraph
import com.omar.nowplaying.ui.BarState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


val topLevelDestinations =
    listOf(
        TopLevelDestination.SONGS,
        TopLevelDestination.PLAYLISTS,
        TopLevelDestination.SETTINGS
    )


@OptIn(ExperimentalMaterial3WindowSizeClassApi::class, ExperimentalFoundationApi::class)
@Composable
fun MusicaApp2(
    modifier: Modifier,
    navController: NavHostController
) {


    val widthClass = calculateWindowSizeClass(activity = LocalContext.current as Activity)

    val density = LocalDensity.current
    val nowPlayingScreenAnchors = remember {
        AnchoredDraggableState(
            BarState.COLLAPSED,
            positionalThreshold = { distance: Float -> 0.5f * distance },
            velocityThreshold = { with(density) { 70.dp.toPx() } },
            animationSpec = tween()
        )
    }

    val appState = rememberMusicaAppState(
        navHostController = navController,
        isNowPlayingExpanded = nowPlayingScreenAnchors.currentValue == BarState.EXPANDED,
        nowPlayingViewModel = hiltViewModel(),
        nowPlayingScreenOffset = {
            if (nowPlayingScreenAnchors.anchors.size > 0)
                nowPlayingScreenAnchors.requireOffset()
            else 0.0f
        },
    )


    val navHost = remember {
        movableContentOf<Modifier, MutableState<Modifier>> { navHostModifier, contentModifier ->
            NavHost(
                modifier = navHostModifier,
                navController = appState.navHostController,
                startDestination = SONGS_NAVIGATION_GRAPH
            ) {
                songsGraph(
                    contentModifier = contentModifier,
                    navController,
                    enableBackPress = mutableStateOf(false)
                )
                playlistsGraph(
                    contentModifier = contentModifier,
                    navController
                )
                settingsGraph(contentModifier = contentModifier)
                tagEditorGraph(contentModifier = contentModifier, navController)
            }
        }
    }

    if (widthClass.widthSizeClass > WindowWidthSizeClass.Compact) {
        ExpandedAppScaffold(
            modifier = modifier,
            appState = appState,
            nowPlayingScreenAnchors = nowPlayingScreenAnchors,
            topLevelDestinations = topLevelDestinations,
            currentDestination = navController.currentBackStackEntryAsState().value?.destination,
            onDestinationSelected = { navController.navigateToTopLevelDestination(it) }
        ) { navHostModifier, contentModifier ->
            navHost(navHostModifier, contentModifier)
        }
    } else {
        CompactAppScaffold(
            modifier = modifier,
            appState = appState,
            nowPlayingScreenAnchors = nowPlayingScreenAnchors,
            topLevelDestinations = topLevelDestinations,
            currentDestination = navController.currentBackStackEntryAsState().value?.destination,
            onDestinationSelected = { navController.navigateToTopLevelDestination(it) }
        ) { navHostModifier, contentModifier ->
            navHost(navHostModifier, contentModifier)
        }
    }

    ViewNowPlayingScreenListenerEffect(
        navController = navController,
        onViewNowPlayingScreen = {
            appState.coroutineScope.launch {
                nowPlayingScreenAnchors.animateTo(
                    BarState.EXPANDED
                )
            }
        }
    )

    NowPlayingCollapser(navController = navController) {
        if (nowPlayingScreenAnchors.currentValue == BarState.EXPANDED) {
            nowPlayingScreenAnchors.animateTo(BarState.COLLAPSED)
        }
    }

}

/**
 * This is responsible to collapse the NowPlayingScreen
 * when a navigation event happens
 */
@Composable
fun NowPlayingCollapser(
    navController: NavHostController,
    onCollapse: suspend () -> Unit
) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    LaunchedEffect(key1 = currentBackStackEntry) {
        onCollapse()
    }
}

/*
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
            velocityThreshold = { with(density) { 50.dp.toPx() } },
            animationSpec = tween()
        )
    }


    val nowPlayingBarHeight = 64.dp
    val barHeightPx = with(density) { nowPlayingBarHeight.toPx() }

    var boxMinOffset by remember { mutableFloatStateOf(0.0f) }

    val scrollProvider = { 1 - (anchorState.offset / boxMinOffset) }


    val isExpanded = anchorState.currentValue == BarState.EXPANDED

    val appState = rememberMusicaAppState(
        navHostController = navController,
        isNowPlayingExpanded = isExpanded,
        nowPlayingViewModel = hiltViewModel(),
        nowPlayingScreenOffset = scrollProvider,
    )

    val shouldShowBottomBar by appState.shouldShowBottomBar.collectAsState(initial = true)
    val shouldShowNowPlayingBar by appState.shouldShowNowPlayingScreen.collectAsState(initial = false)

    val bottomNavBarOffset by animateDpAsState(
        targetValue = if (shouldShowBottomBar) 0.dp else 80.dp,
        label = "BottomBar Offset"
    )


    var layoutHeightPx = remember { 0 }
    val bottomNavBarHeightPx =
        with(density) { 80.dp.toPx() }


    // App itself
    Box(modifier = modifier) {

        // DrawContentFirst
        NavHost(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(
                    bottom = calculateBottomPaddingForContent(
                        shouldShowNowPlayingBar,
                        80.dp - bottomNavBarOffset,
                        nowPlayingBarHeight
                    )
                )
                .fillMaxSize()
                .navigationBarsPadding(),
            navController = navController,
            startDestination = SONGS_NAVIGATION_GRAPH
        ) {
            songsGraph(navController, enableBackPress = mutableStateOf(false))

            playlistsGraph(navController)

            settingsGraph()
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
                barHeight = nowPlayingBarHeight,
                nowPlayingBarPadding = PaddingValues(0.dp),
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
                        layoutHeightPx = layoutSize.height
                        boxMinOffset = anchorState
                            .update(
                                layoutHeightPx,
                                barHeightPx.toInt(),
                                with(density) {
                                    bottomNavBarHeightPx.toInt() - bottomNavBarOffset
                                        .toPx()
                                        .toInt()
                                }
                            )
                            .toFloat()
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


        LaunchedEffect(key1 = shouldShowBottomBar) {
            anchorState.update(
                layoutHeightPx,
                barHeightPx.toInt(),
                with(density) { bottomNavBarHeightPx.toInt() - bottomNavBarOffset.toPx().toInt() },
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
                    IntOffset(0, (navigationBarHeight * scrollProvider()).toInt()) +
                            IntOffset(
                                0,
                                bottomNavBarOffset
                                    .toPx()
                                    .toInt()
                            )
                },
            topLevelDestinations = topLevelDestinations,
            currentDestination = backStackState?.destination,
            onDestinationSelected = {
                navController.navigateToTopLevelDestination(it)
            }
        )

        ViewNowPlayingScreenListenerEffect(
            navController = navController,
            onViewNowPlayingScreen = { scope.launch { anchorState.animateTo(BarState.EXPANDED) } }
        )

    }
}

*/

/**
 * Responsible to expand the NowPlayingScreen when an intent is received
 * or when the app is launched from the media notification
 */
@Composable
fun ViewNowPlayingScreenListenerEffect(
    navController: NavController,
    onViewNowPlayingScreen: () -> Unit
) {
    val context = LocalContext.current
    var handledIntent by rememberSaveable {
        mutableStateOf(false)
    }
    LaunchedEffect(key1 = Unit) {
        delay(500)
        val activity = (context as? Activity) ?: return@LaunchedEffect
        val action = activity.intent.action
        if (action == PlaybackService.VIEW_MEDIA_SCREEN_ACTION && !handledIntent) {
            onViewNowPlayingScreen()
            handledIntent = true
        }
    }

    DisposableEffect(navController) {
        val listener = Consumer<Intent> {
            if (it.action == PlaybackService.VIEW_MEDIA_SCREEN_ACTION) {
                onViewNowPlayingScreen()
            }
        }
        val activity = (context as? ComponentActivity) ?: return@DisposableEffect onDispose { }
        activity.addOnNewIntentListener(listener)
        onDispose { activity.removeOnNewIntentListener(listener) }
    }
}


@OptIn(ExperimentalFoundationApi::class)
fun AnchoredDraggableState<BarState>.update(
    layoutHeightPx: Int,
    barHeightPx: Int,
    bottomBarHeightPx: Int
): Int {
    var offset = 0
    updateAnchors(
        DraggableAnchors {
            offset =
                (-barHeightPx + layoutHeightPx - bottomBarHeightPx)
            BarState.COLLAPSED at offset.toFloat()
            BarState.EXPANDED at 0.0f
        },
        this.currentValue
    )
    return offset
}

fun calculateBottomPaddingForContent(
    shouldShowNowPlayingBar: Boolean,
    bottomBarHeight: Dp,
    nowPlayingBarHeight: Dp
): Dp {
    return bottomBarHeight + (if (shouldShowNowPlayingBar) nowPlayingBarHeight else 0.dp)
}