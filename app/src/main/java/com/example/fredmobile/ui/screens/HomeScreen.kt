package com.example.fredmobile.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fredmobile.R
import com.example.fredmobile.ui.checkin.CheckInViewModel
import com.example.fredmobile.ui.location.LocationUiState
import com.example.fredmobile.ui.location.LocationViewModel
import com.example.fredmobile.ui.navigation.FredBottomBar
import com.example.fredmobile.ui.navigation.Routes
import com.example.fredmobile.ui.weather.WeatherViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await

/**
 * Home dashboard screen for FRED Mobile.
 *
 * Shows:
 * - A friendly greeting and rotating safety tips.
 * - A quick entry point to the Check-In screen.
 * - A shortcut to Settings from the top app bar.
 * - (Optionally) weather and location-aware tips when data is available.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    checkInViewModel: CheckInViewModel,
    weatherViewModel: WeatherViewModel,
    locationViewModel: LocationViewModel
) {
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    val firestore = remember { FirebaseFirestore.getInstance() }
    var isAdmin by remember { mutableStateOf(false) }

    LaunchedEffect(currentUser?.uid) {
        val uid = currentUser?.uid ?: return@LaunchedEffect

        try {
            val snapshot = firestore
                .collection("users")
                .document(uid)
                .get()
                .await()

            val role = snapshot.getString("role") ?: "worker"
            isAdmin = role == "admin"
        } catch (_: Exception) {
            // If anything goes wrong, just treat as non-admin
            isAdmin = false
        }
    }

    // Try displayName, then email before the "@", otherwise use a generic fallback.
    val firstName = remember(currentUser) {
        val raw = currentUser?.displayName
            ?: currentUser?.email?.substringBefore("@")
            ?: "there"
        raw.substringBefore(" ")
    }

    // Use existing check-in state so the assistant message can react to it.
    val checkInState = checkInViewModel.uiState
    val isCheckedIn = checkInState.isCheckedIn

    // Read shared weather state (populated by CheckInScreen).
    val weatherState = weatherViewModel.uiState

    // Read shared location state (populated by LocationViewModel).
    val locationState = locationViewModel.uiState

    // Build a single optional weather tip.
    val weatherTip: String? = remember(
        weatherState.description,
        weatherState.aqi,
        weatherState.isLoading,
        weatherState.errorMessage,
        isCheckedIn
    ) {
        if (weatherState.isLoading || weatherState.errorMessage != null) {
            null
        } else {
            buildWeatherTip(
                description = weatherState.description,
                aqi = weatherState.aqi,
                isCheckedIn = isCheckedIn
            )
        }
    }

    // Build a single optional location tip.
    val locationTip: String? = remember(
        locationState.address,
        locationState.errorMessage,
        locationState.lastLocation,
        isCheckedIn
    ) {
        buildLocationTip(locationState, isCheckedIn)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Field Ready Dashboard") },
                actions = {
                    IconButton(onClick = { navController.navigate(Routes.SETTINGS) }) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Settings"
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
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
                .fillMaxSize()
        ) {
            AssistantAvatarSection(
                firstName = firstName,
                isCheckedIn = isCheckedIn,
                weatherTip = weatherTip,
                locationTip = locationTip
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = { navController.navigate(Routes.CHECKIN) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Check In / Out")
                }
            }

            // Only visible for admin/manager users
            if (isAdmin) {
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    OutlinedButton(
                        onClick = { navController.navigate(Routes.ADMIN) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Team Check-ins")
                    }
                }
            }
        }
    }
}

/**
 * Assistant section that shows a greeting, a rotating tip, and the avatar image.
 *
 * The main message reacts to the current check-in status, and tapping
 * the avatar cycles through the available tips.
 *
 * [weatherTip] and [locationTip] are optional; if not null they get added
 * to the tips list.
 */
@Composable
private fun AssistantAvatarSection(
    firstName: String,
    isCheckedIn: Boolean,
    weatherTip: String?,
    locationTip: String?,
    modifier: Modifier = Modifier
) {
    // Build the dynamic messages list.
    val messages = remember(firstName, isCheckedIn, weatherTip, locationTip) {
        buildList {
            // Status-aware reminder.
            if (isCheckedIn) {
                add("You are checked in for this shift. Remember to check out before you leave the site.")
            } else {
                add("When you arrive on site, check in so we can track your shift.")
            }

            // Location-aware tip if available.
            if (locationTip != null) {
                add(locationTip)
            }

            // Weather-based tip if available.
            if (weatherTip != null) {
                add(weatherTip)
            }

            // General tips.
            add("If you spot something unsafe, log an incident so your supervisor can follow up.")
            add("Need to remember where you were? Use the History tab to see your recent shifts.")
            add("Going to a different site today? Open Sites and double check the address and map.")
            add("Before you start work, take a quick look around for hazards and stay alert.")
        }
    }

    var messageIndex by remember { mutableStateOf(0) }
    val currentMessage = messages[messageIndex]

    // Simple word-by-word animation of the current tip.
    var animatedText by remember(messageIndex) { mutableStateOf("") }

    LaunchedEffect(messageIndex) {
        animatedText = ""
        val words = currentMessage.split(" ")
        for (i in 1..words.size) {
            animatedText = words.take(i).joinToString(" ")
            delay(60)
        }
    }

    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Back to a shorter card, like before.
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Hi $firstName",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = if (animatedText.isNotBlank()) animatedText else currentMessage,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 3,   // back to 3 lines
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // Avatar image. Tapping cycles to the next tip.
        Image(
            painter = painterResource(id = R.drawable.fred_avatar),
            contentDescription = "FRED assistant avatar",
            contentScale = ContentScale.Crop,
            alignment = Alignment.TopCenter,
            modifier = Modifier
                .fillMaxWidth()
                .height(340.dp)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) {
                    messageIndex = (messageIndex + 1) % messages.size
                }
        )
    }
}

/**
 * Builds a weather-aware tip from description and AQI.
 *
 * Returns null if there is nothing useful to say yet.
 */
private fun buildWeatherTip(
    description: String?,
    aqi: Int?,
    isCheckedIn: Boolean
): String? {
    val d = description?.lowercase() ?: return null

    return when {
        "snow" in d || "ice" in d -> {
            "It is $description today. Take small steps and watch your footing."
        }

        "rain" in d || "drizzle" in d -> {
            "Light rain today ($description). Surfaces may be slick so walk carefully."
        }

        "storm" in d || "thunder" in d -> {
            "Stormy conditions ($description). Avoid open areas and stay alert."
        }

        "clear" in d || "sun" in d -> {
            if (isCheckedIn) {
                "Clear skies today ($description). Remember to check out when your shift ends."
            } else {
                "Clear skies today ($description). When you arrive on site, remember to check in."
            }
        }

        aqi != null && aqi >= 4 -> {
            "Air quality is low today (AQI $aqi). Take breaks indoors when you can."
        }

        else -> {
            // Clean, natural fallback
            "$description conditions today. Stay aware of your surroundings and work safely."
        }
    }
}


/**
 * Builds a simple location-aware tip from the current LocationUiState.
 *
 * Returns null if there is nothing useful to say yet.
 */
private fun buildLocationTip(
    state: LocationUiState,
    isCheckedIn: Boolean
): String? {
    // If we have an explicit error, surface something gentle.
    if (state.errorMessage != null) {
        return "Location looks unavailable right now. Check in may use your last known site."
    }

    val address = state.address

    // Use just the first part of the address so it fits better on the card.
    val shortAddress = address
        ?.substringBefore(",")
        ?.trim()
        ?.takeIf { it.isNotBlank() }

    return when {
        shortAddress != null && isCheckedIn -> {
            "You are checked in near $shortAddress today. Stay alert as you move around the site."
        }

        shortAddress != null && !isCheckedIn -> {
            "Looks like you are near $shortAddress today. When you start work, remember to check in."
        }

        // We have coordinates but no readable address yet.
        state.lastLocation != null -> {
            "Your GPS location is active for this device. If the map looks wrong, move closer to the site or into an open area."
        }

        else -> null
    }
}
