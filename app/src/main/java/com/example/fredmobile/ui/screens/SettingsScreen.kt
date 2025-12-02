package com.example.fredmobile.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

/**
 * Settings and preferences screen for FRED.
 * Milestone 1: all state is local (fake).
 * Milestone 3: connect to DataStore with real saved preferences.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    onSignOut: () -> Unit
) {
    // Local-only fake settings for PM1
    var notificationsEnabled by remember { mutableStateOf(true) }
    var darkModeEnabled by remember { mutableStateOf(false) }
    var autoCheckIn by remember { mutableStateOf(false) }
    var weatherUnit by remember { mutableStateOf("Celsius") }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .fillMaxSize()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // GENERAL SETTINGS
            Text(
                text = "General Preferences",
                style = MaterialTheme.typography.titleMedium
            )

            SettingsSwitchItem(
                title = "Enable notifications",
                subtitle = "Receive incident updates, reminders, and check-in alerts.",
                checked = notificationsEnabled,
                onCheckedChange = { notificationsEnabled = it }
            )

            SettingsSwitchItem(
                title = "Dark mode",
                subtitle = "Use a darker color scheme to reduce eye strain.",
                checked = darkModeEnabled,
                onCheckedChange = { darkModeEnabled = it }
            )

            // SAFETY SETTINGS
            Text(
                text = "Safety Features",
                style = MaterialTheme.typography.titleMedium
            )

            SettingsSwitchItem(
                title = "Auto Check-In (placeholder)",
                subtitle = "Allow app to check you in automatically when entering a site geofence.",
                checked = autoCheckIn,
                onCheckedChange = { autoCheckIn = it }
            )

            // WEATHER SETTINGS
            Text(
                text = "Weather Settings",
                style = MaterialTheme.typography.titleMedium
            )

            WeatherUnitDropdown(
                selected = weatherUnit,
                onSelected = { weatherUnit = it }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // SIGN OUT (bigger, easier to tap, not jammed at the bottom)
            Button(
                onClick = onSignOut,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            ) {
                Text("Sign out")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Note: Settings will be saved with DataStore in Milestone 3.",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}


/**
 * A reusable switch item for settings.
 */
@Composable
fun SettingsSwitchItem(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .toggleable(
                    value = checked,
                    onValueChange = onCheckedChange
                )
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(subtitle, style = MaterialTheme.typography.bodySmall)
            }
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        }
    }
}

/**
 * Dropdown menu for selecting weather unit.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherUnitDropdown(
    selected: String,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf("Celsius", "Fahrenheit")

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Weather Unit", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selected,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Unit") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    options.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                onSelected(option)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}