package com.omar.musica.ui.menu

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.omar.musica.store.model.song.Song
import com.omar.musica.ui.albumart.SongAlbumArtImage
import com.omar.musica.ui.albumart.inefficientAlbumArtImageLoader
import com.omar.musica.ui.albumart.toSongAlbumArtModel
import com.omar.musica.ui.theme.ManropeFontFamily
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongBottomSheetMenu(
    song: Song,
    bottomSheetMenuLayout: BottomSheetMenuLayout,
    visible: Boolean,
    onDismissRequest: () -> Unit
) {

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    val hide: (callback: () -> Unit) -> Unit = { callback ->
        scope.launch {
            sheetState.hide()
            callback()
            onDismissRequest()
        }
    }

    val imageSize = 42.dp

    if (visible)

        ModalBottomSheet(
            onDismissRequest = { hide({}) },
            sheetState = sheetState,
            dragHandle = null,
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 12.dp, bottom = 12.dp, top = 12.dp)
            ) {
                SongAlbumArtImage(
                    songAlbumArtModel = song.toSongAlbumArtModel(),
                    modifier = Modifier
                        .height(imageSize)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(8.dp)),

                    )
                Column(
                    modifier = Modifier.padding(horizontal = 12.dp)
                ) {
                    Text(
                        song.metadata.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontFamily = ManropeFontFamily,
                        maxLines = 1,
                        fontWeight = FontWeight.SemiBold,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        song.metadata.artistName.orEmpty() + "  •  " + song.metadata.albumName,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Normal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            HorizontalDivider(thickness = Dp.Hairline)

            Column(
                Modifier.verticalScroll(rememberScrollState())
            ) {
                Spacer(Modifier.height(12.dp))
                bottomSheetMenuLayout.menuItems.forEachIndexed { index, it ->

                    val interactionSource = remember { MutableInteractionSource() }

                    val isPressed by interactionSource.collectIsPressedAsState()
                    val scale by animateFloatAsState(
                        targetValue = if (isPressed) 0.97f else 1f,
                        animationSpec = tween(durationMillis = 100, easing = FastOutSlowInEasing),
                        label = "MenuItemScale"
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                interactionSource,
                                enabled = true,
                                indication = rememberRipple()
                            ) { hide(it.callback) }
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                            }
                            .padding(top = 12.dp, bottom = 12.dp, start = 12.dp, end = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = it.icon,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(start = imageSize / 4)
                                .size(20.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.width(24.dp))
                        Text(
                            text = it.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontFamily = ManropeFontFamily,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    if (bottomSheetMenuLayout.dividersPositions.contains(index))
                        HorizontalDivider(
                            thickness = Dp.Hairline,
                            modifier = Modifier.padding(
                                start = 20.dp + imageSize / 4 + 24.dp + 12.dp,
                                top = 8.dp,
                                bottom = 8.dp
                            )
                        )
                }
            }

        }

    LaunchedEffect(visible) {
        if (visible)
            sheetState.show()
    }


}

data class BottomSheetMenuLayout(
    val menuItems: List<MenuActionItem>,
    val dividersPositions: List<Int>,

    )