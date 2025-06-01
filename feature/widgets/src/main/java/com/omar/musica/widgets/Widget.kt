package com.omar.musica.widgets

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.unit.dp
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.Text
import com.omar.musica.playback.PlaybackManager
import dagger.Module
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.map

class WidgetReceiver : GlanceAppWidgetReceiver() {

  override val glanceAppWidget: GlanceAppWidget
    get() = Widget()

}


@EntryPoint
@InstallIn(SingletonComponent::class)
interface WidgetEntryPoint {
  fun getPlaybackManager(): PlaybackManager
}

private fun getPlaybackManager(appContext: Context): PlaybackManager {
  val hilt = EntryPointAccessors.fromApplication<WidgetEntryPoint>(appContext)
  return hilt.getPlaybackManager()
}

class Widget : GlanceAppWidget() {

  override suspend fun provideGlance(context: Context, id: GlanceId) {
    // Load data needed to render the AppWidget.
    // Use `withContext` to switch to another thread for long running
    // operations.
    val pM = getPlaybackManager(context.applicationContext)
    val title = pM.state.map {  it.core.currentPlayingSong?.metadata?.title ?: "No song playing" }

    provideContent {
      // create your AppWidget here
      Text(text = title.collectAsState(initial = "Skrrr").value)
    }
  }

  @Composable
  private fun MyContent() {
    Column(
      modifier = GlanceModifier.fillMaxSize(),
      verticalAlignment = Alignment.Top,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(text = "Where to?", modifier = GlanceModifier.padding(12.dp))
      Row(horizontalAlignment = Alignment.CenterHorizontally) {
        Button(
          text = "Home",
          onClick = {}
        )
        Button(
          text = "Work",
          onClick = {}
        )
      }
    }
  }

}