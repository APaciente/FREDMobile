package com.example.fredmobile.model.weather

/**
 * Container model for weather alerts.
 *
 * This is a simplified structure used for mock data or custom alert
 * integrations. The OpenWeather "alerts" field appears in certain API
 * tiers (e.g., One Call API), but is not universally available.
 *
 * @param alerts List of alert items, empty if no alerts are present.
 */
data class AlertsResponse(
    val alerts: List<Alert> = emptyList()
)

/**
 * Represents a single weather alert.
 *
 * @param title Title of the alert (for example, "Storm Warning").
 * @param description Text description providing details about the alert.
 * @param event Name or classification of the event.
 * @param start Start time of the alert in Unix epoch seconds.
 * @param end End time of the alert in Unix epoch seconds.
 */
data class Alert(
    val title: String,
    val description: String,
    val event: String,
    val start: Long,
    val end: Long
)
