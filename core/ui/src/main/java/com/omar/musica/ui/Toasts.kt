package com.omar.musica.ui

import android.content.Context
import android.widget.Toast


fun Context.showShortToast(text: String) =
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()

fun Context.showSongsAddedToNextToast(numOfSongs: Int) =
    showShortToast("$numOfSongs ${if (numOfSongs == 1) "Song" else "Songs"} will play next")

fun Context.showSongsAddedToQueueToast(numOfSongs: Int) =
    showShortToast("$numOfSongs ${if (numOfSongs == 1) "Song" else "Songs"} added to queue")

fun Context.showSongsAddedToPlaylistsToast(numOfSongs: Int, numOfPlaylists: Int) =
    showShortToast("$numOfSongs ${if(numOfSongs == 1) "Song" else "Songs"} added to ${if (numOfPlaylists == 1) "Playlist" else "Playlists"}")