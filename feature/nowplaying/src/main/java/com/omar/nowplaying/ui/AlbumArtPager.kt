package com.omar.nowplaying.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.snapping.SnapFlingBehavior
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.omar.musica.store.model.song.Song
import com.omar.musica.ui.albumart.toSongAlbumArtModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlin.math.abs


@OptIn(ExperimentalFoundationApi::class)
val LocalPagerState = compositionLocalOf<PagerState> { throw IllegalStateException() }

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AlbumArtPager(
    modifier: Modifier,
    songs: List<Song>,
    currentSongIndex: Int,
    onSongSwitched: (Int) -> Unit
) {
    val pagerState = LocalPagerState.current
    var lastReportedPage by remember { mutableIntStateOf(pagerState.targetPage) }

    var isDragging by remember { mutableStateOf(false) }



    LaunchedEffect(Unit) {
        pagerState.interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is DragInteraction.Start -> {
                    isDragging = true
                }

                is DragInteraction.Stop, is DragInteraction.Cancel -> {
                    isDragging = false
                }
            }
        }
    }

    // 🔄 Respond immediately to swipe changes (while dragging)
    LaunchedEffect(pagerState.targetPage, isDragging) {
        if (lastReportedPage != pagerState.targetPage) {
            lastReportedPage = pagerState.targetPage
            if (!isDragging)
                onSongSwitched(pagerState.targetPage)
        }
    }
//    LaunchedEffect(pagerState) {
//        snapshotFlow { pagerState.targetPage }
//            .distinctUntilChanged()
//            .collect { page ->
//                if (lastReportedPage != page) {
//                    lastReportedPage = page
//                    if (!isDragging)
//                        onSongSwitched(page)
//                }
//            }
//    }

    HorizontalPager(
        state = pagerState,
        modifier = modifier,
        key = { songs[it].uri }, // optional, improves performance
        contentPadding = PaddingValues(horizontal = 0.dp),
        beyondBoundsPageCount = 1,
    ) { index ->
        val song = songs[index]
        NowPlayingSquareAlbumArt(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp)
                .aspectRatio(1f)
                .shadow(4.dp, shape = RoundedCornerShape(10.dp), clip = true)
                .clip(RoundedCornerShape(10.dp)),
            song = song.toSongAlbumArtModel()
        )
    }
}
