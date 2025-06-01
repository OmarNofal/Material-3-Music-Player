package com.omar.musica

import android.app.Application
import androidx.media3.common.BuildConfig
import com.omar.musica.widgets.WidgetManager
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class MusicaApplication : Application() {

  @Inject
  lateinit var widgetManager: WidgetManager

  override fun onCreate() {
    super.onCreate()
    if (BuildConfig.DEBUG) {
      Timber.plant(Timber.DebugTree())
    }
  }
}