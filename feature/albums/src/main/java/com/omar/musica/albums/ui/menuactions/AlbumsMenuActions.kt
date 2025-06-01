package com.omar.musica.albums.ui.menuactions

import com.omar.musica.ui.menu.MenuActionItem
import com.omar.musica.ui.menu.addToPlaylists
import com.omar.musica.ui.menu.addToQueue
import com.omar.musica.ui.menu.play
import com.omar.musica.ui.menu.playNext
import com.omar.musica.ui.menu.shuffle
import com.omar.musica.ui.menu.shuffleNext

fun buildAlbumsMenuActions(
    onPlay: () -> Unit,
    addToQueue: () -> Unit,
    onPlayNext: () -> Unit,
    onShuffle: () -> Unit,
    onShuffleNext: () -> Unit,
    onAddToPlaylists: () -> Unit
): List<MenuActionItem> {
    return mutableListOf<MenuActionItem>()
        .apply {
            play(onPlay)
            addToQueue(addToQueue)
            playNext(onPlayNext)
            shuffle(onShuffle)
            shuffleNext(onShuffleNext)
            addToPlaylists(onAddToPlaylists)
        }
}