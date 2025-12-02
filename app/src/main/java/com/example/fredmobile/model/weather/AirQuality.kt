package com.example.fredmobile.model.weather

/**
 * Minimal model for OpenWeather "air_pollution" endpoint.
 * We only care about the AQI (1 to 5).
 */
data class AirQualityResponse(
    val list: List<AirQualityEntry>
)

/**
 * Wrapper for the AQI value.
 *
 * Example:
 *   "list": [
 *     { "main": { "aqi": 2 } }
 *   ]
 */
data class AirQualityEntry(
    val main: AirQualityMain
)

data class AirQualityMain(
    val aqi: Int   // 1 = Good, 5 = Very Poor
)
