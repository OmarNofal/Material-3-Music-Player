package com.omar.musica.playlists.playlists

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.omar.musica.playlists.rememberCreatePlaylistDialog


@Composable
fun PlaylistsScreen(
    modifier: Modifier,
    playlistsViewModel: PlaylistsViewModel = hiltViewModel()
) {

    val state by playlistsViewModel.state.collectAsState()

    PlaylistsScreen(
        modifier = modifier,
        state = state,
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistsScreen(
    modifier: Modifier,
    state: PlaylistsScreenState
) {

    val createPlaylistsDialog = rememberCreatePlaylistDialog()

    Scaffold(
        modifier = modifier,
        topBar = {

            TopAppBar(
                title = { Text(text = "Playlists", fontWeight = FontWeight.SemiBold) },
                actions = {
                    IconButton(onClick = { createPlaylistsDialog.launch() }) {
                        Icon(imageVector = Icons.Rounded.Add, contentDescription = null)
                    }
                }
            )

        }
    ) { paddingValues ->

        if (state is PlaylistsScreenState.Loading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {

                item {
                    Divider(Modifier.fillMaxWidth())
                }

                val list = (state as PlaylistsScreenState.Success).playlists

                items(list) {
                    PlaylistRow(
                        Modifier.fillMaxWidth(),
                        it
                    )
                    if (it != list.last()) {
                        Divider(
                            Modifier
                                .fillMaxWidth()
                                .padding(start = (12 + 48 + 8).dp)
                        )
                    }
                }

            }

    }


}
