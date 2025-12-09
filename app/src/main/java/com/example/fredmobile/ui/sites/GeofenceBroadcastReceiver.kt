package com.example.fredmobile.geofence

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val event = GeofencingEvent.fromIntent(intent)
        if (event == null) {
            Log.w(TAG, "GeofencingEvent was null")
            return
        }

        if (event.hasError()) {
            Log.w(TAG, "Geofencing error code=${event.errorCode}")
            return
        }

        val transitionType = when (event.geofenceTransition) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> "ENTER"
            Geofence.GEOFENCE_TRANSITION_EXIT -> "EXIT"
            Geofence.GEOFENCE_TRANSITION_DWELL -> "DWELL"
            else -> {
                Log.d(TAG, "Ignored transition type: ${event.geofenceTransition}")
                return
            }
        }

        val triggeringGeofences = event.triggeringGeofences ?: emptyList()
        if (triggeringGeofences.isEmpty()) {
            Log.d(TAG, "No triggering geofences found for transition=$transitionType")
            return
        }

        // Current signed-in user (if any)
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            Log.d(TAG, "No signed-in user, skipping Firestore write for transition=$transitionType")
            return
        }

        val db = FirebaseFirestore.getInstance()

        triggeringGeofences.forEach { geofence ->
            val siteId = geofence.requestId // matches Site.id from GeofenceManager

            val payload = hashMapOf(
                "userId" to user.uid,
                "siteId" to siteId,
                "transition" to transitionType,
                "timestamp" to FieldValue.serverTimestamp()
            )

            db.collection(COLLECTION_NAME)
                .add(payload)
                .addOnSuccessListener {
                    Log.d(TAG, "Recorded geofence $transitionType for site=$siteId")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Failed to record geofence event for site=$siteId", e)
                }
        }
    }

    companion object {
        private const val TAG = "GeofenceReceiver"
        private const val COLLECTION_NAME = "geofenceEvents"
    }
}
