package com.omar.nowplaying.ui

import android.app.Activity
import android.view.WindowManager
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
import androidx.compose.material.icons.rounded.QueueMusic
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import com.omar.musica.model.playback.PlayerState
import com.omar.musica.model.playback.RepeatMode
import com.omar.musica.store.model.song.Song
import com.omar.musica.ui.albumart.toSongAlbumArtModel
import com.omar.nowplaying.lyrics.LiveLyricsScreen
import com.omar.nowplaying.lyrics.fadingEdge
import com.omar.nowplaying.viewmodel.INowPlayingViewModel


@Composable
fun PlayingScreen2(
    modifier: Modifier,
    song: Song,
    repeatMode: RepeatMode,
    isShuffleOn: Boolean,
    playbackState: PlayerState,
    screenSize: NowPlayingScreenSize,
    nowPlayingActions: INowPlayingViewModel,
    onOpenQueue: () -> Unit = {},
) {

    when (screenSize) {
        NowPlayingScreenSize.COMPACT -> {
            CompactPlayerScreen(
                modifier,
                song,
                playbackState,
                nowPlayingActions,
                onOpenQueue
            )
        }

        NowPlayingScreenSize.PORTRAIT -> {
            PortraitPlayerScreen(
                modifier,
                song,
                playbackState,
                repeatMode,
                isShuffleOn,
                nowPlayingActions,
                onOpenQueue
            )
        }

        NowPlayingScreenSize.LANDSCAPE -> {
            LandscapePlayerScreen(
                modifier,
                song,
                playbackState,
                repeatMode,
                isShuffleOn,
                nowPlayingActions,
                onOpenQueue
            )
        }
    }

}

@Composable
fun CompactPlayerScreen(
    modifier: Modifier,
    song: Song,
    playbackState: PlayerState,
    nowPlayingActions: INowPlayingViewModel,
    onOpenQueue: () -> Unit
) {
    Column(
        modifier,
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SongTextInfo(
            modifier = Modifier.fillMaxWidth(),
            song = song,
            showArtist = false,
            showAlbum = false
        )

        Spacer(modifier = Modifier.height(8.dp))

        SongProgressInfo(
            modifier = Modifier.fillMaxWidth(),
            songDuration = song.metadata.durationMillis,
            songProgressProvider = nowPlayingActions::currentSongProgress,
            onUserSeek = nowPlayingActions::onUserSeek
        )

        Spacer(modifier = Modifier.height(24.dp))

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
            Icon(imageVector = Icons.AutoMirrored.Rounded.QueueMusic, contentDescription = "Queue")
            Text(text = "Queue")
        }
    }
}


@Composable
fun PortraitPlayerScreen(
    modifier: Modifier,
    song: Song,
    playbackState: PlayerState,
    repeatMode: RepeatMode,
    isShuffleOn: Boolean,
    nowPlayingActions: INowPlayingViewModel,
    onOpenQueue: () -> Unit
) {
    Column(
        modifier,
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        var isShowingLyrics by remember {
            mutableStateOf(false)
        }

        AnimatedContent(
            modifier = Modifier.weight(1f, fill = false),
            targetState = isShowingLyrics, label = ""
        ) {
            if (it) {
                val context = LocalContext.current as Activity
                DisposableEffect(key1 = Unit) {
                    context.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                    onDispose { context.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) }
                }
                val fadeBrush = remember {
                    Brush.verticalGradient(
                        0.0f to Color.Red,
                        0.7f to Color.Red,
                        1.0f to Color.Transparent
                    )
                }
                LiveLyricsScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp))
                        .fadingEdge(fadeBrush)
                        .padding(horizontal = 8.dp, vertical = 18.dp),
                )
                BackHandler {
                    isShowingLyrics = false
                }
            } else {
                CrossFadingAlbumArt(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .shadow(32.dp, shape = RoundedCornerShape(12.dp), clip = true)
                        .clip(RoundedCornerShape(12.dp)),
                    containerModifier = Modifier,
                    songAlbumArtModel = song.toSongAlbumArtModel(),
                    errorPainterType = ErrorPainterType.PLACEHOLDER
                )
            }
        }


        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SongTextInfo(
                modifier = Modifier.fillMaxWidth(),
                song = song,
                showAlbum = false
            )

            Spacer(modifier = Modifier.height(8.dp))

            SongProgressInfo(
                modifier = Modifier.fillMaxWidth(),
                songDuration = song.metadata.durationMillis,
                songProgressProvider = nowPlayingActions::currentSongProgress,
                onUserSeek = nowPlayingActions::onUserSeek
            )

            Spacer(modifier = Modifier.height(24.dp))

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
        }
        PlayerFooter(
            modifier = Modifier
                .padding(bottom = 12.dp)
                .fillMaxWidth(),
            songUi = song,
            isShuffleOn = isShuffleOn,
            repeatMode = repeatMode,
            isLyricsOpen = isShowingLyrics,
            onOpenQueue = onOpenQueue,
            onOpenLyrics = { isShowingLyrics = !isShowingLyrics },
            onToggleRepeatMode = nowPlayingActions::toggleRepeatMode,
            onToggleShuffle = nowPlayingActions::toggleShuffleMode
        )
    }
}


@Composable
fun LandscapePlayerScreen(
    modifier: Modifier,
    song: Song,
    playbackState: PlayerState,
    repeatMode: RepeatMode,
    isShuffleOn: Boolean,
    nowPlayingActions: INowPlayingViewModel,
    onOpenQueue: () -> Unit
) {
    Row(
        modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        CrossFadingAlbumArt(
            modifier = Modifier
                .aspectRatio(1f)
                .shadow(32.dp, shape = RoundedCornerShape(12.dp), clip = true)
                .clip(RoundedCornerShape(12.dp)),
            containerModifier = Modifier.weight(1.5f, fill = true),
            songAlbumArtModel = song.toSongAlbumArtModel(),
            errorPainterType = ErrorPainterType.PLACEHOLDER
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column(
            modifier = Modifier.weight(2f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            SongTextInfo(
                modifier = Modifier.fillMaxWidth(),
                song = song,
                showAlbum = false
            )

            Spacer(modifier = Modifier.height(8.dp))

            SongProgressInfo(
                modifier = Modifier.fillMaxWidth(),
                songDuration = song.metadata.durationMillis,
                songProgressProvider = nowPlayingActions::currentSongProgress,
                onUserSeek = nowPlayingActions::onUserSeek
            )

            Spacer(modifier = Modifier.height(24.dp))

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

            PlayerFooter(
                modifier = Modifier
                    .padding(bottom = 6.dp)
                    .fillMaxWidth(),
                songUi = song,
                isShuffleOn = isShuffleOn,
                repeatMode = repeatMode,
                isLyricsOpen = false,
                onOpenQueue = onOpenQueue,
                onOpenLyrics = {},
                onToggleRepeatMode = nowPlayingActions::toggleRepeatMode,
                onToggleShuffle = nowPlayingActions::toggleShuffleMode
            )
        }

    }
}