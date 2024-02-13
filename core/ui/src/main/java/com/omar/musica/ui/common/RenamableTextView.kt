package com.omar.musica.ui.common

import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun RenamableTextView(
    modifier: Modifier,
    inRenameMode: Boolean,
    text: String,
    fontSize: Int,
    fontWeight: FontWeight,
    enableLongPressToEdit: Boolean = false,
    onEnableRenameMode: () -> Unit,
    onRename: (String) -> Unit
) {
    Box(modifier) {
        if (inRenameMode) {

            var textFieldValue by remember {
                mutableStateOf(
                    TextFieldValue(
                        text = text,
                        selection = TextRange(text.length, text.length)
                    )
                )
            }

            val focusRequester = remember { FocusRequester() }

            BasicTextField(
                modifier = Modifier
                    .padding(top = 2.dp)
                    .focusRequester(focusRequester)
                    .border(1.dp, color = MaterialTheme.colorScheme.primary),
                value = textFieldValue,
                textStyle = TextStyle(
                    fontWeight = fontWeight,
                    fontSize = fontSize.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                ),
                onValueChange = { textFieldValue = it },
                singleLine = true,
                maxLines = 1,
                keyboardActions = KeyboardActions(onDone = { onRename(textFieldValue.text) }),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface)
            )

            LaunchedEffect(key1 = Unit) {
                focusRequester.requestFocus()
            }

        } else {
            Text(
                modifier = if (enableLongPressToEdit) Modifier.pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = { onEnableRenameMode() }
                    )
                } else Modifier,
                text = text,
                fontWeight = fontWeight,
                fontSize = fontSize.sp
            )
        }
    }
}