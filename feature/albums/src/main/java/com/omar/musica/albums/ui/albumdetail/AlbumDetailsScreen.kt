package com.omar.musica.albums.ui.albumdetail

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.imageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import coil.size.Size
import com.omar.musica.albums.ui.effects.AlbumDetailStatusBarColorEffect
import com.omar.musica.albums.viewmodel.AlbumDetailsScreenState
import com.omar.musica.albums.viewmodel.AlbumDetailsViewModel
import com.omar.musica.store.model.album.AlbumSong
import com.omar.musica.ui.actions.rememberCreatePlaylistShortcutDialog
import com.omar.musica.ui.albumart.LocalInefficientThumbnailImageLoader
import com.omar.musica.ui.albumart.toSongAlbumArtModel
import com.omar.musica.ui.common.LocalCommonSongsAction
import com.omar.musica.ui.common.MultiSelectState
import com.omar.musica.ui.menu.buildCommonMultipleSongsActions
import com.omar.musica.ui.shortcut.ShortcutDialogData
import com.omar.musica.ui.showShortToast
import com.omar.musica.ui.theme.isAppInDarkTheme
import com.omar.musica.ui.topbar.SelectionTopAppBarScaffold
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun AlbumDetailsScreen(
  modifier: Modifier,
  viewModel: AlbumDetailsViewModel = hiltViewModel(),
  onNavigateToAlbum: (albumId: Int) -> Unit,
  onBackClicked: () -> Unit
) {
  val state by viewModel.state.collectAsState()
  if (state !is AlbumDetailsScreenState.Loaded) {
    AlbumDetailsLoadingScreen(Modifier.fillMaxSize())
    return
  }

  BoxWithConstraints {
    if (maxWidth > maxHeight)
      AlbumDetailsLandscapeScreen(
        modifier = modifier,
        state = state as AlbumDetailsScreenState.Loaded,
        actions = viewModel,
        onBackClicked = onBackClicked,
        onNavigateToAlbum = onNavigateToAlbum
      )
    else
      AlbumDetailsPortraitScreen(
        modifier = modifier,
        state = state as AlbumDetailsScreenState.Loaded,
        actions = viewModel,
        onBackClicked = onBackClicked,
        onNavigateToAlbum = onNavigateToAlbum
      )
  }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumDetailsPortraitScreen(
  modifier: Modifier,
  state: AlbumDetailsScreenState.Loaded,
  actions: AlbumDetailActions,
  onBackClicked: () -> Unit,
  onNavigateToAlbum: (albumId: Int) -> Unit
) {

  val albumInfo = state.albumWithSongs.albumInfo
  val albumSongs = state.albumWithSongs.songs
  val otherAlbums = state.otherAlbums

  val collapseSystem = remember { CollapsingSystem() }

  val density = LocalDensity.current

  if (collapseSystem.collapsePercentage < 0.4f)
    AlbumDetailStatusBarColorEffect(collapsePercentage = collapseSystem.collapsePercentage)

  Box(modifier = modifier
    .onGloballyPositioned {
      collapseSystem.screenWidthPx = it.size.width
    }
    .nestedScroll(collapseSystem.nestedScrollConnection)
  ) {

    AlbumArtHeader(
      modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(1.0f)
        .offset {
          val yOffset =
            (-collapseSystem.collapsePercentage * 0.15 * collapseSystem.screenWidthPx).toInt()
          IntOffset(0, yOffset)
        }
        .graphicsLayer {
          alpha = (1 - collapseSystem.collapsePercentage * 2).coerceIn(0.0f, 1.0f)
        },
      songAlbumArtModel = albumSongs.firstOrNull()?.song.toSongAlbumArtModel(),
      albumInfo = albumInfo,
      fadeEdge = isAppInDarkTheme()
    )

    val multiSelectState = remember {
      MultiSelectState<AlbumSong>()
    }

    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(top = with(density) { collapseSystem.topBarHeightPx.toDp() })
        .offset {
          // offset by 8dp to show rounded corners
          val yOffset =
            ((collapseSystem.totalCollapsableHeightPx - 8.dp.toPx()) * (1 - collapseSystem.collapsePercentage)).toInt()
          IntOffset(0, yOffset)
        }
        .then(
          if (isAppInDarkTheme())
            Modifier
          else
            Modifier.shadow(
              (24 * (1 - collapseSystem.collapsePercentage)).dp,
              spotColor = Color.Transparent
            )
        )
        .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(12.dp))

    ) {
      Spacer(modifier = Modifier.height(14.dp))

      AlbumPlaybackButtons(
        modifier = Modifier.padding(horizontal = 16.dp),
        onPlay = actions::play,
        onShuffle = actions::shuffle
      )

      LazyColumn(
        modifier = Modifier.fillMaxSize()
      ) {

        item {
          Text(
            modifier = Modifier.padding(
              start = 16.dp,
              top = 16.dp,
              bottom = 8.dp
            ),
            text = "Songs",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Light
          )
        }
        itemsIndexed(albumSongs) { num, song ->
          AlbumSongRow(
            modifier = Modifier
              .fillMaxWidth()
              .clickableAndSelectable(
                multiSelectState,
                song
              ) { actions.playAtIndex(num) }
              .then(
                if (multiSelectState.selected.contains(song))
                  Modifier.background(
                    MaterialTheme.colorScheme.onSurface.copy(
                      0.15f
                    )
                  )
                else
                  Modifier
              )
              .padding(start = 24.dp, end = 6.dp, top = 4.dp, bottom = 4.dp),
            song = song,
            song.trackNumber
          )
        }
        if (otherAlbums.isEmpty()) return@LazyColumn
        item {
          Text(
            modifier = Modifier.padding(
              start = 16.dp,
              top = 16.dp,
              bottom = 8.dp
            ),
            text = "More by ${albumInfo.artist}",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Light
          )
        }
        item {
          OtherAlbumsRow(
            modifier = Modifier
              .fillMaxWidth()
              .height(IntrinsicSize.Min),
            otherAlbums = otherAlbums,
            onAlbumClicked = onNavigateToAlbum
          )
        }
      }
    }

    val localCommonSongActions = LocalCommonSongsAction.current
    val addToPlaylistDialog = localCommonSongActions.addToPlaylistDialog

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val createShortcutDialog = LocalCommonSongsAction.current.createShortcutDialog
    val imageLoader = LocalInefficientThumbnailImageLoader.current

    SelectionTopAppBarScaffold(
      modifier = Modifier.fillMaxWidth(),
      multiSelectState = multiSelectState,
      isMultiSelectEnabled = multiSelectState.selected.isNotEmpty(),
      actionItems = buildCommonMultipleSongsActions(
        multiSelectState.selected.map { it.song },
        context,
        localCommonSongActions.playbackActions,
        localCommonSongActions.addToPlaylistDialog,
        localCommonSongActions.shareAction
      ),
      numberOfVisibleIcons = 3
    ) {
      AlbumDetailPortraitTopBar(
        modifier = Modifier.fillMaxWidth(),
        name = albumInfo.name,
        collapseSystem.collapsePercentage,
        onBarHeightChanged = { collapseSystem.topBarHeightPx = it },
        onBackClicked = onBackClicked,
        onPlayNext = {
          actions.playNext()
          context.showShortToast("${albumInfo.name} will play next")
        },
        onAddToQueue = {
          actions.addToQueue()
          context.showShortToast("${albumInfo.name} added to queue")
        },
        onShuffleNext = {
          actions.shuffleNext()
          context.showShortToast("${albumInfo.name} will play next")
        },
        onAddToPlaylists = {
          addToPlaylistDialog.launch(albumSongs.map { it.song })
        },
        onOpenShortcutDialog = {
          scope.launch {
            // get bitmap
            val request = ImageRequest.Builder(context)
              .data(albumSongs.first().song.toSongAlbumArtModel())
              .size(Size.ORIGINAL)
              .build()

            val result = withContext(Dispatchers.IO) { imageLoader.execute(request) }
            val bitmap = if (result is SuccessResult)
              (result.drawable as BitmapDrawable).bitmap
            else
              null

            createShortcutDialog.launchForAlbum(
              ShortcutDialogData.AlbumShortcutDialogData(
                albumInfo.name,
                albumInfo.id,
                bitmap
              )
            )
          }
        }
      )
    }
  }
}

@OptIn(ExperimentalFoundationApi::class)
fun <T> Modifier.clickableAndSelectable(
  multiSelectState: MultiSelectState<T>,
  item: T,
  onNormalClick: () -> Unit
): Modifier = this.combinedClickable(
  onLongClick = {
    multiSelectState.toggle(item)
  },
  onClick = {
    if (multiSelectState.selected.isNotEmpty())
      multiSelectState.toggle(item)
    else
      onNormalClick()
  }
)

@Composable
fun AlbumDetailsLoadingScreen(modifier: Modifier) {
  Box(modifier = modifier, contentAlignment = Alignment.Center) {
    CircularProgressIndicator()
  }
}