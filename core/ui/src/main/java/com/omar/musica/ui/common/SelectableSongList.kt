package com.omar.musica.ui.common

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Divider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.omar.musica.ui.model.SongUi


@OptIn(ExperimentalFoundationApi::class)
fun LazyListScope.selectableSongsList(
    songs: List<SongUi>,
    multiSelectState: MultiSelectState,
    multiSelectEnabled: Boolean,
    animateItemPlacement: Boolean = true,
    menuActionsBuilder: (SongUi) -> List<MenuActionItem>?,
    onSongClicked: (SongUi, Int) -> Unit
) {

    itemsIndexed(songs, key = { _, song -> song.uriString }) { index, song ->

        val menuActions = remember {
            menuActionsBuilder(song)
        }

        val rowState = if (multiSelectEnabled && multiSelectState.selected.contains(song)) {
            SongRowState.SELECTION_STATE_SELECTED
        } else if (multiSelectEnabled) {
            SongRowState.SELECTION_STATE_NOT_SELECTED
        } else
            SongRowState.MENU_SHOWN

        SongRow(
            modifier = Modifier
                .then(if (animateItemPlacement) Modifier.animateItemPlacement() else Modifier)
                .fillMaxWidth()
                .combinedClickable(
                    onLongClick = {
                        multiSelectState.toggleSong(song)
                    }
                ) {
                    if (multiSelectEnabled)
                        multiSelectState.toggleSong(song)
                    else
                        onSongClicked(song, index)
                },
            song = song,
            menuOptions = menuActions,
            rowState
        )
        if (song != songs.last()) {
            Divider(
                Modifier
                    .fillMaxWidth()
                    .padding(start = (12 + 54 + 8).dp)
            )
        }

    }
}