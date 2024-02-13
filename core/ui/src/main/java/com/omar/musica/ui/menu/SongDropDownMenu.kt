package com.omar.musica.ui.menu

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun SongDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    actions: List<MenuActionItem>
) {

    DropdownMenu(expanded = expanded, onDismissRequest = onDismissRequest) {

        actions.forEach{
            DropdownMenuItem(modifier = Modifier.padding(end = 4.dp), text = { Text(text = it.title)} , onClick = { onDismissRequest(); it.callback()})
        }

    }


}