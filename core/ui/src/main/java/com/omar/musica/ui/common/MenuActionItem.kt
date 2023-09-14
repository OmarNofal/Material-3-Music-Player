package com.omar.musica.ui.common

data class MenuActionItem(
    val title: String,
    val callback: () -> Unit
)


fun MutableList<MenuActionItem>.deleteAction(callback: () -> Unit)
    = add(MenuActionItem("Delete", callback))

fun MutableList<MenuActionItem>.playNext(callback: () -> Unit)
    = add(MenuActionItem("Play Next", callback))

fun MutableList<MenuActionItem>.addToPlaylists(callback: () -> Unit)
    = add(MenuActionItem("Add to Playlists", callback))

fun MutableList<MenuActionItem>.share(callback: () -> Unit)
    = add(MenuActionItem("Share", callback))

fun MutableList<MenuActionItem>.songInfo(callback: () -> Unit)
    = add(MenuActionItem("Song Info", callback))