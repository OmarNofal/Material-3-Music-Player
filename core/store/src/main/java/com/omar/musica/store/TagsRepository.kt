package com.omar.musica.store

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import androidx.core.text.isDigitsOnly
import com.omar.musica.model.song.BasicSongMetadata
import com.omar.musica.model.song.ExtendedSongMetadata
import com.omar.musica.store.model.tags.SongTags
import com.shabinder.jaudiotagger.audio.AudioFileIO
import com.shabinder.jaudiotagger.tag.FieldKey
import com.shabinder.jaudiotagger.tag.TagOptionSingleton
import com.shabinder.jaudiotagger.tag.flac.FlacTag
import com.shabinder.jaudiotagger.tag.id3.valuepair.ImageFormats
import com.shabinder.jaudiotagger.tag.images.AndroidArtwork
import com.shabinder.jaudiotagger.tag.reference.PictureTypes
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random


@Singleton
class TagsRepository @Inject constructor(
    @ApplicationContext val context: Context,
    val mediaRepository: MediaRepository,
) {


    suspend fun getSongTags(songUri: Uri): SongTags = withContext(Dispatchers.IO) {
        val songPath = mediaRepository.getSongPath(songUri)
        val audioFile = AudioFileIO.read(File(songPath))
        val tags = audioFile.tagOrCreateAndSetDefault
        val audioHeader = audioFile.audioHeader

        val title = tags.getFirst(FieldKey.TITLE).orEmpty()
        val artist = tags.getFirst(FieldKey.ARTIST).orEmpty()
        val album = tags.getFirst(FieldKey.ALBUM).orEmpty()
        val albumArtist = tags.getFirst(FieldKey.ALBUM_ARTIST).orEmpty()
        val composer = tags.getFirst(FieldKey.COMPOSER).orEmpty()
        val genre = tags.getFirst(FieldKey.GENRE).orEmpty()
        val year = tags.getFirst(FieldKey.YEAR).orEmpty()
        val trackNumber = tags.getFirst(FieldKey.TRACK).orEmpty()
        val discNumber = tags.getFirst(FieldKey.DISC_NO).orEmpty()
        val lyrics = tags.getFirst(FieldKey.LYRICS).orEmpty()

        val durationMillis = audioHeader?.trackLength?.times(1000L) ?: 0L

        val artwork: Bitmap? = tags.firstArtwork?.binaryData?.let {
            BitmapFactory.decodeByteArray(it, 0, it.size)
        }

        Timber.tag("SONG LYRICS").d(lyrics)

        SongTags(
            songUri,
            artwork,
            ExtendedSongMetadata(
                BasicSongMetadata(title, artist, album, durationMillis, 0),
                albumArtist, composer, trackNumber, discNumber, genre, year, lyrics
            )
        )
    }


    /**
     * Edits the tag of some song to [songTags].
     * This doesn't check for permission. Instead, permission should be checked in the UI
     * before calling this method
     */
    suspend fun editTags(uri: Uri, songTags: SongTags) = withContext(Dispatchers.IO) {
        TagOptionSingleton.getInstance().isTruncateTextWithoutErrors = true

        val basicMetadata = songTags.metadata.basicSongMetadata
        val artwork = songTags.artwork
        val extendedMetadata = songTags.metadata

        // Save artwork temporarily if present
        val bitmapTempFile =
            if (artwork != null) context.saveTempBitmapForArtwork(artwork) else null
        val songAndroidArtwork =
            if (artwork != null) AndroidArtwork.createArtworkFromFile(bitmapTempFile) else null

        val originalFilePath = mediaRepository.getSongPath(uri)
        val originalFile = File(originalFilePath)

        // Read audio file and update tags
        val audioFileIO = AudioFileIO.read(originalFile)
        val newTag = audioFileIO.tagOrCreateAndSetDefault.run {
            setField(FieldKey.TITLE, basicMetadata.title)
            setField(FieldKey.ARTIST, basicMetadata.artistName)
            setField(FieldKey.ALBUM, basicMetadata.albumName)
            setField(FieldKey.ALBUM_ARTIST, extendedMetadata.albumArtist)
            setField(FieldKey.GENRE, extendedMetadata.genre)

            if (extendedMetadata.year.isNotEmpty() && extendedMetadata.year.isDigitsOnly())
                setField(FieldKey.YEAR, extendedMetadata.year)

            setField(FieldKey.COMPOSER, extendedMetadata.composer)

            if (extendedMetadata.trackNumber.isNotEmpty() && extendedMetadata.trackNumber.isDigitsOnly())
                setField(FieldKey.TRACK, extendedMetadata.trackNumber)

            if (extendedMetadata.discNumber.isNotEmpty() && extendedMetadata.discNumber.isDigitsOnly())
                setField(FieldKey.DISC_NO, extendedMetadata.discNumber)

            setField(FieldKey.LYRICS, extendedMetadata.lyrics)

            this
        }

        // save artwork
        if (newTag is FlacTag) {

            val flacTag = newTag
            flacTag.deleteArtworkField()

            if (songTags.artwork != null) {
                flacTag.setField(
                    flacTag.createArtworkField(
                        songTags.artwork.toByteArray(),
                        PictureTypes.DEFAULT_ID,
                        ImageFormats.MIME_TYPE_JPEG,
                        "artwork",
                        songTags.artwork.width,
                        songTags.artwork.height,
                        24,
                        0
                    )
                )
            }

        } else {
            // For MP3 and others, use standard artwork setting
            newTag.deleteArtworkField()
            if (songAndroidArtwork != null) {
                newTag.setField(songAndroidArtwork)
            }
        }
        audioFileIO.tag = newTag

        // Save to temp cache file first (important for Android 10+ and to avoid partial writes)
        val cacheFile = File(context.cacheDir, originalFile.name)
        originalFile.inputStream().use { input ->
            cacheFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        audioFileIO.file = cacheFile
        audioFileIO.commit()

        // Now write back to original location depending on Android version:
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // For Android 10 and above, write via ContentResolver with the Uri
            context.contentResolver.openOutputStream(uri)?.use { out ->
                cacheFile.inputStream().use { input ->
                    input.copyTo(out)
                }
            } ?: throw IOException("Unable to open output stream for URI: $uri")
        } else {
            // For Android 9 and below, write directly to the file system path
            if (!originalFile.canWrite()) {
                throw SecurityException("No write permission for file: ${originalFile.path}. Make sure WRITE_EXTERNAL_STORAGE is granted.")
            }

            // Delete the original file before copying (required to avoid FileAlreadyExistsException)
            if (!originalFile.delete()) {
                throw IOException("Failed to delete original file: ${originalFile.path}")
            }

            // Copy the modified file back to original location
            cacheFile.copyTo(originalFile)
        }

        // Clean up temp files
        cacheFile.delete()
        bitmapTempFile?.delete()

        // Trigger media scanner so updated tags show up in media apps
        MediaScannerConnection.scanFile(context, arrayOf(originalFilePath), null, null)
    }


    private suspend fun Context.saveTempBitmapForArtwork(bitmap: Bitmap): File =
        withContext(Dispatchers.IO) {
            val tempFile = File(cacheDir, "album_art_temp_${Random.nextLong()}")
            tempFile.outputStream().use {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            }
            tempFile
        }

    fun Bitmap.toByteArray(
        format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
        quality: Int = 90
    ): ByteArray {
        val stream = ByteArrayOutputStream()
        this.compress(format, quality, stream)
        return stream.toByteArray()
    }

}