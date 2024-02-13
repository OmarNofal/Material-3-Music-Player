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
import com.omar.musica.database.entities.prefs.BlacklistedFolderEntity
import com.omar.musica.model.prefs.AppTheme
import com.omar.musica.model.prefs.DEFAULT_JUMP_DURATION_MILLIS
import com.omar.musica.model.prefs.LibrarySettings
import com.omar.musica.model.prefs.PlayerSettings
import com.omar.musica.model.prefs.PlayerTheme
import com.omar.musica.model.SortOption
import com.omar.musica.model.prefs.UiSettings
import com.omar.musica.model.prefs.UserPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class UserPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val blacklistDao: BlacklistedFoldersDao
) {


    val userSettingsFlow: Flow<UserPreferences> =
        combine(
            context.datastore.data.catch { emptyPreferences() },
            blacklistDao.getAllBlacklistedFoldersFlow()
        ) { settings, blacklistFolders ->
            mapPrefsToModel(settings, blacklistFolders)
        }

    val librarySettingsFlow = userSettingsFlow
        .map {
            it.librarySettings
        }.distinctUntilChanged()

    val playerSettingsFlow = userSettingsFlow
        .map {
            it.playerSettings
        }.distinctUntilChanged()


    suspend fun saveCurrentPosition(songUriString: String, position: Long) {
        context.datastore.edit {
            it[SONG_URI_KEY] = songUriString
            it[SONG_POSITION_KEY] = position
        }
    }

    suspend fun getSavedPosition(): Pair<String?, Long> {
        val prefs = context.datastore.data.first()
        val songUri = prefs[SONG_URI_KEY]
        val songPosition = prefs[SONG_POSITION_KEY] ?: 0
        return songUri to songPosition
    }

    suspend fun changeLibrarySortOrder(sortOption: SortOption, isAscending: Boolean) {
        context.datastore.edit {
            it[SORT_ORDER_KEY] = "${sortOption}:$isAscending"
        }
    }

    suspend fun changeTheme(appTheme: AppTheme) {
        context.datastore.edit {
            it[THEME_KEY] = appTheme.toString()
        }
    }

    suspend fun toggleBlackBackgroundForDarkTheme() {
        context.datastore.edit {
            it[BLACK_BACKGROUND_FOR_DARK_THEME_KEY] =
                !(it[BLACK_BACKGROUND_FOR_DARK_THEME_KEY] ?: false)
        }
    }

    suspend fun changePlayerTheme(playerTheme: PlayerTheme) {
        context.datastore.edit {
            it[PLAYER_THEME_KEY] = playerTheme.toString()
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


    private fun Preferences.getPlayerSettings(): PlayerSettings {
        val jumpDuration = this[JUMP_DURATION_KEY] ?: DEFAULT_JUMP_DURATION_MILLIS
        return PlayerSettings(jumpDuration)
    }

    private fun Preferences.getUiSettings(): UiSettings {
        val theme = AppTheme.valueOf(this[THEME_KEY] ?: "SYSTEM")
        val isUsingDynamicColor = this[DYNAMIC_COLOR_KEY] ?: true
        val playerTheme = PlayerTheme.valueOf(this[PLAYER_THEME_KEY] ?: "BLUR")
        val blackBackgroundForDarkTheme = this[BLACK_BACKGROUND_FOR_DARK_THEME_KEY] ?: false
        return UiSettings(theme, isUsingDynamicColor, playerTheme, blackBackgroundForDarkTheme)
    }

    private fun Preferences.getLibrarySettings(excludedFolders: List<String>): LibrarySettings {
        val sortOptionParts = this[SORT_ORDER_KEY]?.split(":")
        val songsSortOrder = if (sortOptionParts == null)
            SortOption.TITLE to true else SortOption.valueOf(sortOptionParts[0]) to sortOptionParts[1].toBoolean()
        val cacheAlbumCoverArt = this[CACHE_ALBUM_COVER_ART_KEY] ?: true
        return LibrarySettings(songsSortOrder, cacheAlbumCoverArt, excludedFolders)
    }

    private fun mapPrefsToModel(
        prefs: Preferences,
        blacklistedFolders: List<BlacklistedFolderEntity>
    ) = UserPreferences(
        prefs.getLibrarySettings(blacklistedFolders.map { it.folderPath }),
        prefs.getUiSettings(),
        prefs.getPlayerSettings()
    )

    companion object {
        val SORT_ORDER_KEY = stringPreferencesKey("SORT")
        val THEME_KEY = stringPreferencesKey("THEME")
        val DYNAMIC_COLOR_KEY = booleanPreferencesKey("DYNAMIC_COLOR")
        val PLAYER_THEME_KEY = stringPreferencesKey("PLAYER_THEME")
        val BLACK_BACKGROUND_FOR_DARK_THEME_KEY =
            booleanPreferencesKey("BLACK_BACKGROUND_FOR_DARK_THEME")
        val CACHE_ALBUM_COVER_ART_KEY = booleanPreferencesKey("CACHE_ALBUM_COVER_ART")
        val JUMP_DURATION_KEY = intPreferencesKey("JUMP_DURATION_KEY")
        val SONG_URI_KEY = stringPreferencesKey("SONG_URI")
        val SONG_POSITION_KEY = longPreferencesKey("SONG_POSITION")
    }

}