package com.example.fredmobile.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.fredmobile.model.firestore.CheckIn
import com.example.fredmobile.ui.admin.AdminViewModel
import com.example.fredmobile.ui.navigation.FredBottomBar
import com.example.fredmobile.util.toReadableString

/**
 * Admin screen that shows team check-ins in two tabs:
 * a daily summary per user and a recent history of all check-ins.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    navController: NavController,
    adminViewModel: AdminViewModel = viewModel()
) {
    var selectedTab by remember { mutableStateOf(0) }
    val uiState = adminViewModel.uiState

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin – Team Check-ins") },
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
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Today at a glance") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Daily history") }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            when {
                uiState.isLoading -> {
                    CircularProgressIndicator()
                }

                else -> {
                    if (selectedTab == 0) {
                        TodayAtAGlanceList(uiState.todaySummary)
                    } else {
                        AdminHistoryList(uiState.dailyHistory)
                    }
                }
            }
        }
    }
}

/**
 * Displays the latest check-in for each user for the current day.
 */
@Composable
private fun TodayAtAGlanceList(rows: List<CheckIn>) {
    if (rows.isEmpty()) {
        Text("No check-ins recorded yet today.")
        return
    }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(rows, key = { it.id }) { item ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = item.siteName,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "User: ${item.userName.ifBlank { item.userId }}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "Status: ${item.status}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "In: ${item.inTime.toReadableString()}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    if (item.outTime != null) {
                        Text(
                            text = "Out: ${item.outTime.toReadableString()}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

/**
 * Displays recent check-ins for all users in reverse chronological order.
 */
@Composable
private fun AdminHistoryList(rows: List<CheckIn>) {
    if (rows.isEmpty()) {
        Text("No check-ins in history yet.")
        return
    }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(rows, key = { it.id }) { item ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = item.siteName,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "User: ${item.userName.ifBlank { item.userId }}",
                        style = MaterialTheme.typography.bodySmall
                    )
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
                }
            }
        }
    }
}
