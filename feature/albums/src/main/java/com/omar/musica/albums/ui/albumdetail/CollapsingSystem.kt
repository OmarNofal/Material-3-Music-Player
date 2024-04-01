package com.omar.musica.albums.ui.albumdetail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Velocity

class CollapsingSystem {

    var collapsePercentage by mutableFloatStateOf(0.0f)

    var screenWidthPx by mutableIntStateOf(0)

    var topBarHeightPx by mutableIntStateOf(0)

    val totalCollapsableHeightPx: Int get() = screenWidthPx - topBarHeightPx

    val nestedScrollConnection = object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            if (available.y > 0) return Offset.Zero
            val availableY = -available.y
            val scrolledPercentage = availableY / totalCollapsableHeightPx
            val oldPercentage = collapsePercentage
            val newPercentage = (collapsePercentage + scrolledPercentage).coerceIn(0.0f, 1.0f)
            val totalConsumed = (newPercentage - oldPercentage) * -1
            collapsePercentage = newPercentage
            return Offset(0.0f, totalConsumed * totalCollapsableHeightPx)
        }

        override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
            return super.onPostFling(consumed, available)
        }

        override fun onPostScroll(
            consumed: Offset,
            available: Offset,
            source: NestedScrollSource
        ): Offset {
            if (consumed.y > 0) return Offset(0.0f, 0.0f)
            val availableY = -available.y
            val scrolledPercentage = availableY / totalCollapsableHeightPx
            val oldPercentage = collapsePercentage
            val newPercentage = (collapsePercentage + scrolledPercentage).coerceIn(0.0f, 1.0f)
            val totalConsumed = (newPercentage - oldPercentage) * -1
            collapsePercentage = newPercentage
            return Offset(0.0f, totalConsumed * totalCollapsableHeightPx)
        }
    }

}