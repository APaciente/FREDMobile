package com.example.fredmobile.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.fredmobile.model.Site
import com.example.fredmobile.ui.weather.WeatherUiState
import com.example.fredmobile.ui.weather.WeatherViewModel

/**
 * Screen for checking in and out of the current work site.
 *
 * Milestone 1:
 *  - Uses fake site data and local in-memory check-in state.
 *
 * Milestone 2:
 *  - Calls the weather API via [WeatherViewModel] to show
 *    current weather and air quality for the active site.
 *
 * Later milestones will connect this to location, geofencing,
 * and persistent database records.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckInScreen(
    navController: NavController,
    weatherViewModel: WeatherViewModel = viewModel()
) {
    // Fake "current site" for PM1/PM2
    val currentSite = remember {
        Site(
            id = "site1",
            name = "North River Plant",
            address = "1000 River Rd, Winnipeg, MB",
            latitude = 49.90,
            longitude = -97.15
        )
    }

    // Simple in-memory status for PM1 only
    var isCheckedIn by remember { mutableStateOf(false) }
    var lastActionText by remember { mutableStateOf("Not checked in yet today.") }

    // ----- Weather state -----
    val weatherState = weatherViewModel.uiState

    // Load weather once when this screen appears (or when site changes)
    LaunchedEffect(currentSite.id) {
        weatherViewModel.loadWeatherForSite(
            lat = currentSite.latitude,
            lon = currentSite.longitude
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Check In / Out") }
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

            // STATUS CARD
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isCheckedIn)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = if (isCheckedIn) "Status: Checked In" else "Status: Not Checked In",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = lastActionText,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            // WEATHER CARD (real API via WeatherViewModel)
            WeatherSummaryCard(state = weatherState)

            Spacer(modifier = Modifier.height(8.dp))

            // ACTION BUTTONS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        isCheckedIn = true
                        lastActionText = "Checked in just now (sample for PM1)."
                    },
                    modifier = Modifier.weight(1f),
                    enabled = !isCheckedIn
                ) {
                    Text("Check In")
                }

                OutlinedButton(
                    onClick = {
                        isCheckedIn = false
                        lastActionText = "Checked out just now (sample for PM1)."
                    },
                    modifier = Modifier.weight(1f),
                    enabled = isCheckedIn
                ) {
                    Text("Check Out")
                }
            }
        }
    }
}

/**
 * Card that summarizes current weather and air quality for the site.
 *
 * Uses [WeatherUiState] so it can show loading, error, or data
 * without the CheckInScreen needing to know the API details.
 */
@Composable
fun WeatherSummaryCard(state: WeatherUiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Weather at this site",
                style = MaterialTheme.typography.titleMedium
            )

            when {
                state.isLoading -> {
                    Text(
                        text = "Loading latest weather…",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                state.errorMessage != null -> {
                    Text(
                        text = state.errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                else -> {
                    val tempText = state.temperature?.let { "${it.toInt()}°" } ?: "--°"
                    val descText = state.description ?: "No weather description"

                    // Current conditions
                    Text(
                        text = "$tempText • $descText",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    // Next forecast step (if available)
                    if (state.nextTemp != null && state.nextDescription != null) {
                        Text(
                            text = "Next: ${state.nextTemp.toInt()}° • ${state.nextDescription}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    // AQI
                    if (state.aqi != null) {
                        Text(
                            text = "Air quality index: ${state.aqi}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    } else {
                        Text(
                            text = "Air quality: no data",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

