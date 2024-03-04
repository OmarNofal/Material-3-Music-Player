package com.omar.musica.tageditor.ui

import android.graphics.Bitmap
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.transition.CrossfadeTransition
import com.omar.musica.ui.R


@Composable
fun CoverArt(
    modifier: Modifier,
    bitmap: Bitmap?,
    albumName: String,
    songTitle: String,
    onUserPickedNewBitmap: (Bitmap?) -> Unit
) {

    var showArtworkDialog by remember {
        mutableStateOf(false)
    }

    CoverArtPicker(showDialog = showArtworkDialog,
        albumName = albumName,
        songTitle = songTitle,
        onUserPickedBitmap = onUserPickedNewBitmap
    ) {
        showArtworkDialog = false
    }

    val context = LocalContext.current
    Box(modifier = modifier) {
        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 4.dp)
                .aspectRatio(1.0f)
                .clip(RoundedCornerShape(12.dp))
                .align(Alignment.Center)
                .pointerInput(Unit) {
                    detectTapGestures { showArtworkDialog = true }
                },
            model = remember(bitmap) {
                ImageRequest.Builder(context)
                    .data(bitmap)
                    .transitionFactory(CrossfadeTransition.Factory(durationMillis = 400))
                    .build()
            },
            contentDescription = "Artwork",
            placeholder = ColorPainter(Color.Transparent),
            fallback = painterResource(id = R.drawable.placeholder),
            contentScale = ContentScale.Crop
        )
    }
}