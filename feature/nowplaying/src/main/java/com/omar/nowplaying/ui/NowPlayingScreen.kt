package com.omar.nowplaying.ui

import com.omar.musica.ui.albumart.BlurTransformation
import android.app.Activity
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FastForward
import androidx.compose.material.icons.rounded.FastRewind
import androidx.compose.material.icons.rounded.PauseCircle
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material.icons.rounded.QueueMusic
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.omar.musica.playback.state.PlayerState
import com.omar.musica.ui.albumart.LocalInefficientThumbnailImageLoader
import com.omar.musica.ui.common.LocalUserPreferences
import com.omar.musica.ui.millisToTime
import com.omar.musica.ui.model.AppThemeUi
import com.omar.musica.ui.model.PlayerThemeUi
import com.omar.musica.ui.model.SongUi
import com.omar.musica.ui.theme.DarkColorScheme
import com.omar.nowplaying.NowPlayingState
import com.omar.nowplaying.queue.QueueScreen
import com.omar.nowplaying.viewmodel.INowPlayingViewModel
import com.omar.nowplaying.viewmodel.NowPlayingViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive


@Composable
fun NowPlayingScreen(
    modifier: Modifier,
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

    BackHandler(isExpanded) {
        onCollapseNowPlaying()
    }

    val uiState by viewModel.state.collectAsState()


    if (uiState is NowPlayingState.Playing)
        NowPlayingScreen(
            modifier = modifier,
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
    uiState: NowPlayingState.Playing,
    barHeight: Dp,
    isExpanded: Boolean,
    onExpandNowPlaying: () -> Unit,
    progressProvider: () -> Float,
    nowPlayingActions: INowPlayingViewModel
) {

    val playerTheme = LocalUserPreferences.current.uiSettings.playerThemeUi
    val shouldUseDynamicColor =
        LocalUserPreferences.current.uiSettings.isUsingDynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val isDarkTheme = when (LocalUserPreferences.current.uiSettings.theme) {
        AppThemeUi.DARK -> true
        AppThemeUi.LIGHT -> false
        else -> isSystemInDarkTheme()
    }

    // Since we use a darker background image for the NowPlaying screen
    // we need to make the status bar icons lighter
    if (isExpanded && (isDarkTheme || playerTheme == PlayerThemeUi.BLUR))
        DarkStatusBarEffect()


    // We use another material theme here to force dark theme colors when the player theme is
    // set to BLUR.
    MaterialTheme(
        typography = MaterialTheme.typography,
        colorScheme = if (playerTheme == PlayerThemeUi.BLUR) {
            if (shouldUseDynamicColor) dynamicDarkColorScheme(LocalContext.current) else DarkColorScheme
        } else MaterialTheme.colorScheme
    ) {

        Surface(
            modifier = modifier,
            tonalElevation = if (MaterialTheme.colorScheme.background == Color.Black) 0.dp else 4.dp
        ) {
            Box(modifier = Modifier.fillMaxSize()) {

                var isShowingQueue by remember {
                    mutableStateOf(false)
                }

                FullScreenNowPlaying(
                    Modifier.fillMaxSize(),
                    isShowingQueue,
                    { isShowingQueue = false },
                    { isShowingQueue = true },
                    progressProvider,
                    uiState,
                    nowPlayingActions = nowPlayingActions
                )
                LaunchedEffect(key1 = isExpanded) {
                    if (!isExpanded) isShowingQueue = false
                }
                NowPlayingBarHeader(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(barHeight)
                        .pointerInput(Unit) {
                            detectTapGestures { onExpandNowPlaying() }
                        }
                        .graphicsLayer { alpha = (1 - progressProvider() * 2) },
                    nowPlayingState = uiState,
                    songProgressProvider = nowPlayingActions::currentSongProgress,
                    enabled = !isExpanded, // if the view is expanded then disable the header
                    nowPlayingActions::togglePlayback
                )

            }
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
            CrossFadingAlbumArt(
                modifier = Modifier.fillMaxSize(),
                song = song,
                errorPainterType = ErrorPainterType.SOLID_COLOR,
                blurTransformation = remember { BlurTransformation(radius = 40, scale = 0.15f) },
                colorFilter = remember {
                    ColorFilter.tint(
                        Color(0xFF999999),
                        BlendMode.Multiply
                    )
                }
            )
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
                .graphicsLayer { alpha = progressProvider() * 2 }
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

                PlayerScreen(
                    modifier = playerScreenModifier,
                    song = song,
                    playbackState = uiState.playbackState,
                    screenSize = screenSize,
                    nowPlayingActions = nowPlayingActions,
                    onOpenQueue = onOpenQueue
                )
            }
        }


    }
}


@Composable
fun PlayerScreenSkeleton(
    song: SongUi,
    playbackState: PlayerState,
    screenSize: NowPlayingScreenSize,
    nowPlayingActions: INowPlayingViewModel,
    onOpenQueue: () -> Unit,
) {
    val initialModifier = remember(screenSize) {
        if (screenSize == NowPlayingScreenSize.LANDSCAPE) Modifier.fillMaxHeight() else Modifier.fillMaxWidth()
    }

    if (screenSize != NowPlayingScreenSize.COMPACT)
        CrossFadingAlbumArt(
            modifier = initialModifier
                .aspectRatio(1.0f)
                .scale(0.9f)
                .clip(RoundedCornerShape(12.dp))
                .shadow(32.dp),
            song = song,
            errorPainterType = ErrorPainterType.PLACEHOLDER
        )

    Spacer(
        modifier = if (screenSize == NowPlayingScreenSize.LANDSCAPE) Modifier.width(16.dp) else Modifier.height(
            16.dp
        )
    )

    Column {
        SongTextInfo(
            modifier = Modifier.fillMaxWidth(),
            song = song
        )

        Spacer(modifier = Modifier.height(16.dp))

        SongProgressInfo(
            modifier = Modifier.fillMaxWidth(),
            songDuration = song.length,
            songProgressProvider = nowPlayingActions::currentSongProgress,
            onUserSeek = nowPlayingActions::onUserSeek
        )

        Spacer(modifier = Modifier.height(32.dp))

        SongControls(
            modifier = Modifier.fillMaxWidth(),
            isPlaying = playbackState == PlayerState.PLAYING,
            onPrevious = nowPlayingActions::previousSong,
            onTogglePlayback = nowPlayingActions::togglePlayback,
            onNext = nowPlayingActions::nextSong,
            onJumpForward = nowPlayingActions::jumpForward,
            onJumpBackward = nowPlayingActions::jumpBackward
        )
        Spacer(modifier = Modifier.height(32.dp))

        TextButton(
            onClick = onOpenQueue,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Icon(imageVector = Icons.Rounded.QueueMusic, contentDescription = "Queue")
            Text(text = "Queue")
        }
    }
}

@Composable
fun PlayerScreen(
    modifier: Modifier,
    song: SongUi,
    playbackState: PlayerState,
    screenSize: NowPlayingScreenSize,
    nowPlayingActions: INowPlayingViewModel,
    onOpenQueue: () -> Unit,
) {
    if (screenSize == NowPlayingScreenSize.LANDSCAPE)
        Row(modifier, verticalAlignment = Alignment.CenterVertically) {
            PlayerScreenSkeleton(
                song,
                playbackState,
                screenSize,
                nowPlayingActions,
                onOpenQueue
            )
        } else
        Column(modifier) {
            PlayerScreenSkeleton(
                song,
                playbackState,
                screenSize,
                nowPlayingActions,
                onOpenQueue
            )
        }
}

@Composable
fun SongProgressInfo(
    modifier: Modifier,
    songDuration: Long,
    songProgressProvider: () -> Float,
    onUserSeek: (progress: Float) -> Unit
) {


    var currentProgress by remember {
        mutableFloatStateOf(0.0f)
    }

    // Periodically get the progress
    LaunchedEffect(key1 = Unit) {
        while (isActive) {
            currentProgress = songProgressProvider()
            delay(500)
        }
    }

    val songLength = remember(songDuration) {
        songDuration.millisToTime()
    }

    var userSetSliderValue by remember {
        mutableFloatStateOf(0.0f)
    }

    // When the user removes his finger from the slider,
    // the slider will return to the initial position it was on,
    // it is subtle but annoying, so we add a delay
    // to give time for the player to change the position of the song.
    var useSongProgress by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(key1 = useSongProgress) {
        if (!useSongProgress) {
            delay(1000)
            if (isActive) useSongProgress = true
        }
    }

    val sliderInteractionSource = remember { MutableInteractionSource() }
    val isPressed by sliderInteractionSource.collectIsDraggedAsState()

    val progressShown =
        remember(useSongProgress, isPressed, userSetSliderValue, currentProgress) {
            if (useSongProgress && !isPressed) currentProgress else userSetSliderValue
        }

    val timestampShown = remember(songDuration, progressShown) {
        (songDuration * progressShown).toLong().millisToTime()
    }

    Column(modifier) {

        Slider(
            value = progressShown,
            onValueChange = { userSetSliderValue = it },
            modifier = Modifier
                .fillMaxWidth(),
            enabled = true,
            onValueChangeFinished = {
                onUserSeek(userSetSliderValue); useSongProgress = false
            },
            interactionSource = sliderInteractionSource
        )

        Row(
            Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = timestampShown,
                fontSize = 10.sp,
                maxLines = 1,
                fontWeight = FontWeight.Light
            )

            Text(
                text = songLength,
                fontSize = 10.sp,
                maxLines = 1,
                fontWeight = FontWeight.Light
            )

        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SongTextInfo(
    modifier: Modifier,
    song: SongUi
) {


    Column(modifier = modifier) {

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .basicMarquee(
                    delayMillis = 2000
                ),
            text = song.title,

            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            fontSize = 22.sp,
            maxLines = 1
        )


        Spacer(modifier = Modifier.height(4.dp))


        Text(
            modifier = Modifier.fillMaxWidth(),
            text = song.artist ?: "<unknown>",
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center,
            fontSize = 14.sp,
            maxLines = 1
        )


        Spacer(modifier = Modifier.height(4.dp))

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = song.album ?: "<unknown>",
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center,
            fontSize = 12.sp,
            maxLines = 1
        )

    }


}


@Composable
fun SongControls(
    modifier: Modifier,
    isPlaying: Boolean,
    onPrevious: () -> Unit,
    onTogglePlayback: () -> Unit,
    onNext: () -> Unit,
    onJumpForward: () -> Unit,
    onJumpBackward: () -> Unit
) {

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {

        ControlButton(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(4.dp)),
            icon = Icons.Rounded.SkipPrevious,
            "Skip Previous",
            onPrevious
        )

        Spacer(modifier = Modifier.width(8.dp))

        ControlButton(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(4.dp)),
            icon = Icons.Rounded.FastRewind,
            "Jump Back",
            onJumpBackward
        )

        Spacer(modifier = Modifier.width(16.dp))

        val pausePlayButton = remember(isPlaying) {
            if (isPlaying) Icons.Rounded.PauseCircle else Icons.Rounded.PlayCircle
        }

        ControlButton(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape),
            icon = pausePlayButton,
            "Skip Previous",
            onTogglePlayback
        )

        Spacer(modifier = Modifier.width(16.dp))

        ControlButton(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(4.dp)),
            icon = Icons.Rounded.FastForward,
            "Jump Forward",
            onJumpForward
        )

        Spacer(modifier = Modifier.width(8.dp))

        ControlButton(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(4.dp)),
            icon = Icons.Rounded.SkipNext,
            "Skip To Next",
            onNext
        )


    }


}

@Composable
fun ControlButton(
    modifier: Modifier,
    icon: ImageVector,
    contentDescription: String? = null,
    onClick: () -> Unit
) {
    val iconModifier = remember {
        modifier.clickable { onClick() }
    }
    Icon(
        modifier = iconModifier,
        imageVector = icon,
        contentDescription = contentDescription
    )

}


@Composable
fun DarkStatusBarEffect() {
    val view = LocalView.current
    DisposableEffect(Unit) {

        val window = (view.context as Activity).window


        val windowsInsetsController = WindowCompat.getInsetsController(window, view)
        val previous = windowsInsetsController.isAppearanceLightStatusBars


        windowsInsetsController.isAppearanceLightStatusBars = false
        windowsInsetsController.isAppearanceLightNavigationBars = false

        onDispose {
            windowsInsetsController.isAppearanceLightStatusBars = previous
            windowsInsetsController.isAppearanceLightNavigationBars = previous
        }
    }
}


enum class ErrorPainterType {
    PLACEHOLDER, SOLID_COLOR
}

@Composable
fun CrossFadingAlbumArt(
    modifier: Modifier,
    song: SongUi,
    errorPainterType: ErrorPainterType,
    colorFilter: ColorFilter? = null,
    blurTransformation: BlurTransformation? = null,
    contentScale: ContentScale = ContentScale.Crop
) {


    val context = LocalContext.current
    val imageRequest = remember(song.uriString) {
        ImageRequest.Builder(context)
            .data(song)
            .apply { if (blurTransformation != null) this.transformations(blurTransformation) }
            .size(Size.ORIGINAL).build()
    }

    var firstPainter by remember {
        mutableStateOf<Painter>(ColorPainter(Color.Black))
    }

    var secondPainter by remember {
        mutableStateOf<Painter>(ColorPainter(Color.Black))
    }

    var isUsingFirstPainter by remember {
        mutableStateOf(true)
    }

    val solidColorPainter = remember { ColorPainter(Color.Black) }
    val placeholderPainter = painterResource(id = com.omar.musica.ui.R.drawable.placeholder)

    rememberAsyncImagePainter(
        model = imageRequest,
        contentScale = ContentScale.Crop,
        imageLoader = LocalInefficientThumbnailImageLoader.current,
        onState = {
            when (it) {
                is AsyncImagePainter.State.Success -> {
                    val newPainter = it.painter
                    if (isUsingFirstPainter) {
                        secondPainter = newPainter
                    } else {
                        firstPainter = newPainter
                    }
                    isUsingFirstPainter = !isUsingFirstPainter
                }

                is AsyncImagePainter.State.Error -> {
                    if (isUsingFirstPainter) {
                        secondPainter =
                            if (errorPainterType == ErrorPainterType.PLACEHOLDER) placeholderPainter
                            else solidColorPainter
                    } else {
                        firstPainter =
                            if (errorPainterType == ErrorPainterType.PLACEHOLDER) placeholderPainter
                            else solidColorPainter
                    }
                    isUsingFirstPainter = !isUsingFirstPainter
                }

                else -> {

                }
            }
        }
    )

    Crossfade(targetState = isUsingFirstPainter, label = "") {
        Image(
            modifier = modifier,
            painter = if (it) firstPainter else secondPainter,
            contentDescription = null,
            colorFilter = colorFilter,
            contentScale = contentScale
        )
    }
}

enum class NowPlayingScreenSize {
    LANDSCAPE, PORTRAIT, COMPACT
}