package com.omar.nowplaying.queue

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlaylistRemove
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyColumnState


@Composable
fun QueueScreen(
    modifier: Modifier,
    onClose: () -> Unit,
    viewModel: QueueViewModel = hiltViewModel()
) {
    val state by viewModel.queueScreenState.collectAsState()
    QueueScreen(
        modifier = modifier,
        state = state,
        onClose = onClose,
        onClearQueue = viewModel::onClearQueue,
        onSongClicked = viewModel::onSongClicked,
        onRemoveSongFromQueue = viewModel::onRemoveFromQueue,
        reorderSong = viewModel::reorderSong
    )
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun QueueScreen(
    modifier: Modifier,
    state: QueueScreenState,
    onClose: () -> Unit,
    onClearQueue: () -> Unit,
    onSongClicked: (Int) -> Unit,
    onRemoveSongFromQueue: (Int) -> Unit,
    reorderSong: (Int, Int) -> Unit,
) {
    BackHandler(true) {
        onClose()
    }
    val color = Color(0x22000000)

    if (state !is QueueScreenState.Loaded) return

    var fabShown by remember {
        mutableStateOf(true)
    }
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (available.y < -1.0f) fabShown = false
                if (available.y > 1.0f) fabShown = true
                return Offset.Zero
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            val numberOfRemainingSongs = state.songs.size - state.currentSongIndex
            val durationMillis = state.songs.subList(state.currentSongIndex, state.songs.size)
                .sumOf { it.length }
            QueueTopBar(
                color = color,
                numberOfSongsRemaining = numberOfRemainingSongs,
                durationMillisRemaining = durationMillis
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = fabShown,
                enter = scaleIn() + fadeIn(),
                exit = scaleOut() + fadeOut()
            ) {
                FloatingActionButton(onClick = onClearQueue) {
                    Icon(
                        imageVector = Icons.Rounded.PlaylistRemove,
                        contentDescription = "Clear Queue"
                    )
                }
            }
        },
        containerColor = color
    ) {

        val reorderableList = remember(state.songs) {
            ReorderableList(
                mutableStateOf(state.songs.toMutableList()),
                reorderSong
            )
        }
        val songs by reorderableList.items

        val currentSongIndex = state.currentSongIndex


        val lazyListState = rememberLazyListState()

        val reorderState = rememberReorderableLazyColumnState(
            lazyListState = lazyListState,
            onMove = { from, to -> reorderableList.reorder(from.index, to.index) }
        )


        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .nestedScroll(nestedScrollConnection),
            state = lazyListState
        ) {

            itemsIndexed(songs, key = { _, item -> item.uriString }) { index, song ->
                ReorderableItem(
                    reorderableLazyListState = reorderState,
                    key = song.uriString,
                ) { isDragging ->

                    val disabledModifier = Modifier.alpha(0.5f)
                    val songModifier = if (index > currentSongIndex) Modifier
                        .fillMaxWidth()
                    else if (index < currentSongIndex)
                        Modifier
                            .fillMaxWidth()
                            .then(disabledModifier)
                    else Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))

                    QueueSongRow(
                        modifier = songModifier
                            .clickable { onSongClicked(index) }
                            .zIndex(if (isDragging) 2.0f else 0.0f),
                        songUi = song,
                        swipeToDeleteDelay = 100,
                        this@ReorderableItem,
                        onDragStarted = { reorderableList.onDragStarted(index) },
                        onDragStopped = { reorderableList.onDragStopped() },
                    ) {
                        onRemoveSongFromQueue(index)
                    }

                }
            }
        }

        LaunchedEffect(key1 = Unit) {
            if (currentSongIndex >= 0) {
                lazyListState.scrollToItem(currentSongIndex, scrollOffset = -50)
            }
        }

    }
}