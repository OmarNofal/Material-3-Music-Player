package com.omar.musica.ui.model

import androidx.compose.runtime.Stable
import com.omar.musica.model.PlayerSettings
import com.omar.musica.model.PlayerTheme


@Stable
/**
 * Settings applied to the player
 */
data class PlayerSettingsUi(
    /**
     * The amount of time skipped when jumping forward in milliseconds
     */
    val jumpInterval: Int
)



fun PlayerSettings.toPlayerSettingsUi() =
    PlayerSettingsUi(jumpInterval)