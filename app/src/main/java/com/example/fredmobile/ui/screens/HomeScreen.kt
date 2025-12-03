package com.example.fredmobile.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fredmobile.ui.navigation.Routes

/**
 * Home dashboard screen for FRED.
 * Displays quick actions and app overview.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("FRED – Dashboard") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            // APP TITLE
            Column {
                Text(
                    text = "Field Ready Employee Dashboard",
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = "Your daily safety companion.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // QUICK ACTIONS
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = { navController.navigate(Routes.CHECKIN) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Check In / Out")
                }

                Button(
                    onClick = { navController.navigate(Routes.INCIDENT) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Report Incident")
                }

                Button(
                    onClick = { navController.navigate(Routes.SITES) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("View Sites")
                }

                Button(
                    onClick = { navController.navigate(Routes.HISTORY) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("History")
                }

                Button(
                    onClick = { navController.navigate(Routes.SETTINGS) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Settings")
                }
            }
        }
    }
}