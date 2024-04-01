package com.omar.musica.albums.ui.albumsscreen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.omar.musica.store.model.album.BasicAlbum
import com.omar.musica.ui.albumart.SongAlbumArtImage
import com.omar.musica.ui.albumart.toSongAlbumArtModel
import com.omar.musica.ui.common.MultiSelectState


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AlbumsGrid(
    modifier: Modifier,
    albums: List<BasicAlbum>,
    numOfColumns: Int = 2,
    multiSelectState: MultiSelectState<BasicAlbum>,
    onAlbumClicked: (BasicAlbum) -> Unit,
    onAlbumLongClicked: (BasicAlbum) -> Unit
) {

    val listState = rememberLazyGridState()

    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(numOfColumns),
        state = listState
    ) {
        items(albums, key = { it.albumInfo.name }) {
            AlbumGridCard(
                modifier = Modifier
                    .scale(0.95f)
                    .animateItemPlacement()
                    .clip(RoundedCornerShape(16.dp))
                    .combinedClickable(
                        onLongClick = { onAlbumLongClicked(it) },
                        onClick = { onAlbumClicked(it) }
                    ),
                album = it,
                isSelected = multiSelectState.selected.contains(it)
            )
        }
    }

    LaunchedEffect(key1 = albums) {
        listState.scrollToItem(0)
    }

}


@Composable
fun AlbumGridCard(
    modifier: Modifier,
    album: BasicAlbum,
    isSelected: Boolean = false,
) {

    Card(modifier) {

        Box(modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)) {
            SongAlbumArtImage(
                modifier = Modifier
                    .fillMaxSize()
                    .aspectRatio(1f),
                songAlbumArtModel = album.firstSong.toSongAlbumArtModel()
            )
            if (isSelected) {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .aspectRatio(1f)
                    .background(Color(0x88000000)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Rounded.CheckCircle, contentDescription = "Selected")
                }
            }
        }


        Spacer(modifier = Modifier.height(8.dp))

        Text(
            modifier = Modifier.padding(start = 12.dp, end = 8.dp, top = 10.dp),
            text = album.albumInfo.name,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            modifier = Modifier.padding(start = 12.dp, end = 8.dp, bottom = 16.dp),
            text = album.albumInfo.artist,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

    }

}

