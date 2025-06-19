package com.omar.musica.songs.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Sort
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.omar.musica.model.SongSortOption
import com.omar.musica.songs.SongsScreenUiState
import com.omar.musica.songs.viewmodel.SongsViewModel
import com.omar.musica.store.model.song.Song
import com.omar.musica.ui.common.LocalCommonSongsAction
import com.omar.musica.ui.common.MultiSelectState
import com.omar.musica.ui.drag.ListDraggableHandle
import com.omar.musica.ui.menu.MenuActionItem
import com.omar.musica.ui.menu.buildCommonMultipleSongsActions
import com.omar.musica.ui.menu.buildCommonSongActions
import com.omar.musica.ui.songs.SongsSummary
import com.omar.musica.ui.songs.selectableSongsList
import com.omar.musica.ui.theme.ManropeFontFamily
import com.omar.musica.ui.topbar.OverflowMenu
import com.omar.musica.ui.topbar.SelectionTopAppBarScaffold
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch


@Composable
fun SongsScreen(
    modifier: Modifier = Modifier,
    viewModel: SongsViewModel = hiltViewModel(),
    onSearchClicked: () -> Unit,
    onSettingsClicked: () -> Unit
) {
    val songsUiState by viewModel.state.collectAsState()
    SongsScreen(
        modifier,
        songsUiState,
        viewModel::onSongClicked,
        onSearchClicked,
        onSettingsClicked,
        viewModel::onSortOptionChanged
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SongsScreen(
    modifier: Modifier,
    uiState: SongsScreenUiState,
    onSongClicked: (Song, Int) -> Unit,
    onSearchClicked: () -> Unit,
    onSettingsClicked: () -> Unit,
    onSortOptionChanged: (SongSortOption, isAscending: Boolean) -> Unit
) {

    val context = LocalContext.current
    val songs = (uiState as SongsScreenUiState.Success).songs

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val multiSelectState = remember {
        MultiSelectState<Song>()
    }

    val multiSelectEnabled by remember {
        derivedStateOf { multiSelectState.selected.size > 0 }
    }

    BackHandler(multiSelectEnabled) {
        multiSelectState.clear()
    }

    val commonSongActions = LocalCommonSongsAction.current

    Scaffold(
        modifier = modifier,
        topBar = {

            SelectionTopAppBarScaffold(
                modifier = Modifier.fillMaxWidth(),
                multiSelectState = multiSelectState,
                isMultiSelectEnabled = multiSelectEnabled,
                actionItems = buildCommonMultipleSongsActions(
                    multiSelectState.selected,
                    context,
                    commonSongActions.playbackActions,
                    commonSongActions.addToPlaylistDialog,
                    commonSongActions.shareAction
                ),
                numberOfVisibleIcons = 2,
                scrollBehavior = scrollBehavior
            ) {
                TopAppBar(
                    modifier = Modifier.fillMaxWidth(),
                    title = { Text(text = "Songs", fontWeight = FontWeight.SemiBold) },
                    actions = {
                        IconButton(onSearchClicked) {
                            Icon(Icons.Rounded.Search, contentDescription = null)
                        }
                        OverflowMenu(
                            actionItems = listOf(
                                MenuActionItem(
                                    Icons.Rounded.Settings,
                                    "Settings"
                                ) {
                                    onSettingsClicked()
                                }),
                            contentPaddingValues = PaddingValues(
                                vertical = 16.dp,
                                horizontal = 16.dp
                            )
                        )
                    },
                    scrollBehavior = scrollBehavior
                )
            }
        },
    ) { paddingValues ->
        val layoutDirection = LocalLayoutDirection.current

        var hasShownAnimation by rememberSaveable { mutableStateOf(false) }

        AnimatedVisibility(
            visible = hasShownAnimation,
            enter = fadeIn(
                animationSpec = tween(durationMillis = 400, easing = LinearOutSlowInEasing)
            ) + expandVertically(
                animationSpec = tween(durationMillis = 400, easing = LinearOutSlowInEasing),
                expandFrom = Alignment.Top
            )
        ) {

            val listState = rememberLazyListState()

            Box(Modifier.fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier
                        .padding(
                            top = paddingValues.calculateTopPadding(),
                            end = paddingValues.calculateEndPadding(layoutDirection),
                            start = paddingValues.calculateStartPadding(layoutDirection)
                        )
                        .nestedScroll(scrollBehavior.nestedScrollConnection),
                    state = listState
                ) {

                    item {
                        AnimatedVisibility(visible = !multiSelectEnabled) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                SongsSummary(
                                    modifier = Modifier,
                                    songs.count(),
                                    songs.sumOf { it.metadata.durationMillis }
                                )
                                Spacer(Modifier.width(16.dp))
                                SortButtonText(
                                    modifier = Modifier,
                                    songSortOptions = SongSortOption.entries,
                                    onSortOptionSelected = onSortOptionChanged,
                                    currentSongSortOption = uiState.songSortOption,
                                    isAscending = uiState.isSortedAscendingly
                                )
                            }
                        }
                    }



                    selectableSongsList(
                        songs,
                        multiSelectState,
                        multiSelectEnabled,
                        menuActionsBuilder = { song: Song ->
                            with(commonSongActions) {
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
                                    goToAlbumAction = this.goToAlbumAction
                                )
                            }
                        },
                        onSongClicked = onSongClicked
                    )

                    item {
                        Spacer(modifier = Modifier.navigationBarsPadding())
                    }
                }

                val totalItemsCount by remember { derivedStateOf { listState.layoutInfo.totalItemsCount - 2 } }
                val currentItemIndex by remember { derivedStateOf { listState.firstVisibleItemIndex } }
                var isDragHandleVisible by remember { mutableStateOf(false) }
                val scope = rememberCoroutineScope()

                val isSortedAlphabetically = remember(uiState.songSortOption) {
                    uiState.songSortOption in listOf(
                        SongSortOption.ALBUM,
                        SongSortOption.TITLE,
                        SongSortOption.ARTIST
                    )
                }

                val currentLetter = remember(
                    songs,
                    currentItemIndex,
                    uiState.songSortOption
                ) {
                    val song = songs.getOrNull(currentItemIndex - 1) ?: return@remember ""
                    val string = when (uiState.songSortOption) {
                        SongSortOption.TITLE -> song.metadata.title
                        SongSortOption.ALBUM -> song.metadata.albumName.orEmpty()
                        SongSortOption.ARTIST -> song.metadata.artistName.orEmpty()
                        else -> song.metadata.title
                    }
                    string.substring(0, 1)
                }

                ListDraggableHandle(
                    Modifier
                        .fillMaxHeight()
                        .padding(top = paddingValues.calculateTopPadding())
                        .align(Alignment.CenterEnd),
                    visible = isDragHandleVisible,
                    isSortedAlphabetically = isSortedAlphabetically,
                    currentLetter = currentLetter,
                    numberOfItems = totalItemsCount,
                    currentItem = currentItemIndex,
                ) { index ->
                    scope.launch {
                        listState.scrollToItem(index + 1)
                    }
                }

                LaunchedEffect(currentItemIndex) {
                    isDragHandleVisible = true
                    delay(1000)
                    if (isActive)
                        isDragHandleVisible = false
                }
            }
        }

        LaunchedEffect(Unit) {
            hasShownAnimation = true
        }

    }


}

@Composable
fun SortButtonText(
    modifier: Modifier,
    songSortOptions: List<SongSortOption>,
    onSortOptionSelected: (SongSortOption, Boolean) -> Unit,
    currentSongSortOption: SongSortOption,
    isAscending: Boolean
) {

    var bottomSheetVisible by remember { mutableStateOf(false) }

    TextButton(
        modifier = modifier,
        onClick = { bottomSheetVisible = true }
    ) {
        Text(currentSongSortOption.title)
        Spacer(Modifier.width(6.dp))

        val iconRotation by animateFloatAsState(if (isAscending) 0f else 180f)
        Icon(
            imageVector = Icons.Rounded.ArrowUpward,
            contentDescription = null,
            modifier = Modifier.graphicsLayer { rotationZ = iconRotation })
    }

    SortBottomSheet(
        sortOptions = songSortOptions,
        selectedSortOption = currentSongSortOption,
        isAscending = isAscending,
        visible = bottomSheetVisible,
        onSortOptionChanged = onSortOptionSelected,
        onDismissRequest = { bottomSheetVisible = false })
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortBottomSheet(
    sortOptions: List<SongSortOption>,
    selectedSortOption: SongSortOption,
    isAscending: Boolean,
    visible: Boolean,
    onSortOptionChanged: (SongSortOption, Boolean) -> Unit,
    onDismissRequest: () -> Unit,
) {

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    val hide: () -> Unit = {
        scope.launch { sheetState.hide() }
            .invokeOnCompletion { if (!sheetState.isVisible) onDismissRequest() }
    }

    if (visible)
        ModalBottomSheet(onDismissRequest = hide, sheetState = sheetState) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 12.dp)
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = Icons.AutoMirrored.Rounded.Sort,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Column(
                    modifier = Modifier.padding(horizontal = 12.dp)
                ) {
                    Text(
                        "Sort by",
                        style = MaterialTheme.typography.titleMedium,
                        fontFamily = ManropeFontFamily,
                        maxLines = 1,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        "Select option again to change sort order",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Normal
                    )
                }
            }
            HorizontalDivider(thickness = 3.dp)
            sortOptions.forEach {
                val selected = it == selectedSortOption
                val onClick: () -> Unit = {
                    if (selected) onSortOptionChanged(it, !isAscending)
                    else onSortOptionChanged(it, isAscending)
                    hide()
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onClick)
                        .then(
                            if (selected) Modifier.background(
                                MaterialTheme.colorScheme.primaryContainer
                            ) else Modifier
                        )
                        .padding(vertical = 12.dp, horizontal = 12.dp)
                ) {
                    Text(
                        it.title,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Start,
                        maxLines = 1,
                        style = MaterialTheme.typography.titleSmall,
                        fontFamily = ManropeFontFamily,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                    )
                    if (selected) {
                        val icon =
                            if (isAscending) Icons.Rounded.ArrowUpward else Icons.Rounded.ArrowDownward
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                if (it != sortOptions.last()) {
                    Spacer(Modifier.height(2.dp))
                }
            }
        }

    LaunchedEffect(visible) {
        if (visible)
            sheetState.show()
    }
}
