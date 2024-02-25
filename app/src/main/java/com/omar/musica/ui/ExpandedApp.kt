package com.omar.musica.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.gestures.snapTo
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import com.omar.musica.navigation.MusicaNavigationRail
import com.omar.musica.navigation.TopLevelDestination
import com.omar.musica.state.MusicaAppState
import com.omar.nowplaying.ui.BarState
import com.omar.nowplaying.ui.NowPlayingScreen
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


private val EXPANDED_SCREEN_NOW_PLAYING_HEIGHT = 56.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExpandedAppScaffold(
    modifier: Modifier,
    appState: MusicaAppState,
    nowPlayingScreenAnchors: AnchoredDraggableState<BarState>,
    topLevelDestinations: List<TopLevelDestination>,
    currentDestination: NavDestination?,
    onDestinationSelected: (TopLevelDestination) -> Unit,
    content: @Composable (Modifier, MutableState<Modifier>) -> Unit
) {

    var nowPlayingMinOffset by remember { mutableStateOf(0) }
    val density = LocalDensity.current

    // Navhost takes the whole available screen.
    // contentModifier is added to the screens (composables) themselves to handle cases
    // such as when NowPlayingBar is hidden or visible
    val contentModifier = remember {
        mutableStateOf<Modifier>(Modifier)
    }

    val shouldShowNowPlayingBar by appState.shouldShowNowPlayingScreen.collectAsState(initial = true)

    LaunchedEffect(shouldShowNowPlayingBar) {
        contentModifier.value =
            Modifier.padding(bottom = if (shouldShowNowPlayingBar) EXPANDED_SCREEN_NOW_PLAYING_HEIGHT else 0.dp)
        if (!shouldShowNowPlayingBar)
            nowPlayingScreenAnchors.snapTo(BarState.COLLAPSED)
    }

    Box(modifier = modifier) {
        Row(Modifier.fillMaxSize()) {

            MusicaNavigationRail(
                modifier = Modifier,
                topLevelDestinations = topLevelDestinations,
                currentDestination = currentDestination,
                onDestinationSelected = onDestinationSelected,
            )

            val layoutDirection = LocalLayoutDirection.current
            content(
                Modifier
                    .fillMaxSize()
                    .consumeRailInsets(layoutDirection, density, WindowInsets.navigationBars),
                contentModifier,
            )

        }

        val nowPlayingBarHeightPx = with(density) { EXPANDED_SCREEN_NOW_PLAYING_HEIGHT.toPx() }
        val navigationBarInsets = WindowInsets.navigationBars
        AnimatedVisibility(
            visible = appState.shouldShowNowPlayingScreen.collectAsState(initial = false).value,
            enter = slideInVertically(
                tween(500),
                initialOffsetY = { nowPlayingBarHeightPx.roundToInt() * 2 }),
            exit = slideOutVertically(
                tween(500),
                targetOffsetY = { -nowPlayingBarHeightPx.roundToInt() * 2 })
        ) {
            NowPlayingScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .offset {
                        IntOffset(
                            x = 0,
                            y = nowPlayingScreenAnchors
                                .requireOffset()
                                .roundToInt(),
                        )
                    }
                    .onSizeChanged {
                        nowPlayingMinOffset = nowPlayingScreenAnchors.update(
                            it.height,
                            with(density) { EXPANDED_SCREEN_NOW_PLAYING_HEIGHT.toPx() }.toInt(),
                            0
                        ) - with(density) { navigationBarInsets.getBottom(this) }
                    }
                    .anchoredDraggable(nowPlayingScreenAnchors, Orientation.Vertical),
                nowPlayingBarPadding =
                PaddingValues(
                    bottom = with(density) { navigationBarInsets.getBottom(this).toDp() },
                    end = with(density) {
                        navigationBarInsets.getRight(this, LayoutDirection.Ltr).toDp()
                    },
                    start = with(density) {
                        navigationBarInsets.getLeft(this, LayoutDirection.Ltr).toDp()
                    }
                ),
                barHeight = EXPANDED_SCREEN_NOW_PLAYING_HEIGHT,
                isExpanded = nowPlayingScreenAnchors.currentValue == BarState.EXPANDED,
                onCollapseNowPlaying = {
                    appState.coroutineScope.launch {
                        nowPlayingScreenAnchors.animateTo(
                            BarState.COLLAPSED
                        )
                    }
                },
                onExpandNowPlaying = {
                    appState.coroutineScope.launch {
                        nowPlayingScreenAnchors.animateTo(
                            BarState.EXPANDED
                        )
                    }
                },
                progressProvider = { 1 - (appState.nowPlayingScreenOffset() / nowPlayingMinOffset) }
            )
        }

    }

}

fun Modifier.consumeRailInsets(
    layoutDirection: LayoutDirection,
    density: Density,
    navigationBarsInsets: WindowInsets
): Modifier =
    this.consumeWindowInsets(
        PaddingValues(start = if (layoutDirection == LayoutDirection.Ltr)
            with(density) {
                navigationBarsInsets
                    .getLeft(
                        density,
                        layoutDirection
                    )
                    .toDp()
            }
        else
            with(density) {
                navigationBarsInsets
                    .getRight(
                        density,
                        layoutDirection
                    )
                    .toDp()
            }
        )
    ) // consume the insets handled by the Rail
