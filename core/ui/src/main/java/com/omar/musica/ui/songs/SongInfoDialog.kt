package com.omar.musica.ui.songs

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.omar.musica.store.model.song.Song
import com.omar.musica.ui.R
import com.omar.musica.ui.albumart.LocalEfficientThumbnailImageLoader
import com.omar.musica.ui.albumart.toSongAlbumArtModel
import com.omar.musica.ui.millisToTime
import java.io.File
import kotlin.math.pow


@Composable
fun rememberSongDialog(): SongInfoDialog {

    val internalStoragePath = remember {
        Environment.getExternalStorageDirectory().absolutePath
    }

    var song by remember {
        mutableStateOf<Song?>(null)
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
                                model = safeSong.toSongAlbumArtModel(),
                                error = painterResource(R.drawable.placeholder),
                                placeholder = painterResource(R.drawable.placeholder),
                                contentDescription = null,
                                imageLoader = LocalEfficientThumbnailImageLoader.current
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            SongMetadataRow(
                                modifier = rowModifier,
                                title = "Title",
                                value = safeSong.metadata.title
                            )
                        }
                        Divider(
                            Modifier
                                .padding(top = 8.dp)
                                .fillMaxWidth()
                        )
                        SongMetadataSpacer()
                        SongMetadataRow(
                            modifier = rowModifier,
                            title = "File Name",
                            value = File(safeSong.filePath).name
                        )
                        SongMetadataSpacer()
                        SongMetadataRow(
                            modifier = rowModifier,
                            title = "File Location",
                            value = safeSong.filePath.replace(
                                internalStoragePath,
                                "Internal Storage",
                                true
                            )
                        )
                        SongMetadataSpacer()
                        SongMetadataRow(
                            modifier = rowModifier,
                            title = "Artist",
                            value = safeSong.metadata.artistName ?: "<unknown>"
                        )
                        SongMetadataSpacer()
                        SongMetadataRow(
                            modifier = rowModifier,
                            title = "Album",
                            value = safeSong.metadata.albumName ?: "<unknown>"
                        )
                        SongMetadataSpacer()
                        SongMetadataRow(
                            modifier = rowModifier,
                            title = "File Size",
                            value = safeSong.metadata.sizeBytes.bytesToSizeString()
                        )
                        SongMetadataSpacer()
                        SongMetadataRow(
                            modifier = rowModifier,
                            title = "Duration",
                            value = safeSong.metadata.durationMillis.millisToTime()
                        )
                    }
                }
            }
        )

    return remember {
        object : SongInfoDialog {
            override fun open(songUi: Song) {
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
    fun open(songUi: Song)
}


val sizeRanges = arrayOf(
    2.0.pow(10.0).toLong() until 2.0.pow(20.0).toLong() to "KB",
    2.0.pow(20.0).toLong() until 2.0.pow(30.0).toLong() to "MB",
    2.0.pow(30.0).toLong() until Long.MAX_VALUE to "GB"
)

fun Long.bytesToSizeString(): String {

    if (this in 0 until 1024) return "$this Bytes"

    val result = try {
        val sizeRange = sizeRanges.first { this in it.first }
        "${this / sizeRange.first.first} ${sizeRange.second}"
    } catch (e: NoSuchElementException) {
        "Unknown size"
    }

    return result
}