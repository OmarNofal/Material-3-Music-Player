package com.omar.musica.ui.actions

import android.net.Uri
import com.omar.musica.store.model.song.Song

interface GoToAlbumAction {
  fun open(song: Song)
}