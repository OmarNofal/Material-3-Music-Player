@file:OptIn(ExperimentalFoundationApi::class)

package com.omar.musica.ui.compact


import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import com.omar.nowplaying.ui.BarState


/**
 * This class is responsible to calculate the offsets
 * of the components (bottom bar, NowPlayingScreen) in the Compact App Screen.
 * It assumes that everything is initially placed into its normal position
 * (ie. the bottom bar is shown and the NowPlayingScreen is above it)
 */
class CompactAppOffsetCalculator @OptIn(ExperimentalFoundationApi::class) constructor(
    private val nowPlayingAnchors: AnchoredDraggableState<BarState>,
    private val bottomBarOffsetAnimation: State<Int>,
    private val scrollProvider: () -> Float,
    private val navigationInsets: WindowInsets,
    private val bottomBarHeightPx: Int,
    private val density: Density,
    private val isNowPlayingVisible: Boolean,
    private val isPinnedMode: Boolean,
) {


    /**
     * Returns the offset of the NowPlaying screen
     * taking into consideration the drag commands of the user
     * and the visibility of the bottomBar
     */
    @OptIn(ExperimentalFoundationApi::class)
    fun getNowPlayingOffset(): IntOffset {
        val navigationInsetPx = getBottomNavInsetsPx()

        val dragProgress = scrollProvider()
        val dragOffset = nowPlayingAnchors.requireOffset().toInt();

        val baseOffset = dragOffset - navigationInsetPx * (1 - dragProgress)
        val bottomBarOffset = (getBottomBarOffset().y)
            .coerceIn(0, bottomBarHeightPx) * (1 - dragProgress)

        val y = baseOffset.toInt() + bottomBarOffset.toInt()
        return IntOffset(0, y)
    }

    /**
     * Returns the bottom bar offset when it slides out and into
     * the screen
     */
    fun getBottomBarOffset(): IntOffset {
        val navigationInsetsPx = getBottomNavInsetsPx()
        val totalBarHeightPx = bottomBarHeightPx + navigationInsetsPx

        val nowPlayingExpansionProgress = scrollProvider().coerceIn(0.0f, 1.0f)
        var y: Float = bottomBarOffsetAnimation.value.toFloat()

        if (isNowPlayingVisible)
            y += totalBarHeightPx * nowPlayingExpansionProgress

        return IntOffset(0, y.toInt())
    }

    private fun getBottomNavInsetsPx() = navigationInsets.getBottom(density)

    val bottomBarAlpha: Float
        get() = 1 - scrollProvider()

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun rememberCompactScreenUiState(
    screenHeightPx: Int,
    nowPlayingAnchors: AnchoredDraggableState<BarState>,
    scrollProvider: () -> Float,
    bottomBarHeightPx: Int,
    density: Density,
    isPinnedMode: Boolean,
    isNowPlayingVisible: Boolean,
    showBottomBar: Boolean
): CompactAppOffsetCalculator {

    val navigationInsets = WindowInsets.navigationBars

    val bottomNavBarOffsetPx = animateIntAsState(
        targetValue = if (showBottomBar) 0 else bottomBarHeightPx +
                navigationInsets.getBottom(density),
        animationSpec = tween(400), label = ""
    )

    return remember(
        screenHeightPx,
        scrollProvider,
        navigationInsets,
        bottomBarHeightPx,
        density,
        isPinnedMode,
        isNowPlayingVisible,
        showBottomBar
    ) {
        CompactAppOffsetCalculator(
            nowPlayingAnchors,
            bottomNavBarOffsetPx,
            scrollProvider,
            navigationInsets,
            bottomBarHeightPx,
            density,
            isNowPlayingVisible,
            isPinnedMode
        )
    }

}