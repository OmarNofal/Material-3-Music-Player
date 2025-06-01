package com.omar.nowplaying.ui

import android.app.Activity
import android.view.WindowManager
import androidx.activity.compose.BackHandler
import androidx.annotation.ColorInt
import androidx.compose.animation.Animatable
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import coil.request.ImageRequest
import com.omar.musica.model.playback.PlayerState
import com.omar.musica.model.playback.RepeatMode
import com.omar.musica.store.model.song.Song
import com.omar.musica.ui.albumart.LocalInefficientThumbnailImageLoader
import com.omar.musica.ui.albumart.SongAlbumArtModel
import com.omar.musica.ui.albumart.toSongAlbumArtModel
import com.omar.musica.ui.common.toInt
import com.omar.nowplaying.lyrics.LiveLyricsScreen
import com.omar.nowplaying.lyrics.fadingEdge
import com.omar.nowplaying.viewmodel.INowPlayingViewModel


@Composable
fun PlayingScreen2(
  modifier: Modifier,
  song: Song,
  isFavorite: Boolean,
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
        isFavorite,
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
        isFavorite,
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
      playButtonColor = MaterialTheme.colorScheme.primary,
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
fun nowPlayingScreenTint(songAlbumArtModel: SongAlbumArtModel): Color {
  val imageLoader = LocalInefficientThumbnailImageLoader.current
  val context = LocalContext.current

  val defaultColor = LocalContentColor.current
  val color = remember { Animatable(defaultColor) }

  LaunchedEffect(songAlbumArtModel) {
    val result = imageLoader.execute(
      ImageRequest.Builder(context)
        .allowHardware(false)
        .size(240, 240)
        .data(songAlbumArtModel)
        .build()
    )

    val bitmap = result.drawable?.toBitmap()
    if (bitmap == null) {1
      color.animateTo(defaultColor)
      return@LaunchedEffect
    }

    val palette = Palette.from(bitmap).generate()

    // Try better swatches with fallback order
    val swatch = palette.vibrantSwatch
      ?: palette.lightVibrantSwatch
      ?: palette.mutedSwatch
      ?: palette.dominantSwatch

    val baseColor = swatch?.let { Color(it.rgb) } ?: defaultColor
    val improvedColor = improveColor(baseColor)

    color.animateTo(improvedColor)
  }

  return color.value
}

fun improveColor(color: Color): Color {
  val intColor = color.toArgb()

  return if (!isColorTooDark(intColor) && !isColorTooUnsaturated(intColor)) {
    color
  } else {
    val lightened = lightenColor(intColor, 0.3f)
    if (!isColorTooDark(lightened.toArgb())) {
      lightened
    } else {
      invertColor(lightened)
    }
  }
}

// Brightness check using perceived brightness
fun isColorTooDark(@ColorInt color: Int): Boolean {
  val r = android.graphics.Color.red(color)
  val g = android.graphics.Color.green(color)
  val b = android.graphics.Color.blue(color)
  val brightness = (r * 299 + g * 587 + b * 114) / 1000
  return brightness < 100 // Tune threshold
}

// Optional: Skip very desaturated (grey-ish) colors
fun isColorTooUnsaturated(@ColorInt color: Int): Boolean {
  val hsl = FloatArray(3)
  ColorUtils.colorToHSL(color, hsl)
  return hsl[1] < 0.15f // saturation below 15%
}

// Lighten color using HSL
fun lightenColor(@ColorInt color: Int, amount: Float = 0.2f): Color {
  val hsl = FloatArray(3)
  ColorUtils.colorToHSL(color, hsl)
  hsl[2] = (hsl[2] + amount).coerceAtMost(1f)
  return Color(ColorUtils.HSLToColor(hsl))
}

// Simple invert
fun invertColor(color: Color): Color =
  Color(1f - color.red, 1f - color.green, 1f - color.blue, color.alpha)


@Composable
fun PortraitPlayerScreen(
  modifier: Modifier,
  song: Song,
  isFavorite: Boolean,
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
      modifier = Modifier.weight(1f),
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
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .fadingEdge(fadeBrush)
            .padding(horizontal = 8.dp, vertical = 2.dp),
        )
        BackHandler {
          isShowingLyrics = false
        }
      } else {
        CrossFadingAlbumArt(
          modifier = Modifier
            .aspectRatio(1f)
            .shadow(4.dp, shape = RoundedCornerShape(10.dp), clip = true)
            .clip(RoundedCornerShape(10.dp)),
          containerModifier = Modifier,
          songAlbumArtModel = song.toSongAlbumArtModel(),
          errorPainterType = ErrorPainterType.PLACEHOLDER
        )
      }
    }
    val contentColor = nowPlayingScreenTint(songAlbumArtModel = song.toSongAlbumArtModel())
    Column(
      modifier = Modifier.weight(1f),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceEvenly
    ) {
      SongProgressInfo(
        modifier = Modifier.fillMaxWidth(),
        songDuration = song.metadata.durationMillis,
        tint = contentColor,
        songProgressProvider = nowPlayingActions::currentSongProgress,
        onUserSeek = nowPlayingActions::onUserSeek
      )
      Spacer(modifier = Modifier.height(8.dp))
      SongTextInfo(
        modifier = Modifier.fillMaxWidth(),
        song = song,
        showAlbum = false
      )
      Spacer(modifier = Modifier.height(24.dp))
      SongControls(
        modifier = Modifier.fillMaxWidth(),
        isPlaying = playbackState == PlayerState.PLAYING,
        playButtonColor = contentColor,
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
      isFavorite = isFavorite,
      isShuffleOn = isShuffleOn,
      repeatMode = repeatMode,
      isLyricsOpen = isShowingLyrics,
      onToggleFavorite = nowPlayingActions::toggleFavorite,
      onOpenQueue = onOpenQueue,
      onToggleLyrics = { isShowingLyrics = !isShowingLyrics },
      onToggleRepeatMode = nowPlayingActions::toggleRepeatMode,
      onToggleShuffle = nowPlayingActions::toggleShuffleMode
    )
  }
}


@Composable
fun LandscapePlayerScreen(
  modifier: Modifier,
  song: Song,
  isFavorite: Boolean = false,
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

    var isShowingLyrics by remember {
      mutableStateOf(false)
    }

    val photoLyricsWeight by animateFloatAsState(
      targetValue = if (isShowingLyrics) 2.5f else 1.5f,
      label = ""
    )

    AnimatedContent(
      modifier = Modifier.weight(photoLyricsWeight),
      targetState = isShowingLyrics,
      label = ""
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
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .fadingEdge(fadeBrush)
            .padding(vertical = 4.dp),
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
          containerModifier = Modifier.fillMaxWidth(),
          songAlbumArtModel = song.toSongAlbumArtModel(),
          errorPainterType = ErrorPainterType.PLACEHOLDER
        )
      }
    }


    Spacer(modifier = Modifier.width(8.dp))
    VerticalDivider(modifier = Modifier.height(1000.dp))
    Spacer(modifier = Modifier.width(8.dp))

    Column(
      modifier = Modifier.weight(2f),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      SongTextInfo(
        modifier = Modifier.fillMaxWidth(),
        song = song,
        showAlbum = false,
        marqueeEffect = false
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
        playButtonColor = MaterialTheme.colorScheme.primary,
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
        isFavorite = isFavorite,
        isShuffleOn = isShuffleOn,
        repeatMode = repeatMode,
        isLyricsOpen = isShowingLyrics,
        onOpenQueue = onOpenQueue,
        onToggleFavorite = nowPlayingActions::toggleFavorite,
        onToggleLyrics = { isShowingLyrics = !isShowingLyrics },
        onToggleRepeatMode = nowPlayingActions::toggleRepeatMode,
        onToggleShuffle = nowPlayingActions::toggleShuffleMode
      )
    }
  }
}