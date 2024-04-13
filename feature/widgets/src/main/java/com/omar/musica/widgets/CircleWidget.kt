package com.omar.musica.widgets

import android.R
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import coil.size.Size
import coil.transform.CircleCropTransformation
import com.omar.musica.widgets.ui.CircleWidgetUi
import com.omar.musica.widgets.ui.WidgetState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext


class CircleWidget : AbstractAppWidget() {

    override val sizeMode: SizeMode
        get() = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val widgetManager = getWidgetManager(context)

        provideContent {
            val uiState by widgetManager.state.collectAsState(WidgetState.NoQueue)
            CircleWidgetUi(uiState)
        }
    }


}

class CircleWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = CircleWidget()
}