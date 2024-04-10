package com.omar.musica.ui.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector


@Composable
fun InputStringDialog(
    title: String,
    placeholder: String? = null,
    icon: ImageVector? = null,
    focusRequester: FocusRequester = FocusRequester(),
    isInputValid: (String) -> Boolean,
    onConfirm: (String) -> Unit,
    onDismissRequest: () -> Unit,
) {

    var input by remember {
        mutableStateOf("")
    }

    val isError = remember(input) { !isInputValid(input) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        icon = if (icon != null) {
            { Icon(imageVector = icon, contentDescription = null) }
        } else null,
        title = { Text(text = title) },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = "Cancel")
            }
        },
        confirmButton = {
            TextButton(
                onClick = { if (!isError) onConfirm(input) },
                enabled = !isError
            ) {
                Text(text = "Confirm")
            }
        },
        text = {
            TextField(
                modifier = Modifier.focusRequester(focusRequester),
                value = input,
                onValueChange = {
                    input = it
                },
                isError = isError,
                placeholder = { Text(text = placeholder.orEmpty()) }
            )
        }
    )

    LaunchedEffect(key1 = Unit) {
        focusRequester.requestFocus()
    }

}