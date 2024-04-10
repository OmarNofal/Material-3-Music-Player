package com.omar.musica.albums.ui.albumdetail

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.omar.musica.albums.ui.menuactions.buildSingleAlbumMenuActions
import com.omar.musica.albums.viewmodel.AlbumDetailsScreenState
import com.omar.musica.store.model.album.AlbumSong
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


private const val SPLIT = 0.45f

@Composable
fun LandscapeAlbumDetailScreen(
    modifier: Modifier,
    state: AlbumDetailsScreenState.Loaded,
    actions: AlbumDetailActions,
    onBackClicked: () -> Unit,
    onNavigateToAlbum: (String, String) -> Unit
) {

    val albumInfo = state.albumWithSongs.albumInfo
    val albumSongs = state.albumWithSongs.songs

    val multiSelectState = remember { MultiSelectState<AlbumSong>() }


    val addToPlaylistDialog = rememberAddToPlaylistDialog()

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
                onAddToPlaylists = { addToPlaylistDialog.launch(albumSongs.map { it.song }) }
            )
        }
    ) { paddingValues ->

        BoxWithConstraints(Modifier.padding(paddingValues)) {
            val imageHeight = maxHeight

            Row {

                AlbumArt(
                    modifier = Modifier
                        .height(imageHeight)
                        .aspectRatio(1.0f)
                        .clip(RoundedCornerShape(6.dp)),
                    art = albumSongs.firstOrNull()?.song.toSongAlbumArtModel()
                )

            }

        }

        /*Row(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            Box(
                modifier = Modifier
                    .weight(SPLIT),
                contentAlignment = Alignment.Center
            ) {

            }

            Box(modifier = Modifier.weight(1.0f - SPLIT)) {

            }

        }*/
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
        numberOfVisibleIcons = 3
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
            }
        )

    }

}
