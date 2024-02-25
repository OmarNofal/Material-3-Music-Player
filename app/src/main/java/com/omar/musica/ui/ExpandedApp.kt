package com.omar.musica.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationRail
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
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
    topLevelDestinations: List<TopLevelDestination>,
    currentDestination: NavDestination?,
    onDestinationSelected: (TopLevelDestination) -> Unit,
    content: @Composable (Modifier) -> Unit
) {

    var nowPlayingMinOffset by remember { mutableStateOf(0) }
    val density = LocalDensity.current
    val nowPlayingScreenAnchors = remember {
        AnchoredDraggableState(
            BarState.COLLAPSED,
            positionalThreshold = { distance: Float -> 0.5f * distance },
            velocityThreshold = { with(density) { 50.dp.toPx() } },
            animationSpec = tween()
        )
    }

    val shouldShowNowPlayingBar by appState.shouldShowNowPlayingScreen.collectAsState(initial = true)
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
                    .consumeWindowInsets(
                        PaddingValues(start = if (layoutDirection == LayoutDirection.Ltr)
                            with(density) { WindowInsets.navigationBars.getLeft(density, layoutDirection).toDp() }
                        else
                            with(density) { WindowInsets.navigationBars.getRight(density, layoutDirection).toDp() }
                        )
                    ) // consume the insets handled by the Rail
                    .padding(bottom = if (shouldShowNowPlayingBar) EXPANDED_SCREEN_NOW_PLAYING_HEIGHT else 0.dp)
            )

        }

        val navigationBarInsets = WindowInsets.navigationBars
        AnimatedVisibility(visible = appState.shouldShowNowPlayingScreen.collectAsState(initial = false).value) {
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
                        end = with(density) { navigationBarInsets.getRight(this, LayoutDirection.Ltr).toDp() },
                        start = with(density) { navigationBarInsets.getLeft(this, LayoutDirection.Ltr).toDp() }
                    )
                ,
                barHeight = EXPANDED_SCREEN_NOW_PLAYING_HEIGHT,
                isExpanded = nowPlayingScreenAnchors.currentValue == BarState.EXPANDED,
                onCollapseNowPlaying = { appState.coroutineScope.launch{ nowPlayingScreenAnchors.animateTo(BarState.COLLAPSED) } },
                onExpandNowPlaying = { appState.coroutineScope.launch{ nowPlayingScreenAnchors.animateTo(BarState.EXPANDED) } },
                progressProvider = { 1 - (nowPlayingScreenAnchors.requireOffset() / nowPlayingMinOffset)}
            )
        }

    }

}
