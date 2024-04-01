package com.omar.musica.albums.ui.albumdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.omar.musica.albums.ui.effects.AlbumDetailStatusBarColorEffect
import com.omar.musica.albums.viewmodel.AlbumDetailsScreenState
import com.omar.musica.albums.viewmodel.AlbumDetailsViewModel
import com.omar.musica.store.model.album.BasicAlbum
import com.omar.musica.ui.albumart.LocalInefficientThumbnailImageLoader
import com.omar.musica.ui.albumart.SongAlbumArtImage
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

    var collapsePercentage by remember {
        mutableFloatStateOf(0.0f)
    }

    val density = LocalDensity.current

    var screenWidthPx by remember {
        mutableIntStateOf(0)
    }

    var topBarHeightPx by remember {
        mutableIntStateOf(0)
    }

    val totalCollapsableHeightPx = remember(screenWidthPx, topBarHeightPx) {
        screenWidthPx - topBarHeightPx
    }

    if (collapsePercentage < 0.4f)
        AlbumDetailStatusBarColorEffect(collapsePercentage = collapsePercentage)

    val nestedScrollConnection = object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            if (available.y > 0) return Offset.Zero
            val availableY = -available.y
            val scrolledPercentage = availableY / totalCollapsableHeightPx
            val oldPercentage = collapsePercentage
            val newPercentage = (collapsePercentage + scrolledPercentage).coerceIn(0.0f, 1.0f)
            val totalConsumed = (newPercentage - oldPercentage) * -1
            collapsePercentage = newPercentage
            return Offset(0.0f, totalConsumed * totalCollapsableHeightPx)
        }

        override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
            return super.onPostFling(consumed, available)
        }

        override fun onPostScroll(
            consumed: Offset,
            available: Offset,
            source: NestedScrollSource
        ): Offset {
            if (consumed.y > 0) return Offset(0.0f, 0.0f)
            val availableY = -available.y
            val scrolledPercentage = availableY / totalCollapsableHeightPx
            val oldPercentage = collapsePercentage
            val newPercentage = (collapsePercentage + scrolledPercentage).coerceIn(0.0f, 1.0f)
            val totalConsumed = (newPercentage - oldPercentage) * -1
            collapsePercentage = newPercentage
            return Offset(0.0f, totalConsumed * totalCollapsableHeightPx)
        }
    }


    Box(modifier = modifier
        .onGloballyPositioned {
            screenWidthPx = it.size.width
        }
        .nestedScroll(nestedScrollConnection)

    ) {


        // Image Here

        val brush = remember {
            Brush.verticalGradient(
                0.0f to Color.Red,
                0.8f to Color.Red,
                1.0f to Color.Transparent
            )
        }
        Box(modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.0f)
            .offset {
                IntOffset(0, (-(collapsePercentage * 0.15) * screenWidthPx).toInt())
            }
            .graphicsLayer {
                alpha = (1 - collapsePercentage * 2).coerceIn(0.0f, 1.0f)
            }
        ) {
            val darkMode = isAppInDarkTheme()
            val color = remember(darkMode) {
                if (darkMode)
                    Color(0x99999999)
                else
                    Color(0xFFFFFFFF)
            }
            AsyncImage(
                model = albumSongs.firstOrNull()?.song.toSongAlbumArtModel(),
                contentDescription = "Album Art",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.0f)
                        then (if (isAppInDarkTheme()) Modifier.fadingEdge(brush) else Modifier),
                imageLoader = LocalInefficientThumbnailImageLoader.current,
                colorFilter = ColorFilter.tint(
                    color,
                    BlendMode.Multiply
                ),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .align(Alignment.BottomStart)
                    .padding(start = 16.dp, bottom = 12.dp),
            ) {
                val backgroundColor = when (isAppInDarkTheme()) {
                    true -> Color(0x00FFFFFF)
                    false -> MaterialTheme.colorScheme.surfaceDim.copy(alpha = 0.85f)
                }
                AlbumTitle(
                    modifier = Modifier
                        .background(backgroundColor)
                        .padding(1.dp),
                    name = albumInfo.name
                )
                if (!isAppInDarkTheme())
                    Spacer(modifier = Modifier.height(2.dp))
                ArtistName(
                    modifier = Modifier
                        .background(backgroundColor)
                        .padding(1.dp),
                    name = albumInfo.artist
                )
            }
        }


        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = with(density) { topBarHeightPx.toDp() })
                .offset {
                    IntOffset(0, (totalCollapsableHeightPx * (1 - collapsePercentage)).toInt())
                }
                .then(if (isAppInDarkTheme()) Modifier else Modifier.shadow((24 * (1 - collapsePercentage)).dp))

        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.padding(horizontal = 16.dp)) {
                    LargeAlbumButton(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Rounded.PlayArrow,
                        title = "Play",
                        color = MaterialTheme.colorScheme.primaryContainer,
                        onClick = actions::play
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    LargeAlbumButton(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Rounded.Shuffle,
                        title = "Shuffle",
                        color = MaterialTheme.colorScheme.primaryContainer,
                        onClick = actions::shuffle
                    )
                }
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
                            Row(
                                modifier = Modifier
                                    .horizontalScroll(rememberScrollState())
                                    .height(IntrinsicSize.Max)
                            ) {
                                otherAlbums.forEach { album ->
                                    OtherAlbum(
                                        modifier = Modifier
                                            .padding(4.dp)
                                            .fillMaxHeight()
                                            .width(IntrinsicSize.Min)
                                            .clip(RoundedCornerShape(6.dp))
                                            .clickable {
                                                onNavigateToAlbum(
                                                    album.albumInfo.name,
                                                    album.albumInfo.artist
                                                )
                                            }
                                            .padding(6.dp),
                                        album
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        AlbumDetailPortraitTopBar(
            modifier = Modifier.fillMaxWidth(),
            name = albumInfo.name,
            collapsePercentage,
            onBarHeightChanged = { topBarHeightPx = it },
            onBackClicked = onBackClicked,
            onPlayNext = actions::playNext,
            onAddToQueue = actions::addToQueue,
            onShuffleNext = actions::shuffleNext,
        )
    }


}

@Composable
fun OtherAlbum(
    modifier: Modifier,
    album: BasicAlbum
) {

    Column(modifier) {
        SongAlbumArtImage(
            modifier = Modifier
                .height(128.dp)
                .aspectRatio(1.0f)
                .clip(RoundedCornerShape(6.dp)),
            songAlbumArtModel = album.firstSong.toSongAlbumArtModel()
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = album.albumInfo.name,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }

}

@Composable
fun LargeAlbumButton(
    modifier: Modifier,
    icon: ImageVector,
    title: String,
    color: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .shadow(6.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .drawBehind { drawRect(color) }
            .clickable { onClick() }
            .padding(8.dp),
        contentAlignment = Alignment.Center) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Icon(imageVector = icon, contentDescription = "")
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = title, fontWeight = FontWeight.Light)
        }
    }
}

@Composable
fun AlbumTitle(
    modifier: Modifier,
    name: String
) {
    Text(
        modifier = modifier,
        text = name,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 26.sp,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
fun ArtistName(
    modifier: Modifier,
    name: String
) {
    Text(
        modifier = modifier,
        text = name,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

fun Modifier.fadingEdge(brush: Brush) =
    this
        .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
        .drawWithContent {
            drawContent()
            drawRect(brush = brush, blendMode = BlendMode.DstIn)
        }

@Composable
fun AlbumDetailsLoadingScreen(modifier: Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}