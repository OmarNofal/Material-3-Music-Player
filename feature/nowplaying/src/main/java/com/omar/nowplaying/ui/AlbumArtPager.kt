package com.omar.nowplaying.ui

import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.omar.musica.store.model.song.Song
import com.omar.musica.ui.albumart.toSongAlbumArtModel


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AlbumArtPager(
    modifier: Modifier,
    songs: List<Song>,
    currentSongIndex: Int,
    onSongSwitched: (Int) -> Unit
) {

    val pagerState = rememberPagerState(currentSongIndex) { songs.size }
    HorizontalPager(
        state = pagerState,
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 0.dp),
        beyondBoundsPageCount = 1
    ) { index ->
        val song = songs[index]
        CrossFadingAlbumArt(
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(1f)
                .shadow(4.dp, shape = RoundedCornerShape(10.dp), clip = true)
                .clip(RoundedCornerShape(10.dp)),
            containerModifier = Modifier.padding(horizontal = 14.dp),
            songAlbumArtModel = song.toSongAlbumArtModel(),
            errorPainterType = ErrorPainterType.PLACEHOLDER
        )
    }

    LaunchedEffect(pagerState.settledPage) {
        if (pagerState.settledPage != currentSongIndex)
            // User switched using the pager
            onSongSwitched(pagerState.settledPage)
    }

    LaunchedEffect(currentSongIndex) {
        if (currentSongIndex != pagerState.settledPage)
            pagerState.animateScrollToPage(currentSongIndex, animationSpec = tween(250))
    }

}