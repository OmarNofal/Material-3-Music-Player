package com.omar.feature.playlists

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight



fun add(x: Int, y: Int) = x + y

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongsTopAppBarBla(
    modifier: Modifier = Modifier,
    onSearchClicked: () -> Unit
) {

    CenterAlignedTopAppBar(
        modifier = modifier,
        title = { Text("Songs", fontWeight = FontWeight.SemiBold) },
        actions = {
            IconButton(onSearchClicked) {
                Icon(Icons.Rounded.Search, contentDescription = null)
            }
        }
    )

}