package com.omar.nowplaying.ui

import android.annotation.SuppressLint
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import com.omar.musica.ui.common.LocalAppColorScheme
import com.omar.musica.ui.model.PlayerThemeUi


@SuppressLint("RestrictedApi")
@Composable
fun NowPlayingMaterialTheme(
    playerThemeUi: PlayerThemeUi,
    content: @Composable () -> Unit
) {

    val localAppColorScheme = LocalAppColorScheme.current
    val currentColorScheme = MaterialTheme.colorScheme
    val scheme =
        if (playerThemeUi == PlayerThemeUi.BLUR)
            localAppColorScheme.darkColorScheme
        else
            currentColorScheme

    val contentColor =
        if (playerThemeUi == PlayerThemeUi.BLUR)
            Color(0xEEFFFFFF)
        else LocalContentColor.current

    MaterialTheme(
        colorScheme = scheme
    ) {
        CompositionLocalProvider(
            LocalContentColor provides contentColor
        ) {
            content()
        }
    }

}