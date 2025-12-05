package com.example.fredmobile.auth

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

/**
 * ViewModel that manages sign-in / sign-up / sign-out state.
 *
 * Milestone 3:
 *  - Email/password sign-in + registration
 *  - Google sign-in (via ID token)
 *  - Exposes a simple AuthUiState for the composable AuthScreen.
 */
class AuthViewModel(
    private val repo: AuthRepository = AuthRepository()
) : ViewModel() {

    var uiState = mutableStateOf(AuthUiState())
        private set

    init {
        // If a user is already signed in, expose them to the UI
        uiState.value = uiState.value.copy(
            currentUser = repo.currentUser()
        )
    }

    fun onEmailChange(newEmail: String) {
        uiState.value = uiState.value.copy(email = newEmail, errorMessage = null)
    }

    fun onPasswordChange(newPassword: String) {
        uiState.value = uiState.value.copy(password = newPassword, errorMessage = null)
    }

    fun onDisplayNameChange(newName: String) {
        uiState.value = uiState.value.copy(displayName = newName, errorMessage = null)
    }

    fun switchToRegisterMode() {
        uiState.value = uiState.value.copy(
            isRegistering = true,
            errorMessage = null
        )
    }

    fun switchToSignInMode() {
        uiState.value = uiState.value.copy(
            isRegistering = false,
            errorMessage = null
        )
    }

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
     * Called from the UI when Google Sign-In returns an ID token.
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

    fun setGoogleError(message: String) {
        uiState.value = uiState.value.copy(
            isLoading = false,
            errorMessage = message
        )
    }

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
