package com.omar.nowplaying.lyrics

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupPositionProvider

fun Modifier.shimmerLoadingAnimation(
  widthOfShadowBrush: Int = 600,
  angleOfAxisY: Float = 270f,
  durationMillis: Int = 800,
): Modifier {
  return composed {
    val shimmerColors = listOf(
      Color.White.copy(alpha = 0.3f),
      Color.White.copy(alpha = 0.5f),
      Color.White.copy(alpha = 1.0f),
      Color.White.copy(alpha = 0.5f),
      Color.White.copy(alpha = 0.3f),
    )
    val transition = rememberInfiniteTransition(label = "")

    val translateAnimation = transition.animateFloat(
      initialValue = 0f,
      targetValue = (durationMillis + widthOfShadowBrush).toFloat(),
      animationSpec = infiniteRepeatable(
        animation = tween(
          durationMillis = durationMillis,
          easing = LinearEasing,
        ),
        repeatMode = RepeatMode.Restart,
      ),
      label = "Shimmer loading animation",
    )

    this.drawWithContent {
      drawContent()
      drawRect(
        Brush.linearGradient(
          colors = shimmerColors,
          start = Offset(x = translateAnimation.value - widthOfShadowBrush, y = 0.0f),
          end = Offset(x = translateAnimation.value, y = angleOfAxisY),
        ), blendMode = BlendMode.DstIn
      )
    }
  }
}

class ContextMenuPopupProvider : PopupPositionProvider {
  override fun calculatePosition(
    anchorBounds: IntRect,
    windowSize: IntSize,
    layoutDirection: LayoutDirection,
    popupContentSize: IntSize
  ): IntOffset {
    val popupHeight = popupContentSize.height

    val availableHeight = anchorBounds.topLeft.y

    return if (availableHeight >= popupHeight + 40) return IntOffset(
      anchorBounds.topLeft.x,
      anchorBounds.topLeft.y - popupHeight
    )
    else IntOffset(anchorBounds.topLeft.x, anchorBounds.bottomLeft.y + popupHeight)
  }
}


@Composable
fun LineContextMenu(
  modifier: Modifier,
  onCopy: () -> Unit,
  onShare: () -> Unit
) {

  AnimatedVisibility(
    visible = true, enter = fadeIn(), exit = fadeOut()
  ) {
    Row(
      modifier
        .clip(RoundedCornerShape(8.dp))
        .background(MaterialTheme.colorScheme.secondaryContainer),
      verticalAlignment = Alignment.CenterVertically,

      ) {
      Text(
        text = "Copy",
        modifier = Modifier
          .clickable { onCopy() }
          .padding(start = 12.dp, top = 12.dp, bottom = 12.dp, end = 8.dp)
      )
      VerticalDivider(color = MaterialTheme.colorScheme.onSecondaryContainer)
      Text(text = "Share",
        modifier = Modifier
          .clickable { onShare() }
          .padding(start = 12.dp, top = 12.dp, bottom = 12.dp, end = 8.dp)
      )
    }
  }
}
