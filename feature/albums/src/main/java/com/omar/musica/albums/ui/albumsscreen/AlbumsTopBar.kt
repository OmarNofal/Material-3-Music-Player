package com.omar.musica.albums.ui.albumsscreen

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Sort
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.omar.musica.model.AlbumsSortOption
import com.omar.musica.model.prefs.IsAscending


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumsTopBar(
  scrollBehavior: TopAppBarScrollBehavior,
  gridSize: Int,
  sortOrder: Pair<AlbumsSortOption, IsAscending>,
  onChangeSortOption: (Pair<AlbumsSortOption, IsAscending>) -> Unit,
  onChangeGridSize: (Int) -> Unit
) {

  var gridSizeDropdownShown by remember {
    mutableStateOf(false)
  }

  var sortOptionDropdownMenuShown by remember {
    mutableStateOf(false)
  }

  TopAppBar(
    title = {
      Text(text = "Albums", fontWeight = FontWeight.SemiBold)
    },
    scrollBehavior = scrollBehavior,
    actions = {
      IconButton(onClick = { gridSizeDropdownShown = !gridSizeDropdownShown }) {
        Icon(imageVector = Icons.Rounded.GridView, contentDescription = "Grid Size")
        GridSizeDropDownMenu(
          visible = gridSizeDropdownShown,
          currentSize = gridSize,
          onSizeSelected = { gridSizeDropdownShown = false; onChangeGridSize(it) },
          onDismissRequest = { gridSizeDropdownShown = false }
        )
      }

      IconButton(onClick = { sortOptionDropdownMenuShown = true }) {
        Icon(imageVector = Icons.AutoMirrored.Rounded.Sort, contentDescription = "Sort")
        SortOptionDropdownMenu(
          visible = sortOptionDropdownMenuShown,
          sortOption = sortOrder.first,
          isAscending = sortOrder.second,
          onChangeSortCriteria = {
            onChangeSortOption(it to sortOrder.second); sortOptionDropdownMenuShown = false
          },
          onChangeAscending = { onChangeSortOption(sortOrder.first to it) },
          onDismissRequest = { sortOptionDropdownMenuShown = false }
        )
      }
    }
  )

}

@Composable
fun GridSizeDropDownMenu(
  visible: Boolean,
  currentSize: Int,
  onSizeSelected: (Int) -> Unit,
  onDismissRequest: () -> Unit,
) {

  DropdownMenu(expanded = visible, onDismissRequest = onDismissRequest) {
    Text(
      text = "Grid Size",
      modifier = Modifier.padding(start = 8.dp, top = 4.dp, bottom = 4.dp, end = 32.dp),
      style = MaterialTheme.typography.titleMedium
    )
    (1 until 5).forEach {
      DropdownMenuItem(
        text = { Text(it.toString()) },
        onClick = { onSizeSelected(it) },
        trailingIcon = {
          RadioButton(selected = it == currentSize, onClick = null)
        }
      )
    }
  }

}

@Composable
fun SortOptionDropdownMenu(
  visible: Boolean,
  sortOption: AlbumsSortOption,
  isAscending: IsAscending,
  onChangeSortCriteria: (AlbumsSortOption) -> Unit,
  onChangeAscending: (IsAscending) -> Unit,
  onDismissRequest: () -> Unit,
) {
  DropdownMenu(expanded = visible, onDismissRequest = onDismissRequest) {
    Text(
      text = "Sort Albums",
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
      text = { Text("Name") },
      onClick = { onChangeSortCriteria(AlbumsSortOption.NAME) },
      trailingIcon = {
        RadioButton(selected = sortOption == AlbumsSortOption.NAME, onClick = null)
      }
    )
    DropdownMenuItem(
      text = { Text("Artist") },
      onClick = { onChangeSortCriteria(AlbumsSortOption.ARTIST) },
      trailingIcon = {
        RadioButton(selected = sortOption == AlbumsSortOption.ARTIST, onClick = null)
      }
    )
    Spacer(modifier = Modifier.width(16.dp))
    DropdownMenuItem(
      text = { Text("Number of Songs") },
      onClick = { onChangeSortCriteria(AlbumsSortOption.NUMBER_OF_SONGS) },
      trailingIcon = {
        RadioButton(
          selected = sortOption == AlbumsSortOption.NUMBER_OF_SONGS,
          onClick = null
        )
      }
    )
  }
}