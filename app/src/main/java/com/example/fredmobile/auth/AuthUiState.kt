package com.example.fredmobile.auth

/**
 * UI state for the authentication screen.
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
