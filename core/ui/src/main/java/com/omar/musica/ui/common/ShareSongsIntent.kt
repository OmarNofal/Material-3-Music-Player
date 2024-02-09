package com.omar.musica.ui.common

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.omar.musica.ui.actions.SongShareAction
import com.omar.musica.ui.model.SongUi


object SongsSharer : SongShareAction {

    override fun share(context: Context, songs: List<SongUi>) {
        if (songs.isEmpty()) return
        if (songs.size == 1) shareSingleSong(context, songs[0])
        else shareMultipleSongs(context, songs)
    }

}

fun shareSongs(context: Context, songs: List<SongUi>) {
    if (songs.isEmpty()) return
    if (songs.size == 1) shareSingleSong(context, songs[0])
    else shareMultipleSongs(context, songs)
}


fun shareSingleSong(context: Context, song: SongUi) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        putExtra(Intent.EXTRA_STREAM, song.uriString.toUri())
        type = "audio/*"
    }
    val chooser = Intent.createChooser(intent, "Share ${song.title}")
    context.startActivity(chooser)
}

fun shareMultipleSongs(context: Context, songs: List<SongUi>) {
    val intent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
        val uris = songs.map { it.uriString.toUri() }
        type = "audio/*"
        putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(uris))
    }
    val chooser =
        Intent.createChooser(intent, "Share ${songs[0].title} and ${songs.size - 1} other files")
    context.startActivity(chooser)
}