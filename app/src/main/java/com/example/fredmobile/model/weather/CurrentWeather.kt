package com.example.fredmobile.model.weather

/**
 * Minimal model for OpenWeather "current weather" endpoint.
 * Only keeps the fields we actually use in the UI.
 */
data class CurrentWeatherResponse(
    val main: MainInfo,
    val weather: List<WeatherDescription>
)

/** Main weather metrics (we only need temperature for now). */
data class MainInfo(
    val temp: Double
)

/** Short description like "clear sky", "light rain", etc. */
data class WeatherDescription(
    val description: String
)
