package com.omar.musica.widgets.ui

import android.content.ComponentName
import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.omar.musica.widgets.NextSongAction
import com.omar.musica.widgets.PreviousSongAction
import com.omar.musica.widgets.R
import com.omar.musica.widgets.TogglePlaybackAction


@Composable
fun CardWidgetUi(
    state: WidgetState
) {

    if (state is WidgetState.NoQueue) {
        Text(text = "Nothing Running")
        return
    }

    val safeState = state as WidgetState.Playback
    val context = LocalContext.current

    Row(
        modifier = GlanceModifier.fillMaxWidth().height(100.dp).background(Color.White)
            .clickable(actionStartActivity(ComponentName(context, "com.omar.musica.MainActivity"))),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Image(modifier = GlanceModifier.size(100.dp, 100.dp), bitmap = safeState.image)

        Spacer(GlanceModifier.width(8.dp))

        Column(
            GlanceModifier.fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SongTextInfo(
                modifier = GlanceModifier.defaultWeight(),
                title = safeState.title,
                artist = safeState.artist
            )

            Controls(modifier = GlanceModifier.fillMaxWidth(), isPlaying = safeState.isPlaying)
        }

    }

}

@Composable
fun Controls(
    modifier: GlanceModifier,
    iconSize: Dp = 32.dp,
    isPlaying: Boolean
) {
    val context = LocalContext.current
    Row(
        modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        androidx.glance.Image(
            modifier = GlanceModifier.size(iconSize)
                .clickable(actionRunCallback<PreviousSongAction>()),
            provider = ImageProvider(R.drawable.round_skip_previous_24),
            contentDescription = "Previous Song",
        )

        Spacer(GlanceModifier.width(24.dp))

        PlayButton(modifier = GlanceModifier.size(iconSize + 3.dp), isPlaying)

        Spacer(GlanceModifier.width(24.dp))

        androidx.glance.Image(
            modifier = GlanceModifier.size(iconSize)
                .clickable(actionRunCallback<NextSongAction>()),
            provider = ImageProvider(R.drawable.round_skip_next_24),
            contentDescription = "Previous Song",
        )

    }
}

@Composable
fun PlayButton(
    modifier: GlanceModifier,
    isPlaying: Boolean,
    color: ColorProvider = ColorProvider(Color.Black),
) {
    val icon =
        if (isPlaying)
            R.drawable.pause
        else R.drawable.play

    androidx.glance.Image(
        modifier = modifier.clickable(actionRunCallback<TogglePlaybackAction>()),
        provider = ImageProvider(icon),
        contentDescription = "",
        colorFilter = ColorFilter.tint(color)
    )
}

@Composable
fun SongTextInfo(
    modifier: GlanceModifier,
    title: String,
    artist: String
) {
    Column(modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(text = title, style = TextStyle())
        Spacer(GlanceModifier.height(4.dp))
        Text(text = artist)
    }
}

@Composable
fun Image(
    modifier: GlanceModifier,
    bitmap: Bitmap?
) {
    val provider =
        if (bitmap == null)
            ImageProvider(com.omar.musica.ui.R.drawable.placeholder)
        else
            ImageProvider(bitmap)

    androidx.glance.Image(
        modifier = modifier,
        provider = provider,
        contentDescription = "",
        contentScale = ContentScale.Crop
    )
}
