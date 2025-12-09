package com.example.fredmobile.auth

/**
 * Immutable UI state for the authentication screen.
 *
 * This state is produced by [AuthViewModel] and consumed by the
 * composable auth screen. It keeps all auth-related values in one
 * place so the UI can be driven purely from state.
 *
 * @param email Current value of the email text field.
 * @param password Current value of the password text field.
 * @param displayName Optional display name used during registration.
 * @param isRegistering True when the screen is in "create account" mode.
 * @param isLoading True while a sign-in or registration request is in progress.
 * @param errorMessage Latest error message to show in the UI, if any.
 * @param currentUser The currently authenticated [AuthUser], or null if signed out.
 */
data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val displayName: String = "",
    val isRegistering: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val currentUser: AuthUser? = null
)