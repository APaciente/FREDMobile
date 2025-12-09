package com.example.fredmobile.data.remote

import com.example.fredmobile.model.weather.CurrentWeatherResponse
import com.example.fredmobile.model.weather.ForecastResponse
import com.example.fredmobile.model.weather.AirQualityResponse
import com.example.fredmobile.model.weather.AlertsResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit service for accessing weather-related data from the
 * OpenWeather API.
 *
 * Exposes endpoints for:
 * - Current weather
 * - Short-term forecast
 * - Air quality
 * - Weather alerts (simulated or backed by a compatible endpoint)
 */
interface WeatherApiService {

    /**
     * Returns current weather information for the given coordinates.
     *
     * @param lat Latitude of the location.
     * @param lon Longitude of the location.
     * @param apiKey OpenWeather API key.
     * @param units Units for temperature values (default is metric).
     */
    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): CurrentWeatherResponse

    /**
     * Returns a multi-hour forecast for the given coordinates.
     *
     * @param lat Latitude of the location.
     * @param lon Longitude of the location.
     * @param apiKey OpenWeather API key.
     * @param units Units for temperature values (default is metric).
     */
    @GET("forecast")
    suspend fun getForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): ForecastResponse

    /**
     * Returns air quality data for the given coordinates.
     *
     * @param lat Latitude of the location.
     * @param lon Longitude of the location.
     * @param apiKey OpenWeather API key.
     */
    @GET("air_pollution")
    suspend fun getAirQuality(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String
    ): AirQualityResponse

    /**
     * Returns weather alerts for the given coordinates.
     *
     * Depending on the backend configuration, this may be backed
     * by a real alerts endpoint or simulated alert data.
     *
     * @param lat Latitude of the location.
     * @param lon Longitude of the location.
     * @param apiKey OpenWeather API key.
     */
    @GET("alerts")
    suspend fun getWeatherAlerts(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String
    ): AlertsResponse
}
