package com.omar.musica.ui.common

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.omar.musica.model.Song
import com.omar.musica.ui.albumart.LocalThumbnailImageLoader
import timber.log.Timber


@Composable
fun SongItem(
    modifier: Modifier,
    song: Song,
    menuOptions: List<MenuActionItem>? = null
) {

    Row(
        modifier = modifier
            .padding(
                start = 12.dp,
                end = 12.dp,
                top = 12.dp,
                bottom = 12.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        AsyncImage(
            modifier = Modifier
                .size(54.dp)
                .clip(RoundedCornerShape(6.dp)),
            model = song,
            imageLoader = LocalThumbnailImageLoader.current,
            contentDescription = "Cover Photo",
            contentScale = ContentScale.Crop,
            fallback = rememberVectorPainter(image = Icons.Rounded.MusicNote),
            placeholder = rememberVectorPainter(image = Icons.Rounded.MusicNote),
            error = rememberVectorPainter(image = Icons.Rounded.MusicNote),
            onError = { it -> Timber.d("uri: ${it.result.request.data.toString()}" + it.result.throwable.stackTraceToString()) }
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column(Modifier.weight(1f)) {

            Text(
                text = song.title,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            //Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = song.artist.toString(),
                fontWeight = FontWeight.Normal,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = song.album.toString(),
                    fontSize = 11.sp,
                    maxLines = 1,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = song.length.millisToTime(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal
                )
            }


        }

        if (menuOptions != null) {
            Box() {
                var expanded by remember { mutableStateOf(false) }
                IconButton(onClick = { expanded = true }) {
                    Icon(imageVector = Icons.Rounded.MoreVert, contentDescription = null)
                }
                SongDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    actions = menuOptions
                )
            }
        }

    }

}

fun Long.toAlbumArtUri() = "content://media/external/audio/$this"