package com.example.fredmobile.auth

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

/**
 * ViewModel that manages authentication state and operations.
 *
 * It exposes [AuthUiState] to the UI and delegates all auth calls
 * to [AuthRepository]. The composable screens interact with this
 * ViewModel instead of talking to Firebase directly.
 */
class AuthViewModel(
    private val repo: AuthRepository = AuthRepository()
) : ViewModel() {

    /**
     * Current authentication UI state.
     */
    var uiState = mutableStateOf(AuthUiState())
        private set

    init {
        // If a user is already signed in, expose them to the UI.
        uiState.value = uiState.value.copy(
            currentUser = repo.currentUser()
        )
    }

    /**
     * Updates the email field and clears any existing error message.
     */
    fun onEmailChange(newEmail: String) {
        uiState.value = uiState.value.copy(email = newEmail, errorMessage = null)
    }

    /**
     * Updates the password field and clears any existing error message.
     */
    fun onPasswordChange(newPassword: String) {
        uiState.value = uiState.value.copy(password = newPassword, errorMessage = null)
    }

    /**
     * Updates the display name field and clears any existing error message.
     */
    fun onDisplayNameChange(newName: String) {
        uiState.value = uiState.value.copy(displayName = newName, errorMessage = null)
    }

    /**
     * Switches the screen into "register new account" mode.
     */
    fun switchToRegisterMode() {
        uiState.value = uiState.value.copy(
            isRegistering = true,
            errorMessage = null
        )
    }

    /**
     * Switches the screen into "sign in" mode.
     */
    fun switchToSignInMode() {
        uiState.value = uiState.value.copy(
            isRegistering = false,
            errorMessage = null
        )
    }

    /**
     * Attempts to sign in with the current email and password fields.
     */
    fun signInWithEmail() {
        val email = uiState.value.email.trim()
        val password = uiState.value.password

        if (email.isBlank() || password.isBlank()) {
            uiState.value = uiState.value.copy(
                errorMessage = "Email and password are required."
            )
            return
        }

        uiState.value = uiState.value.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            try {
                val user = repo.signInWithEmail(email, password)
                uiState.value = uiState.value.copy(
                    isLoading = false,
                    currentUser = user
                )
            } catch (e: Exception) {
                uiState.value = uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Sign-in failed."
                )
            }
        }
    }

    /**
     * Attempts to register a new account with the current form values.
     */
    fun registerWithEmail() {
        val email = uiState.value.email.trim()
        val password = uiState.value.password
        val displayName = uiState.value.displayName.trim()

        if (email.isBlank() || password.isBlank() || displayName.isBlank()) {
            uiState.value = uiState.value.copy(
                errorMessage = "Name, email, and password are required."
            )
            return
        }

        uiState.value = uiState.value.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            try {
                val user = repo.registerWithEmail(email, password, displayName)
                uiState.value = uiState.value.copy(
                    isLoading = false,
                    currentUser = user,
                    isRegistering = false
                )
            } catch (e: Exception) {
                uiState.value = uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Registration failed."
                )
            }
        }
    }

    /**
     * Called when Google Sign-In returns an ID token.
     * Uses the token to authenticate with Firebase.
     */
    fun handleGoogleIdToken(idToken: String) {
        uiState.value = uiState.value.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            try {
                val user = repo.signInWithGoogle(idToken)
                uiState.value = uiState.value.copy(
                    isLoading = false,
                    currentUser = user
                )
            } catch (e: Exception) {
                uiState.value = uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Google sign-in failed."
                )
            }
        }
    }

    /**
     * Sets an error message related to Google sign-in.
     */
    fun setGoogleError(message: String) {
        uiState.value = uiState.value.copy(
            isLoading = false,
            errorMessage = message
        )
    }

    /**
     * Signs the current user out and clears auth-related fields.
     */
    fun signOut() {
        repo.signOut()
        uiState.value = uiState.value.copy(
            currentUser = null,
            email = "",
            password = "",
            displayName = "",
            isRegistering = false
        )
    }
}
