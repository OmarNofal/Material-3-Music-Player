package com.omar.musica.ui.songs

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.omar.musica.ui.millisToTime


@Composable
fun SongsSummary(
    modifier: Modifier = Modifier,
    numberOfSongs: Int,
    totalDuration: Long
) {

    Row(modifier, verticalAlignment = Alignment.CenterVertically) {

        Text(text = "$numberOfSongs songs", fontSize = 12.sp, fontWeight = FontWeight.Normal)

        Spacer(modifier = Modifier.width(8.dp))

        Icon(modifier = Modifier.size(16.dp), imageVector = Icons.Rounded.Timer, contentDescription = null)
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = totalDuration.millisToTime(), fontSize = 12.sp, fontWeight = FontWeight.Normal)

    }

}