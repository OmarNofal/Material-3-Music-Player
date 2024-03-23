package com.omar.musica.albums.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.omar.musica.albums.viewmodel.AlbumsScreenState
import com.omar.musica.albums.viewmodel.AlbumsViewModel


@Composable
fun AlbumsScreen(
    modifier: Modifier,
    viewModel: AlbumsViewModel = hiltViewModel()
) {

    val state by viewModel.state.collectAsState()

    AlbumsScreen(modifier = modifier, state = state)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumsScreen(
    modifier: Modifier,
    state: AlbumsScreenState
) {

    val albums = state.albums

    val topAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    
    Scaffold(
        topBar = {
            AlbumsTopBar(scrollBehavior = topAppBarScrollBehavior)
        },
        modifier = modifier,
    ) { paddingValues ->

//        LazyColumn(
//            Modifier
//                .fillMaxSize()
//                .padding(paddingValues)) {
//            items(albums, key = { it.albumInfo.name }) {
//                AlbumRow(modifier =
//                Modifier
//                    .fillMaxWidth()
//                    .clickable { }
//                    .padding(top = 12.dp, bottom = 12.dp, start = 12.dp, end = 12.dp),
//                    album = it
//                )
//                if (it != albums.last()) {
//                    HorizontalDivider(
//                        modifier = Modifier.fillMaxWidth().padding(start = (56 + 8 + 12).dp)
//                    )
//                }
//            }
//        }
        AlbumsGrid(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection), 
            albums = albums
        )
    }


}