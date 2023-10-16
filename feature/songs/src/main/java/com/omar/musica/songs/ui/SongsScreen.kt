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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import com.omar.musica.model.SortOption
import com.omar.musica.songs.SongsScreenUiState
import com.omar.musica.songs.viewmodel.SongsViewModel
import com.omar.musica.ui.common.MenuActionItem
import com.omar.musica.ui.common.MultiSelectState
import com.omar.musica.ui.common.SongsSummary
import com.omar.musica.ui.common.addToPlaylists
import com.omar.musica.ui.common.deleteAction
import com.omar.musica.ui.common.playNext
import com.omar.musica.ui.common.rememberSongDialog
import com.omar.musica.ui.common.selectableSongsList
import com.omar.musica.ui.common.share
import com.omar.musica.ui.common.shareSongs
import com.omar.musica.ui.common.showSongsAddedToNextToast
import com.omar.musica.ui.common.songInfo
import com.omar.musica.ui.model.LibrarySettingsUi
import com.omar.musica.ui.model.SongUi
import com.omar.musica.ui.playlist.rememberAddToPlaylistDialog


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


@OptIn(ExperimentalMaterial3Api::class)
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

    val addToPlaylistDialog = rememberAddToPlaylistDialog()

    Scaffold(
        modifier = modifier,
        topBar = {
            SongsTopAppBar(
                Modifier,
                onSearchClicked,
                onShare,
                scrollBehavior,
                multiSelectState,
                onAddToPlaylists = {
                    addToPlaylistDialog.launch(multiSelectState.selected)
                }
            ) {
                context.showSongsAddedToNextToast(multiSelectState.selected.size)
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



            selectableSongsList(
                songs,
                multiSelectState,
                multiSelectEnabled,
                menuActionsBuilder = { song: SongUi ->
                    mutableListOf<MenuActionItem>()
                        .apply {
                            playNext { onPlayNext(listOf(song)); context.showSongsAddedToNextToast(1) }
                            addToPlaylists { addToPlaylistDialog.launch(listOf(song)) }
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
                },
                onSongClicked = onSongClicked
            )
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

