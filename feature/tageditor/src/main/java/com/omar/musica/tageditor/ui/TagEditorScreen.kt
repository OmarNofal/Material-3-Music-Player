package com.omar.musica.tageditor.ui

import android.Manifest
import android.app.Activity
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.omar.musica.store.model.tags.SongTags
import com.omar.musica.tageditor.state.TagEditorState
import com.omar.musica.tageditor.viewmodel.TagEditorViewModel
import com.omar.musica.ui.showShortToast
import kotlinx.coroutines.launch


@Composable
fun TagEditorScreen(
    modifier: Modifier,
    tagEditorViewModel: TagEditorViewModel = hiltViewModel(),
    onClose: () -> Unit,
) {

    val state by tagEditorViewModel.state.collectAsState()


    val context = LocalContext.current

    LaunchedEffect(key1 = state) {
        if (state is TagEditorState.Loaded) {
            val safeState = state as? TagEditorState.Loaded
            if (safeState?.isFailed == true) {
                context.showShortToast("Failed to update tags")
                onClose()
            }
            if (safeState?.isSaved == true) {
                context.showShortToast("Tags updated successfully")
                onClose()
            }
        }
    }

    val coroutineScope = rememberCoroutineScope()


    TagEditorScreen(
        modifier = modifier,
        state,
        tagEditorViewModel::getLyrics,
        tagEditorViewModel::saveTags,
        onClose
    )
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun TagEditorScreen(
    modifier: Modifier,
    state: TagEditorState,
    getLyrics: suspend () -> String?,
    onSaveUserTags: (SongTags) -> Unit,
    onClose: () -> Unit,
) {


    var editedSongTags: SongTags? by remember(state) {
        if (state is TagEditorState.Loaded) mutableStateOf(state.tags)
        else mutableStateOf(null)
    }

    val shouldShowFab by remember(editedSongTags) {
        derivedStateOf {
            val currentTags = editedSongTags
            val startTags = (state as? TagEditorState.Loaded)?.tags
            if (currentTags == null || startTags == null) false
            else currentTags != startTags
        }
    }

    val activity = LocalContext.current as Activity
    val contract = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = {
            if (it.resultCode == Activity.RESULT_OK)
                onSaveUserTags(editedSongTags!!)
        })

    val coroutineScope = rememberCoroutineScope()
    // Launcher for requesting WRITE_EXTERNAL_STORAGE permission
    val writePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (granted) {
                // Permission granted, call ViewModel function
                coroutineScope.launch {
                    onSaveUserTags(editedSongTags!!)
                }
            } else {
                // Permission denied, show message
                Toast.makeText(activity, "Write permission is required to save tags.", Toast.LENGTH_LONG).show()
            }
        }
    )

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = modifier,
        topBar = {
            TagEditorTopBar(onClose, scrollBehavior)
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = shouldShowFab,
                enter = scaleIn(initialScale = 0.7f) + fadeIn(),
                exit = scaleOut(targetScale = 0.7f) + fadeOut()
            ) {
                FloatingActionButton(onClick = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        val writeRequest = MediaStore
                            .createWriteRequest(
                                activity.contentResolver,
                                listOf(editedSongTags!!.uri)
                            )
                        contract.launch(
                            IntentSenderRequest.Builder(writeRequest)
                                .build()
                        )
                    } else {
                        writePermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        onSaveUserTags(editedSongTags!!)
                    }
                }) {
                    Icon(imageVector = Icons.Rounded.Save, contentDescription = "Save")
                }
            }
        }
    ) {

        if (state is TagEditorState.Loading) {
            Box(modifier = Modifier.padding(it)) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                )
            }
            return@Scaffold
        }

        val windowClass = calculateWindowSizeClass(activity = LocalContext.current as Activity)


        val scope = rememberCoroutineScope()
        val populateLyrics: () -> Unit = remember {
            {
                scope.launch {
                    val lyrics = getLyrics()
                    if (lyrics != null) {
                        editedSongTags =
                            editedSongTags!!.copy(
                                metadata = editedSongTags!!.metadata.copy(
                                    lyrics = lyrics
                                )
                            )
                    }
                }
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            val shouldShowProgressBar = (state as? TagEditorState.Loaded)?.isSaving ?: false
            if (shouldShowProgressBar)
                LinearProgressIndicator(
                    Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                )
            if (windowClass.widthSizeClass >= WindowWidthSizeClass.Medium)
                LoadedLandscapeTagEditorScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                        .then(if (shouldShowProgressBar) Modifier.alpha(0.5f) else Modifier),
                    tags = editedSongTags!!,
                    populateLyrics = populateLyrics,
                    nestedScrollConnection = scrollBehavior.nestedScrollConnection,
                    onEditTags = { newTags -> editedSongTags = newTags }
                )
            else
                LoadedPortraitTagEditorScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                        .then(if (shouldShowProgressBar) Modifier.alpha(0.5f) else Modifier),
                    editedSongTags!!,
                    populateLyrics = populateLyrics,
                    nestedScrollConnection = scrollBehavior.nestedScrollConnection
                ) { newTags -> editedSongTags = newTags }
        }

    }
}

@Composable
fun LoadedLandscapeTagEditorScreen(
    modifier: Modifier,
    tags: SongTags,
    populateLyrics: () -> Unit,
    nestedScrollConnection: NestedScrollConnection,
    onEditTags: (SongTags) -> Unit,
) {

    val basicMetadata = tags.metadata.basicSongMetadata

    val commonFieldModifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp)
        .padding(bottom = 6.dp)

    Row(
        modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {

        // image
        CoverArt(
            modifier = Modifier
                .weight(0.4f)
                .fillMaxHeight()
                .padding(top = 4.dp)
                .scale(0.9f),
            bitmap = tags.artwork,
            albumName = basicMetadata.albumName.orEmpty(),
            songTitle = basicMetadata.title,
            onUserPickedNewBitmap = { onEditTags(tags.copy(artwork = it)) }
        )

        LazyColumn(
            modifier = Modifier
                .weight(0.6f)
                .fillMaxSize()
                .nestedScroll(nestedScrollConnection)
        ) {
            tagFields(commonFieldModifier, tags, onEditTags, populateLyrics)
        }

    }

}

@Composable
fun LoadedPortraitTagEditorScreen(
    modifier: Modifier,
    tags: SongTags,
    populateLyrics: () -> Unit,
    nestedScrollConnection: NestedScrollConnection,
    onEditTags: (SongTags) -> Unit,
) {

    val commonFieldModifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp)
        .padding(bottom = 6.dp)

    LazyColumn(modifier.nestedScroll(nestedScrollConnection)) {
        item {
            CoverArt(
                modifier = Modifier.fillMaxWidth(),
                bitmap = tags.artwork,
                albumName = tags.metadata.basicSongMetadata.albumName.orEmpty(),
                songTitle = tags.metadata.basicSongMetadata.title,
                onUserPickedNewBitmap = { onEditTags(tags.copy(artwork = it)) }
            )
        }
        item { Spacer(modifier = Modifier.height(8.dp)) }
        tagFields(
            commonFieldModifier = commonFieldModifier,
            tags = tags,
            onEditTags = onEditTags,
            onLoadLyrics = populateLyrics,
        )
    }
}


fun LazyListScope.tagFields(
    commonFieldModifier: Modifier,
    tags: SongTags,
    onEditTags: (SongTags) -> Unit,
    onLoadLyrics: () -> Unit
) {
    val basicSongMetadata = tags.metadata.basicSongMetadata
    val extendedMetadata = tags.metadata

    item {
        SingleFieldText(
            modifier = commonFieldModifier,
            value = basicSongMetadata.title,
            name = "Title",
            onChange = {
                onEditTags(
                    tags.copy(
                        metadata = tags.metadata.copy(
                            basicSongMetadata = tags.metadata.basicSongMetadata.copy(
                                title = it
                            )
                        )
                    )
                )
            },
        )
    }
    item {
        SingleFieldText(
            modifier = commonFieldModifier,
            value = basicSongMetadata.artistName.orEmpty(),
            name = "Artist",
            onChange = {
                onEditTags(
                    tags.copy(
                        metadata = tags.metadata.copy(
                            basicSongMetadata = tags.metadata.basicSongMetadata.copy(
                                artistName = it
                            )
                        )
                    )
                )
            },
        )
    }
    item {
        SingleFieldText(
            modifier = commonFieldModifier,
            value = basicSongMetadata.albumName.orEmpty(),
            name = "Album",
            onChange = {
                onEditTags(
                    tags.copy(
                        metadata = tags.metadata.copy(
                            basicSongMetadata = basicSongMetadata.copy(
                                albumName = it
                            )
                        )
                    )
                )
            },
        )
    }
    item {
        SingleFieldText(
            modifier = commonFieldModifier,
            value = extendedMetadata.albumArtist,
            name = "Album Artist",
            onChange = {
                onEditTags(
                    tags.copy(
                        metadata = extendedMetadata.copy(
                            albumArtist = it
                        )
                    )
                )
            },
        )
    }
    item {
        SingleFieldText(
            modifier = commonFieldModifier,
            value = extendedMetadata.composer,
            name = "Composer",
            onChange = {
                onEditTags(
                    tags.copy(
                        metadata = extendedMetadata.copy(
                            composer = it
                        )
                    )
                )
            },
        )
    }
    item {
        Row(modifier = commonFieldModifier) {
            SingleFieldText(
                modifier = Modifier.weight(1f),
                value = extendedMetadata.trackNumber,
                name = "Track Number",
                onChange = {
                    onEditTags(
                        tags.copy(
                            metadata = extendedMetadata.copy(
                                trackNumber = it
                            )
                        )
                    )
                },
                maxLines = 1
            )
            Spacer(modifier = Modifier.width(4.dp))
            SingleFieldText(
                modifier = Modifier.weight(1f),
                value = extendedMetadata.discNumber,
                onChange = {
                    onEditTags(
                        tags.copy(
                            metadata = extendedMetadata.copy(
                                discNumber = it
                            )
                        )
                    )
                },
                name = "Disc Number"
            )
        }
    }
    item {
        Row(modifier = commonFieldModifier) {
            SingleFieldText(
                modifier = Modifier.weight(1f),
                value = extendedMetadata.genre,
                name = "Genre",
                onChange = {
                    onEditTags(
                        tags.copy(
                            metadata = tags.metadata.copy(
                                genre = it
                            )
                        )
                    )
                },
                maxLines = 1
            )
            Spacer(modifier = Modifier.width(4.dp))
            SingleFieldText(
                modifier = Modifier.weight(1f),
                value = extendedMetadata.year,
                name = "Year",
                onChange = {
                    onEditTags(
                        tags.copy(
                            metadata = extendedMetadata.copy(
                                year = it
                            )
                        )
                    )
                },
            )
        }
    }
    item {
        Box(modifier = commonFieldModifier) {
            SingleFieldText(
                modifier = Modifier
                    .heightIn(240.dp)
                    .fillMaxWidth(),
                value = tags.metadata.lyrics,
                name = "Lyrics",
                onChange = { onEditTags(tags.copy(metadata = extendedMetadata.copy(lyrics = it))) },
            )
            IconButton(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 12.dp, top = 12.dp)
                    .background(Color.White.copy(alpha = 0.2f), shape = CircleShape)
                    .padding(1.dp)
                    .alpha(0.8f), onClick = onLoadLyrics
            ) {
                Icon(imageVector = Icons.Filled.Download, contentDescription = "Download Lyrics")
            }
        }
    }
}

@Composable
fun SingleFieldText(
    modifier: Modifier,
    value: String,
    name: String,
    onChange: (String) -> Unit,
    maxLines: Int = Int.MAX_VALUE
) {
    OutlinedTextField(
        modifier = modifier,
        value = value,
        onValueChange = onChange,
        placeholder = { Text(text = name) },
        label = { Text(text = name) },
        maxLines = maxLines
    )
}