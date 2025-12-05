package com.example.fredmobile.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.fredmobile.R
import com.example.fredmobile.ui.checkin.CheckInViewModel
import com.example.fredmobile.ui.navigation.FredBottomBar
import com.example.fredmobile.ui.navigation.Routes
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

/**
 * Home dashboard screen for FRED.
 * Avatar, safety tips, and quick access to check-in.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    checkInViewModel: CheckInViewModel = viewModel()
) {
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    // Try displayName, then email before the @, otherwise fallback
    val firstName = remember(currentUser) {
        val raw = currentUser?.displayName
            ?: currentUser?.email?.substringBefore("@")
            ?: "there"
        raw.substringBefore(" ")
    }

    // Use existing check-in state so messages can react to it
    val checkInState = checkInViewModel.uiState
    val isCheckedIn = checkInState.isCheckedIn

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Field Ready Dashboard") },
                actions = {
                    IconButton(onClick = { navController.navigate(Routes.SETTINGS) }) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Settings"
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
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
                .fillMaxSize()
        ) {

            // Avatar + coach messages
            AssistantAvatarSection(
                firstName = firstName,
                isCheckedIn = isCheckedIn
            )

            Spacer(modifier = Modifier.height(8.dp))

            // CHECK-IN button near bottom, full width
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = { navController.navigate(Routes.CHECKIN) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Check In / Out")
                }
            }
        }
    }
}

/**
 * Wraps the message card + avatar image.
 * For now this only reacts to check-in status (no live weather / AQI yet).
 */
@Composable
private fun AssistantAvatarSection(
    firstName: String,
    isCheckedIn: Boolean,
    modifier: Modifier = Modifier
) {
    // Build the dynamic messages list
    val messages = remember(firstName, isCheckedIn) {
        buildList {
            // Status-aware main reminder
            if (isCheckedIn) {
                add("You’re checked in right now. Don’t forget to check out before you leave.")
            } else {
                add("Hi $firstName, don’t forget to check in when you arrive on site.")
            }

            // Generic / app-related tips
            add("If you see something unsafe, log an incident so your team knows.")
            add("You can review your past shifts any time in the History tab.")
            add("Heading to a different site today? Double-check the map before you check in.")
            add("Stay safe out there today.")
        }
    }

    var messageIndex by remember { mutableStateOf(0) }
    val currentMessage = messages[messageIndex]

    // Word-by-word animation text
    var animatedText by remember(messageIndex) { mutableStateOf("") }

    LaunchedEffect(messageIndex) {
        animatedText = ""
        val words = currentMessage.split(" ")
        for (i in 1..words.size) {
            animatedText = words.take(i).joinToString(" ")
            delay(60)
        }
    }

    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Message card (fixed height so it doesn't jump)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Hi $firstName",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = if (animatedText.isNotBlank()) animatedText else currentMessage,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // Avatar image – zoomed, but aligned so head stays visible
        Image(
            painter = painterResource(id = R.drawable.fred_avatar),
            contentDescription = "FRED assistant avatar",
            contentScale = ContentScale.Crop,
            alignment = Alignment.TopCenter,
            modifier = Modifier
                .fillMaxWidth()
                .height(340.dp)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null // hidden interaction
                ) {
                    // Cycle tip on tap
                    messageIndex = (messageIndex + 1) % messages.size
                }
        )
    }
}
