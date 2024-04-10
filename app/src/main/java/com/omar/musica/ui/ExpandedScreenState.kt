package com.omar.musica.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import com.omar.nowplaying.ui.BarState


data class ExpandedScreenOffsets @OptIn(ExperimentalFoundationApi::class) constructor(
    val nowPlayingBarSize: Dp,
    val nowPlayingAnchors: AnchoredDraggableState<BarState>,
    val bottomInsets: WindowInsets,
    private val density: Density,
) {

    @OptIn(ExperimentalFoundationApi::class)
    fun calculateNowPlayingOffset(): IntOffset {
        val x = 0
        val y =
            nowPlayingAnchors.offset.toInt() - bottomInsets.getBottom(density) - with(density) {
                nowPlayingBarSize.toPx().toInt()
            }
        return IntOffset(x, y)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun rememberExpandedScreenState(
    nowPlayingBarSize: Dp,
    nowPlayingMinOffset: Float,
    nowPlayingAnchors: AnchoredDraggableState<BarState>
): ExpandedScreenOffsets {
    val navigationBarInsets = WindowInsets.navigationBars
    val density = LocalDensity.current
    return remember(
        nowPlayingBarSize,
        density,
        nowPlayingAnchors,
        nowPlayingMinOffset,
        navigationBarInsets
    ) {
        ExpandedScreenOffsets(
            nowPlayingBarSize,
            nowPlayingAnchors,
            navigationBarInsets,
            density
        )
    }
}
