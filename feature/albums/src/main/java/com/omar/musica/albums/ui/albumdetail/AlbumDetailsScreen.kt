package com.omar.musica.albums.ui.albumdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.omar.musica.albums.ui.effects.AlbumDetailStatusBarColorEffect
import com.omar.musica.albums.viewmodel.AlbumDetailsScreenState
import com.omar.musica.albums.viewmodel.AlbumDetailsViewModel
import com.omar.musica.ui.albumart.toSongAlbumArtModel
import com.omar.musica.ui.theme.isAppInDarkTheme

@Composable
fun AlbumDetailsScreen(
    modifier: Modifier,
    viewModel: AlbumDetailsViewModel = hiltViewModel(),
    onNavigateToAlbum: (album: String, artist: String) -> Unit,
    onBackClicked: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    if (state !is AlbumDetailsScreenState.Loaded) {
        AlbumDetailsLoadingScreen(Modifier.fillMaxSize())
        return
    }

    AlbumDetailsPortraitScreen(
        modifier = modifier,
        state = state as AlbumDetailsScreenState.Loaded,
        actions = viewModel,
        onBackClicked = onBackClicked,
        onNavigateToAlbum = onNavigateToAlbum
    )

}


@Composable
fun AlbumDetailsPortraitScreen(
    modifier: Modifier,
    state: AlbumDetailsScreenState.Loaded,
    actions: AlbumDetailActions,
    onBackClicked: () -> Unit,
    onNavigateToAlbum: (String, String) -> Unit
) {

    val albumInfo = state.albumWithSongs.albumInfo
    val albumSongs = state.albumWithSongs.songs
    val otherAlbums = state.otherAlbums

    val collapseSystem = remember { CollapsingSystem() }

    val density = LocalDensity.current

    if (collapseSystem.collapsePercentage < 0.4f)
        AlbumDetailStatusBarColorEffect(collapsePercentage = collapseSystem.collapsePercentage)

    Box(modifier = modifier
        .onGloballyPositioned {
            collapseSystem.screenWidthPx = it.size.width
        }
        .nestedScroll(collapseSystem.nestedScrollConnection)
    ) {

        AlbumArtHeader(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.0f)
                .offset {
                    IntOffset(
                        0,
                        (-(collapseSystem.collapsePercentage * 0.15) * collapseSystem.screenWidthPx).toInt()
                    )
                }
                .graphicsLayer {
                    alpha = (1 - collapseSystem.collapsePercentage * 2).coerceIn(0.0f, 1.0f)
                },
            songAlbumArtModel = albumSongs.firstOrNull()?.song.toSongAlbumArtModel(),
            albumInfo = albumInfo,
            fadeEdge = isAppInDarkTheme()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = with(density) { collapseSystem.topBarHeightPx.toDp() })
                .offset {
                    IntOffset(
                        0,
                        (collapseSystem.totalCollapsableHeightPx * (1 - collapseSystem.collapsePercentage)).toInt()
                    )
                }
                .then(
                    if (isAppInDarkTheme())
                        Modifier
                    else
                        Modifier.shadow((24 * (1 - collapseSystem.collapsePercentage)).dp)
                )
                .background(MaterialTheme.colorScheme.surface)

        ) {
            Spacer(modifier = Modifier.height(12.dp))

            AlbumPlaybackButtons(
                modifier = Modifier.padding(horizontal = 16.dp),
                onPlay = actions::play,
                onShuffle = actions::shuffle
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {

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
                            .clickable { actions.playAtIndex(num) }
                            .padding(start = 24.dp, end = 6.dp, top = 4.dp, bottom = 4.dp),
                        song = song,
                        song.trackNumber
                    )
                }
                if (otherAlbums.isNotEmpty()) {
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
                }
            }
        }
        AlbumDetailPortraitTopBar(
            modifier = Modifier.fillMaxWidth(),
            name = albumInfo.name,
            collapseSystem.collapsePercentage,
            onBarHeightChanged = { collapseSystem.topBarHeightPx = it },
            onBackClicked = onBackClicked,
            onPlayNext = actions::playNext,
            onAddToQueue = actions::addToQueue,
            onShuffleNext = actions::shuffleNext,
        )
    }


}

@Composable
fun AlbumDetailsLoadingScreen(modifier: Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}