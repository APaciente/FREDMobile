package com.example.fredmobile.ui.sites

import android.app.Application
import android.location.Geocoder
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fredmobile.model.Site
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import java.util.UUID

data class SitesUiState(
    val sites: List<Site> = emptyList(),
    val previewLatLng: LatLng? = null,
    val previewAddress: String? = null,
    val isSearching: Boolean = false,
    val errorMessage: String? = null,
    val geofencedSiteIds: Set<String> = emptySet()
)

/**
 * Holds the list of sites for the Sites screen and manages geofence toggles.
 * Currently keeps sites in memory only; a future version can persist them to Firestore or Room.
 */
class SitesViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val geofenceManager = GeofenceManager(application)

    // Start with an empty list of sites
    var uiState by mutableStateOf(SitesUiState())
        private set

    /**
     * Search an address string and show a preview marker on the map.
     */
    fun searchAddress(query: String) {
        if (query.isBlank()) return

        viewModelScope.launch {
            uiState = uiState.copy(isSearching = true, errorMessage = null)

            try {
                val geocoder = Geocoder(getApplication(), Locale.getDefault())
                val result = withContext(Dispatchers.IO) {
                    @Suppress("DEPRECATION")
                    geocoder.getFromLocationName(query, 1)
                }?.firstOrNull()

                if (result == null) {
                    uiState = uiState.copy(
                        isSearching = false,
                        previewLatLng = null,
                        previewAddress = null,
                        errorMessage = "Could not find that location."
                    )
                    return@launch
                }

                val latLng = LatLng(result.latitude, result.longitude)
                val formatted = buildString {
                    if (!result.featureName.isNullOrBlank()) append(result.featureName)
                    if (!result.thoroughfare.isNullOrBlank()) {
                        if (isNotEmpty()) append(" ")
                        append(result.thoroughfare)
                    }
                    if (!result.locality.isNullOrBlank()) {
                        if (isNotEmpty()) append(", ")
                        append(result.locality)
                    }
                }.ifBlank { null }

                uiState = uiState.copy(
                    isSearching = false,
                    previewLatLng = latLng,
                    previewAddress = formatted,
                    errorMessage = null
                )
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isSearching = false,
                    previewLatLng = null,
                    previewAddress = null,
                    errorMessage = "Search failed. Please try again."
                )
            }
        }
    }

    /**
     * Save the current preview as a new site and optionally enable geofencing.
     */
    fun savePreviewAsSite(name: String, enableGeofence: Boolean) {
        val latLng = uiState.previewLatLng ?: return
        val address = uiState.previewAddress ?: "Planned site"

        val newSite = Site(
            id = UUID.randomUUID().toString(),
            name = name.ifBlank { "Planned site" },
            address = address,
            latitude = latLng.latitude,
            longitude = latLng.longitude
        )

        val updatedSites = uiState.sites + newSite
        uiState = uiState.copy(sites = updatedSites)

        if (enableGeofence) {
            setGeofenceEnabled(newSite, true)
        }

        // Clear preview
        uiState = uiState.copy(
            previewLatLng = null,
            previewAddress = null
        )
    }

    /**
     * Toggle geofence for a given site.
     */
    fun setGeofenceEnabled(site: Site, enabled: Boolean) {
        val currentIds = uiState.geofencedSiteIds.toMutableSet()

        if (enabled) {
            currentIds.add(site.id)
            geofenceManager.addGeofenceForSite(site)
        } else {
            currentIds.remove(site.id)
            geofenceManager.removeGeofenceForSite(site.id)
        }

        uiState = uiState.copy(geofencedSiteIds = currentIds)
    }

    /**
     * Remove a site and its geofence (if enabled).
     */
    fun deleteSite(site: Site) {
        val updatedSites = uiState.sites.filterNot { it.id == site.id }
        val updatedGeofences = uiState.geofencedSiteIds - site.id

        uiState = uiState.copy(
            sites = updatedSites,
            geofencedSiteIds = updatedGeofences
        )

        // Also remove the geofence from Google Play Services
        geofenceManager.removeGeofenceForSite(site.id)
    }
}
