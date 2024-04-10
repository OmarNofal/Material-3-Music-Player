package com.omar.musica.songs.ui.search

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ManageSearch
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.omar.musica.ui.R
import com.omar.musica.songs.SearchScreenUiState
import com.omar.musica.songs.viewmodel.SearchViewModel
import com.omar.musica.store.model.album.BasicAlbum
import com.omar.musica.store.model.song.Song
import com.omar.musica.ui.albumart.LocalInefficientThumbnailImageLoader
import com.omar.musica.ui.albumart.toSongAlbumArtModel
import com.omar.musica.ui.common.CommonSongsActions
import com.omar.musica.ui.common.LocalCommonSongsAction
import com.omar.musica.ui.common.MultiSelectState
import com.omar.musica.ui.menu.buildCommonSongActions
import com.omar.musica.ui.songs.selectableSongsList


@Composable
fun SearchScreen(
    modifier: Modifier,
    searchViewModel: SearchViewModel = hiltViewModel(),
    onBackPressed: () -> Unit,
    onNavigateToAlbum: (BasicAlbum) -> Unit,
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
        onBackPressed = { focusManager.clearFocus(); onBackPressed() },
        onAlbumClicked = onNavigateToAlbum
    )

}


@Composable
internal fun SearchScreen(
    modifier: Modifier,
    state: SearchScreenUiState,
    enableBackPress: Boolean = true,
    onSongClicked: (Song, Int) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    searchFocusRequester: FocusRequester,
    onBackPressed: () -> Unit,
    onAlbumClicked: (BasicAlbum) -> Unit
) {

    LaunchedEffect(key1 = Unit) {
        searchFocusRequester.requestFocus()
    }

    BackHandler(enableBackPress) {
        onBackPressed()
    }

    val multiSelectState = remember { MultiSelectState<Song>() }

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
                SearchTopBar(
                    searchQuery = state.searchQuery,
                    multiSelectState = multiSelectState,
                    multiSelectEnabled = multiSelectEnabled,
                    commonSongsActions = commonSongsActions,
                    searchFocusRequester = searchFocusRequester,
                    onBackPressed = onBackPressed,
                    onSearchQueryChanged = onSearchQueryChanged
                )
            }
        ) { paddingValues ->

            AnimatedContent(
                targetState = state.searchQuery.isBlank() to (state.songs.isEmpty()),
                label = ""
            ) {


                if (it.first) EmptyQueryScreen(modifier = Modifier.fillMaxSize())
                else if (it.second) NoResultsScreen(modifier = Modifier.fillMaxSize())
                else SearchScreenContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = paddingValues.calculateTopPadding()),
                    songs = state.songs,
                    albums = state.albums,
                    commonSongsActions = commonSongsActions,
                    multiSelectState = multiSelectState,
                    multiSelectEnabled = multiSelectEnabled,
                    onAlbumClicked = onAlbumClicked,
                    onSongClicked = onSongClicked
                )
            }

        }

    }
}


@Composable
fun SearchScreenContent(
    modifier: Modifier,
    songs: List<Song>,
    albums: List<BasicAlbum>,
    commonSongsActions: CommonSongsActions,
    multiSelectState: MultiSelectState<Song>,
    multiSelectEnabled: Boolean,
    onAlbumClicked: (BasicAlbum) -> Unit,
    onSongClicked: (Song, Int) -> Unit
) {

    val context = LocalContext.current

    LazyColumn(
        modifier = modifier
    ) {

        if (albums.isNotEmpty())
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Header(text = "Albums")
            }

        if (albums.isNotEmpty())
            item {
                AlbumsRow(
                    modifier = Modifier.fillMaxWidth(),
                    albums = albums,
                    onAlbumClicked = onAlbumClicked
                )
            }

        if (songs.isNotEmpty())
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Header(text = "Songs")
            }

        selectableSongsList(
            songs,
            multiSelectState,
            multiSelectEnabled,
            animateItemPlacement = false, // for some reason if it is true, the application will crash, i have no idea why
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


@Composable
private fun AlbumsRow(
    modifier: Modifier,
    albums: List<BasicAlbum>,
    onAlbumClicked: (BasicAlbum) -> Unit
) {
    LazyRow(modifier) {
        item { Spacer(modifier = Modifier.width(6.dp)) }
        items(albums) { album ->
            Album(modifier = Modifier
                .width(IntrinsicSize.Min)
                .clip(RoundedCornerShape(6.dp))
                .clickable { onAlbumClicked(album) }
                .padding(6.dp), basicAlbum = album)
        }
    }
}

@Composable
private fun Album(
    modifier: Modifier,
    basicAlbum: BasicAlbum
) {
    Column(
        modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        AsyncImage(
            modifier = Modifier
                .width(72.dp)
                .aspectRatio(1.0f)
                .clip(CircleShape),
            model = basicAlbum.firstSong.toSongAlbumArtModel(),
            contentDescription = "Album Art",
            error = painterResource(id = R.drawable.placeholder),
            imageLoader = LocalInefficientThumbnailImageLoader.current,
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = basicAlbum.albumInfo.name,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

    }
}

@Composable
private fun Header(text: String) {
    Text(
        modifier = Modifier.padding(start = 16.dp),
        text = text,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp
    )
}

@Composable
private fun EmptyQueryScreen(
    modifier: Modifier
) {
    IconWithTextScreen(
        modifier = modifier,
        iconVector = Icons.AutoMirrored.Rounded.ManageSearch,
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
            Icon(
                imageVector = iconVector,
                contentDescription = "",
                modifier = Modifier.size(72.dp)
            )
            Text(text = text, fontWeight = FontWeight.Light, fontSize = 16.sp)
        }
    }
}