package com.example.fredmobile.ui.incident

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fredmobile.data.FirestoreRepository
import kotlinx.coroutines.launch

/**
 * ViewModel for submitting incidents to Firestore (PM3).
 */
class IncidentViewModel(
    private val repo: FirestoreRepository = FirestoreRepository()
) : ViewModel() {

    var isSaving by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun submitIncident(
        siteId: String,
        siteName: String,
        severity: String,
        description: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                isSaving = true
                errorMessage = null

                repo.createIncident(
                    siteId = siteId,
                    siteName = siteName,
                    severity = severity,
                    description = description
                )

                isSaving = false
                onSuccess()
            } catch (e: Exception) {
                isSaving = false
                errorMessage = "Could not submit incident."
            }
        }
    }
}
