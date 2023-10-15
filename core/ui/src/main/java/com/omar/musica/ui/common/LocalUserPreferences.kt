package com.omar.musica.ui.common

import androidx.compose.runtime.compositionLocalOf
import com.omar.musica.ui.model.UserPreferencesUi


val LocalUserPreferences = compositionLocalOf<UserPreferencesUi> { throw IllegalArgumentException("Not provided") }