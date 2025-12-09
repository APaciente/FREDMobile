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

/**
 * Extension property to access the app's settings DataStore.
 */
val Context.settingsDataStore by preferencesDataStore(name = SETTINGS_STORE_NAME)

/**
 * Preference keys used to store individual settings values.
 */
private object SettingsKeys {
    val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
    val DARK_MODE_ENABLED = booleanPreferencesKey("dark_mode_enabled")
    val AUTO_CHECKIN_ENABLED = booleanPreferencesKey("auto_checkin_enabled")
    val WEATHER_UNIT = stringPreferencesKey("weather_unit")
}

/**
 * In-memory model of the user settings stored in DataStore.
 *
 * @param notificationsEnabled Whether app notifications are enabled.
 * @param darkModeEnabled Whether dark mode is enabled.
 * @param autoCheckInEnabled Whether automatic check-in is enabled.
 * @param weatherUnit Preferred weather unit label (e.g., "Celsius" or "Fahrenheit").
 */
data class Settings(
    val notificationsEnabled: Boolean = true,
    val darkModeEnabled: Boolean = false,
    val autoCheckInEnabled: Boolean = false,
    val weatherUnit: String = "Celsius"
)

/**
 * Repository that reads and writes user settings using Jetpack DataStore.
 *
 * Exposes a [Flow] of [Settings] for observing changes and provides
 * suspend functions to update individual preference values.
 */
class SettingsRepository(private val context: Context) {

    /**
     * Stream of [Settings] reflecting the latest values in DataStore.
     */
    val settingsFlow: Flow<Settings> = context.settingsDataStore.data.map { prefs ->
        Settings(
            notificationsEnabled = prefs[SettingsKeys.NOTIFICATIONS_ENABLED] ?: true,
            darkModeEnabled = prefs[SettingsKeys.DARK_MODE_ENABLED] ?: false,
            autoCheckInEnabled = prefs[SettingsKeys.AUTO_CHECKIN_ENABLED] ?: false,
            weatherUnit = prefs[SettingsKeys.WEATHER_UNIT] ?: "Celsius"
        )
    }

    /**
     * Updates whether notifications are enabled.
     */
    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { prefs ->
            prefs[SettingsKeys.NOTIFICATIONS_ENABLED] = enabled
        }
    }

    /**
     * Updates whether dark mode is enabled.
     */
    suspend fun setDarkModeEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { prefs ->
            prefs[SettingsKeys.DARK_MODE_ENABLED] = enabled
        }
    }

    /**
     * Updates whether automatic check-in is enabled.
     */
    suspend fun setAutoCheckInEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { prefs ->
            prefs[SettingsKeys.AUTO_CHECKIN_ENABLED] = enabled
        }
    }

    /**
     * Updates the preferred weather unit label.
     */
    suspend fun setWeatherUnit(unit: String) {
        context.settingsDataStore.edit { prefs ->
            prefs[SettingsKeys.WEATHER_UNIT] = unit
        }
    }
}
