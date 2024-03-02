package com.omar.musica.songs.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ManageSearch
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.omar.musica.songs.SearchScreenUiState
import com.omar.musica.songs.viewmodel.SearchViewModel
import com.omar.musica.store.model.song.Song
import com.omar.musica.ui.common.LocalCommonSongsAction
import com.omar.musica.ui.common.MultiSelectState
import com.omar.musica.ui.menu.buildCommonMultipleSongsActions
import com.omar.musica.ui.menu.buildCommonSongActions
import com.omar.musica.ui.songs.selectableSongsList
import com.omar.musica.ui.topbar.SelectionTopAppBarScaffold


@Composable
fun SearchScreen(
    modifier: Modifier,
    searchViewModel: SearchViewModel = hiltViewModel(),
    onBackPressed: () -> Unit,
    enableBackPress: Boolean = true,
) {
    val state by searchViewModel.state.collectAsStateWithLifecycle(minActiveState = Lifecycle.State.STARTED)

    val searchFocusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    SearchScreen(
        modifier = modifier,
        state = state,
        enableBackPress = enableBackPress,
        onSongClicked = searchViewModel::onSongClicked,
        onSearchQueryChanged = searchViewModel::onSearchQueryChanged,
        searchFocusRequester = searchFocusRequester,
        onBackPressed = { focusManager.clearFocus(); onBackPressed() }
    )

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SearchScreen(
    modifier: Modifier,
    state: SearchScreenUiState,
    enableBackPress: Boolean = true,
    onSongClicked: (Song, Int) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    searchFocusRequester: FocusRequester,
    onBackPressed: () -> Unit
) {

    LaunchedEffect(key1 = Unit) {
        searchFocusRequester.requestFocus()
    }

    BackHandler(enableBackPress) {
        onBackPressed()
    }

    val context = LocalContext.current

    val multiSelectState = remember { MultiSelectState() }

    val multiSelectEnabled by remember {
        derivedStateOf { multiSelectState.selected.size > 0 }
    }

    BackHandler(multiSelectEnabled && enableBackPress) {
        multiSelectState.clear()
    }

    val commonSongsActions = LocalCommonSongsAction.current

    Surface(modifier = Modifier.fillMaxSize(), tonalElevation = 2.dp) {

        Scaffold(
            modifier.fillMaxSize(),
            topBar = {
                SelectionTopAppBarScaffold(
                    modifier = Modifier.fillMaxWidth(),
                    multiSelectState,
                    multiSelectEnabled,
                    buildCommonMultipleSongsActions(
                        multiSelectState.selected,
                        context,
                        commonSongsActions.playbackActions,
                        commonSongsActions.addToPlaylistDialog,
                        commonSongsActions.shareAction
                    ),
                    2,
                ) {
                    TopAppBar(modifier = Modifier.fillMaxWidth(), title = {
                        TextField(
                            modifier = Modifier.focusRequester(searchFocusRequester),
                            value = state.searchQuery,
                            onValueChange = onSearchQueryChanged,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                            ),
                            singleLine = true,
                            maxLines = 1,
                            placeholder = { Text(text = "Search your entire library") }
                        )
                    },
                        navigationIcon = {
                            IconButton(onClick = onBackPressed) {
                                Icon(
                                    imageVector = Icons.Rounded.ArrowBack,
                                    contentDescription = null
                                )
                            }
                        }
                    )
                }
            }
        ) { paddingValues ->

            AnimatedContent(
                targetState = state.searchQuery.isBlank() to (state.songs.isEmpty()),
                label = ""
            ) {

                if (it.first) {
                    EmptyQueryScreen(modifier = Modifier.fillMaxSize())
                } else if (it.second) {
                    NoResultsScreen(modifier = Modifier.fillMaxSize())
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = paddingValues.calculateTopPadding())
                    ) {

                        item {
                            if (state.songs.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    modifier = Modifier.padding(start = 16.dp),
                                    text = "Songs",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        selectableSongsList(
                            state.songs,
                            multiSelectState,
                            multiSelectEnabled,
                            animateItemPlacement = false, // for some reason if it is true, the application will crash on, no idea why
                            menuActionsBuilder = { song: Song ->
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
                                        tagEditorAction = this.openTagEditorAction
                                    )
                                }
                            },
                            onSongClicked = onSongClicked
                        )

                    }
                }

            }

        }

    }

}

@Composable
private fun EmptyQueryScreen(
    modifier: Modifier
) {
    IconWithTextScreen(
        modifier = modifier,
        iconVector = Icons.Rounded.ManageSearch,
        text = "Search all songs on this device"
    )
}


@Composable
private fun NoResultsScreen(
    modifier: Modifier
) {
    IconWithTextScreen(
        modifier = modifier,
        iconVector = Icons.Rounded.SearchOff,
        text = "No songs matching the query"
    )
}


@Composable
private fun IconWithTextScreen(
    modifier: Modifier,
    iconVector: ImageVector,
    text: String
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = iconVector, contentDescription = "", modifier = Modifier.size(72.dp))
            Text(text = text, fontWeight = FontWeight.Light, fontSize = 16.sp)
        }
    }
}