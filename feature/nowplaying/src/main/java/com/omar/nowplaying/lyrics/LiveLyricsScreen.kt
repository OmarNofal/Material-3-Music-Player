package com.omar.nowplaying.lyrics

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
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
    lyricsViewModel::saveExternalLyricsToSongFile
  )
}

@Composable
fun LiveLyricsScreen(
  modifier: Modifier,
  state: LyricsScreenState,
  songProgressMillis: () -> Long,
  onSeekToPositionMillis: (Long) -> Unit,
  onRetry: () -> Unit,
  onSaveLyricsToSongFile: () -> Unit
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
        songProgressMillis = songProgressMillis,
        onSaveLyricsToSongFile = onSaveLyricsToSongFile,
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
            context.shareText(line)
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
  songProgressMillis: () -> Long,
  onSaveLyricsToSongFile: () -> Unit,
) {
  var lyricIndex by remember(synchronizedLyrics) {
    mutableIntStateOf(-1)
  }
  val listState = rememberLazyListState()

  val density = LocalDensity.current
  val itemsSpacing = 12.dp
  val itemsSpacingPx = with(density) { itemsSpacing.toPx() }

  val coroutineScope = rememberCoroutineScope()
  var listHeightPx by remember {
    mutableIntStateOf(0)
  }
  var contextMenuShownIndex by remember {
    mutableIntStateOf(-1)
  }

  var actionsShown by remember {
    mutableStateOf(true)
  }

  val scrollListener = object : NestedScrollConnection {
    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
      if (available.y > 1.0)
        actionsShown = true
      return Offset.Zero
    }
  }

  LaunchedEffect(key1 = actionsShown) {
    delay(1000)
    if (actionsShown) actionsShown = false
  }

  LyricSynchronizerEffect(
    synchronizedLyrics = synchronizedLyrics,
    songProgressMillis = songProgressMillis
  ) {
    if (lyricIndex == it) return@LyricSynchronizerEffect
    lyricIndex = it
    if (contextMenuShownIndex == -1)
      coroutineScope.launch {
//        val visibleItems = listState.layoutInfo.visibleItemsInfo
//        val itemInfo = visibleItems.find { it.index == lyricIndex }
//        val shouldNotScroll = itemInfo != null && itemInfo.offset <= listHeightPx / 2.0f
//        if (!shouldNotScroll) {
//          listState.animateScrollToItem(it, -itemsSpacingPx.toInt())
//          actionsShown = false
//        }
        listState.animateScrollToItem(it, -itemsSpacingPx.toInt())
        actionsShown = false
      }
  }

  val vibrationManager = LocalHapticFeedback.current
  Box(modifier.onGloballyPositioned { listHeightPx = it.size.height }) {
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .nestedScroll(scrollListener),
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
    val clipboardManager = LocalClipboardManager.current
    LyricsActions(
      modifier = Modifier
        .fillMaxHeight()
        .align(Alignment.CenterEnd),
      isShown = actionsShown,
      lyricsFetchSource = lyricsFetchSource,
      onSaveToSongFile = {
      },
      onFetchWebVersion = { /*TODO*/ },
      onCopy = {
        clipboardManager.setText(AnnotatedString(synchronizedLyrics.constructStringForSharing()))
        actionsShown = false
      }
    )
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
      var index = synchronizedLyrics.segments.binarySearch { it.durationMillis - currentMillis.toInt() }
      if (index < 0) {
        index = (-(index + 1) - 1).coerceIn(0, synchronizedLyrics.segments.size - 1)
      }
      updateLambda.invoke(index)
      delay(300)
    }
  }
}


