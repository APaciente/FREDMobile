package com.example.fredmobile.ui.weather

/**
 * UI state for weather section on the Check-In screen.
 */
data class WeatherUiState(
    val isLoading: Boolean = false,
    val temperature: Double? = null,
    val description: String? = null,
    val aqi: Int? = null,
    val nextTemp: Double? = null,
    val nextDescription: String? = null,
    val errorMessage: String? = null
)
