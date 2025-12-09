package com.example.fredmobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fredmobile.ui.AppNavHost
import com.example.fredmobile.ui.settings.SettingsViewModel
import com.example.fredmobile.ui.settings.SettingsViewModelFactory
import com.example.fredmobile.ui.theme.FredmobileTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState


/**
 * Main activity and entry point for the FRED Mobile app.
 *
 * Configures the Compose UI, wires in the settings ViewModel backed
 * by DataStore, and applies the app theme (including dark mode)
 * before hosting the navigation graph.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            // App-wide SettingsViewModel so we can drive theme from preferences
            val settingsViewModel: SettingsViewModel = viewModel(
                factory = SettingsViewModelFactory(applicationContext)
            )
            val settingsUiState by settingsViewModel.uiState.collectAsState()

            val darkModeOn = settingsUiState.settings.darkModeEnabled
            // Reuse autoCheckInEnabled as our "larger text" flag
            val largeTextOn = settingsUiState.settings.autoCheckInEnabled

            FredmobileTheme(
                darkTheme = darkModeOn,
                largeText = largeTextOn
            ) {
                AppNavHost()
            }
        }
    }
}
