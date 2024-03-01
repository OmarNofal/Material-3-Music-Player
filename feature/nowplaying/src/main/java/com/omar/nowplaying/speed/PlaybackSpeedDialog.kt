package com.omar.nowplaying.speed

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun PlaybackSpeedDialog(
    initSpeed: Float,
    initPitch: Float,
    onSetSpeed: (speed: Float, pitch: Float) -> Unit,
    onDismissRequest: () -> Unit
) {

    var speed by remember {
        mutableStateOf(initSpeed)
    }
    var pitch by remember {
        mutableStateOf(initPitch)
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = "Playback Speed") },
        confirmButton = {
            TextButton(onClick = { onSetSpeed(speed, pitch); onDismissRequest() }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = { speed = 1.0f; pitch = 1.0f; }) {
                Text(text = "Reset")
            }
        },
        text = {
            Column {

                Text(text = "Speed: ${"%.1f".format(speed)}x")
                Slider(
                    value = speed,
                    valueRange = 0.1f..2.0f,
                    steps = 18,
                    onValueChange = { speed = it }
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(text = "Pitch: ${"%.1f".format(pitch)}x")
                Slider(
                    value = pitch,
                    valueRange = 0.1f..2.0f,
                    steps = 18,
                    onValueChange = { pitch = it }
                )


            }
        }
    )


}

@Composable
fun rememberPlaybackSpeedDialog(
    viewModel: PlaybackSpeedViewModel
): PlaybackSpeedDialog {

    var isShown by remember { mutableStateOf(false) }

    if (isShown) {
        val (initSpeed, initPitch) = remember {
            viewModel.playbackParameters
        }
        PlaybackSpeedDialog(
            initSpeed, initPitch,
            onSetSpeed = viewModel::setParameters
        ) { isShown = false }
    }

    return remember {
        object : PlaybackSpeedDialog {
            override fun launch() {
                isShown = true
            }
        }
    }
}

interface PlaybackSpeedDialog {
    fun launch()
}