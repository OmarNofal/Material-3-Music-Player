package com.omar.musica.store

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import com.omar.musica.model.song.BasicSongMetadata
import com.omar.musica.model.song.ExtendedSongMetadata
import com.omar.musica.store.model.tags.SongTags
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.TagOptionSingleton
import org.jaudiotagger.tag.images.AndroidArtwork
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random


@Singleton
class TagsRepository @Inject constructor(
  @ApplicationContext val context: Context,
  val mediaRepository: MediaRepository,
) {


  suspend fun getSongTags(songUri: Uri): SongTags = withContext(Dispatchers.IO) {
    val metadataRetriever = MediaMetadataRetriever().apply { setDataSource(context, songUri) }
    val title =
      metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) ?: ""
    val artist =
      metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST) ?: ""
    val album =
      metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM) ?: ""
    val albumArtist =
      metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST) ?: ""
    val composer =
      metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_COMPOSER) ?: ""
    val genre = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE) ?: ""
    var year = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE) ?: ""
    if (year.isEmpty()) {
      year = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR) ?: ""
    }
    val trackNumber = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER) ?: ""
    val discNumber =
      metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DISC_NUMBER) ?: ""

    val artwork: Bitmap? = metadataRetriever.embeddedPicture?.let {
      return@let BitmapFactory.decodeByteArray(it, 0, it.size)
    }

    SongTags(
      songUri,
      artwork,
      ExtendedSongMetadata(
        BasicSongMetadata(title, artist, album, 0, 0),
        albumArtist, composer, trackNumber, discNumber, genre, year, ""
      )
    )
  }

  /**
   * Edits the tag of some song to [songTags].
   * This doesn't check for permission. Instead, permission should be checked in the UI
   * before calling this method
   */
  suspend fun editTags(uri: Uri, songTags: SongTags) = withContext(Dispatchers.IO) {
    TagOptionSingleton.getInstance().isAndroid = true;

    val basicMetadata = songTags.metadata.basicSongMetadata
    val artwork = songTags.artwork
    val extendedMetadata = songTags.metadata

    val bitmapTempFile =
      if (artwork != null)
        context.saveTempBitmapForArtwork(artwork)
      else null

    val songAndroidArtwork = if (artwork != null)
      AndroidArtwork.createArtworkFromFile(bitmapTempFile)
    else
      null

    val filePath = mediaRepository.getSongPath(uri)
    val originalSongFile = File(filePath)

    val audioFileIO = AudioFileIO.read(originalSongFile)
    val newTag = audioFileIO.tagOrCreateAndSetDefault.run {
      setField(FieldKey.TITLE, basicMetadata.title)
      setField(FieldKey.ARTIST, basicMetadata.artistName)
      setField(FieldKey.ALBUM, basicMetadata.albumName)
      setField(FieldKey.ALBUM_ARTIST, extendedMetadata.albumArtist)
      setField(FieldKey.GENRE, extendedMetadata.genre)
      setField(FieldKey.YEAR, extendedMetadata.year)
      setField(FieldKey.ORIGINAL_YEAR, extendedMetadata.year)
      setField(FieldKey.COMPOSER, extendedMetadata.composer)
      setField(FieldKey.TRACK, extendedMetadata.trackNumber)
      setField(FieldKey.DISC_NO, extendedMetadata.discNumber)
      deleteArtworkField()
      if (songAndroidArtwork != null)
        setField(songAndroidArtwork)
      this
    }
    audioFileIO.tag = newTag

    // need to save song in app-specific directory and then copy it to original dir
    // because the tagging library has to create temp files and this is forbidden in android R :(
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      val cacheFile = File(context.cacheDir, originalSongFile.name)
      cacheFile.outputStream().use { cache ->
        originalSongFile.inputStream().use {
          it.copyTo(cache)
        }
      }
      audioFileIO.file = cacheFile
      audioFileIO.commit()
      // copy back to the original directory ( I hate android R)
      // In Android Q, for some unknown reason, using the File Api directly results in permission
      // denied, even though we have requestLegacyExternalStorage enabled
      // but opening it with contentResolver works for some reason ¯\_(ツ)_/¯
      if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
        val os = context.contentResolver.openOutputStream(uri)!!
        os.use { stream ->
          cacheFile.inputStream().use {
            it.copyTo(stream)
          }
        }
      } else {
        originalSongFile.outputStream().use { original ->
          cacheFile.inputStream().use {
            it.copyTo(original)
          }
        }
      }
      cacheFile.delete()
    } else {
      audioFileIO.commit()
    }
    bitmapTempFile?.delete()
    MediaScannerConnection.scanFile(context, arrayOf(filePath), null, null)
  }


  private suspend fun Context.saveTempBitmapForArtwork(bitmap: Bitmap): File =
    withContext(Dispatchers.IO) {
      val tempFile = File(cacheDir, "album_art_temp_${Random.nextLong()}")
      tempFile.outputStream().use {
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
      }
      tempFile
    }

}