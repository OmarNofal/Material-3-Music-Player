package com.omar.musica.ui.albumart

import android.content.Context
import androidx.compose.runtime.staticCompositionLocalOf
import coil.ImageLoader
import kotlinx.coroutines.Dispatchers


fun Context.efficientAlbumArtImageLoader() = ImageLoader.Builder(this)
    .components {
        add(AlbumKeyer())
        add(AlbumArtFetcher.Factory())
    }.build()

fun Context.inefficientAlbumArtImageLoader() = ImageLoader.Builder(this)
    .dispatcher(Dispatchers.IO.limitedParallelism(5))
    .components {
        add(SongKeyer())
        add(AlbumArtFetcher.Factory())
    }.build()

val LocalEfficientThumbnailImageLoader =
    staticCompositionLocalOf<ImageLoader> { throw IllegalStateException() }
val LocalInefficientThumbnailImageLoader =
    staticCompositionLocalOf<ImageLoader> { throw IllegalStateException() }