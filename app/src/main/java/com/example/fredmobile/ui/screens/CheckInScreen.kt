package com.example.fredmobile.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.fredmobile.model.Site
import com.example.fredmobile.ui.checkin.CheckInViewModel
import com.example.fredmobile.ui.navigation.FredBottomBar
import com.example.fredmobile.ui.settings.SettingsViewModel
import com.example.fredmobile.ui.settings.SettingsViewModelFactory
import com.example.fredmobile.ui.weather.ForecastItemUi
import com.example.fredmobile.ui.weather.WeatherUiState
import com.example.fredmobile.ui.weather.WeatherViewModel
import com.example.fredmobile.util.mapWeatherDescriptionToEmoji
import com.example.fredmobile.util.toOpenWeatherUnits
import com.example.fredmobile.util.toTemperatureSuffix

/**
 * Screen for checking in and out of the current work site.
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
        },
        bottomBar = {
            FredBottomBar(navController = navController)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            // Scrollable content (cards)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // STATUS + SITE CARD (combined)
                StatusAndSiteCard(
                    checkInState = checkInState,
                    currentSite = currentSite
                )

                // MAP PREVIEW CARD (real Google Map)
                MapPreviewCard(currentSite = currentSite)

                // WEATHER SUMMARY
                WeatherSummaryCard(
                    weatherState = weatherState,
                    tempSuffix = tempSuffix
                )

                // NEXT HOURS – single row with 3 boxes (3h, 6h, 9h)
                if (
                    !weatherState.isLoading &&
                    weatherState.errorMessage == null &&
                    weatherState.forecastItems.isNotEmpty()
                ) {
                    ForecastRowCard(
                        items = weatherState.forecastItems,
                        tempSuffix = tempSuffix
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            // ACTION BUTTONS – pinned at the bottom above the nav bar
            val canCheckIn = !checkInState.isCheckedIn && !checkInState.isLoading
            val canCheckOut = checkInState.isCheckedIn && !checkInState.isLoading

            val green = Color(0xFF2E7D32)
            val red = Color(0xFFC62828)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // CHECK IN BUTTON (green when clickable)
                Button(
                    onClick = {
                        checkInViewModel.checkIn(
                            siteId = currentSite.id,
                            siteName = currentSite.name
                        )
                    },
                    modifier = Modifier.weight(1f),
                    enabled = canCheckIn,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (canCheckIn) green
                        else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (canCheckIn) Color.White
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text("Check In")
                }

                // CHECK OUT BUTTON (solid red when clickable)
                Button(
                    onClick = { checkInViewModel.checkOut() },
                    modifier = Modifier.weight(1f),
                    enabled = canCheckOut,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (canCheckOut) red
                        else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (canCheckOut) Color.White
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text("Check Out")
                }
            }
        }
    }
}

/* ---------- Small composables ---------- */

/**
 * Combined status + site info card.
 * Green when checked in, red when not checked in.
 */
@Composable
private fun StatusAndSiteCard(
    checkInState: com.example.fredmobile.ui.checkin.CheckInUiState,
    currentSite: Site
) {
    val green = Color(0xFF2E7D32)
    val red = Color(0xFFC62828)
    val containerColor = if (checkInState.isCheckedIn) green else red
    val textColor = Color.White

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = if (checkInState.isCheckedIn)
                    "Status: Checked In"
                else
                    "Status: Not Checked In",
                style = MaterialTheme.typography.titleMedium,
                color = textColor
            )

            // Site name + address right under status
            Text(
                text = currentSite.name,
                style = MaterialTheme.typography.labelMedium,
                color = textColor
            )
            Text(
                text = currentSite.address,
                style = MaterialTheme.typography.bodySmall,
                color = textColor
            )

            if (checkInState.errorMessage != null) {
                Text(
                    text = checkInState.errorMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = textColor
                )
            }
        }
    }
}

/**
 * Real Google Maps preview card centered on the current site.
 */
@Composable
private fun MapPreviewCard(currentSite: Site) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Map preview",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Here you’ll see a live map centered on this site.",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Lat: ${currentSite.latitude}, Lon: ${currentSite.longitude}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "TODO: integrate Google Maps + location (Phase 4B).",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
private fun WeatherSummaryCard(
    weatherState: WeatherUiState,
    tempSuffix: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
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
                    val emoji = mapWeatherDescriptionToEmoji(weatherState.description)
                    Text(
                        text = "$emoji ${weatherState.temperature?.toInt()}$tempSuffix • ${weatherState.description ?: ""}",
                        style = MaterialTheme.typography.bodyMedium
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

/**
 * One-row card with three mini forecast boxes (3h, 6h, 9h).
 */
@Composable
private fun ForecastRowCard(
    items: List<ForecastItemUi>,
    tempSuffix: String
) {
    val firstThree = items.take(3)
    if (firstThree.isEmpty()) return

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                firstThree.forEach { item ->
                    ForecastMiniCard(
                        item = item,
                        tempSuffix = tempSuffix,
                        modifier = Modifier.weight(1f)
                    )
                }

                // If less than 3 items, fill remaining space
                repeat(3 - firstThree.size) {
                    Spacer(
                        modifier = Modifier
                            .weight(1f)
                            .height(0.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ForecastMiniCard(
    item: ForecastItemUi,
    tempSuffix: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = item.timeLabel,        // "In 3h", "In 6h", etc.
                style = MaterialTheme.typography.labelSmall
            )

            val emoji = mapWeatherDescriptionToEmoji(item.description)
            val tempText = item.temp?.toInt()?.toString() ?: "–"

            Text(
                text = "$emoji $tempText$tempSuffix",
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = item.description ?: "",
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1
            )
        }
    }
}
