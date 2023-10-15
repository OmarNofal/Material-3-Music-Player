package com.omar.musica.model


/**
 * Settings applied to the player
 */
data class PlayerSettings(
    /**
     * The amount of time skipped when jumping forward in milliseconds
     */
    val jumpInterval: Int
) {
    companion object {
        val DEFAULT_PLAYER_SETTINGS = PlayerSettings(DEFAULT_JUMP_DURATION_MILLIS)
    }
}

const val DEFAULT_JUMP_DURATION_MILLIS = 10_000