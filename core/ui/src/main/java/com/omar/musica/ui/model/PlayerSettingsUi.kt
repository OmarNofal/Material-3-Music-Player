package com.omar.musica.ui.model

import androidx.compose.runtime.Stable
import com.omar.musica.model.prefs.PlayerSettings


@Stable
/**
 * Settings applied to the player
 */
data class PlayerSettingsUi(
    /**
     * The amount of time skipped when jumping forward in milliseconds
     */
    val jumpInterval: Int,

    /**
     * Pause when volume reaches zero?
     */
    val pauseOnVolumeZero: Boolean,

    /**
     * Should we start playback when volume increases
     * if it was paused before due to zero volume
     */
    val resumeWhenVolumeIncreases: Boolean,
)



fun PlayerSettings.toPlayerSettingsUi() =
    PlayerSettingsUi(jumpInterval, pauseOnVolumeZero, resumeWhenVolumeIncreases)