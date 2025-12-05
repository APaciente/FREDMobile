package com.example.fredmobile.ui.weather

/**
 * Simple UI model for weather + forecast.
 */
data class WeatherUiState(
    val isLoading: Boolean = false,
    val temperature: Double? = null,
    val description: String? = null,
    val aqi: Int? = null,
    val nextTemp: Double? = null,
    val nextDescription: String? = null,
    val errorMessage: String? = null,
    val forecastItems: List<ForecastItemUi> = emptyList()
)

/**
 * One “slot” in the short forecast strip on the Check-In screen.
 */
data class ForecastItemUi(
    val timeLabel: String,          // e.g. "3 PM"
    val temp: Double?,
    val description: String?
)
