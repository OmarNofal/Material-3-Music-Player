package com.omar.musica.albums.ui.albumdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import coil.compose.AsyncImage
import com.omar.musica.albums.ui.menuactions.buildSingleAlbumMenuActions
import com.omar.musica.albums.viewmodel.AlbumDetailsScreenState
import com.omar.musica.model.album.BasicAlbumInfo
import com.omar.musica.store.model.album.AlbumSong
import com.omar.musica.store.model.album.BasicAlbum
import com.omar.musica.ui.R
import com.omar.musica.ui.albumart.LocalInefficientThumbnailImageLoader
import com.omar.musica.ui.albumart.SongAlbumArtModel
import com.omar.musica.ui.albumart.toSongAlbumArtModel
import com.omar.musica.ui.common.LocalCommonSongsAction
import com.omar.musica.ui.common.MultiSelectState
import com.omar.musica.ui.menu.buildCommonMultipleSongsActions
import com.omar.musica.ui.playlist.rememberAddToPlaylistDialog
import com.omar.musica.ui.topbar.OverflowMenu
import com.omar.musica.ui.topbar.SelectionTopAppBarScaffold
import kotlin.math.max


private const val MAX_SPLIT = 0.45f

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumDetailsLandscapeScreen(
    modifier: Modifier,
    state: AlbumDetailsScreenState.Loaded,
    actions: AlbumDetailActions,
    onBackClicked: () -> Unit,
    onNavigateToAlbum: (Int) -> Unit
) {

    val albumInfo = state.albumWithSongs.albumInfo
    val albumSongs = state.albumWithSongs.songs

    val multiSelectState = remember { MultiSelectState<AlbumSong>() }
    val addToPlaylistDialog = rememberAddToPlaylistDialog()

    val topBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = modifier,
        topBar = {
            LandscapeAlbumDetailTopBar(
                albumName = albumInfo.name,
                multiSelectState = multiSelectState,
                onBackClicked = onBackClicked,
                onPlayNext = actions::playNext,
                onAddToQueue = actions::addToQueue,
                onShuffleNext = actions::shuffleNext,
                onAddToPlaylists = { addToPlaylistDialog.launch(albumSongs.map { it.song }) },
                scrollBehavior = topBarScrollBehavior
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->

        BoxWithConstraints(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            val screenWidth = maxWidth

            val maximumAlbumInfoWidth = screenWidth * MAX_SPLIT
            val imageSize = min(
                maximumAlbumInfoWidth,
                maxHeight
            ) // we want the image to fit to the content and make it square

            val split = max(0.1f, imageSize.value / screenWidth.value)

            val showButtonsUnderImage =
                imageSize < maxHeight // only show buttons under image when there is space under the image

            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                AlbumInfoHeader(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(split),
                    songAlbumArtModel = albumSongs.firstOrNull()?.song.toSongAlbumArtModel(),
                    showPlaybackButtons = showButtonsUnderImage,
                    onPlay = actions::play,
                    onShuffle = actions::shuffle
                )

                MainContent(
                    modifier = Modifier
                        .weight(1 - split)
                        .nestedScroll(topBarScrollBehavior.nestedScrollConnection),
                    showPlaybackButtons = !showButtonsUnderImage,
                    actions = actions,
                    albumSongs = albumSongs,
                    otherAlbums = state.otherAlbums,
                    multiSelectState = multiSelectState,
                    albumInfo = albumInfo,
                    onNavigateToAlbum = onNavigateToAlbum
                )
            }
        }
    }
}

@Composable
private fun MainContent(
    modifier: Modifier,
    showPlaybackButtons: Boolean,
    actions: AlbumDetailActions,
    albumSongs: List<AlbumSong>,
    otherAlbums: List<BasicAlbum>,
    multiSelectState: MultiSelectState<AlbumSong>,
    albumInfo: BasicAlbumInfo,
    onNavigateToAlbum: (Int) -> Unit
) {
    LazyColumn(
        modifier = modifier
    ) {

        if (showPlaybackButtons) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(0.95f),
                    contentAlignment = Alignment.Center
                ) {
                    AlbumPlaybackButtons(
                        modifier = Modifier.fillMaxWidth(),
                        onPlay = actions::play,
                        onShuffle = actions::shuffle
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(8.dp)) }

        item {
            Text(
                modifier = Modifier.padding(
                    start = 16.dp,
                    top = 16.dp,
                    bottom = 8.dp
                ),
                text = "Songs",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Light
            )
        }

        itemsIndexed(albumSongs) { num, song ->
            AlbumSongRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickableAndSelectable(
                        multiSelectState,
                        song
                    ) { actions.playAtIndex(num) }
                    .then(
                        if (multiSelectState.selected.contains(song))
                            Modifier.background(
                                MaterialTheme.colorScheme.onSurface.copy(
                                    0.15f
                                )
                            )
                        else
                            Modifier
                    )
                    .padding(start = 12.dp, end = 8.dp, top = 4.dp, bottom = 4.dp),
                song = song,
                song.trackNumber
            )
        }
        if (otherAlbums.isEmpty()) return@LazyColumn
        item {
            Text(
                modifier = Modifier.padding(
                    start = 16.dp,
                    top = 16.dp,
                    bottom = 8.dp
                ),
                text = "More by ${albumInfo.artist}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Light
            )
        }
        item {
            OtherAlbumsRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                otherAlbums = otherAlbums,
                onAlbumClicked = onNavigateToAlbum
            )
        }

        item { Spacer(modifier = Modifier.navigationBarsPadding()) }
    }

}


@Composable
private fun AlbumInfoHeader(
    modifier: Modifier,
    songAlbumArtModel: SongAlbumArtModel,
    showPlaybackButtons: Boolean,
    onPlay: () -> Unit,
    onShuffle: () -> Unit
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
        ) {

            AlbumArt(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1.0f)
                    .scale(0.95f)
                    .clip(RoundedCornerShape(12.dp)),
                art = songAlbumArtModel
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (showPlaybackButtons)
                AlbumPlaybackButtons(
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .padding(bottom = 12.dp)
                        .align(Alignment.CenterHorizontally),
                    onPlay = onPlay,
                    onShuffle = onShuffle
                )
        }
    }
}

@Composable
private fun AlbumArt(
    modifier: Modifier,
    art: SongAlbumArtModel
) {
    AsyncImage(
        modifier = modifier,
        model = art,
        contentDescription = null,
        imageLoader = LocalInefficientThumbnailImageLoader.current,
        error = painterResource(id = R.drawable.placeholder)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LandscapeAlbumDetailTopBar(
    albumName: String,
    multiSelectState: MultiSelectState<AlbumSong>,
    onBackClicked: () -> Unit,
    onPlayNext: () -> Unit,
    onAddToQueue: () -> Unit,
    onShuffleNext: () -> Unit,
    onAddToPlaylists: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) {

    val context = LocalContext.current

    val localCommonSongActions = LocalCommonSongsAction.current

    SelectionTopAppBarScaffold(
        modifier = Modifier.fillMaxWidth(),
        multiSelectState = multiSelectState,
        isMultiSelectEnabled = multiSelectState.selected.isNotEmpty(),
        actionItems = buildCommonMultipleSongsActions(
            multiSelectState.selected.map { it.song },
            context,
            localCommonSongActions.playbackActions,
            localCommonSongActions.addToPlaylistDialog,
            localCommonSongActions.shareAction
        ),
        numberOfVisibleIcons = 3,
        scrollBehavior = scrollBehavior
    ) {

        TopAppBar(
            title = { Text(text = albumName) },
            navigationIcon = {
                IconButton(onClick = onBackClicked) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null)
                }
            },
            actions = {
                OverflowMenu(
                    actionItems = buildSingleAlbumMenuActions(
                        onPlayNext,
                        onAddToQueue,
                        onShuffleNext,
                        onAddToPlaylists
                    )
                )
            },
            scrollBehavior = scrollBehavior
        )

    }

}
