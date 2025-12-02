package com.example.fredmobile.model.weather

/**
 * Minimal model for OpenWeather "forecast" endpoint.
 * We keep a list of time steps with temp + description.
 */
data class ForecastResponse(
    val list: List<ForecastItem>
)

/**
 * Single forecast item (3-hour step).
 *
 * Example JSON field names:
 *   "dt_txt": "2025-11-25 12:00:00"
 *   "main": { "temp": 3.5 }
 *   "weather": [ { "description": "light snow" } ]
 */
data class ForecastItem(
    val dt_txt: String,
    val main: MainInfo,
    val weather: List<WeatherDescription>
)
