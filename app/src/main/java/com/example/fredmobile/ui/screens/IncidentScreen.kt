package com.example.fredmobile.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.fredmobile.ui.incident.IncidentViewModel
import com.example.fredmobile.ui.navigation.FredBottomBar
import kotlinx.coroutines.launch

/**
 * Incident report screen.
 * PM1: local-only form with fake submit action.
 * PM3: now saves incidents to Firestore via [IncidentViewModel].
 *
 * Attachments:
 *  - Lets the user pick a photo from the gallery.
 *  - ViewModel can upload this to Firebase Storage and store the link.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncidentScreen(
    navController: NavController,
    siteId: String = "demo-site-id",
    siteName: String = "Current Site",
    incidentViewModel: IncidentViewModel = viewModel()
) {
    var description by remember { mutableStateOf(TextFieldValue("")) }
    var severity by remember { mutableStateOf("Medium") }
    var includePhoto by remember { mutableStateOf(false) }

    // Store the selected photo URI as a String so it survives config changes
    var photoUri by rememberSaveable { mutableStateOf<String?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    val isSaving = incidentViewModel.isSaving
    val errorMessage = incidentViewModel.errorMessage

    // Gallery picker launcher
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        photoUri = uri?.toString()
        includePhoto = uri != null
    }

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
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            FredBottomBar(navController = navController)
        }
    ) { innerPadding ->
        // OUTER COLUMN: content (scrollable) + fixed bottom area with button
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // SCROLLABLE CONTENT
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(scrollState),
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

                // ATTACHMENTS (smaller card)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
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
                                onCheckedChange = { checked ->
                                    includePhoto = checked
                                    if (checked) {
                                        pickImageLauncher.launch("image/*")
                                    } else {
                                        photoUri = null
                                    }
                                }
                            )

                            Column {
                                Text("Attach a photo from gallery")

                                if (photoUri != null) {
                                    Text(
                                        text = "Photo selected",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    TextButton(
                                        onClick = { pickImageLauncher.launch("image/*") }
                                    ) {
                                        Text("Change photo")
                                    }
                                } else if (includePhoto) {
                                    TextButton(
                                        onClick = { pickImageLauncher.launch("image/*") }
                                    ) {
                                        Text("Choose photo")
                                    }
                                }
                            }
                        }

                        Text(
                            text = "If you attach a photo, it will be uploaded with this incident record.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            // FIXED BOTTOM AREA (button + error text)
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Button(
                    onClick = {
                        if (description.text.isBlank()) {
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
                                description = description.text,
                                photoUri = photoUri,   // ✅ pass photoUri into VM
                                onSuccess = {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            message = "Incident saved to Firestore.",
                                            withDismissAction = true
                                        )
                                    }

                                    description = TextFieldValue("")
                                    severity = "Medium"
                                    includePhoto = false
                                    photoUri = null

                                    navController.popBackStack()
                                }
                            )
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
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
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
