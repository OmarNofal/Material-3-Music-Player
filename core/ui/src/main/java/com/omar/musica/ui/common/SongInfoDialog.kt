package com.omar.musica.ui.common

import android.os.Environment
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.omar.musica.ui.albumart.LocalThumbnailImageLoader
import com.omar.musica.ui.model.SongUi


@Composable
fun rememberSongDialog(): SongInfoDialog {

    val internalStoragePath = remember {
        Environment.getExternalStorageDirectory().absolutePath
    }

    var song by remember {
        mutableStateOf<SongUi?>(null)
    }

    val safeSong = song
    if (safeSong != null)
        AlertDialog(
            confirmButton = {
                TextButton(onClick = { song = null }) {
                    Text(text = "Return")
                }
            },
            icon = { Icon(imageVector = Icons.Rounded.Info, contentDescription = null) },
            onDismissRequest = { song = null },
            text = {
                LazyColumn {
                    item {

                        val rowModifier = Modifier.fillMaxWidth()

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                modifier = Modifier
                                    .size(64.dp),
                                model = safeSong,
                                contentDescription = null,
                                imageLoader = LocalThumbnailImageLoader.current
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            SongMetadataRow(
                                modifier = rowModifier,
                                title = "Title",
                                value = safeSong.title
                            )
                        }
                        Divider(Modifier.padding(top = 8.dp).fillMaxWidth())
                        SongMetadataSpacer()
                        SongMetadataRow(
                            modifier = rowModifier,
                            title = "File Name",
                            value = safeSong.fileName
                        )
                        SongMetadataSpacer()
                        SongMetadataRow(
                            modifier = rowModifier,
                            title = "File Location",
                            value = safeSong.location.replace(internalStoragePath, "Internal Storage", true)
                        )
                        SongMetadataSpacer()
                        SongMetadataRow(
                            modifier = rowModifier,
                            title = "Artist",
                            value = safeSong.artist ?: "<unknown>"
                        )
                        SongMetadataSpacer()
                        SongMetadataRow(
                            modifier = rowModifier,
                            title = "Album",
                            value = safeSong.album ?: "<unknown>"
                        )
                    }
                }
            }
        )

    return remember {
        object : SongInfoDialog {
            override fun open(songUi: SongUi) {
                song = songUi
            }
        }
    }
}

@Composable
fun SongMetadataRow(
    modifier: Modifier,
    title: String,
    value: String
) {

    Column(modifier) {
        Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 10.sp)
        Text(text = value, fontWeight = FontWeight.Normal, fontSize = 14.sp)
    }

}

@Composable
internal fun SongMetadataSpacer() {
    Spacer(modifier = Modifier.height(16.dp))
}

interface SongInfoDialog {
    fun open(songUi: SongUi)
}