package com.omar.musica.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import com.omar.nowplaying.NowPlayingState
import com.omar.nowplaying.viewmodel.NowPlayingViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.map


@Composable
fun rememberMusicaAppState(
    navHostController: NavHostController,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    isNowPlayingExpanded: Boolean,
    nowPlayingViewModel: NowPlayingViewModel,
    nowPlayingVisibilityProvider: () -> Float
): MusicaAppState {
    return remember(
        navHostController,
        coroutineScope,
        isNowPlayingExpanded,
        nowPlayingVisibilityProvider
    ) {
        MusicaAppState(
            navHostController,
            coroutineScope,
            isNowPlayingExpanded,
            nowPlayingViewModel,
            nowPlayingVisibilityProvider
        )
    }
}


@Stable
class MusicaAppState(
    val navHostController: NavHostController,
    val coroutineScope: CoroutineScope,
    val isNowPlayingExpanded: Boolean,
    val nowPlayingViewModel: NowPlayingViewModel,
    val nowPlayingVisibilityProvider: () -> Float
) {


    /**
     * Whether we should show the NowPlaying Screen or not.
     */
    val shouldShowNowPlayingScreen = nowPlayingViewModel.state.map { it is NowPlayingState.Playing }


}