package com.omar.musica.songs.ui

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import com.omar.musica.songs.SongsScreenUiState
import com.omar.musica.songs.viewmodel.SongsViewModel
import com.omar.musica.ui.common.MenuActionItem
import com.omar.musica.ui.common.SelectableSongRow
import com.omar.musica.ui.common.SongsSummary
import com.omar.musica.ui.common.addToPlaylists
import com.omar.musica.ui.common.deleteAction
import com.omar.musica.ui.common.playNext
import com.omar.musica.ui.common.rememberSongDialog
import com.omar.musica.ui.common.share
import com.omar.musica.ui.common.shareSongs
import com.omar.musica.ui.common.songInfo
import com.omar.musica.ui.model.SongUi


@ChecksSdkIntAtLeast(30)
private val api30AndUp = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R

@Composable
fun SongsScreen(
    modifier: Modifier = Modifier,
    viewModel: SongsViewModel = hiltViewModel(),
    onSearchClicked: () -> Unit,
) {
    val songsUiState by viewModel.state.collectAsState()
    val context = LocalContext.current
    SongsScreen(
        modifier,
        songsUiState,
        viewModel::onSongClicked,
        viewModel::onPlayNext,
        { shareSongs(context, it) },
        viewModel::onDelete,
        onSearchClicked,
        viewModel::onSortOptionChanged
    )
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
internal fun SongsScreen(
    modifier: Modifier,
    uiState: SongsScreenUiState,
    onSongClicked: (SongUi, Int) -> Unit,
    onPlayNext: (List<SongUi>) -> Unit,
    onShare: (List<SongUi>) -> Unit,
    onDelete: (List<SongUi>) -> Unit,
    onSearchClicked: () -> Unit,
    onSortOptionChanged: (SortOption, isAscending: Boolean) -> Unit
) {

    val context = LocalContext.current
    val songs = (uiState as SongsScreenUiState.Success).songs

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val deleteRequestLauncher = deleteRequestLauncher()

    val songDialog = rememberSongDialog()

    val multiSelectState = remember {
        MultiSelectState()
    }

    val multiSelectEnabled by remember {
        derivedStateOf { multiSelectState.selected.size > 0 }
    }

    BackHandler(multiSelectEnabled) {
        multiSelectState.clear()
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            SongsTopAppBar(Modifier, onSearchClicked, scrollBehavior, multiSelectState) {
                onPlayNext(multiSelectState.selected)
                multiSelectState.clear()
            }
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(top = paddingValues.calculateTopPadding())
                .nestedScroll(scrollBehavior.nestedScrollConnection),
        ) {

            item {
                AnimatedVisibility(visible = !multiSelectEnabled) {
                    SongsSummary(
                        Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, top = 16.dp),
                        songs.count(),
                        songs.sumOf { it.length }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Divider(Modifier.fillMaxSize())
                }
            }

            item {
                AnimatedVisibility(visible = !multiSelectEnabled) {
                    SortChip(
                        modifier = Modifier.padding(top = 8.dp, start = 12.dp, bottom = 4.dp),
                        sortOptions = SortOption.entries,
                        onSortOptionSelected = onSortOptionChanged,
                        currentSortOption = uiState.sortOption,
                        isAscending = uiState.isSortedAscendingly
                    )
                }
            }

            itemsIndexed(songs, key = { _, song -> song.uriString }) { index, song ->

                val menuActions = remember {
                    mutableListOf<MenuActionItem>()
                        .apply {
                            playNext { onPlayNext(listOf(song)) }
                            addToPlaylists { }
                            share { onShare(listOf(song)) }
                            songInfo { songDialog.open(song) }
                            deleteAction {
                                if (api30AndUp) {
                                    deleteRequestLauncher.launch(
                                        getIntentSenderRequest(
                                            context,
                                            song.uriString.toUri()
                                        )
                                    )
                                } else {
                                    onDelete(listOf(song))
                                }
                            }
                        }
                }

                SelectableSongRow(
                    modifier = Modifier
                        .animateItemPlacement()
                        .fillMaxWidth()
                        .combinedClickable(
                            onLongClick = {
                                multiSelectState.toggleSong(song)
                            }
                        ) {
                            if (multiSelectEnabled)
                                multiSelectState.toggleSong(song)
                            else
                                onSongClicked(song, index)
                        },
                    song = song,
                    menuOptions = menuActions,
                    multiSelectOn = multiSelectEnabled,
                    isSelected = multiSelectState.selected.contains(song)
                )
                if (song != songs.last()) {
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

@Composable
fun deleteRequestLauncher(): ActivityResultLauncher<IntentSenderRequest> {
    val context = LocalContext.current
    return rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = {
            if (it.resultCode == Activity.RESULT_OK) {
                Toast.makeText(context, "Song deleted", Toast.LENGTH_SHORT).show()
            }
        }
    )
}


@RequiresApi(30)
fun getIntentSenderRequest(context: Context, uri: Uri): IntentSenderRequest {
    return with(context) {

        val deleteRequest =
            android.provider.MediaStore.createDeleteRequest(contentResolver, listOf(uri))

        IntentSenderRequest.Builder(deleteRequest)
            .setFillInIntent(null)
            .setFlags(android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION, 0)
            .build()
    }
}

data class MultiSelectState(
    val selected: MutableList<SongUi> = mutableStateListOf()
) {
    fun selectSong(songUi: SongUi) {
        selected.add(songUi)
    }

    fun toggleSong(songUi: SongUi) {
        if (selected.contains(songUi)) {
            deselectSong(songUi)
        } else {
            selectSong(songUi)
        }
    }

    fun clear() {
        selected.clear()
    }

    fun deselectSong(songUi: SongUi) {
        selected.remove(songUi)
    }
}