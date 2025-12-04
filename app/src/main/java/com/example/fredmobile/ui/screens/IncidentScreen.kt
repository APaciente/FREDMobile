package com.example.fredmobile.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.fredmobile.ui.incident.IncidentViewModel
import kotlinx.coroutines.launch

/**
 * Incident report screen.
 * PM1: local-only form with fake submit action.
 * PM3: now saves incidents to Firestore via [IncidentViewModel].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncidentScreen(
    navController: NavController,
    // For now these can be hard-coded or passed from Check-in/Sites later
    siteId: String = "demo-site-id",
    siteName: String = "Current Site",
    incidentViewModel: IncidentViewModel = viewModel()
) {
    var description by remember { mutableStateOf(TextFieldValue("")) }
    var severity by remember { mutableStateOf("Medium") }
    var includePhoto by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val isSaving = incidentViewModel.isSaving
    val errorMessage = incidentViewModel.errorMessage

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Report Incident") },
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

            // PHOTO + EXTRA (still PM1 placeholder)
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
                        // simple validation
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = "Please add a description before submitting."
                            )
                        }
                    } else {
                        incidentViewModel.submitIncident(
                            siteId = siteId,
                            siteName = siteName,
                            severity = severity,
                            description = description.text
                        ) {
                            // onSuccess
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "Incident saved to Firestore.",
                                    withDismissAction = true
                                )
                            }

                            // reset form
                            description = TextFieldValue("")
                            severity = "Medium"
                            includePhoto = false

                            // optional: go back to previous screen (e.g., History)
                            navController.popBackStack()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = !isSaving
            ) {
                Text(if (isSaving) "Submitting..." else "Submit Incident")
            }

            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

/**
 * Simple severity selector using Filter chips.
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
