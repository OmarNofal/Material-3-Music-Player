package com.omar.musica.songs.ui

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongsTopAppBar(
    modifier: Modifier = Modifier,
    onSearchClicked: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) {

    CenterAlignedTopAppBar(
        modifier = modifier,
        title = { Text("Songs", fontWeight = FontWeight.SemiBold) },
        actions = {
            IconButton(onSearchClicked) {
                Icon(Icons.Rounded.Search, contentDescription = null)
            }
        },
        scrollBehavior = scrollBehavior
    )

}