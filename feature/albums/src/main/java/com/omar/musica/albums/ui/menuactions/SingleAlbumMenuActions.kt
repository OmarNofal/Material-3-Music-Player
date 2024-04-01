package com.omar.musica.albums.ui.menuactions

import com.omar.musica.ui.menu.MenuActionItem
import com.omar.musica.ui.menu.addToPlaylists
import com.omar.musica.ui.menu.addToQueue
import com.omar.musica.ui.menu.playNext
import com.omar.musica.ui.menu.shuffleNext


fun buildSingleAlbumMenuActions(
    onPlayNext: () -> Unit,
    addToQueue: () -> Unit,
    onShuffleNext: () -> Unit,
    onAddToPlaylists: () -> Unit
): List<MenuActionItem> {
    return mutableListOf<MenuActionItem>()
        .apply {
            playNext(onPlayNext)
            addToQueue(addToQueue)
            shuffleNext(onShuffleNext)
            addToPlaylists(onAddToPlaylists)
        }
}