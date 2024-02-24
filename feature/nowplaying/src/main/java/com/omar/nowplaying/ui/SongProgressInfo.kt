package com.omar.nowplaying.ui

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.omar.musica.ui.millisToTime
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive


@Composable
fun SongProgressInfo(
    modifier: Modifier,
    songDuration: Long,
    songProgressProvider: () -> Float,
    onUserSeek: (progress: Float) -> Unit
) {


    var currentProgress by remember {
        mutableFloatStateOf(0.0f)
    }

    // Periodically get the progress
    LaunchedEffect(key1 = Unit) {
        while (isActive) {
            currentProgress = songProgressProvider()
            delay(500)
        }
    }

    val songLength = remember(songDuration) {
        songDuration.millisToTime()
    }

    var userSetSliderValue by remember {
        mutableFloatStateOf(0.0f)
    }

    // When the user removes his finger from the slider,
    // the slider will return to the initial position it was on,
    // it is subtle but annoying, so we add a delay
    // to give time for the player to change the position of the song.
    var useSongProgress by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(key1 = useSongProgress) {
        if (!useSongProgress) {
            delay(1000)
            if (isActive) useSongProgress = true
        }
    }

    val sliderInteractionSource = remember { MutableInteractionSource() }
    val isPressed by sliderInteractionSource.collectIsDraggedAsState()

    val progressShown =
        remember(useSongProgress, isPressed, userSetSliderValue, currentProgress) {
            if (useSongProgress && !isPressed) currentProgress else userSetSliderValue
        }

    val timestampShown = remember(songDuration, progressShown) {
        (songDuration * progressShown).toLong().millisToTime()
    }

    Column(modifier) {

        Slider(
            value = progressShown,
            onValueChange = { userSetSliderValue = it },
            modifier = Modifier
                .fillMaxWidth(),
            enabled = true,
            onValueChangeFinished = {
                onUserSeek(userSetSliderValue); useSongProgress = false
            },
            interactionSource = sliderInteractionSource
        )

        Row(
            Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = timestampShown,
                fontSize = 10.sp,
                maxLines = 1,
                fontWeight = FontWeight.Light
            )

            Text(
                text = songLength,
                fontSize = 10.sp,
                maxLines = 1,
                fontWeight = FontWeight.Light
            )

        }
    }
}
