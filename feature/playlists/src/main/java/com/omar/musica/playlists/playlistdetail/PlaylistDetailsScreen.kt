package com.omar.musica.playlists.playlistdetail

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.PlaylistAdd
import androidx.compose.material.icons.rounded.PlaylistRemove
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.omar.musica.ui.common.MultiSelectState
import com.omar.musica.ui.common.SongAlbumArtImage
import com.omar.musica.ui.common.millisToTime
import com.omar.musica.ui.common.selectableSongsList
import com.omar.musica.ui.model.SongUi
import kotlinx.coroutines.launch


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
        onBackPressed = onBackPressed,
        onPlayPlaylist = playlistDetailViewModel::onPlay,
        onShuffle = playlistDetailViewModel::onShuffle,
        onSongClicked = playlistDetailViewModel::onSongClicked,
        onPlayNext = playlistDetailViewModel::onPlayNext,
        onAddToQueue = playlistDetailViewModel::addToQueue,
        onShuffleNext = playlistDetailViewModel::onShuffleNext,
        onEdit = {},
        onRename = playlistDetailViewModel::onRename,
        onDelete = playlistDetailViewModel::onDeletePlaylist
    )

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PlaylistDetailScreen(
    modifier: Modifier,
    state: PlaylistDetailScreenState,
    onBackPressed: () -> Unit,
    onPlayPlaylist: () -> Unit,
    onShuffle: () -> Unit,
    onSongClicked: (SongUi) -> Unit,
    onPlayNext: () -> Unit,
    onAddToQueue: () -> Unit,
    onShuffleNext: () -> Unit,
    onEdit: () -> Unit,
    onRename: (String) -> Unit,
    onDelete: () -> Unit
) {

    if (state is PlaylistDetailScreenState.Loading) return
    val state = state as PlaylistDetailScreenState.Loaded

    val scope = rememberCoroutineScope()
    val topBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    val listState = rememberLazyListState()
    val shouldShowTopBarTitle by rememberShouldShowTopBar(listState = listState)

    val deletePlaylistDialog = rememberDeletePlaylistDialog(playlistName = state.name, onDelete = onDelete)

    var inRenameMode by remember { mutableStateOf(false) }
    BackHandler(inRenameMode) {
        inRenameMode = false
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                scrollBehavior = topBarScrollBehavior,

                title = {
                    AnimatedVisibility(
                        visible = shouldShowTopBarTitle,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Text(text = state.name, fontWeight = FontWeight.SemiBold)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { onBackPressed() }) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = null)
                    }
                },

                actions = {
                    var isExpanded by remember {
                        mutableStateOf(false)
                    }
                    PlaylistDropdownMenu(
                        expanded = isExpanded,
                        { isExpanded = false },
                        onPlayNext,
                        onAddToQueue,
                        onShuffleNext,
                        onEdit,
                        { inRenameMode = true; scope.launch { listState.animateScrollToItem(0) } },
                        onDelete = { deletePlaylistDialog.launch() }
                    )
                    IconButton(onClick = { isExpanded = true }) {
                        Icon(imageVector = Icons.Rounded.MoreVert, contentDescription = "")
                    }
                }
            )
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
                    state.songs.sumOf { it.length },
                    state.songs.firstOrNull(),
                    inRenameMode = inRenameMode,
                    onRename = { inRenameMode = false; onRename(it) },
                    onEnableRenameMode = { inRenameMode = true },
                    onPlayPlaylist,
                    onShuffle
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
                MultiSelectState(),
                multiSelectEnabled = false,
                animateItemPlacement = true,
                menuActionsBuilder = { null },
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
    firstSong: SongUi?, // for the playlist image,
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
                    .weight(0.4f), song = firstSong
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(
            Modifier
                .weight(0.6f)
                .fillMaxHeight(), verticalArrangement = Arrangement.SpaceBetween
        ) {

            Column {
                Box {
                    if (inRenameMode) {

                        var textFieldValue by remember {
                            mutableStateOf(TextFieldValue(
                                text = name,
                                selection = TextRange(name.length, name.length)
                            ))
                        }

                        val focusRequester = remember { FocusRequester() }

                        BasicTextField(
                            modifier = Modifier
                                .padding(top = 2.dp)
                                .focusRequester(focusRequester)
                                .border(1.dp, color = MaterialTheme.colorScheme.primary)
                            ,
                            value = textFieldValue,
                            textStyle = TextStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            onValueChange = { textFieldValue = it },
                            singleLine = true,
                            maxLines = 1,
                            keyboardActions = KeyboardActions(onDone = { onRename(textFieldValue.text) })
                        )

                        LaunchedEffect(key1 = Unit) {
                            focusRequester.requestFocus()
                        }

                    } else {
                        Text(
                            modifier = Modifier.pointerInput(Unit) {
                                detectTapGestures(
                                    onLongPress = { onEnableRenameMode() }
                                )
                            },
                            text = name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "$numberOfSongs songs • ${songsDuration.millisToTime()}",
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp
                )
            }
            Spacer(modifier = Modifier.heightIn(6.dp, Dp.Unspecified))
            Row(modifier = Modifier.fillMaxWidth()) {
                Button(modifier = Modifier.weight(0.7f), onClick = onPlay, enabled = numberOfSongs > 0) {
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
            Icon(modifier = Modifier.size(72.dp), imageVector = Icons.Filled.PlaylistAdd, contentDescription = null)
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = "Your playlist is empty!\nAdd songs from the main page.",
                fontWeight = FontWeight.Light, fontSize = 16.sp
            )
        }
    }
}


@Composable
private fun PlaylistDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onPlayNext: () -> Unit,
    onAddToQueue: () -> Unit,
    onShuffleNext: () -> Unit,
    onEdit: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit
) {
    DropdownMenu(
        modifier = Modifier
            .widthIn(128.dp)
            .clip(RoundedCornerShape(4.dp)),
        expanded = expanded, onDismissRequest = onDismissRequest
    ) {
        DropdownMenuItem(
            text = { Text(text = "Play Next") },
            onClick = { onPlayNext(); onDismissRequest() })
        DropdownMenuItem(
            text = { Text(text = "Add to Playing Queue") },
            onClick = { onAddToQueue(); onDismissRequest() })
        DropdownMenuItem(
            text = { Text(text = "Shuffle Next") },
            onClick = { onShuffleNext(); onDismissRequest() })
        DropdownMenuItem(text = { Text(text = "Edit") }, onClick = { onEdit(); onDismissRequest() })
        DropdownMenuItem(
            text = { Text(text = "Rename") },
            onClick = { onRename(); onDismissRequest() })
        DropdownMenuItem(
            text = { Text(text = "Delete") },
            onClick = { onDelete(); onDismissRequest() })
    }
}

private fun Context.showToast(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}