package com.example.fredmobile.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.fredmobile.model.Site
import com.example.fredmobile.ui.checkin.CheckInViewModel
import com.example.fredmobile.ui.settings.SettingsViewModel
import com.example.fredmobile.ui.settings.SettingsViewModelFactory
import com.example.fredmobile.ui.weather.WeatherUiState
import com.example.fredmobile.ui.weather.WeatherViewModel
import com.example.fredmobile.util.toOpenWeatherUnits
import com.example.fredmobile.util.toTemperatureSuffix

/**
 * Screen for checking in and out of the current work site.
 *
 * PM1: fake state only.
 * PM2: shows real weather data via [WeatherViewModel].
 * PM3: persists check-ins to Firestore via [CheckInViewModel] and uses
 *      Settings (DataStore) for weather units.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckInScreen(
    navController: NavController,
    checkInViewModel: CheckInViewModel = viewModel(),
    weatherViewModel: WeatherViewModel = viewModel()
) {
    val context = LocalContext.current

    // SettingsViewModel for weather unit preference
    val settingsViewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(context)
    )
    val settingsUiState by settingsViewModel.uiState.collectAsState()
    val weatherUnitPref = settingsUiState.settings.weatherUnit
    val tempSuffix = weatherUnitPref.toTemperatureSuffix()

    // Fake "current site" for now – later this could come from Firestore or location
    val currentSite = remember {
        Site(
            id = "site1",
            name = "North River Plant",
            address = "1000 River Rd, Winnipeg, MB",
            latitude = 49.90,
            longitude = -97.15
        )
    }

    val checkInState = checkInViewModel.uiState
    val weatherState = weatherViewModel.uiState

    // Load weather when the site or unit preference changes
    LaunchedEffect(currentSite.id, weatherUnitPref) {
        val units = weatherUnitPref.toOpenWeatherUnits()
        weatherViewModel.loadWeatherForSite(
            lat = currentSite.latitude,
            lon = currentSite.longitude,
            units = units
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Check In / Out") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // CURRENT SITE CARD
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Current site",
                        style = MaterialTheme.typography.labelMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = currentSite.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = currentSite.address,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Lat: ${currentSite.latitude}, Lon: ${currentSite.longitude}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            // STATUS CARD (now backed by Firestore state)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (checkInState.isCheckedIn)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = if (checkInState.isCheckedIn)
                            "Status: Checked In"
                        else
                            "Status: Not Checked In",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = checkInState.statusText,
                        style = MaterialTheme.typography.bodySmall
                    )
                    if (checkInState.errorMessage != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = checkInState.errorMessage,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            // WEATHER PREVIEW CARD
            WeatherCard(
                weatherState = weatherState,
                tempSuffix = tempSuffix
            )

            Spacer(modifier = Modifier.height(8.dp))

            // ACTION BUTTONS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        checkInViewModel.checkIn(
                            siteId = currentSite.id,
                            siteName = currentSite.name
                        )
                    },
                    modifier = Modifier.weight(1f),
                    enabled = !checkInState.isCheckedIn && !checkInState.isLoading
                ) {
                    Text("Check In")
                }

                OutlinedButton(
                    onClick = { checkInViewModel.checkOut() },
                    modifier = Modifier.weight(1f),
                    enabled = checkInState.isCheckedIn && !checkInState.isLoading
                ) {
                    Text("Check Out")
                }
            }
        }
    }
}

/**
 * Simple card that shows current weather + AQI from [WeatherUiState].
 */
@Composable
private fun WeatherCard(
    weatherState: WeatherUiState,
    tempSuffix: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Weather at this site",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))

            when {
                weatherState.isLoading -> {
                    Text("Loading weather…", style = MaterialTheme.typography.bodySmall)
                }
                weatherState.errorMessage != null -> {
                    Text(
                        text = weatherState.errorMessage ?: "Weather not available.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                else -> {
                    Text(
                        text = "${weatherState.temperature?.toInt()}$tempSuffix • ${weatherState.description ?: ""}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "Air quality index: ${weatherState.aqi ?: "–"}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
