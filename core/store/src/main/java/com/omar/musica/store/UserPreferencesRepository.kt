package com.omar.musica.store

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.omar.musica.model.AppTheme
import com.omar.musica.model.UserPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class UserPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun getUserSettingsFlow(): Flow<UserPreferences> = context.datastore
        .data
        .catch {
            emptyPreferences()
        }.map { settings ->
            mapPrefsToModel(settings)
        }


    suspend fun changeTheme(appTheme: AppTheme) {
        context.datastore.edit {
            it[THEME_KEY] = appTheme.toString()
        }
    }

    private fun mapPrefsToModel(prefs: Preferences) = UserPreferences(
        songsSortOrder = "",
        theme = AppTheme.valueOf(prefs[THEME_KEY] ?: "SYSTEM"),
        isUsingDynamicColor = prefs[DYNAMIC_COLOR_KEY] ?: true,
        cacheAlbumCoverArt = prefs[CACHE_ALBUM_COVER_ART_KEY] ?: true,
        excludedFolders = listOf(),
        minDurationMillis = prefs[MIN_DURATION_MILLIS_KEY] ?: -1
    )

    companion object {
        val THEME_KEY = stringPreferencesKey("THEME")
        val DYNAMIC_COLOR_KEY = booleanPreferencesKey("DYNAMIC_COLOR")
        val MIN_DURATION_MILLIS_KEY = longPreferencesKey("MIN_DURATION_KEY")
        val CACHE_ALBUM_COVER_ART_KEY = booleanPreferencesKey("CACHE_ALBUM_COVER_ART")
    }

}