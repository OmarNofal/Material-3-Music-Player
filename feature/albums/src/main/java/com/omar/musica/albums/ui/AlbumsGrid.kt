package com.omar.musica.albums.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.omar.musica.store.model.album.BasicAlbum
import com.omar.musica.ui.albumart.SongAlbumArtImage
import com.omar.musica.ui.albumart.toSongAlbumArtModel


@Composable
fun AlbumsGrid(
    modifier: Modifier,
    albums: List<BasicAlbum>,
    numOfColumns: Int = 2
) {

    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(numOfColumns),
    ) {
        items(albums, key = {it.albumInfo.name}) {
            AlbumGridCard(
                modifier = Modifier.scale(0.95f).clip(RoundedCornerShape(16.dp)).clickable {  },
                album = it
            )
        }
    }

}


@Composable
fun AlbumGridCard(
    modifier: Modifier,
    album: BasicAlbum
) {

    Card(modifier) {
        SongAlbumArtImage(
            modifier = Modifier
                .aspectRatio(1f),
            songAlbumArtModel = album.firstSong.toSongAlbumArtModel()
        )

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

