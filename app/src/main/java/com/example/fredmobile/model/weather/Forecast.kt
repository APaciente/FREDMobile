package com.example.fredmobile.model.weather

/**
 * Response model for the OpenWeather "forecast" endpoint.
 *
 * Contains a list of forecast entries, each representing a 3-hour
 * time step with temperature and weather condition details.
 *
 * @param list List of forecast items returned by the API.
 */
data class ForecastResponse(
    val list: List<ForecastItem>
)

/**
 * Represents a single 3-hour forecast entry.
 *
 * Example fields returned by the API:
 * - "dt_txt": timestamp as a human-readable string
 * - "main": temperature data
 * - "weather": list of condition descriptions
 *
 * @param dt_txt Date/time of the forecast in string form.
 * @param main Main weather metrics, such as temperature.
 * @param weather List of descriptive weather conditions.
 */
data class ForecastItem(
    val dt_txt: String,
    val main: MainInfo,
    val weather: List<WeatherDescription>
)
