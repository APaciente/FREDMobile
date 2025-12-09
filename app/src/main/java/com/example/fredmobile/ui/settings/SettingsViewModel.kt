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

/**
 * UI state for the settings screen.
 *
 * Wraps the current [Settings] and a simple loading flag while values
 * are being read from DataStore.
 */
data class SettingsUiState(
    val isLoading: Boolean = true,
    val settings: Settings = Settings()
)

/**
 * ViewModel that exposes user settings and updates them via [SettingsRepository].
 *
 * Observes the DataStore-backed flow and provides a [SettingsUiState] for the UI.
 */
class SettingsViewModel(
    private val repo: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState

    init {
        // Observe changes from DataStore and push them into the UI state.
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
 * [ViewModelProvider.Factory] for creating [SettingsViewModel] instances.
 *
 * Provides a [SettingsRepository] built from the given [Context].
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
