package com.omar.musica.store

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob


private const val SETTINGS_FILE = "settings"

internal val Context.datastore by preferencesDataStore(SETTINGS_FILE)