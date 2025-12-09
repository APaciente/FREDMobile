package com.example.fredmobile.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.fredmobile.model.firestore.CheckIn
import com.example.fredmobile.model.firestore.Incident
import com.example.fredmobile.ui.history.HistoryViewModel
import com.example.fredmobile.ui.navigation.FredBottomBar
import com.example.fredmobile.util.toReadableString

/**
 * History screen for viewing past check-ins and incidents, with an option to export data as CSV.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    navController: NavController,
    historyViewModel: HistoryViewModel = viewModel()
) {
    val uiState = historyViewModel.uiState
    var selectedTab by remember { mutableStateOf(0) }
    val context = LocalContext.current

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult

        val csv = if (selectedTab == 0) {
            buildCheckInsCsv(uiState.checkIns)
        } else {
            buildIncidentsCsv(uiState.incidents)
        }

        try {
            context.contentResolver.openOutputStream(uri)?.use { out ->
                out.write(csv.toByteArray())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

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
                },
                actions = {
                    IconButton(
                        onClick = {
                            val suggestedName = if (selectedTab == 0) {
                                "checkins.csv"
                            } else {
                                "incidents.csv"
                            }
                            exportLauncher.launch(suggestedName)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.FileDownload,
                            contentDescription = "Export CSV"
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

            when {
                uiState.isLoading -> {
                    CircularProgressIndicator()
                }

                uiState.errorMessage != null -> {
                    Text(
                        text = uiState.errorMessage,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                else -> {
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
}

/**
 * Displays a list of past check-ins with basic details and a delete action.
 */
@Composable
private fun CheckInHistoryList(
    checkIns: List<CheckIn>,
    onDelete: (String) -> Unit
) {
    if (checkIns.isEmpty()) {
        Text("No check-ins yet.")
        return
    }

    val sorted = remember(checkIns) {
        checkIns.sortedByDescending { it.inTime?.seconds ?: 0L }
    }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(sorted, key = { it.id }) { item ->
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

/**
 * Displays a list of reported incidents with details and a delete action.
 */
@Composable
private fun IncidentHistoryList(
    incidents: List<Incident>,
    onDelete: (String) -> Unit
) {
    if (incidents.isEmpty()) {
        Text("No incidents reported yet.")
        return
    }

    val sorted = remember(incidents) {
        incidents.sortedByDescending { it.createdAt?.seconds ?: 0L }
    }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(sorted, key = { it.id }) { incident ->
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

private fun buildCheckInsCsv(checkIns: List<CheckIn>): String {
    if (checkIns.isEmpty()) return "Site,Status,In,Out\n"

    val header = "Site,Status,In,Out\n"
    val sorted = checkIns.sortedByDescending { it.inTime?.seconds ?: 0L }

    val rows = sorted.joinToString("\n") { ci ->
        val site = ci.siteName.csvSafe()
        val status = ci.status.csvSafe()
        val inTime = ci.inTime.toReadableString().csvSafe()
        val outTime = ci.outTime.toReadableString().csvSafe()
        "$site,$status,$inTime,$outTime"
    }
    return header + rows + "\n"
}

private fun buildIncidentsCsv(incidents: List<Incident>): String {
    if (incidents.isEmpty()) return "Site,Severity,Description,Reported\n"

    val header = "Site,Severity,Description,Reported\n"
    val sorted = incidents.sortedByDescending { it.createdAt?.seconds ?: 0L }

    val rows = sorted.joinToString("\n") { inc ->
        val site = inc.siteName.csvSafe()
        val severity = inc.severity.csvSafe()
        val desc = inc.description.csvSafe()
        val created = inc.createdAt.toReadableString().csvSafe()
        "$site,$severity,$desc,$created"
    }
    return header + rows + "\n"
}

/**
 * Simple CSV escaping: wraps the value in quotes and escapes existing quotes.
 */
private fun String.csvSafe(): String =
    "\"" + this.replace("\"", "\"\"") + "\""
