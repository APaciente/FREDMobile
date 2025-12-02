package com.example.fredmobile.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

/**
 * Incident report screen.
 * Milestone 1: local-only form with fake submit action.
 * Later milestones: save to Firestore, upload photos, etc.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncidentScreen(navController: NavController) {
    var description by remember { mutableStateOf(TextFieldValue("")) }
    var severity by remember { mutableStateOf("Medium") }
    var includePhoto by remember { mutableStateOf(false) }
    var showSnackbar by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Report Incident") }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = "Use this form to record safety incidents at your current site.",
                style = MaterialTheme.typography.bodyMedium
            )

            // SEVERITY
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Severity",
                        style = MaterialTheme.typography.titleMedium
                    )

                    SeveritySelector(
                        selected = severity,
                        onSelectedChange = { severity = it }
                    )
                }
            }

            // DESCRIPTION
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Incident description",
                        style = MaterialTheme.typography.titleMedium
                    )
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("What happened?") },
                        placeholder = { Text("Describe the incident, location, and any injuries.") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 120.dp),
                        maxLines = 5
                    )
                }
            }

            // PHOTO + EXTRA
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Attachments (optional)",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Checkbox(
                            checked = includePhoto,
                            onCheckedChange = { includePhoto = it }
                        )
                        Text("I will attach a photo (placeholder for PM1).")
                    }

                    Text(
                        text = "In later milestones, this will open the camera or gallery and upload to Firebase Storage.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // SUBMIT BUTTON
            Button(
                onClick = {
                    if (description.text.isBlank()) {
                        // fake validation
                        showSnackbar = true
                    } else {
                        // For PM1 we just clear the form and show success
                        description = TextFieldValue("")
                        severity = "Medium"
                        includePhoto = false
                        showSnackbar = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Submit Incident (PM1 sample)")
            }

            // Show snackbar feedback
            LaunchedEffect(showSnackbar) {
                if (showSnackbar) {
                    snackbarHostState.showSnackbar(
                        message = "Incident submitted locally (demo for Milestone 1).",
                        withDismissAction = true
                    )
                    showSnackbar = false
                }
            }
        }
    }
}

/**
 * Simple severity selector using Assist chips.
 */
@Composable
private fun SeveritySelector(
    selected: String,
    onSelectedChange: (String) -> Unit
) {
    val options = listOf("Low", "Medium", "High")

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        options.forEach { level ->
            FilterChip(
                selected = selected == level,
                onClick = { onSelectedChange(level) },
                label = { Text(level) }
            )
        }
    }
}
