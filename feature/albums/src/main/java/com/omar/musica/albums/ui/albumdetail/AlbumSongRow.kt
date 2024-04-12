package com.omar.musica.albums.ui.albumdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.omar.musica.store.model.album.AlbumSong
import com.omar.musica.ui.common.LocalCommonSongsAction
import com.omar.musica.ui.menu.buildCommonSongActions
import com.omar.musica.ui.topbar.OverflowMenu


@Composable
fun AlbumSongRow(
    modifier: Modifier,
    song: AlbumSong,
    number: Int? = null
) {

    Row(
        modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        val trackString =
            if (number != null && number != 0)
                number.toString() else "-"
        Text(
            modifier = Modifier.width(30.dp),
            textAlign = TextAlign.Center,
            text = trackString,
            fontWeight = FontWeight.Light
        )
        Spacer(modifier = Modifier.width(26.dp))
        Text(
            modifier = Modifier.weight(1f),
            text = song.song.metadata.title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.width(4.dp))

        val songActions = LocalCommonSongsAction.current
        val context = LocalContext.current
        val actions = remember {
            with(songActions) {
                buildCommonSongActions(
                    song.song,
                    context,
                    playbackActions,
                    songInfoDialog,
                    addToPlaylistDialog,
                    shareAction,
                    setRingtoneAction,
                    deleteAction,
                    openTagEditorAction
                )
            }
        }

        OverflowMenu(actionItems = actions, icon = Icons.Rounded.MoreHoriz, showIcons = false)
    }

}