package com.omar.musica.albums.ui.albumdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Shadow
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
  // Scrim color: typically a semi-transparent black or dark gray
  // You might want to adjust alpha based on testing
  val scrimColor = Color.Black.copy(alpha = 0.6f) // Or use a theme-aware color

  Box(
    modifier = modifier
  ) {
    AsyncImage(
      model = songAlbumArtModel,
      contentDescription = "Album Art",
      modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(1.0f)
        .then(if (fadeEdge) Modifier.fadingEdge(fadeBrush) else Modifier),
      imageLoader = LocalInefficientThumbnailImageLoader.current,
      // You can keep the tint or remove it if the scrim is strong enough
      colorFilter = ColorFilter.tint(
        if (darkMode) Color(0x99999999) else Color(0xBBFFFFFF), // Adjusted alpha for better base
        BlendMode.Multiply // Multiply mode darkens the image
      ),
      contentScale = ContentScale.Crop,
      error = painterResource(id = com.omar.musica.ui.R.drawable.placeholder)
    )
    // Gradient Scrim
    Box(
      modifier = Modifier
        .align(Alignment.BottomCenter) // Align to bottom
        .fillMaxWidth()
        .height(120.dp) // Adjust height to cover text area sufficiently
        .background(
          Brush.verticalGradient(
            colors = listOf(
              Color.Transparent,
              scrimColor.copy(alpha = 0.5f), // Start with lower alpha
              scrimColor // End with scrimColor
            ),
            // You can adjust startY and endY if needed
            // startY = 0.0f, // Start from top of this Box
            // endY = Float.POSITIVE_INFINITY // Extend to bottom of this Box
          )
        )
    )
    Column(
      modifier = Modifier
        .fillMaxWidth(0.9f)
        .align(Alignment.BottomStart)
        .padding(start = 16.dp, bottom = 25.dp),
    ) {
      AlbumTitle(
        modifier = Modifier.padding(horizontal = 1.dp), // Added vertical padding
        name = albumInfo.name
      )
      // Spacer(modifier = Modifier.height(2.dp)) // Consider removing if vertical padding added to Text
      ArtistName(
        modifier = Modifier.padding(horizontal = 1.dp), // Added vertical padding
        name = albumInfo.artist
      )
    }
  }
}

// AlbumTitle and ArtistName - Consider adding text shadows
@Composable
fun AlbumTitle(
  modifier: Modifier,
  name: String
) {
  Text(
    modifier = modifier,
    text = name,
    fontWeight = FontWeight.ExtraBold,
    fontSize = 40.sp,
    lineHeight = 40.sp,
    maxLines = 3,
    color = Color.White,
    overflow = TextOverflow.Ellipsis,
    // Optional: Add shadow for better readability
//    style = LocalTextStyle.current.copy(
//      shadow = Shadow(
//          color = Color.Black.copy(alpha = 0.7f),
//          offset = Offset(1f, 1f),
//          blurRadius = 2f
//      )
//    )
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
    fontSize = 18.sp,
    maxLines = 1,
    color = Color.White,
    overflow = TextOverflow.Ellipsis,
    // Optional: Add shadow for better readability
     style = LocalTextStyle.current.copy(
        shadow = Shadow(
            color = Color.Black.copy(alpha = 0.5f),
            offset = Offset(1f, 1f),
            blurRadius = 2f
        )
     )
  )
}

fun Modifier.fadingEdge(brush: Brush) =
  this
    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
    .drawWithContent {
      drawContent()
      drawRect(brush = brush, blendMode = BlendMode.DstIn)
    }