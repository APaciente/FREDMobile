package com.example.fredmobile.util

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Extension to show Firestore Timestamps in a simple readable way.
 */
fun Timestamp?.toReadableString(): String {
    if (this == null) return "-"
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    return sdf.format(this.toDate())
}
