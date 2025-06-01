package com.omar.musica.albums.ui.effects

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.omar.musica.ui.common.LocalUserPreferences
import com.omar.musica.ui.model.AppThemeUi


@Composable
fun AlbumDetailStatusBarColorEffect(
  collapsePercentage: Float,
) {
  val window = (LocalContext.current as Activity).window
  val view = LocalView.current

  val windowsInsetsController = WindowCompat.getInsetsController(window, view)
  val oldColor = remember { window.statusBarColor }
  val oldTheme = remember { windowsInsetsController.isAppearanceLightStatusBars }

  val isDarkTheme =
    when (LocalUserPreferences.current.uiSettings.theme) {
      AppThemeUi.LIGHT -> false
      AppThemeUi.DARK -> true
      else -> isSystemInDarkTheme()
    }
  DisposableEffect(Unit) {
    window.statusBarColor = 0x33000000
    windowsInsetsController.isAppearanceLightStatusBars = false

    onDispose {
      window.statusBarColor = oldColor
      windowsInsetsController.isAppearanceLightStatusBars = oldTheme
    }
  }
}