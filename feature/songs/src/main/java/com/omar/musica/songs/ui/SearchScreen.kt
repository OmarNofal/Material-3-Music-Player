package com.omar.musica.songs.ui

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.PlainTooltipBox
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.omar.musica.songs.SearchScreenUiState
import com.omar.musica.songs.viewmodel.SearchViewModel
import com.omar.musica.ui.common.MenuActionItem
import com.omar.musica.ui.common.MultiSelectState
import com.omar.musica.ui.common.addToPlaylists
import com.omar.musica.ui.common.deleteAction
import com.omar.musica.ui.common.playNext
import com.omar.musica.ui.common.rememberSongDialog
import com.omar.musica.ui.common.selectableSongsList
import com.omar.musica.ui.common.share
import com.omar.musica.ui.common.shareSongs
import com.omar.musica.ui.common.showSongsAddedToNextToast
import com.omar.musica.ui.common.songInfo
import com.omar.musica.ui.model.SongUi


@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.R)
private val api30AndUp = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R


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

    val context = LocalContext.current
    SearchScreen(
        modifier = modifier,
        state = state,
        enableBackPress = enableBackPress,
        onSongClicked = searchViewModel::onSongClicked,
        onPlayNext = searchViewModel::onPlayNext,
        onDelete = searchViewModel::onDelete,
        onSearchQueryChanged = searchViewModel::onSearchQueryChanged,
        searchFocusRequester = searchFocusRequester,
        onShare = { shareSongs(context, it) },
        onBackPressed = { focusManager.clearFocus(); onBackPressed() }
    )

}


@Composable
internal fun SearchScreen(
    modifier: Modifier,
    state: SearchScreenUiState,
    enableBackPress: Boolean = true,
    onSongClicked: (SongUi, Int) -> Unit,
    onPlayNext: (List<SongUi>) -> Unit,
    onDelete: (List<SongUi>) -> Unit,
    onShare: (List<SongUi>) -> Unit,
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

    Surface(tonalElevation = 2.dp) {

        Scaffold(
            modifier.fillMaxSize(),
            topBar = {
                SearchScreenTopBar(
                    modifier = Modifier.fillMaxWidth(),
                    state = state,
                    onSearchQueryChanged = onSearchQueryChanged,
                    focusRequester = searchFocusRequester,
                    onBackPressed = onBackPressed,
                    multiSelectState = multiSelectState,
                    multiSelectEnabled = multiSelectEnabled,
                    onPlayNext = {
                        context.showSongsAddedToNextToast(multiSelectState.selected.size)
                        onPlayNext(multiSelectState.selected)
                        multiSelectState.clear()
                    },
                    onShare = onShare
                )
            }
        ) { paddingValues ->
            val deleteRequestLauncher = deleteRequestLauncher()

            val songInfoDialog = rememberSongDialog()



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
                    menuActionsBuilder = { song: SongUi ->
                        mutableListOf<MenuActionItem>()
                            .apply {
                                playNext { onPlayNext(listOf(song)) }
                                addToPlaylists { }
                                share { onShare(listOf(song)) }
                                songInfo { songInfoDialog.open(song) }
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

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreenTopBar(
    modifier: Modifier,
    state: SearchScreenUiState,
    multiSelectState: MultiSelectState,
    multiSelectEnabled: Boolean,
    focusRequester: FocusRequester,
    onBackPressed: () -> Unit,
    onPlayNext: () -> Unit,
    onShare: (List<SongUi>) -> Unit,
    onSearchQueryChanged: (String) -> Unit
) {
    AnimatedContent(
        targetState = multiSelectEnabled, label = "",
        transitionSpec = {
            if (targetState) {
                scaleIn(initialScale = 0.8f) + fadeIn() togetherWith scaleOut(targetScale = 1.2f) + fadeOut()
            } else {
                scaleIn(initialScale = 1.2f) + fadeIn() togetherWith scaleOut(targetScale = 0.8f) + fadeOut()
            }
        }
    ) {
        if (it) {
            TopAppBar(
                modifier = modifier,
                title = {
                    Text(
                        text = "${multiSelectState.selected.size} selected",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                actions = {
                    PlainTooltipBox(tooltip = { Text(text = "Play Next") }) {
                        IconButton(modifier = Modifier.tooltipAnchor(), onClick = onPlayNext) {
                            Icon(Icons.Rounded.SkipNext, contentDescription = "Play Next")
                        }
                    }
                    PlainTooltipBox(tooltip = { Text(text = "Share") }) {
                        IconButton(
                            modifier = Modifier.tooltipAnchor(),
                            onClick = { onShare(multiSelectState.selected) }) {
                            Icon(Icons.Rounded.Share, contentDescription = "Play Next")
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { multiSelectState.selected.clear() }) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "End Multi selection mode"
                        )
                    }
                }
            )
        } else {
            TopAppBar(modifier = modifier, title = {
                TextField(
                    modifier = Modifier.focusRequester(focusRequester),
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
                        Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    }
}




