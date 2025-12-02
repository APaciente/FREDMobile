package com.example.fredmobile.model.weather

/**
 * Simple placeholder model for weather alerts.
 * This can be used with a custom or mock endpoint if needed.
 */
data class AlertsResponse(
    val alerts: List<Alert> = emptyList()
)

/**
 * Basic alert information.
 */
data class Alert(
    val title: String,
    val description: String,
    val event: String,
    val start: Long,
    val end: Long
)
