package com.omar.musica.ui.albumart

import android.content.Context
import androidx.compose.runtime.staticCompositionLocalOf
import coil.ImageLoader
import java.lang.IllegalStateException


fun Context.thumbnailImageLoader() = ImageLoader.Builder(this)
        .components {
            add(SongKeyer())
            add(AlbumArtFetcher.Factory())
        }.build()

val LocalThumbnailImageLoader = staticCompositionLocalOf<ImageLoader> { throw IllegalStateException() }