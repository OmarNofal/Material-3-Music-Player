package com.omar.musica.ui.common

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.omar.musica.model.Song


fun shareSongs(context: Context, songs: List<Song>) {
    if (songs.isEmpty()) return
    if (songs.size == 1) shareSingleSong(context, songs[0])
    else shareMultipleSongs(context, songs)
}


fun shareSingleSong(context: Context, song: Song) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        putExtra(Intent.EXTRA_STREAM, song.uriString.toUri())
        type = "audio/*"
    }
    val chooser = Intent.createChooser(intent, "Share ${song.title}")
    context.startActivity(chooser)
}

fun shareMultipleSongs(context: Context, songs: List<Song>) {
    val intent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
        val uris = songs.map { it.uriString.toUri() }
        type = "audio/*"
        putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(uris))
    }
    val chooser = Intent.createChooser(intent, "Share ${songs[0].title} and ${songs.size - 1} other files")
    context.startActivity(chooser)
}