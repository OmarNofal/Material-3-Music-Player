package com.omar.musica.songs.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Forward
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.PlainTooltipBox
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongsTopAppBar(
    modifier: Modifier = Modifier,
    onSearchClicked: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    multiSelectState: MultiSelectState,
    onPlayNext: () -> Unit
) {

    if (multiSelectState.selected.size > 0) {

        TopAppBar(
            modifier = modifier,
            title = {
                Text(
                    text = "${multiSelectState.selected.size} selected",
                    fontWeight = FontWeight.SemiBold
                )
            },
            actions = {
                PlainTooltipBox(tooltip = { Text(text = "Play Next") }) {
                    IconButton(modifier = Modifier.tooltipAnchor(), onClick = onPlayNext) {
                        Icon(Icons.Rounded.SkipNext, contentDescription = "Play Next")
                    }
                }
            },
            navigationIcon = {
                IconButton(onClick = { multiSelectState.selected.clear() }) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "End Multiselection mode"
                    )
                }
            }
        )

    } else

        TopAppBar(
            modifier = modifier,
            title = { Text(text = "Songs", fontWeight = FontWeight.SemiBold) },
            actions = {
                IconButton(onSearchClicked) {
                    Icon(Icons.Rounded.Search, contentDescription = null)
                }
            },
            scrollBehavior = scrollBehavior
        )

}