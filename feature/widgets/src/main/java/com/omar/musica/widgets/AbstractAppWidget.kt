package com.omar.musica.widgets

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

abstract class AbstractAppWidget : GlanceAppWidget() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface WidgetManagerEntryPoint {
        fun getWidgetManager(): WidgetManager
    }


    fun getWidgetManager(context: Context) =
        EntryPointAccessors.fromApplication<WidgetManagerEntryPoint>(context.applicationContext)
            .getWidgetManager()
}