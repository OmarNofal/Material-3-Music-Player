package com.omar.musica.ui.albumart

import android.media.MediaMetadataRetriever
import coil.ImageLoader
import coil.decode.DataSource
import coil.decode.ImageSource
import coil.fetch.FetchResult
import coil.fetch.Fetcher
import coil.fetch.SourceResult
import coil.key.Keyer
import coil.request.Options
import okio.buffer
import okio.source
import timber.log.Timber
import java.io.ByteArrayInputStream


class AlbumKeyer : Keyer<SongAlbumArtModel> {
  /**
   * Songs in the same album (should) have the same art work.
   * So we use the albumId as the key to use the same image
   * for all songs in the same album. If the song has no album, then use its uri as the key
   */
  override fun key(data: SongAlbumArtModel, options: Options): String =
    data.albumId?.toString() ?: data.uri.toString()
}

class SongKeyer : Keyer<SongAlbumArtModel> {


  override fun key(data: SongAlbumArtModel, options: Options): String =
    data.uri.toString()
}

class AlbumArtFetcher(
  private val data: SongAlbumArtModel,
  private val options: Options
) : Fetcher {

  override suspend fun fetch(): FetchResult? {


    Timber.d(
      "%snull", "AlbumArtFetcher request: " +
        "$data\n"
    )

    val metadataRetriever = MediaMetadataRetriever()
      .apply { setDataSource(options.context, data.uri) }

    val byteArr = metadataRetriever.embeddedPicture ?: return null

    val bufferedSource = ByteArrayInputStream(byteArr).source().buffer()
    return SourceResult(
      ImageSource(bufferedSource, options.context),
      "image/*",
      DataSource.MEMORY
    )
  }


  class Factory : Fetcher.Factory<SongAlbumArtModel> {

    override fun create(
      data: SongAlbumArtModel,
      options: Options,
      imageLoader: ImageLoader
    ): Fetcher {
      return AlbumArtFetcher(data, options)
    }
  }

}