package com.example.fredmobile.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Holds UI state for the authentication screen and talks to [AuthRepository].
 */
data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLoginMode: Boolean = true  // true = login, false = register
)

class AuthViewModel(
    private val repo: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    val isLoggedIn: Boolean
        get() = repo.currentUser != null

    fun onEmailChange(newEmail: String) {
        _uiState.value = _uiState.value.copy(email = newEmail, errorMessage = null)
    }

    fun onPasswordChange(newPassword: String) {
        _uiState.value = _uiState.value.copy(password = newPassword, errorMessage = null)
    }

    fun toggleMode() {
        _uiState.value = _uiState.value.copy(
            isLoginMode = !_uiState.value.isLoginMode,
            errorMessage = null
        )
    }

    fun submit(onSuccess: () -> Unit) {
        val state = _uiState.value

        if (state.email.isBlank() || state.password.isBlank()) {
            _uiState.value = state.copy(errorMessage = "Email and password are required.")
            return
        }

        _uiState.value = state.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            val result = if (state.isLoginMode) {
                repo.signIn(state.email, state.password)
            } else {
                repo.signUp(state.email, state.password)
            }

            _uiState.value = _uiState.value.copy(isLoading = false)

            result.onSuccess {
                onSuccess()
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "Authentication failed."
                )
            }
        }
    }

    fun signOut() {
        repo.signOut()
        _uiState.value = AuthUiState()  // reset fields
    }
}
