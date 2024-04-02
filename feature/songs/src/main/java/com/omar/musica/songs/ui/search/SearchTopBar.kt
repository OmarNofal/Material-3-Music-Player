package com.omar.musica.songs.ui.search

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.omar.musica.store.model.song.Song
import com.omar.musica.ui.common.CommonSongsActions
import com.omar.musica.ui.common.MultiSelectState
import com.omar.musica.ui.menu.buildCommonMultipleSongsActions
import com.omar.musica.ui.topbar.SelectionTopAppBarScaffold


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SearchTopBar(
    searchQuery: String,
    multiSelectState: MultiSelectState<Song>,
    multiSelectEnabled: Boolean,
    commonSongsActions: CommonSongsActions,
    searchFocusRequester: FocusRequester,
    onBackPressed: () -> Unit,
    onSearchQueryChanged: (String) -> Unit,
) {
    val context = LocalContext.current
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
                value = searchQuery,
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
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = null
                    )
                }
            }
        )
    }
}