package com.omar.musica.model.prefs


/**
 * Settings applied to the player
 */
data class PlayerSettings(
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



const val DEFAULT_JUMP_DURATION_MILLIS = 10_000