package com.omar.nowplaying.queue


import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.DragHandle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.omar.musica.store.model.song.Song
import com.omar.musica.ui.common.LocalUserPreferences
import com.omar.musica.ui.songs.SongInfoRow
import kotlinx.coroutines.delay
import sh.calvin.reorderable.ReorderableItemScope
import kotlin.math.abs
import kotlin.math.roundToInt



@OptIn(ExperimentalFoundationApi::class)
@Composable
fun QueueSongRow(
    modifier: Modifier,
    songUi: Song,
    swipeToDeleteDelay: Int,
    reorderScope: ReorderableItemScope,
    onDragStarted: () -> Unit,
    onDragStopped: () -> Unit,
    onRemoveFromQueue: () -> Unit,
) {

    val density = LocalDensity.current
    val anchorState = remember {
        AnchoredDraggableState(
            SwipeToDeleteState.IDLE,
            anchors = DraggableAnchors {
                SwipeToDeleteState.LEFT at -100.0f
                SwipeToDeleteState.IDLE at 0.0f
                SwipeToDeleteState.RIGHT at 100.0f
            },
            positionalThreshold = { totalDistance: Float -> .5f * totalDistance },
            velocityThreshold = { with(density) { 50.dp.toPx() } },
            animationSpec = tween()
        )
    }

    var rowWidth by remember {
        mutableStateOf(0)
    }

    LaunchedEffect(key1 = anchorState.currentValue) {
        if (anchorState.currentValue != SwipeToDeleteState.IDLE) {
            delay(swipeToDeleteDelay.toLong())
            onRemoveFromQueue()
        }
    }

    Box(modifier = modifier
        .height(IntrinsicSize.Max)
        .onSizeChanged {
            val width = it.width
            rowWidth = width
            anchorState.updateAnchors(
                DraggableAnchors {
                    SwipeToDeleteState.LEFT at -width.toFloat()
                    SwipeToDeleteState.IDLE at 0.0f
                    SwipeToDeleteState.RIGHT at width.toFloat()
                },
                SwipeToDeleteState.IDLE
            )
        }) {

        DeleteBackground(
            modifier = Modifier.fillMaxSize(),
            swipeProgress = { anchorState.requireOffset() / rowWidth })

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .offset {
                    IntOffset(
                        anchorState
                            .requireOffset()
                            .roundToInt(), 0
                    )
                }
                .anchoredDraggable(anchorState, Orientation.Horizontal),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SongInfoRow(
                modifier = Modifier.weight(1f),
                song = songUi,
                efficientThumbnailLoading = LocalUserPreferences.current.librarySettings.cacheAlbumCoverArt
            )
            IconButton(
                onClick = {},
                modifier = with(reorderScope) {
                    Modifier.draggableHandle(
                        onDragStarted = { onDragStarted() },
                        onDragStopped = { onDragStopped() }
                    )
                }) {
                Icon(imageVector = Icons.Rounded.DragHandle, contentDescription = "Drag to Reorder")
            }
        }
    }

}

@Composable
fun DeleteBackground(
    modifier: Modifier,
    swipeProgress: () -> Float, // -1 to 1
) {

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Icon(
            modifier = Modifier
                .padding(start = 32.dp)
                .graphicsLayer {
                    val leftIconScale =
                        if (swipeProgress() > 0.0f)
                            (2.0f * abs(swipeProgress()) + 0.5f).coerceAtMost(1.0f)
                        else 0.0f
                    alpha = leftIconScale
                    scaleX = leftIconScale
                    scaleY = leftIconScale
                },
            imageVector = Icons.Rounded.Delete,
            contentDescription = null
        )

        Icon(
            modifier = Modifier
                .padding(end = 32.dp)
                .graphicsLayer {
                    val rightIconScale =
                        if (swipeProgress() < 0.0f)
                            (2.0f * abs(swipeProgress()) + 0.5f).coerceAtMost(1.0f)
                        else 0.0f
                    alpha = rightIconScale
                    scaleX = rightIconScale
                    scaleY = rightIconScale
                },
            imageVector = Icons.Rounded.Delete,
            contentDescription = null
        )
    }

}

enum class SwipeToDeleteState {
    LEFT, IDLE, RIGHT
}