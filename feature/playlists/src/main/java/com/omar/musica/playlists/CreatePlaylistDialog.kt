package com.omar.musica.playlists

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlaylistAdd
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.hilt.navigation.compose.hiltViewModel


@Composable
fun CreatePlaylistDialog(
    viewModel: CreatePlaylistViewModel = hiltViewModel(),
    onDismissRequest: () -> Unit
) {

    val playlistNames by viewModel.currentPlaylists.collectAsState(initial = listOf())
    var currentName by rememberSaveable {
        mutableStateOf("")
    }

    val isError by remember {
        derivedStateOf { currentName.isNotBlank() && currentName in playlistNames }
    }

    val focusRequester = remember { FocusRequester() }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        icon = { Icon(imageVector = Icons.Rounded.PlaylistAdd, contentDescription = null) },
        title = { Text(text = "New Playlist") },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = "Cancel")
            }
        },
        confirmButton = {
            TextButton(onClick = { viewModel.onInsertPlaylist(currentName); onDismissRequest()}, enabled = !isError) {
                Text(text = "Create")
            }
        },
        text = {
            TextField(
                modifier = Modifier.focusRequester(focusRequester),
                value = currentName,
                onValueChange = { currentName = it },
                isError = isError
            )
        }
    )

    LaunchedEffect(key1 = Unit, block = { focusRequester.requestFocus() })

}

@Composable
fun rememberCreatePlaylistDialog(): PlaylistDialog {

    var isVisible by remember {
        mutableStateOf(false)
    }

    if (isVisible)
        CreatePlaylistDialog {
            isVisible = false
        }

    return remember {
        object : PlaylistDialog {
            override fun launch() {
                isVisible = true
            }
        }
    }
}

interface PlaylistDialog {
    fun launch()
}