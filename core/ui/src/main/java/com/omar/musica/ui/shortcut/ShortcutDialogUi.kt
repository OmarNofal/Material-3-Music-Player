package com.omar.musica.ui.shortcut

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AppShortcut
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun ShortcutDialogUi(
    listName: String,
    onSubmit: (shortcutName: String, action: ShortcutAction) -> Unit,
    onDismissRequest: () -> Unit,
) {

    var shortcutName by remember(listName) {
        mutableStateOf(listName)
    }

    var shortcutAction by remember(listName) {
        mutableStateOf(ShortcutAction.PLAY)
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        icon = {
            Icon(imageVector = Icons.Rounded.AppShortcut, contentDescription = "")
        },
        confirmButton = {
            TextButton(onClick = { onSubmit(shortcutName, shortcutAction) }) {
                Text(text = "Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = "Cancel")
            }
        },
        title = { Text(text = "Create shortcut for $listName", maxLines = 1, overflow = TextOverflow.Ellipsis) },
        text = {
            LazyColumn {

                item {
                    Text(
                        modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
                        text = "When I click the shortcut, do..."
                    )
                }

                ShortcutAction.entries.forEach { action ->
                    item {
                        ShortcutOption(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(4.dp))
                                .clickable { shortcutAction = action }
                                .padding(
                                    horizontal = 4.dp,
                                    vertical = 8.dp
                                ),
                            action = action,
                            isSelected = action == shortcutAction,
                            listName = listName
                        )
                    }
                }

                item {
                    HorizontalDivider(
                        modifier = Modifier
                            .padding(vertical = 12.dp),
                    )
                }

                item {
                    TextField(
                        value = shortcutName,
                        onValueChange = { shortcutName = it },
                        label = { Text(text = "Shortcut Name") },
                    )
                }
            }
        }
    )

}


@Composable
private fun ShortcutOption(
    modifier: Modifier,
    action: ShortcutAction,
    isSelected: Boolean,
    listName: String
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = isSelected, onClick = null)
        Text(
            modifier = Modifier.padding(start = 6.dp),
            text = actionTitleFromShortcutAction(listName, action),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

internal fun actionTitleFromShortcutAction(listName: String, action: ShortcutAction): String =
    when (action) {
        ShortcutAction.PLAY -> "Play $listName"
        ShortcutAction.SHUFFLE -> "Shuffle $listName"
        ShortcutAction.OPEN_IN_APP -> "Open in the app"
    }

@Preview
@Composable
private fun DialogPreview() {
    ShortcutDialogUi(listName = "Lana Del Rey", onSubmit = { a, b -> Unit }) {
        
    }
}