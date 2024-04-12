package com.omar.musica.widgets

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.text.Text
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

class PlaybackWidget : GlanceAppWidget() {


    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface WidgetManagerEntryPoint {
        fun getWidgetManager(): WidgetManager
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val viewModel =
            EntryPointAccessors.fromApplication<WidgetManagerEntryPoint>(context.applicationContext)
                .getWidgetManager()
        provideContent { Content(viewModel) }
    }

    @Composable
    fun Content(
        viewModel: WidgetManager
    ) {

        val state by viewModel.state.collectAsState(initial = WidgetState.NoQueue)

        if (state is WidgetState.NoQueue)
            Text(text = "Nothing Running")
        else if (state is WidgetState.Playback)
            Text(text = (state as WidgetState.Playback).title)
    }

}