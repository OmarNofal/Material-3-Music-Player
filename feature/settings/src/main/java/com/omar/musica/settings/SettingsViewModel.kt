package com.omar.musica.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omar.musica.model.AppTheme
import com.omar.musica.store.UserPreferencesRepository
import com.omar.musica.ui.model.UserPreferencesUi
import com.omar.musica.ui.model.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val state = userPreferencesRepository.getUserSettingsFlow()
        .map { SettingsState.Loaded(it.toUiModel()) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, SettingsState.Loading)

    fun onFolderDeleted(folder: String) {
        viewModelScope.launch {
            userPreferencesRepository.deleteFolderFromBlacklist(folder)
        }
    }

    fun onToggleCacheAlbumArt() {
        viewModelScope.launch {
            userPreferencesRepository.toggleCacheAlbumArt()
        }
    }

    fun onFolderAdded(folder: String) {
        viewModelScope.launch {
            userPreferencesRepository.addBlacklistedFolder(folder)
        }
    }

    fun onThemeSelected(appTheme: AppTheme) {
        viewModelScope.launch {
            userPreferencesRepository.changeTheme(appTheme)
        }
    }

    fun onJumpDurationChanged(durationMillis: Int) {
        viewModelScope.launch {
            userPreferencesRepository.changeJumpDurationMillis(durationMillis)
        }
    }

    fun toggleDynamicColorScheme() {
        viewModelScope.launch {
            userPreferencesRepository.toggleDynamicColor()
        }
    }

}

sealed interface SettingsState {
    data object Loading : SettingsState
    data class Loaded(val userPreferences: UserPreferencesUi) : SettingsState
}