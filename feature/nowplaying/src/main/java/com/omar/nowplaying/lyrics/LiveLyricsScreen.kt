package com.omar.nowplaying.lyrics

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.omar.musica.model.lyrics.PlainLyrics
import com.omar.musica.model.lyrics.SynchronizedLyrics
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive


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
        lyricsViewModel::setSongProgressMillis
    )
}

@Composable
fun LiveLyricsScreen(
    modifier: Modifier,
    state: LyricsScreenState,
    songProgressMillis: () -> Long,
    onSeekToPositionMillis: (Long) -> Unit,
) {
    when (state) {
        is LyricsScreenState.NoLyrics ->
            NoLyricsState(modifier = modifier, reason = state.reason, {})

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
                onSeekToPositionMillis = onSeekToPositionMillis,
                songProgressMillis = songProgressMillis
            )
    }
}

@Composable
fun LyricLine(
    modifier: Modifier,
    line: String,
    isCurrentLine: Boolean
) {
    Text(
        modifier = modifier
            .graphicsLayer {
                alpha = if (isCurrentLine) 1.0f else 0.35f
            },
        text = line,
        fontSize = 26.sp,
        fontWeight = FontWeight.ExtraBold
    )
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

    LazyColumn(
        modifier
    ) {
        item {
            LyricLine(
                modifier = Modifier
                    .fillMaxWidth(0.9f),
                line = plainLyrics.text,
                isCurrentLine = true
            )
            Spacer(modifier = Modifier.height(itemsSpacing))
        }
    }
}

@Composable
fun SyncedLyricsState(
    modifier: Modifier,
    synchronizedLyrics: SynchronizedLyrics,
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



    LyricSynchronizerEffect(
        synchronizedLyrics = synchronizedLyrics,
        songProgressMillis = songProgressMillis
    ) {
        if (lyricIndex == it) return@LyricSynchronizerEffect
        lyricIndex = it
        listState.animateScrollToItem(it, -itemsSpacingPx.toInt())
    }

    LazyColumn(modifier = modifier, state = listState) {

        itemsIndexed(synchronizedLyrics.segments) { index, segment ->
            LyricLine(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .pointerInput(Unit) {
                        detectTapGestures {
                            onSeekToPositionMillis(segment.durationMillis.toLong())
                        }
                    },
                line = segment.text,
                isCurrentLine = index == lyricIndex
            )

            Spacer(modifier = Modifier.height(itemsSpacing))
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