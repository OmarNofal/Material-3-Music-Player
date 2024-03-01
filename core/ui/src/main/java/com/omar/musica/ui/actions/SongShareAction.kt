package com.omar.musica.ui.actions

import android.content.Context
import com.omar.musica.store.model.song.Song


fun interface SongShareAction {

    fun share(context: Context, songs: List<Song>)

}