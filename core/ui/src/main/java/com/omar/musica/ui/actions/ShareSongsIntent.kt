package com.omar.musica.ui.actions

import android.content.Context
import android.content.Intent
import com.omar.musica.store.model.song.Song


object SongsSharer : SongShareAction {

    override fun share(context: Context, songs: List<Song>) {
        if (songs.isEmpty()) return
        if (songs.size == 1) shareSingleSong(context, songs[0])
        else shareMultipleSongs(context, songs)
    }

}

fun shareSongs(context: Context, songs: List<Song>) {
    if (songs.isEmpty()) return
    if (songs.size == 1) shareSingleSong(context, songs[0])
    else shareMultipleSongs(context, songs)
}


fun shareSingleSong(context: Context, song: Song) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        putExtra(Intent.EXTRA_STREAM, song.uri)
        type = "audio/*"
    }
    val chooser = Intent.createChooser(intent, "Share ${song.metadata.title}")
    context.startActivity(chooser)
}

fun shareMultipleSongs(context: Context, songs: List<Song>) {
    val intent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
        val uris = songs.map { it.uri }
        type = "audio/*"
        putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(uris))
    }
    val chooser =
        Intent.createChooser(
            intent,
            "Share ${songs[0].metadata.title} and ${songs.size - 1} other files"
        )
    context.startActivity(chooser)
}