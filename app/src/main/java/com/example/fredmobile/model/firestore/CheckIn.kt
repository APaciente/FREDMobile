package com.example.fredmobile.model.firestore

import com.google.firebase.Timestamp

/**
 * Firestore document representing a single check-in record.
 *
 * Documents are stored under:
 * `users/{uid}/checkins/{checkInId}`
 *
 * @param id Firestore document ID for this check-in.
 * @param siteId Identifier of the site where the user checked in.
 * @param siteName Human-readable name of the site.
 * @param inTime Timestamp when the user checked in.
 * @param outTime Timestamp when the user checked out (if completed).
 * @param status Current status of the check-in, for example "IN_PROGRESS" or "COMPLETED".
 */
data class CheckIn(
    val id: String = "",
    val siteId: String = "",
    val siteName: String = "",
    val inTime: Timestamp? = null,
    val outTime: Timestamp? = null,
    val status: String = "IN_PROGRESS"
)
