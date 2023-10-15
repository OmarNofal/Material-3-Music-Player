package com.omar.musica.settings

import android.os.Build
import android.os.FileUtils
import android.provider.DocumentsContract
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Block
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toFile
import androidx.hilt.navigation.compose.hiltViewModel
import com.omar.musica.model.AppTheme
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
        onThemeSelected = settingsViewModel::onThemeSelected,
        onToggleDynamicColor = settingsViewModel::toggleDynamicColorScheme,
        onFolderDeleted = settingsViewModel::onFolderDeleted,
        onFolderAdded = settingsViewModel::onFolderAdded
    )
}

@Composable
fun SettingsScreen(
    modifier: Modifier,
    state: SettingsState,
    onThemeSelected: (AppTheme) -> Unit,
    onToggleDynamicColor: () -> Unit,
    onFolderDeleted: (String) -> Unit,
    onFolderAdded: (String) -> Unit
) {

    Scaffold(
        modifier = modifier,
        topBar = { SettingsTopAppBar() }
    )
    { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues), contentAlignment = Alignment.Center
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
                    onThemeSelected = onThemeSelected,
                    onToggleDynamicColor = onToggleDynamicColor,
                    onFolderDeleted = onFolderDeleted,
                    onFolderAdded = onFolderAdded
                )
            }

        }
    }

}


@Composable
fun SettingsList(
    modifier: Modifier,
    userPreferences: UserPreferencesUi,
    onThemeSelected: (AppTheme) -> Unit,
    onToggleDynamicColor: () -> Unit,
    onFolderDeleted: (String) -> Unit,
    onFolderAdded: (String) -> Unit
) {
    val sectionTitleModifier = Modifier
        .fillMaxWidth()
        .padding(start = 32.dp, top = 16.dp)

    LazyColumn(
        modifier
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
                currentSelected = userPreferences.theme,
                onDismissRequest = { appThemeDialogVisible = false },
                onThemeSelected = {
                    appThemeDialogVisible = false
                    onThemeSelected(it)
                }
            )
            Column(modifier = Modifier
                .fillMaxWidth()
                .clickable { appThemeDialogVisible = true }
                .padding(horizontal = 32.dp, vertical = 16.dp)) {
                Text(text = "App Theme", fontSize = 16.sp)
                val text = when (userPreferences.theme) {
                    AppTheme.SYSTEM -> "Follow System Settings"
                    AppTheme.LIGHT -> "Light"
                    AppTheme.DARK -> "Dark"
                }
                Text(
                    text = text,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            item {
                Divider(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 32.dp)
                )
            }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onToggleDynamicColor() }
                        .padding(horizontal = 32.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Dynamic Color Scheme")
                    Switch(
                        checked = userPreferences.isUsingDynamicColor,
                        onCheckedChange = { onToggleDynamicColor() })
                }
            }
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
                folders = userPreferences.excludedFolders,
                onFolderAdded = { onFolderAdded(it) },
                onFolderDeleted = onFolderDeleted,
                onDismissRequest = { blacklistDialogVisible = false }
            )
            Column(modifier = Modifier
                .fillMaxWidth()
                .clickable { blacklistDialogVisible = true }
                .padding(horizontal = 32.dp, vertical = 16.dp)) {
                Text(text = "Blacklisted Folders", fontSize = 16.sp)
                Text(
                    text = "Music in these folders will not appear in the app",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

    }


}


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
            val documentTree = DocumentsContract.buildDocumentUriUsingTree(uri,
                DocumentsContract.getTreeDocumentId(uri));
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
            Column {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(folders) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = it, modifier = Modifier.weight(1f))
                            Spacer(modifier = Modifier.width(4.dp))
                            IconButton(onClick =  { onFolderDeleted(it) } ) {
                                Icon(imageVector = Icons.Rounded.Delete, contentDescription = "Remove Folder from Blacklist")
                            }
                        }
                    }
                }

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
    currentSelected: AppTheme,
    onDismissRequest: () -> Unit,
    onThemeSelected: (AppTheme) -> Unit,
) {
    if (!visible) return
    val optionsStrings = listOf("Follow System Settings", "Light", "Dark")
    val options = listOf(AppTheme.SYSTEM, AppTheme.LIGHT, AppTheme.DARK)
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
fun SettingsTopAppBar() {
    TopAppBar(title = { Text(text = "Settings", fontWeight = FontWeight.SemiBold) })
}