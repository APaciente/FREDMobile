package com.example.fredmobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fredmobile.ui.AppNavHost
import com.example.fredmobile.ui.settings.SettingsViewModel
import com.example.fredmobile.ui.settings.SettingsViewModelFactory
import com.example.fredmobile.ui.theme.FredmobileTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val context = LocalContext.current

            // Read settings from DataStore
            val settingsViewModel: SettingsViewModel = viewModel(
                factory = SettingsViewModelFactory(context)
            )
            val settingsUiState by settingsViewModel.uiState.collectAsState()

            val darkModeEnabled = settingsUiState.settings.darkModeEnabled

            FredmobileTheme(darkTheme = darkModeEnabled) {
                AppNavHost()
            }
        }
    }
}
