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
 * ViewModel that loads check-in and incident history from Firestore.
 *
 * Used by the History screen for PM3 (data persistence + CRUD).
 */
data class HistoryUiState(
    val isLoading: Boolean = false,
    val checkIns: List<CheckIn> = emptyList(),
    val incidents: List<Incident> = emptyList(),
    val errorMessage: String? = null
)

class HistoryViewModel(
    private val repo: FirestoreRepository = FirestoreRepository()
) : ViewModel() {

    var uiState by mutableStateOf(HistoryUiState())
        private set

    init {
        refresh()
    }

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

    fun deleteCheckIn(id: String) {
        viewModelScope.launch {
            try {
                repo.deleteCheckIn(id)
                refresh() // reload list after delete
            } catch (_: Exception) {
                // you can add error UI later if you want
            }
        }
    }

    fun deleteIncident(id: String) {
        viewModelScope.launch {
            try {
                repo.deleteIncident(id)
                refresh()
            } catch (_: Exception) { }
        }
    }
}
