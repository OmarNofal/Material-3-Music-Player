package com.omar.musica.albums.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumsTopBar(
    scrollBehavior: TopAppBarScrollBehavior
) {

    TopAppBar(
        title = {
            Text(text = "Albums", fontWeight = FontWeight.SemiBold)
        },
        scrollBehavior = scrollBehavior
    )

}