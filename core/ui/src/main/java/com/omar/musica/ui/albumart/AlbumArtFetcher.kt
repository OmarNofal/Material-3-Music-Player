//package com.omar.musica.ui.albumart
//
//import android.graphics.Bitmap
//import android.graphics.BitmapFactory
//import android.media.MediaMetadataRetriever
//import android.net.Uri
//import coil.ImageLoader
//import coil.fetch.BitmapFetcher
//import coil.fetch.FetchResult
//import coil.fetch.Fetcher
//import coil.request.Options
//import coil.size.pxOrElse
//
//
//class AlbumArtFetcher(
//    private val data: Uri,
//    private val options: Options
//) : Fetcher {
//
//    override suspend fun fetch(): FetchResult? {
//
//        val metadataRetriever = MediaMetadataRetriever()
//            .apply { setDataSource(options.context, data) }
//
//        val byteArr = metadataRetriever.embeddedPicture ?: return null
//
//        val options = BitmapFactory.Options()
//            .apply {
//                outWidth = options.size.height.pxOrElse { 0 }
//                outHeight = options.size.width.pxOrElse { 0 }
//                inScaled
//            }
//        val bitmap = BitmapFactory.decodeByteArray(byteArr, 0, byteArr.size)
//
//    }
//
//
//    class Factory : Fetcher.Factory<Uri> {
//
//        fun create(data: Uri, options: Options, imageLoader: ImageLoader): Fetcher {
//            return AlbumArtFetcher(data, options)
//        }
//    }
//
//}