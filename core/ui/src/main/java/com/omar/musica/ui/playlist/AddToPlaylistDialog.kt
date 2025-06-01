package com.omar.musica.ui.playlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.PlaylistAdd
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.PlaylistAdd
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.omar.musica.model.playlist.PlaylistInfo
import com.omar.musica.store.model.song.Song
import com.omar.musica.ui.showSongsAddedToPlaylistsToast


@Composable
fun AddToPlaylistDialog(
  viewModel: AddToPlaylistViewModel = hiltViewModel(),
  songs: List<Song>,
  onDismissRequest: () -> Unit
) {
  val state by viewModel.state.collectAsState()
  val dialogEntries = remember(state) {
    when (val safeState = state) {
      is AddToPlaylistState.Loading -> AddToPlaylistDialogState()
      is AddToPlaylistState.Success -> {
        AddToPlaylistDialogState(
          mutableStateListOf(*safeState.playlists.map {
            AddToPlaylistDialogEntry(
              it,
              false
            )
          }.toTypedArray())
        )
      }
    }
  }
  val context = LocalContext.current
  val createPlaylistDialog = rememberCreatePlaylistDialog()
  AlertDialog(
    onDismissRequest = onDismissRequest,
    confirmButton = {
      TextButton(onClick = {
        if (state !is AddToPlaylistState.Success) return@TextButton
        val selectedPlaylists =
          dialogEntries.entries.filter { it.isSelected }.map { it.playlist }
        viewModel.addSongsToPlaylists(songs, selectedPlaylists)
        context.showSongsAddedToPlaylistsToast(songs.size, selectedPlaylists.size)
        onDismissRequest()
      }, enabled = dialogEntries.entries.any { it.isSelected }
      ) {
        Text("Confirm")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismissRequest) {
        Text("Cancel")
      }
    },
    text = {
      if (state is AddToPlaylistState.Loading) {
        Box {
          CircularProgressIndicator()
        }
      } else {
        LazyColumn {
          itemsIndexed(dialogEntries.entries) { index, entry ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(4.dp))
                .clickable { dialogEntries.toggle(index) },
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Checkbox(
                checked = entry.isSelected,
                onCheckedChange = { dialogEntries.toggle(index) })
              Spacer(modifier = Modifier.width(8.dp))
              Text(text = entry.playlist.name)
            }
          }
          item {
            HorizontalDivider()
          }
          item {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(4.dp))
                .clickable(onClick = createPlaylistDialog::launch)
                .padding(8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(imageVector = Icons.Rounded.Add, contentDescription = null)
              Spacer(modifier = Modifier.width(8.dp))
              Text(text = "Create a new playlist")
            }
          }
        }
      }
    },
    title = { Text(text = "Add to Playlists") },
    icon = { Icon(imageVector = Icons.AutoMirrored.Rounded.PlaylistAdd, contentDescription = null) }
  )

}


@Composable
fun rememberAddToPlaylistDialog(): AddToPlaylistDialog {

  var dialogSongs by remember {
    mutableStateOf<List<Song>?>(null)
  }

  if (dialogSongs != null) {
    AddToPlaylistDialog(songs = dialogSongs.orEmpty()) {
      dialogSongs = null
    }
  }

  return remember {
    object : AddToPlaylistDialog {
      override fun launch(songs: List<Song>) {
        dialogSongs = songs
      }
    }
  }

}

private data class AddToPlaylistDialogState(
  val entries: MutableList<AddToPlaylistDialogEntry> = mutableStateListOf()
) {
  fun toggle(index: Int) {
    val isSelected = entries[index].isSelected
    entries[index] = entries[index].copy(isSelected = !isSelected)
  }
}

data class AddToPlaylistDialogEntry(
  val playlist: PlaylistInfo,
  val isSelected: Boolean
)

interface AddToPlaylistDialog {
  fun launch(songs: List<Song>)
}