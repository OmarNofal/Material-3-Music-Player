package com.omar.musica.ui.playlist

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.PlaylistAdd
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusRequester
import androidx.hilt.navigation.compose.hiltViewModel
import com.omar.musica.ui.dialogs.InputStringDialog


@Composable
fun CreatePlaylistDialog(
  viewModel: CreatePlaylistViewModel = hiltViewModel(),
  onDismissRequest: () -> Unit
) {
  val playlistNames by viewModel.currentPlaylists.collectAsState(initial = listOf())
  val focusRequester = remember { FocusRequester() }
  InputStringDialog(
    title = "New Playlist",
    placeholder = "Playlist Name...",
    icon = Icons.AutoMirrored.Rounded.PlaylistAdd,
    isInputValid = { input -> input.isNotBlank() && input !in playlistNames },
    onConfirm = { name -> viewModel.onInsertPlaylist(name); onDismissRequest() },
    focusRequester = focusRequester,
    onDismissRequest = onDismissRequest
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