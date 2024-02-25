package com.omar.musica.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import com.omar.musica.navigation.MusicaBottomNavBar
import com.omar.musica.navigation.TopLevelDestination
import com.omar.musica.state.MusicaAppState
import com.omar.nowplaying.ui.BarState
import com.omar.nowplaying.ui.NowPlayingScreen
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


private val COMPACT_NOW_PLAYING_BAR_HEIGHT = 56.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CompactAppScaffold(
    modifier: Modifier,
    appState: MusicaAppState,
    nowPlayingScreenAnchors: AnchoredDraggableState<BarState>,
    topLevelDestinations: List<TopLevelDestination>,
    currentDestination: NavDestination?,
    onDestinationSelected: (TopLevelDestination) -> Unit,
    content: @Composable (Modifier, MutableState<Modifier>) -> Unit
) {

    val density = LocalDensity.current
    val shouldShowNowPlayingBar by appState.shouldShowNowPlayingScreen.collectAsState(initial = true)
    val nowPlayingBarHeightPx = with(density) { COMPACT_NOW_PLAYING_BAR_HEIGHT.toPx() }
    val shouldShowBottomBar by appState.shouldShowBottomBar.collectAsState(initial = true)

    val navigationBarInsets = WindowInsets.navigationBars

    val bottomNavBarOffset by animateDpAsState(
        targetValue = if (shouldShowBottomBar) 0.dp else 80.dp + with(density) {
            navigationBarInsets.getBottom(this).toDp()
        },
        label = "BottomBar Offset"
    )

    var layoutHeightPx = remember { 0 }
    val bottomNavBarHeightPx =
        with(density) { 80.dp.toPx() }

    var nowPlayingBarMinOffset by remember {
        mutableStateOf(0)
    }

    val scrollProvider = { 1 - (appState.nowPlayingScreenOffset() / nowPlayingBarMinOffset) }
    val barHeightPx = with(density) { COMPACT_NOW_PLAYING_BAR_HEIGHT.toPx() }


    val contentModifier = remember {
        mutableStateOf<Modifier>(Modifier)
    }

    LaunchedEffect(key1 = shouldShowNowPlayingBar) {
        if (!shouldShowNowPlayingBar)
            nowPlayingScreenAnchors.animateTo(BarState.COLLAPSED)
    }

    LaunchedEffect(key1 = shouldShowBottomBar, key2 = shouldShowNowPlayingBar) {
        contentModifier.value = Modifier.padding(
            bottom = calculateBottomPaddingForContent(
                shouldShowNowPlayingBar,
                if (shouldShowBottomBar) 80.dp else 0.dp,
                COMPACT_NOW_PLAYING_BAR_HEIGHT
            )
        )
    }

    // App itself
    Box(modifier = modifier) {

        // DrawContentFirst
        Box(modifier = Modifier.fillMaxSize()) {
            content(
                Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxSize()
                    .navigationBarsPadding(),
                contentModifier
            )
        }


        AnimatedVisibility(
            visible = shouldShowNowPlayingBar,
            enter = slideInVertically(
                tween(500),
                initialOffsetY = { nowPlayingBarHeightPx.roundToInt() * 2 }),
            exit = slideOutVertically(
                spring(),
                targetOffsetY = { -nowPlayingBarHeightPx.roundToInt() })
        ) {

            NowPlayingScreen(
                barHeight = COMPACT_NOW_PLAYING_BAR_HEIGHT,
                nowPlayingBarPadding = PaddingValues(0.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .offset {
                        IntOffset(
                            x = 0,
                            y = nowPlayingScreenAnchors
                                .requireOffset()
                                .roundToInt() - ((1 - scrollProvider()) * navigationBarInsets.getBottom(
                                this
                            )).toInt(),
                        ) + if (!shouldShowBottomBar || bottomNavBarOffset != 0.dp)
                            IntOffset(
                                x = 0,
                                y = ((bottomNavBarOffset.toPx() - navigationBarInsets.getBottom(
                                    this
                                )) * (1 - scrollProvider()))
                                    .toInt()
                                    .coerceIn(
                                        0,
                                        80.dp
                                            .toPx()
                                            .toInt()
                                    )
                            )
                        else IntOffset.Zero
                    }

                    .onSizeChanged { layoutSize ->
                        layoutHeightPx = layoutSize.height
                        nowPlayingBarMinOffset = nowPlayingScreenAnchors
                            .update(
                                layoutHeightPx,
                                nowPlayingBarHeightPx.toInt(),
                                bottomNavBarHeightPx.toInt()
                            )
                    }
                    .anchoredDraggable(nowPlayingScreenAnchors, Orientation.Vertical),
                onCollapseNowPlaying = {
                    appState.coroutineScope.launch {
                        nowPlayingScreenAnchors.animateTo(BarState.COLLAPSED)
                    }
                },
                onExpandNowPlaying = {
                    appState.coroutineScope.launch {
                        nowPlayingScreenAnchors.animateTo(BarState.EXPANDED)
                    }
                },
                isExpanded = nowPlayingScreenAnchors.currentValue == BarState.EXPANDED,
                progressProvider = scrollProvider,
                viewModel = appState.nowPlayingViewModel
            )
        }

        MusicaBottomNavBar(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .graphicsLayer { alpha = 1 - scrollProvider() }
                .offset {
                    val navigationBarHeight = 80.dp.toPx()
                    if (!shouldShowBottomBar || bottomNavBarOffset != 0.dp)
                        IntOffset(
                            0,
                            bottomNavBarOffset
                                .toPx()
                                .toInt()
                        )
                    else
                        IntOffset(
                            0,
                            (navigationBarHeight * scrollProvider()).toInt()
                        )
                },
            topLevelDestinations = topLevelDestinations,
            currentDestination = currentDestination,
            onDestinationSelected = onDestinationSelected
        )

        ViewNowPlayingScreenListenerEffect(
            navController = appState.navHostController,
            onViewNowPlayingScreen = {
                appState.coroutineScope.launch {
                    nowPlayingScreenAnchors.animateTo(
                        BarState.EXPANDED
                    )
                }
            }
        )

    }

}