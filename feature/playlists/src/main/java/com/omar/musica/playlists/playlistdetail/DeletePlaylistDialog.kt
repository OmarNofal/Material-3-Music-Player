package com.omar.musica.playlists.playlistdetail

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue



interface DeletePlaylistDialogLauncher {
    fun launch()
}


@Composable
private fun DeletePlaylistDialog(
    visible: Boolean,
    playlistName: String,
    onDismissRequest: () -> Unit,
    onDelete: () -> Unit
) {
    if (!visible) return
    AlertDialog(
        icon = { Icon(imageVector = Icons.Rounded.Delete, contentDescription = null) },
        title = { Text(text = "Delete playlist $playlistName?") },
        text = { Text(text = "This cannot be undone") },
        confirmButton = {
            TextButton(onClick = onDelete) {
                Text(text = "Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = "Cancel")
            }
        },
        onDismissRequest = onDismissRequest
    )
}


@Composable
fun rememberDeletePlaylistDialog(
    playlistName: String,
    onDelete: () -> Unit
): DeletePlaylistDialogLauncher {
    var visible by remember { mutableStateOf(false) }
    DeletePlaylistDialog(
        visible = visible,
        playlistName = playlistName,
        onDelete = { onDelete(); visible = false },
        onDismissRequest = { visible = false }
    )
    return remember {
        object : DeletePlaylistDialogLauncher {
            override fun launch() {
                visible = true
            }
        }
    }
}