package com.example.fredmobile.ui.sites

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.fredmobile.geofence.GeofenceBroadcastReceiver
import com.example.fredmobile.model.Site
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices

/**
 * Wraps the Android geofencing APIs.
 *
 * Registers and removes geofences for planned sites. When a transition
 * happens, GeofenceBroadcastReceiver handles the event and writes to Firestore.
 */
class GeofenceManager(
    private val context: Context
) {

    private val geofencingClient: GeofencingClient =
        LocationServices.getGeofencingClient(context)

    private val geofenceRadiusMeters = 150f   // tweak as you like

    private val geofencePendingIntent: PendingIntent by lazy {
        // Make sure GeofenceBroadcastReceiver is in this package or update the import.
        val intent = Intent(context, GeofenceBroadcastReceiver::class.java)
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        PendingIntent.getBroadcast(context, 0, intent, flags)
    }

    /**
     * Register a geofence around the given site.
     * Requires location permission to already be granted.
     */
    fun addGeofenceForSite(site: Site) {
        val geofence = Geofence.Builder()
            .setRequestId(site.id) // used as siteId in the BroadcastReceiver
            .setCircularRegion(
                site.latitude,
                site.longitude,
                geofenceRadiusMeters
            )
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(
                Geofence.GEOFENCE_TRANSITION_ENTER or
                        Geofence.GEOFENCE_TRANSITION_EXIT
            )
            .build()

        val request = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()

        try {
            geofencingClient.addGeofences(request, geofencePendingIntent)
                .addOnSuccessListener {
                    Log.d(TAG, "Geofence added for site=${site.id}")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Failed to add geofence for site=${site.id}", e)
                }
        } catch (se: SecurityException) {
            Log.w(TAG, "Missing location permission when adding geofence", se)
        }
    }

    /**
     * Remove the geofence registered for this site id.
     */
    fun removeGeofenceForSite(siteId: String) {
        try {
            geofencingClient.removeGeofences(listOf(siteId))
                .addOnSuccessListener {
                    Log.d(TAG, "Geofence removed for site=$siteId")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Failed to remove geofence for site=$siteId", e)
                }
        } catch (se: SecurityException) {
            Log.w(TAG, "Missing location permission when removing geofence", se)
        }
    }

    companion object {
        private const val TAG = "GeofenceManager"
    }
}
