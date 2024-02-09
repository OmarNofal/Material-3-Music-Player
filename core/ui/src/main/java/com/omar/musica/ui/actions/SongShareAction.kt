package com.omar.musica.ui.actions

import android.content.Context
import com.omar.musica.ui.model.SongUi


fun interface SongShareAction {

    fun share(context: Context, songs: List<SongUi>)

}