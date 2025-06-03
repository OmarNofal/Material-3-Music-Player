package com.omar.musica.songs.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Sort
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.omar.musica.model.SongSortOption
import com.omar.musica.songs.SongsScreenUiState
import com.omar.musica.songs.viewmodel.SongsViewModel
import com.omar.musica.store.model.song.Song
import com.omar.musica.ui.common.LocalCommonSongsAction
import com.omar.musica.ui.common.LocalUserPreferences
import com.omar.musica.ui.common.MultiSelectState
import com.omar.musica.ui.menu.MenuActionItem
import com.omar.musica.ui.menu.buildCommonMultipleSongsActions
import com.omar.musica.ui.menu.buildCommonSongActions
import com.omar.musica.ui.songs.SongsSummary
import com.omar.musica.ui.songs.selectableSongsList
import com.omar.musica.ui.topbar.OverflowMenu
import com.omar.musica.ui.topbar.SelectionTopAppBarScaffold


@Composable
fun SongsScreen(
  modifier: Modifier = Modifier,
  viewModel: SongsViewModel = hiltViewModel(),
  onSearchClicked: () -> Unit,
  onSettingsClicked: () -> Unit,
  onAudioSearchClicked: () -> Unit = {}
) {
  val songsUiState by viewModel.state.collectAsState()
  SongsScreen(
    modifier,
    songsUiState,
    viewModel::onSongClicked,
    onSearchClicked,
    onSettingsClicked,
    onAudioSearchClicked,
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
  onAudioSearchClicked: () -> Unit,
  onSortOptionChanged: (SongSortOption, isAscending: Boolean) -> Unit
) {

  val context = LocalContext.current
  val songs = (uiState as SongsScreenUiState.Success).songs

  val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

  val multiSelectState = remember {
    MultiSelectState<Song>()
  }

  val multiSelectEnabled by remember {
    derivedStateOf { multiSelectState.selected.isNotEmpty() }
  }

  var sortOptionDropdownMenuShown by remember {
    mutableStateOf(false)
  }

  BackHandler(multiSelectEnabled) {
    multiSelectState.clear()
  }

  val commonSongActions = LocalCommonSongsAction.current
  val librarySettings = LocalUserPreferences.current.librarySettings

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
            IconButton(onAudioSearchClicked) {
              Icon(Icons.Rounded.Mic, contentDescription = "Audio Search")
            }
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
              contentPaddingValues = PaddingValues(vertical = 16.dp, horizontal = 16.dp)
            )
          },
          scrollBehavior = scrollBehavior
        )
      }
    },
  ) { paddingValues ->
    val layoutDirection = LocalLayoutDirection.current
    LazyColumn(
      modifier = Modifier
        .padding(
          top = paddingValues.calculateTopPadding(),
          end = paddingValues.calculateEndPadding(layoutDirection),
          start = paddingValues.calculateStartPadding(layoutDirection)
        )
        .nestedScroll(scrollBehavior.nestedScrollConnection),
    ) {
      item {
        HorizontalDivider()
      }
      item {
        AnimatedVisibility(visible = !multiSelectEnabled) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            SongsSummary(
              modifier = Modifier,
              songs.count(),
              songs.sumOf { it.metadata.durationMillis }
            )
            Spacer(Modifier.width(16.dp))
//            SortChip(
//              modifier = Modifier,
//              songSortOptions = SongSortOption.entries,
//              onSortOptionSelected = onSortOptionChanged,
//              currentSongSortOption = uiState.songSortOption,
//              isAscending = uiState.isSortedAscendingly
//            )
            IconButton(
              modifier = Modifier.height(32.dp),
              onClick = { sortOptionDropdownMenuShown = true }
            ) {
              Icon(imageVector = Icons.AutoMirrored.Rounded.Sort, contentDescription = "Sort")
              SortOptionDropdownMenu(
                visible = sortOptionDropdownMenuShown,
                sortOption = librarySettings.songsSortOrder.first,
                isAscending = librarySettings.songsSortOrder.second,
                onChangeSortCriteria = {
                  onSortOptionChanged(it, librarySettings.songsSortOrder.second) ;
                  sortOptionDropdownMenuShown = false
                },
                onChangeAscending = {
                  onSortOptionChanged(librarySettings.songsSortOrder.first, it)
                },
                onDismissRequest = {
                  sortOptionDropdownMenuShown = false
                },
              )
            }
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
  }
}