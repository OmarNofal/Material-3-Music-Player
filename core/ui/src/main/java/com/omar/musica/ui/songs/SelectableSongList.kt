package com.omar.musica.ui.songs

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import com.omar.musica.store.model.song.Song
import com.omar.musica.ui.common.MultiSelectState
import com.omar.musica.ui.menu.MenuActionItem


@OptIn(ExperimentalFoundationApi::class)
fun LazyListScope.selectableSongsList(
    songs: List<Song>,
    multiSelectState: MultiSelectState<Song>,
    multiSelectEnabled: Boolean,
    animateItemPlacement: Boolean = true,
    menuActionsBuilder: (Song) -> List<MenuActionItem>?,
    onSongClicked: (Song, Int) -> Unit
) {

    itemsIndexed(songs, key = { _, song -> song.uri.toString() }) { index, song ->

        val menuActions = remember {
            menuActionsBuilder(song)
        }

        val rowState = if (multiSelectEnabled && multiSelectState.selected.contains(song)) {
            SongRowState.SELECTION_STATE_SELECTED
        } else if (multiSelectEnabled) {
            SongRowState.SELECTION_STATE_NOT_SELECTED
        } else
            SongRowState.MENU_SHOWN

        val interactionSource = remember { MutableInteractionSource() }

        SongRow(
            modifier = Modifier
                .then(if (animateItemPlacement) Modifier.animateItemPlacement() else Modifier)
                .fillMaxWidth()
                .background(
                    color = if (rowState == SongRowState.SELECTION_STATE_SELECTED) MaterialTheme.colorScheme.primaryContainer.copy(
                        alpha = 0.4f
                    ) else Color.Transparent
                )
                .combinedClickable(
                    interactionSource = interactionSource,
                    indication = rememberRipple(),
                    onLongClick = {
                        multiSelectState.toggle(song)
                    }
                ) {
                    if (multiSelectEnabled)
                        multiSelectState.toggle(song)
                    else
                        onSongClicked(song, index)
                }
                .pressToScale(interactionSource = interactionSource),
            song = song,
            menuOptions = menuActions,
            rowState
        )

    }
}

fun Modifier.pressToScale(
    pressedScale: Float = 0.98f,
    interactionSource: MutableInteractionSource
) = composed {

    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) pressedScale else 1f,
        animationSpec = tween(durationMillis = 100, easing = FastOutSlowInEasing)
    )

    this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}