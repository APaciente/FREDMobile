package com.example.fredmobile.ui.incident

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fredmobile.data.FirestoreRepository
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for submitting incidents to [FirestoreRepository].
 *
 * Exposes simple state flags for saving progress and error messages so
 * the UI can show loading indicators and feedback to the user.
 */
class IncidentViewModel(
    private val repo: FirestoreRepository = FirestoreRepository()
) : ViewModel() {

    /**
     * True while an incident is being saved.
     */
    var isSaving by mutableStateOf(false)
        private set

    /**
     * Latest error message to display in the UI, or null if there is no error.
     */
    var errorMessage by mutableStateOf<String?>(null)
        private set

    /**
     * Submits a new incident and optionally includes a photo reference.
     *
     * @param siteId Identifier of the site where the incident occurred.
     * @param siteName Human-readable name of the site.
     * @param severity Incident severity level.
     * @param description Text description of the incident.
     * @param photoUri Optional local URI of a photo to attach to the incident.
     * @param onSuccess Callback invoked after the incident is successfully saved.
     */
    fun submitIncident(
        siteId: String,
        siteName: String,
        severity: String,
        description: String,
        photoUri: String?,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                isSaving = true
                errorMessage = null

                repo.createIncidentWithOptionalPhoto(
                    siteId = siteId,
                    siteName = siteName,
                    severity = severity,
                    description = description,
                    localPhotoUri = photoUri
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
