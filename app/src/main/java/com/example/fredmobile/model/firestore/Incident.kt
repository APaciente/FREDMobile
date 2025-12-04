package com.example.fredmobile.model.firestore

import com.google.firebase.Timestamp

/**
 * Firestore document for a safety incident.
 *
 * Stored under: users/{uid}/incidents/{incidentId}
 */
data class Incident(
    val id: String = "",
    val siteId: String = "",
    val siteName: String = "",
    val severity: String = "Low",
    val description: String = "",
    val createdAt: Timestamp? = null
)
