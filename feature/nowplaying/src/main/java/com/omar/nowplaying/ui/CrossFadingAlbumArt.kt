package com.omar.nowplaying.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.omar.musica.store.model.song.Song
import com.omar.musica.ui.R
import com.omar.musica.ui.albumart.BlurTransformation
import com.omar.musica.ui.albumart.LocalInefficientThumbnailImageLoader
import com.omar.musica.ui.albumart.SongAlbumArtModel
import com.omar.musica.ui.albumart.toSongAlbumArtModel
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min


enum class ErrorPainterType {
    PLACEHOLDER, SOLID_COLOR
}

@Composable
fun CrossFadingAlbumArt(
    modifier: Modifier,
    containerModifier: Modifier = Modifier,
    songAlbumArtModel: SongAlbumArtModel,
    errorPainterType: ErrorPainterType,
    colorFilter: ColorFilter? = null,
    blurTransformation: BlurTransformation? = null,
    contentScale: ContentScale = ContentScale.Crop
) {


    val context = LocalContext.current
    val imageRequest = remember(songAlbumArtModel.uri.toString()) {
        ImageRequest.Builder(context)
            .data(songAlbumArtModel)
            .apply { if (blurTransformation != null) this.transformations(blurTransformation) }
            .size(Size.ORIGINAL).build()
    }

    var firstPainter by remember {
        mutableStateOf<Painter>(ColorPainter(Color.Black))
    }

    var secondPainter by remember {
        mutableStateOf<Painter>(ColorPainter(Color.Black))
    }

    var isUsingFirstPainter by remember {
        mutableStateOf(true)
    }

    val solidColorPainter = remember { ColorPainter(Color.Black) }
    val placeholderPainter = painterResource(id = R.drawable.placeholder)

    rememberAsyncImagePainter(
        model = imageRequest,
        contentScale = ContentScale.Crop,
        imageLoader = LocalInefficientThumbnailImageLoader.current,
        onState = {
            when (it) {
                is AsyncImagePainter.State.Success -> {
                    val newPainter = it.painter
                    if (isUsingFirstPainter) {
                        secondPainter = newPainter
                    } else {
                        firstPainter = newPainter
                    }
                    isUsingFirstPainter = !isUsingFirstPainter
                }

                is AsyncImagePainter.State.Error -> {
                    if (isUsingFirstPainter) {
                        secondPainter =
                            if (errorPainterType == ErrorPainterType.PLACEHOLDER) placeholderPainter
                            else solidColorPainter
                    } else {
                        firstPainter =
                            if (errorPainterType == ErrorPainterType.PLACEHOLDER) placeholderPainter
                            else solidColorPainter
                    }
                    isUsingFirstPainter = !isUsingFirstPainter
                }

                else -> {

                }
            }
        }
    )

    Crossfade(modifier = containerModifier, targetState = isUsingFirstPainter, label = "") {

        Image(
            modifier = modifier,
            painter = if (it) firstPainter else secondPainter,
            contentDescription = null,
            colorFilter = colorFilter,
            contentScale = contentScale
        )

    }
}


@Composable
fun MorphingBlurredAlbumArtBackground(
    modifier: Modifier,
    songs: List<Song>,
    currentIndex: Int,
    swipeOffsetProvider: (Int) -> Float
) {

    Box(modifier) {

        BlurredAlbumArt(
            Modifier
                .fillMaxSize()
                .graphicsLayer {
                    alpha = -1 * min(0.0f, swipeOffsetProvider(currentIndex - 1))
                }, song = songs.getOrNull(currentIndex - 1)?.toSongAlbumArtModel()
        )

        BlurredAlbumArt(
            Modifier
                .fillMaxSize()
                .graphicsLayer {
                    alpha = 1 - abs(swipeOffsetProvider(currentIndex))
                }, song = songs.getOrNull(currentIndex)?.toSongAlbumArtModel()
        )

        BlurredAlbumArt(
            Modifier
                .fillMaxSize()
                .graphicsLayer {
                    alpha = max(0.0f, swipeOffsetProvider(currentIndex + 1))
                }, song = songs.getOrNull(currentIndex + 1)?.toSongAlbumArtModel()
        )

    }

}

@Composable
fun BlurredAlbumArt(modifier: Modifier, song: SongAlbumArtModel?) {

    val context = LocalContext.current
    val imageRequest = remember(song?.uri.toString()) {
        ImageRequest.Builder(context)
            .data(song)
            .apply { this.transformations(BlurTransformation(25, 0.2f)) }
            .size(Size.ORIGINAL).build()
    }

    AsyncImage(
        modifier = modifier,
        model = imageRequest,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        error = remember { ColorPainter(Color.Black) },
        imageLoader = LocalInefficientThumbnailImageLoader.current,
        colorFilter = remember {
            ColorFilter.tint(
                Color(0xFFEEEEEE),
                BlendMode.Multiply
            )
        }
    )
}


