package com.example.fredmobile.ui.screens

import android.Manifest
import android.location.Geocoder
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import android.annotation.SuppressLint
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Locale

/**
 * Screen for checking in and out of the current work site / current location.
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

    // --- Location plumbing ---------------------------------------------------

    // Fused location client
    val fusedClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    // Do we have location permission?
    var hasLocationPermission by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        hasLocationPermission =
            (perms[Manifest.permission.ACCESS_FINE_LOCATION] == true) ||
                    (perms[Manifest.permission.ACCESS_COARSE_LOCATION] == true)
    }

    // Ask for permission once when the screen first appears
    LaunchedEffect(Unit) {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    // Device location (updated when permission is granted)
    var userLocation by remember { mutableStateOf<LatLng?>(null) }

    // Human-readable address for current coordinates
    var resolvedAddress by remember { mutableStateOf<String?>(null) }

    @SuppressLint("MissingPermission")
    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            try {
                val loc = fusedClient.lastLocation.await()
                if (loc != null) {
                    val latLng = LatLng(loc.latitude, loc.longitude)
                    userLocation = latLng

                    // Reverse-geocode to a readable address
                    val geocoder = Geocoder(context, Locale.getDefault())
                    val result = withContext(Dispatchers.IO) {
                        @Suppress("DEPRECATION")
                        geocoder.getFromLocation(
                            latLng.latitude,
                            latLng.longitude,
                            1
                        )
                    }?.firstOrNull()

                    result?.let { addr ->
                        val line1 = listOfNotNull(
                            addr.featureName,     // house number / POI
                            addr.thoroughfare     // street
                        ).joinToString(" ")

                        val cityLine = listOfNotNull(
                            addr.locality,        // city
                            addr.adminArea        // province
                        ).joinToString(", ")

                        resolvedAddress = listOfNotNull(line1, cityLine)
                            .filter { it.isNotBlank() }
                            .joinToString(", ")
                    }
                }
            } catch (_: Exception) {
                // swallow for now – fallback will be used
            }
        }
    }

    // Fallback "site" if we don't have a real location yet
    val fallbackSite = remember {
        Site(
            id = "site1",
            name = "North River Plant",
            address = "1000 River Rd, Winnipeg, MB",
            latitude = 49.90,
            longitude = -97.15
        )
    }

    // Choose which coordinates to use: device location if available, else fallback site
    val effectiveLatLng: LatLng = userLocation
        ?: LatLng(fallbackSite.latitude, fallbackSite.longitude)

    // Build a "UI site" that represents whatever we are using right now
    val uiSite = remember(userLocation, effectiveLatLng, resolvedAddress) {
        if (userLocation != null) {
            val displayName = resolvedAddress ?: "Current location"
            val displayAddress = resolvedAddress
                ?: "Using GPS near %.4f, %.4f".format(
                    effectiveLatLng.latitude,
                    effectiveLatLng.longitude
                )

            Site(
                id = "current_location",
                name = displayName,
                address = displayAddress,
                latitude = effectiveLatLng.latitude,
                longitude = effectiveLatLng.longitude
            )
        } else {
            // Before permission / first fix – fall back to your demo site
            fallbackSite.copy(
                latitude = effectiveLatLng.latitude,
                longitude = effectiveLatLng.longitude
            )
        }
    }

    val checkInState = checkInViewModel.uiState
    val weatherState = weatherViewModel.uiState

    // --- Weather: ALWAYS use the same coords as the map / uiSite -------------
    LaunchedEffect(effectiveLatLng, weatherUnitPref) {
        val units = weatherUnitPref.toOpenWeatherUnits()
        weatherViewModel.loadWeatherForSite(
            lat = effectiveLatLng.latitude,
            lon = effectiveLatLng.longitude,
            units = units
        )
    }

    // -------------------------------------------------------------------------

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

                // STATUS + SITE CARD (combined – now reflects uiSite)
                StatusAndSiteCard(
                    checkInState = checkInState,
                    currentSite = uiSite
                )

                // MAP PREVIEW CARD – centered on uiSite coords
                MapPreviewCard(
                    currentSite = uiSite,
                    hasLocationPermission = hasLocationPermission
                )

                // WEATHER SUMMARY – using same coords as map
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
                            siteId = uiSite.id,
                            siteName = uiSite.name
                            // later you can also pass lat/lon to Firestore if desired
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
 * Now also optionally shows the blue "my location" dot when permission is granted.
 */
@Composable
private fun MapPreviewCard(
    currentSite: Site,
    hasLocationPermission: Boolean
) {
    val context = LocalContext.current

    // Position for this site
    val siteLatLng = LatLng(currentSite.latitude, currentSite.longitude)

    // Remember a single MapView instance
    val mapView = remember {
        MapView(context).apply {
            // No savedInstanceState for preview
            onCreate(null)
        }
    }

    // Handle basic lifecycle so the map actually shows
    DisposableEffect(Unit) {
        mapView.onStart()
        mapView.onResume()

        onDispose {
            mapView.onPause()
            mapView.onStop()
            mapView.onDestroy()
        }
    }

    // Configure the camera + marker when the composable is shown
    LaunchedEffect(siteLatLng, hasLocationPermission) {
        mapView.getMapAsync { googleMap ->
            googleMap.uiSettings.apply {
                setAllGesturesEnabled(true)
            }

            if (hasLocationPermission) {
                try {
                    googleMap.isMyLocationEnabled = true
                } catch (_: SecurityException) {
                    // ignore – shouldn't happen if permission flag is correct
                }
            }

            googleMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(siteLatLng, 15f)
            )

            googleMap.clear()
            googleMap.addMarker(
                MarkerOptions()
                    .position(siteLatLng)
                    .title(currentSite.name)
                    .snippet(currentSite.address)
            )
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { mapView }
        )
    }
}

@Composable
private fun WeatherSummaryCard(
    weatherState: WeatherUiState,
    tempSuffix: String
) {
    // Pick colors based on current description
    val style = resolveWeatherStyle(weatherState.description)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = style.background
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            when {
                weatherState.isLoading -> {
                    Text(
                        "Loading weather…",
                        style = MaterialTheme.typography.bodySmall,
                        color = style.text
                    )
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
                        style = MaterialTheme.typography.bodyMedium,
                        color = style.text
                    )
                    Text(
                        text = "Air quality index: ${weatherState.aqi ?: "–"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = style.text
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
    val style = resolveWeatherStyle(item.description)

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 2.dp,
        color = style.chipBackground
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = item.timeLabel,        // "In 3h", "In 6h", etc.
                style = MaterialTheme.typography.labelSmall,
                color = style.chipText
            )

            val emoji = mapWeatherDescriptionToEmoji(item.description)
            val tempText = item.temp?.toInt()?.toString() ?: "–"

            Text(
                text = "$emoji $tempText$tempSuffix",
                style = MaterialTheme.typography.bodySmall,
                color = style.chipText
            )

            Text(
                text = item.description ?: "",
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                color = style.chipText
            )
        }
    }
}

// --------- Weather visual styles (simple theming) ---------

private data class WeatherCardStyle(
    val background: Color,
    val text: Color,
    val chipBackground: Color,
    val chipText: Color
)

private fun resolveWeatherStyle(description: String?): WeatherCardStyle {
    val d = description?.lowercase() ?: ""

    return when {
        // Rainy
        "rain" in d || "drizzle" in d || "shower" in d -> WeatherCardStyle(
            background = Color(0xFF0F172A),      // dark navy
            text = Color(0xFFE0F2FE),           // very light blue
            chipBackground = Color(0xFF1D4ED8), // blue chip
            chipText = Color(0xFFEFF6FF)
        )

        // Snow / sleet
        "snow" in d || "sleet" in d -> WeatherCardStyle(
            background = Color(0xFF0F172A),
            text = Color(0xFFE5F4FF),
            chipBackground = Color(0xFF38BDF8),
            chipText = Color(0xFF02131F)
        )

        // Cloudy / overcast
        "cloud" in d || "overcast" in d -> WeatherCardStyle(
            background = Color(0xFF111827),      // dark grey
            text = Color(0xFFE5E7EB),           // light grey
            chipBackground = Color(0xFF374151), // mid grey chip
            chipText = Color(0xFFF9FAFB)
        )

        // Clear / sunny
        "clear" in d || "sun" in d -> WeatherCardStyle(
            background = Color(0xFF1F2937),      // slightly warm dark
            text = Color(0xFFFDE68A),           // warm yellow
            chipBackground = Color(0xFFF59E0B), // orange chip
            chipText = Color(0xFF1F2937)
        )

        // Fallback
        else -> WeatherCardStyle(
            background = Color(0xFF1F2937),
            text = Color(0xFFE5E7EB),
            chipBackground = Color(0xFF4B5563),
            chipText = Color(0xFFF9FAFB)
        )
    }
}
