package com.example.fredmobile.model

/**
 * Model representing a work site.
 *
 * Contains identifying information, a human-readable address,
 * and geographic coordinates. Site data can later be loaded from
 * remote sources such as Firestore or local storage.
 *
 * @param id Unique identifier for the site.
 * @param name Display name of the site.
 * @param address Physical address or description of the location.
 * @param latitude Latitude coordinate of the site.
 * @param longitude Longitude coordinate of the site.
 */
data class Site(
    val id: String,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double
)
