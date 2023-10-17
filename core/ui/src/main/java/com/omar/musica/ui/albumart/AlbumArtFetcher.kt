package com.omar.musica.ui.albumart

import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import androidx.core.graphics.drawable.toDrawable
import androidx.core.net.toUri
import coil.ImageLoader
import coil.decode.DataSource
import coil.decode.ImageSource
import coil.fetch.DrawableResult
import coil.fetch.FetchResult
import coil.fetch.Fetcher
import coil.fetch.SourceResult
import coil.key.Keyer
import coil.request.Options
import coil.size.pxOrElse
import com.omar.musica.ui.model.SongUi
import okio.Buffer
import okio.BufferedSource
import okio.Okio
import okio.Source
import okio.buffer
import okio.source
import timber.log.Timber
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.nio.ByteBuffer


class AlbumKeyer : Keyer<SongUi> {
    /**
     * Songs in the same album (should) have the same art work.
     * So we use the albumId as the key to use the same image
     * for all songs in the same album. If the song has no album, then use its uri as the key
     */
    override fun key(data: SongUi, options: Options): String =
        data.albumId?.toString() ?: data.uriString
}

class SongKeyer : Keyer<SongUi> {


    override fun key(data: SongUi, options: Options): String =
        data.uriString
}

class AlbumArtFetcher(
    private val data: SongUi,
    private val options: Options
) : Fetcher {

    override suspend fun fetch(): FetchResult? {


        Timber.d(
            "AlbumArtFetcher request: " +
                    "$data\n" +
                    "${options.size}"
        )

        val metadataRetriever = MediaMetadataRetriever()
            .apply { setDataSource(options.context, data.uriString.toUri()) }

        val byteArr = metadataRetriever.embeddedPicture ?: return null

        val bufferedSource = ByteArrayInputStream(byteArr).source().buffer()
        return SourceResult(
            ImageSource(bufferedSource, options.context),
            "image/*",
            DataSource.MEMORY
        )
    }


    class Factory : Fetcher.Factory<SongUi> {

        override fun create(data: SongUi, options: Options, imageLoader: ImageLoader): Fetcher {
            return AlbumArtFetcher(data, options)
        }
    }

}