package com.example.fredmobile.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.fredmobile.model.Site
import com.example.fredmobile.ui.navigation.FredBottomBar
import com.example.fredmobile.ui.sites.SitesUiState
import com.example.fredmobile.ui.sites.SitesViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

/**
 * Screen for managing work sites, including searching, previewing on a map,
 * and configuring geofences for saved locations.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SitesScreen(
    navController: NavController,
    sitesViewModel: SitesViewModel
) {
    val uiState = sitesViewModel.uiState

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
        },
        bottomBar = {
            FredBottomBar(navController = navController)
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                SearchAndMapSection(
                    uiState = uiState,
                    onSearch = { query -> sitesViewModel.searchAddress(query) },
                    onSavePreview = { name, enableGeofence ->
                        sitesViewModel.savePreviewAsSite(name, enableGeofence)
                    }
                )
            }

            item {
                Text(
                    text = "Saved sites",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            items(uiState.sites) { site ->
                val geofenceOn = uiState.geofencedSiteIds.contains(site.id)

                SiteCard(
                    site = site,
                    geofenceEnabled = geofenceOn,
                    onGeofenceToggle = { s, enabled ->
                        sitesViewModel.setGeofenceEnabled(s, enabled)
                    },
                    onDelete = { s ->
                        sitesViewModel.deleteSite(s)
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

/**
 * Search section for looking up addresses, previewing them on the map,
 * and saving the preview as a site with optional geofencing.
 */
@Composable
private fun SearchAndMapSection(
    uiState: SitesUiState,
    onSearch: (String) -> Unit,
    onSavePreview: (name: String, enableGeofence: Boolean) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var newSiteName by remember { mutableStateOf("") }
    var enableGeofence by remember { mutableStateOf(true) }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Search address or place") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = { onSearch(query) },
                enabled = query.isNotBlank() && !uiState.isSearching,
                modifier = Modifier.weight(1f)
            ) {
                Text(if (uiState.isSearching) "Searching..." else "Search")
            }
        }

        if (uiState.errorMessage != null) {
            Text(
                text = uiState.errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        MapPreviewAllSites(uiState = uiState)

        if (uiState.previewLatLng != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = uiState.previewAddress ?: "Preview location",
                style = MaterialTheme.typography.bodySmall
            )
            OutlinedTextField(
                value = newSiteName,
                onValueChange = { newSiteName = it },
                label = { Text("Site name (optional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Checkbox(
                    checked = enableGeofence,
                    onCheckedChange = { enableGeofence = it }
                )
                Text("Enable geofence for this site")
            }
            Button(
                onClick = { onSavePreview(newSiteName, enableGeofence) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add site from preview")
            }
        }
    }
}

/**
 * Map view that displays all saved sites and the current preview marker.
 */
@Composable
private fun MapPreviewAllSites(
    uiState: SitesUiState
) {
    val context = LocalContext.current
    val mapView = remember {
        MapView(context).apply { onCreate(null) }
    }

    DisposableEffect(Unit) {
        mapView.onStart()
        mapView.onResume()
        onDispose {
            mapView.onPause()
            mapView.onStop()
            mapView.onDestroy()
        }
    }

    val sites = uiState.sites
    val previewLatLng = uiState.previewLatLng

    LaunchedEffect(sites, previewLatLng) {
        mapView.getMapAsync { googleMap ->
            googleMap.uiSettings.setAllGesturesEnabled(true)

            googleMap.clear()

            sites.forEach { site ->
                val pos = LatLng(site.latitude, site.longitude)
                googleMap.addMarker(
                    MarkerOptions()
                        .position(pos)
                        .title(site.name)
                        .snippet(site.address)
                )
            }

            previewLatLng?.let { latLng ->
                googleMap.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .title("Preview")
                )
            }

            val focus = previewLatLng
                ?: sites.firstOrNull()?.let { LatLng(it.latitude, it.longitude) }
                ?: LatLng(49.9, -97.15)

            googleMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(focus, 12f)
            )
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { mapView }
        )
    }
}

/**
 * Card layout for displaying a single work site with a geofence toggle and remove action.
 */
@Composable
private fun SiteCard(
    site: Site,
    geofenceEnabled: Boolean,
    onGeofenceToggle: (Site, Boolean) -> Unit,
    onDelete: (Site) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
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
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Auto check-in / out",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "FRED uses this geofence for this site.",
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Switch(
                        checked = geofenceEnabled,
                        onCheckedChange = { enabled ->
                            onGeofenceToggle(site, enabled)
                        }
                    )
                    TextButton(
                        onClick = { onDelete(site) }
                    ) {
                        Text("Remove")
                    }
                }
            }
        }
    }
}
