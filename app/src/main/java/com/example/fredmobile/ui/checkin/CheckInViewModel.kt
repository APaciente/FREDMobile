package com.example.fredmobile.ui.checkin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fredmobile.data.FirestoreRepository
import kotlinx.coroutines.launch

/**
 * ViewModel that manages check-in / check-out state and
 * persists it to Firestore for the current authenticated user.
 */
data class CheckInUiState(
    val isLoading: Boolean = false,
    val isCheckedIn: Boolean = false,
    val activeCheckInId: String? = null,
    val statusText: String = "Not checked in yet today.",
    val errorMessage: String? = null
)

class CheckInViewModel(
    private val repo: FirestoreRepository = FirestoreRepository()
) : ViewModel() {

    var uiState by mutableStateOf(CheckInUiState())
        private set

    init {
        // On startup, see if there is an active IN_PROGRESS check-in
        refreshFromFirestore()
    }

    fun refreshFromFirestore() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            try {
                val all = repo.getCheckInsOnce()
                val active = all.lastOrNull { it.status == "IN_PROGRESS" }
                val lastCompleted = all.lastOrNull { it.status == "COMPLETED" }

                if (active != null) {
                    uiState = uiState.copy(
                        isLoading = false,
                        isCheckedIn = true,
                        activeCheckInId = active.id,
                        statusText = "Checked in at ${active.siteName}."
                    )
                } else if (lastCompleted != null) {
                    uiState = uiState.copy(
                        isLoading = false,
                        isCheckedIn = false,
                        activeCheckInId = null,
                        statusText = "Last check-in completed at ${lastCompleted.siteName}."
                    )
                } else {
                    uiState = CheckInUiState() // default
                }
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = "Could not load check-in history."
                )
            }
        }
    }

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
