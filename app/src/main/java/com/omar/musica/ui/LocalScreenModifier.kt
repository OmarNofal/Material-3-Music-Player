package com.omar.musica.ui

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier


val LocalScreenModifier =
    staticCompositionLocalOf<Modifier> { throw IllegalArgumentException("No modifier passed") }