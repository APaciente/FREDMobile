package com.example.fredmobile.model.firestore

import com.google.firebase.Timestamp

/**
 * Firestore document for a single check-in.
 *
 * Stored under `users/{uid}/checkins/{checkInId}`.
 *
 * @param id Document ID of this check-in.
 * @param userId UID of the user who performed the check-in.
 * @param userName Display name of the user at the time of check-in.
 * @param siteId Identifier of the site.
 * @param siteName Human-readable name of the site.
 * @param inTime Timestamp when the user checked in.
 * @param outTime Timestamp when the user checked out, if applicable.
 * @param status Current status of the check-in (for example, "IN_PROGRESS" or "COMPLETED").
 */
data class CheckIn(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val siteId: String = "",
    val siteName: String = "",
    val inTime: Timestamp? = null,
    val outTime: Timestamp? = null,
    val status: String = "IN_PROGRESS"
)
