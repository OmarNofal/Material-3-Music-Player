package com.omar.musica.ui.albumart

import android.content.Context
import androidx.compose.runtime.staticCompositionLocalOf
import coil.ImageLoader
import coil.transition.CrossfadeTransition
import java.lang.IllegalStateException


fun Context.thumbnailImageLoader() = ImageLoader.Builder(this)
    .transitionFactory(CrossfadeTransition.Factory())
        .components {
            add(SongKeyer())
            add(AlbumArtFetcher.Factory())
        }.build()

val LocalThumbnailImageLoader = staticCompositionLocalOf<ImageLoader> { throw IllegalStateException() }