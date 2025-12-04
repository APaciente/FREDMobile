package com.example.fredmobile.ui.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.fredmobile.data.Settings
import com.example.fredmobile.data.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class SettingsUiState(
    val isLoading: Boolean = true,
    val settings: Settings = Settings()
)

class SettingsViewModel(
    private val repo: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState

    init {
        // Start listening to changes from DataStore
        viewModelScope.launch {
            repo.settingsFlow.collectLatest { settings ->
                _uiState.value = SettingsUiState(
                    isLoading = false,
                    settings = settings
                )
            }
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repo.setNotificationsEnabled(enabled)
        }
    }

    fun setDarkModeEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repo.setDarkModeEnabled(enabled)
        }
    }

    fun setAutoCheckInEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repo.setAutoCheckInEnabled(enabled)
        }
    }

    fun setWeatherUnit(unit: String) {
        viewModelScope.launch {
            repo.setWeatherUnit(unit)
        }
    }
}

/**
 * Simple factory so we can pass Context to SettingsRepository without Hilt.
 */
class SettingsViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            val repo = SettingsRepository(context)
            return SettingsViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
