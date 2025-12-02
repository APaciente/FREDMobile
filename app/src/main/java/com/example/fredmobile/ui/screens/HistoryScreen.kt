package com.example.fredmobile.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

/**
 * History screen showing past check-ins and incidents.
 * For Milestone 1 we display static sample data.
 * Later milestones will load this from Firestore / Room.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(navController: NavController) {
    var selectedTabIndex by remember { mutableStateOf(0) }

    val tabs = listOf("Check-ins", "Incidents")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("History") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
        ) {

            Text(
                text = "Review your recent activity.",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Tabs
            TabRow(
                selectedTabIndex = selectedTabIndex
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Content under tabs
            when (selectedTabIndex) {
                0 -> CheckInHistoryList(sampleCheckins)
                1 -> IncidentHistoryList(sampleIncidents)
            }
        }
    }
}

/**
 * List of previous check-ins.
 */
@Composable
private fun CheckInHistoryList(entries: List<CheckInHistoryEntry>) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(entries) { entry ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = entry.siteName,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Date: ${entry.date}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "In: ${entry.checkInTime}, Out: ${entry.checkOutTime}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "Status: ${entry.status}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

/**
 * List of previous incident reports.
 */
@Composable
private fun IncidentHistoryList(entries: List<IncidentHistoryEntry>) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(entries) { entry ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "${entry.siteName} – ${entry.severity} incident",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Date: ${entry.date}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "Summary: ${entry.summary}",
                        style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

/**
 * Simple models + sample data for Milestone 1.
 * Later we will replace these with data from the database.
 */
private data class CheckInHistoryEntry(
    val siteName: String,
    val date: String,
    val checkInTime: String,
    val checkOutTime: String,
    val status: String
)

private data class IncidentHistoryEntry(
    val siteName: String,
    val date: String,
    val severity: String,
    val summary: String
)

private val sampleCheckins = listOf(
    CheckInHistoryEntry(
        siteName = "North River Plant",
        date = "2025-11-25",
        checkInTime = "08:15",
        checkOutTime = "16:30",
        status = "Completed"
    ),
    CheckInHistoryEntry(
        siteName = "West Substation",
        date = "2025-11-24",
        checkInTime = "09:05",
        checkOutTime = "15:50",
        status = "Completed"
    ),
    CheckInHistoryEntry(
        siteName = "Downtown Office",
        date = "2025-11-23",
        checkInTime = "08:45",
        checkOutTime = "12:00",
        status = "Left early (training)"
    )
)

private val sampleIncidents = listOf(
    IncidentHistoryEntry(
        siteName = "North River Plant",
        date = "2025-11-20",
        severity = "Medium",
        summary = "Minor slip near loading dock. No injury."
    ),
    IncidentHistoryEntry(
        siteName = "West Substation",
        date = "2025-11-18",
        severity = "High",
        summary = "Electrical panel left open; area secured and reported."
    )
)
