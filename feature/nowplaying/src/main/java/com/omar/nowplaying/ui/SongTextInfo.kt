package com.omar.nowplaying.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.MarqueeAnimationMode
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.omar.musica.store.model.song.Song

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SongTextInfo(
    modifier: Modifier,
    song: Song,
    showArtist: Boolean = true,
    showAlbum: Boolean = true,
    marqueeEffect: Boolean = true,
) {


    Column(modifier = modifier) {

        AnimatedContent(
            modifier = Modifier.fillMaxWidth(),
            targetState = song.metadata.title,
            transitionSpec = { fadeIn(tween(delayMillis = 150)) togetherWith fadeOut(tween(durationMillis = 150)) }
        ) { title ->
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (marqueeEffect)
                            Modifier.basicMarquee(
                                iterations = Int.MAX_VALUE,
                                delayMillis = 1000,
                                animationMode = MarqueeAnimationMode.Immediately
                            )
                        else Modifier
                    ),
                text = title,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 22.sp,
                maxLines = 1
            )
        }
        if (showArtist) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = song.metadata.artistName ?: "<unknown>",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                maxLines = 1,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        }

        if (showAlbum) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = song.metadata.albumName ?: "<unknown>",
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                maxLines = 1
            )
        }
    }

}

