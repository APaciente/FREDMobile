package com.example.fredmobile.ui.history

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fredmobile.data.FirestoreRepository
import com.example.fredmobile.model.firestore.CheckIn
import com.example.fredmobile.model.firestore.Incident
import kotlinx.coroutines.launch

/**
 * Immutable UI state for the history screen.
 *
 * @param isLoading True while history data is being loaded.
 * @param checkIns List of check-in records for the current user.
 * @param incidents List of incident records for the current user.
 * @param errorMessage Error message to display if loading fails, or null if none.
 */
data class HistoryUiState(
    val isLoading: Boolean = false,
    val checkIns: List<CheckIn> = emptyList(),
    val incidents: List<Incident> = emptyList(),
    val errorMessage: String? = null
)

/**
 * ViewModel that loads and manages check-in and incident history
 * from [FirestoreRepository] for the currently authenticated user.
 */
class HistoryViewModel(
    private val repo: FirestoreRepository = FirestoreRepository()
) : ViewModel() {

    /**
     * Current UI state for the history screen.
     */
    var uiState by mutableStateOf(HistoryUiState())
        private set

    init {
        refresh()
    }

    /**
     * Reloads check-in and incident history from Firestore and updates the UI state.
     */
    fun refresh() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            try {
                val checkIns = repo.getCheckInsOnce()
                val incidents = repo.getIncidentsOnce()
                uiState = uiState.copy(
                    isLoading = false,
                    checkIns = checkIns,
                    incidents = incidents
                )
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = "Could not load history."
                )
            }
        }
    }

    /**
     * Deletes a check-in by its ID and refreshes the history lists.
     *
     * @param id Firestore document ID of the check-in to delete.
     */
    fun deleteCheckIn(id: String) {
        viewModelScope.launch {
            try {
                repo.deleteCheckIn(id)
                refresh()
            } catch (_: Exception) {
                // Optional: handle delete errors in the UI if needed.
            }
        }
    }

    /**
     * Deletes an incident by its ID and refreshes the history lists.
     *
     * @param id Firestore document ID of the incident to delete.
     */
    fun deleteIncident(id: String) {
        viewModelScope.launch {
            try {
                repo.deleteIncident(id)
                refresh()
            } catch (_: Exception) {
                // Optional: handle delete errors in the UI if needed.
            }
        }
    }
}
