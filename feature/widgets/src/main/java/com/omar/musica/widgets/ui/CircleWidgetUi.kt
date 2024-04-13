package com.omar.musica.widgets.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.min
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.ImageProvider
import androidx.glance.LocalSize
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.size
import androidx.glance.unit.ColorProvider
import androidx.palette.graphics.Palette
import com.omar.musica.widgets.R

@Composable
fun CircleWidgetUi(
    state: WidgetState
) {

    if (state !is WidgetState.Playback)
        return

    Box(
        contentAlignment = Alignment.Center,
        modifier = GlanceModifier.fillMaxSize()
    )
    {
        val size = LocalSize.current
        val imageSize = min(size.width, size.height) * 0.95f


        Image(modifier = GlanceModifier.size(imageSize), bitmap = state.image)

        Box(
            modifier = GlanceModifier.size(imageSize * 1.1f),
            contentAlignment = Alignment.BottomCenter
        ) {
            Box(
                modifier = GlanceModifier.size(imageSize / 5),
                contentAlignment = Alignment.Center
            ) {

                val color = remember(state.image) {
                    Palette.from(state.image!!)
                        .generate()
                }

                val buttonColor = Color(color.getDominantColor(0x000000))
                val iconColor = Color(color.dominantSwatch?.bodyTextColor ?: 0x000000)

                androidx.glance.Image(
                    modifier = GlanceModifier.fillMaxSize(),
                    provider = ImageProvider(R.drawable.rounded_button),
                    contentDescription = "",
                    colorFilter = ColorFilter.tint(ColorProvider(buttonColor))
                )

                PlayButton(
                    modifier = GlanceModifier,
                    isPlaying = state.isPlaying,
                    color = ColorProvider(iconColor)
                )
            }
        }
    }

}
