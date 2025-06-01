package com.omar.musica.albums.ui.albumdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.omar.musica.model.album.BasicAlbumInfo
import com.omar.musica.ui.albumart.LocalInefficientThumbnailImageLoader
import com.omar.musica.ui.albumart.SongAlbumArtModel
import com.omar.musica.ui.theme.isAppInDarkTheme


val fadeBrush = Brush.verticalGradient(
  0.0f to Color.Red,
  0.8f to Color.Red,
  1.0f to Color.Transparent
)


@Composable
internal fun AlbumArtHeader(
  modifier: Modifier = Modifier,
  songAlbumArtModel: SongAlbumArtModel,
  albumInfo: BasicAlbumInfo,
  fadeEdge: Boolean = true,
) {
  val darkMode = isAppInDarkTheme()
  val color = remember(darkMode) {
    if (darkMode)
      Color(0x99999999)
    else
      Color(0xFFFFFFFF)
  }

  Box(
    modifier = modifier
  ){
    AsyncImage(
      model = songAlbumArtModel,
      contentDescription = "Album Art",
      modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(1.0f)
        .then(if (fadeEdge) Modifier.fadingEdge(fadeBrush) else Modifier),
      imageLoader = LocalInefficientThumbnailImageLoader.current,
      colorFilter = ColorFilter.tint(
        color,
        BlendMode.Multiply
      ),
      contentScale = ContentScale.Crop,
      error = painterResource(id = com.omar.musica.ui.R.drawable.placeholder)
    )

    Column(
      modifier = Modifier
        .fillMaxWidth(0.9f)
        .align(Alignment.BottomStart)
        .padding(start = 16.dp, bottom = 12.dp),
    ) {
      AlbumTitle(
        modifier = Modifier.padding(1.dp),
        name = albumInfo.name
      )
      if (!isAppInDarkTheme()) Spacer(modifier = Modifier.height(2.dp))
      ArtistName(
        modifier = Modifier.padding(1.dp),
        name = albumInfo.artist
      )
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