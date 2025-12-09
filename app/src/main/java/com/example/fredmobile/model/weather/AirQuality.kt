package com.example.fredmobile.model.weather

/**
 * Response model for the OpenWeather "air_pollution" endpoint.
 *
 * Only the fields needed by the app are included. The API returns
 * a list of AQI readings, where the first item represents the
 * current air quality at the requested coordinates.
 *
 * @param list List of air quality readings returned by the API.
 */
data class AirQualityResponse(
    val list: List<AirQualityEntry>
)

/**
 * Entry containing the main AQI value.
 *
 * @param main Container for the AQI integer value.
 */
data class AirQualityEntry(
    val main: AirQualityMain
)

/**
 * Represents the numeric Air Quality Index (AQI) value.
 *
 * AQI scale used by OpenWeather:
 * 1 = Good
 * 2 = Fair
 * 3 = Moderate
 * 4 = Poor
 * 5 = Very Poor
 *
 * @param aqi Numeric AQI value from 1 to 5.
 */
data class AirQualityMain(
    val aqi: Int
)
