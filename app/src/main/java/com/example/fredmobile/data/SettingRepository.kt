package com.example.fredmobile.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// ---- DataStore setup ----

private const val SETTINGS_STORE_NAME = "fred_settings"

// Extension on Context
val Context.settingsDataStore by preferencesDataStore(name = SETTINGS_STORE_NAME)

// Preference keys
private object SettingsKeys {
    val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
    val DARK_MODE_ENABLED = booleanPreferencesKey("dark_mode_enabled")
    val AUTO_CHECKIN_ENABLED = booleanPreferencesKey("auto_checkin_enabled")
    val WEATHER_UNIT = stringPreferencesKey("weather_unit")
}

// Settings model
data class Settings(
    val notificationsEnabled: Boolean = true,
    val darkModeEnabled: Boolean = false,
    val autoCheckInEnabled: Boolean = false,
    val weatherUnit: String = "Celsius"
)

/**
 * Repository that reads/writes user settings using DataStore.
 */
class SettingsRepository(private val context: Context) {

    val settingsFlow: Flow<Settings> = context.settingsDataStore.data.map { prefs ->
        Settings(
            notificationsEnabled = prefs[SettingsKeys.NOTIFICATIONS_ENABLED] ?: true,
            darkModeEnabled = prefs[SettingsKeys.DARK_MODE_ENABLED] ?: false,
            autoCheckInEnabled = prefs[SettingsKeys.AUTO_CHECKIN_ENABLED] ?: false,
            weatherUnit = prefs[SettingsKeys.WEATHER_UNIT] ?: "Celsius"
        )
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { prefs ->
            prefs[SettingsKeys.NOTIFICATIONS_ENABLED] = enabled
        }
    }

    suspend fun setDarkModeEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { prefs ->
            prefs[SettingsKeys.DARK_MODE_ENABLED] = enabled
        }
    }

    suspend fun setAutoCheckInEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { prefs ->
            prefs[SettingsKeys.AUTO_CHECKIN_ENABLED] = enabled
        }
    }

    suspend fun setWeatherUnit(unit: String) {
        context.settingsDataStore.edit { prefs ->
            prefs[SettingsKeys.WEATHER_UNIT] = unit
        }
    }
}
