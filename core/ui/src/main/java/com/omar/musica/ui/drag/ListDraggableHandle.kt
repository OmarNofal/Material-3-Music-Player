package com.omar.musica.ui.drag

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.ArrowDropUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt


@Composable
fun ListDraggableHandle(
    modifier: Modifier,
    visible: Boolean,
    isSortedAlphabetically: Boolean,
    currentLetter: String,
    numberOfItems: Int,
    currentItem: Int,
    onScroll: (index: Int) -> Unit
) {

    var dragAreaHeightPx by remember { mutableIntStateOf(0) }

    var offset by remember { mutableStateOf(Offset.Zero) }

    var isDragging by remember { mutableStateOf(false) }

    val dragHandleSize = 56.dp
    val dragHandleSizePx = with(LocalDensity.current) { dragHandleSize.toPx().roundToInt() }

    AnimatedVisibility(
        modifier = modifier,
        visible = visible,
        enter = slideInHorizontally(
            initialOffsetX = { it },
            animationSpec = tween(400, easing = FastOutSlowInEasing)
        ),
        exit = slideOutHorizontally(
            targetOffsetX = { it },
            animationSpec = tween(400, easing = FastOutSlowInEasing)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .onGloballyPositioned { dragAreaHeightPx = it.size.height - dragHandleSizePx },
            contentAlignment = Alignment.TopEnd
        ) {

            val colorScheme = MaterialTheme.colorScheme

            // Handle
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset { IntOffset(0, offset.y.toInt()) },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {


                AnimatedVisibility(
                    visible = isSortedAlphabetically && isDragging,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                color = colorScheme.secondaryContainer,
                                shape = RoundedCornerShape(32.dp)
                            )
                            .padding(horizontal = 32.dp, vertical = 10.dp)
                    ) {
                        Text(
                            currentLetter.capitalize(Locale.current),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Medium,
                            color = colorScheme.onSecondaryContainer
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .draggable(
                            rememberDraggableState {
                                val newYOffset =
                                    (offset.y + it).coerceIn(
                                        0f,
                                        dragAreaHeightPx.toFloat()
                                    )
                                offset = if (newYOffset < 0f)
                                    Offset(0f, 0f)
                                else
                                    Offset(0f, newYOffset)

                                val percentage = offset.y / dragAreaHeightPx
                                val newItemIndex = (numberOfItems * percentage).roundToInt()
                                if (newItemIndex != currentItem)
                                    onScroll(newItemIndex)
                            },
                            Orientation.Vertical,
                            startDragImmediately = true,
                            onDragStarted = { isDragging = true },
                            onDragStopped = { isDragging = false }
                        ),
                    contentAlignment = Alignment.Center
                )
                {

                    // Half circle
                    Canvas(
                        modifier = Modifier
                            .size(dragHandleSize)
                            .offset(dragHandleSize / 2, 0.dp)
                    ) {
                        drawArc(
                            color = colorScheme.secondaryContainer,
                            startAngle = 90f,
                            sweepAngle = 180f,
                            useCenter = true,
                            topLeft = Offset.Zero,
                            size = size
                        )
                    }

                    // Icons
                    Box(
                        modifier = Modifier.offset(16.dp, 0.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowDropUp,
                            contentDescription = null,
                            modifier = Modifier
                                .size(36.dp)
                                .padding(bottom = 8.dp),
                            tint = colorScheme.onSecondaryContainer
                        )
                        Icon(
                            imageVector = Icons.Rounded.ArrowDropDown,
                            contentDescription = null,
                            modifier = Modifier
                                .size(36.dp)
                                .padding(top = 8.dp),
                            tint = colorScheme.onSecondaryContainer
                        )
                    }
                }
            }

        }
    }

    LaunchedEffect(numberOfItems, currentItem) {
        if (isDragging) return@LaunchedEffect
        val percentage = currentItem / numberOfItems.toFloat()
        offset = Offset(0f, dragAreaHeightPx * percentage)
    }

}

