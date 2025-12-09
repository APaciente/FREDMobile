package com.example.fredmobile.model.weather

/**
 * Response model for the OpenWeather "current weather" endpoint.
 *
 * Only the fields required by the app UI are included. The response
 * provides basic temperature information and a short textual weather
 * description.
 *
 * @param main Temperature-related weather data.
 * @param weather List of textual weather descriptions; typically the first item is used.
 */
data class CurrentWeatherResponse(
    val main: MainInfo,
    val weather: List<WeatherDescription>
)

/**
 * Temperature metrics returned by the API.
 *
 * @param temp Current temperature in the selected units (Celsius or Fahrenheit).
 */
data class MainInfo(
    val temp: Double
)

/**
 * Brief description of the weather condition.
 *
 * Examples include "clear sky", "light rain", or "overcast clouds".
 *
 * @param description Short text describing the current weather.
 */
data class WeatherDescription(
    val description: String
)
