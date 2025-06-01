package com.omar.nowplaying.ui

import android.app.Activity
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FastForward
import androidx.compose.material.icons.outlined.FastRewind
import androidx.compose.material.icons.outlined.SkipNext
import androidx.compose.material.icons.outlined.SkipPrevious
import androidx.compose.material.icons.rounded.FastForward
import androidx.compose.material.icons.rounded.FastRewind
import androidx.compose.material.icons.rounded.PauseCircle
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material.icons.sharp.FastForward
import androidx.compose.material.icons.sharp.FastRewind
import androidx.compose.material.icons.sharp.PauseCircle
import androidx.compose.material.icons.sharp.PlayCircle
import androidx.compose.material.icons.sharp.SkipNext
import androidx.compose.material.icons.sharp.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.LinearGradient
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.omar.musica.ui.albumart.BlurTransformation
import com.omar.musica.ui.albumart.toSongAlbumArtModel
import com.omar.musica.ui.common.LocalUserPreferences
import com.omar.musica.ui.model.AppThemeUi
import com.omar.musica.ui.model.PlayerThemeUi
import com.omar.nowplaying.NowPlayingState
import com.omar.nowplaying.queue.QueueScreen
import com.omar.nowplaying.viewmodel.INowPlayingViewModel
import com.omar.nowplaying.viewmodel.NowPlayingViewModel


@Composable
fun NowPlayingScreen(
  modifier: Modifier,
  nowPlayingBarPadding: PaddingValues,
  barHeight: Dp,
  isExpanded: Boolean,
  onCollapseNowPlaying: () -> Unit,
  onExpandNowPlaying: () -> Unit,
  progressProvider: () -> Float,
  viewModel: NowPlayingViewModel = hiltViewModel()
) {

  val focusManager = LocalFocusManager.current
  LaunchedEffect(key1 = isExpanded) {
    if (isExpanded) {
      focusManager.clearFocus(true)
    }
  }

  if (isExpanded) {
    BackHandler(true) {
      onCollapseNowPlaying()
    }
  }

  val uiState by viewModel.state.collectAsState()

  if (uiState is NowPlayingState.Playing)
    NowPlayingScreen(
      modifier = modifier.clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
      nowPlayingBarPadding = nowPlayingBarPadding,
      uiState = uiState as NowPlayingState.Playing,
      barHeight = barHeight,
      isExpanded = isExpanded,
      onExpandNowPlaying = onExpandNowPlaying,
      progressProvider = progressProvider,
      nowPlayingActions = viewModel
    )
}

@Composable
internal fun NowPlayingScreen(
  modifier: Modifier,
  nowPlayingBarPadding: PaddingValues,
  uiState: NowPlayingState.Playing,
  barHeight: Dp,
  isExpanded: Boolean,
  onExpandNowPlaying: () -> Unit,
  progressProvider: () -> Float,
  nowPlayingActions: INowPlayingViewModel
) {

  val playerTheme = LocalUserPreferences.current.uiSettings.playerThemeUi
  val isDarkTheme = when (LocalUserPreferences.current.uiSettings.theme) {
    AppThemeUi.DARK -> true
    AppThemeUi.LIGHT -> false
    else -> isSystemInDarkTheme()
  }

  // Since we use a darker background image for the NowPlaying screen
  // we need to make the status bar icons lighter
  if (isExpanded && (isDarkTheme || playerTheme == PlayerThemeUi.BLUR))
    DarkStatusBarEffect()

  Surface(
    modifier = modifier, //if (MaterialTheme.colorScheme.background == Color.Black) 0.dp else 3.dp,
    tonalElevation = 3.dp
  ) {
    Box(modifier = Modifier.fillMaxSize()) {
      var isShowingQueue by remember {
        mutableStateOf(false)
      }
      NowPlayingMaterialTheme(playerThemeUi = playerTheme) {
        FullScreenNowPlaying(
          Modifier
            .fillMaxSize()
            .graphicsLayer {
              alpha = ((progressProvider() - 0.15f) * 2.0f).coerceIn(0.0f, 1.0f)
            },
          isShowingQueue,
          { isShowingQueue = false },
          { isShowingQueue = true },
          progressProvider,
          uiState,
          nowPlayingActions = nowPlayingActions
        )
      }
      LaunchedEffect(key1 = isExpanded) {
        if (!isExpanded) isShowingQueue = false
      }
      MiniPlayer(
        modifier = Modifier
          .fillMaxWidth()
          .height(barHeight)
          .padding(nowPlayingBarPadding)
          .pointerInput(Unit) {
            detectTapGestures { onExpandNowPlaying() }
          }
          .graphicsLayer {
            alpha = (1 - (progressProvider() * 6.66f).coerceAtMost(1.0f))
          },
        nowPlayingState = uiState,
        showExtraControls = LocalUserPreferences.current.uiSettings.showMiniPlayerExtraControls,
        songProgressProvider = nowPlayingActions::currentSongProgress,
        enabled = !isExpanded, // if the view is expanded then disable the header
        nowPlayingActions::togglePlayback,
        nowPlayingActions::nextSong,
        nowPlayingActions::previousSong
      )
    }
  }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun FullScreenNowPlaying(
  modifier: Modifier,
  isShowingQueue: Boolean,
  onCloseQueue: () -> Unit,
  onOpenQueue: () -> Unit,
  progressProvider: () -> Float,
  uiState: NowPlayingState.Playing,
  nowPlayingActions: INowPlayingViewModel,
) {
  val song = remember(uiState.song) {
    uiState.song
  }
  Box(
    modifier = modifier,
    contentAlignment = Alignment.Center
  ) {
    val playerTheme = LocalUserPreferences.current.uiSettings.playerThemeUi
    AnimatedVisibility(
      visible = playerTheme == PlayerThemeUi.BLUR,
      enter = fadeIn(), exit = fadeOut()
    ) {
      Box(modifier = Modifier.matchParentSize()) {
        CrossFadingAlbumArt(
          modifier = Modifier.fillMaxSize(),
          songAlbumArtModel = song.toSongAlbumArtModel(),
          errorPainterType = ErrorPainterType.SOLID_COLOR,
          blurTransformation = remember {
            BlurTransformation(
              radius = 25,
              scale = 0.2f
            )
          },
          colorFilter = remember {
            ColorFilter.tint(
              Color(0xFFCCCCCC),
              BlendMode.Multiply
            )
          }
        )
        val screenHeight = LocalContext.current.resources.displayMetrics.heightPixels
        Box(
          modifier = Modifier
            .matchParentSize()
            .background(
              Brush.verticalGradient(
                colors = listOf(Color.Transparent, Color.Black.copy(0.7f), Color.Black.copy(alpha = 1.0f)),
                startY = screenHeight * 0.2f,
                endY = Float.POSITIVE_INFINITY
              )
            )
        )
      }
    }

    val activity = LocalContext.current as Activity
    val windowSizeClass = calculateWindowSizeClass(activity = activity)
    val heightClass = windowSizeClass.heightSizeClass
    val widthClass = windowSizeClass.widthSizeClass

    val screenSize = when {
      heightClass == WindowHeightSizeClass.Compact && widthClass == WindowWidthSizeClass.Compact -> NowPlayingScreenSize.COMPACT
      heightClass == WindowHeightSizeClass.Compact && widthClass != WindowWidthSizeClass.Compact -> NowPlayingScreenSize.LANDSCAPE
      else -> NowPlayingScreenSize.PORTRAIT
    }
    val paddingModifier = remember(screenSize) {
      if (screenSize == NowPlayingScreenSize.LANDSCAPE)
        Modifier.padding(16.dp)
      else
        Modifier.padding(start = 16.dp, end = 16.dp, top = 32.dp)

    }
    val playerScreenModifier = remember(paddingModifier) {
      Modifier
        .fillMaxSize()
        .graphicsLayer {
          alpha = ((progressProvider() - 0.15f) * 2.0f).coerceIn(0.0f, 1.0f)
        }
        .then(paddingModifier)
        .statusBarsPadding()
    }
    AnimatedContent(
      modifier = Modifier.fillMaxSize(),
      targetState = isShowingQueue, label = "",
      transitionSpec = {
        if (this.targetState)
          fadeIn() togetherWith fadeOut()
        else
          scaleIn(initialScale = 1.2f) + fadeIn() togetherWith fadeOut()
      }
    ) {
      if (it) {
        QueueScreen(
          modifier = Modifier
            .fillMaxSize()
            .graphicsLayer { alpha = progressProvider() * 2 },
          onClose = onCloseQueue
        )
      } else {
        PlayingScreen2(
          modifier = playerScreenModifier.navigationBarsPadding(),
          song = song,
          isFavorite = uiState.isFavorite,
          playbackState = uiState.playbackState,
          repeatMode = uiState.repeatMode,
          isShuffleOn = uiState.isShuffleOn,
          screenSize = screenSize,
          nowPlayingActions = nowPlayingActions,
          onOpenQueue = onOpenQueue
        )
      }
    }
  }
}


@Composable
fun SongControls(
  modifier: Modifier,
  isPlaying: Boolean,
  playButtonColor: Color,
  onPrevious: () -> Unit,
  onTogglePlayback: () -> Unit,
  onNext: () -> Unit,
  onJumpForward: () -> Unit,
  onJumpBackward: () -> Unit
) {

  Row(
    modifier = modifier,
    horizontalArrangement = Arrangement.SpaceEvenly,
    verticalAlignment = Alignment.CenterVertically
  ) {

    ControlButton(
      modifier = Modifier
        .size(26.dp)
        .clip(RoundedCornerShape(4.dp)),
      icon = Icons.Outlined.SkipPrevious,
      contentDescription = "Skip Previous",
      onClick = onPrevious
    )
    //Spacer(modifier = Modifier.width(16.dp))
    ControlButton(
      modifier = Modifier
        .size(26.dp)
        .clip(RoundedCornerShape(16.dp)),
      icon = Icons.Outlined.FastRewind,
      contentDescription = "Jump Back",
      onClick = onJumpBackward
    )
    //Spacer(modifier = Modifier.width(16.dp))
    val pausePlayButton = remember(isPlaying) {
      if (isPlaying) Icons.Sharp.PauseCircle else Icons.Sharp.PlayCircle
    }
    ControlButton(
      modifier = Modifier
        .size(72.dp)
        .clip(CircleShape),
      icon = pausePlayButton,
      tint = playButtonColor,
      contentDescription = "Skip Previous",
      onClick = onTogglePlayback
    )
    //Spacer(modifier = Modifier.width(16.dp))
    ControlButton(
      modifier = Modifier
        .size(26.dp)
        .clip(RoundedCornerShape(4.dp)),
      icon = Icons.Outlined.FastForward,
      contentDescription = "Jump Forward",
      onClick = onJumpForward
    )

    //Spacer(modifier = Modifier.width(16.dp))

    ControlButton(
      modifier = Modifier
        .size(26.dp)
        .clip(RoundedCornerShape(4.dp)),
      icon = Icons.Outlined.SkipNext,
      contentDescription = "Skip To Next",
      onClick = onNext
    )
    LookaheadScope {

    }
  }
}

@Composable
fun ControlButton(
  modifier: Modifier,
  icon: ImageVector,
  tint: Color? = null,
  contentDescription: String? = null,
  onClick: () -> Unit
) {
  val iconModifier = remember {
    modifier.clickable { onClick() }
  }
  Icon(
    modifier = iconModifier,
    imageVector = icon,
    tint = tint ?: LocalContentColor.current,
    contentDescription = contentDescription
  )
}

enum class NowPlayingScreenSize {
  LANDSCAPE, PORTRAIT, COMPACT
}