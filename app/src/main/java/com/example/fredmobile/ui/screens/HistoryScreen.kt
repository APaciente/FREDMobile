package com.example.fredmobile.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.fredmobile.model.firestore.CheckIn
import com.example.fredmobile.model.firestore.Incident
import com.example.fredmobile.ui.history.HistoryViewModel
import com.example.fredmobile.util.toReadableString


/**
 * History screen showing past check-ins and incidents.
 *
 * PM1: used fake in-memory data.
 * PM3: now backed by Firestore via [HistoryViewModel].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    navController: NavController,
    historyViewModel: HistoryViewModel = viewModel()
) {
    val uiState = historyViewModel.uiState
    var selectedTab by remember { mutableStateOf(0) } // 0 = Check-ins, 1 = Incidents

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("History") },
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
                .fillMaxSize()
        ) {

            // Tabs
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Check-ins") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Incidents") }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage,
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                if (selectedTab == 0) {
                    CheckInHistoryList(
                        checkIns = uiState.checkIns,
                        onDelete = { id -> historyViewModel.deleteCheckIn(id) }
                    )
                } else {
                    IncidentHistoryList(
                        incidents = uiState.incidents,
                        onDelete = { id -> historyViewModel.deleteIncident(id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CheckInHistoryList(
    checkIns: List<CheckIn>,
    onDelete: (String) -> Unit
) {
    if (checkIns.isEmpty()) {
        Text("No check-ins yet.")
        return
    }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(checkIns, key = { it.id }) { item ->
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = item.siteName,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Status: ${item.status}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "In: ${item.inTime.toReadableString()}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "Out: ${item.outTime.toReadableString()}",
                        style = MaterialTheme.typography.bodySmall
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Tap delete to remove this record.",
                            style = MaterialTheme.typography.bodySmall
                        )
                        TextButton(onClick = { onDelete(item.id) }) {
                            Text("Delete")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun IncidentHistoryList(
    incidents: List<Incident>,
    onDelete: (String) -> Unit
) {
    if (incidents.isEmpty()) {
        Text("No incidents reported yet.")
        return
    }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(incidents, key = { it.id }) { incident ->
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = incident.siteName,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Severity: ${incident.severity}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = incident.description,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "Reported: ${incident.createdAt.toReadableString()}",
                        style = MaterialTheme.typography.bodySmall
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Tap delete to remove this incident.",
                            style = MaterialTheme.typography.bodySmall
                        )
                        TextButton(onClick = { onDelete(incident.id) }) {
                            Text("Delete")
                        }
                    }
                }
            }
        }
    }
}
