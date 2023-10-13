package com.omar.musica.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omar.musica.model.AppTheme
import com.omar.musica.model.UserPreferences
import com.omar.musica.store.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
): ViewModel() {

    val state = userPreferencesRepository.getUserSettingsFlow()
        .map { SettingsState.Loaded(it) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, SettingsState.Loading)


    fun onThemeSelected(appTheme: AppTheme) {
        viewModelScope.launch {
            userPreferencesRepository.changeTheme(appTheme)
        }
    }

    fun toggleDynamicColorScheme() {
        viewModelScope.launch {
            userPreferencesRepository.toggleDynamicColor()
        }
    }

}

sealed interface SettingsState {
    data object Loading: SettingsState
    data class Loaded(val userPreferences: UserPreferences): SettingsState
}