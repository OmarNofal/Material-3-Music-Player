package com.omar.musica.store

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import com.omar.musica.model.Song


class MediaRepository(private val context: Context) {


    fun getAllSongs(): List<Song> {

        val projection =
            arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media.ALBUM
            )

        return with(context) {

            val cursor = contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                null, null, null, null
            ) ?: throw Exception("Invalid cursor")


            val results = mutableListOf<Song>()
            cursor.use { c ->
                while (c.moveToNext()) {
                    val idColumn = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
                    val fileNameColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)
                    val titleColumn = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
                    val artistColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
                    val durationColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)
                    val sizeColumn = cursor.getColumnIndex(MediaStore.Audio.Media.SIZE)
                    val pathColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DATA)
                    val albumColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)
                    val song = Song(
                        title = c.getString(titleColumn),
                        artist = c.getString(artistColumn),
                        album = c.getString(albumColumn),
                        length = c.getLong(durationColumn),
                        location = c.getString(pathColumn),
                        size = c.getLong(sizeColumn),
                        fileName = cursor.getString(fileNameColumn),
                        uriString = ContentUris.withAppendedId(
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                            cursor.getInt(idColumn).toLong()
                        ).toString()
                    ).also(results::add)
                }
            }

            results
        }
    }


}