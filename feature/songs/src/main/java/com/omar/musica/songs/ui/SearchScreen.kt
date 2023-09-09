package com.omar.musica.songs.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.omar.musica.model.Song
import com.omar.musica.songs.SearchScreenUiState
import com.omar.musica.songs.viewmodel.SearchViewModel
import com.omar.musica.ui.common.SongItem


@Composable
fun SearchScreen(
    modifier: Modifier,
    searchViewModel: SearchViewModel = hiltViewModel(),
    onBackPressed: () -> Unit
) {

    val state by searchViewModel.state.collectAsStateWithLifecycle(minActiveState = Lifecycle.State.STARTED)

    val searchFocusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    SearchScreen(
        modifier = modifier,
        state = state,
        onSongClicked = searchViewModel::onSongClicked,
        onPlayNext = searchViewModel::onPlayNext,
        onSearchQueryChanged = searchViewModel::onSearchQueryChanged,
        searchFocusRequester = searchFocusRequester,
        onBackPressed = { focusManager.clearFocus(); onBackPressed() }
    )

}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
internal fun SearchScreen(
    modifier: Modifier,
    state: SearchScreenUiState,
    onSongClicked: (Song, Int) -> Unit,
    onPlayNext: (Song) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    searchFocusRequester: FocusRequester,
    onBackPressed: () -> Unit
) {

    LaunchedEffect(key1 = Unit) {
        searchFocusRequester.requestFocus()
    }

    BackHandler {
        onBackPressed()
    }

    SearchBar(
        modifier = modifier
            .focusRequester(searchFocusRequester)
            .fillMaxSize(),
        query = state.searchQuery,
        onQueryChange = onSearchQueryChanged,
        onSearch = {}, // Search is done automatically
        active = true,
        shape = SearchBarDefaults.inputFieldShape,
        onActiveChange = { if (!it) onBackPressed() },
        placeholder = { Text(text = "Search your entire library") },
        leadingIcon = {
            IconButton(onClick = onBackPressed) {
                Icon(Icons.Rounded.ArrowBack, contentDescription = null)
            }
        }
    ) {


        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
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

            itemsIndexed(state.songs, { _, item -> item.uriString }) { index, song ->

//                val menuActions = remember {
//                    mutableListOf<MenuActionItem>()
//                        .apply {
//                            playNext { onPlayNext(listOf(song)) }
//                            addToPlaylists { }
//                            share { onShare(listOf(song)) }
//                            deleteAction {
//                                if (api30AndUp) {
//                                    deleteRequestLauncher.launch(getIntentSenderRequest(context, song.uriString.toUri()))
//                                } else {
//                                    onDelete(listOf(song))
//                                }
//                            }
//                        }
//                }

                SongItem(
                    modifier = Modifier
                        .animateItemPlacement()
                        .fillMaxWidth()
                        .clickable { onSongClicked(song, index) },
                    song = song,
                    menuOptions = null
                )
                if (song != state.songs.last()) {
                    Divider(
                        Modifier
                            .fillMaxWidth()
                            .padding(start = (12 + 54 + 8).dp)
                    )
                }

            }

        }

    }


}