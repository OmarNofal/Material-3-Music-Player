package com.omar.nowplaying.queue

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.omar.musica.ui.common.MultiSelectState
import com.omar.musica.ui.common.selectableSongsList


@Composable
fun QueueScreen(
    modifier: Modifier,
    onClose: () -> Unit,
    viewModel: QueueViewModel = hiltViewModel()
) {
    val state by viewModel.queueScreenState.collectAsState()
    QueueScreen(modifier = modifier, state = state, onClose = onClose)
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun QueueScreen(
    modifier: Modifier,
    state: QueueScreenState,
    onClose: () -> Unit
) {
    BackHandler(true) {
        onClose()
    }
    val color = Color(0x22000000)
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = "Queue") }, colors = TopAppBarDefaults.topAppBarColors(containerColor = color, ),
                navigationIcon = { IconButton(onClick = onClose) {
                    Icon(imageVector = Icons.Rounded.Close, contentDescription = "Close Queue")
                }}
            ) },
        containerColor = color
    ) {

        if (state is QueueScreenState.Loading) return@Scaffold

        val songs = (state as QueueScreenState.Loaded).songs
        LazyColumn(modifier = Modifier
            .fillMaxSize()
            .padding(it)) {
            selectableSongsList(
                songs,
                MultiSelectState(),
                false,
                animateItemPlacement = true,
                menuActionsBuilder = { null },
                onSongClicked = { _, _ -> Unit }
            )
        }

    }
}