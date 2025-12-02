package com.example.fredmobile.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fredmobile.model.Site

/**
 * Screen for checking in and out of the current work site.
 * For Milestone 1 we use fake data and local state only.
 * Later milestones will connect this to location, geofencing, and database.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckInScreen(navController: NavController) {
    // Fake "current site" for PM1
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

            // WEATHER PREVIEW PLACEHOLDER (for PM2 endpoints)
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
                    Text(
                        text = "Current: 3°C, cloudy (sample data)",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "Later: light snow expected this afternoon.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "This section will be powered by real API calls in PM2.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

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
