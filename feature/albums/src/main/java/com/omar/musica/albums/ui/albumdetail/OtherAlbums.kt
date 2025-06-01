package com.omar.musica.albums.ui.albumdetail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.omar.musica.store.model.album.BasicAlbum
import com.omar.musica.ui.albumart.SongAlbumArtImage
import com.omar.musica.ui.albumart.toSongAlbumArtModel


@Composable
fun OtherAlbumsRow(
  modifier: Modifier,
  otherAlbums: List<BasicAlbum>,
  onAlbumClicked: (albumId: Int) -> Unit
) {
  Row(
    modifier = modifier
      .horizontalScroll(rememberScrollState())
  ) {
    otherAlbums.forEach { album ->
      OtherAlbum(
        modifier = Modifier
          .padding(4.dp)
          .fillMaxHeight()
          .width(IntrinsicSize.Min)
          .clip(RoundedCornerShape(6.dp))
          .clickable {
            onAlbumClicked(
              album.albumInfo.id
            )
          }
          .padding(6.dp),
        album
      )
    }
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