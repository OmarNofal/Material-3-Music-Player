package com.omar.musica.playlists.playlists

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.omar.musica.model.PlaylistInfo
import com.omar.musica.ui.albumart.LocalThumbnailImageLoader


@Composable
fun PlaylistRow(
    modifier: Modifier,
    playlistInfo: PlaylistInfo,
) {

    Row(
        modifier = modifier.padding(
            start = 12.dp,
            end = 12.dp,
            top = 12.dp,
            bottom = 12.dp
        ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        PlaylistImage()
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = playlistInfo.name,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = playlistInfo.numberOfSongs.toString() + " songs",
                fontWeight = FontWeight.Normal,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }

}

@Composable
fun PlaylistImage() {
    AsyncImage(
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(6.dp)),
        model = null,
        imageLoader = LocalThumbnailImageLoader.current,
        contentDescription = "Playlist Photo",
        contentScale = ContentScale.Crop,
        error = rememberVectorPainter(image = Icons.Rounded.LibraryMusic)
    )
}
