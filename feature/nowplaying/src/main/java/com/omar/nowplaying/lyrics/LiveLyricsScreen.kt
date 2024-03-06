package com.omar.nowplaying.lyrics

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CopyAll
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.omar.musica.model.lyrics.LyricsFetchSource
import com.omar.musica.model.lyrics.PlainLyrics
import com.omar.musica.model.lyrics.SynchronizedLyrics
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch


@Composable
fun LiveLyricsScreen(
    modifier: Modifier,
    lyricsViewModel: LiveLyricsViewModel = hiltViewModel()
) {

    val state by lyricsViewModel.state.collectAsState()
    LiveLyricsScreen(
        modifier = modifier,
        state,
        lyricsViewModel::songProgressMillis,
        lyricsViewModel::setSongProgressMillis,
        lyricsViewModel::onRetry,
    )
}

@Composable
fun LiveLyricsScreen(
    modifier: Modifier,
    state: LyricsScreenState,
    songProgressMillis: () -> Long,
    onSeekToPositionMillis: (Long) -> Unit,
    onRetry: () -> Unit
) {
    when (state) {
        is LyricsScreenState.NoLyrics ->
            NoLyricsState(modifier = modifier, reason = state.reason, onRetry)

        is LyricsScreenState.Loading, is LyricsScreenState.SearchingLyrics ->
            LoadingState(modifier = modifier)

        is LyricsScreenState.NotPlaying ->
            NotPlayingState(modifier = modifier)

        is LyricsScreenState.TextLyrics ->
            PlainLyricsState(modifier = modifier, plainLyrics = state.plainLyrics)

        is LyricsScreenState.SyncedLyrics ->
            SyncedLyricsState(
                modifier = modifier,
                synchronizedLyrics = state.syncedLyrics,
                lyricsFetchSource = state.lyricsSource,
                onSeekToPositionMillis = onSeekToPositionMillis,
                songProgressMillis = songProgressMillis
            )
    }
}

@Composable
fun LyricLine(
    modifier: Modifier,
    line: String,
    isCurrentLine: Boolean = false,
    isShowingContextMenu: Boolean = false,
    onDismissContextMenu: () -> Unit = {}
) {

    val context = LocalContext.current
    val localClipboardManager = LocalClipboardManager.current
    Box(modifier = modifier) {
        if (isShowingContextMenu) {
            Popup(
                popupPositionProvider = ContextMenuPopupProvider(),
                onDismissRequest = onDismissContextMenu
            ) {
                LineContextMenu(
                    Modifier
                        .width(IntrinsicSize.Max)
                        .height(IntrinsicSize.Max),
                    onCopy = {
                        localClipboardManager.setText(AnnotatedString(line))
                        onDismissContextMenu()
                    },
                    onShare = {
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, line)
                        }
                        context.startActivity(Intent.createChooser(intent, "Share lyrics"))
                        onDismissContextMenu()
                    }
                )
            }
        }
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    alpha = if (isCurrentLine || isShowingContextMenu) 1.0f else 0.35f
                }
                .then(if (isShowingContextMenu) Modifier.shimmerLoadingAnimation() else Modifier),
            text = line,
            fontSize = 25.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

fun Modifier.fadingEdge(brush: Brush) =
    this
        .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
        .drawWithContent {
            drawContent()
            drawRect(brush = brush, blendMode = BlendMode.DstIn)
        }


@Composable
fun NoLyricsState(
    modifier: Modifier,
    reason: NoLyricsReason,
    onRetry: () -> Unit,
) {
    when (reason) {
        NoLyricsReason.NOT_FOUND -> {
            Box(modifier = modifier) {
                Text(modifier = Modifier.align(Alignment.Center), text = "No lyrics available")
            }
        }

        NoLyricsReason.NETWORK_ERROR -> {
            Column(
                modifier = modifier,
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Check your network connection")
                Spacer(modifier = Modifier.height(4.dp))
                Button(onClick = onRetry) {
                    Text(text = "Try Again")
                }
            }
        }
    }
}

@Composable
fun LoadingState(
    modifier: Modifier
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun NotPlayingState(
    modifier: Modifier
) {
    Box(modifier = modifier) {
        Text(modifier = Modifier.align(Alignment.Center), text = "No song is being played.")
    }
}

@Composable
fun PlainLyricsState(
    modifier: Modifier,
    plainLyrics: PlainLyrics
) {
    val itemsSpacing = 12.dp

    var contextMenuShownIndex by remember {
        mutableStateOf(-1)
    }

    val vibrationManager = LocalHapticFeedback.current
    LazyColumn(
        modifier
    ) {
        itemsIndexed(plainLyrics.lines) { index, s ->
            LyricLine(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onLongPress = {
                                contextMenuShownIndex = index
                                vibrationManager.performHapticFeedback(HapticFeedbackType.LongPress)
                            }) { }
                    },
                line = s,
                isCurrentLine = true,
                isShowingContextMenu = index == contextMenuShownIndex,
                onDismissContextMenu = { contextMenuShownIndex = -1 }
            )
            Spacer(modifier = Modifier.height(itemsSpacing))
        }
    }
}

@Composable
fun SyncedLyricsState(
    modifier: Modifier,
    synchronizedLyrics: SynchronizedLyrics,
    lyricsFetchSource: LyricsFetchSource,
    onSeekToPositionMillis: (Long) -> Unit,
    songProgressMillis: () -> Long
) {

    var lyricIndex by remember(synchronizedLyrics) {
        mutableStateOf(-1)
    }

    val listState = rememberLazyListState()

    val density = LocalDensity.current
    val itemsSpacing = 12.dp
    val itemsSpacingPx = with(density) { itemsSpacing.toPx() }

    val coroutineScope = rememberCoroutineScope()
    var listHeightPx by remember {
        mutableStateOf(0)
    }
    var contextMenuShownIndex by remember {
        mutableStateOf(-1)
    }

    var actionsShown by remember {
        mutableStateOf(true)
    }

    LyricSynchronizerEffect(
        synchronizedLyrics = synchronizedLyrics,
        songProgressMillis = songProgressMillis
    ) {
        if (lyricIndex == it) return@LyricSynchronizerEffect
        lyricIndex = it
        if (contextMenuShownIndex == -1)
            coroutineScope.launch {
                val visibleItems = listState.layoutInfo.visibleItemsInfo
                val itemInfo = visibleItems.find { it.index == lyricIndex }
                val shouldNotScroll = itemInfo != null && itemInfo.offset <= listHeightPx / 2.0f
                if (!shouldNotScroll) {
                    listState.animateScrollToItem(it, -itemsSpacingPx.toInt())
                    actionsShown = false
                }
            }
    }

    val vibrationManager = LocalHapticFeedback.current

    Box(modifier.onGloballyPositioned { listHeightPx = it.size.height }) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            state = listState
        ) {

            itemsIndexed(synchronizedLyrics.segments) { index, segment ->
                LyricLine(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onPress = { actionsShown = true },
                                onLongPress = {
                                    contextMenuShownIndex = index
                                    vibrationManager.performHapticFeedback(HapticFeedbackType.LongPress)
                                }
                            ) {
                                onSeekToPositionMillis(segment.durationMillis.toLong())
                            }
                        },
                    line = segment.text,
                    isCurrentLine = index == lyricIndex && contextMenuShownIndex == -1,
                    isShowingContextMenu = index == contextMenuShownIndex,
                    onDismissContextMenu = { contextMenuShownIndex = -1 }
                )

                Spacer(modifier = Modifier.height(itemsSpacing))
            }

        }
        LyricsActions(
            modifier = Modifier
                .fillMaxHeight()
                .align(Alignment.CenterEnd),
            isShown = actionsShown,
            lyricsFetchSource = lyricsFetchSource,
            onSaveToSongFile = { /*TODO*/ },
            onFetchWebVersion = { /*TODO*/ }) {
        }
    }
}

@Composable
fun LyricSynchronizerEffect(
    synchronizedLyrics: SynchronizedLyrics,
    songProgressMillis: () -> Long,
    onLyricsIndexCalculated: suspend (Int) -> Unit,
) {
    val updateLambda by rememberUpdatedState(newValue = onLyricsIndexCalculated)
    LaunchedEffect(synchronizedLyrics) {
        while (isActive) {
            val currentMillis = songProgressMillis()
            var index =
                synchronizedLyrics.segments.binarySearch { it.durationMillis - currentMillis.toInt() }
            if (index < 0) {
                index = (-(index + 1) - 1).coerceIn(0, synchronizedLyrics.segments.size - 1)
            }
            updateLambda.invoke(index)
            delay(200)
        }
    }
}


fun Modifier.shimmerLoadingAnimation(
    widthOfShadowBrush: Int = 600,
    angleOfAxisY: Float = 270f,
    durationMillis: Int = 800,
): Modifier {
    return composed {

        val shimmerColors = listOf(
            Color.White.copy(alpha = 0.3f),
            Color.White.copy(alpha = 0.5f),
            Color.White.copy(alpha = 1.0f),
            Color.White.copy(alpha = 0.5f),
            Color.White.copy(alpha = 0.3f),
        )

        val transition = rememberInfiniteTransition(label = "")

        val translateAnimation = transition.animateFloat(
            initialValue = 0f,
            targetValue = (durationMillis + widthOfShadowBrush).toFloat(),
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = durationMillis,
                    easing = LinearEasing,
                ),
                repeatMode = RepeatMode.Restart,
            ),
            label = "Shimmer loading animation",
        )

        this.drawWithContent {
            drawContent()
            drawRect(
                Brush.linearGradient(
                    colors = shimmerColors,
                    start = Offset(x = translateAnimation.value - widthOfShadowBrush, y = 0.0f),
                    end = Offset(x = translateAnimation.value, y = angleOfAxisY),
                ), blendMode = BlendMode.DstIn
            )
        }
    }
}

class ContextMenuPopupProvider : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize
    ): IntOffset {
        val popupHeight = popupContentSize.height

        val availableHeight = anchorBounds.topLeft.y

        return if (availableHeight >= popupHeight + 40) return IntOffset(
            anchorBounds.topLeft.x,
            anchorBounds.topLeft.y - popupHeight
        )
        else IntOffset(anchorBounds.topLeft.x, anchorBounds.bottomLeft.y + popupHeight)
    }
}


@Composable
fun LineContextMenu(
    modifier: Modifier,
    onCopy: () -> Unit,
    onShare: () -> Unit
) {

    AnimatedVisibility(
        visible = true, enter = fadeIn(), exit = fadeOut()
    ) {
        Row(
            modifier
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.secondaryContainer),
            verticalAlignment = Alignment.CenterVertically,

            ) {
            Text(
                text = "Copy",
                modifier = Modifier
                    .clickable { onCopy() }
                    .padding(start = 12.dp, top = 12.dp, bottom = 12.dp, end = 8.dp)
            )
            VerticalDivider(color = MaterialTheme.colorScheme.onSecondaryContainer)
            Text(text = "Share",
                modifier = Modifier
                    .clickable { onShare() }
                    .padding(start = 12.dp, top = 12.dp, bottom = 12.dp, end = 8.dp)
            )
        }
    }
}

/**
 * Shows actions which can be done to the lyrics depending on whether the lyrics
 * are fetched from API or from the song metadata
 */
@Composable
fun LyricsActions(
    modifier: Modifier,
    isShown: Boolean,
    lyricsFetchSource: LyricsFetchSource,
    onSaveToSongFile: () -> Unit,
    onFetchWebVersion: () -> Unit,
    onCopy: () -> Unit,
) {
    AnimatedVisibility(modifier = modifier, visible = isShown, enter = fadeIn(), exit = fadeOut()) {
        Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.End) {

            IconButton(
                onClick =
                if (lyricsFetchSource == LyricsFetchSource.FROM_INTERNET)
                    onSaveToSongFile else
                    onFetchWebVersion
            ) {
                val icon =
                    if (lyricsFetchSource == LyricsFetchSource.FROM_INTERNET)
                        Icons.Rounded.Save
                    else
                        Icons.Rounded.Language
                Icon(imageVector = icon, contentDescription = null)
            }

            Spacer(modifier = Modifier.height(6.dp))

            IconButton(
                onClick = onCopy
            ) {
                Icon(imageVector = Icons.Rounded.CopyAll, contentDescription = null)
            }

        }
    }
}