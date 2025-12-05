package com.example.fredmobile.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fredmobile.R
import com.example.fredmobile.auth.AuthViewModel
import com.example.fredmobile.auth.AuthUiState
import com.example.fredmobile.ui.navigation.Routes
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

/**
 * Authentication screen for FRED.
 *
 * Milestone 3:
 *  - Email/password sign-in and registration
 *  - Google Sign-In (second provider)
 *  - Navigates to Home when a user is authenticated.
 */
@Composable
fun AuthScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val uiState by authViewModel.uiState
    val context = LocalContext.current
    val webClientId = stringResource(id = R.string.default_web_client_id)

    // Google Sign-In config
    val googleSignInClient = remember(webClientId) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .build()
        GoogleSignIn.getClient(context, gso)
    }

    // Launcher for Google sign-in activity
    val googleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account.idToken
            if (idToken != null) {
                authViewModel.handleGoogleIdToken(idToken)
            } else {
                authViewModel.setGoogleError("Google sign-in failed: missing ID token.")
            }
        } catch (e: ApiException) {
            authViewModel.setGoogleError(
                "Google sign-in cancelled or failed (code ${e.statusCode})."
            )
        } catch (e: Exception) {
            authViewModel.setGoogleError(
                "Google sign-in error: ${e.localizedMessage ?: "Unknown error"}"
            )
        }
    }

    // Navigate away when user is authenticated
    LaunchedEffect(uiState.currentUser) {
        if (uiState.currentUser != null) {
            navController.navigate(Routes.HOME) {
                popUpTo(Routes.AUTH) { inclusive = true }
            }
        }
    }

    AuthContent(
        uiState = uiState,
        onEmailChange = authViewModel::onEmailChange,
        onPasswordChange = authViewModel::onPasswordChange,
        onDisplayNameChange = authViewModel::onDisplayNameChange,
        onSignInClick = authViewModel::signInWithEmail,
        onRegisterClick = authViewModel::registerWithEmail,
        onSwitchToRegister = authViewModel::switchToRegisterMode,
        onSwitchToSignIn = authViewModel::switchToSignInMode,
        onGoogleClick = {
            val intent = googleSignInClient.signInIntent
            googleLauncher.launch(intent)
        }
    )
}

@Composable
private fun AuthContent(
    uiState: AuthUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onDisplayNameChange: (String) -> Unit,
    onSignInClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onSwitchToRegister: () -> Unit,
    onSwitchToSignIn: () -> Unit,
    onGoogleClick: () -> Unit
) {
    val isRegister = uiState.isRegistering

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 32.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "FRED Mobile",
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = if (isRegister) "Create your account" else "Sign in to continue",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Name only in register mode
            if (isRegister) {
                OutlinedTextField(
                    value = uiState.displayName,
                    onValueChange = onDisplayNameChange,
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            OutlinedTextField(
                value = uiState.email,
                onValueChange = onEmailChange,
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.password,
                onValueChange = onPasswordChange,
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (!isRegister) {
                // SIGN IN MODE
                Button(
                    onClick = onSignInClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    enabled = !uiState.isLoading
                ) {
                    Text("Sign in")
                }

                OutlinedButton(
                    onClick = onSwitchToRegister,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    enabled = !uiState.isLoading
                ) {
                    Text("Create account")
                }
            } else {
                // REGISTER MODE
                Button(
                    onClick = onRegisterClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    enabled = !uiState.isLoading
                ) {
                    Text("Create account")
                }

                TextButton(onClick = onSwitchToSignIn) {
                    Text("Back to sign in")
                }
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            OutlinedButton(
                onClick = onGoogleClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                enabled = !uiState.isLoading
            ) {
                Text("Sign in with Google")
            }

            if (uiState.isLoading) {
                Spacer(modifier = Modifier.height(12.dp))
                CircularProgressIndicator()
            }
        }
    }
}
