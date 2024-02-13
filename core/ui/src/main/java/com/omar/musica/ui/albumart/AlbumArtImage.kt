package com.omar.musica.ui.albumart

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.omar.musica.ui.R
import com.omar.musica.ui.albumart.LocalEfficientThumbnailImageLoader
import com.omar.musica.ui.albumart.LocalInefficientThumbnailImageLoader
import com.omar.musica.ui.model.SongUi


@Composable
fun SongAlbumArtImage(
    modifier: Modifier,
    song: SongUi
) {
    val context = LocalContext.current
    val imageRequest = remember(song) {
        ImageRequest.Builder(context)
            .data(song)
            .crossfade(true).build()
    }
    AsyncImage(
        modifier = modifier,
        model = imageRequest,
        contentDescription = "Artwork",
        contentScale = ContentScale.Crop,
        imageLoader = LocalInefficientThumbnailImageLoader.current,
        error = painterResource(id = R.drawable.placeholder),
        placeholder = painterResource(id = R.drawable.placeholder)
    )
}