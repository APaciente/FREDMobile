package com.example.fredmobile.model

/**
 * Simple model for a work site.
 * In later milestones this will be loaded from Firestore / Room.
 */
data class Site(
    val id: String,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double
)
