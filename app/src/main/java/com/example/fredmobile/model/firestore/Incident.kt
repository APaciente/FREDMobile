package com.example.fredmobile.model.firestore

import com.google.firebase.Timestamp

/**
 * Firestore document representing a safety incident.
 *
 * Documents are stored under:
 * `users/{uid}/incidents/{incidentId}`
 *
 * @param id Firestore document ID for this incident.
 * @param siteId Identifier of the site where the incident occurred.
 * @param siteName Human-readable name of the site.
 * @param severity Incident severity level (for example "Low", "Medium", "High").
 * @param description Text description of what happened.
 * @param createdAt Timestamp when the incident was created.
 * @param photoUrl Optional download URL for an incident photo, if one was uploaded.
 */
data class Incident(
    val id: String = "",
    val siteId: String = "",
    val siteName: String = "",
    val severity: String = "Low",
    val description: String = "",
    val createdAt: Timestamp? = null,
    val photoUrl: String? = null
)