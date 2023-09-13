package com.omar.nowplaying.ui

import BlurTransformation
import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.omar.musica.playback.state.PlayerState
import com.omar.musica.ui.albumart.LocalThumbnailImageLoader
import com.omar.musica.ui.common.SongAlbumArtImage
import com.omar.musica.ui.common.millisToTime
import com.omar.musica.ui.model.SongUi
import com.omar.nowplaying.NowPlayingState
import com.omar.nowplaying.viewmodel.NowPlayingViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import timber.log.Timber


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
            onUserSeek = viewModel::onUserSeek,
            onPrevious = viewModel::previousSong,
            onTogglePlayback = viewModel::togglePlayback,
            onNext = viewModel::nextSong,
            onJumpForward = viewModel::jumpForward,
            onJumpBackward = viewModel::jumpBackward
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
    onUserSeek: (Float) -> Unit,
    onPrevious: () -> Unit,
    onTogglePlayback: () -> Unit,
    onNext: () -> Unit,
    onJumpForward: () -> Unit,
    onJumpBackward: () -> Unit
) {

    // Since we use a darker background image for the NowPlaying screen
    // we need to make the status bar icons lighter
    DarkStatusBarEffect(isExpanded)

    Surface(
        modifier = modifier
    ) {

        CompositionLocalProvider(
            // since we darken the background color, use lighter text and icon color
            LocalContentColor provides Color(0xFFEEEEEE)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {

                FullScreenNowPlaying(
                    Modifier.fillMaxSize(),
                    progressProvider,
                    uiState,
                    onUserSeek,
                    onPrevious,
                    onTogglePlayback,
                    onNext,
                    onJumpForward,
                    onJumpBackward
                )

                NowPlayingBarHeader(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(barHeight)
                        .pointerInput(Unit) {
                            detectTapGestures { onExpandNowPlaying() }
                        }
                        .graphicsLayer { alpha = (1 - progressProvider() * 2) },
                    nowPlayingState = uiState,
                    enabled = !isExpanded, // if the view is expanded then disable the header
                    onTogglePlayback
                )

            }
        }
    }


}


@Composable
fun FullScreenNowPlaying(
    modifier: Modifier,
    progressProvider: () -> Float,
    uiState: NowPlayingState.Playing,
    onUserSeek: (Float) -> Unit,
    onPrevious: () -> Unit,
    onTogglePlayback: () -> Unit,
    onNext: () -> Unit,
    onJumpForward: () -> Unit,
    onJumpBackward: () -> Unit
) {

    val song = remember(uiState.song) {
        uiState.song
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            modifier = Modifier.fillMaxSize(),
            model = ImageRequest.Builder(LocalContext.current)
                .crossfade(500)
                .data(song)
                .transformations(
                    BlurTransformation(radius = 40, scale = 0.15f)
                ).build(),
            contentDescription = null,
            imageLoader = LocalThumbnailImageLoader.current,
            contentScale = ContentScale.Crop,
            onError = { Timber.e(it.result.throwable) },
            error = ColorPainter(Color.Black),
            colorFilter = ColorFilter.tint(
                Color(0xFF999999),
                BlendMode.Multiply
            )
        )


        NowPlayingUi(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { alpha = progressProvider() * 2 }
                .padding(start = 16.dp, end = 16.dp, top = 32.dp)
                .statusBarsPadding(),
            song = song,
            songProgress = uiState.songProgress,
            playbackState = uiState.playbackState,
            onUserSeek = onUserSeek,
            onPrevious = onPrevious,
            onTogglePlayback = onTogglePlayback,
            onNext = onNext,
            onJumpForward = onJumpForward,
            onJumpBackward = onJumpBackward
        )


    }
}

@Composable
fun NowPlayingUi(
    modifier: Modifier,
    song: SongUi,
    songProgress: Float,
    playbackState: PlayerState,
    onUserSeek: (Float) -> Unit,
    onPrevious: () -> Unit,
    onTogglePlayback: () -> Unit,
    onNext: () -> Unit,
    onJumpForward: () -> Unit,
    onJumpBackward: () -> Unit,
) {

    Column(modifier) {

        SongAlbumArtImage(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.0f)
                .scale(0.9f)
                .shadow(32.dp)
                .clip(RoundedCornerShape(12.dp)),
            song = song
        )

        Spacer(modifier = Modifier.height(16.dp))

        SongTextInfo(
            modifier = Modifier.fillMaxWidth(),
            song = song
        )

        Spacer(modifier = Modifier.height(16.dp))

        SongProgressInfo(
            modifier = Modifier.fillMaxWidth(),
            songDuration = song.length,
            progress = songProgress,
            onUserSeek = onUserSeek
        )

        Spacer(modifier = Modifier.height(32.dp))

        SongControls(
            modifier = Modifier.fillMaxWidth(),
            isPlaying = playbackState == PlayerState.PLAYING,
            onPrevious = onPrevious,
            onTogglePlayback = onTogglePlayback,
            onNext = onNext,
            onJumpForward = onJumpForward,
            onJumpBackward = onJumpBackward
        )

    }


}

@Composable
fun SongProgressInfo(
    modifier: Modifier,
    songDuration: Long,
    progress: Float,
    onUserSeek: (progress: Float) -> Unit
) {


    val songLength = remember(songDuration) {
        songDuration.millisToTime()
    }

    var userSetSliderValue by remember {
        mutableFloatStateOf(0.0f)
    }

    // When the user removes his finger from the slider,
    // the slider will return to the initial position it was on,
    // it is subtle but annoying, so we add a delay of 500ms
    // to give time for the player to change the position of the song.
    var useSongProgress by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(key1 = useSongProgress) {
        if (!useSongProgress) {
            delay(500)
            if (isActive) useSongProgress = true
        }
    }

    val sliderInteractionSource = remember { MutableInteractionSource() }
    val isPressed by sliderInteractionSource.collectIsDraggedAsState()

    val progressShown = remember(useSongProgress, isPressed, userSetSliderValue, progress) {
        if (useSongProgress && !isPressed) progress else userSetSliderValue
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
            onValueChangeFinished = { onUserSeek(userSetSliderValue); useSongProgress = false },
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

    Icon(
        modifier = modifier.clickable(onClick = onClick),
        imageVector = icon,
        contentDescription = contentDescription
    )

}


@Composable
fun DarkStatusBarEffect(darkStatusBarColor: Boolean) {
    val view = LocalView.current
    DisposableEffect(key1 = darkStatusBarColor) {

        val window = (view.context as Activity).window


        val windowsInsetsController = WindowCompat.getInsetsController(window, view)
        val previous = windowsInsetsController.isAppearanceLightStatusBars

        if (darkStatusBarColor) {
            windowsInsetsController.isAppearanceLightStatusBars = false
            windowsInsetsController.isAppearanceLightNavigationBars = false
        }

        onDispose {
            windowsInsetsController.isAppearanceLightStatusBars = previous
            windowsInsetsController.isAppearanceLightNavigationBars = previous
        }
    }
}