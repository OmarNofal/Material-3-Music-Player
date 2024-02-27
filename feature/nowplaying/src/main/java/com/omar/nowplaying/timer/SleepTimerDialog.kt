package com.omar.nowplaying.timer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerLayoutType
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt



@Composable
fun SleepTimerDialog(
    onSetTimer: (minutes: Int, finishLastSong: Boolean) -> Unit,
    onDeleteTimer: () -> Unit,
    onDismissRequest: () -> Unit
) {

    var minutes by remember {
        mutableStateOf(0.0f)
    }
    var finishLastSong by remember {
        mutableStateOf(false)
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = "Sleep Timer") },
        confirmButton = {
            TextButton(onClick = { onSetTimer(minutes.roundToInt(), finishLastSong); onDismissRequest() }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDeleteTimer(); onDismissRequest() }) {
                Text(text = "Delete Timer")
            }
        },
        text = {
            Column {

                Row(verticalAlignment = Alignment.CenterVertically){
                    Slider(
                        modifier = Modifier.weight(1f),
                        value = minutes,
                        valueRange = 0.0f..120.0f,
                        onValueChange = { minutes = it }
                    )
                    Text(text = "${minutes.roundToInt()} mins")
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(checked = finishLastSong, onCheckedChange = { finishLastSong = it } )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Finish last song", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    )


}

@Composable
fun rememberSleepTimerDialog(
    onSetTimer: (minutes: Int, finishLastSong: Boolean) -> Unit,
    onDeleteTimer: () -> Unit
): SleepTimerDialog {

    var isShown by remember { mutableStateOf(false) }

    if (isShown) {
        SleepTimerDialog(onSetTimer = onSetTimer, onDeleteTimer = onDeleteTimer) { isShown = false }
    }

    return remember {
        object : SleepTimerDialog {
            override fun launch() {
                isShown = true
            }
        }
    }
}

interface SleepTimerDialog {
    fun launch()
}