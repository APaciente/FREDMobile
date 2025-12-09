package com.example.fredmobile.ui.location

import android.annotation.SuppressLint
import android.app.Application
import android.location.Geocoder
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Locale

data class LocationUiState(
    val isLoading: Boolean = false,
    val lastLocation: LatLng? = null,
    val address: String? = null,
    val errorMessage: String? = null
)

/**
 * Holds the *current device location* + resolved address.
 * Used by both Check-In and Incident screens.
 */
class LocationViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val fusedClient =
        LocationServices.getFusedLocationProviderClient(application)

    var uiState by mutableStateOf(LocationUiState())
        private set

    @SuppressLint("MissingPermission")
    fun refreshLocation() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)

            try {
                val loc = fusedClient.lastLocation.await()
                if (loc == null) {
                    uiState = uiState.copy(
                        isLoading = false,
                        errorMessage = "Location not available."
                    )
                    return@launch
                }

                val latLng = LatLng(loc.latitude, loc.longitude)

                val geocoder = Geocoder(getApplication(), Locale.getDefault())
                val addr = withContext(Dispatchers.IO) {
                    @Suppress("DEPRECATION")
                    geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                }?.firstOrNull()

                val line1 = listOfNotNull(
                    addr?.featureName,
                    addr?.thoroughfare
                ).joinToString(" ")

                val cityLine = listOfNotNull(
                    addr?.locality,
                    addr?.adminArea
                ).joinToString(", ")

                val formatted = listOfNotNull(line1, cityLine)
                    .filter { it.isNotBlank() }
                    .joinToString(", ")

                uiState = LocationUiState(
                    isLoading = false,
                    lastLocation = latLng,
                    address = formatted.ifBlank { null },
                    errorMessage = null
                )
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = "Location not available."
                )
            }
        }
    }
}
