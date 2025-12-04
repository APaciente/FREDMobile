package com.example.fredmobile.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.navigation.NavController
import com.example.fredmobile.model.Site

/**
 * Screen that shows the list of work sites.
 * For PM1 we use a static list of sample sites.
 * Later milestones will load these from Firestore / Room.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SitesScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sites") },
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

            Text(
                text = "Assigned work sites",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "These are example sites for Milestone 1. " +
                        "In later milestones they will come from the database.",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(sampleSites) { site ->
                    SiteCard(
                        site = site,
                        onViewDetails = {
                            // TODO: navigate to a SiteDetailScreen later (PM1/PM2)
                            // navController.navigate("site_detail/${site.id}")
                        }
                    )
                }
            }
        }
    }
}

/**
 * Card UI for a single site.
 */
@Composable
fun SiteCard(
    site: Site,
    onViewDetails: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = site.name,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = site.address,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Lat: ${site.latitude}, Lon: ${site.longitude}",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextButton(onClick = onViewDetails) {
                    Text("View details (coming soon)")
                }
            }
        }
    }
}

/**
 * Static sample data for PM1 UI.
 */
private val sampleSites = listOf(
    Site(
        id = "site1",
        name = "North River Plant",
        address = "1000 River Rd, Winnipeg, MB",
        latitude = 49.900,
        longitude = -97.150
    ),
    Site(
        id = "site2",
        name = "West Substation",
        address = "250 Industrial Ave, Winnipeg, MB",
        latitude = 49.895,
        longitude = -97.210
    ),
    Site(
        id = "site3",
        name = "Downtown Office",
        address = "400 Main St, Winnipeg, MB",
        latitude = 49.887,
        longitude = -97.135
    )
)
