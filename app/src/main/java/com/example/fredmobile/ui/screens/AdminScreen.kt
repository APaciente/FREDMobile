package com.example.fredmobile.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fredmobile.ui.navigation.FredBottomBar

/**
 * Admin dashboard placeholder.
 *
 * Tab 1: "Today at a glance" – will show each user’s latest check-in for today.
 * Tab 2: "Daily history" – will group today’s check-ins by user.
 *
 * For now this is just a skeleton you can wire up later.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    navController: NavController
) {
    var selectedTab by remember { mutableStateOf(0) }

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

            when (selectedTab) {
                0 -> {
                    Text(
                        text = "Today at a glance view – coming soon.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                1 -> {
                    Text(
                        text = "Daily history view – coming soon.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
