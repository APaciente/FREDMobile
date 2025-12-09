package com.example.fredmobile.ui.weather

/**
 * UI state holder for current weather, air quality, and short-term forecast.
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
 * One entry in the short forecast strip shown on the Check-In screen.
 *
 * @param timeLabel label such as "In 3h" or "In 6h".
 * @param temp forecast temperature at that time.
 * @param description short text description of conditions.
 */
data class ForecastItemUi(
    val timeLabel: String,
    val temp: Double?,
    val description: String?
)
