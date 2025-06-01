package com.omar.musica.songs.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Sort
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.omar.musica.model.AlbumsSortOption
import com.omar.musica.model.SongSortOption
import com.omar.musica.model.prefs.IsAscending


@Composable
fun SortChip(
  modifier: Modifier,
  songSortOptions: List<SongSortOption>,
  onSortOptionSelected: (SongSortOption, ascending: Boolean) -> Unit,
  currentSongSortOption: SongSortOption,
  isAscending: Boolean = true,
) {

  var isDialogShown by rememberSaveable { mutableStateOf(false) }

  if (isDialogShown)
    SortOptionsDialog(
      songSortOptions = songSortOptions,
      onDismissRequest = { isDialogShown = false },
      onSelected = { sortOption, asc ->
        isDialogShown = false; onSortOptionSelected(sortOption, asc)
      }
    )

  SuggestionChip(
    modifier = modifier,
    onClick = { isDialogShown = !isDialogShown },
    label = {
      val ascendingText = if (isAscending) "Ascending" else "Descending"
      Text(text = "${currentSongSortOption.getString()} - $ascendingText", maxLines = 1, overflow = TextOverflow.Ellipsis)
    },
    icon = { Icon(Icons.Rounded.Sort, contentDescription = null) })

}


@Composable
fun SortOptionDropdownMenu(
  visible: Boolean,
  sortOption: SongSortOption,
  isAscending: IsAscending,
  onChangeSortCriteria: (SongSortOption) -> Unit,
  onChangeAscending: (IsAscending) -> Unit,
  onDismissRequest: () -> Unit,
) {
  DropdownMenu(expanded = visible, onDismissRequest = onDismissRequest) {
    Text(
      text = "Sort Songs",
      modifier = Modifier.padding(start = 8.dp, top = 8.dp, bottom = 8.dp, end = 16.dp),
      style = MaterialTheme.typography.titleMedium
    )
    DropdownMenuItem(
      text = { Text(text = "Ascending") },
      onClick = { onChangeAscending(!isAscending) },
      trailingIcon = {
        Checkbox(
          checked = isAscending,
          onCheckedChange = null
        )
      }
    )
    HorizontalDivider()
    DropdownMenuItem(
      text = { Text("Title") },
      onClick = { onChangeSortCriteria(SongSortOption.TITLE) },
      trailingIcon = {
        RadioButton(selected = sortOption == SongSortOption.TITLE, onClick = null)
      }
    )
    DropdownMenuItem(
      text = { Text("Artist") },
      onClick = { onChangeSortCriteria(SongSortOption.ARTIST) },
      trailingIcon = {
        RadioButton(selected = sortOption == SongSortOption.ARTIST, onClick = null)
      }
    )
    DropdownMenuItem(
      text = { Text("Album") },
      onClick = { onChangeSortCriteria(SongSortOption.ALBUM) },
      trailingIcon = { RadioButton(selected = sortOption == SongSortOption.ALBUM, onClick = null)
      }
    )
    DropdownMenuItem(
      text = { Text("File Size") },
      onClick = { onChangeSortCriteria(SongSortOption.FileSize) },
      trailingIcon = { RadioButton(selected = sortOption == SongSortOption.FileSize, onClick = null)
      }
    )
    DropdownMenuItem(
      text = { Text("Duration") },
      onClick = { onChangeSortCriteria(SongSortOption.Duration) },
      trailingIcon = { RadioButton(selected = sortOption == SongSortOption.Duration, onClick = null)
      }
    )
  }
}


@Composable
fun SortOptionsDialog(
  songSortOptions: List<SongSortOption>,
  onDismissRequest: () -> Unit,
  onSelected: (SongSortOption, ascending: Boolean) -> Unit,
) {
  AlertDialog(
    onDismissRequest = onDismissRequest,
    dismissButton = {
      TextButton(onClick = onDismissRequest) {
        Text(text = "Cancel")
      }
    },
    title = { Text(text = "Sort Songs by") },
    confirmButton = {},
    text = {
      LazyColumn(Modifier.fillMaxWidth()) {

        songSortOptions.forEach { songSortOption: SongSortOption ->

          item {
            Text(
              modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(4.dp))
                .clickable { onSelected(songSortOption, true) }
                .padding(12.dp),
              text = songSortOption.getString() + " Ascending",
              fontWeight = FontWeight.Normal
            )
          }
          item {
            Text(
              modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(8.dp))
                .clickable { onSelected(songSortOption, false) }
                .padding(12.dp),
              text = songSortOption.getString() + " Descending",
              fontWeight = FontWeight.Normal
            )
          }
        }
      }
    }
  )

}


fun SongSortOption.getString() = when (this) {
  SongSortOption.TITLE -> "Title"
  SongSortOption.ALBUM -> "Album"
  SongSortOption.ARTIST -> "Artist"
  SongSortOption.FileSize -> "File Size"
  SongSortOption.Duration -> "Duration"
}
