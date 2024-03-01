package com.omar.musica.store.preferences

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore


private const val SETTINGS_FILE = "settings"

internal val Context.datastore by preferencesDataStore(SETTINGS_FILE)