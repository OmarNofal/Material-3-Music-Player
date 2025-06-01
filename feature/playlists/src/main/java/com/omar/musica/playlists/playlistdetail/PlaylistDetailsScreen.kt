package com.omar.musica.playlists.playlistdetail

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.request.ImageRequest
import coil.request.SuccessResult
import coil.size.Size
import com.omar.musica.model.playlist.PlaylistInfo
import com.omar.musica.store.model.song.Song
import com.omar.musica.ui.albumart.LocalInefficientThumbnailImageLoader
import com.omar.musica.ui.albumart.SongAlbumArtImage
import com.omar.musica.ui.albumart.toSongAlbumArtModel
import com.omar.musica.ui.common.LocalCommonSongsAction
import com.omar.musica.ui.common.MultiSelectState
import com.omar.musica.ui.common.RenamableTextView
import com.omar.musica.ui.menu.MenuActionItem
import com.omar.musica.ui.menu.addShortcutToHomeScreen
import com.omar.musica.ui.menu.addToQueue
import com.omar.musica.ui.menu.buildCommonMultipleSongsActions
import com.omar.musica.ui.menu.buildCommonSongActions
import com.omar.musica.ui.menu.delete
import com.omar.musica.ui.menu.edit
import com.omar.musica.ui.menu.playNext
import com.omar.musica.ui.menu.removeFromPlaylist
import com.omar.musica.ui.menu.rename
import com.omar.musica.ui.menu.shuffleNext
import com.omar.musica.ui.millisToTime
import com.omar.musica.ui.shortcut.ShortcutDialogData
import com.omar.musica.ui.showShortToast
import com.omar.musica.ui.songs.selectableSongsList
import com.omar.musica.ui.topbar.OverflowMenu
import com.omar.musica.ui.topbar.SelectionTopAppBarScaffold
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun PlaylistDetailScreen(
  modifier: Modifier,
  onBackPressed: () -> Unit,
  playlistDetailViewModel: PlaylistDetailViewModel = hiltViewModel()
) {
  val state by playlistDetailViewModel.state.collectAsState()
  LaunchedEffect(key1 = state) {
    if (state is PlaylistDetailScreenState.Deleted) {
      onBackPressed()
    }
  }
  if (state is PlaylistDetailScreenState.Deleted) {
    return
  }
  PlaylistDetailScreen(
    modifier = modifier,
    state = state,
    playlistActions = playlistDetailViewModel,
    onBackPressed = onBackPressed,
    onSongClicked = playlistDetailViewModel::onSongClicked,
    onEdit = {},
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PlaylistDetailScreen(
  modifier: Modifier,
  state: PlaylistDetailScreenState,
  playlistActions: PlaylistActions,
  onBackPressed: () -> Unit,
  onSongClicked: (Song) -> Unit,
  onEdit: () -> Unit,
) {
  if (state is PlaylistDetailScreenState.Loading) return
  val state = state as PlaylistDetailScreenState.Loaded

  val scope = rememberCoroutineScope()
  val topBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

  val listState = rememberLazyListState()
  val shouldShowTopBarTitle by rememberShouldShowTopBar(listState = listState)

  val deletePlaylistDialog = rememberDeletePlaylistDialog(playlistName = state.name, onDelete = playlistActions::delete)

  val context = LocalContext.current
  val commonSongsActions = LocalCommonSongsAction.current

  var inRenameMode by remember { mutableStateOf(false) }
  BackHandler(inRenameMode) {
    inRenameMode = false
  }

  val multiSelectState = remember {
    MultiSelectState<Song>()
  }

  val multiSelectEnabled by remember {
    derivedStateOf { multiSelectState.selected.isNotEmpty() }
  }

  BackHandler(multiSelectEnabled) {
    multiSelectState.clear()
  }

  Scaffold(
    modifier = modifier.pointerInput(Unit) {
      detectTapGestures(
        onPress = {
          if (inRenameMode) inRenameMode = false
        }
      )
    },
    topBar = {
      SelectionTopAppBarScaffold(
        modifier = Modifier.fillMaxWidth(),
        multiSelectState = multiSelectState,
        isMultiSelectEnabled = multiSelectEnabled,
        actionItems = buildCommonMultipleSongsActions(
          multiSelectState.selected,
          context,
          commonSongsActions.playbackActions,
          commonSongsActions.addToPlaylistDialog,
          commonSongsActions.shareAction
        ).apply {
          removeFromPlaylist {
            playlistActions.removeSongs(multiSelectState.selected.map { it.uri.toString() })
            multiSelectState.clear()
          }
        },
        numberOfVisibleIcons = 2,
        scrollBehavior = topBarScrollBehavior
      ) {
        PlaylistDetailTopBar(
          name = state.name,
          id = state.id,
          firstSong = state.songs.firstOrNull(),
          showName = shouldShowTopBarTitle,
          onBackPressed = onBackPressed,
          playlistActions = playlistActions,
          onRename = {
            scope.launch { listState.animateScrollToItem(0) }
            inRenameMode = true
          },
          onDelete = { deletePlaylistDialog.launch() },
          onEdit = { },
          scrollBehavior = topBarScrollBehavior
        )
      }
    },
    ) { paddingValues ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(top = paddingValues.calculateTopPadding())
        .nestedScroll(topBarScrollBehavior.nestedScrollConnection),
      state = listState,
    ) {
      item {
        PlaylistHeader(
          Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(start = 16.dp, end = 16.dp, top = 8.dp),
          state.name,
          state.numberOfSongs,
          state.songs.sumOf { it.metadata.durationMillis },
          state.songs.firstOrNull(),
          inRenameMode = inRenameMode,
          onRename = { inRenameMode = false; playlistActions.rename(it) },
          onEnableRenameMode = { inRenameMode = true },
          playlistActions::play,
          playlistActions::shuffle
        )
      }

      if (state.numberOfSongs == 0) {
        item {
          EmptyPlaylist(
            modifier = Modifier
              .fillMaxSize()
              .padding(paddingValues)
          )
        }
        return@LazyColumn
      }

      item {
        Text(
          text = "Songs",
          fontWeight = FontWeight.Medium,
          fontSize = 12.sp,
          modifier = Modifier.padding(
            top = 16.dp,
            bottom = 8.dp,
            start = 16.dp,
            end = 16.dp
          )
        )
      }

      selectableSongsList(
        state.songs,
        multiSelectState,
        multiSelectEnabled = multiSelectEnabled,
        animateItemPlacement = true,
        menuActionsBuilder = { song ->
          with(commonSongsActions) {
            buildCommonSongActions(
              song = song,
              context = context,
              songPlaybackActions = this.playbackActions,
              songInfoDialog = this.songInfoDialog,
              addToPlaylistDialog = this.addToPlaylistDialog,
              shareAction = this.shareAction,
              setAsRingtoneAction = this.setRingtoneAction,
              songDeleteAction = this.deleteAction,
              tagEditorAction = this.openTagEditorAction,
              goToAlbumAction
            ).apply {
              add(
                3,
                MenuActionItem(Icons.Rounded.Delete, "Remove from Playlist") {
                  playlistActions.removeSongs(listOf(song.uri.toString()))
                })
            }
          }
        },
        onSongClicked = { song, _ -> onSongClicked(song) }
      )

    }
  }
}


@Composable
private fun PlaylistHeader(
  modifier: Modifier,
  name: String,
  numberOfSongs: Int,
  songsDuration: Long,
  firstSong: Song?, // for the playlist image,
  inRenameMode: Boolean,
  onRename: (String) -> Unit,
  onEnableRenameMode: () -> Unit,
  onPlay: () -> Unit,
  onShuffle: () -> Unit
) {
  Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically
  ) {
    if (firstSong != null) {
      SongAlbumArtImage(
        modifier = Modifier
          .clip(RoundedCornerShape(8.dp))
          .aspectRatio(1.0f)
          .weight(0.4f),
        songAlbumArtModel = firstSong.toSongAlbumArtModel(),
        crossFadeDuration = 150
      )
    }
    Spacer(modifier = Modifier.width(8.dp))
    Column(
      Modifier
        .weight(0.6f)
        .fillMaxHeight(), verticalArrangement = Arrangement.SpaceBetween
    ) {
      Column {
        RenamableTextView(
          modifier = Modifier,
          inRenameMode = inRenameMode,
          text = name,
          fontSize = 24,
          fontWeight = FontWeight.Bold,
          onEnableRenameMode = onEnableRenameMode,
          onRename = onRename,
          enableLongPressToEdit = true
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = "$numberOfSongs songs • ${songsDuration.millisToTime()}",
          fontWeight = FontWeight.Normal,
          fontSize = 12.sp
        )
      }
      Spacer(modifier = Modifier.heightIn(6.dp, Dp.Unspecified))
      Row(modifier = Modifier.fillMaxWidth()) {
        Button(
          modifier = Modifier.weight(0.7f),
          onClick = onPlay,
          enabled = numberOfSongs > 0
        ) {
          Icon(imageVector = Icons.Rounded.PlayArrow, contentDescription = "Play")
        }
        Spacer(modifier = Modifier.width(4.dp))
        Button(
          modifier = Modifier.weight(0.3f),
          onClick = onShuffle,
          shape = CircleShape,
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
          enabled = numberOfSongs > 0
        ) {
          Icon(imageVector = Icons.Rounded.Shuffle, contentDescription = "Shuffle")
        }
      }

    }

  }

}

@Composable
fun rememberShouldShowTopBar(
  listState: LazyListState
): State<Boolean> {
  return remember {
    derivedStateOf {
      if (listState.layoutInfo.visibleItemsInfo.isEmpty()) return@derivedStateOf false
      if (listState.firstVisibleItemIndex != 0) true
      else {
        val visibleItems = listState.layoutInfo.visibleItemsInfo
        listState.firstVisibleItemScrollOffset > visibleItems[0].size * 0.8f
      }
    }
  }
}

@Composable
fun EmptyPlaylist(
  modifier: Modifier
) {
  Box(modifier = modifier, contentAlignment = Alignment.Center) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Icon(
        modifier = Modifier.size(72.dp),
        imageVector = Icons.Filled.PlaylistAdd,
        contentDescription = null
      )
      Text(
        modifier = Modifier.padding(horizontal = 16.dp),
        text = "Your playlist is empty!\nAdd songs from the main page.",
        fontWeight = FontWeight.Light, fontSize = 16.sp
      )
    }
  }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistDetailTopBar(
  modifier: Modifier = Modifier,
  name: String,
  id: Int,
  firstSong: Song? = null,
  showName: Boolean,
  onBackPressed: () -> Unit,
  playlistActions: PlaylistActions,
  onRename: () -> Unit,
  onDelete: () -> Unit,
  onEdit: () -> Unit,
  scrollBehavior: TopAppBarScrollBehavior? = null,
) {
  val context = LocalContext.current
  val createShortcutDialog = LocalCommonSongsAction.current.createShortcutDialog
  val scope = CoroutineScope(Dispatchers.IO)
  TopAppBar(
    modifier = modifier,
    title = {
      AnimatedVisibility(
        visible = showName,
        enter = fadeIn(),
        exit = fadeOut()
      ) {
        Text(text = name, fontWeight = FontWeight.SemiBold)
      }
    },
    scrollBehavior = scrollBehavior,
    navigationIcon = {
      IconButton(onClick = onBackPressed) {
        Icon(imageVector = Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null)
      }
    },
    actions = {
      val imageLoader = LocalInefficientThumbnailImageLoader.current
      val onCreateShortcut = {
        scope.launch {
          // get bitmap
          val request = ImageRequest.Builder(context)
            .data(firstSong.toSongAlbumArtModel())
            .size(Size.ORIGINAL)
            .build()
          val result = withContext(Dispatchers.IO) { imageLoader.execute(request) }
          val bitmap = if (result is SuccessResult)
            (result.drawable as BitmapDrawable).bitmap
          else
            null
          createShortcutDialog.launchForPlaylist(
            ShortcutDialogData.PlaylistShortcutDialogData(
              name,
              id,
              bitmap
            )
          )
        }
        Unit
      }
      val actionItems = remember {
        if (PlaylistInfo.isBuildInPlaylist(id))
        buildBuildInPlaylistActions(
          context,
          playlistActions,
          onCreateShortcut
        )
        else buildPlaylistActions(
          context,
          playlistActions,
          onRename,
          onCreateShortcut,
          onEdit,
          onDelete,
        )
      }
      OverflowMenu(actionItems = actionItems)
    }
  )
}

fun buildPlaylistActions(
  context: Context,
  playlistActions: PlaylistActions,
  renameAction: () -> Unit,
  createShortcut: () -> Unit,
  editAction: () -> Unit,
  deleteAction: () -> Unit,
): MutableList<MenuActionItem> {
  return mutableListOf<MenuActionItem>().apply {
    playNext { playlistActions.playNext(); context.showShortToast("Playlist will play next") }
    addToQueue { playlistActions.addToQueue(); context.showShortToast("Playlist added to queue") }
    shuffleNext { playlistActions.shuffleNext(); context.showShortToast("Playlist will play next") }
    rename(renameAction)
    addShortcutToHomeScreen(createShortcut)
    edit(editAction)
    delete(deleteAction)
  }
}

fun buildBuildInPlaylistActions(
  context: Context,
  playlistActions: PlaylistActions,
  createShortcut: () -> Unit,
): MutableList<MenuActionItem> {
  return mutableListOf<MenuActionItem>().apply {
    playNext { playlistActions.playNext(); context.showShortToast("Playlist will play next") }
    addToQueue { playlistActions.addToQueue(); context.showShortToast("Playlist added to queue") }
    shuffleNext { playlistActions.shuffleNext(); context.showShortToast("Playlist will play next") }
    addShortcutToHomeScreen(createShortcut)
  }
}
