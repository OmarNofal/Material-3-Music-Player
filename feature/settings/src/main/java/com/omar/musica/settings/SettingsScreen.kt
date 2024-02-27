package com.omar.musica.settings

import android.os.Build
import android.provider.DocumentsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Block
import androidx.compose.material.icons.rounded.BlurCircular
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.FastForward
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.omar.musica.settings.common.ColorPickerDialog
import com.omar.musica.settings.common.GeneralSettingsItem
import com.omar.musica.settings.common.SettingInfo
import com.omar.musica.settings.common.SwitchSettingsItem
import com.omar.musica.ui.common.fromIntToAccentColor
import com.omar.musica.ui.common.toInt
import com.omar.musica.ui.model.AppThemeUi
import com.omar.musica.ui.model.PlayerThemeUi
import com.omar.musica.ui.model.UserPreferencesUi
import getPath


@Composable
fun SettingsScreen(
    modifier: Modifier,
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val state by settingsViewModel.state.collectAsState()
    SettingsScreen(
        modifier = modifier,
        state = state,
        settingsCallbacks = settingsViewModel
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier,
    state: SettingsState,
    settingsCallbacks: ISettingsViewModel
) {

    val topBarScrollBehaviour = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = modifier,
        topBar = { SettingsTopAppBar(topBarScrollBehaviour) }
    )
    { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding()),
            contentAlignment = Alignment.Center
        ) {

            if (state is SettingsState.Loading) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                )
            } else if (state is SettingsState.Loaded) {
                SettingsList(
                    modifier = Modifier.fillMaxSize(),
                    userPreferences = state.userPreferences,
                    settingsCallbacks = settingsCallbacks,
                    nestedScrollConnection = topBarScrollBehaviour.nestedScrollConnection,
                )
            }

        }
    }

}


@Composable
fun SettingsList(
    modifier: Modifier,
    userPreferences: UserPreferencesUi,
    settingsCallbacks: ISettingsViewModel,
    nestedScrollConnection: NestedScrollConnection
) {
    val sectionTitleModifier = Modifier
        .fillMaxWidth()
        .padding(start = 32.dp, top = 16.dp)

    LazyColumn(
        modifier.nestedScroll(nestedScrollConnection)
    ) {
        item {
            Divider(Modifier.fillMaxWidth())
        }
        item {
            SectionTitle(modifier = sectionTitleModifier, title = "Interface")
        }
        item {
            var appThemeDialogVisible by remember {
                mutableStateOf(false)
            }
            AppThemeDialog(
                visible = appThemeDialogVisible,
                currentSelected = userPreferences.uiSettings.theme,
                onDismissRequest = { appThemeDialogVisible = false },
                onThemeSelected = {
                    appThemeDialogVisible = false
                    settingsCallbacks.onThemeSelected(it)
                }
            )
            val text = when (userPreferences.uiSettings.theme) {
                AppThemeUi.SYSTEM -> "Follow System Settings"
                AppThemeUi.LIGHT -> "Light"
                AppThemeUi.DARK -> "Dark"
            }
            GeneralSettingsItem(modifier = Modifier
                .fillMaxWidth()
                .clickable { appThemeDialogVisible = true }
                .padding(horizontal = 32.dp, vertical = 16.dp),
                title = "App Theme",
                subtitle = text
            )
        }

        item {
            SwitchSettingsItem(
                modifier = Modifier
                    .fillMaxWidth(),
                title = "Use Black Background for Dark Theme",
                subtitle = "Preserves battery on AMOLED screens",
                toggled = userPreferences.uiSettings.blackBackgroundForDarkTheme,
                onToggle = { settingsCallbacks.toggleBlackBackgroundForDarkTheme() }
            )
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            item {
                SwitchSettingsItem(
                    modifier = Modifier
                        .fillMaxWidth(),
                    title = "Dynamic Color Scheme",
                    toggled = userPreferences.uiSettings.isUsingDynamicColor,
                    onToggle = { settingsCallbacks.toggleDynamicColorScheme() }
                )
            }
        }

        item {
            var accentColorDialogVisible by remember {
                mutableStateOf(false)
            }
            if (accentColorDialogVisible) {
                ColorPickerDialog(
                    initialColor = userPreferences.uiSettings.accentColor.fromIntToAccentColor(),
                    onColorChanged = { color -> settingsCallbacks.setAccentColor(color.toInt()) },
                    onDismissRequest = { accentColorDialogVisible = false }
                )
            }
            GeneralSettingsItem(modifier = Modifier
                .fillMaxWidth()
                .clickable { accentColorDialogVisible = true }
                .padding(horizontal = 32.dp, vertical = 16.dp),
                title = "Accent Color",
                subtitle = "Color of the app theme"
            )
        }

        item {
            SwitchSettingsItem(
                modifier = Modifier
                    .fillMaxWidth(),
                title = "MiniPlayer Extra Controls",
                subtitle = "Show next and previous buttons in MiniPlayer",
                toggled = userPreferences.uiSettings.showMiniPlayerExtraControls,
                onToggle = settingsCallbacks::toggleShowExtraControls,
            )
        }

        item {
            var playerThemeDialogVisible by remember {
                mutableStateOf(false)
            }
            PlayerThemeDialog(
                playerThemeDialogVisible,
                userPreferences.uiSettings.playerThemeUi,
                onThemeSelected = {
                    playerThemeDialogVisible = false
                    settingsCallbacks.onPlayerThemeChanged(it)
                },
                onDismissRequest = { playerThemeDialogVisible = false },
            )
            GeneralSettingsItem(modifier = Modifier
                .fillMaxWidth()
                .clickable { playerThemeDialogVisible = true }
                .padding(horizontal = 32.dp, vertical = 16.dp),
                title = "Player Theme",
                subtitle = when (userPreferences.uiSettings.playerThemeUi) {
                    PlayerThemeUi.SOLID -> "Solid"
                    PlayerThemeUi.BLUR -> "Blur"
                }
            )
        }

        item {
            HorizontalDivider(
                Modifier
                    .fillMaxWidth()
            )
        }

        item {
            SectionTitle(modifier = sectionTitleModifier, title = "Library")
        }

        item {
            var blacklistDialogVisible by remember {
                mutableStateOf(false)
            }
            BlacklistedFoldersDialog(
                isVisible = blacklistDialogVisible,
                folders = userPreferences.librarySettings.excludedFolders,
                onFolderAdded = { settingsCallbacks.onFolderAdded(it) },
                onFolderDeleted = settingsCallbacks::onFolderDeleted,
                onDismissRequest = { blacklistDialogVisible = false }
            )
            GeneralSettingsItem(modifier = Modifier
                .fillMaxWidth()
                .clickable { blacklistDialogVisible = true }
                .padding(horizontal = 32.dp, vertical = 16.dp),
                title = "Blacklisted Folders",
                subtitle = "Music in these folders will not appear in the app"
            )
        }

        item {
            SwitchSettingsItem(modifier = Modifier.fillMaxWidth(),
                title = "Cache Album Art",
                info = SettingInfo(
                    title = "Album Art Cache",
                    text = "If enabled, this will cache the album art of one song and reuse it for all songs which have the same album name.\n\n" +
                            "This will greatly improve efficiency and loading times. However, this might cause problems if two songs in the same album " +
                            "don't have the same artwork.\n\n" +
                            "If disabled, this will load the album art of each song separately, which will result in correct artwork, at the expense of loading times" +
                            " and memory.",
                    icon = Icons.Rounded.Info
                ),
                toggled = userPreferences.librarySettings.cacheAlbumCoverArt,
                onToggle = { settingsCallbacks.onToggleCacheAlbumArt() }
            )
        }

        item {
            HorizontalDivider(
                Modifier
                    .fillMaxWidth()
            )
        }

        item {
            SectionTitle(modifier = sectionTitleModifier, title = "Player")
        }

        item {
            SwitchSettingsItem(
                modifier = Modifier.fillMaxWidth(),
                title = "Pause on Volume Zero",
                "Pause if the volume is set to zero",
                toggled = userPreferences.playerSettings.pauseOnVolumeZero,
                onToggle = { settingsCallbacks.togglePauseVolumeZero() }
            )
        }

        item {
            SwitchSettingsItem(
                modifier = Modifier.fillMaxWidth(),
                title = "Resume when gaining volume",
                "Resume if the volume increases, if it was paused due to volume loss",
                toggled = userPreferences.playerSettings.resumeWhenVolumeIncreases,
                onToggle = { settingsCallbacks.toggleResumeVolumeNotZero() }
            )
        }

        item {
            var jumpDurationDialogVisible by remember {
                mutableStateOf(false)
            }
            JumpDurationDialog(
                jumpDurationDialogVisible,
                userPreferences.playerSettings.jumpInterval,
                onDurationChanged = {
                    jumpDurationDialogVisible = false
                    settingsCallbacks.onJumpDurationChanged(it)
                },
                { jumpDurationDialogVisible = false }
            )
            GeneralSettingsItem(modifier = Modifier
                .fillMaxWidth()
                .clickable { jumpDurationDialogVisible = true }
                .padding(horizontal = 32.dp, vertical = 16.dp),
                title = "Jump Interval",
                subtitle = "${userPreferences.playerSettings.jumpInterval / 1000} seconds"
            )
        }


    }


}


@Composable
fun JumpDurationDialog(
    visible: Boolean,
    currentDurationMillis: Int,
    onDurationChanged: (Int) -> Unit,
    onDismissRequest: () -> Unit
) {

    if (!visible) return

    var durationString by remember(currentDurationMillis) {
        mutableStateOf((currentDurationMillis / 1000).toString())
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        dismissButton = { TextButton(onClick = onDismissRequest) { Text(text = "Close") } },
        confirmButton = {
            TextButton(onClick = {
                val duration = durationString.toIntOrNull() ?: return@TextButton
                onDurationChanged(duration * 1000)
            }) { Text(text = "Confirm") }
        },
        icon = { Icon(Icons.Rounded.FastForward, contentDescription = null) },
        title = { Text(text = "Jump Interval") },
        text = {
            TextField(
                value = durationString,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                onValueChange = { durationString = it })
        }
    )


}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BlacklistedFoldersDialog(
    isVisible: Boolean,
    folders: List<String>,
    onFolderAdded: (String) -> Unit,
    onFolderDeleted: (String) -> Unit,
    onDismissRequest: () -> Unit,
) {

    if (!isVisible) return

    val context = LocalContext.current
    val directoryPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree(),
        onResult = { uri ->
            if (uri == null) return@rememberLauncherForActivityResult
            val documentTree = DocumentsContract.buildDocumentUriUsingTree(
                uri,
                DocumentsContract.getTreeDocumentId(uri)
            )
            val path = getPath(context, documentTree) ?: return@rememberLauncherForActivityResult
            onFolderAdded(path)
        }
    )


    AlertDialog(
        onDismissRequest = onDismissRequest,
        dismissButton = { TextButton(onClick = onDismissRequest) { Text(text = "Close") } },
        confirmButton = { },
        icon = { Icon(Icons.Rounded.Block, contentDescription = null) },
        title = { Text(text = "Blacklisted Folders") },
        text = {
            Column(verticalArrangement = Arrangement.SpaceBetween) {
                LazyColumn(modifier = Modifier) {
                    items(folders) {
                        Row(
                            modifier = Modifier.animateItemPlacement(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = it, modifier = Modifier.weight(1f))
                            Spacer(modifier = Modifier.width(4.dp))
                            IconButton(onClick = { onFolderDeleted(it) }) {
                                Icon(
                                    imageVector = Icons.Rounded.Delete,
                                    contentDescription = "Remove Folder from Blacklist"
                                )
                            }
                        }
                        if (it != folders.last()) {
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Divider(Modifier.fillMaxWidth())
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                    .fillMaxWidth()
                    .clip(
                        RoundedCornerShape(4.dp)
                    )
                    .clickable { directoryPicker.launch(null) }
                    .padding(8.dp)) {
                    Icon(imageVector = Icons.Rounded.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Add Path")
                }
            }
        }
    )

}


@Composable
fun AppThemeDialog(
    visible: Boolean,
    currentSelected: AppThemeUi,
    onDismissRequest: () -> Unit,
    onThemeSelected: (AppThemeUi) -> Unit,
) {
    if (!visible) return
    val optionsStrings = listOf("Follow System Settings", "Light", "Dark")
    val options = listOf(AppThemeUi.SYSTEM, AppThemeUi.LIGHT, AppThemeUi.DARK)
    val selectedOptionIndex by remember {
        mutableStateOf(
            options.indexOf(currentSelected).coerceAtLeast(0)
        )
    }
    AlertDialog(
        onDismissRequest = onDismissRequest,
        dismissButton = { TextButton(onClick = onDismissRequest) { Text(text = "Cancel") } },
        confirmButton = { },
        icon = { Icon(Icons.Rounded.LightMode, contentDescription = null) },
        title = { Text(text = "App Theme") },
        text = {
            Column {
                optionsStrings.forEachIndexed { index, option ->
                    val onSelected = {
                        if (index == selectedOptionIndex) {
                            Unit
                        } else {
                            onThemeSelected(options[index])
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = selectedOptionIndex == index,
                            onClick = { onSelected() }
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = option, modifier = Modifier.clickable { onSelected() })
                    }
                }
            }
        }
    )
}

@Composable
fun PlayerThemeDialog(
    visible: Boolean,
    currentSelected: PlayerThemeUi,
    onDismissRequest: () -> Unit,
    onThemeSelected: (PlayerThemeUi) -> Unit,
) {
    if (!visible) return
    val optionsStrings = listOf("Solid", "Blur")
    val options = listOf(PlayerThemeUi.SOLID, PlayerThemeUi.BLUR)
    val selectedOptionIndex by remember {
        mutableStateOf(
            options.indexOf(currentSelected).coerceAtLeast(0)
        )
    }
    AlertDialog(
        onDismissRequest = onDismissRequest,
        dismissButton = { TextButton(onClick = onDismissRequest) { Text(text = "Cancel") } },
        confirmButton = { },
        icon = { Icon(Icons.Rounded.BlurCircular, contentDescription = null) },
        title = { Text(text = "Player Theme") },
        text = {
            Column {
                optionsStrings.forEachIndexed { index, option ->
                    val onSelected = {
                        if (index == selectedOptionIndex) {
                            Unit
                        } else {
                            onThemeSelected(options[index])
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = selectedOptionIndex == index,
                            onClick = { onSelected() }
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = option, modifier = Modifier.clickable { onSelected() })
                    }
                }
            }
        }
    )
}


@Composable
fun SectionTitle(
    modifier: Modifier,
    title: String
) {
    Text(
        modifier = modifier,
        text = title,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        color = MaterialTheme.colorScheme.tertiary
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsTopAppBar(topAppBarScrollBehavior: TopAppBarScrollBehavior) {
    TopAppBar(
        title = { Text(text = "Settings", fontWeight = FontWeight.SemiBold) },
        scrollBehavior = topAppBarScrollBehavior
    )
}