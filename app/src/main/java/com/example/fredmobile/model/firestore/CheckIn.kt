package com.example.fredmobile.model.firestore

import com.google.firebase.Timestamp

/**
 * Firestore document for a single check-in record.
 *
 * Stored under: users/{uid}/checkins/{checkInId}
 */
data class CheckIn(
    val id: String = "",          // Firestore doc id (filled after create)
    val siteId: String = "",
    val siteName: String = "",
    val inTime: Timestamp? = null,
    val outTime: Timestamp? = null,
    val status: String = "IN_PROGRESS" // IN_PROGRESS / COMPLETED
)
