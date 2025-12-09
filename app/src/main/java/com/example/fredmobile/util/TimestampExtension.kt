package com.example.fredmobile.util

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Converts a Firestore [Timestamp] to a simple, human-readable string.
 *
 * Format: `yyyy-MM-dd HH:mm` in the current locale.
 * Returns "-" if the timestamp is null.
 */
fun Timestamp?.toReadableString(): String {
    if (this == null) return "-"
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    return sdf.format(this.toDate())
}