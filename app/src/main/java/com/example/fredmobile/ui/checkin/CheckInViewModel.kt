package com.example.fredmobile.ui.checkin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fredmobile.data.FirestoreRepository
import kotlinx.coroutines.launch

/**
 * Immutable UI state for the check-in feature.
 *
 * @param isLoading True while a network or database operation is in progress.
 * @param isCheckedIn True if there is an active check-in for the user.
 * @param activeCheckInId ID of the active check-in document, if any.
 * @param statusText Human-readable status message shown in the UI.
 * @param errorMessage Latest error message to display, or null if none.
 */
data class CheckInUiState(
    val isLoading: Boolean = false,
    val isCheckedIn: Boolean = false,
    val activeCheckInId: String? = null,
    val statusText: String = "Not checked in yet today.",
    val errorMessage: String? = null
)

/**
 * ViewModel that manages check-in and check-out operations
 * and exposes [CheckInUiState] to the UI.
 *
 * It uses [FirestoreRepository] to read and write check-in data
 * for the currently authenticated user.
 */
class CheckInViewModel(
    private val repo: FirestoreRepository = FirestoreRepository()
) : ViewModel() {

    /**
     * Current UI state for the check-in screen.
     */
    var uiState by mutableStateOf(CheckInUiState())
        private set

    init {
        // On startup, attempt to restore the most recent check-in state.
        refreshFromFirestore()
    }

    /**
     * Reloads check-in history from Firestore and updates the UI state.
     *
     * If there is an active "IN_PROGRESS" check-in, it is restored.
     * Otherwise, the last completed check-in is used to build a status message.
     */
    fun refreshFromFirestore() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            try {
                val all = repo.getCheckInsOnce()
                val active = all.lastOrNull { it.status == "IN_PROGRESS" }
                val lastCompleted = all.lastOrNull { it.status == "COMPLETED" }

                uiState = when {
                    active != null -> {
                        uiState.copy(
                            isLoading = false,
                            isCheckedIn = true,
                            activeCheckInId = active.id,
                            statusText = "Checked in at ${active.siteName}."
                        )
                    }
                    lastCompleted != null -> {
                        uiState.copy(
                            isLoading = false,
                            isCheckedIn = false,
                            activeCheckInId = null,
                            statusText = "Last check-in completed at ${lastCompleted.siteName}."
                        )
                    }
                    else -> {
                        CheckInUiState()
                    }
                }
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = "Could not load check-in history."
                )
            }
        }
    }

    /**
     * Creates a new check-in for the given site and updates the UI state.
     *
     * @param siteId Identifier of the site to check into.
     * @param siteName Human-readable name of the site.
     */
    fun checkIn(siteId: String, siteName: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            try {
                val created = repo.createCheckIn(siteId, siteName)
                uiState = uiState.copy(
                    isLoading = false,
                    isCheckedIn = true,
                    activeCheckInId = created.id,
                    statusText = "Checked in just now at $siteName."
                )
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = "Check-in failed. Please try again."
                )
            }
        }
    }

    /**
     * Completes the currently active check-in, if there is one.
     */
    fun checkOut() {
        val activeId = uiState.activeCheckInId ?: return

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            try {
                repo.completeCheckIn(activeId)
                uiState = uiState.copy(
                    isLoading = false,
                    isCheckedIn = false,
                    activeCheckInId = null,
                    statusText = "Checked out just now."
                )
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = "Check-out failed. Please try again."
                )
            }
        }
    }
}
