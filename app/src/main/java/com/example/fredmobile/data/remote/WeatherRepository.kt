package com.example.fredmobile.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Repository that provides weather data to the rest of the app.
 *
 * It configures Retrofit with the OpenWeather base URL and exposes
 * coroutine-friendly functions for fetching current weather, forecasts,
 * air quality, and alerts.
 */
class WeatherRepository {

    private val api = Retrofit.Builder()
        .baseUrl("https://api.openweathermap.org/data/2.5/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(WeatherApiService::class.java)

    // In a production app, this key should be provided via secure config
    // (for example BuildConfig or remote configuration), not hard-coded.
    private val apiKey = "c4394605ecdb75083cd9926c61dce9d4"

    /**
     * Fetches current weather for the given coordinates.
     *
     * @param lat Latitude of the location.
     * @param lon Longitude of the location.
     * @param units Unit system ("metric" for Celsius, "imperial" for Fahrenheit).
     */
    suspend fun getCurrentWeather(lat: Double, lon: Double, units: String) =
        api.getCurrentWeather(
            lat = lat,
            lon = lon,
            apiKey = apiKey,
            units = units
        )

    /**
     * Fetches the forecast for the given coordinates.
     *
     * @param lat Latitude of the location.
     * @param lon Longitude of the location.
     * @param units Unit system ("metric" for Celsius, "imperial" for Fahrenheit).
     */
    suspend fun getForecast(lat: Double, lon: Double, units: String) =
        api.getForecast(
            lat = lat,
            lon = lon,
            apiKey = apiKey,
            units = units
        )

    /**
     * Fetches air quality information for the given coordinates.
     *
     * @param lat Latitude of the location.
     * @param lon Longitude of the location.
     */
    suspend fun getAirQuality(lat: Double, lon: Double) =
        api.getAirQuality(lat, lon, apiKey)

    /**
     * Fetches weather alerts for the given coordinates.
     *
     * @param lat Latitude of the location.
     * @param lon Longitude of the location.
     */
    suspend fun getAlerts(lat: Double, lon: Double) =
        api.getWeatherAlerts(lat, lon, apiKey)
}
