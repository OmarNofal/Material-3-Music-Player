//package com.omar.musica.songs.ui
//
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.rounded.Close
//import androidx.compose.material.icons.rounded.PlaylistAdd
//import androidx.compose.material.icons.rounded.Search
//import androidx.compose.material.icons.rounded.Share
//import androidx.compose.material.icons.rounded.SkipNext
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.TooltipBox
//import androidx.compose.material3.Text
//import androidx.compose.material3.TopAppBar
//import androidx.compose.material3.TopAppBarScrollBehavior
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.text.font.FontWeight
//import com.omar.musica.ui.common.MultiSelectState
//import com.omar.musica.ui.model.SongUi
//
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun SongsTopAppBar(
//    modifier: Modifier = Modifier,
//    onSearchClicked: () -> Unit,
//    onShare: (List<SongUi>) -> Unit,
//    scrollBehavior: TopAppBarScrollBehavior,
//    multiSelectState: MultiSelectState,
//    onAddToPlaylists: () -> Unit,
//    onPlayNext: () -> Unit
//) {
//
//    if (multiSelectState.selected.size > 0) {
//
//        TopAppBar(
//            modifier = modifier,
//            title = {
//                Text(
//                    text = "${multiSelectState.selected.size} selected",
//                    fontWeight = FontWeight.SemiBold
//                )
//            },
//            actions = {
//                PlainTooltipBox(tooltip = { Text(text = "Play Next") }) {
//                    IconButton(modifier = Modifier.tooltipAnchor(), onClick = onPlayNext) {
//                        Icon(Icons.Rounded.SkipNext, contentDescription = "Play Next")
//                    }
//                }
//                PlainTooltipBox(tooltip = { Text(text = "Share") }) {
//                    IconButton(modifier = Modifier.tooltipAnchor(), onClick = { onShare(multiSelectState.selected) }) {
//                        Icon(Icons.Rounded.Share, contentDescription = "Share")
//                    }
//                }
//                PlainTooltipBox(tooltip = { Text(text = "Add to Playlists") }) {
//                    IconButton(modifier = Modifier.tooltipAnchor(), onClick = onAddToPlaylists) {
//                        Icon(Icons.Rounded.PlaylistAdd, contentDescription = "Add to Playlists")
//                    }
//                }
//            },
//            navigationIcon = {
//                IconButton(onClick = { multiSelectState.selected.clear() }) {
//                    Icon(
//                        imageVector = Icons.Rounded.Close,
//                        contentDescription = "End Multi selection mode"
//                    )
//                }
//            }
//        )
//
//    } else
//    {}
//
//
//}