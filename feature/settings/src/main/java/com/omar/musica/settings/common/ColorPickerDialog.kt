package com.omar.musica.settings.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt


@Composable
fun ColorPickerDialog(
    initialColor: Color,
    onColorChanged: (Color) -> Unit,
    onDismissRequest: () -> Unit
) {

    var color by remember(initialColor) {
        mutableStateOf(initialColor)
    }


    AlertDialog(
        confirmButton = {
            TextButton(onClick = { onColorChanged(color) }) {
                Text(text = "Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = "Cancel")
            }
        },
        title = { Text(text = "Accent Color")},
        onDismissRequest = onDismissRequest,
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Color Preview
                Box(
                    modifier = Modifier
                        .fillMaxWidth(1.0f)
                        .heightIn(max = 200.dp)
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .drawBehind {
                            drawRect(color)
                        }
                )

                ColorComponentSlider(
                    currentValue = (color.red * 255).toInt(),
                    colorChar = 'R',
                    onValueChange = { color = color.copy(red = it.toFloat() / 255.0f) }
                )

                ColorComponentSlider(
                    currentValue = (color.green * 255).toInt(),
                    colorChar = 'G',
                    onValueChange = { color = color.copy(green = it.toFloat() / 255.0f) }
                )

                ColorComponentSlider(
                    currentValue = (color.blue * 255).toInt(),
                    colorChar = 'B',
                    onValueChange = { color = color.copy(blue = it.toFloat() / 255.0f) }
                )

            }
        })

}

@Composable
private fun ColorComponentSlider(
    currentValue: Int,
    colorChar: Char,
    onValueChange: (Int) -> Unit,
) {

    Row(verticalAlignment = Alignment.CenterVertically) {

        Text(text = "$colorChar")

        Slider(
            modifier = Modifier.weight(1f),
            value = currentValue.toFloat(),
            onValueChange = { onValueChange(it.roundToInt()) },
            valueRange = 0.0f..255.0f,
        )

        Text(text = "$currentValue")

    }

}