package com.omar.musica.ui.compact

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import com.omar.musica.navigation.MusicaBottomNavBar
import com.omar.musica.navigation.TopLevelDestination
import com.omar.musica.state.MusicaAppState
import com.omar.musica.ui.ViewNowPlayingScreenListenerEffect
import com.omar.musica.ui.calculateBottomPaddingForContent
import com.omar.musica.ui.update
import com.omar.nowplaying.ui.BarState
import com.omar.nowplaying.ui.NowPlayingScreen
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


private val COMPACT_NOW_PLAYING_BAR_HEIGHT = 68.dp

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
  val shouldShowNowPlayingBar by appState.shouldShowNowPlayingScreen.collectAsState(initial = false)
  val nowPlayingBarHeightPx = with(density) { COMPACT_NOW_PLAYING_BAR_HEIGHT.toPx() }
  val shouldShowBottomBar by appState.shouldShowBottomBar.collectAsState(initial = false)

  var layoutHeightPx = remember { 0 }
  val bottomNavBarHeightPx =
    with(density) { 80.dp.toPx() }

  var nowPlayingBarMinOffset by remember {
    mutableIntStateOf(0)
  }

  val scrollProvider = { 1 - (appState.nowPlayingScreenOffset() / nowPlayingBarMinOffset) }

  val contentModifier = remember { mutableStateOf<Modifier>(Modifier) }

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

  val uiState = rememberCompactScreenUiState(
    screenHeightPx = layoutHeightPx,
    nowPlayingAnchors = nowPlayingScreenAnchors,
    scrollProvider = scrollProvider,
    bottomBarHeightPx = bottomNavBarHeightPx.toInt(),
    density = density,
    isPinnedMode = false,
    isNowPlayingVisible = shouldShowNowPlayingBar,
    showBottomBar = shouldShowBottomBar
  )
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
        tween(600),
        initialOffsetY = { nowPlayingBarHeightPx.roundToInt() * 2 }),
      exit = slideOutVertically(
        tween(600),
        targetOffsetY = { -nowPlayingBarHeightPx.roundToInt() })
    ) {
      NowPlayingScreen(
        barHeight = COMPACT_NOW_PLAYING_BAR_HEIGHT,
        nowPlayingBarPadding = PaddingValues(0.dp),
        modifier = Modifier
          .fillMaxSize()
          .offset {
            uiState.getNowPlayingOffset()
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
        .graphicsLayer { alpha = uiState.bottomBarAlpha }
        .offset {
          uiState.getBottomBarOffset()
        },
      topLevelDestinations = topLevelDestinations,
      currentDestination = currentDestination,
      onDestinationSelected = onDestinationSelected
    )

    /*val nowPlayingViewModel = hiltViewModel<NowPlayingViewModel>()
    FloatingMiniPlayer(
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(bottom = 128.dp, end = 16.dp)
            .fillMaxWidth(0.6f)
            .height(58.dp)
            .clip(RoundedCornerShape(12.dp)),
        nowPlayingState = nowPlayingViewModel.state.collectAsState().value,
        showExtraControls = true,
        songProgressProvider = { 0.5f },
        enabled = true,
        onTogglePlayback = { *//*TODO*//* },
            onNext = { *//*TODO*//* }) {
        }*/

    ViewNowPlayingScreenListenerEffect(
      navController = appState.navHostController,
      onViewNowPlayingScreen = {
        appState.coroutineScope.launch {
          nowPlayingScreenAnchors.animateTo(BarState.EXPANDED)
        }
      }
    )
  }
}