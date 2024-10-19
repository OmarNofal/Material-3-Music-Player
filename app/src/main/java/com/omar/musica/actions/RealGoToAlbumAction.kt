package com.omar.musica.actions

import androidx.navigation.NavController
import com.omar.musica.albums.navigation.navigateToAlbumDetail
import com.omar.musica.store.AlbumsRepository
import com.omar.musica.store.model.song.Song
import com.omar.musica.ui.actions.GoToAlbumAction

class RealGoToAlbumAction(
    private val albumsRepository: AlbumsRepository,
    private val navController: NavController
) : GoToAlbumAction {

    override fun open(song: Song) {
        val albumId = albumsRepository.getSongAlbumId(song) ?: return
        navController.navigateToAlbumDetail(albumId)
    }

}