package com.omar.musica.store

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.omar.musica.database.dao.BlacklistedFoldersDao
import com.omar.musica.database.entities.BlacklistedFolderEntity
import com.omar.musica.model.AppTheme
import com.omar.musica.model.PlayerSettings
import com.omar.musica.model.UserPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class UserPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val blacklistDao: BlacklistedFoldersDao
) {

    fun getUserSettingsFlow(): Flow<UserPreferences> =
        combine(
            context.datastore.data.catch { emptyPreferences() },
            blacklistDao.getAllBlacklistedFoldersFlow()
        ) { settings, blacklistFolders ->
            mapPrefsToModel(settings, blacklistFolders)
        }

    fun jumpForwardIntervalFlow() = context.datastore.data
        .catch { emptyPreferences() }
        .map { it[JUMP_DURATION_KEY] ?: 10_000 }

    suspend fun changeTheme(appTheme: AppTheme) {
        context.datastore.edit {
            it[THEME_KEY] = appTheme.toString()
        }
    }

    suspend fun toggleCacheAlbumArt() {
        context.datastore.edit {
            it[CACHE_ALBUM_COVER_ART_KEY] = !(it[CACHE_ALBUM_COVER_ART_KEY] ?: true)
        }
    }

    suspend fun toggleDynamicColor() {
        context.datastore.edit {
            it[DYNAMIC_COLOR_KEY] = !(it[DYNAMIC_COLOR_KEY] ?: true)
        }
    }

    suspend fun deleteFolderFromBlacklist(folder: String) = withContext(Dispatchers.IO) {
        blacklistDao.deleteFolder(folder)
    }

    suspend fun addBlacklistedFolder(folder: String) = withContext(Dispatchers.IO) {
        blacklistDao.addFolder(BlacklistedFolderEntity(0, folder))
    }

    suspend fun changeJumpDurationMillis(duration: Int) {
        context.datastore.edit {
            it[JUMP_DURATION_KEY] = duration
        }
    }

    fun playerSettingsFlow() = context.datastore.data
        .catch { emptyPreferences() }
        .map {
            PlayerSettings(it[JUMP_DURATION_KEY] ?: 10_000)
        }

    private fun mapPrefsToModel(prefs: Preferences, blacklistedFolders: List<BlacklistedFolderEntity>) = UserPreferences(
        songsSortOrder = "",
        theme = AppTheme.valueOf(prefs[THEME_KEY] ?: "SYSTEM"),
        isUsingDynamicColor = prefs[DYNAMIC_COLOR_KEY] ?: true,
        cacheAlbumCoverArt = prefs[CACHE_ALBUM_COVER_ART_KEY] ?: true,
        excludedFolders = blacklistedFolders.map { it.folderPath },
        minDurationMillis = prefs[MIN_DURATION_MILLIS_KEY] ?: -1,
        jumpDuration = prefs[JUMP_DURATION_KEY] ?: 10000
    )

    companion object {
        val THEME_KEY = stringPreferencesKey("THEME")
        val DYNAMIC_COLOR_KEY = booleanPreferencesKey("DYNAMIC_COLOR")
        val MIN_DURATION_MILLIS_KEY = longPreferencesKey("MIN_DURATION_KEY")
        val CACHE_ALBUM_COVER_ART_KEY = booleanPreferencesKey("CACHE_ALBUM_COVER_ART")
        val JUMP_DURATION_KEY = intPreferencesKey("JUMP_DURATION_KEY")
    }

}