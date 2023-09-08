package com.omar.musica.ui.common

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


@Composable
fun SongDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    actions: List<MenuActionItem>
) {

    DropdownMenu(expanded = expanded, onDismissRequest = onDismissRequest) {

        actions.forEach{
            DropdownMenuItem(text = { Text(text = it.title)} , onClick = { onDismissRequest(); it.callback()})
        }

    }


}